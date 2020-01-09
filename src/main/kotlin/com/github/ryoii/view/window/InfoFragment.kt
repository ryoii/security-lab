package com.github.ryoii.view.window

import com.github.ryoii.controller.ExperimentController
import com.github.ryoii.model.ExperimentModel
import tornadofx.*

class InfoFragment : Fragment() {

    private val experimentController by inject<ExperimentController>(FX.defaultScope)
    private val experimentModel by inject<ExperimentModel>()

    override val root = flowpane {

        form {
            fieldset("实验信息") {
                field("实验名") { textfield(experimentModel.name) }
                field("主页") { textfield(experimentModel.homepage) }
            }

            fieldset("docker信息") {
                field("镜像名") { textfield(experimentModel.imageName) }
                field("容器名") { textfield(experimentModel.containerName) }
            }

            fieldset("映射信息") {
                field("端口映射") { textfield(experimentModel.portMap) }
                field("文件挂载") { textfield(experimentModel.mount) }
            }

            fieldset("描述") {
                textarea(experimentModel.description) { isWrapText = true }
            }

            buttonbar {
                button("保存").action {
                    experimentModel.commit()
                    experimentController.saveExperiment(experimentModel.item)
                    this@InfoFragment.close()
                }
            }
        }
    }
}