package com.github.ryoii.controller

import com.github.ryoii.model.ContainerModel
import com.github.ryoii.model.Experiment
import com.github.ryoii.model.GlobalInfoModel
import com.github.ryoii.rest.requester.HttpsURLEngine
import tornadofx.Controller
import tornadofx.Rest
import java.util.*

class DockerController : Controller() {

    private val globalInfo = find<GlobalInfoModel>().item
    val api by inject<Rest>()

    init {
        Rest.engineProvider = ::HttpsURLEngine
        api.baseURI = "https://${globalInfo.dockerMachineIp}:${globalInfo.dockerMachineAPIPort}"
        val key = Properties().apply { load(ClassLoader.getSystemResourceAsStream("key.properties")) }
        api.setBasicAuth(key.getProperty("cert"), key.getProperty("key"))
    }

    /*******************************************
     * Container API
     *******************************************/
    fun runContainer(experiment: Experiment) {
        val container = ContainerModel(experiment)
        val query = if (container.name.isBlank()) "" else "?name=${container.name}"
        api.post("/containers/create${query}", container).let {
            when (it.statusCode) {
                201 -> {
                    container.id = it.one().getString("Id")
                    startContainer(container.id)
                }
                // TODO 404, no such image
                // TODO 409, conflict, maybe the container name has been used
            }
        }
    }

    private fun startContainer(containerId: String) {
        api.post("/containers/${containerId}/start").statusCode
    }

    fun stopContainer(experiment: Experiment) {
        api.post("/containers/${experiment.containerID}/stop").let {
            when(it.statusCode) {
                204 -> {
                    // No problem
                }
                304 -> {
                    // already stop
                }
                404 -> {
                    // no such container
                }
            }
        }
    }

    /*******************************************
     * Image API
     *******************************************/
    private fun pullImage(experiment: Experiment) {
        // TODO remove tag arg
        api.post("/images/create?fromImage=${experiment.imageName}&tag=latest")
    }
}