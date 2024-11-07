package edu.uw.ischool.chuns4.awty


import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import java.util.concurrent.Executors

class MainActivity : AppCompatActivity() {
    lateinit var messageEditText: EditText
    lateinit var phoneNumberEditText: EditText
    lateinit var intervalEditText: EditText
    lateinit var startStopButton: Button

    private var isRunning = false
    private val executor = Executors.newSingleThreadExecutor()
    private val handler = Handler(Looper.getMainLooper())
    private var intervalMinutes = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        messageEditText = findViewById(R.id.messageEditText)
        phoneNumberEditText = findViewById(R.id.phoneNumberEditText)
        intervalEditText = findViewById(R.id.intervalEditText)
        startStopButton = findViewById(R.id.startStopButton)

        startStopButton.setOnClickListener {
            if (isRunning) {
                stopMessaging()
            } else {
                startMessaging()
            }
        }
    }

    private fun startMessaging() {
        val message = messageEditText.text.toString()
        val phoneNumber = phoneNumberEditText.text.toString()
        intervalMinutes = intervalEditText.text.toString().toIntOrNull() ?: 0

        if (message.isBlank() || phoneNumber.isBlank() || intervalMinutes <= 0) {
            if (message.isBlank() && phoneNumber.isBlank() && intervalMinutes <= 0) {
                Toast.makeText(this, "Please enter all valid values.", Toast.LENGTH_SHORT).show()
            } else if (message.isBlank() && phoneNumber.isBlank()) {
                Toast.makeText(this, "Please enter message and phone number.", Toast.LENGTH_SHORT).show()
            } else if (message.isBlank() && intervalMinutes <= 0) {
                Toast.makeText(this, "Please enter message and correct interval minutes.", Toast.LENGTH_SHORT).show()
            } else if (phoneNumber.isBlank() && intervalMinutes <= 0) {
                Toast.makeText(this, "Please enter phone number and correct interval minutes.", Toast.LENGTH_SHORT).show()
            } else if (message.isBlank()) {
                Toast.makeText(this, "Please enter message.", Toast.LENGTH_SHORT).show()
            } else if (phoneNumber.isBlank()) {
                Toast.makeText(this, "Please enter phone number.", Toast.LENGTH_SHORT).show()
            } else if (intervalMinutes <= 0) {
                Toast.makeText(this, "Please enter correct interval minutes.", Toast.LENGTH_SHORT).show()
            }
            return
        }

        isRunning = true
        startStopButton.text = "Stop"

        executor.execute {
            while (isRunning) {
                handler.post {
                    Toast.makeText(this, "$phoneNumber: $message", Toast.LENGTH_SHORT).show()
                }
                Thread.sleep(intervalMinutes * 60 * 1000L)
            }
        }
    }

    private fun stopMessaging() {
        isRunning = false
        startStopButton.text = "Start"
    }

    override fun onDestroy() {
        super.onDestroy()
        isRunning = false
        executor.shutdown()
    }

}