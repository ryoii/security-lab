package com.github.ryoii.rest.requester


import javafx.application.Platform
import org.bouncycastle.asn1.ASN1InputStream
import org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers
import org.bouncycastle.asn1.pkcs.PrivateKeyInfo
import org.bouncycastle.asn1.x509.AlgorithmIdentifier
import org.bouncycastle.openssl.PEMReader
import tornadofx.DigestAuthContext
import tornadofx.JsonModel
import tornadofx.Rest
import java.io.ByteArrayInputStream
import java.io.FileReader
import java.io.InputStream
import java.net.URI
import java.nio.charset.StandardCharsets
import java.security.KeyFactory
import java.security.KeyStore
import java.security.cert.CertificateFactory
import java.security.cert.X509Certificate
import java.security.spec.PKCS8EncodedKeySpec
import java.util.zip.DeflaterInputStream
import java.util.zip.GZIPInputStream
import javax.json.JsonValue
import javax.net.ssl.*
import kotlin.collections.*

class HttpsURLEngine(val rest: Rest) : Rest.Engine() {

    var sslSocketFactory: SSLSocketFactory? = null

    override fun reset() {
        requestInterceptor = null
        sslSocketFactory = null
    }

    override fun request(seq: Long, method: Rest.Request.Method, uri: URI, entity: Any?) =
        HttpsURLRequest(this, seq, method, uri, entity)

    override fun setBasicAuth(username: String, password: String) {
        sslSocketFactory = createSSLContext(username, password).socketFactory
    }

    override fun setDigestAuth(username: String, password: String) {
        rest.authContext = DigestAuthContext(username, password)
    }
}

class HttpsURLRequest(
    val engine: HttpsURLEngine,
    override val seq: Long,
    override val method: Rest.Request.Method,
    override val uri: URI,
    override val entity: Any?
) : Rest.Request {
    lateinit var connection: HttpsURLConnection
    val headers = mutableMapOf<String, String>()

    init {
        reset()
    }

    override fun reset() {
        val url = uri.toURL()
        connection =
            (if (engine.rest.proxy != null) url.openConnection(engine.rest.proxy) else url.openConnection()) as HttpsURLConnection
        connection.sslSocketFactory = engine.sslSocketFactory
        headers += "Accept-Encoding" to "gzip, deflate"
        headers += "Content-Type" to "application/json"
        headers += "Accept" to "application/json"
        headers += "User-Agent" to "TornadoFX/Java ${System.getProperty("java.version")}"
        headers += "Connection" to "Keep-Alive"
    }

    override fun execute(): Rest.Response {
        engine.rest.authContext?.interceptRequest(this)
        engine.requestInterceptor?.invoke(this)

        for ((key, value) in headers)
            connection.addRequestProperty(key, value)

        connection.requestMethod = method.toString()

        if (entity != null) {
            if (headers["Content-Type"] == null)
                connection.addRequestProperty("Content-Type", "application/json")

            connection.doOutput = true

            val data = when (entity) {
                is JsonModel -> entity.toJSON().toString().toByteArray(StandardCharsets.UTF_8)
                is JsonValue -> entity.toString().toByteArray(StandardCharsets.UTF_8)
                is InputStream -> entity.readBytes()
                else -> throw IllegalArgumentException("Don't know how to handle entity of type ${entity.javaClass}")
            }
            connection.addRequestProperty("Content-Length", data.size.toString())
            connection.connect()
            connection.outputStream.write(data)
            connection.outputStream.flush()
            connection.outputStream.close()
        } else {
            connection.connect()
        }

        val response = HttpsURLResponse(this)
        if (connection.doOutput) response.bytes()

        val modifiedResponse = engine.rest.authContext?.interceptResponse(response) ?: response

        engine.responseInterceptor?.invoke(modifiedResponse)

        return modifiedResponse
    }

    override fun addHeader(name: String, value: String) {
        headers[name] = value
    }

    override fun getHeader(name: String) = headers[name]

    override var properties: MutableMap<Any, Any> = HashMap()
}

class HttpsURLResponse(override val request: HttpsURLRequest) : Rest.Response {
    override val statusCode: Int get() = request.connection.responseCode
    private var bytesRead: ByteArray? = null

    override fun close() {
        consume()
    }

    override fun consume(): Rest.Response = apply {
        try {
            if (bytesRead == null) {
                bytes()
                return this
            }

            with(request.connection) {
                if (doInput) content().close()
            }
        } catch (ignored: Throwable) {
            ignored.printStackTrace()
        }
        Platform.runLater { Rest.ongoingRequests.remove(request) }
    }

    override val reason: String get() = request.connection.responseMessage

    override fun text() = bytes().toString(StandardCharsets.UTF_8)

    override fun content(): InputStream = request.connection.errorStream ?: request.connection.inputStream

    override fun bytes(): ByteArray {
        bytesRead?.let { return it }

        try {
            val unwrapped = when (request.connection.contentEncoding) {
                "gzip" -> GZIPInputStream(content())
                "deflate" -> DeflaterInputStream(content())
                else -> content()
            }
            bytesRead = unwrapped.readBytes()
        } catch (error: Exception) {
            bytesRead = ByteArray(0)
            throw error
        } finally {
            consume()
        }
        return bytesRead!!
    }

    override val headers: MutableMap<String, MutableList<String>> get() = request.connection.headerFields
}

private fun createSSLContext(certPath: String, keyPath: String): SSLContext {
    val certByte = PEMReader(FileReader(certPath)).readPemObject().content
    val key1Byte = PEMReader(FileReader(keyPath)).readPemObject().content

    /* 将PKCS#1格式是要转换成Java最低支持的PKCS#8 */
    /* TODO 去除bouncy castle的依赖 */
    val key8Byte = ASN1InputStream(key1Byte).use {
        PrivateKeyInfo(AlgorithmIdentifier(PKCSObjectIdentifiers.rsaEncryption), it.readObject()).derEncoded
    }

    val cert = CertificateFactory.getInstance("x.509").generateCertificate(ByteArrayInputStream(certByte))
    val key = KeyFactory.getInstance("RSA").generatePrivate(PKCS8EncodedKeySpec(key8Byte))

    val keyStore = KeyStore.getInstance("JKS").apply {
        load(null)
        setCertificateEntry("cert-alias", cert)
        setKeyEntry("key-alias", key, "changeIt".toCharArray(), arrayOf(cert))
    }

    /* keyMangers和trustManagers由对应的Factory的实现类通过getXXXManagers()方法返回
    *  由源码发现返回逻辑为创建新建数组，即每次调用xxxManagers返回并非同一对象，
    *  kotlin的代码风格造成属性调用的错觉，本质上是方法调用。
    *  请保持对xxxManagers数组对象的引用或使用高阶函数确保操作同一对象
    *  Debug time: 8h */
    val kmf = KeyManagerFactory.getInstance("SunX509").run {
        init(keyStore, "changeIt".toCharArray())
        keyManagers
    }

    val tmf = TrustManagerFactory.getInstance("PkIx").run {
        val nullStore: KeyStore? = null
        init(nullStore)
        trustManagers.also {
            for ((i, tm) in it.withIndex()) {
                if (tm is X509TrustManager) {
                    it[i] = TrustSelfSignedManager(tm)
                }
            }
        }
    }

    return SSLContext.getInstance("TLS").apply {
        init(kmf, tmf, null)
    }
}

private class TrustSelfSignedManager(private val tm: X509TrustManager) : X509TrustManager by tm {
    override fun checkServerTrusted(chain: Array<out X509Certificate>?, authType: String?) {
        if (chain?.size != 1) {
            tm.checkServerTrusted(chain, authType)
        }
    }
}