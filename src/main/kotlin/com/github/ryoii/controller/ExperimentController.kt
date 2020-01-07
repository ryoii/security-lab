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
}