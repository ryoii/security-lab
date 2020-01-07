package com.github.ryoii.model

import javafx.beans.property.SimpleBooleanProperty
import javafx.beans.property.SimpleStringProperty
import tornadofx.ItemViewModel
import tornadofx.getValue
import tornadofx.setValue

/**
 * docker镜像数据对象
 */
class Experiment() {

    constructor(id: Int, name: String, imageName: String, command: String, description: String) : this() {
        this.id = id
        this.name = name
        this.imageName = imageName
        this.command = command
        this.description = description
    }

    var id: Int? = null

    /**
     *  项目名
     */
    val nameProperty = SimpleStringProperty(this, "name", "")
    var name: String by nameProperty

    /**
     * 完整镜像名
     */
    val imageNameProperty = SimpleStringProperty(this, "imageName", "")
    var imageName: String by imageNameProperty

    /**
     * 启动命令
     */
    val commandProperty = SimpleStringProperty(this, "command", "")
    var command: String by commandProperty

    /**
     * 描述
     */
    val descriptionProperty = SimpleStringProperty(this, "description", "")
    var description: String by descriptionProperty

    override fun toString(): String {
        return "Image(name=$name, imageName=$imageName, command=$command, description=$description)"
    }

    val stateProperty = SimpleBooleanProperty(this, "state", false)
    var state: Boolean by stateProperty

    val hostProperty = SimpleStringProperty(this, "host", "")
    var host: String by hostProperty

    var containerID = ""
}

class ExperimentModel(experiment: Experiment?) : ItemViewModel<Experiment>(experiment) {

    constructor(): this(Experiment())

    val name = bind(Experiment::nameProperty)
    val imageName = bind(Experiment::imageNameProperty)
    val command = bind(Experiment::commandProperty)
    val description = bind(Experiment::descriptionProperty)
    val state = bind(Experiment::stateProperty)
    val host = bind(Experiment::hostProperty)
}