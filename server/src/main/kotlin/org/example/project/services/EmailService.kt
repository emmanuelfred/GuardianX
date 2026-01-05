package org.example.project.services

import io.ktor.server.application.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.example.project.data.models.Location
import java.util.*
import javax.mail.*
import javax.mail.internet.InternetAddress
import javax.mail.internet.MimeMessage

class EmailService(application: Application) {
    private val smtpHost: String = application.environment.config.property("smtp.host").getString()
    private val smtpPort: String = application.environment.config.property("smtp.port").getString()
    private val smtpUsername: String = application.environment.config.property("smtp.username").getString()
    private val smtpPassword: String = application.environment.config.property("smtp.password").getString()
    private val fromEmail: String = application.environment.config.property("smtp.fromEmail").getString()
    private val fromName: String = application.environment.config.property("smtp.fromName").getString()

    private val session: Session by lazy {
        val props = Properties().apply {
            put("mail.smtp.host", smtpHost)
            put("mail.smtp.port", smtpPort)
            put("mail.smtp.auth", "true")
            put("mail.smtp.starttls.enable", "true")
            put("mail.smtp.ssl.protocols", "TLSv1.2")
        }
        Session.getInstance(props, object : Authenticator() {
            override fun getPasswordAuthentication(): PasswordAuthentication {
                return PasswordAuthentication(smtpUsername, smtpPassword)
            }
        })
    }

    suspend fun sendEmail(to: String, subject: String, htmlBody: String): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                val message = MimeMessage(session).apply {
                    setFrom(InternetAddress(fromEmail, fromName))
                    setRecipients(Message.RecipientType.TO, InternetAddress.parse(to))
                    setSubject(subject)
                    setContent(htmlBody, "text/html; charset=utf-8")
                }
                Transport.send(message)
                true
            } catch (e: Exception) {
                e.printStackTrace()
                false
            }
        }
    }

    suspend fun sendVerificationEmail(to: String, fullName: String, code: String): Boolean {
        val subject = "Verify Your GuardianX Account"
        val htmlBody = """
            <!DOCTYPE html>
            <html>
            <head>
                <style>
                    body { font-family: Arial, sans-serif; line-height: 1.6; color: #333; }
                    .container { max-width: 600px; margin: 0 auto; padding: 20px; }
                    .header { background: #1e3a5f; color: white; padding: 20px; text-align: center; border-radius: 8px 8px 0 0; }
                    .content { background: #f9f9f9; padding: 30px; border-radius: 0 0 8px 8px; }
                    .code { font-size: 32px; font-weight: bold; color: #1e3a5f; text-align: center; padding: 20px; background: white; border-radius: 8px; letter-spacing: 8px; margin: 20px 0; }
                    .footer { text-align: center; margin-top: 20px; color: #666; font-size: 12px; }
                </style>
            </head>
            <body>
                <div class="container">
                    <div class="header">
                        <h1>🛡️ GuardianX</h1>
                    </div>
                    <div class="content">
                        <h2>Hello, $fullName!</h2>
                        <p>Thank you for registering with GuardianX. To complete your registration, please use the verification code below:</p>
                        <div class="code">$code</div>
                        <p>This code will expire in 15 minutes.</p>
                        <p>If you didn't create an account with GuardianX, please ignore this email.</p>
                        <p>Stay Safe,<br>The GuardianX Team</p>
                    </div>
                    <div class="footer">
                        <p>© ${java.time.Year.now().value} GuardianX. Your Safety, Our Priority.</p>
                    </div>
                </div>
            </body>
            </html>
        """.trimIndent()
        return sendEmail(to, subject, htmlBody)
    }

    suspend fun sendPasswordResetEmail(to: String, fullName: String, code: String): Boolean {
        val subject = "Reset Your GuardianX Password"
        val htmlBody = """
            <!DOCTYPE html>
            <html>
            <head>
                <style>
                    body { font-family: Arial, sans-serif; line-height: 1.6; color: #333; }
                    .container { max-width: 600px; margin: 0 auto; padding: 20px; }
                    .header { background: #1e3a5f; color: white; padding: 20px; text-align: center; border-radius: 8px 8px 0 0; }
                    .content { background: #f9f9f9; padding: 30px; border-radius: 0 0 8px 8px; }
                    .code { font-size: 32px; font-weight: bold; color: #ff6b5b; text-align: center; padding: 20px; background: white; border-radius: 8px; letter-spacing: 8px; margin: 20px 0; }
                    .footer { text-align: center; margin-top: 20px; color: #666; font-size: 12px; }
                </style>
            </head>
            <body>
                <div class="container">
                    <div class="header">
                        <h1>🛡️ GuardianX</h1>
                    </div>
                    <div class="content">
                        <h2>Hello, $fullName!</h2>
                        <p>We received a request to reset your password. Use the code below to reset it:</p>
                        <div class="code">$code</div>
                        <p>This code will expire in 15 minutes.</p>
                        <p>If you didn't request a password reset, please ignore this email or contact support if you're concerned about your account security.</p>
                        <p>Stay Safe,<br>The GuardianX Team</p>
                    </div>
                    <div class="footer">
                        <p>© ${java.time.Year.now().value} GuardianX. Your Safety, Our Priority.</p>
                    </div>
                </div>
            </body>
            </html>
        """.trimIndent()
        return sendEmail(to, subject, htmlBody)
    }

    suspend fun sendSOSAlertEmail(
        to: String,
        contactName: String,
        userName: String,
        location: Location,
        message: String? = null
    ): Boolean {
        val mapsLink = "https://www.google.com/maps?q=${location.latitude},${location.longitude}"
        val subject = "🚨 EMERGENCY ALERT from $userName - GuardianX"
        val htmlBody = """
            <!DOCTYPE html>
            <html>
            <head>
                <style>
                    body { font-family: Arial, sans-serif; line-height: 1.6; color: #333; }
                    .container { max-width: 600px; margin: 0 auto; padding: 20px; }
                    .header { background: #ff6b5b; color: white; padding: 20px; text-align: center; border-radius: 8px 8px 0 0; }
                    .content { background: #fff5f5; padding: 30px; border-radius: 0 0 8px 8px; }
                    .alert-box { background: white; border: 2px solid #ff6b5b; border-radius: 8px; padding: 20px; margin: 20px 0; }
                    .location-btn { display: inline-block; background: #1e3a5f; color: white; padding: 15px 30px; text-decoration: none; border-radius: 8px; margin: 10px 0; }
                    .footer { text-align: center; margin-top: 20px; color: #666; font-size: 12px; }
                </style>
            </head>
            <body>
                <div class="container">
                    <div class="header">
                        <h1>🚨 EMERGENCY ALERT</h1>
                    </div>
                    <div class="content">
                        <h2>Dear $contactName,</h2>
                        <p><strong>$userName</strong> has triggered an emergency alert and needs help!</p>
                        
                        <div class="alert-box">
                            <h3>📍 Location Details:</h3>
                            <p><strong>Address:</strong> ${location.address ?: "Address not available"}</p>
                            <p><strong>Coordinates:</strong> ${location.latitude}, ${location.longitude}</p>
                            <p><strong>Time:</strong> ${java.time.Instant.ofEpochMilli(location.timestamp)}</p>
                            ${if (message != null) "<p><strong>Message:</strong> $message</p>" else ""}
                        </div>
                        
                        <center>
                            <a href="$mapsLink" class="location-btn">📍 View Location on Google Maps</a>
                        </center>
                        
                        <p><strong>What you should do:</strong></p>
                        <ul>
                            <li>Try to contact $userName immediately</li>
                            <li>If you cannot reach them, consider contacting local authorities</li>
                            <li>Use the map link above to see their last known location</li>
                        </ul>
                        
                        <p>This alert was sent via GuardianX Safety App.</p>
                    </div>
                    <div class="footer">
                        <p>© ${java.time.Year.now().value} GuardianX. Your Safety, Our Priority.</p>
                    </div>
                </div>
            </body>
            </html>
        """.trimIndent()
        return sendEmail(to, subject, htmlBody)
    }

    suspend fun sendTripStartedEmail(
        to: String,
        contactName: String,
        userName: String,
        startLocation: Location,
        destination: Location,
        expectedArrivalTime: Long
    ): Boolean {
        val startMapsLink = "https://www.google.com/maps?q=${startLocation.latitude},${startLocation.longitude}"
        val destMapsLink = "https://www.google.com/maps?q=${destination.latitude},${destination.longitude}"
        val arrivalTime = java.time.Instant.ofEpochMilli(expectedArrivalTime)
        
        val subject = "🚗 $userName has started a trip - GuardianX"
        val htmlBody = """
            <!DOCTYPE html>
            <html>
            <head>
                <style>
                    body { font-family: Arial, sans-serif; line-height: 1.6; color: #333; }
                    .container { max-width: 600px; margin: 0 auto; padding: 20px; }
                    .header { background: #1e3a5f; color: white; padding: 20px; text-align: center; border-radius: 8px 8px 0 0; }
                    .content { background: #f0f4f8; padding: 30px; border-radius: 0 0 8px 8px; }
                    .trip-box { background: white; border-radius: 8px; padding: 20px; margin: 20px 0; }
                    .location-btn { display: inline-block; background: #22c55e; color: white; padding: 10px 20px; text-decoration: none; border-radius: 8px; margin: 5px; font-size: 14px; }
                    .footer { text-align: center; margin-top: 20px; color: #666; font-size: 12px; }
                </style>
            </head>
            <body>
                <div class="container">
                    <div class="header">
                        <h1>🚗 Trip Started</h1>
                    </div>
                    <div class="content">
                        <h2>Hello $contactName,</h2>
                        <p><strong>$userName</strong> has started a trip and added you as an emergency contact for monitoring.</p>
                        
                        <div class="trip-box">
                            <h3>Trip Details:</h3>
                            <p><strong>From:</strong> ${startLocation.address ?: "Starting point"}</p>
                            <a href="$startMapsLink" class="location-btn">📍 View Start</a>
                            
                            <p><strong>To:</strong> ${destination.address ?: "Destination"}</p>
                            <a href="$destMapsLink" class="location-btn">📍 View Destination</a>
                            
                            <p><strong>Expected Arrival:</strong> $arrivalTime</p>
                        </div>
                        
                        <p>You will be notified if anything unusual happens during this trip.</p>
                        
                        <p>Stay Safe,<br>The GuardianX Team</p>
                    </div>
                    <div class="footer">
                        <p>© ${java.time.Year.now().value} GuardianX. Your Safety, Our Priority.</p>
                    </div>
                </div>
            </body>
            </html>
        """.trimIndent()
        return sendEmail(to, subject, htmlBody)
    }

    // Generate a 6-digit code
    fun generateVerificationCode(): String {
        return (100000..999999).random().toString()
    }
}
