package com.github.ryoii.model

import javafx.beans.property.SimpleStringProperty
import tornadofx.ItemViewModel
import tornadofx.getValue
import tornadofx.setValue

class GlobalInfo {

    val dockerMachineIpProperty = SimpleStringProperty(this, "dockerMachineIp", "192.168.99.100")
    var dockerMachineIp: String by dockerMachineIpProperty

    val dockerMachineAPIPortProperty = SimpleStringProperty(this, "dockerMachineAPIPort", "2376")
    var dockerMachineAPIPort: String by dockerMachineAPIPortProperty


    override fun toString(): String {
        return "Config(dockerMachineIp=$dockerMachineIp, dockerMachineAPIPort=$dockerMachineAPIPort)"
    }
}


class GlobalInfoModel(globalInfo: GlobalInfo): ItemViewModel<GlobalInfo>(globalInfo) {

    constructor():this(GlobalInfo())

    val dockerMachineIp = bind(GlobalInfo::dockerMachineIpProperty)
    val dockerMachineAPIPort = bind(GlobalInfo::dockerMachineAPIPortProperty)
}