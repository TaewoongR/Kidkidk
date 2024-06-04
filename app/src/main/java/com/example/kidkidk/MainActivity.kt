package com.example.kidkidk

import android.Manifest.permission.BLUETOOTH_CONNECT
import android.Manifest.permission.BLUETOOTH_SCAN
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothSocket
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.ui.DetectScreen
import com.example.ui.NavigationScreen
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import dagger.hilt.android.AndroidEntryPoint
import java.io.IOException
import java.io.InputStream
import java.util.UUID

@AndroidEntryPoint
class MainActivity : ComponentActivity(), OnMapReadyCallback {
    private val bluetoothAdapter: BluetoothAdapter? = BluetoothAdapter.getDefaultAdapter()
    private lateinit var bluetoothSocket: BluetoothSocket
    private lateinit var inputStream: InputStream

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // 권한 요청
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (ContextCompat.checkSelfPermission(this, BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this, BLUETOOTH_SCAN) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(BLUETOOTH_CONNECT, BLUETOOTH_SCAN),
                    1
                )
            }
        }

        setContent {
            var bluetoothData by remember { mutableStateOf("Waiting for data...") }
            val enableBtLauncher = rememberLauncherForActivityResult(
                ActivityResultContracts.StartActivityForResult()
            ) {
                if (it.resultCode == RESULT_OK) {
                    setupBluetoothConnection { data ->
                        bluetoothData = data
                    }
                }
            }

            if (bluetoothAdapter == null) {
                bluetoothData = "Bluetooth is not available"
            } else {
                if (!bluetoothAdapter.isEnabled) {
                    enableBtLauncher.launch(Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE))
                } else {
                    setupBluetoothConnection { data ->
                        bluetoothData = data
                    }
                }
            }
            NavigationScreen(bluetoothData)
        }
    }

    private fun setupBluetoothConnection(onDataReceived: (String) -> Unit) {
        val device: BluetoothDevice? = bluetoothAdapter?.bondedDevices?.find { it.name == "HC-05" }
        device?.let {
            val uuid: UUID = it.uuids[0].uuid
            try {
                bluetoothSocket = it.createRfcommSocketToServiceRecord(uuid)
                bluetoothSocket.connect()
                inputStream = bluetoothSocket.inputStream
                listenForData(onDataReceived)
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }

    private fun listenForData(onDataReceived: (String) -> Unit) {
        val buffer = ByteArray(1024)
        var bytes: Int

        Thread {
            while (true) {
                try {
                    bytes = inputStream.read(buffer)
                    val readMessage = String(buffer, 0, bytes)
                    onDataReceived(readMessage)
                } catch (e: IOException) {
                    e.printStackTrace()
                    break
                }
            }
        }.start()
    }

    override fun onDestroy() {
        super.onDestroy()
        try {
            bluetoothSocket.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        googleMap.addMarker(
            MarkerOptions()
                .position(LatLng(0.0, 0.0))
                .title("Marker")
        )
    }
}