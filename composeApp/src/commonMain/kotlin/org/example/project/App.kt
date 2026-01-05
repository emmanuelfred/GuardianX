package org.example.project

import androidx.compose.runtime.*
import org.example.project.screens.*
import org.example.project.theme.GuardianXTheme

// Complete enum for all screens
enum class AppScreen {
    // Auth Flow
    Splash, Onboarding, Login, SignUp,
    VerifyEmail, ForgotPassword, VerifyResetCode, ResetPassword,
    
    // Main App
    Home, Map, Community, Profile,
    
    // Safety Features
    PanicMode, StartTrip, TripInProgress,
    ShareLocation, ReportDanger,
    
    // Contacts
    GuardianContacts, AddContact,
    
    // Profile & Settings
    EditProfile, Settings, NotificationSettings, SafetyTips
}

@Composable
fun App() {
    GuardianXTheme {
        var currentScreen by remember { mutableStateOf(AppScreen.Splash) }
        var userEmail by remember { mutableStateOf("") }
        var previousScreen by remember { mutableStateOf(AppScreen.Home) }

        when (currentScreen) {
            // ===== AUTH FLOW =====
            AppScreen.Splash -> SplashScreen(
                onSplashComplete = { currentScreen = AppScreen.Onboarding }
            )
            
            AppScreen.Onboarding -> OnboardingScreen(
                onOnboardingComplete = { currentScreen = AppScreen.Login }
            )
            
            AppScreen.Login -> LoginScreen(
                onLoginClick = { currentScreen = AppScreen.Home },
                onSignUpClick = { currentScreen = AppScreen.SignUp },
                onForgotPasswordClick = { currentScreen = AppScreen.ForgotPassword }
            )
            
            AppScreen.SignUp -> SignUpScreen(
                onSignUpClick = { email ->
                    userEmail = email
                    currentScreen = AppScreen.VerifyEmail
                },
                onLoginClick = { currentScreen = AppScreen.Login }
            )
            
            AppScreen.VerifyEmail -> VerifyEmailScreen(
                email = userEmail,
                onVerifySuccess = { currentScreen = AppScreen.Home },
                onBackClick = { currentScreen = AppScreen.SignUp },
                onResendCode = { /* Resend verification code */ }
            )
            
            AppScreen.ForgotPassword -> ForgotPasswordScreen(
                onSendCode = { email ->
                    userEmail = email
                    currentScreen = AppScreen.VerifyResetCode
                },
                onBackClick = { currentScreen = AppScreen.Login }
            )
            
            AppScreen.VerifyResetCode -> VerifyResetCodeScreen(
                email = userEmail,
                onVerifySuccess = { currentScreen = AppScreen.ResetPassword },
                onBackClick = { currentScreen = AppScreen.ForgotPassword },
                onResendCode = { /* Resend code */ }
            )
            
            AppScreen.ResetPassword -> ResetPasswordScreen(
                onPasswordReset = { currentScreen = AppScreen.Login },
                onBackClick = { currentScreen = AppScreen.VerifyResetCode }
            )
            
            // ===== MAIN TABS =====
            AppScreen.Home -> HomeScreen(
                onSOSClick = { currentScreen = AppScreen.PanicMode },
                onShareLocationClick = { currentScreen = AppScreen.ShareLocation },
                onReportDangerClick = { currentScreen = AppScreen.ReportDanger },
                onStartTripClick = { currentScreen = AppScreen.StartTrip },
                onProfileClick = { currentScreen = AppScreen.Profile },
                // Bottom Nav
                onHomeClick = { },
                onMapClick = { currentScreen = AppScreen.Map },
                onCommunityClick = { currentScreen = AppScreen.Community },
                onSettingsClick = { currentScreen = AppScreen.Profile }
            )
            
            AppScreen.Map -> MapScreen(
                onHomeClick = { currentScreen = AppScreen.Home },
                onMapClick = { },
                onCommunityClick = { currentScreen = AppScreen.Community },
                onProfileClick = { currentScreen = AppScreen.Profile },
                onSOSClick = { currentScreen = AppScreen.PanicMode }
            )
            
            AppScreen.Community -> CommunityScreen(
                onHomeClick = { currentScreen = AppScreen.Home },
                onMapClick = { currentScreen = AppScreen.Map },
                onCommunityClick = { },
                onProfileClick = { currentScreen = AppScreen.Profile }
            )
            
            AppScreen.Profile -> ProfileScreen(
                onHomeClick = { currentScreen = AppScreen.Home },
                onMapClick = { currentScreen = AppScreen.Map },
                onCommunityClick = { currentScreen = AppScreen.Community },
                onProfileClick = { },
                onEditProfile = { currentScreen = AppScreen.EditProfile },
                onGuardianContacts = { currentScreen = AppScreen.GuardianContacts },
                onSettings = { currentScreen = AppScreen.Settings },
                onSafetyTips = { currentScreen = AppScreen.SafetyTips },
                onLogout = { currentScreen = AppScreen.Login }
            )
            
            // ===== SAFETY FEATURES =====
            AppScreen.PanicMode -> PanicModeScreen(
                onCallPolice = { /* Call police */ },
                onSendAlert = { /* Send alert to contacts */ },
                onCancel = { currentScreen = AppScreen.Home }
            )
            
            AppScreen.StartTrip -> StartTripScreen(
                onBackClick = { currentScreen = AppScreen.Home },
                onStartTrip = { currentScreen = AppScreen.TripInProgress },
                onHomeClick = { currentScreen = AppScreen.Home },
                onMapClick = { currentScreen = AppScreen.Map },
                onCommunityClick = { currentScreen = AppScreen.Community },
                onProfileClick = { currentScreen = AppScreen.Profile }
            )

            AppScreen.TripInProgress -> TripInProgressScreen(
                destination = "123 Safe St, Secure City",
                onBackClick = { currentScreen = AppScreen.Home },
                onEndTrip = { currentScreen = AppScreen.Home }
            )
            
            AppScreen.ShareLocation -> ShareLocationScreen(
                onBackClick = { currentScreen = AppScreen.Home },
                onShareLocation = { /* Share location with selected contacts */ }
            )
            
            AppScreen.ReportDanger -> ReportDangerScreen(
                onBackClick = { currentScreen = AppScreen.Home },
                onSubmit = { currentScreen = AppScreen.Home }
            )
            
            // ===== CONTACTS =====
            AppScreen.GuardianContacts -> GuardianContactsScreen(
                onBackClick = { currentScreen = AppScreen.Profile },
                onAddContact = { currentScreen = AppScreen.AddContact }
            )
            
            AppScreen.AddContact -> AddContactScreen(
                onBackClick = { currentScreen = AppScreen.GuardianContacts },
                onSaveContact = { currentScreen = AppScreen.GuardianContacts }
            )
            
            // ===== PROFILE & SETTINGS =====
            AppScreen.EditProfile -> EditProfileScreen(
                onBackClick = { currentScreen = AppScreen.Profile },
                onSaveProfile = { currentScreen = AppScreen.Profile }
            )
            
            AppScreen.Settings -> SettingsScreen(
                onBackClick = { currentScreen = AppScreen.Profile },
                onLogout = { currentScreen = AppScreen.Login }
            )
            
            AppScreen.NotificationSettings -> NotificationSettingsScreen(
                onBackClick = { currentScreen = AppScreen.Settings }
            )
            
            AppScreen.SafetyTips -> SafetyTipsScreen(
                onBackClick = { currentScreen = AppScreen.Profile }
            )
        }
    }
}
