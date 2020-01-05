package com.github.ryoii.controller

import com.github.ryoii.database.SQLiteConnector
import com.github.ryoii.model.Image
import javafx.collections.ObservableList
import tornadofx.Controller
import tornadofx.observable

class ImageController : Controller() {

    val cache: ObservableList<Image> = getImages().observable()

    private fun getImages() = SQLiteConnector.queryImages()

    fun updateImage(image: Image) {
        SQLiteConnector.update(image)
        cache.setAll(getImages())
    }

    fun saveImages(image: Image) {
        SQLiteConnector.save(image)
        cache.setAll(getImages())
    }

    fun deleteImage(image: Image) {
        SQLiteConnector.delete(image)
        cache.setAll(getImages())
    }

    fun runImage(image: Image) {
        if (image.state) {
            // 停止镜像
            Runtime.getRuntime().exec("docker stop ${image.containerID}")
            image.state = false
            image.host = ""
        } else {
            // 启动镜像
            println(image.command)
            Runtime.getRuntime().exec(image.command).inputStream.use {
                val res = String(it.readAllBytes())
                image.host = "192.168.99.100"
                image.state = true
                image.containerID = res
                println(image.containerID)
            }
        }
    }
}