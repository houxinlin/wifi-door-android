package com.hxl.easyintelligenceapi

interface State {
    fun  getState(callback:(Int?)->Unit)

    fun setState(state: Int,success:(String?)->Unit)
}