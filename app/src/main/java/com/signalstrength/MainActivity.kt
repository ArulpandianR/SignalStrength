package com.signalstrength

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.telephony.*
import android.widget.GridLayout
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {

    private var gridLayout: GridLayout? = null
    val mainHandler = Handler(Looper.getMainLooper())
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        gridLayout = findViewById(R.id.gridLayout)

        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                101
            )
        }
        start.setOnClickListener {
            stop.isEnabled = true
            start.isEnabled = false
            mainHandler.post(updateTextTask)
        }
        stop.setOnClickListener {
            start.isEnabled = true
            stop.isEnabled = false
            mainHandler.removeCallbacks(updateTextTask)
        }
        clear.setOnClickListener {
            gridLayout?.removeAllViews()
        }
    }

    private val updateTextTask = object : Runnable {
        override fun run() {
            getAllCellinfo(this@MainActivity)
            mainHandler.postDelayed(this, 1000)
        }
    }

    @SuppressLint("MissingPermission")
    fun getAllCellinfo(context: Context) {
        val telephonyManager =
            context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
        var strength = ""
        var identity = ""
        var type = ""
        val cellInfos = telephonyManager.allCellInfo //This will give info of all sims present inside your mobile


        if (cellInfos != null) {
            for (i in cellInfos.indices) {
                if (cellInfos[i].isRegistered) {
                    if (cellInfos[i] is CellInfoWcdma) {
                        val cellInfoWcdma = cellInfos[i] as CellInfoWcdma
                        val cellSignalStrengthWcdma =
                            cellInfoWcdma.cellSignalStrength
                        strength = cellSignalStrengthWcdma.dbm.toString()
                        type = "WCDMA"
                    } else if (cellInfos[i] is CellInfoGsm) {
                        val cellInfogsm = cellInfos[i] as CellInfoGsm
                        val cellSignalStrengthGsm =
                            cellInfogsm.cellSignalStrength
                        strength = cellSignalStrengthGsm.dbm.toString()
                        type = "GSM"
                    } else if (cellInfos[i] is CellInfoLte) {
                        val cellInfoLte = cellInfos[i] as CellInfoLte
                        val cellSignalStrengthIdentityLte =
                            cellInfoLte.cellIdentity
                        val cellSignalStrengthLte =
                            cellInfoLte.cellSignalStrength
                       // identity = cellInfoLte?.cellIdentity.mobileNetworkOperator.operatorAlphaShort ?: ""
                        strength = cellSignalStrengthLte.dbm.toString()
                        type = "LTE"
                    } else if (cellInfos[i] is CellInfoCdma) {
                        val cellInfoCdma = cellInfos[i] as CellInfoCdma
                        val cellSignalStrengthCdma =
                            cellInfoCdma.cellSignalStrength
                        strength = cellSignalStrengthCdma.dbm.toString()
                        type = "CDMA"
                    }
                    addGrid(type, strength,identity)
                }
            }
        }
    }

    fun addGrid(signalType: String, signalValue: String,identity:String) {
        val nameView = TextView(this)
        nameView.setPadding(10, 10, 10, 10)
        nameView.text = "$signalType : $identity"
        gridLayout?.addView(nameView)
        val valueView = TextView(this)
        valueView.setPadding(10, 10, 10, 10)
        valueView.text = signalValue
        gridLayout?.addView(valueView)
    }

}