package com.github.ryoii

import com.github.ryoii.controller.DockerController
import com.github.ryoii.model.GlobalInfoModel
import com.github.ryoii.view.MainView
import tornadofx.App
import tornadofx.find
import tornadofx.launch

fun main(args: Array<String>) {

    launch<MainApp>(args)
}

class MainApp: App(MainView::class) {

    private val global = find<GlobalInfoModel>()

    override fun init() {
        super.init()
        find<DockerController>()

        //TODO fetch global info
        println(global.item)
    }

}