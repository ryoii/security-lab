package com.github.ryoii.controller

import com.github.ryoii.event.*
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

        /* 实验事件注册 */
        subscribe<RunExperimentEvent> {

        }

        subscribe<StopExperimentEvent> {

        }
    }

    /*******************************************
     * Container API
     *******************************************/
    private fun runContainer(experiment: Experiment) {
        val container = ContainerModel(experiment)
        val query = if (container.name.isBlank()) "" else "?name=${container.name}"
        api.post("/containers/create${query}", container).let {
            when (it.statusCode) {
                201 -> {
                    container.id = it.one().getString("Id")
                    startContainer(experiment)
                }
                404 -> {
                    // no such image
                }
                409 -> {
                    // conflict, maybe the container name has been used
                }
            }
        }
    }

    private fun startContainer(experiment: Experiment) {
        api.post("/containers/${experiment.containerID}/start").let {
            when(it.statusCode) {
                204 -> {
                    // No problem
                }
                304 -> {
                    // already start
                }
                404 -> {
                    // no such container
                }
            }
        }
    }

    private fun stopContainer(experiment: Experiment) {
        api.post("/containers/${experiment.containerID}/stop").let {
            when (it.statusCode) {
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
        api.post("/images/create?fromImage=${experiment.imageName}&tag=latest").let {
            when(it.statusCode) {
                200 -> {
                    // no problem
                }
                404 -> {
                    // image does not exist or can't read image
                }
            }
        }
    }
}