package com.github.ryoii.controller

import com.github.ryoii.database.SQLiteConnector
import com.github.ryoii.model.Experiment
import javafx.collections.ObservableList
import tornadofx.Controller
import tornadofx.observable

class ExperimentController : Controller() {

    val cache: ObservableList<Experiment> = getExperiments().observable()

    private fun getExperiments() = SQLiteConnector.queryExperiments()

    fun updateExperiment(experiment: Experiment) {
        SQLiteConnector.update(experiment)
        cache.setAll(getExperiments())
    }

    fun saveExperiment(experiment: Experiment) {
        SQLiteConnector.save(experiment)
        cache.setAll(getExperiments())
    }

    fun deleteExperiment(experiment: Experiment) {
        SQLiteConnector.delete(experiment)
        cache.setAll(getExperiments())
    }

    fun runExperiment(experiment: Experiment): String =
        if (experiment.state) {
            // 停止镜像
            Runtime.getRuntime().exec("docker stop ${experiment.containerID}").inputStream.use {
                experiment.state = false
                experiment.host = ""
                experiment.containerID = ""
                return@use String(it.readAllBytes())
            }
        } else {
            Runtime.getRuntime().exec(experiment.command).inputStream.use {
                val res = String(it.readAllBytes())
                experiment.state = true
                experiment.host = "192.168.99.100"
                experiment.containerID = res
                return@use res
            }
        }
}