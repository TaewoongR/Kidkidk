package com.example.ui

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

@HiltViewModel
class SharedViewModel @Inject constructor(
) : ViewModel() {

    private val _uiState = MutableStateFlow(SharedUiState())
    val uiState: StateFlow<SharedUiState> = _uiState.asStateFlow()

    private val detectedTime = MutableStateFlow(0L)
    private val timeRange = MutableStateFlow(0L)
    private var isHelloDetected = false

    private val firestore = FirebaseFirestore.getInstance()

    init {
        initialUiState()
    }

    fun updateBluetoothData(data: String) {
        viewModelScope.launch {
            processBluetoothData(data)
        }
    }

    fun processBluetoothData(data: String) {
        viewModelScope.launch {
            Log.d("viewmodel", "Data Collected: $data")
            when {
                data == "hello" && !isHelloDetected -> {
                    isHelloDetected = true
                    detectedTime.value = System.currentTimeMillis()
                    Log.d("viewmodel", "Hello detected: ${detectedTime.value}")
                }

                data == "hello" && isHelloDetected -> {
                    timeRange.value = System.currentTimeMillis() - detectedTime.value
                    Log.d(
                        "viewmodel",
                        "Time Range Updated: ${timeRange.value}, Detected Time: ${
                            formatTime(detectedTime.value)
                        }"
                    )
                }

                data != "hello" && isHelloDetected -> {
                    isHelloDetected = false
                    detectedTime.value = 0L
                    timeRange.value = 0L
                    Log.d("viewmodel", "Data changed from hello, Reset detectedTime")
                }
            }
            initialUiState()
        }
    }

    fun initialUiState() {
        val signalRange = (timeRange.value / 1000).toInt()
        _uiState.value = SharedUiState(
            signalRange = signalRange,
            parkedStart = formatTime(detectedTime.value)
        )
        Log.d(
            "viewmodel",
            "UI State Updated: signalRange = ${uiState.value.signalRange}, parkedStart = ${uiState.value.parkedStart}"
        )
    }

    private fun formatTime(timeMillis: Long): String {
        return if (timeMillis == 0L) {
            ""
        } else {
            val sdf = SimpleDateFormat("yyyy/MM/dd hh:mm", Locale.getDefault())
            val calendar = Calendar.getInstance()
            calendar.timeInMillis = timeMillis
            sdf.format(calendar.time)
        }
    }

    fun reportIllegalParking(signalRange: Int, parkedStart: String) {
        viewModelScope.launch {
            val data = hashMapOf(
                "signalRange" to signalRange,
                "parkedStart" to parkedStart,
                "timestamp" to System.currentTimeMillis()
            )

            firestore.collection("report")
                .add(data)
                .addOnSuccessListener {
                    Log.d("SharedViewModel", "DocumentSnapshot successfully written!")
                }
                .addOnFailureListener { e ->
                    Log.w("SharedViewModel", "Error writing document", e)
                }
        }
    }
}