package com.github.ryoii.view

import com.github.ryoii.controller.ExperimentController
import com.github.ryoii.model.ExperimentModel
import tornadofx.*

class DirectoryView : View() {

    private val experimentController by inject<ExperimentController>()
    private val experimentModel by inject<ExperimentModel>()

    override val root = vbox {

        listview(experimentController.cache) {

            fitToParentHeight()

            cellFormat {
                text = it.name
            }

            experimentModel.rebindOnChange(this) { image ->
                item = image
            }
        }
    }
}
