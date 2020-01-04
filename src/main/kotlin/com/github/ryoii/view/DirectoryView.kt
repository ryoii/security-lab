package com.github.ryoii.view

import com.github.ryoii.controller.ImageController
import tornadofx.*

class DirectoryView : View() {

    private val imageController: ImageController by inject()

    override val root = vbox {

        listview(imageController.cache) {

            fitToParentHeight()

            cellFormat {
                text = it.name
            }

            imageController.imageModel.rebindOnChange(this) { image ->
                item = image
            }
        }
    }
}
