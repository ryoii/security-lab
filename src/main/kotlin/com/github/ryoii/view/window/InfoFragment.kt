package com.github.ryoii.view.window

import com.github.ryoii.controller.ExperimentController
import com.github.ryoii.model.ExperimentModel
import tornadofx.*

class InfoFragment : Fragment() {

    private val experimentController by inject<ExperimentController>(DefaultScope)
    private val experimentModel by inject<ExperimentModel>()

    override val root = flowpane {

        form {
            fieldset("实验信息") {
                field("实验名") { textfield(experimentModel.name) }
                field("镜像名") { textfield(experimentModel.imageName) }
                field("描述") { textarea(experimentModel.description) }
            }
            fieldset("启动信息") {
                field("启动命令") { textfield(experimentModel.command) }
            }

            button("保存").action {
                experimentModel.commit()
                experimentController.saveExperiment(experimentModel.item)
                this@InfoFragment.close()
            }
        }
    }
}