package com.github.ryoii.model

import tornadofx.JsonBuilder
import tornadofx.JsonModel
import javax.json.JsonObject
import javax.json.JsonValue
import javax.json.spi.JsonProvider

class ContainerModel(private val experiment: Experiment) : JsonModel {

    private val imageName = experiment.imageName
    private val ports = HashMap<String, String>()
    private val mounts = HashMap<String, String>()

    val name = experiment.containerName
    var id
        get() = experiment.containerID
        set(value) {
            experiment.containerID = value
        }

    init {
        //TODO parse portMap and mount
    }

    override fun toJSON(json: JsonBuilder) {
        with(json) {
            add("Image", imageName)
            add("ExposedPorts", JsonBuilder().apply {
                ports.values.forEach { add("$it/tcp", JsonValue.EMPTY_JSON_OBJECT) }
            }.build())
            add("HostConfig", JsonBuilder().apply {
                add("PortBindings", getPortBindings())
                add("AutoRemove", true)
            }.build())
        }
    }

    private fun getPortBindings(): JsonObject {
        val ret = HashMap<String, Any>()
        ports.forEach { (k, v) ->
            ret["$v/tcp"] = listOf(mapOf("HostPort" to k))
        }
        return JsonProvider.provider().createObjectBuilder(ret).build()
    }
}