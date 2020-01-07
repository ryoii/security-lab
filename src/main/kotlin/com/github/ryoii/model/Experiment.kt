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

    constructor(id: Int, name: String, imageName: String, containerName: String, description: String,
                portMap: String, mount: String, homepage: String) : this() {
        this.id = id
        this.name = name
        this.imageName = imageName
        this.containerName = containerName
        this.description = description
        this.portMap = portMap
        this.mount = mount
        this.homepage = homepage
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
     * 容器名
     */
    val containerNameProperty = SimpleStringProperty(this, "containerName", "")
    var containerName: String by containerNameProperty

    /**
     * 描述
     */
    val descriptionProperty = SimpleStringProperty(this, "description", "")
    var description: String by descriptionProperty

    /**
     * 端口映射
     */
    val portMapProperty = SimpleStringProperty(this, "portMap", "")
    var portMap: String by portMapProperty

    /**
     * 文件挂载
     */
    val mountProperty = SimpleStringProperty(this, "mount", "")
    var mount: String by mountProperty

    /**
     * 主页
     */
    val homepageProperty = SimpleStringProperty(this, "homepage", "")
    var homepage: String by homepageProperty

    /**
     * 状态属性，不参与持久化
     */
    val stateProperty = SimpleBooleanProperty(this, "state", false)
    var state: Boolean by stateProperty

    var containerID = ""
}

class ExperimentModel(experiment: Experiment?) : ItemViewModel<Experiment>(experiment) {

    constructor() : this(Experiment())

    val name = bind(Experiment::nameProperty)
    val imageName = bind(Experiment::imageNameProperty)
    val containerName = bind(Experiment::containerNameProperty)
    val description = bind(Experiment::descriptionProperty)
    val portMap = bind(Experiment::portMapProperty)
    val mount = bind(Experiment::mountProperty)
    val homepage = bind(Experiment::homepageProperty)
    val state = bind(Experiment::stateProperty)
}