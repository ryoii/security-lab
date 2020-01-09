package com.github.ryoii.view

import com.github.ryoii.controller.ExperimentController
import com.github.ryoii.model.ExperimentModel
import javafx.geometry.HPos
import javafx.geometry.Pos
import javafx.scene.layout.Background
import javafx.scene.layout.BackgroundFill
import javafx.scene.paint.Paint
import javafx.scene.text.TextAlignment
import tornadofx.*

class DirectoryView : View() {

    private val experimentController by inject<ExperimentController>()
    private val experimentModel by inject<ExperimentModel>()

    override val root = vbox {

        listview(experimentController.cache) {

            fitToParentHeight()

            cellFormat {

                graphic = vbox {
                    label(it.nameProperty)

                    hbox {
                        alignment = Pos.CENTER_RIGHT
                        label(stringBinding(it.stateProperty) { if (value) "● 运行中" else "● 未运行" })
                    }
                }
            }

            experimentModel.rebindOnChange(this) { image ->
                item = image
            }
        }
    }
}
