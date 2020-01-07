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

            button("删除").action {
                alert(Alert.AlertType.CONFIRMATION, "确认删除", "正在删除：${experimentModel.name.value}") {
                    if (result == ButtonType.OK) {
                        experimentController.deleteExperiment(experimentModel.item)
                    }
                }
            }

            this += find<InfoFragment>(scope).root.apply {
                enableWhen(toggle.selectedProperty())
            }
        }
    }
}