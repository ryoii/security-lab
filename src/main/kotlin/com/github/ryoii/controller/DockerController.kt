package com.github.ryoii.controller

import com.github.ryoii.event.*
import com.github.ryoii.model.ContainerModel
import com.github.ryoii.model.Experiment
import com.github.ryoii.model.GlobalInfoModel
import com.github.ryoii.rest.requester.HttpsURLEngine
import tornadofx.*
import java.io.BufferedReader
import java.io.InputStreamReader
import java.util.*

class DockerController : Controller() {

    private val globalInfo = find<GlobalInfoModel>().item
    private val api by inject<Rest>()

    init {
        Rest.engineProvider = ::HttpsURLEngine
        api.baseURI = "https://${globalInfo.dockerMachineIp}:${globalInfo.dockerMachineAPIPort}"
        val key = Properties().apply { load(ClassLoader.getSystemResourceAsStream("key.properties")) }
        api.setBasicAuth(key.getProperty("cert"), key.getProperty("key"))

        /* 实验事件注册 */
        subscribe<RunExperimentEvent> {
            runContainer(it.experiment)
        }

        subscribe<StopExperimentEvent> {
            stopContainer(it.experiment)
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
                404 -> pullImage(experiment) // 无法创建容器，镜像不存在
                409 -> fire(ContainerNameConflictEvent(experiment)) // 容器名冲突，实验已运行，或实验设置的容器名冲突
            }
        }
    }

    private fun startContainer(experiment: Experiment) {
        api.post("/containers/${experiment.containerID}/start").let {
            when (it.statusCode) {
                204 -> fire(ExperimentStartedEvent(experiment)) // 实验启动完成
                304 -> fire(ContainerNameConflictEvent(experiment)) // 实验已启动，重复启动错误
                404 -> fire(ContainerNotFoundEvent(experiment)) // 找不到容器
            }
        }
    }

    private fun stopContainer(experiment: Experiment) {
        api.post("/containers/${experiment.containerID}/stop").let {
            when (it.statusCode) {
                204 -> fire(ExperimentStoppedEvent(experiment)) // 实验停止完成
                304 -> fire(ContainerNameConflictEvent(experiment)) // 重复停止
                404 -> fire(ContainerNotFoundEvent(experiment)) // 找不到容器
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
//                  image pull is a keep alive request
                    BufferedReader(InputStreamReader(it.content())).use { buffer ->
                        while (true) {
                            val json = loadJsonObject(buffer.readLine()) ?: break
                            if ("Downloading" == json.getString("status")) {
                                // 解析进度
                                with(json.getJsonObject("progressDetail")) {
                                    val progress = getDouble("current") / getDouble("total")
                                    fire(PullImageEvent(experiment, progress))
                                }
                            }
                        }
                    }
                    runContainer(experiment)
                }
                404 -> fire(ImageNotFoundEvent(experiment))
            }
        }
    }
}