package com.github.ryoii.view.window

import com.github.ryoii.controller.ImageController
import com.github.ryoii.model.ImageModel
import com.github.ryoii.view.DirectoryView
import javafx.scene.control.Alert
import javafx.scene.control.ButtonType
import tornadofx.*

class EditFragment : Fragment() {

    private val imageController by inject<ImageController>(DefaultScope)
    private val imageModel by inject<ImageModel>()

    override val root = borderpane {

        left = find<DirectoryView>(scope).root

        center = vbox {

            val toggle = togglebutton("编辑", selectFirst = false)

            form {
                enableWhen(toggle.selectedProperty())

                fieldset("项目信息") {
                    field("项目名") { textfield(imageModel.name) }
                    field("镜像名") { textfield(imageModel.imageName) }
                }
                fieldset("描述") {
                    textarea(imageModel.description) { isWrapText = true }
                }
                fieldset("启动信息") {
                    field("启动命令") { textfield(imageModel.command) }
                }

                buttonbar {
                    button("删除") {
                        setOnAction {
                            alert(Alert.AlertType.CONFIRMATION,"确认删除","正在删除：${imageModel.name.value}") {
                                if (result == ButtonType.OK) {
                                    imageController.deleteImage(imageModel.item)
                                    imageController.flush()
                                }
                            }
                        }
                    }
                    button("保存") {
                        setOnAction {
                            imageModel.commit()
                            imageController.run {
                                updateImage(imageModel.item)
                                flush()
                            }
                        }
                    }
                }
            }
        }
    }
}