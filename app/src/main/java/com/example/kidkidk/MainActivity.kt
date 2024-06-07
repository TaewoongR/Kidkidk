package com.example.kidkidk

import android.Manifest.permission.BLUETOOTH_CONNECT
import android.Manifest.permission.BLUETOOTH_SCAN
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothSocket
import android.content.Intent
import android.content.pm.PackageManager
import kotlinx.coroutines.*
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.ui.NavigationScreen
import com.example.ui.SharedViewModel
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
    private var bluetoothSocket: BluetoothSocket? = null
    private var inputStream: InputStream? = null

    private val sharedViewModel:SharedViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 권한 요청
        if (ContextCompat.checkSelfPermission(this, BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED ||
            ContextCompat.checkSelfPermission(this, BLUETOOTH_SCAN) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(BLUETOOTH_CONNECT, BLUETOOTH_SCAN),
                1
            )
        }

        setContent {
            var bluetoothData by remember { mutableStateOf("Waiting for data...") }
            val enableBtLauncher = rememberLauncherForActivityResult(
                ActivityResultContracts.StartActivityForResult()
            ) {
                if (it.resultCode == RESULT_OK) {
                    setupBluetoothConnection { data ->
                        bluetoothData = data
                        sharedViewModel.updateBluetoothData(data)
                    }
                }
            }

            var job by remember { mutableStateOf<Job?>(null) }

            fun resetTimer() {
                job?.cancel()
                job = CoroutineScope(Dispatchers.Main).launch {
                    delay(3000)
                    sharedViewModel.updateBluetoothData("null")
                }
            }

            LaunchedEffect(bluetoothData) {
                resetTimer()
            }

            if (bluetoothAdapter == null) {
                bluetoothData = "Bluetooth is not available"
            } else {
                if (!bluetoothAdapter.isEnabled) {
                    enableBtLauncher.launch(Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE))
                } else {
                    setupBluetoothConnection { data ->
                        bluetoothData = data
                        resetTimer()
                        sharedViewModel.updateBluetoothData(data)
                    }
                }
            }

            NavigationScreen(sharedViewModel)
        }

    }

    private fun setupBluetoothConnection(onDataReceived: (String) -> Unit) {
        val device: BluetoothDevice? = bluetoothAdapter?.bondedDevices?.find { it.name == "HC-06" }
        device?.let {
            val uuid: UUID = it.uuids[0].uuid
            try {
                bluetoothSocket = it.createRfcommSocketToServiceRecord(uuid)
                bluetoothSocket?.connect()
                inputStream = bluetoothSocket?.inputStream
                Log.d("Bluetooth", "Bluetooth connection established. Ready to receive data.")
                listenForData(onDataReceived)
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }

    private var bluetoothJob: Job? = null

    private fun listenForData(onDataReceived: (String) -> Unit) {
        bluetoothJob = CoroutineScope(Dispatchers.IO).launch {
            val buffer = ByteArray(1024)
            var bytes: Int

            Log.d("Bluetooth", "Listening for data...")
            while (isActive) {
                try {
                    inputStream?.let {
                        bytes = it.read(buffer)
                        if (bytes > 0) {
                            val readMessage = String(buffer, 0, bytes)
                            withContext(Dispatchers.Main) {
                                onDataReceived(readMessage)
                                Log.d("Bluetooth", "Data received: $readMessage")
                            }
                        }
                    }
                } catch (e: IOException) {
                    e.printStackTrace()
                    break
                }
            }
        }
    }


    override fun onDestroy() {
        super.onDestroy()
        try {
            bluetoothSocket?.close()
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
