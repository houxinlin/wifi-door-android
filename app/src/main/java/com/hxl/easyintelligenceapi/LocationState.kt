package com.hxl.easyintelligenceapi

import android.util.Log


class LocationState:State {

    companion object{
        const val OPEN_WITH_LOCAL = "http://192.168.4.1/open"
        const val CLOSE_WITH_LOCAL = "http://192.168.4.1/close"
        const val READ_STATE = "http://192.168.4.1/getState"
    }

    override fun getState(callback: (Int?) -> Unit) {
          NetUtils.sendGet(READ_STATE){
              callback(if (it==null) StateValue.NONE.state else it!!.toInt())
          }
    }

    override fun setState(state: Int, success: (String?) -> Unit) {
        val url =  if (state == 1) OPEN_WITH_LOCAL else CLOSE_WITH_LOCAL
        Log.i("TAG",url)
        NetUtils.sendGet(url){ success(it) }
    }

}