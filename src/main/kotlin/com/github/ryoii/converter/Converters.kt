package com.github.ryoii.converter

import javafx.util.StringConverter

class StateConverter : StringConverter<Boolean>() {
    override fun toString(state: Boolean?): String =
        if (state!!) "运行中" else "未运行"

    // ignore
    override fun fromString(p0: String?): Boolean = true
}

class RunButtonStateConverter: StringConverter<Boolean>() {
    override fun toString(state: Boolean?): String =
        if (state!!) "停止" else "运行"

    // ignore
    override fun fromString(p0: String?): Boolean = true
}