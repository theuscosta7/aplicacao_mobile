package com.example.projetomobile

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

class MainActivity : AppCompatActivity() {

    private lateinit var notificationHelper: Notification

    companion object {
        private const val PERMISSION_REQUEST_CODE = 100
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        notificationHelper = Notification(this)

        val notification1 = findViewById<Button>(R.id.Notification1)
        val notification2 = findViewById<Button>(R.id.Notification2)
        val cameraButton = findViewById<Button>(R.id.CameraPermission)
        val locationButton = findViewById<Button>(R.id.LocationPermission)

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
            checkAndRequestPermission(Manifest.permission.CAMERA, "Câmera")
        }

        locationButton.setOnClickListener {
            checkAndRequestPermission(Manifest.permission.ACCESS_FINE_LOCATION, "Localização")
        }
    }

    private fun checkAndRequestPermission(permission: String, tipo: String) {
        if (ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this, "$tipo: Permissão já concedida", Toast.LENGTH_SHORT).show()
        } else {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, permission)) {
                showPermissionExplanationDialog(permission, tipo)
            } else {
                ActivityCompat.requestPermissions(this, arrayOf(permission), PERMISSION_REQUEST_CODE)
            }
        }
    }

    private fun showPermissionExplanationDialog(permission: String, tipo: String) {
        AlertDialog.Builder(this)
            .setTitle("Permissão necessária")
            .setMessage("Este recurso precisa de acesso à $tipo. Deseja permitir?")
            .setPositiveButton("Sim") { _, _ ->
                ActivityCompat.requestPermissions(this, arrayOf(permission), PERMISSION_REQUEST_CODE)
            }
            .setNegativeButton("Não", null)
            .show()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
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
}