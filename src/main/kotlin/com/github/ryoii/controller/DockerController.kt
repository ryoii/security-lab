package com.github.ryoii.controller

import com.github.ryoii.model.GlobalInfoModel
import com.github.ryoii.model.ContainerModel
import com.github.ryoii.model.Experiment
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
    fun runContainer(experiment: Experiment): String? {
        val container = ContainerModel(experiment)
        val query = if (container.name.isBlank()) "" else "?name=${container.name}"
        container.id = api.post("/containers/create${query}", container).one().getString("Id")
        val statusCode = api.post("/containers/${container.id}/start").statusCode
        return "[$statusCode]${container.id}"
    }

    fun stopContainer(experiment: Experiment) {

    }
}