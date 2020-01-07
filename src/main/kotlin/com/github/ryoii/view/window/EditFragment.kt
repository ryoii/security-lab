package com.github.ryoii.view.window

import com.github.ryoii.controller.ExperimentController
import com.github.ryoii.model.ExperimentModel
import com.github.ryoii.view.DirectoryView
import javafx.scene.control.Alert
import javafx.scene.control.ButtonType
import tornadofx.*

class EditFragment : Fragment() {

    private val experimentController by inject<ExperimentController>(DefaultScope)
    private val experimentModel by inject<ExperimentModel>()

    override val root = borderpane {

        left = find<DirectoryView>(scope).root

        center = vbox {

            val toggle = togglebutton("编辑", selectFirst = false)

            form {
                enableWhen(toggle.selectedProperty())

                fieldset("实验信息") {
                    field("实验名") { textfield(experimentModel.name) }
                    field("镜像名") { textfield(experimentModel.imageName) }
                }
                fieldset("描述") {
                    textarea(experimentModel.description) { isWrapText = true }
                }
                fieldset("启动信息") {
                    field("启动命令") { textfield(experimentModel.command) }
                }

                buttonbar {
                    button("删除").action {
                        alert(Alert.AlertType.CONFIRMATION, "确认删除", "正在删除：${experimentModel.name.value}") {
                            if (result == ButtonType.OK) {
                                experimentController.deleteExperiment(experimentModel.item)
                            }
                        }
                    }
                    button("保存").action {
                        experimentModel.commit()
                        experimentController.updateExperiment(experimentModel.item)
                    }
                }
            }
        }
    }
}