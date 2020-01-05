package com.github.ryoii.view

import com.github.ryoii.controller.ImageController
import com.github.ryoii.converter.RunButtonStateConverter
import com.github.ryoii.converter.StateConverter
import com.github.ryoii.model.ImageModel
import tornadofx.*

class OperationView : View() {

    private val imageController: ImageController by inject()
    private val imageModel: ImageModel by inject()

    override val root = vbox {

        minWidth = 600.0

        form {
            visibleProperty().bind(imageModel.empty.not())
            spacing = 10.0

            fieldset("项目信息") {
                text(imageModel.name)
            }

            fieldset("描述") {
                label(imageModel.description) {
                    isWrapText = true
                }
            }
            separator()
            fieldset("操作") {
                button {
                    textProperty().bindBidirectional(imageModel.state, RunButtonStateConverter())
                    setOnAction {
                        imageController.runImage(imageModel.item)
                    }
                }
                field("状态") {
                    label().textProperty().bindBidirectional(imageModel.state, StateConverter())
                }
                field("IP地址") {
                    text(imageModel.host)
                }
            }
        }
    }
}