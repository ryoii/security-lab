package com.github.ryoii.event

import com.github.ryoii.model.Experiment
import tornadofx.EventBus.RunOn.BackgroundThread
import tornadofx.FXEvent

/*
* 事件是基于观察者模式设计的消息机制，通过声明式订阅进行消息传递
* 事件的定义根据是否携带参数，定义为单例事件和实例事件
* 类似有关Experiment的事件，涉及需要获取具体的实验信息，因此大部分是实例的
* 该类事件采用后台线程执行，从而避免UI阻塞
* */


/**
 * 启动实验，不论相关docker镜像是否存在
 */
class RunExperimentEvent(val experiment: Experiment) : FXEvent(BackgroundThread)

/**
 * 停止实验
 */
class StopExperimentEvent(val experiment: Experiment) : FXEvent(BackgroundThread)