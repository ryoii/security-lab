package com.github.ryoii.view

import com.github.ryoii.model.ExperimentModel
import com.github.ryoii.model.GlobalInfoModel
import javafx.scene.layout.Background
import tornadofx.*
import java.awt.Desktop
import java.net.URI

class OperationView : View() {

    private val globalInfo = find<GlobalInfoModel>().item
    private val experimentModel by inject<ExperimentModel>()

    override val root = vbox {

        minWidth = 600.0

        form {
            visibleProperty().bind(experimentModel.empty.not())

            fieldset {
                textProperty.bindBidirectional(experimentModel.name)
            }

            separator()

            fieldset("状态") {
                field("运行状态") {
                    label(stringBinding(experimentModel.state) { if (value) "运行中" else "未运行" })
                }
                field("主页") {
                    hyperlink(experimentModel.homepage).action {
                        if (experimentModel.item.state) {
                            Desktop.getDesktop().browse(URI(
                                "http://${globalInfo.dockerMachineIp}${experimentModel.homepage.value}"
                            ))
                        }
                    }
                }
            }

            fieldset("描述") {
                textarea(experimentModel.description) {
                    background = Background.EMPTY
                    isEditable = false
                    isWrapText = true
                }
            }

            buttonbar {
                button(stringBinding(experimentModel.state) { if (value) "停止" else "运行" }).action {
                    with(experimentModel.item) {
                        state = !state
                    }
                }
            }
        }
    }
}