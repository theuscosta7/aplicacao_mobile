package com.example.projetomobile

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.os.Looper
import android.provider.MediaStore
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.biometric.BiometricPrompt
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.location.*

class MainActivity : AppCompatActivity() {

    private lateinit var notificationHelper: Notification
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    private lateinit var biometricPrompt: BiometricPrompt
    private lateinit var promptInfo: BiometricPrompt.PromptInfo

    companion object {
        private const val PERMISSION_REQUEST_CODE = 100
        private const val CAMERA_REQUEST_CODE = 101
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        notificationHelper = Notification(this)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        val notification1 = findViewById<Button>(R.id.Notification1)
        val notification2 = findViewById<Button>(R.id.Notification2)
        val cameraButton = findViewById<Button>(R.id.CameraPermission)
        val locationButton = findViewById<Button>(R.id.LocationPermission)
        val autenticationButton = findViewById<Button>(R.id.btnBiometricAuth)

        notification1.setOnClickListener {
            notificationHelper.showBasicNotification(
                title = "Notificação Básica",
                content = "Este é um exemplo de notificação simples."
            )
        }

        notification2.setOnClickListener {
            notificationHelper.showAdvancedNotification(
                title = "Notificação Avançada",
                content = "Este é um exemplo com ações.",
                imageResId = R.drawable.logo
            )
        }

        cameraButton.setOnClickListener {
            checkAndRequestPermission(Manifest.permission.CAMERA, "Câmera") {
                openCamera()
            }
        }

        locationButton.setOnClickListener {
            checkAndRequestPermission(Manifest.permission.ACCESS_FINE_LOCATION, "Localização") {
                getCurrentLocation()
            }
        }

        // Autenticação
        val executor = ContextCompat.getMainExecutor(this)
        biometricPrompt = BiometricPrompt(this, executor,
            object : BiometricPrompt.AuthenticationCallback() {
                override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                    super.onAuthenticationSucceeded(result)
                    val intent = Intent(this@MainActivity, Authentication::class.java)
                    startActivity(intent)
                }

                override fun onAuthenticationFailed() {
                    super.onAuthenticationFailed()
                    showToast("Autenticação falhou.")
                }
            })

        promptInfo = BiometricPrompt.PromptInfo.Builder()
            .setTitle("Teste de Autenticação")
            .setSubtitle("Localize o sensor.")
            .setNegativeButtonText("Cancelar")
            .build()

        autenticationButton.setOnClickListener {
            biometricPrompt.authenticate(promptInfo)
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    private fun checkAndRequestPermission(permission: String, tipo: String, onGranted: () -> Unit) {
        if (ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this, "$tipo: Permissão já concedida", Toast.LENGTH_SHORT).show()
            onGranted()
        } else {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, permission)) {
                showPermissionExplanationDialog(permission, tipo, onGranted)
            } else {
                ActivityCompat.requestPermissions(this, arrayOf(permission), PERMISSION_REQUEST_CODE)
            }
        }
    }

    private fun showPermissionExplanationDialog(permission: String, tipo: String, onGranted: () -> Unit) {
        AlertDialog.Builder(this)
            .setTitle("Permissão necessária")
            .setMessage("Este recurso precisa de acesso à $tipo. Deseja permitir?")
            .setPositiveButton("Sim") { _, _ ->
                ActivityCompat.requestPermissions(this, arrayOf(permission), PERMISSION_REQUEST_CODE)
            }
            .setNegativeButton("Não", null)
            .show()
    }

    private fun openCamera() {
        val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        if (cameraIntent.resolveActivity(packageManager) != null) {
            startActivityForResult(cameraIntent, CAMERA_REQUEST_CODE)
        } else {
            Toast.makeText(this, "Nenhuma câmera disponível.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun getCurrentLocation() {
        val locationRequest = LocationRequest.create().apply {
            interval = 10000
            fastestInterval = 5000
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
            numUpdates = 1
        }

        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }

        fusedLocationClient.requestLocationUpdates(
            locationRequest,
            object : LocationCallback() {
                override fun onLocationResult(result: LocationResult) {
                    val location: Location? = result.lastLocation
                    if (location != null) {
                        val lat = location.latitude
                        val lon = location.longitude
                        Toast.makeText(
                            this@MainActivity,
                            "Localização: $lat, $lon",
                            Toast.LENGTH_LONG
                        ).show()
                    } else {
                        Toast.makeText(this@MainActivity, "Não foi possível obter a localização", Toast.LENGTH_SHORT).show()
                    }
                }
            },
            Looper.getMainLooper()
        )
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == PERMISSION_REQUEST_CODE) {
            for (i in permissions.indices) {
                val tipo = when (permissions[i]) {
                    Manifest.permission.CAMERA -> "Câmera"
                    Manifest.permission.ACCESS_FINE_LOCATION -> "Localização"
                    else -> "Permissão"
                }

                if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, "$tipo: Permissão concedida!", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, "$tipo: Permissão negada.", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == CAMERA_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            Toast.makeText(this, "Foto capturada com sucesso!", Toast.LENGTH_SHORT).show()
        }
    }
}
