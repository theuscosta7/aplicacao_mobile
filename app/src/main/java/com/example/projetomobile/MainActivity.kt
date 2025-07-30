package com.example.projetomobile

import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    private lateinit var notificationHelper: Notification

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Inicializa o helper de notificações
        notificationHelper = Notification(this)

        // Botões de notificação
        val notification1 = findViewById<Button>(R.id.Notification1)
        val notification2 = findViewById<Button>(R.id.Notification2)

        // Clique no botão de notificação básica
        notification1.setOnClickListener {
            notificationHelper.showBasicNotification(
                title = "Notificação Básica",
                content = "Este é um exemplo de notificação simples."
            )
        }

        // Clique no botão de notificação avançada
        notification2.setOnClickListener {
            notificationHelper.showAdvancedNotification(
                title = "Notificação Avançada",
                content = "Este é um exemplo com ações.",
                imageUrl = "" // Ainda não está usando imagem
            )
        }
    }
}
