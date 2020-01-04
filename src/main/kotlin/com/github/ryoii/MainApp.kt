package com.github.ryoii

import com.github.ryoii.view.MainView
import tornadofx.App
import tornadofx.launch

fun main(args: Array<String>) {

    launch<MainApp>(args)
}

class MainApp: App(MainView::class) {

}