package edu.uw.ischool.chuns4.awty

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.telephony.SmsManager
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
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
    private val PERMISSION_REQUEST_CODE = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
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
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.SEND_SMS), PERMISSION_REQUEST_CODE)
            return
        }
        isRunning = true
        startStopButton.text = "Stop"

        executor.execute {
            while (isRunning) {
                handler.post {
                    Toast.makeText(this, "$phoneNumber: $message", Toast.LENGTH_SHORT).show()
                    sendSMS(phoneNumber, message)
                }
                Thread.sleep(intervalMinutes * 60 * 1000L)
            }
        }
    }

    private fun stopMessaging() {
        isRunning = false
        startStopButton.text = "Start"
    }

    private fun sendSMS(phoneNumber: String, message: String) {
        try {
            val smsManager = SmsManager.getDefault()
            smsManager.sendTextMessage(phoneNumber, null, message, null, null)
        } catch (e: Exception) {
            handler.post {
                Toast.makeText(this, "Failed to send SMS: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startMessaging()
            } else {
                Toast.makeText(this, "SMS permission denied.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        isRunning = false
        executor.shutdown()
    }
}
