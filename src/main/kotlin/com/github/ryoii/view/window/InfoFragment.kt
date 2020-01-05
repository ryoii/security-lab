package com.github.ryoii.view.window

import com.github.ryoii.controller.ImageController
import com.github.ryoii.model.ImageModel
import tornadofx.*

class InfoFragment : Fragment() {

    private val imageControl by inject<ImageController>(DefaultScope)
    private val image by inject<ImageModel>()

    override val root = flowpane {

        form {
            fieldset("镜像信息") {
                field("项目名") { textfield(image.name) }
                field("镜像名") { textfield(image.imageName) }
                field("描述") { textarea(image.description) }
            }
            fieldset("启动信息") {
                field("启动命令") { textfield(image.command) }
            }

            button("保存").action {
                image.commit()
                imageControl.saveImages(image.item)
                this@InfoFragment.close()
            }
        }
    }
}