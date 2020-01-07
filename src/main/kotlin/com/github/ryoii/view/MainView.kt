package com.github.ryoii.view

import com.github.ryoii.model.ExperimentModel
import com.github.ryoii.view.window.EditFragment
import com.github.ryoii.view.window.InfoFragment
import javafx.stage.Modality
import tornadofx.*

class MainView : View() {

    override val root = borderpane {

        top = menubar {
            menu("实验") {
                item("添加实验").action {
                    find<InfoFragment>(Scope(ExperimentModel())).openModal(
                        modality = Modality.APPLICATION_MODAL,
                        resizable = false
                    )
                }
                item("编辑实验").action {
                    find<EditFragment>(Scope()).openModal(
                        modality = Modality.APPLICATION_MODAL,
                        resizable = false
                    )
                }
            }
        }

        left = find<DirectoryView>().root
        center = find<OperationView>().root
    }

}