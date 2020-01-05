package com.github.ryoii.view

import com.github.ryoii.controller.ImageController
import com.github.ryoii.model.ImageModel
import tornadofx.*

class DirectoryView : View() {

    private val imageController: ImageController by inject()
    private val imageModel: ImageModel by inject()

    override val root = vbox {

        listview(imageController.cache) {

            fitToParentHeight()

            cellFormat {
                text = it.name
            }

            imageModel.rebindOnChange(this) { image ->
                item = image
            }
        }
    }
}
