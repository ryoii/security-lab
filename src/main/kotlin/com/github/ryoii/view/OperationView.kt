package com.github.ryoii.view

import com.github.ryoii.controller.ExperimentController
import com.github.ryoii.converter.RunButtonStateConverter
import com.github.ryoii.converter.StateConverter
import com.github.ryoii.model.ExperimentModel
import tornadofx.*

class OperationView : View() {

    private val experimentController by inject<ExperimentController>()
    private val experimentModel by inject<ExperimentModel>()

    override val root = vbox {

        minWidth = 600.0

        form {
            visibleProperty().bind(experimentModel.empty.not())
            spacing = 10.0

            fieldset("实验信息") {
                text(experimentModel.name)
            }

            fieldset("描述") {
                label(experimentModel.description) {
                    isWrapText = true
                }
            }
            separator()
            fieldset("操作") {
                button {
                    textProperty().bindBidirectional(experimentModel.state, RunButtonStateConverter())
                }.action {
                    //experimentController.runExperiment(experimentModel.item)
                }
                field("状态") {
                    label().textProperty().bindBidirectional(experimentModel.state, StateConverter())
                }
                field("IP地址") {
                    //text(experimentModel.host)
                }
            }
        }
    }
}