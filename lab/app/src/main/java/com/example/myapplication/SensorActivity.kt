package com.example.myapplication

import android.Manifest
import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import android.content.pm.PackageManager
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

// 1. Data State สำหรับเก็บข้อมูล Accelerometer และ Location
data class SensorLocationState(
    val accelX: Float = 0f,
    val accelY: Float = 0f,
    val accelZ: Float = 0f,
    val latitude: Double = 0.0,
    val longitude: Double = 0.0,
    val isLocationPermissionGranted: Boolean = false
)

// 2. ViewModel สำหรับจัดการข้อมูลและลอจิกตามหลัก MVVM
class SensorViewModel(application: Application) : AndroidViewModel(application), SensorEventListener, LocationListener {
    private val _uiState = MutableStateFlow(SensorLocationState())
    val uiState: StateFlow<SensorLocationState> = _uiState.asStateFlow()

    private val sensorManager = application.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    private val accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
    private val locationManager = application.getSystemService(Context.LOCATION_SERVICE) as LocationManager

    fun updatePermissionStatus(isGranted: Boolean) {
        _uiState.value = _uiState.value.copy(isLocationPermissionGranted = isGranted)
    }

    fun startListening() {
        // ลงทะเบียน Accelerometer
        accelerometer?.let {
            sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_UI)
        }
    }

    @SuppressLint("MissingPermission")
    fun requestLocationUpdates() {
        if (_uiState.value.isLocationPermissionGranted) {
            // ตรวจสอบว่าเปิด GPS ไว้หรือไม่ แล้วขออัปเดตตำแหน่ง
            if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 2000L, 1f, this)
            } else if (locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
                locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 2000L, 1f, this)
            }
        }
    }

    fun stopListening() {
        // ยกเลิกการดักจับเซนเซอร์และพิกัดเมื่อไม่ได้ใช้งาน
        sensorManager.unregisterListener(this)
        locationManager.removeUpdates(this)
    }

    // --- SensorEventListener ---
    override fun onSensorChanged(event: SensorEvent?) {
        if (event?.sensor?.type == Sensor.TYPE_ACCELEROMETER) {
            _uiState.value = _uiState.value.copy(
                accelX = event.values[0],
                accelY = event.values[1],
                accelZ = event.values[2]
            )
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        // ไม่ใช้งาน
    }

    // --- LocationListener ---
    override fun onLocationChanged(location: Location) {
        _uiState.value = _uiState.value.copy(
            latitude = location.latitude,
            longitude = location.longitude
        )
    }
    @Deprecated("Deprecated in Java")
    override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {}
    override fun onProviderEnabled(provider: String) {}
    override fun onProviderDisabled(provider: String) {}
}

// 3. Activity เลเยอร์ที่เป็นเสมือน View ไปผูกกับ ViewModel
class SensorActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val viewModel: SensorViewModel = viewModel()
            val uiState by viewModel.uiState.collectAsState()

            // สร้าง Launcher ขอสิทธิ์ Location
            val locationPermissionLauncher = rememberLauncherForActivityResult(
                contract = ActivityResultContracts.RequestPermission()
            ) { isGranted ->
                viewModel.updatePermissionStatus(isGranted)
                if (isGranted) {
                    viewModel.requestLocationUpdates()
                }
            }

            // เช็คสิทธิ์เมื่อเริ่มหน้านี้
            LaunchedEffect(Unit) {
                val hasPermission = ContextCompat.checkSelfPermission(
                    this@SensorActivity,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED
                
                viewModel.updatePermissionStatus(hasPermission)
                if (hasPermission) {
                    viewModel.requestLocationUpdates()
                } else {
                    locationPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
                }
            }

            // สมัครการใช้งาน Sensor เมื่อหน้าจอแสดงผล และยกเลิกเมื่อโดนทำลายเพื่อประหยัดแบตเตอรี่
            DisposableEffect(Unit) {
                viewModel.startListening()
                onDispose {
                    viewModel.stopListening()
                }
            }

            SensorScreen(
                uiState = uiState,
                onBackClicked = { finish() }
            )
        }
    }
}

// แยกส่วน UI
@Composable
fun SensorScreen(
    uiState: SensorLocationState,
    onBackClicked: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(text = "Accelerometer Data", style = androidx.compose.material3.MaterialTheme.typography.titleLarge)
        Spacer(modifier = Modifier.height(16.dp))
        Text(text = "X: ${String.format("%.2f", uiState.accelX)}")
        Text(text = "Y: ${String.format("%.2f", uiState.accelY)}")
        Text(text = "Z: ${String.format("%.2f", uiState.accelZ)}")

        Spacer(modifier = Modifier.height(32.dp))

        Text(text = "Location Data", style = androidx.compose.material3.MaterialTheme.typography.titleLarge)
        Spacer(modifier = Modifier.height(16.dp))
        if (uiState.isLocationPermissionGranted) {
            Text(text = "Latitude: ${uiState.latitude}")
            Text(text = "Longitude: ${uiState.longitude}")
        } else {
            Text(text = "Location permission denied.")
        }

        Spacer(modifier = Modifier.height(32.dp))
        
        Button(onClick = onBackClicked) {
            Text("กลับไปหน้าหลัก")
        }
    }
}
