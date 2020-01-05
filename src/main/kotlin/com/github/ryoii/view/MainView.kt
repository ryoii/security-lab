package com.github.ryoii.view

import com.github.ryoii.model.ImageModel
import com.github.ryoii.view.window.EditFragment
import com.github.ryoii.view.window.InfoFragment
import javafx.stage.Modality
import tornadofx.*

class MainView : View() {

    override val root = borderpane {

        top = menubar {
            menu("镜像") {
                item("添加镜像").action {
                    find<InfoFragment>(Scope(ImageModel())).openModal(
                        modality = Modality.APPLICATION_MODAL,
                        resizable = false
                    )
                }
                item("编辑镜像").action {
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