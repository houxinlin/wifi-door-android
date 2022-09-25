package com.hxl.easyintelligenceapi

class NetworkState:State {
    companion object {
        const val GET_STATUS = "http://xxxx.com:8082/get/status"
        const val SET_STATUS="http://xxxx.com:8082/set/status?status="
    }

    override fun getState(callback: (Int?) -> Unit) {
        NetUtils.sendGet(GET_STATUS){
            callback(if (it==null) StateValue.NONE.state else it!!.toInt())
        }
    }

    override fun setState(state: Int, success: (String?) -> Unit) {
        NetUtils.sendGet("$SET_STATUS$state"){ success(it) }
    }
}