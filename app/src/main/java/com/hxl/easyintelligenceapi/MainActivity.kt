package com.hxl.easyintelligenceapi

import android.Manifest
import android.animation.ObjectAnimator
import android.app.ProgressDialog
import android.content.pm.PackageManager
import android.net.wifi.WifiManager
import android.os.Bundle
import android.util.Log
import android.view.animation.BounceInterpolator
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import       android.widget.Toast

class MainActivity : AppCompatActivity() {


    private lateinit var button: TextView
    private var state: StateValue = StateValue.NONE
    private lateinit var loading: ProgressDialog

    private lateinit var stateManager: State
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        button = findViewById<TextView>(R.id.button)
        val isLocation = isLocation()
        stateManager = if (isLocation) LocationState() else NetworkState()
        if (!hasPermission()) {
            requestPermission()
        }
        initView()
    }

    fun  requestPermission(){
        val permission = arrayOf(Manifest.permission.ACCESS_FINE_LOCATION)
        ActivityCompat.requestPermissions(this, permission, 200)
    }
    private fun initView() {
        loading = ProgressDialog(this)
        loading.setTitle("加载中")

        button.setOnClickListener {
            if (!hasPermission()) {
                android.widget.Toast.makeText(this, "请赋予权限", Toast.LENGTH_SHORT).show()
                requestPermission()
                return@setOnClickListener
            }
            beginAnimator()
            loading.show()
            changeStatus(if (state == StateValue.CLOSE) StateValue.OPEN else StateValue.CLOSE)
        }
    }

    private fun beginAnimator() {
        var objectAnimator = ObjectAnimator.ofFloat(button, "scaleY", 0f, 1f)
        objectAnimator.duration = 800
        objectAnimator.interpolator = BounceInterpolator()
        objectAnimator.start()
    }

    private fun refreshState() {
        stateManager.getState {
            reverse(it!!)
        }
    }

    private fun reverse(value: Int) {
        state = if (value == 0) StateValue.CLOSE else StateValue.OPEN
        button.setBackgroundResource(if (value == 0) R.drawable.shape_raduis_status else R.drawable.shape_raduis_status_red)
        button.setText(if (value == 0) "开门" else "关门")
        Log.i("TAG", "getStatus $state $value")
    }

    private fun hasPermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun isLocation():Boolean{
        return kotlin.collections.listOf<String>("\"103门禁\"","\"103灯\"").contains(getCurrentSsid())
    }
    private fun changeStatus(newStatus: StateValue) {
        val isLocation = isLocation()
        stateManager = if (isLocation) LocationState() else NetworkState()
        stateManager.setState(newStatus.state) {
            Log.i("TAG", it.toString())
            loading.cancel()
            if ((stateManager is LocationState)) {
                state =
                    if (state.state == StateValue.CLOSE.state) StateValue.OPEN else StateValue.CLOSE
                reverse(state.state)
                return@setState
            }
            refreshState()

        }

    }
    private fun restart(){
        val isLocation = getCurrentSsid() == "\"103门禁\""
        stateManager = if (isLocation) LocationState() else NetworkState()
    }

    private fun getCurrentSsid(): String {
        val wifiMgr = applicationContext.getSystemService(WIFI_SERVICE) as WifiManager
        if (wifiMgr != null) {
            val info = wifiMgr.connectionInfo
            if (info != null) return info.ssid
        }
        return ""

    }

    override fun onResume() {
        super.onResume()
        refreshState()
    }
}