package com.github.ryoii.event

import com.github.ryoii.model.Experiment
import tornadofx.FXEvent

/*
* 通知UI进行样式变更和数据刷新的事件
* 在应用线程上执行（UI更新必须在应用线程上执行）
* */

/**
 * 实验已启动
 */
class ExperimentStartedEvent(val experiment: Experiment) : FXEvent()

/**
 * 实验已停止
 */
class ExperimentStoppedEvent(val experiment: Experiment) : FXEvent()

/**
 * 实验的容器名被占用
 *
 * 实验容器运行时才创建，该事件包含创建容器的容器名冲突，
 * 也包含运行容器时，容器名已存在，即实验重复启动
 */
class ContainerNameConflictEvent(val experiment: Experiment) : FXEvent()

/**
 * 运行的容器不存在
 *
 * 实验容器先创建后运行，实际上应该不会出现该错误
 */
class ContainerNotFoundEvent(val experiment: Experiment) : FXEvent()

/**
 * 拉取镜像进度推流
 */
class PullImageEvent(val experiment: Experiment, val process: Double) : FXEvent()

/**
 * 拉取镜像时，镜像不存在
 */
class ImageNotFoundEvent(val experiment: Experiment) : FXEvent()