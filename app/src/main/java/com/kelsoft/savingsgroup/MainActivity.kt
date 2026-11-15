package com.kelsoft.savingsgroup

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.ContentPaste
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.OpenInNew
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material.icons.filled.Savings
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.kelsoft.savingsgroup.ui.theme.SavingsGroupTheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.tasks.await
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

// ---------- Route names ----------
object Routes {
    const val WELCOME = "welcome"
    const val LOGIN = "login"
    const val REGISTER = "register"
    const val MAIN = "main"
    const val MAKE_DEPOSIT = "make_deposit"
}

// ---------- Shared theme colors ----------
private val GoldTitle = Color(0xFFF3D37A)
private val GoldSoft = Color(0xFFFFD54F)
private val CreamText = Color(0xFFECE0B8)
private val BlueLink = Color(0xFF4FA0FF)
private val ErrorRed = Color(0xFFFF6B6B)
private val SuccessGreen = Color(0xFF8BC34A)
private val PendingAmber = Color(0xFFFFC107)
private val DarkNavyOverlay = Color(0xDD0D0821)

// ---------- App-level Firebase singletons ----------
private val auth: FirebaseAuth get() = Firebase.auth
private val db: FirebaseFirestore get() = Firebase.firestore

// ---------- Simple in-memory session ----------
object Session {
    var currentUser: FirebaseUser? = null
    var currentProfile: UserProfile? = null
}

data class UserProfile(
    val uid: String = "",
    val fullName: String = "",
    val phone: String = "",
    val email: String = "",
    val role: String = "member",
    val joinedAt: Long = 0L,
    val status: String = "active",
    val totalSavings: Long = 0L,
)

data class FireDeposit(
    val id: String = "",
    val userId: String = "",
    val memberName: String = "",
    val amount: Long = 0L,
    val method: String = "",
    val network: String = "",
    val phone: String = "",
    val reference: String = "",
    val bankName: String = "",
    val contributionFor: String = "",
    val status: String = "pending",
    val note: String = "",
    val balanceAfter: Long = 0L,
    val createdAt: Long = 0L,
)

data class FirePenalty(
    val id: String = "",
    val userId: String = "",
    val memberName: String = "",
    val amount: Long = 0L,
    val reason: String = "",
    val status: String = "unpaid",   // "unpaid" | "cleared"
    val createdAt: Long = 0L,
    val clearedAt: Long = 0L,
)

data class FireMessage(
    val id: String = "",
    val senderId: String = "",
    val senderName: String = "",
    val text: String = "",
    val createdAt: Long = 0L,
)

data class FireMeeting(
    val id: String = "",
    val title: String = "",
    val date: Long = 0L,
    val durationMinutes: Int = 120,
    val location: String = "",
    val agenda: String = "",
    val minutes: String = "",
    val status: String = "scheduled",
    val createdBy: String = "",
    val createdByName: String = "",
    val createdAt: Long = 0L,
    val attendance: Map<String, String> = emptyMap(),
)

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            SavingsGroupTheme {
                SavingsGroupApp()
            }
        }
    }
}

@Composable
fun SavingsGroupApp() {
    val navController = rememberNavController()

    Surface(modifier = Modifier.fillMaxSize()) {
        NavHost(
            navController = navController,
            startDestination = Routes.WELCOME,
        ) {
            composable(Routes.WELCOME) {
                WelcomeScreen(
                    onSignInClick = { navController.navigate(Routes.LOGIN) },
                    onCreateAccountClick = { navController.navigate(Routes.REGISTER) },
                )
            }
            composable(Routes.LOGIN) {
                LoginScreen(
                    onBackClick = { navController.popBackStack() },
                    onSignInSuccess = {
                        navController.navigate(Routes.MAIN) {
                            popUpTo(Routes.WELCOME) { inclusive = true }
                        }
                    },
                    onRegisterClick = { navController.navigate(Routes.REGISTER) },
                )
            }
            composable(Routes.REGISTER) {
                RegisterScreen(
                    onBackClick = { navController.popBackStack() },
                    onRegisterSuccess = {
                        navController.navigate(Routes.MAIN) {
                            popUpTo(Routes.WELCOME) { inclusive = true }
                        }
                    },
                    onSignInClick = { navController.navigate(Routes.LOGIN) },
                )
            }
            composable(Routes.MAIN) {
                MainContainerScreen(
                    onSignOutClick = {
                        auth.signOut()
                        Session.currentUser = null
                        Session.currentProfile = null
                        navController.navigate(Routes.WELCOME) {
                            popUpTo(Routes.WELCOME) { inclusive = true }
                        }
                    },
                    onMakeDepositClick = { navController.navigate(Routes.MAKE_DEPOSIT) },
                )
            }
            composable(Routes.MAKE_DEPOSIT) {
                MakeDepositFlow(
                    onBackClick = { navController.popBackStack() },
                    onDoneClick = {
                        navController.navigate(Routes.MAIN) {
                            popUpTo(Routes.MAIN) { inclusive = true }
                        }
                    },
                )
            }
        }
    }
}

// =====================================================
//                   WELCOME SCREEN
// =====================================================
@Composable
fun WelcomeScreen(
    onSignInClick: () -> Unit,
    onCreateAccountClick: () -> Unit,
) {
    val backgrounds = listOf(
        R.drawable.download1,
        R.drawable.download2,
        R.drawable.download3,
        R.drawable.download4,
        R.drawable.download5,
    )

    var currentIndex by remember { mutableStateOf(0) }

    LaunchedEffect(Unit) {
        while (true) {
            delay(6000)
            currentIndex = (currentIndex + 1) % backgrounds.size
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        backgrounds.forEachIndexed { index, resId ->
            val alpha by animateFloatAsState(
                targetValue = if (index == currentIndex) 1f else 0f,
                animationSpec = tween(durationMillis = 1200),
                label = "bg_alpha_$index",
            )
            Image(
                painter = painterResource(id = resId),
                contentDescription = null,
                modifier = Modifier.fillMaxSize().alpha(alpha),
                contentScale = ContentScale.Crop,
            )
        }

        val logoGlow = Brush.radialGradient(
            colors = listOf(Color(0x3ECCA64E), Color(0x1899721D), Color(0x00000000)),
            radius = 650f,
        )
        Box(modifier = Modifier.fillMaxSize().background(logoGlow))
        ConstellationBackground(modifier = Modifier.fillMaxSize())
        BottomArcGlow(modifier = Modifier.fillMaxSize())

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 28.dp, vertical = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween,
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Image(
                    painter = painterResource(id = R.drawable.logo),
                    contentDescription = "Savings Group Logo",
                    modifier = Modifier.size(240.dp),
                    contentScale = ContentScale.Fit,
                )
                Spacer(modifier = Modifier.height(24.dp))
                Text(
                    text = "CITY ALUMNI SAVING\nGROUP",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Serif,
                    color = GoldTitle,
                    textAlign = TextAlign.Center,
                    lineHeight = 30.sp,
                    letterSpacing = 1.2.sp,
                    style = TextStyle(
                        shadow = Shadow(Color(0xCC000000), Offset(2f, 2f), 8f)
                    ),
                )
                Spacer(modifier = Modifier.height(18.dp))
                Text(
                    text = "Small Coins, Big Dreams",
                    fontSize = 24.sp,
                    fontStyle = FontStyle.Italic,
                    fontFamily = FontFamily.Cursive,
                    color = GoldSoft,
                    textAlign = TextAlign.Center,
                    letterSpacing = 0.5.sp,
                    style = TextStyle(
                        shadow = Shadow(Color(0xFF000000), Offset(2f, 2f), 8f)
                    ),
                )
            }

            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                GradientBlueButton(text = "SIGN IN", onClick = onSignInClick)
                Spacer(modifier = Modifier.height(14.dp))
                GlassButton(text = "CREATE ACCOUNT", onClick = onCreateAccountClick)
                Spacer(modifier = Modifier.height(28.dp))

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(Color(0x99000000))
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                        .clickable { },
                ) {
                    Text(
                        text = "Terms of Service",
                        fontSize = 14.sp,
                        color = Color(0xFFFFE082),
                        textDecoration = TextDecoration.Underline,
                        textAlign = TextAlign.Center,
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}

// =====================================================
//                    LOGIN SCREEN
// =====================================================
@Composable
fun LoginScreen(
    onBackClick: () -> Unit,
    onSignInSuccess: () -> Unit,
    onRegisterClick: () -> Unit,
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var isLoading by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxSize()) {
        Image(
            painter = painterResource(id = R.drawable.download2),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop,
        )
        Box(modifier = Modifier.fillMaxSize().background(Color(0xCC0D0821)))

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
        ) {
            IconButton(onClick = onBackClick, modifier = Modifier.padding(top = 8.dp)) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = Color.White,
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
            Image(
                painter = painterResource(id = R.drawable.logo),
                contentDescription = "Logo",
                modifier = Modifier.size(100.dp).align(Alignment.CenterHorizontally),
                contentScale = ContentScale.Fit,
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Welcome Back",
                fontSize = 26.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Serif,
                color = GoldTitle,
                modifier = Modifier.align(Alignment.CenterHorizontally),
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = "Sign in to continue",
                fontSize = 15.sp,
                color = CreamText,
                modifier = Modifier.align(Alignment.CenterHorizontally),
            )
            Spacer(modifier = Modifier.height(36.dp))

            CosmicTextField(
                value = email,
                onValueChange = { email = it; errorMessage = null },
                label = "Email",
                keyboardType = KeyboardType.Email,
                imeAction = ImeAction.Next,
            )
            Spacer(modifier = Modifier.height(16.dp))
            CosmicTextField(
                value = password,
                onValueChange = { password = it; errorMessage = null },
                label = "Password",
                keyboardType = KeyboardType.Password,
                imeAction = ImeAction.Done,
                isPassword = true,
                passwordVisible = passwordVisible,
                onTogglePassword = { passwordVisible = !passwordVisible },
            )
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = "Forgot password?",
                color = BlueLink,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.align(Alignment.End).clickable { },
            )

            if (errorMessage != null) {
                Spacer(modifier = Modifier.height(12.dp))
                Text(text = errorMessage!!, color = ErrorRed, fontSize = 14.sp)
            }

            Spacer(modifier = Modifier.height(28.dp))

            if (isLoading) {
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center,
                ) {
                    CircularProgressIndicator(color = GoldSoft)
                }
            } else {
                GradientBlueButton(
                    text = "SIGN IN",
                    onClick = {
                        when {
                            email.isBlank() -> errorMessage = "Please enter your email"
                            password.isBlank() -> errorMessage = "Please enter your password"
                            password.length < 6 -> errorMessage = "Password must be at least 6 characters"
                            else -> {
                                errorMessage = null
                                isLoading = true
                                // We'll trigger via a launcher effect below
                            }
                        }
                    },
                )
            }

            // Firebase sign-in effect — runs when isLoading becomes true
            if (isLoading) {
                LaunchedEffect(Unit) {
                    try {
                        val result = auth.signInWithEmailAndPassword(email.trim(), password).await()
                        val user = result.user
                        if (user != null) {
                            Session.currentUser = user
                            val doc = db.collection("users").document(user.uid).get().await()
                            val profile = doc.toObject(UserProfile::class.java)
                            Session.currentProfile = profile
                        }
                        isLoading = false
                        errorMessage = null
                        onSignInSuccess()
                    } catch (e: Exception) {
                        isLoading = false
                        errorMessage = friendlyAuthError(e.message)
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
            Row(
                modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp),
                horizontalArrangement = Arrangement.Center,
            ) {
                Text(text = "Don't have an account? ", color = Color(0xCCFFFFFF), fontSize = 14.sp)
                Text(
                    text = "Register",
                    color = BlueLink,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.clickable { onRegisterClick() },
                )
            }
        }
    }
}

// =====================================================
//                   REGISTER SCREEN
// =====================================================
@Composable
fun RegisterScreen(
    onBackClick: () -> Unit,
    onRegisterSuccess: () -> Unit,
    onSignInClick: () -> Unit,
) {
    var fullName by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var inviteCode by remember { mutableStateOf("") }

    var passwordVisible by remember { mutableStateOf(false) }
    var confirmVisible by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var isLoading by remember { mutableStateOf(false) }

    val VALID_INVITE_CODE = "CITY2026"

    Box(modifier = Modifier.fillMaxSize()) {
        Image(
            painter = painterResource(id = R.drawable.download4),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop,
        )
        Box(modifier = Modifier.fillMaxSize().background(DarkNavyOverlay))

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
        ) {
            IconButton(onClick = onBackClick, modifier = Modifier.padding(top = 8.dp)) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = Color.White,
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Image(
                painter = painterResource(id = R.drawable.logo),
                contentDescription = "Logo",
                modifier = Modifier.size(90.dp).align(Alignment.CenterHorizontally),
                contentScale = ContentScale.Fit,
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = "Create Account",
                fontSize = 26.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Serif,
                color = GoldTitle,
                modifier = Modifier.align(Alignment.CenterHorizontally),
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = "Join the savings group",
                fontSize = 15.sp,
                color = CreamText,
                modifier = Modifier.align(Alignment.CenterHorizontally),
            )
            Spacer(modifier = Modifier.height(28.dp))

            CosmicTextField(
                value = fullName,
                onValueChange = { fullName = it; errorMessage = null },
                label = "Full Name",
                keyboardType = KeyboardType.Text,
                imeAction = ImeAction.Next,
            )
            Spacer(modifier = Modifier.height(14.dp))
            CosmicTextField(
                value = phone,
                onValueChange = { phone = it; errorMessage = null },
                label = "Phone Number",
                keyboardType = KeyboardType.Phone,
                imeAction = ImeAction.Next,
            )
            Spacer(modifier = Modifier.height(14.dp))
            CosmicTextField(
                value = email,
                onValueChange = { email = it; errorMessage = null },
                label = "Email",
                keyboardType = KeyboardType.Email,
                imeAction = ImeAction.Next,
            )
            Spacer(modifier = Modifier.height(14.dp))
            CosmicTextField(
                value = password,
                onValueChange = { password = it; errorMessage = null },
                label = "Password",
                keyboardType = KeyboardType.Password,
                imeAction = ImeAction.Next,
                isPassword = true,
                passwordVisible = passwordVisible,
                onTogglePassword = { passwordVisible = !passwordVisible },
            )
            Spacer(modifier = Modifier.height(14.dp))
            CosmicTextField(
                value = confirmPassword,
                onValueChange = { confirmPassword = it; errorMessage = null },
                label = "Confirm Password",
                keyboardType = KeyboardType.Password,
                imeAction = ImeAction.Next,
                isPassword = true,
                passwordVisible = confirmVisible,
                onTogglePassword = { confirmVisible = !confirmVisible },
            )
            Spacer(modifier = Modifier.height(14.dp))
            CosmicTextField(
                value = inviteCode,
                onValueChange = { inviteCode = it; errorMessage = null },
                label = "Invitation Code",
                keyboardType = KeyboardType.Text,
                imeAction = ImeAction.Done,
            )

            if (errorMessage != null) {
                Spacer(modifier = Modifier.height(12.dp))
                Text(text = errorMessage!!, color = ErrorRed, fontSize = 14.sp)
            }

            Spacer(modifier = Modifier.height(24.dp))

            if (isLoading) {
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center,
                ) {
                    CircularProgressIndicator(color = GoldSoft)
                }
            } else {
                GradientBlueButton(
                    text = "CREATE ACCOUNT",
                    onClick = {
                        when {
                            fullName.isBlank() -> errorMessage = "Please enter your full name"
                            phone.isBlank() -> errorMessage = "Please enter your phone number"
                            phone.length < 9 -> errorMessage = "Please enter a valid phone number"
                            email.isBlank() -> errorMessage = "Please enter your email"
                            !email.contains("@") -> errorMessage = "Please enter a valid email"
                            password.isBlank() -> errorMessage = "Please enter a password"
                            password.length < 6 -> errorMessage = "Password must be at least 6 characters"
                            confirmPassword != password -> errorMessage = "Passwords do not match"
                            inviteCode.isBlank() -> errorMessage = "Please enter the invitation code"
                            inviteCode.trim().uppercase() != VALID_INVITE_CODE ->
                                errorMessage = "Invalid invitation code"
                            else -> {
                                errorMessage = null
                                isLoading = true
                            }
                        }
                    },
                )
            }

            // Firebase create-account effect
            if (isLoading) {
                LaunchedEffect(Unit) {
                    try {
                        // 1. Create the Firebase Auth user
                        val result = auth.createUserWithEmailAndPassword(
                            email.trim(),
                            password
                        ).await()
                        val user = result.user
                        if (user == null) {
                            isLoading = false
                            errorMessage = "Something went wrong. Please try again."
                            return@LaunchedEffect
                        }

                        // 2. Figure out the role. First ever user becomes admin.
                        val usersCount = db.collection("users").get().await().size()
                        val role = if (usersCount == 0) "admin" else "member"

                        // 3. Create the Firestore profile
                        val profile = UserProfile(
                            uid = user.uid,
                            fullName = fullName.trim(),
                            phone = phone.trim(),
                            email = email.trim(),
                            role = role,
                            joinedAt = System.currentTimeMillis(),
                            status = "active",
                            totalSavings = 0L,
                        )
                        db.collection("users").document(user.uid).set(profile).await()

                        Session.currentUser = user
                        Session.currentProfile = profile
                        isLoading = false
                        errorMessage = null
                        onRegisterSuccess()
                    } catch (e: Exception) {
                        isLoading = false
                        errorMessage = friendlyAuthError(e.message)
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))
            Row(
                modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp),
                horizontalArrangement = Arrangement.Center,
            ) {
                Text(text = "Already have an account? ", color = Color(0xCCFFFFFF), fontSize = 14.sp)
                Text(
                    text = "Sign In",
                    color = BlueLink,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.clickable { onSignInClick() },
                )
            }
        }
    }
}

// =====================================================
//         MAIN CONTAINER — Bottom Nav + Tab Content
// =====================================================
private enum class BottomTab(val label: String, val icon: ImageVector) {
    HOME("Home", Icons.Filled.Home),
    SAVINGS("Savings", Icons.Filled.Savings),
    PENALTIES("Penalties", Icons.Filled.Warning),
    CHAT("Chat", Icons.Filled.Chat),
    PROFILE("Profile", Icons.Filled.AccountCircle),
}

@Composable
fun MainContainerScreen(
    onSignOutClick: () -> Unit,
    onMakeDepositClick: () -> Unit,
) {
    var selectedTab by remember { mutableStateOf(BottomTab.HOME) }
    var selectedDeposit by remember { mutableStateOf<FireDeposit?>(null) }
    var showMembers by remember { mutableStateOf(false) }
    var showReports by remember { mutableStateOf(false) }
    var showMeetings by remember { mutableStateOf(false) }

    // Deposits
    var deposits by remember { mutableStateOf<List<FireDeposit>>(emptyList()) }
    var depositsLoading by remember { mutableStateOf(true) }

    // Penalties
    var penalties by remember { mutableStateOf<List<FirePenalty>>(emptyList()) }

    // Chat
    var messages by remember { mutableStateOf<List<FireMessage>>(emptyList()) }

    // Meetings
    var meetings by remember { mutableStateOf<List<FireMeeting>>(emptyList()) }

    // Deposits listener
    LaunchedEffect(Unit) {
        db.collection("deposits")
            .orderBy("createdAt", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    depositsLoading = false
                    return@addSnapshotListener
                }
                if (snapshot != null) {
                    deposits = snapshot.documents.mapNotNull { d ->
                        d.toObject(FireDeposit::class.java)?.copy(id = d.id)
                    }
                    depositsLoading = false
                }
            }
    }

    // Penalties listener
    LaunchedEffect(Unit) {
        db.collection("penalties")
            .orderBy("createdAt", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) return@addSnapshotListener
                if (snapshot != null) {
                    penalties = snapshot.documents.mapNotNull { d ->
                        d.toObject(FirePenalty::class.java)?.copy(id = d.id)
                    }
                }
            }
    }

    // Chat listener — last 200 messages, chronological order
    LaunchedEffect(Unit) {
        db.collection("chats")
            .orderBy("createdAt", Query.Direction.ASCENDING)
            .limit(200)
            .addSnapshotListener { snapshot, error ->
                if (error != null) return@addSnapshotListener
                if (snapshot != null) {
                    messages = snapshot.documents.mapNotNull { d ->
                        d.toObject(FireMessage::class.java)?.copy(id = d.id)
                    }
                }
            }
    }

    // Meetings listener
    LaunchedEffect(Unit) {
        db.collection("meetings")
            .orderBy("date", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) return@addSnapshotListener
                if (snapshot != null) {
                    meetings = snapshot.documents.mapNotNull { d ->
                        d.toObject(FireMeeting::class.java)?.copy(id = d.id)
                    }
                }
            }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Image(
            painter = painterResource(id = R.drawable.download3),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop,
        )
        Box(modifier = Modifier.fillMaxSize().background(DarkNavyOverlay))

        Column(modifier = Modifier.fillMaxSize()) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ) {
                when {
                        showMeetings -> MeetingsScreen(
                            meetings = meetings,
                            myRole = Session.currentProfile?.role ?: "member",
                            myUid = Session.currentUser?.uid ?: "",
                            onBack = { showMeetings = false },
                        )
                        showReports -> ReportsScreen(
                        deposits = deposits,
                        penalties = penalties,
                        onBack = { showReports = false },
                    )
                    showMembers -> MembersScreen(
                        deposits = deposits,
                        onBack = { showMembers = false },
                    )
                    selectedDeposit != null -> DepositDetailScreen(
                        deposit = selectedDeposit!!,
                        onBack = { selectedDeposit = null },
                    )
                    else -> when (selectedTab) {
                        BottomTab.HOME -> HomeTab(
                            onMakeDepositClick = onMakeDepositClick,
                            onMembersClick = { showMembers = true },
                            onReportsClick = { showReports = true },
                            onMeetingsClick = { showMeetings = true },
                            deposits = deposits,
                            currentUserName = Session.currentProfile?.fullName?.split(" ")?.firstOrNull() ?: "Member",
                        )
                        BottomTab.SAVINGS -> SavingsTab(
                            deposits = deposits,
                            isLoading = depositsLoading,
                            onDepositClick = { selectedDeposit = it },
                        )
                        BottomTab.PENALTIES -> PenaltiesTab(
                            penalties = penalties,
                            myRole = Session.currentProfile?.role ?: "member",
                            myName = Session.currentProfile?.fullName ?: "Member",
                        )
                        BottomTab.CHAT -> ChatTab(
                            messages = messages,
                            myId = Session.currentUser?.uid ?: "",
                            myName = Session.currentProfile?.fullName ?: "Member",
                        )
                        BottomTab.PROFILE -> ProfileTab(
                            onSignOutClick = onSignOutClick,
                            name = Session.currentProfile?.fullName ?: "Member",
                            role = Session.currentProfile?.role ?: "member",
                        )
                    }
                }
            }

            if (selectedDeposit == null && !showMembers && !showReports && !showMeetings) {
                BottomNavBar(
                    selectedTab = selectedTab,
                    onTabSelected = { selectedTab = it },
                )
            }
        }
    }
}

// =====================================================
//                       HOME TAB
// =====================================================
@Composable
private fun HomeTab(
    onMakeDepositClick: () -> Unit,
    onMembersClick: () -> Unit,
    onReportsClick: () -> Unit,
    onMeetingsClick: () -> Unit,
    deposits: List<FireDeposit>,
    currentUserName: String,
) {
    val confirmed = deposits.filter { it.status == "confirmed" }
    val totalBalance = confirmed.sumOf { it.amount }
    val thisMonth = confirmed.filter {
        val cal = Calendar.getInstance().apply { timeInMillis = it.createdAt }
        val now = Calendar.getInstance()
        cal.get(Calendar.MONTH) == now.get(Calendar.MONTH) &&
            cal.get(Calendar.YEAR) == now.get(Calendar.YEAR)
    }.sumOf { it.amount }
    val pendingCount = deposits.count { it.status == "pending" }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 16.dp),
    ) {
        Text(
            text = "${timeGreeting()}, $currentUserName 👋",
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White,
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = "CITY ALUMNI SAVING GROUP",
            fontSize = 13.sp,
            color = GoldTitle,
            letterSpacing = 1.sp,
        )

        Spacer(modifier = Modifier.height(20.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(20.dp))
                .background(
                    Brush.linearGradient(
                        colors = listOf(
                            Color(0xFF3A2A0A),
                            Color(0xFF6B4E10),
                            Color(0xFF3A2A0A),
                        )
                    )
                )
                .border(
                    width = 1.5.dp,
                    brush = Brush.linearGradient(
                        listOf(Color(0xFFFFE082), Color(0xFFB8860B), Color(0xFFFFE082))
                    ),
                    shape = RoundedCornerShape(20.dp),
                )
                .padding(20.dp),
        ) {
            Column {
                Text(
                    text = "💰 GROUP BALANCE",
                    fontSize = 13.sp,
                    color = GoldTitle,
                    letterSpacing = 1.2.sp,
                    fontWeight = FontWeight.Bold,
                )
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = "UGX ${formatMoney(totalBalance)}",
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Serif,
                    color = Color.White,
                    style = TextStyle(
                        shadow = Shadow(Color(0xAA000000), Offset(2f, 2f), 6f)
                    ),
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "↑ UGX ${formatMoney(thisMonth)} this month",
                    fontSize = 13.sp,
                    color = SuccessGreen,
                )
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            StatCard(Modifier.weight(1f), "👥", "Members", "—")
            StatCard(Modifier.weight(1f), "💵", "Savings", formatMoney(totalBalance))
        }
        Spacer(modifier = Modifier.height(12.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            StatCard(Modifier.weight(1f), "💳", "Loans", "0")
            StatCard(Modifier.weight(1f), "⚠️", "Pending", pendingCount.toString())
        }

        Spacer(modifier = Modifier.height(18.dp))

        // MEMBERS tile
        HomeTile(
            emoji = "👥",
            title = "MEMBERS",
            subtitle = "View all group members",
            onClick = onMembersClick,
        )

        Spacer(modifier = Modifier.height(12.dp))

        // REPORTS tile
        HomeTile(
            emoji = "📊",
            title = "REPORTS",
            subtitle = "Monthly summary & contributions",
            onClick = onReportsClick,
        )

        Spacer(modifier = Modifier.height(12.dp))

        // MEETINGS tile
        HomeTile(
            emoji = "📅",
            title = "MEETINGS",
            subtitle = "Schedule, agenda & attendance",
            onClick = onMeetingsClick,
        )

        Spacer(modifier = Modifier.height(18.dp))

        GradientBlueButton(text = "+  MAKE DEPOSIT", onClick = onMakeDepositClick)

        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
private fun HomeTile(
    emoji: String,
    title: String,
    subtitle: String,
    onClick: () -> Unit,
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(Color(0x33FFFFFF))
            .border(1.dp, Color(0x66FFD54F), RoundedCornerShape(16.dp))
            .clickable { onClick() }
            .padding(18.dp),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(Color(0x55FFD54F)),
                contentAlignment = Alignment.Center,
            ) {
                Text(text = emoji, fontSize = 24.sp)
            }
            Spacer(modifier = Modifier.width(14.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    fontSize = 12.sp,
                    color = GoldTitle,
                    letterSpacing = 1.sp,
                    fontWeight = FontWeight.Bold,
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = subtitle,
                    fontSize = 14.sp,
                    color = Color.White,
                    fontWeight = FontWeight.Medium,
                )
            }
            Text(
                text = "→",
                fontSize = 22.sp,
                color = GoldSoft,
                fontWeight = FontWeight.Bold,
            )
        }
    }
}

@Composable
private fun StatCard(
    modifier: Modifier = Modifier,
    emoji: String,
    label: String,
    value: String,
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(Color(0x33FFFFFF))
            .border(
                width = 1.dp,
                color = Color(0x44FFD54F),
                shape = RoundedCornerShape(16.dp),
            )
            .padding(16.dp),
    ) {
        Column {
            Text(text = emoji, fontSize = 22.sp)
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = label,
                fontSize = 12.sp,
                color = CreamText,
                letterSpacing = 0.8.sp,
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = value,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
            )
        }
    }
}

// =====================================================
//               PLACEHOLDER TABS
// =====================================================
@Composable
private fun PlaceholderTab(title: String, subtitle: String) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center,
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = title,
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Serif,
                color = GoldTitle,
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = subtitle,
                fontSize = 15.sp,
                color = CreamText,
            )
        }
    }
}

// =====================================================
//                     PROFILE TAB
// =====================================================
@Composable
private fun ProfileTab(
    onSignOutClick: () -> Unit,
    name: String,
    role: String,
) {
    var showSeedDialog by remember { mutableStateOf(false) }
    var seedStatus by remember { mutableStateOf<String?>(null) }
    var isSeeding by remember { mutableStateOf(false) }

    // Enabled for all roles so you can easily seed demo data
    val isAdmin = true

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Spacer(modifier = Modifier.height(24.dp))
        Image(
            painter = painterResource(id = R.drawable.logo),
            contentDescription = "Logo",
            modifier = Modifier.size(120.dp),
            contentScale = ContentScale.Fit,
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = name,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = FontFamily.Serif,
            color = GoldTitle,
            textAlign = TextAlign.Center,
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = "Member since 2026",
            fontSize = 14.sp,
            color = CreamText,
        )
        Spacer(modifier = Modifier.height(8.dp))
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(20.dp))
                .background(Color(0x33FFD54F))
                .padding(horizontal = 14.dp, vertical = 6.dp),
        ) {
            Text(
                text = "⭐ ${role.replaceFirstChar { it.uppercase() }}",
                fontSize = 12.sp,
                color = GoldSoft,
                fontWeight = FontWeight.Bold,
            )
        }

        // ----- Admin-only seed section -----
        if (isAdmin && false) { // Toggle to true if you ever need to re-seed.
            Spacer(modifier = Modifier.height(32.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(Color(0x22FF6B6B))
                    .border(1.dp, Color(0x66FF6B6B), RoundedCornerShape(14.dp))
                    .padding(16.dp),
            ) {
                Column {
                    Text(
                        text = "🔧 ADMIN TOOLS",
                        fontSize = 12.sp,
                        color = Color(0xFFFFAB91),
                        letterSpacing = 1.sp,
                        fontWeight = FontWeight.Bold,
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "One-time setup helpers. Use carefully.",
                        fontSize = 11.sp,
                        color = CreamText,
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    if (isSeeding) {
                        Box(
                            modifier = Modifier.fillMaxWidth(),
                            contentAlignment = Alignment.Center,
                        ) {
                            CircularProgressIndicator(color = GoldSoft)
                        }
                    } else {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(Color(0x44FF6B6B))
                                .border(1.dp, Color(0x88FF6B6B), RoundedCornerShape(10.dp))
                                .clickable { showSeedDialog = true },
                            contentAlignment = Alignment.Center,
                        ) {
                            Text(
                                text = "🌱  SEED DEMO DEPOSITS",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                                letterSpacing = 0.5.sp,
                            )
                        }
                    }

                    if (seedStatus != null) {
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = seedStatus!!,
                            fontSize = 12.sp,
                            color = GoldSoft,
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(36.dp))
        GradientBlueButton(text = "SIGN OUT", onClick = onSignOutClick)
    }

    // ----- Confirmation dialog -----
    if (showSeedDialog) {
        AlertDialog(
            onDismissRequest = { showSeedDialog = false },
            containerColor = Color(0xFF1A1228),
            titleContentColor = GoldTitle,
            textContentColor = CreamText,
            title = {
                Text(
                    text = "Seed demo deposits?",
                    fontFamily = FontFamily.Serif,
                    fontWeight = FontWeight.Bold,
                )
            },
            text = {
                Text(
                    text = "This will DELETE all existing deposits in the database " +
                        "and replace them with the 13 records from your PDF. " +
                        "Any test deposits you made will be lost.\n\n" +
                        "Continue?",
                    fontSize = 14.sp,
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        showSeedDialog = false
                        isSeeding = true
                        seedStatus = null
                    },
                ) {
                    Text(
                        text = "SEED",
                        color = Color(0xFFFF6B6B),
                        fontWeight = FontWeight.Bold,
                    )
                }
            },
            dismissButton = {
                TextButton(onClick = { showSeedDialog = false }) {
                    Text(text = "CANCEL", color = GoldSoft)
                }
            },
        )
    }

    // ----- Actually run the seed -----
    if (isSeeding) {
        LaunchedEffect(Unit) {
            seedStatus = try {
                seedDeposits()
                isSeeding = false
                "✅ Successfully seeded 13 deposits."
            } catch (e: Exception) {
                isSeeding = false
                "❌ Failed: ${e.message}"
            }
        }
    }
}

// =====================================================
//                   BOTTOM NAV BAR
// =====================================================
@Composable
private fun BottomNavBar(
    selectedTab: BottomTab,
    onTabSelected: (BottomTab) -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xE60D0821))
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        BottomTab.values().forEach { tab ->
            val isSelected = tab == selectedTab
            val tint = if (isSelected) GoldSoft else Color(0x99FFFFFF)

            Column(
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .clickable { onTabSelected(tab) }
                    .padding(horizontal = 10.dp, vertical = 6.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                if (isSelected) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(Color(0x33FFD54F)),
                        contentAlignment = Alignment.Center,
                    ) {
                        Icon(
                            imageVector = tab.icon,
                            contentDescription = tab.label,
                            tint = tint,
                            modifier = Modifier.size(20.dp),
                        )
                    }
                } else {
                    Box(
                        modifier = Modifier.size(36.dp),
                        contentAlignment = Alignment.Center,
                    ) {
                        Icon(
                            imageVector = tab.icon,
                            contentDescription = tab.label,
                            tint = tint,
                            modifier = Modifier.size(20.dp),
                        )
                    }
                }
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = tab.label,
                    fontSize = 10.sp,
                    color = tint,
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                )
            }
        }
    }
}

// =====================================================
//              MAKE DEPOSIT FLOW
// =====================================================
private enum class DepositStep { AMOUNT, DETAILS, RECEIPT }

private enum class PaymentMethod(val label: String, val icon: ImageVector, val subtitle: String) {
    MOBILE_MONEY("MOBILE MONEY", Icons.Filled.PhoneAndroid, "MTN or Airtel"),
    BANK("BANK DEPOSIT", Icons.Filled.AccountBalance, "Bank transfer"),
}

@Composable
fun MakeDepositFlow(
    onBackClick: () -> Unit,
    onDoneClick: () -> Unit,
) {
    var step by remember { mutableStateOf(DepositStep.AMOUNT) }
    var amount by remember { mutableStateOf("") }
    var selectedMethod by remember { mutableStateOf<PaymentMethod?>(null) }
    var contributionFor by remember { mutableStateOf("Current week") }
    var network by remember { mutableStateOf("MTN") }
    var phone by remember { mutableStateOf("") }
    var reference by remember { mutableStateOf("") }
    var bankName by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var isSaving by remember { mutableStateOf(false) }
    var savedReceiptId by remember { mutableStateOf<String?>(null) }

    Box(modifier = Modifier.fillMaxSize()) {
        Image(
            painter = painterResource(id = R.drawable.download5),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop,
        )
        Box(modifier = Modifier.fillMaxSize().background(Color(0xE60D0821)))

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
        ) {
            if (step != DepositStep.RECEIPT) {
                IconButton(
                    onClick = {
                        if (step == DepositStep.AMOUNT) onBackClick()
                        else step = DepositStep.AMOUNT
                    },
                    modifier = Modifier.padding(top = 8.dp),
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = Color.White,
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            when (step) {
                DepositStep.AMOUNT -> AmountStep(
                    amount = amount,
                    onAmountChange = { amount = it.filter { c -> c.isDigit() }; errorMessage = null },
                    selectedMethod = selectedMethod,
                    onMethodSelected = { selectedMethod = it; errorMessage = null },
                    contributionFor = contributionFor,
                    onContributionChange = { contributionFor = it },
                    errorMessage = errorMessage,
                    onContinue = {
                        val amt = amount.toIntOrNull() ?: 0
                        when {
                            amount.isBlank() -> errorMessage = "Please enter an amount"
                            amt < 1000 -> errorMessage = "Minimum deposit is UGX 1,000"
                            selectedMethod == null -> errorMessage = "Please choose a payment method"
                            else -> {
                                errorMessage = null
                                step = DepositStep.DETAILS
                            }
                        }
                    },
                )

                DepositStep.DETAILS -> DetailsStep(
                    amount = amount.toIntOrNull() ?: 0,
                    method = selectedMethod!!,
                    network = network,
                    onNetworkChange = { network = it },
                    phone = phone,
                    onPhoneChange = { phone = it.filter { c -> c.isDigit() }; errorMessage = null },
                    reference = reference,
                    onReferenceChange = { reference = it; errorMessage = null },
                    bankName = bankName,
                    onBankNameChange = { bankName = it; errorMessage = null },
                    contributionFor = contributionFor,
                    errorMessage = errorMessage,
                    isSaving = isSaving,
                    onSubmit = {
                        val amt = amount.toIntOrNull() ?: 0
                        when (selectedMethod) {
                            PaymentMethod.MOBILE_MONEY -> {
                                when {
                                    phone.isBlank() || phone.length < 9 ->
                                        errorMessage = "Please enter a valid phone number"
                                    reference.isBlank() ->
                                        errorMessage = "Please enter the transaction reference"
                                    else -> {
                                        errorMessage = null
                                        isSaving = true
                                    }
                                }
                            }
                            PaymentMethod.BANK -> {
                                when {
                                    bankName.isBlank() ->
                                        errorMessage = "Please enter the bank name"
                                    reference.isBlank() ->
                                        errorMessage = "Please enter the deposit reference"
                                    else -> {
                                        errorMessage = null
                                        isSaving = true
                                    }
                                }
                            }
                            null -> errorMessage = "Please go back and choose a method"
                        }
                    },
                )

                DepositStep.RECEIPT -> ReceiptStep(
                    amount = amount.toIntOrNull() ?: 0,
                    method = selectedMethod!!,
                    network = network,
                    phone = phone,
                    reference = reference,
                    bankName = bankName,
                    contributionFor = contributionFor,
                    receiptId = savedReceiptId ?: "SG-UNKNOWN",
                    onDoneClick = onDoneClick,
                )
            }

            // Firebase save effect — runs when isSaving becomes true
            if (isSaving) {
                LaunchedEffect(Unit) {
                    try {
                        val amt = amount.toIntOrNull()?.toLong() ?: 0L
                        val user = Session.currentUser
                        val profile = Session.currentProfile

                        // Calculate current confirmed balance BEFORE this deposit
                        val snapshot = db.collection("deposits")
                            .whereEqualTo("status", "confirmed")
                            .get().await()
                        val currentBalance = snapshot.documents.sumOf { doc ->
                            (doc.getLong("amount") ?: 0L)
                        }

                        val deposit = FireDeposit(
                            userId = user?.uid ?: "unknown",
                            memberName = profile?.fullName ?: "Unknown",
                            amount = amt,
                            method = if (selectedMethod == PaymentMethod.MOBILE_MONEY) "Mobile Money" else "Bank",
                            network = if (selectedMethod == PaymentMethod.MOBILE_MONEY) network else "",
                            phone = if (selectedMethod == PaymentMethod.MOBILE_MONEY) phone else "",
                            reference = reference,
                            bankName = if (selectedMethod == PaymentMethod.BANK) bankName else "",
                            contributionFor = contributionFor,
                            status = "pending",
                            note = "",
                            balanceAfter = currentBalance + amt,
                            createdAt = System.currentTimeMillis(),
                        )

                        val ref = db.collection("deposits").add(deposit).await()
                        savedReceiptId = ref.id
                        isSaving = false
                        step = DepositStep.RECEIPT
                    } catch (e: Exception) {
                        isSaving = false
                        errorMessage = "Could not save: ${e.message}"
                    }
                }
            }
        }
    }
}

@Composable
private fun AmountStep(
    amount: String,
    onAmountChange: (String) -> Unit,
    selectedMethod: PaymentMethod?,
    onMethodSelected: (PaymentMethod) -> Unit,
    contributionFor: String,
    onContributionChange: (String) -> Unit,
    errorMessage: String?,
    onContinue: () -> Unit,
) {
    Text(
        text = "MAKE A DEPOSIT",
        fontSize = 22.sp,
        fontWeight = FontWeight.Bold,
        fontFamily = FontFamily.Serif,
        color = GoldTitle,
    )
    Spacer(modifier = Modifier.height(20.dp))

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(Color(0x33FFFFFF))
            .border(1.dp, Color(0x66FFD54F), RoundedCornerShape(16.dp))
            .padding(20.dp),
    ) {
        Column {
            Text(
                text = "AMOUNT (UGX)",
                fontSize = 12.sp,
                color = GoldTitle,
                letterSpacing = 1.sp,
                fontWeight = FontWeight.Bold,
            )
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = amount,
                onValueChange = onAmountChange,
                singleLine = true,
                placeholder = { Text("20,000", color = Color(0x66FFFFFF), fontSize = 28.sp) },
                textStyle = TextStyle(
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Serif,
                    color = Color.White,
                ),
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(10.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = Color(0x33000000),
                    unfocusedContainerColor = Color(0x22000000),
                    focusedBorderColor = Color(0xFFFFD54F),
                    unfocusedBorderColor = Color(0x44FFFFFF),
                    cursorColor = GoldSoft,
                ),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            )
        }
    }

    Spacer(modifier = Modifier.height(14.dp))

    val quickAmounts = listOf(1000, 2000, 5000, 10000, 20000, 50000, 100000)
    Column {
        quickAmounts.chunked(4).forEach { row ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                row.forEach { amt ->
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(10.dp))
                            .background(Color(0x33FFFFFF))
                            .border(1.dp, Color(0x44FFD54F), RoundedCornerShape(10.dp))
                            .clickable { onAmountChange(amt.toString()) }
                            .padding(vertical = 10.dp),
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(
                            text = formatMoney(amt.toLong()),
                            fontSize = 13.sp,
                            color = CreamText,
                            fontWeight = FontWeight.Medium,
                        )
                    }
                }
                repeat(4 - row.size) { Spacer(modifier = Modifier.weight(1f)) }
            }
            Spacer(modifier = Modifier.height(8.dp))
        }
    }

    Spacer(modifier = Modifier.height(16.dp))

    Text(
        text = "PAYMENT METHOD",
        fontSize = 12.sp,
        color = GoldTitle,
        letterSpacing = 1.sp,
        fontWeight = FontWeight.Bold,
    )
    Spacer(modifier = Modifier.height(10.dp))

    PaymentMethod.values().forEach { method ->
        val isSelected = method == selectedMethod
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(14.dp))
                .background(if (isSelected) Color(0x44FFD54F) else Color(0x33FFFFFF))
                .border(
                    width = if (isSelected) 1.5.dp else 1.dp,
                    color = if (isSelected) GoldSoft else Color(0x44FFFFFF),
                    shape = RoundedCornerShape(14.dp),
                )
                .clickable { onMethodSelected(method) }
                .padding(16.dp),
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = method.icon,
                    contentDescription = null,
                    tint = if (isSelected) GoldSoft else CreamText,
                    modifier = Modifier.size(28.dp),
                )
                Spacer(modifier = Modifier.width(14.dp))
                Column {
                    Text(
                        text = method.label,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        letterSpacing = 0.5.sp,
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = method.subtitle,
                        fontSize = 12.sp,
                        color = CreamText,
                    )
                }
            }
        }
        Spacer(modifier = Modifier.height(10.dp))
    }

    Spacer(modifier = Modifier.height(10.dp))

    Text(
        text = "CONTRIBUTION FOR",
        fontSize = 12.sp,
        color = GoldTitle,
        letterSpacing = 1.sp,
        fontWeight = FontWeight.Bold,
    )
    Spacer(modifier = Modifier.height(10.dp))

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        listOf("Current week", "Future weeks").forEach { option ->
            val isSelected = contributionFor == option
            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(12.dp))
                    .background(if (isSelected) Color(0x44FFD54F) else Color(0x33FFFFFF))
                    .border(
                        width = if (isSelected) 1.5.dp else 1.dp,
                        color = if (isSelected) GoldSoft else Color(0x44FFFFFF),
                        shape = RoundedCornerShape(12.dp),
                    )
                    .clickable { onContributionChange(option) }
                    .padding(vertical = 14.dp),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = option,
                    fontSize = 13.sp,
                    color = if (isSelected) GoldSoft else CreamText,
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                )
            }
        }
    }

    if (errorMessage != null) {
        Spacer(modifier = Modifier.height(16.dp))
        Text(text = errorMessage, color = ErrorRed, fontSize = 14.sp)
    }

    Spacer(modifier = Modifier.height(28.dp))

    GradientBlueButton(text = "CONTINUE", onClick = onContinue)

    Spacer(modifier = Modifier.height(20.dp))
}

@Composable
private fun DetailsStep(
    amount: Int,
    method: PaymentMethod,
    network: String,
    onNetworkChange: (String) -> Unit,
    phone: String,
    onPhoneChange: (String) -> Unit,
    reference: String,
    onReferenceChange: (String) -> Unit,
    bankName: String,
    onBankNameChange: (String) -> Unit,
    contributionFor: String,
    errorMessage: String?,
    isSaving: Boolean,
    onSubmit: () -> Unit,
) {
    val context = LocalContext.current
    val treasurerNumber = "0770123456"

    Text(
        text = when (method) {
            PaymentMethod.MOBILE_MONEY -> "PAY VIA MOBILE MONEY"
            PaymentMethod.BANK -> "BANK DEPOSIT"
        },
        fontSize = 20.sp,
        fontWeight = FontWeight.Bold,
        fontFamily = FontFamily.Serif,
        color = GoldTitle,
    )

    Spacer(modifier = Modifier.height(16.dp))

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(Color(0x33FFFFFF))
            .border(1.dp, Color(0x66FFD54F), RoundedCornerShape(16.dp))
            .padding(18.dp),
    ) {
        Column {
            Text(
                text = "AMOUNT",
                fontSize = 11.sp,
                color = GoldTitle,
                letterSpacing = 1.sp,
                fontWeight = FontWeight.Bold,
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "UGX ${formatMoney(amount.toLong())}",
                fontSize = 26.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Serif,
                color = Color.White,
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = "For: $contributionFor",
                fontSize = 13.sp,
                color = CreamText,
            )
        }
    }

    Spacer(modifier = Modifier.height(20.dp))

    if (method == PaymentMethod.MOBILE_MONEY) {
        Text(
            text = "NETWORK",
            fontSize = 12.sp,
            color = GoldTitle,
            letterSpacing = 1.sp,
            fontWeight = FontWeight.Bold,
        )
        Spacer(modifier = Modifier.height(10.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            listOf("MTN", "Airtel").forEach { net ->
                val isSelected = network == net
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(12.dp))
                        .background(if (isSelected) Color(0x44FFD54F) else Color(0x33FFFFFF))
                        .border(
                            width = if (isSelected) 1.5.dp else 1.dp,
                            color = if (isSelected) GoldSoft else Color(0x44FFFFFF),
                            shape = RoundedCornerShape(12.dp),
                        )
                        .clickable { onNetworkChange(net) }
                        .padding(vertical = 14.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = net,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isSelected) GoldSoft else CreamText,
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(14.dp))
                .background(Color(0x22000000))
                .border(1.dp, Color(0x44FFD54F), RoundedCornerShape(14.dp))
                .padding(16.dp),
        ) {
            Column {
                Text(
                    text = "HOW TO PAY",
                    fontSize = 12.sp,
                    color = GoldTitle,
                    letterSpacing = 1.sp,
                    fontWeight = FontWeight.Bold,
                )
                Spacer(modifier = Modifier.height(10.dp))

                InstructionStep(
                    number = "1",
                    title = "Open $network Money",
                    body = if (network == "MTN")
                        "Dial *165#  or open the MTN MoMo app"
                    else
                        "Dial *185#  or open the Airtel Money app",
                )
                InstructionStep(
                    number = "2",
                    title = "Send UGX ${formatMoney(amount.toLong())}",
                    body = "to the treasurer's number below",
                )
                InstructionStep(
                    number = "3",
                    title = "Copy the transaction ID",
                    body = "You'll receive it by SMS after payment",
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "TREASURER'S NUMBER",
            fontSize = 12.sp,
            color = GoldTitle,
            letterSpacing = 1.sp,
            fontWeight = FontWeight.Bold,
        )
        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(Color(0x33000000))
                .border(1.dp, Color(0x66FFD54F), RoundedCornerShape(12.dp))
                .padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = treasurerNumber,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Serif,
                color = Color.White,
                modifier = Modifier.weight(1f),
            )
            IconButton(
                onClick = {
                    val clipboard = context.getSystemService(android.content.Context.CLIPBOARD_SERVICE) as android.content.ClipboardManager
                    clipboard.setPrimaryClip(android.content.ClipData.newPlainText("Treasurer Number", treasurerNumber))
                },
            ) {
                Icon(
                    imageVector = Icons.Filled.ContentCopy,
                    contentDescription = "Copy number",
                    tint = GoldSoft,
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "YOUR PHONE NUMBER",
            fontSize = 12.sp,
            color = GoldTitle,
            letterSpacing = 1.sp,
            fontWeight = FontWeight.Bold,
        )
        Spacer(modifier = Modifier.height(8.dp))
        CosmicTextField(
            value = phone,
            onValueChange = onPhoneChange,
            label = "07XXXXXXXX",
            keyboardType = KeyboardType.Phone,
            imeAction = ImeAction.Next,
        )

        Spacer(modifier = Modifier.height(20.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(Color(0x33FFD54F))
                .border(1.5.dp, GoldSoft, RoundedCornerShape(12.dp))
                .clickable {
                    val url = if (network == "MTN")
                        "https://mtn.co.ug/momo"
                    else
                        "https://www.airtel.co.ug/airtel-money"
                    val intent = android.content.Intent(android.content.Intent.ACTION_VIEW, android.net.Uri.parse(url))
                    try { context.startActivity(intent) } catch (_: Exception) { }
                },
            contentAlignment = Alignment.Center,
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Filled.OpenInNew,
                    contentDescription = null,
                    tint = GoldSoft,
                    modifier = Modifier.size(20.dp),
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "OPEN $network MONEY",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = GoldSoft,
                    letterSpacing = 1.sp,
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "TRANSACTION ID / REFERENCE",
            fontSize = 12.sp,
            color = GoldTitle,
            letterSpacing = 1.sp,
            fontWeight = FontWeight.Bold,
        )
        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            OutlinedTextField(
                value = reference,
                onValueChange = onReferenceChange,
                singleLine = true,
                label = { Text("e.g. TXN123456") },
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = Color(0x33000000),
                    unfocusedContainerColor = Color(0x22000000),
                    focusedBorderColor = Color(0xFF2B78E4),
                    unfocusedBorderColor = Color(0x66FFFFFF),
                    focusedLabelColor = GoldTitle,
                    unfocusedLabelColor = Color(0xCCFFFFFF),
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    cursorColor = Color(0xFF2B78E4),
                ),
            )
            Spacer(modifier = Modifier.width(8.dp))
            IconButton(
                onClick = {
                    val clipboard = context.getSystemService(android.content.Context.CLIPBOARD_SERVICE) as android.content.ClipboardManager
                    val clip = clipboard.primaryClip
                    if (clip != null && clip.itemCount > 0) {
                        val pasted = clip.getItemAt(0).text?.toString().orEmpty()
                        if (pasted.isNotBlank()) onReferenceChange(pasted.trim())
                    }
                },
                modifier = Modifier
                    .size(52.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0x33FFD54F))
                    .border(1.dp, GoldSoft, RoundedCornerShape(12.dp)),
            ) {
                Icon(
                    imageVector = Icons.Filled.ContentPaste,
                    contentDescription = "Paste from clipboard",
                    tint = GoldSoft,
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        Text(
            text = "🟡 Your deposit will be marked as pending until the treasurer confirms it",
            fontSize = 12.sp,
            color = PendingAmber,
        )
    } else {
        Text(
            text = "BANK NAME",
            fontSize = 12.sp,
            color = GoldTitle,
            letterSpacing = 1.sp,
            fontWeight = FontWeight.Bold,
        )
        Spacer(modifier = Modifier.height(8.dp))
        CosmicTextField(
            value = bankName,
            onValueChange = onBankNameChange,
            label = "e.g. Stanbic, Centenary",
            keyboardType = KeyboardType.Text,
            imeAction = ImeAction.Next,
        )

        Spacer(modifier = Modifier.height(18.dp))

        Text(
            text = "DEPOSIT REFERENCE",
            fontSize = 12.sp,
            color = GoldTitle,
            letterSpacing = 1.sp,
            fontWeight = FontWeight.Bold,
        )
        Spacer(modifier = Modifier.height(8.dp))
        CosmicTextField(
            value = reference,
            onValueChange = onReferenceChange,
            label = "e.g. DEP-889231",
            keyboardType = KeyboardType.Text,
            imeAction = ImeAction.Done,
        )

        Spacer(modifier = Modifier.height(14.dp))

        Text(
            text = "🟡 Waiting for treasurer verification after submission",
            fontSize = 12.sp,
            color = PendingAmber,
        )
    }

    if (errorMessage != null) {
        Spacer(modifier = Modifier.height(16.dp))
        Text(text = errorMessage, color = ErrorRed, fontSize = 14.sp)
    }

    Spacer(modifier = Modifier.height(28.dp))

    if (isSaving) {
        Box(
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.Center,
        ) {
            CircularProgressIndicator(color = GoldSoft)
        }
    } else {
        GradientBlueButton(text = "SUBMIT PAYMENT", onClick = onSubmit)
    }

    Spacer(modifier = Modifier.height(20.dp))
}

@Composable
private fun InstructionStep(number: String, title: String, body: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        verticalAlignment = Alignment.Top,
    ) {
        Box(
            modifier = Modifier
                .size(24.dp)
                .clip(CircleShape)
                .background(Color(0x55FFD54F))
                .border(1.dp, GoldSoft, CircleShape),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = number,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = GoldSoft,
            )
        }
        Spacer(modifier = Modifier.width(12.dp))
        Column {
            Text(
                text = title,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = body,
                fontSize = 12.sp,
                color = CreamText,
            )
        }
    }
}

@Composable
private fun ReceiptStep(
    amount: Int,
    method: PaymentMethod,
    network: String,
    phone: String,
    reference: String,
    bankName: String,
    contributionFor: String,
    receiptId: String,
    onDoneClick: () -> Unit,
) {
    val dateText = remember { currentDateTimeText() }

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Spacer(modifier = Modifier.height(8.dp))

        Icon(
            imageVector = Icons.Filled.CheckCircle,
            contentDescription = null,
            tint = SuccessGreen,
            modifier = Modifier.size(72.dp),
        )

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = "Payment Recorded",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = FontFamily.Serif,
            color = GoldTitle,
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = "Your deposit has been submitted",
            fontSize = 14.sp,
            color = CreamText,
        )

        Spacer(modifier = Modifier.height(24.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(Color(0x33FFFFFF))
                .border(1.dp, Color(0x66FFD54F), RoundedCornerShape(16.dp))
                .padding(18.dp),
        ) {
            Column {
                Text(
                    text = "CITY ALUMNI SAVING GROUP",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = GoldTitle,
                    letterSpacing = 1.sp,
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "RECEIPT  #${receiptId.take(8).uppercase()}",
                    fontSize = 12.sp,
                    color = CreamText,
                )

                Spacer(modifier = Modifier.height(14.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(1.dp)
                        .background(Color(0x44FFD54F)),
                )
                Spacer(modifier = Modifier.height(14.dp))

                ReceiptRow("Member", Session.currentProfile?.fullName ?: "Member")
                ReceiptRow("Date", dateText)
                ReceiptRow("Amount", "UGX ${formatMoney(amount.toLong())}")
                ReceiptRow(
                    "Method",
                    if (method == PaymentMethod.MOBILE_MONEY) "📱 Mobile Money" else "🏦 Bank Deposit",
                )
                if (method == PaymentMethod.MOBILE_MONEY) {
                    ReceiptRow("Network", network)
                    ReceiptRow("Phone", phone)
                } else {
                    ReceiptRow("Bank", bankName)
                }
                ReceiptRow("Reference", reference)
                ReceiptRow("For", contributionFor)
                ReceiptRow("Status", "🟡 Pending verification")

                Spacer(modifier = Modifier.height(10.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(1.dp)
                        .background(Color(0x44FFD54F)),
                )
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = "Thank you for contributing to City Alumni Saving Group.",
                    fontSize = 11.sp,
                    color = CreamText,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth(),
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(52.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0x33FFFFFF))
                    .border(1.5.dp, Color(0x88FFFFFF), RoundedCornerShape(12.dp))
                    .clickable { },
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = "SAVE RECEIPT",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    letterSpacing = 0.5.sp,
                )
            }
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(52.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0x33FFFFFF))
                    .border(1.5.dp, Color(0x88FFFFFF), RoundedCornerShape(12.dp))
                    .clickable { },
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = "SHARE",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    letterSpacing = 0.5.sp,
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        GradientBlueButton(text = "DONE  →  HOME", onClick = onDoneClick)

        Spacer(modifier = Modifier.height(20.dp))
    }
}

@Composable
private fun ReceiptRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 5.dp),
    ) {
        Text(
            text = label,
            fontSize = 12.sp,
            color = CreamText,
            modifier = Modifier.width(90.dp),
        )
        Text(
            text = value,
            fontSize = 13.sp,
            fontWeight = FontWeight.Medium,
            color = Color.White,
        )
    }
}

// =====================================================
//                   SAVINGS TAB
// =====================================================
@Composable
private fun SavingsTab(
    deposits: List<FireDeposit>,
    isLoading: Boolean,
    onDepositClick: (FireDeposit) -> Unit,
) {
    var selectedFilter by remember { mutableStateOf("All") }
    val filters = listOf("All", "MTN", "Airtel", "Bank", "Pending")

    val filtered = when (selectedFilter) {
        "All" -> deposits
        "Pending" -> deposits.filter { it.status == "pending" }
        else -> deposits.filter { it.network == selectedFilter }
    }

    val totalSaved = deposits.filter { it.status == "confirmed" }.sumOf { it.amount }
    val pendingTotal = deposits.filter { it.status == "pending" }.sumOf { it.amount }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp, vertical = 16.dp),
    ) {
        Text(
            text = "SAVINGS HISTORY",
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = FontFamily.Serif,
            color = GoldTitle,
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = "CITY ALUMNI SAVING GROUP",
            fontSize = 12.sp,
            color = CreamText,
            letterSpacing = 1.sp,
        )

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(14.dp))
                    .background(Color(0x33FFFFFF))
                    .border(1.dp, Color(0x44FFD54F), RoundedCornerShape(14.dp))
                    .padding(14.dp),
            ) {
                Column {
                    Text(
                        text = "CONFIRMED",
                        fontSize = 11.sp,
                        color = GoldTitle,
                        letterSpacing = 1.sp,
                        fontWeight = FontWeight.Bold,
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "UGX ${formatMoney(totalSaved)}",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Serif,
                        color = Color.White,
                    )
                }
            }
            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(14.dp))
                    .background(Color(0x33FFFFFF))
                    .border(1.dp, Color(0x44FFD54F), RoundedCornerShape(14.dp))
                    .padding(14.dp),
            ) {
                Column {
                    Text(
                        text = "PENDING",
                        fontSize = 11.sp,
                        color = PendingAmber,
                        letterSpacing = 1.sp,
                        fontWeight = FontWeight.Bold,
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "UGX ${formatMoney(pendingTotal)}",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Serif,
                        color = Color.White,
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            filters.forEach { filter ->
                val isSelected = selectedFilter == filter
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(if (isSelected) Color(0x66FFD54F) else Color(0x33FFFFFF))
                        .border(
                            width = 1.dp,
                            color = if (isSelected) GoldSoft else Color(0x44FFFFFF),
                            shape = RoundedCornerShape(20.dp),
                        )
                        .clickable { selectedFilter = filter }
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                ) {
                    Text(
                        text = filter,
                        fontSize = 12.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                        color = if (isSelected) GoldSoft else CreamText,
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (isLoading) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center,
            ) {
                CircularProgressIndicator(color = GoldSoft)
            }
        } else if (filtered.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = "No deposits yet",
                    fontSize = 14.sp,
                    color = CreamText,
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                items(filtered) { deposit ->
                    FireDepositRow(deposit = deposit, onClick = { onDepositClick(deposit) })
                }
                item { Spacer(modifier = Modifier.height(16.dp)) }
            }
        }
    }
}

@Composable
private fun FireDepositRow(
    deposit: FireDeposit,
    onClick: () -> Unit,
) {
    val emoji = when {
        deposit.network == "MTN" || deposit.network == "Airtel" -> "📱"
        deposit.method == "Bank" -> "🏦"
        else -> "💵"
    }

    val isConfirmed = deposit.status == "confirmed"
    val statusLabel = if (isConfirmed) "✅ Confirmed" else "🟡 Pending"
    val statusColor = if (isConfirmed) SuccessGreen else PendingAmber

    val dateStr = formatDate(deposit.createdAt)

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(Color(0x33FFFFFF))
            .border(1.dp, Color(0x33FFD54F), RoundedCornerShape(14.dp))
            .clickable { onClick() }
            .padding(14.dp),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(Color(0x33FFD54F))
                    .border(1.dp, Color(0x66FFD54F), CircleShape),
                contentAlignment = Alignment.Center,
            ) {
                Text(text = emoji, fontSize = 20.sp)
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "${deposit.network.ifBlank { deposit.method }} Deposit",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "$dateStr · ${deposit.memberName}",
                    fontSize = 11.sp,
                    color = CreamText,
                )
            }

            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = "+UGX ${formatMoney(deposit.amount)}",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = SuccessGreen,
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = statusLabel,
                    fontSize = 10.sp,
                    color = statusColor,
                    fontWeight = FontWeight.Medium,
                )
            }
        }
    }
}

// =====================================================
//             DEPOSIT DETAIL
// =====================================================
@Composable
private fun DepositDetailScreen(
    deposit: FireDeposit,
    onBack: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(20.dp),
    ) {
        IconButton(onClick = onBack, modifier = Modifier.padding(top = 4.dp)) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Back",
                tint = Color.White,
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "DEPOSIT DETAIL",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = FontFamily.Serif,
            color = GoldTitle,
        )

        Spacer(modifier = Modifier.height(20.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(Color(0x33FFFFFF))
                .border(1.dp, Color(0x66FFD54F), RoundedCornerShape(16.dp))
                .padding(18.dp),
        ) {
            Column {
                Text(
                    text = "CITY ALUMNI SAVING GROUP",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = GoldTitle,
                    letterSpacing = 1.sp,
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "RECEIPT  #${deposit.id.take(8).uppercase()}",
                    fontSize = 12.sp,
                    color = CreamText,
                )

                Spacer(modifier = Modifier.height(14.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(1.dp)
                        .background(Color(0x44FFD54F)),
                )
                Spacer(modifier = Modifier.height(14.dp))

                ReceiptRow("Member", deposit.memberName)
                ReceiptRow("Date", formatDate(deposit.createdAt))
                ReceiptRow("Amount", "UGX ${formatMoney(deposit.amount)}")
                ReceiptRow("Method", deposit.method)
                if (deposit.network.isNotBlank()) {
                    ReceiptRow("Network", deposit.network)
                    ReceiptRow("Phone", deposit.phone)
                }
                if (deposit.bankName.isNotBlank()) {
                    ReceiptRow("Bank", deposit.bankName)
                }
                ReceiptRow("Reference", deposit.reference)
                ReceiptRow("For", deposit.contributionFor)
                ReceiptRow("Status", if (deposit.status == "confirmed") "✅ Confirmed" else "🟡 Pending")

                Spacer(modifier = Modifier.height(10.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(1.dp)
                        .background(Color(0x44FFD54F)),
                )
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = "Thank you for contributing to City Alumni Saving Group.",
                    fontSize = 11.sp,
                    color = CreamText,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth(),
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        GradientBlueButton(text = "BACK TO LIST", onClick = onBack)

        Spacer(modifier = Modifier.height(20.dp))
    }
}

// =====================================================
//               REUSABLE COSMIC COMPONENTS
// =====================================================
@Composable
private fun GradientBlueButton(text: String, onClick: () -> Unit) {
    val gradient = Brush.verticalGradient(
        colors = listOf(
            Color(0xFF2B78E4),
            Color(0xFF1E62D0),
            Color(0xFF1044A5),
            Color(0xFF0C3888),
        )
    )
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
            .clip(RoundedCornerShape(14.dp))
            .background(gradient)
            .border(
                width = 1.dp,
                brush = Brush.verticalGradient(
                    listOf(Color(0x889BC5FF), Color(0x221044A5))
                ),
                shape = RoundedCornerShape(14.dp),
            )
            .clickable { onClick() },
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = text,
            fontSize = 17.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White,
            letterSpacing = 1.sp,
        )
    }
}

@Composable
private fun GlassButton(text: String, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
            .clip(RoundedCornerShape(14.dp))
            .background(Color(0x22FFFFFF))
            .border(
                width = 1.5.dp,
                brush = Brush.verticalGradient(
                    listOf(Color(0x88FFFFFF), Color(0x33FFFFFF))
                ),
                shape = RoundedCornerShape(14.dp),
            )
            .clickable { onClick() },
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = text,
            fontSize = 17.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White,
            letterSpacing = 1.sp,
        )
    }
}

@Composable
private fun CosmicTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    keyboardType: KeyboardType,
    imeAction: ImeAction,
    isPassword: Boolean = false,
    passwordVisible: Boolean = false,
    onTogglePassword: (() -> Unit)? = null,
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        singleLine = true,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedContainerColor = Color(0x33000000),
            unfocusedContainerColor = Color(0x22000000),
            focusedBorderColor = Color(0xFF2B78E4),
            unfocusedBorderColor = Color(0x66FFFFFF),
            focusedLabelColor = GoldTitle,
            unfocusedLabelColor = Color(0xCCFFFFFF),
            focusedTextColor = Color.White,
            unfocusedTextColor = Color.White,
            cursorColor = Color(0xFF2B78E4),
        ),
        visualTransformation = if (isPassword && !passwordVisible)
            PasswordVisualTransformation()
        else
            VisualTransformation.None,
        keyboardOptions = KeyboardOptions(
            keyboardType = keyboardType,
            imeAction = imeAction,
        ),
        trailingIcon = if (isPassword && onTogglePassword != null) {
            {
                IconButton(onClick = onTogglePassword) {
                    Icon(
                        imageVector = if (passwordVisible)
                            Icons.Filled.Visibility
                        else
                            Icons.Filled.VisibilityOff,
                        contentDescription = if (passwordVisible)
                            "Hide password" else "Show password",
                        tint = Color.White,
                    )
                }
            }
        } else null,
    )
}

// =====================================================
//               CANVAS OVERLAY DRAWINGS
// =====================================================
@Composable
private fun ConstellationBackground(modifier: Modifier = Modifier) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        val goldLineColor = Color(0x25E5C158)

        val p1 = Offset(w * 0.08f, h * 0.08f)
        val p2 = Offset(w * 0.32f, h * 0.04f)
        val p3 = Offset(w * 0.24f, h * 0.16f)
        val p4 = Offset(w * 0.06f, h * 0.24f)
        val p5 = Offset(w * 0.36f, h * 0.28f)
        val p6 = Offset(w * 0.20f, h * 0.36f)
        val p7 = Offset(w * 0.08f, h * 0.46f)
        val p8 = Offset(w * 0.28f, h * 0.52f)

        drawLine(goldLineColor, p1, p2, strokeWidth = 1.5f)
        drawLine(goldLineColor, p1, p3, strokeWidth = 1.5f)
        drawLine(goldLineColor, p2, p3, strokeWidth = 1.5f)
        drawLine(goldLineColor, p1, p4, strokeWidth = 1.5f)
        drawLine(goldLineColor, p3, p5, strokeWidth = 1.5f)
        drawLine(goldLineColor, p4, p6, strokeWidth = 1.5f)
        drawLine(goldLineColor, p3, p6, strokeWidth = 1.5f)
        drawLine(goldLineColor, p5, p6, strokeWidth = 1.5f)
        drawLine(goldLineColor, p6, p7, strokeWidth = 1.5f)
        drawLine(goldLineColor, p6, p8, strokeWidth = 1.5f)
        drawLine(goldLineColor, p7, p8, strokeWidth = 1.5f)

        val q1 = Offset(w * 0.88f, h * 0.06f)
        val q2 = Offset(w * 0.70f, h * 0.12f)
        val q3 = Offset(w * 0.94f, h * 0.20f)
        val q4 = Offset(w * 0.76f, h * 0.28f)
        val q5 = Offset(w * 0.96f, h * 0.38f)
        val q6 = Offset(w * 0.82f, h * 0.46f)

        drawLine(goldLineColor, q1, q2, strokeWidth = 1.5f)
        drawLine(goldLineColor, q1, q3, strokeWidth = 1.5f)
        drawLine(goldLineColor, q2, q4, strokeWidth = 1.5f)
        drawLine(goldLineColor, q3, q4, strokeWidth = 1.5f)
        drawLine(goldLineColor, q3, q5, strokeWidth = 1.5f)
        drawLine(goldLineColor, q4, q5, strokeWidth = 1.5f)
        drawLine(goldLineColor, q4, q6, strokeWidth = 1.5f)
        drawLine(goldLineColor, q5, q6, strokeWidth = 1.5f)

        listOf(p1, p2, p3, p4, p5, p6, p7, p8, q1, q2, q3, q4, q5, q6).forEach { pos ->
            drawCircle(Color(0xFFF3D37A), radius = 3f, center = pos)
            drawCircle(Color(0x33F3D37A), radius = 6f, center = pos)
        }

        val particlePositions = listOf(
            Offset(w * 0.15f, h * 0.58f),
            Offset(w * 0.26f, h * 0.63f),
            Offset(w * 0.40f, h * 0.61f),
            Offset(w * 0.54f, h * 0.64f),
            Offset(w * 0.68f, h * 0.60f),
            Offset(w * 0.82f, h * 0.63f),
            Offset(w * 0.22f, h * 0.73f),
            Offset(w * 0.10f, h * 0.81f),
            Offset(w * 0.90f, h * 0.80f),
            Offset(w * 0.28f, h * 0.85f),
            Offset(w * 0.75f, h * 0.86f),
        )

        particlePositions.forEachIndexed { i, pos ->
            val radius = if (i % 2 == 0) 3.5f else 2f
            drawCircle(Color(0xFFF3D37A), radius = radius, center = pos)
            drawCircle(Color(0x44FFD700), radius = radius * 2.5f, center = pos)
        }
    }
}

@Composable
private fun BottomArcGlow(modifier: Modifier = Modifier) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height

        val arcPath = Path().apply {
            moveTo(-w * 0.1f, h * 0.91f)
            quadraticTo(w * 0.5f, h * 0.86f, w * 1.1f, h * 0.91f)
        }

        drawPath(
            path = arcPath,
            brush = Brush.horizontalGradient(
                listOf(
                    Color(0x00A0C8FF),
                    Color(0x55A0C8FF),
                    Color(0x99BCE3FF),
                    Color(0x55A0C8FF),
                    Color(0x00A0C8FF)
                )
            ),
            style = Stroke(width = 5f),
        )

        drawPath(
            path = arcPath,
            brush = Brush.horizontalGradient(
                listOf(
                    Color(0x00FFFFFF),
                    Color(0x77FFFFFF),
                    Color(0xEEFFFFFF),
                    Color(0x77FFFFFF),
                    Color(0x00FFFFFF)
                )
            ),
            style = Stroke(width = 1.5f),
        )
    }
}

// =====================================================
//                    UTILITIES
// =====================================================
private fun timeGreeting(): String {
    val hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
    return when {
        hour in 5..11 -> "Good morning"
        hour in 12..17 -> "Good afternoon"
        else -> "Good evening"
    }
}

private fun formatMoney(amount: Long): String {
    return amount.toString()
        .reversed()
        .chunked(3)
        .joinToString(",")
        .reversed()
}

private fun currentDateTimeText(): String {
    val cal = Calendar.getInstance()
    val day = cal.get(Calendar.DAY_OF_MONTH)
    val monthNames = listOf(
        "Jan", "Feb", "Mar", "Apr", "May", "Jun",
        "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"
    )
    val month = monthNames[cal.get(Calendar.MONTH)]
    val year = cal.get(Calendar.YEAR)
    val hour = cal.get(Calendar.HOUR)
    val minute = cal.get(Calendar.MINUTE).toString().padStart(2, '0')
    val ampm = if (cal.get(Calendar.AM_PM) == Calendar.AM) "am" else "pm"
    return "$day $month $year  $hour:$minute$ampm"
}

private fun formatDate(timestamp: Long): String {
    if (timestamp == 0L) return "—"
    return try {
        val sdf = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
        sdf.format(Date(timestamp))
    } catch (e: Exception) {
        "—"
    }
}

private fun friendlyAuthError(raw: String?): String {
    val msg = raw ?: return "Something went wrong. Please try again."
    return when {
        msg.contains("password is invalid", true) -> "Wrong password. Please try again."
        msg.contains("no user record", true) -> "No account found with that email."
        msg.contains("email address is already in use", true) -> "That email is already registered."
        msg.contains("network", true) -> "No internet connection. Check your network."
        msg.contains("badly formatted", true) -> "Please check the email format."
        msg.contains("at least 6 characters", true) -> "Password must be at least 6 characters."
        else -> msg
    }
}

// =====================================================
//                   DATA SEEDER (ADMIN ONLY)
// =====================================================
private suspend fun seedDeposits() {
    val depositsRef = db.collection("deposits")

    // 1. Delete all existing deposit documents
    val existing = depositsRef.get().await()
    for (doc in existing.documents) {
        doc.reference.delete().await()
    }

    // 2. Insert the 13 records from the group's sheet
    val seeds: List<Map<String, Any?>> = listOf(
        mapOf(
            "userId" to "seed-mayiga",
            "memberName" to "Mayiga Frank",
            "amount" to 20000L,
            "method" to "Mobile Money",
            "network" to "MTN",
            "phone" to "256754358524",
            "reference" to "TXN-08JUL-001",
            "bankName" to "",
            "contributionFor" to "Week 1",
            "status" to "confirmed",
            "note" to "Initial deposit, remaining 20k",
            "balanceAfter" to 20000L,
            "createdAt" to 1751932800000L,
        ),
        mapOf(
            "userId" to "seed-ruta",
            "memberName" to "Ruta Evander",
            "amount" to 40000L,
            "method" to "Mobile Money",
            "network" to "Airtel",
            "phone" to "256764174427",
            "reference" to "TXN-09JUL-002",
            "bankName" to "",
            "contributionFor" to "Week 1",
            "status" to "confirmed",
            "note" to "Initial deposit, no penalty",
            "balanceAfter" to 60000L,
            "createdAt" to 1752019200000L,
        ),
        mapOf(
            "userId" to "seed-felix",
            "memberName" to "Felix Ayebazibwe",
            "amount" to 40000L,
            "method" to "Mobile Money",
            "network" to "MTN",
            "phone" to "0770111222",
            "reference" to "TXN-09JUL-003",
            "bankName" to "",
            "contributionFor" to "Week 1",
            "status" to "confirmed",
            "note" to "Initial deposit, uncleared penalty",
            "balanceAfter" to 100000L,
            "createdAt" to 1752019200000L,
        ),
        mapOf(
            "userId" to "seed-makoba",
            "memberName" to "Makoba Jackson",
            "amount" to 40000L,
            "method" to "Bank",
            "network" to "",
            "phone" to "0770333444",
            "reference" to "DEP-09JUL-004",
            "bankName" to "Stanbic Bank",
            "contributionFor" to "Week 1",
            "status" to "confirmed",
            "note" to "Initial deposit, penalty cleared later",
            "balanceAfter" to 140000L,
            "createdAt" to 1752019200000L,
        ),
        mapOf(
            "userId" to "seed-isaac",
            "memberName" to "Isaac Kelvin",
            "amount" to 20000L,
            "method" to "Mobile Money",
            "network" to "MTN",
            "phone" to "768760798",
            "reference" to "TXN-09JUL-005",
            "bankName" to "",
            "contributionFor" to "Week 1",
            "status" to "confirmed",
            "note" to "Initial deposit, remaining 20k",
            "balanceAfter" to 160000L,
            "createdAt" to 1752019200000L,
        ),
        mapOf(
            "userId" to "seed-isaac",
            "memberName" to "Isaac Kelvin",
            "amount" to 20000L,
            "method" to "Mobile Money",
            "network" to "MTN",
            "phone" to "768760798",
            "reference" to "TXN-12JUL-006",
            "bankName" to "",
            "contributionFor" to "Week 2",
            "status" to "confirmed",
            "note" to "Initial deposit",
            "balanceAfter" to 180000L,
            "createdAt" to 1752278400000L,
        ),
        mapOf(
            "userId" to "seed-isaac",
            "memberName" to "Isaac Kelvin",
            "amount" to 10000L,
            "method" to "Mobile Money",
            "network" to "MTN",
            "phone" to "768760798",
            "reference" to "TXN-19JUL-007",
            "bankName" to "",
            "contributionFor" to "Week 3",
            "status" to "confirmed",
            "note" to "Normal deposit for 24, 31 July",
            "balanceAfter" to 190000L,
            "createdAt" to 1752883200000L,
        ),
        mapOf(
            "userId" to "seed-makoba",
            "memberName" to "Makoba Jackson",
            "amount" to 10000L,
            "method" to "Bank",
            "network" to "",
            "phone" to "0770333444",
            "reference" to "DEP-21JUL-008",
            "bankName" to "Stanbic Bank",
            "contributionFor" to "Week 3",
            "status" to "confirmed",
            "note" to "Normal deposit for 24, 31 July",
            "balanceAfter" to 200000L,
            "createdAt" to 1753056000000L,
        ),
        mapOf(
            "userId" to "seed-remo",
            "memberName" to "Remo Amos",
            "amount" to 40000L,
            "method" to "Mobile Money",
            "network" to "Airtel",
            "phone" to "763315937",
            "reference" to "TXN-21JUL-009",
            "bankName" to "",
            "contributionFor" to "Week 3",
            "status" to "confirmed",
            "note" to "Initial deposit, UGX 1,000 penalty cleared",
            "balanceAfter" to 240000L,
            "createdAt" to 1753056000000L,
        ),
        mapOf(
            "userId" to "seed-mayiga",
            "memberName" to "Mayiga Frank",
            "amount" to 20000L,
            "method" to "Mobile Money",
            "network" to "MTN",
            "phone" to "754358524",
            "reference" to "TXN-24JUL-010",
            "bankName" to "",
            "contributionFor" to "Week 4",
            "status" to "confirmed",
            "note" to "Initial deposit, penalty cleared 6 Aug",
            "balanceAfter" to 260000L,
            "createdAt" to 1753315200000L,
        ),
        mapOf(
            "userId" to "seed-ruta",
            "memberName" to "Ruta Evander",
            "amount" to 10000L,
            "method" to "Mobile Money",
            "network" to "Airtel",
            "phone" to "256764174427",
            "reference" to "TXN-24JUL-011",
            "bankName" to "",
            "contributionFor" to "Week 4",
            "status" to "confirmed",
            "note" to "Normal deposit for 24, 31 July",
            "balanceAfter" to 270000L,
            "createdAt" to 1753315200000L,
        ),
        mapOf(
            "userId" to "seed-remo",
            "memberName" to "Remo Amos",
            "amount" to 10000L,
            "method" to "Mobile Money",
            "network" to "Airtel",
            "phone" to "763315937",
            "reference" to "TXN-24JUL-012",
            "bankName" to "",
            "contributionFor" to "Week 4",
            "status" to "confirmed",
            "note" to "Normal deposit for 24, 31 July",
            "balanceAfter" to 280000L,
            "createdAt" to 1753315200000L,
        ),
        mapOf(
            "userId" to "seed-felix",
            "memberName" to "Felix Ayebazibwe",
            "amount" to 20000L,
            "method" to "Mobile Money",
            "network" to "MTN",
            "phone" to "0770111222",
            "reference" to "TXN-28JUL-013",
            "bankName" to "",
            "contributionFor" to "Week 4",
            "status" to "confirmed",
            "note" to "Normal deposit, penalty cleared 28 July",
            "balanceAfter" to 300000L,
            "createdAt" to 1753660800000L,
        ),
    )

    // 3. Insert each — using auto-generated IDs
    for (record in seeds) {
        depositsRef.add(record).await()
    }
}

// =====================================================
//                   MEMBERS SCREEN
// =====================================================
@Composable
private fun MembersScreen(
    deposits: List<FireDeposit>,
    onBack: () -> Unit,
) {
    var members by remember { mutableStateOf<List<UserProfile>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var searchQuery by remember { mutableStateOf("") }
    var selectedMember by remember { mutableStateOf<UserProfile?>(null) }

    // Load users in real time
    LaunchedEffect(Unit) {
        db.collection("users")
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    isLoading = false
                    return@addSnapshotListener
                }
                if (snapshot != null) {
                    members = snapshot.documents.mapNotNull { doc ->
                        doc.toObject(UserProfile::class.java)
                    }
                    isLoading = false
                }
            }
    }

    // Compute savings per userId from deposits
    val savingsByUser: Map<String, Long> = deposits
        .filter { it.status == "confirmed" }
        .groupBy { it.userId }
        .mapValues { entry -> entry.value.sumOf { it.amount } }

    // Sort: admin first, then treasurer, then member; then alphabetically
    val roleOrder = mapOf("admin" to 0, "treasurer" to 1, "member" to 2)
    val sorted = members.sortedWith(
        compareBy<UserProfile> { roleOrder[it.role] ?: 3 }
            .thenBy { it.fullName.lowercase() }
    )

    val filtered = if (searchQuery.isBlank()) sorted else sorted.filter {
        it.fullName.contains(searchQuery, ignoreCase = true) ||
            it.phone.contains(searchQuery) ||
            it.email.contains(searchQuery, ignoreCase = true)
    }

    // Show member detail screen instead of list
    if (selectedMember != null) {
        MemberDetailScreen(
            member = selectedMember!!,
            savings = savingsByUser[selectedMember!!.uid] ?: 0L,
            deposits = deposits.filter { it.userId == selectedMember!!.uid },
            onBack = { selectedMember = null },
        )
        return
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp, vertical = 16.dp),
    ) {
        // Header with back
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onBack, modifier = Modifier.size(36.dp)) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = Color.White,
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
            Column {
                Text(
                    text = "MEMBERS",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Serif,
                    color = GoldTitle,
                )
                Text(
                    text = "CITY ALUMNI SAVING GROUP",
                    fontSize = 11.sp,
                    color = CreamText,
                    letterSpacing = 1.sp,
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Stat cards
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(14.dp))
                    .background(Color(0x33FFFFFF))
                    .border(1.dp, Color(0x44FFD54F), RoundedCornerShape(14.dp))
                    .padding(14.dp),
            ) {
                Column {
                    Text(
                        text = "TOTAL",
                        fontSize = 11.sp,
                        color = GoldTitle,
                        letterSpacing = 1.sp,
                        fontWeight = FontWeight.Bold,
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = members.size.toString(),
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Serif,
                        color = Color.White,
                    )
                }
            }
            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(14.dp))
                    .background(Color(0x33FFFFFF))
                    .border(1.dp, Color(0x44FFD54F), RoundedCornerShape(14.dp))
                    .padding(14.dp),
            ) {
                Column {
                    Text(
                        text = "ADMINS",
                        fontSize = 11.sp,
                        color = GoldTitle,
                        letterSpacing = 1.sp,
                        fontWeight = FontWeight.Bold,
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = members.count { it.role == "admin" || it.role == "treasurer" }.toString(),
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Serif,
                        color = Color.White,
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Search box
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            placeholder = { Text("Search by name, phone, or email", color = Color(0x88FFFFFF)) },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            leadingIcon = {
                Icon(
                    imageVector = Icons.Filled.Search,
                    contentDescription = "Search",
                    tint = GoldSoft,
                )
            },
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = Color(0x33000000),
                unfocusedContainerColor = Color(0x22000000),
                focusedBorderColor = Color(0xFFFFD54F),
                unfocusedBorderColor = Color(0x44FFFFFF),
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White,
                cursorColor = GoldSoft,
            ),
        )

        Spacer(modifier = Modifier.height(14.dp))

        if (isLoading) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center,
            ) {
                CircularProgressIndicator(color = GoldSoft)
            }
        } else if (filtered.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = if (searchQuery.isBlank()) "No members yet" else "No members match your search",
                    fontSize = 14.sp,
                    color = CreamText,
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                items(filtered) { member ->
                    MemberRow(
                        member = member,
                        savings = savingsByUser[member.uid] ?: 0L,
                        onClick = { selectedMember = member },
                    )
                }
                item { Spacer(modifier = Modifier.height(16.dp)) }
            }
        }
    }
}

@Composable
private fun MemberRow(
    member: UserProfile,
    savings: Long,
    onClick: () -> Unit,
) {
    val (roleLabel, roleColor, avatarEmoji) = when (member.role) {
        "admin" -> Triple("⭐ Admin", GoldSoft, "👑")
        "treasurer" -> Triple("💰 Treasurer", SuccessGreen, "💼")
        else -> Triple("Member", Color(0x88FFFFFF), "👤")
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(Color(0x33FFFFFF))
            .border(1.dp, Color(0x33FFD54F), RoundedCornerShape(14.dp))
            .clickable { onClick() }
            .padding(14.dp),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            // Avatar
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(
                        if (member.role == "admin") Color(0x55FFD54F)
                        else Color(0x33FFFFFF)
                    )
                    .border(1.dp, Color(0x66FFD54F), CircleShape),
                contentAlignment = Alignment.Center,
            ) {
                Text(text = avatarEmoji, fontSize = 22.sp)
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = member.fullName.ifBlank { "Unnamed Member" },
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                )
                Spacer(modifier = Modifier.height(3.dp))
                Text(
                    text = member.phone.ifBlank { member.email },
                    fontSize = 11.sp,
                    color = CreamText,
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "Savings: UGX ${formatMoney(savings)}",
                    fontSize = 12.sp,
                    color = SuccessGreen,
                    fontWeight = FontWeight.Medium,
                )
            }

            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = roleLabel,
                    fontSize = 10.sp,
                    color = roleColor,
                    fontWeight = FontWeight.Bold,
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "→",
                    fontSize = 18.sp,
                    color = GoldSoft,
                )
            }
        }
    }
}

// =====================================================
//                MEMBER DETAIL SCREEN
// =====================================================
@Composable
private fun MemberDetailScreen(
    member: UserProfile,
    savings: Long,
    deposits: List<FireDeposit>,
    onBack: () -> Unit,
) {
    val (roleLabel, roleColor, avatarEmoji) = when (member.role) {
        "admin" -> Triple("⭐ Admin", GoldSoft, "👑")
        "treasurer" -> Triple("💰 Treasurer", SuccessGreen, "💼")
        else -> Triple("Member", CreamText, "👤")
    }

    val joinedText = if (member.joinedAt > 0) formatDate(member.joinedAt) else "—"
    val confirmedDeposits = deposits.filter { it.status == "confirmed" }
    val pendingDeposits = deposits.filter { it.status == "pending" }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(20.dp),
    ) {
        IconButton(onClick = onBack, modifier = Modifier.size(36.dp)) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Back",
                tint = Color.White,
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Profile header
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Box(
                modifier = Modifier
                    .size(96.dp)
                    .clip(CircleShape)
                    .background(
                        if (member.role == "admin") Color(0x55FFD54F)
                        else Color(0x33FFFFFF)
                    )
                    .border(2.dp, Color(0x88FFD54F), CircleShape),
                contentAlignment = Alignment.Center,
            ) {
                Text(text = avatarEmoji, fontSize = 42.sp)
            }
            Spacer(modifier = Modifier.height(14.dp))
            Text(
                text = member.fullName.ifBlank { "Unnamed Member" },
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Serif,
                color = GoldTitle,
                textAlign = TextAlign.Center,
            )
            Spacer(modifier = Modifier.height(6.dp))
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(20.dp))
                    .background(Color(0x33FFD54F))
                    .padding(horizontal = 14.dp, vertical = 6.dp),
            ) {
                Text(
                    text = roleLabel,
                    fontSize = 12.sp,
                    color = roleColor,
                    fontWeight = FontWeight.Bold,
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Total savings card
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(
                    Brush.linearGradient(
                        listOf(Color(0xFF3A2A0A), Color(0xFF6B4E10), Color(0xFF3A2A0A))
                    )
                )
                .border(1.dp, Color(0x88FFD54F), RoundedCornerShape(16.dp))
                .padding(18.dp),
        ) {
            Column {
                Text(
                    text = "💰 TOTAL SAVINGS",
                    fontSize = 12.sp,
                    color = GoldTitle,
                    letterSpacing = 1.sp,
                    fontWeight = FontWeight.Bold,
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "UGX ${formatMoney(savings)}",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Serif,
                    color = Color.White,
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Info rows
        Text(
            text = "PROFILE",
            fontSize = 12.sp,
            color = GoldTitle,
            letterSpacing = 1.sp,
            fontWeight = FontWeight.Bold,
        )
        Spacer(modifier = Modifier.height(10.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(14.dp))
                .background(Color(0x33FFFFFF))
                .border(1.dp, Color(0x33FFD54F), RoundedCornerShape(14.dp))
                .padding(16.dp),
        ) {
            Column {
                ReceiptRow("Phone", member.phone.ifBlank { "—" })
                ReceiptRow("Email", member.email.ifBlank { "—" })
                ReceiptRow("Joined", joinedText)
                ReceiptRow("Status", member.status.replaceFirstChar { it.uppercase() })
                ReceiptRow("Role", roleLabel)
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Deposit counts
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(14.dp))
                    .background(Color(0x33FFFFFF))
                    .border(1.dp, Color(0x44FFD54F), RoundedCornerShape(14.dp))
                    .padding(14.dp),
            ) {
                Column {
                    Text(
                        text = "CONFIRMED",
                        fontSize = 11.sp,
                        color = SuccessGreen,
                        letterSpacing = 1.sp,
                        fontWeight = FontWeight.Bold,
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = confirmedDeposits.size.toString(),
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                    )
                }
            }
            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(14.dp))
                    .background(Color(0x33FFFFFF))
                    .border(1.dp, Color(0x44FFD54F), RoundedCornerShape(14.dp))
                    .padding(14.dp),
            ) {
                Column {
                    Text(
                        text = "PENDING",
                        fontSize = 11.sp,
                        color = PendingAmber,
                        letterSpacing = 1.sp,
                        fontWeight = FontWeight.Bold,
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "UGX ${formatMoney(pendingDeposits.sumOf { it.amount })}",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Recent deposits
        if (deposits.isNotEmpty()) {
            Text(
                text = "RECENT DEPOSITS",
                fontSize = 12.sp,
                color = GoldTitle,
                letterSpacing = 1.sp,
                fontWeight = FontWeight.Bold,
            )
            Spacer(modifier = Modifier.height(10.dp))

            deposits.sortedByDescending { it.createdAt }.take(10).forEach { deposit ->
                FireDepositRow(deposit = deposit, onClick = { })
                Spacer(modifier = Modifier.height(8.dp))
            }
        }

        Spacer(modifier = Modifier.height(20.dp))
    }
}

// =====================================================
//                   PENALTIES TAB
// =====================================================
@Composable
private fun PenaltiesTab(
    penalties: List<FirePenalty>,
    myRole: String,
    myName: String,
) {
    val isAdmin = myRole == "admin" || myRole == "treasurer"
    var showAddDialog by remember { mutableStateOf(false) }

    val unpaidTotal = penalties.filter { it.status == "unpaid" }.sumOf { it.amount }
    val clearedTotal = penalties.filter { it.status == "cleared" }.sumOf { it.amount }

    // Members see only their own; admins see all
    val visible = if (isAdmin) penalties else penalties.filter { it.memberName == myName }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp, vertical = 16.dp),
        ) {
            Text(
                text = "PENALTIES",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Serif,
                color = GoldTitle,
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "CITY ALUMNI SAVING GROUP",
                fontSize = 12.sp,
                color = CreamText,
                letterSpacing = 1.sp,
            )

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(14.dp))
                        .background(Color(0x33FFFFFF))
                        .border(1.dp, Color(0x66FF6B6B), RoundedCornerShape(14.dp))
                        .padding(14.dp),
                ) {
                    Column {
                        Text(
                            text = "UNPAID",
                            fontSize = 11.sp,
                            color = ErrorRed,
                            letterSpacing = 1.sp,
                            fontWeight = FontWeight.Bold,
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "UGX ${formatMoney(unpaidTotal)}",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Serif,
                            color = Color.White,
                        )
                    }
                }
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(14.dp))
                        .background(Color(0x33FFFFFF))
                        .border(1.dp, Color(0x44FFD54F), RoundedCornerShape(14.dp))
                        .padding(14.dp),
                ) {
                    Column {
                        Text(
                            text = "CLEARED",
                            fontSize = 11.sp,
                            color = SuccessGreen,
                            letterSpacing = 1.sp,
                            fontWeight = FontWeight.Bold,
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "UGX ${formatMoney(clearedTotal)}",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Serif,
                            color = Color.White,
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (visible.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center,
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(text = "🎉", fontSize = 42.sp)
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "No penalties",
                            fontSize = 16.sp,
                            color = CreamText,
                            fontWeight = FontWeight.Medium,
                        )
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                ) {
                    items(visible) { penalty ->
                        PenaltyRow(
                            penalty = penalty,
                            canManage = isAdmin,
                            onClear = {
                                db.collection("penalties").document(penalty.id)
                                    .update(
                                        mapOf(
                                            "status" to "cleared",
                                            "clearedAt" to System.currentTimeMillis(),
                                        )
                                    )
                            },
                        )
                    }
                    item { Spacer(modifier = Modifier.height(80.dp)) }
                }
            }
        }

        // FAB to add penalty (admin only)
        if (isAdmin) {
            FloatingActionButton(
                onClick = { showAddDialog = true },
                containerColor = GoldSoft,
                contentColor = Color(0xFF1A1228),
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(20.dp),
            ) {
                Icon(imageVector = Icons.Filled.Add, contentDescription = "Add penalty")
            }
        }
    }

    if (showAddDialog) {
        AddPenaltyDialog(onDismiss = { showAddDialog = false })
    }
}

@Composable
private fun PenaltyRow(
    penalty: FirePenalty,
    canManage: Boolean,
    onClear: () -> Unit,
) {
    val isUnpaid = penalty.status == "unpaid"
    val statusColor = if (isUnpaid) ErrorRed else SuccessGreen
    val statusLabel = if (isUnpaid) "🔴 Unpaid" else "✅ Cleared"

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(Color(0x33FFFFFF))
            .border(1.dp, Color(0x33FFD54F), RoundedCornerShape(14.dp))
            .padding(14.dp),
    ) {
        Column {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(Color(0x44FF6B6B))
                        .border(1.dp, Color(0x66FF6B6B), CircleShape),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(text = "⚠️", fontSize = 20.sp)
                }
                Spacer(modifier = Modifier.width(14.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = penalty.memberName,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = penalty.reason,
                        fontSize = 12.sp,
                        color = CreamText,
                    )
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = "UGX ${formatMoney(penalty.amount)}",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = statusColor,
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = statusLabel,
                        fontSize = 10.sp,
                        color = statusColor,
                        fontWeight = FontWeight.Medium,
                    )
                }
            }

            if (canManage && isUnpaid) {
                Spacer(modifier = Modifier.height(12.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(40.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(Color(0x448BC34A))
                        .border(1.dp, SuccessGreen, RoundedCornerShape(10.dp))
                        .clickable { onClear() },
                    contentAlignment = Alignment.Center,
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Filled.Check,
                            contentDescription = null,
                            tint = SuccessGreen,
                            modifier = Modifier.size(18.dp),
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "MARK AS CLEARED",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = SuccessGreen,
                            letterSpacing = 0.5.sp,
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun AddPenaltyDialog(onDismiss: () -> Unit) {
    var memberName by remember { mutableStateOf("") }
    var amount by remember { mutableStateOf("") }
    var reason by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var isSaving by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = { if (!isSaving) onDismiss() },
        containerColor = Color(0xFF1A1228),
        titleContentColor = GoldTitle,
        textContentColor = CreamText,
        title = {
            Text(
                text = "Add Penalty",
                fontFamily = FontFamily.Serif,
                fontWeight = FontWeight.Bold,
            )
        },
        text = {
            Column {
                OutlinedTextField(
                    value = memberName,
                    onValueChange = { memberName = it; errorMessage = null },
                    label = { Text("Member name") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = Color(0x33000000),
                        unfocusedContainerColor = Color(0x22000000),
                        focusedBorderColor = GoldSoft,
                        unfocusedBorderColor = Color(0x66FFFFFF),
                        focusedLabelColor = GoldTitle,
                        unfocusedLabelColor = Color(0xCCFFFFFF),
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        cursorColor = GoldSoft,
                    ),
                )
                Spacer(modifier = Modifier.height(10.dp))
                OutlinedTextField(
                    value = amount,
                    onValueChange = { amount = it.filter { c -> c.isDigit() }; errorMessage = null },
                    label = { Text("Amount (UGX)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = Color(0x33000000),
                        unfocusedContainerColor = Color(0x22000000),
                        focusedBorderColor = GoldSoft,
                        unfocusedBorderColor = Color(0x66FFFFFF),
                        focusedLabelColor = GoldTitle,
                        unfocusedLabelColor = Color(0xCCFFFFFF),
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        cursorColor = GoldSoft,
                    ),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                )
                Spacer(modifier = Modifier.height(10.dp))
                OutlinedTextField(
                    value = reason,
                    onValueChange = { reason = it; errorMessage = null },
                    label = { Text("Reason") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = Color(0x33000000),
                        unfocusedContainerColor = Color(0x22000000),
                        focusedBorderColor = GoldSoft,
                        unfocusedBorderColor = Color(0x66FFFFFF),
                        focusedLabelColor = GoldTitle,
                        unfocusedLabelColor = Color(0xCCFFFFFF),
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        cursorColor = GoldSoft,
                    ),
                )
                if (errorMessage != null) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(text = errorMessage!!, color = ErrorRed, fontSize = 12.sp)
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    when {
                        memberName.isBlank() -> errorMessage = "Enter member name"
                        amount.isBlank() -> errorMessage = "Enter amount"
                        reason.isBlank() -> errorMessage = "Enter reason"
                        else -> {
                            isSaving = true
                            errorMessage = null
                        }
                    }
                },
                enabled = !isSaving,
            ) {
                Text(text = if (isSaving) "Saving..." else "ADD", color = GoldSoft, fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss, enabled = !isSaving) {
                Text(text = "CANCEL", color = CreamText)
            }
        },
    )

    if (isSaving) {
        LaunchedEffect(Unit) {
            try {
                val penalty = FirePenalty(
                    userId = "manual-" + System.currentTimeMillis(),
                    memberName = memberName.trim(),
                    amount = amount.toLongOrNull() ?: 0L,
                    reason = reason.trim(),
                    status = "unpaid",
                    createdAt = System.currentTimeMillis(),
                    clearedAt = 0L,
                )
                db.collection("penalties").add(penalty).await()
                isSaving = false
                onDismiss()
            } catch (e: Exception) {
                isSaving = false
                errorMessage = "Failed: ${e.message}"
            }
        }
    }
}

// =====================================================
//                     CHAT TAB
// =====================================================
@Composable
private fun ChatTab(
    messages: List<FireMessage>,
    myId: String,
    myName: String,
) {
    var draft by remember { mutableStateOf("") }
    var isSending by remember { mutableStateOf(false) }
    var pendingSend by remember { mutableStateOf<String?>(null) }
    val listState = rememberLazyListState()

    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) {
            listState.animateScrollToItem(messages.size - 1)
        }
    }

    // Perform send when pendingSend is set
    LaunchedEffect(pendingSend) {
        val text = pendingSend ?: return@LaunchedEffect
        try {
            val msg = FireMessage(
                senderId = myId,
                senderName = myName,
                text = text,
                createdAt = System.currentTimeMillis(),
            )
            db.collection("chats").add(msg).await()
        } catch (_: Exception) {
        } finally {
            pendingSend = null
            isSending = false
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp, vertical = 12.dp),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(Color(0x55FFD54F))
                    .border(1.dp, GoldSoft, CircleShape),
                contentAlignment = Alignment.Center,
            ) {
                Text(text = "💬", fontSize = 22.sp)
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(
                    text = "GROUP CHAT",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Serif,
                    color = GoldTitle,
                )
                Text(
                    text = "${messages.size} messages · City Alumni",
                    fontSize = 11.sp,
                    color = CreamText,
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .clip(RoundedCornerShape(14.dp))
                .background(Color(0x22FFFFFF))
                .border(1.dp, Color(0x33FFD54F), RoundedCornerShape(14.dp))
                .padding(12.dp),
        ) {
            if (messages.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center,
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(text = "💬", fontSize = 42.sp)
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "No messages yet",
                            fontSize = 15.sp,
                            color = CreamText,
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Say hello to the group!",
                            fontSize = 12.sp,
                            color = Color(0x88FFFFFF),
                        )
                    }
                }
            } else {
                LazyColumn(
                    state = listState,
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    items(messages) { msg ->
                        ChatBubble(
                            message = msg,
                            isMine = msg.senderId == myId,
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            OutlinedTextField(
                value = draft,
                onValueChange = { draft = it },
                placeholder = { Text("Type a message...", color = Color(0x88FFFFFF)) },
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(24.dp),
                maxLines = 4,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = Color(0x33000000),
                    unfocusedContainerColor = Color(0x22000000),
                    focusedBorderColor = GoldSoft,
                    unfocusedBorderColor = Color(0x44FFFFFF),
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    cursorColor = GoldSoft,
                ),
            )
            Spacer(modifier = Modifier.width(8.dp))
            Box(
                modifier = Modifier
                    .size(50.dp)
                    .clip(CircleShape)
                    .background(
                        if (draft.isBlank() || isSending) Color(0x44FFD54F)
                        else GoldSoft
                    )
                    .clickable(enabled = draft.isNotBlank() && !isSending) {
                        val text = draft.trim()
                        if (text.isEmpty()) return@clickable
                        pendingSend = text
                        draft = ""
                        isSending = true
                    },
                contentAlignment = Alignment.Center,
            ) {
                if (isSending) {
                    CircularProgressIndicator(
                        color = Color(0xFF1A1228),
                        modifier = Modifier.size(22.dp),
                        strokeWidth = 2.dp,
                    )
                } else {
                    Icon(
                        imageVector = Icons.Filled.Send,
                        contentDescription = "Send",
                        tint = if (draft.isBlank()) Color(0x88FFFFFF) else Color(0xFF1A1228),
                        modifier = Modifier.size(22.dp),
                    )
                }
            }
        }
    }
}

@Composable
private fun ChatBubble(
    message: FireMessage,
    isMine: Boolean,
) {
    val alignment = if (isMine) Alignment.End else Alignment.Start
    val bubbleColor = if (isMine) Color(0xFF2B78E4) else Color(0x55FFFFFF)
    val textColor = Color.White
    val timeText = formatTimeOnly(message.createdAt)

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = alignment,
    ) {
        if (!isMine) {
            Text(
                text = message.senderName,
                fontSize = 11.sp,
                color = GoldSoft,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(start = 8.dp, bottom = 2.dp),
            )
        }

        Box(
            modifier = Modifier
                .widthIn(max = 280.dp)
                .clip(
                    RoundedCornerShape(
                        topStart = 14.dp,
                        topEnd = 14.dp,
                        bottomStart = if (isMine) 14.dp else 4.dp,
                        bottomEnd = if (isMine) 4.dp else 14.dp,
                    )
                )
                .background(bubbleColor)
                .border(
                    width = 1.dp,
                    color = if (isMine) Color(0x662B78E4) else Color(0x33FFD54F),
                    shape = RoundedCornerShape(
                        topStart = 14.dp,
                        topEnd = 14.dp,
                        bottomStart = if (isMine) 14.dp else 4.dp,
                        bottomEnd = if (isMine) 4.dp else 14.dp,
                    ),
                )
                .padding(horizontal = 12.dp, vertical = 8.dp),
        ) {
            Column {
                Text(
                    text = message.text,
                    fontSize = 14.sp,
                    color = textColor,
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = timeText,
                    fontSize = 10.sp,
                    color = Color(0xAAFFFFFF),
                    modifier = Modifier.align(Alignment.End),
                )
            }
        }
    }
}

private fun formatTimeOnly(timestamp: Long): String {
    if (timestamp == 0L) return ""
    return try {
        val sdf = SimpleDateFormat("HH:mm", Locale.getDefault())
        sdf.format(Date(timestamp))
    } catch (_: Exception) {
        ""
    }
}

// =====================================================
//                 REPORTS SCREEN
// =====================================================
private enum class ReportTab(val label: String) {
    MONTHLY("Monthly"),
    MEMBERS("Members"),
    SUMMARY("Summary"),
}

@Composable
private fun ReportsScreen(
    deposits: List<FireDeposit>,
    penalties: List<FirePenalty>,
    onBack: () -> Unit,
) {
    var selectedTab by remember { mutableStateOf(ReportTab.MONTHLY) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp, vertical = 16.dp),
    ) {
        // Header
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onBack, modifier = Modifier.size(36.dp)) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = Color.White,
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
            Column {
                Text(
                    text = "REPORTS",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Serif,
                    color = GoldTitle,
                )
                Text(
                    text = "CITY ALUMNI SAVING GROUP",
                    fontSize = 11.sp,
                    color = CreamText,
                    letterSpacing = 1.sp,
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Tab chips
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            ReportTab.values().forEach { tab ->
                val isSelected = tab == selectedTab
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(12.dp))
                        .background(if (isSelected) Color(0x66FFD54F) else Color(0x33FFFFFF))
                        .border(
                            width = 1.dp,
                            color = if (isSelected) GoldSoft else Color(0x44FFFFFF),
                            shape = RoundedCornerShape(12.dp),
                        )
                        .clickable { selectedTab = tab }
                        .padding(vertical = 12.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = tab.label,
                        fontSize = 13.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                        color = if (isSelected) GoldSoft else CreamText,
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        when (selectedTab) {
            ReportTab.MONTHLY -> MonthlyReport(deposits, penalties)
            ReportTab.MEMBERS -> MembersReport(deposits)
            ReportTab.SUMMARY -> OverallSummary(deposits, penalties)
        }
    }
}

// =====================================================
//                 MONTHLY REPORT
// =====================================================
@Composable
private fun MonthlyReport(
    deposits: List<FireDeposit>,
    penalties: List<FirePenalty>,
) {
    var selectedMonthOffset by remember { mutableStateOf(0) } // 0 = this month, -1 = last month, etc.

    // Compute the displayed month
    val displayCal = remember(selectedMonthOffset) {
        Calendar.getInstance().apply {
            add(Calendar.MONTH, selectedMonthOffset)
        }
    }

    val displayMonth = displayCal.get(Calendar.MONTH)
    val displayYear = displayCal.get(Calendar.YEAR)
    val monthName = listOf(
        "January", "February", "March", "April", "May", "June",
        "July", "August", "September", "October", "November", "December"
    )[displayMonth]

    // Filter data for that month
    val monthDeposits = deposits.filter {
        val cal = Calendar.getInstance().apply { timeInMillis = it.createdAt }
        cal.get(Calendar.MONTH) == displayMonth && cal.get(Calendar.YEAR) == displayYear
    }
    val monthPenalties = penalties.filter {
        val cal = Calendar.getInstance().apply { timeInMillis = it.createdAt }
        cal.get(Calendar.MONTH) == displayMonth && cal.get(Calendar.YEAR) == displayYear
    }

    val totalDeposits = monthDeposits.filter { it.status == "confirmed" }.sumOf { it.amount }
    val pendingDeposits = monthDeposits.filter { it.status == "pending" }.sumOf { it.amount }
    val totalPenalties = monthPenalties.sumOf { it.amount }
    val clearedPenalties = monthPenalties.filter { it.status == "cleared" }.sumOf { it.amount }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState()),
    ) {
        // Month selector
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            IconButton(onClick = { selectedMonthOffset -= 1 }) {
                Text(text = "◀", fontSize = 18.sp, color = GoldSoft)
            }
            Text(
                text = "$monthName $displayYear",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Serif,
                color = GoldTitle,
            )
            IconButton(
                onClick = { if (selectedMonthOffset < 0) selectedMonthOffset += 1 },
                enabled = selectedMonthOffset < 0,
            ) {
                Text(
                    text = "▶",
                    fontSize = 18.sp,
                    color = if (selectedMonthOffset < 0) GoldSoft else Color(0x44FFFFFF),
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Hero card — total deposits this month
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(
                    Brush.linearGradient(
                        listOf(Color(0xFF3A2A0A), Color(0xFF6B4E10), Color(0xFF3A2A0A))
                    )
                )
                .border(1.dp, Color(0x88FFD54F), RoundedCornerShape(16.dp))
                .padding(18.dp),
        ) {
            Column {
                Text(
                    text = "💰 CONFIRMED DEPOSITS",
                    fontSize = 12.sp,
                    color = GoldTitle,
                    letterSpacing = 1.sp,
                    fontWeight = FontWeight.Bold,
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "UGX ${formatMoney(totalDeposits)}",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Serif,
                    color = Color.White,
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Grid of stats
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            ReportStat(
                modifier = Modifier.weight(1f),
                label = "TRANSACTIONS",
                value = monthDeposits.size.toString(),
                color = Color.White,
            )
            ReportStat(
                modifier = Modifier.weight(1f),
                label = "PENDING",
                value = "UGX ${formatMoney(pendingDeposits)}",
                color = PendingAmber,
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            ReportStat(
                modifier = Modifier.weight(1f),
                label = "PENALTIES",
                value = "UGX ${formatMoney(totalPenalties)}",
                color = ErrorRed,
            )
            ReportStat(
                modifier = Modifier.weight(1f),
                label = "CLEARED",
                value = "UGX ${formatMoney(clearedPenalties)}",
                color = SuccessGreen,
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Method breakdown
        if (monthDeposits.isNotEmpty()) {
            Text(
                text = "BY METHOD",
                fontSize = 12.sp,
                color = GoldTitle,
                letterSpacing = 1.sp,
                fontWeight = FontWeight.Bold,
            )
            Spacer(modifier = Modifier.height(10.dp))

            val byMethod = monthDeposits.filter { it.status == "confirmed" }
                .groupBy { if (it.method == "Bank") "Bank" else it.network.ifBlank { "Other" } }

            byMethod.forEach { (method, list) ->
                MethodRow(
                    emoji = if (method == "Bank") "🏦" else "📱",
                    label = method,
                    count = list.size,
                    amount = list.sumOf { it.amount },
                )
                Spacer(modifier = Modifier.height(8.dp))
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Export button (placeholder for now)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(Color(0x33FFFFFF))
                .border(1.5.dp, GoldSoft, RoundedCornerShape(12.dp))
                .clickable {
                    // Phase 14: real export
                },
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = "📤  EXPORT THIS MONTH",
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = GoldSoft,
                letterSpacing = 1.sp,
            )
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Composable
private fun ReportStat(
    modifier: Modifier = Modifier,
    label: String,
    value: String,
    color: Color,
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(Color(0x33FFFFFF))
            .border(1.dp, Color(0x33FFD54F), RoundedCornerShape(12.dp))
            .padding(12.dp),
    ) {
        Column {
            Text(
                text = label,
                fontSize = 10.sp,
                color = CreamText,
                letterSpacing = 0.8.sp,
                fontWeight = FontWeight.Bold,
            )
            Spacer(modifier = Modifier.height(5.dp))
            Text(
                text = value,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = color,
            )
        }
    }
}

@Composable
private fun MethodRow(
    emoji: String,
    label: String,
    count: Int,
    amount: Long,
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(Color(0x22FFFFFF))
            .border(1.dp, Color(0x33FFD54F), RoundedCornerShape(12.dp))
            .padding(12.dp),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(text = emoji, fontSize = 20.sp)
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = label,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                )
                Text(
                    text = "$count transaction${if (count != 1) "s" else ""}",
                    fontSize = 11.sp,
                    color = CreamText,
                )
            }
            Text(
                text = "UGX ${formatMoney(amount)}",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = SuccessGreen,
            )
        }
    }
}

// =====================================================
//                 MEMBERS REPORT
// =====================================================
@Composable
private fun MembersReport(deposits: List<FireDeposit>) {
    // Group confirmed deposits by member name
    val byMember = deposits
        .filter { it.status == "confirmed" }
        .groupBy { it.memberName }
        .map { (name, list) -> Triple(name, list.size, list.sumOf { it.amount }) }
        .sortedByDescending { it.third }

    val grandTotal = byMember.sumOf { it.third }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState()),
    ) {
        // Hero — grand total
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(
                    Brush.linearGradient(
                        listOf(Color(0xFF3A2A0A), Color(0xFF6B4E10), Color(0xFF3A2A0A))
                    )
                )
                .border(1.dp, Color(0x88FFD54F), RoundedCornerShape(16.dp))
                .padding(18.dp),
        ) {
            Column {
                Text(
                    text = "🏆 TOP CONTRIBUTORS",
                    fontSize = 12.sp,
                    color = GoldTitle,
                    letterSpacing = 1.sp,
                    fontWeight = FontWeight.Bold,
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "UGX ${formatMoney(grandTotal)}",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Serif,
                    color = Color.White,
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "${byMember.size} members contributed",
                    fontSize = 12.sp,
                    color = CreamText,
                )
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        if (byMember.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = "No contributions yet",
                    fontSize = 14.sp,
                    color = CreamText,
                )
            }
        } else {
            byMember.forEachIndexed { index, (name, count, amount) ->
                val sharePercent = if (grandTotal > 0) (amount * 100.0 / grandTotal).toInt() else 0

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .background(Color(0x33FFFFFF))
                        .border(1.dp, Color(0x33FFD54F), RoundedCornerShape(14.dp))
                        .padding(14.dp),
                ) {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            // Rank badge
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(
                                        when (index) {
                                            0 -> Color(0x88FFD700)
                                            1 -> Color(0x88C0C0C0)
                                            2 -> Color(0x88CD7F32)
                                            else -> Color(0x33FFFFFF)
                                        }
                                    )
                                    .border(1.dp, Color(0x66FFD54F), CircleShape),
                                contentAlignment = Alignment.Center,
                            ) {
                                Text(
                                    text = "${index + 1}",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White,
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = name,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White,
                                )
                                Text(
                                    text = "$count deposit${if (count != 1) "s" else ""} · $sharePercent% of total",
                                    fontSize = 11.sp,
                                    color = CreamText,
                                )
                            }
                            Text(
                                text = "UGX ${formatMoney(amount)}",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = SuccessGreen,
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        // Progress bar
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(6.dp)
                                .clip(RoundedCornerShape(3.dp))
                                .background(Color(0x22FFFFFF)),
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth(sharePercent / 100f)
                                    .height(6.dp)
                                    .clip(RoundedCornerShape(3.dp))
                                    .background(
                                        Brush.horizontalGradient(
                                            listOf(GoldSoft, Color(0xFFFFB300))
                                        )
                                    ),
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(10.dp))
            }
        }

        Spacer(modifier = Modifier.height(20.dp))
    }
}

// =====================================================
//                 OVERALL SUMMARY
// =====================================================
@Composable
private fun OverallSummary(
    deposits: List<FireDeposit>,
    penalties: List<FirePenalty>,
) {
    val confirmed = deposits.filter { it.status == "confirmed" }
    val pending = deposits.filter { it.status == "pending" }

    val totalConfirmed = confirmed.sumOf { it.amount }
    val totalPending = pending.sumOf { it.amount }
    val totalPenalties = penalties.sumOf { it.amount }
    val clearedPenalties = penalties.filter { it.status == "cleared" }.sumOf { it.amount }
    val unpaidPenalties = totalPenalties - clearedPenalties

    val firstDate = confirmed.minByOrNull { it.createdAt }?.createdAt ?: 0L
    val lastDate = confirmed.maxByOrNull { it.createdAt }?.createdAt ?: 0L

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState()),
    ) {
        // Group balance hero
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(18.dp))
                .background(
                    Brush.linearGradient(
                        listOf(Color(0xFF3A2A0A), Color(0xFF6B4E10), Color(0xFF3A2A0A))
                    )
                )
                .border(1.5.dp, Color(0x88FFD54F), RoundedCornerShape(18.dp))
                .padding(20.dp),
        ) {
            Column {
                Text(
                    text = "💎 TOTAL GROUP BALANCE",
                    fontSize = 12.sp,
                    color = GoldTitle,
                    letterSpacing = 1.2.sp,
                    fontWeight = FontWeight.Bold,
                )
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = "UGX ${formatMoney(totalConfirmed)}",
                    fontSize = 30.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Serif,
                    color = Color.White,
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "All-time confirmed contributions",
                    fontSize = 12.sp,
                    color = CreamText,
                )
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        Text(
            text = "BREAKDOWN",
            fontSize = 12.sp,
            color = GoldTitle,
            letterSpacing = 1.sp,
            fontWeight = FontWeight.Bold,
        )
        Spacer(modifier = Modifier.height(10.dp))

        SummaryRow("💵 Confirmed deposits", "UGX ${formatMoney(totalConfirmed)}", SuccessGreen)
        SummaryRow("⏳ Pending deposits", "UGX ${formatMoney(totalPending)}", PendingAmber)
        SummaryRow("⚠️ Total penalties", "UGX ${formatMoney(totalPenalties)}", ErrorRed)
        SummaryRow("✅ Cleared penalties", "UGX ${formatMoney(clearedPenalties)}", SuccessGreen)
        SummaryRow("🔴 Unpaid penalties", "UGX ${formatMoney(unpaidPenalties)}", ErrorRed)

        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = "ACTIVITY",
            fontSize = 12.sp,
            color = GoldTitle,
            letterSpacing = 1.sp,
            fontWeight = FontWeight.Bold,
        )
        Spacer(modifier = Modifier.height(10.dp))

        SummaryRow("Total deposits", "${deposits.size}", Color.White)
        SummaryRow("Confirmed", "${confirmed.size}", SuccessGreen)
        SummaryRow("Pending", "${pending.size}", PendingAmber)
        SummaryRow("Total penalties", "${penalties.size}", Color.White)

        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = "TIMELINE",
            fontSize = 12.sp,
            color = GoldTitle,
            letterSpacing = 1.sp,
            fontWeight = FontWeight.Bold,
        )
        Spacer(modifier = Modifier.height(10.dp))

        if (firstDate > 0L) {
            SummaryRow("First deposit", formatDate(firstDate), Color.White)
            SummaryRow("Latest deposit", formatDate(lastDate), Color.White)
        } else {
            SummaryRow("No activity yet", "—", CreamText)
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Composable
private fun SummaryRow(label: String, value: String, valueColor: Color) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 5.dp)
            .clip(RoundedCornerShape(10.dp))
            .background(Color(0x22FFFFFF))
            .padding(horizontal = 14.dp, vertical = 10.dp),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = label,
                fontSize = 13.sp,
                color = CreamText,
                modifier = Modifier.weight(1f),
            )
            Text(
                text = value,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = valueColor,
            )
        }
    }
}

// =====================================================
//                    MEETINGS SCREEN
// =====================================================
private enum class MeetingsTab(val label: String) {
    UPCOMING("Upcoming"),
    PAST("Past"),
    ALL("All"),
}

@Composable
private fun MeetingsScreen(
    meetings: List<FireMeeting>,
    myRole: String,
    myUid: String,
    onBack: () -> Unit,
) {
    val isAdmin = myRole == "admin" || myRole == "treasurer"
    var selectedTab by remember { mutableStateOf(MeetingsTab.UPCOMING) }
    var selectedMeetingId by remember { mutableStateOf<String?>(null) }
    var showCreateDialog by remember { mutableStateOf(false) }
    val now = System.currentTimeMillis()
    val upcoming = meetings.filter { it.date > now && it.status == "scheduled" }.sortedBy { it.date }
    val past = meetings.filter { it.date <= now || it.status == "completed" }.sortedByDescending { it.date }
    val visible = when (selectedTab) {
        MeetingsTab.UPCOMING -> upcoming
        MeetingsTab.PAST -> past
        MeetingsTab.ALL -> meetings.sortedByDescending { it.date }
    }
    val selectedMeeting = meetings.firstOrNull { it.id == selectedMeetingId }

    if (selectedMeeting != null) {
        MeetingDetailScreen(
            meeting = selectedMeeting,
            myRole = myRole,
            myUid = myUid,
            onBack = { selectedMeetingId = null },
        )
        return
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp, vertical = 16.dp),
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onBack, modifier = Modifier.size(36.dp)) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = Color.White,
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text(
                        text = "MEETINGS",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Serif,
                        color = GoldTitle,
                    )
                    Text(
                        text = "CITY ALUMNI SAVING GROUP",
                        fontSize = 11.sp,
                        color = CreamText,
                        letterSpacing = 1.sp,
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                MeetingCountCard(Modifier.weight(1f), "UPCOMING", upcoming.size)
                MeetingCountCard(Modifier.weight(1f), "PAST", past.size)
            }
            Spacer(modifier = Modifier.height(14.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                MeetingsTab.values().forEach { tab ->
                    val selected = selectedTab == tab
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(12.dp))
                            .background(if (selected) Color(0x66FFD54F) else Color(0x33FFFFFF))
                            .border(1.dp, if (selected) GoldSoft else Color(0x44FFFFFF), RoundedCornerShape(12.dp))
                            .clickable { selectedTab = tab }
                            .padding(vertical = 12.dp),
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(
                            text = tab.label,
                            fontSize = 13.sp,
                            fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal,
                            color = if (selected) GoldSoft else CreamText,
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(14.dp))

            if (visible.isEmpty()) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(text = "📅", fontSize = 42.sp)
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = when (selectedTab) {
                                MeetingsTab.UPCOMING -> "No upcoming meetings"
                                MeetingsTab.PAST -> "No past meetings"
                                MeetingsTab.ALL -> "No meetings scheduled"
                            },
                            fontSize = 15.sp,
                            color = CreamText,
                        )
                        if (isAdmin) {
                            Spacer(modifier = Modifier.height(6.dp))
                            Text("Tap + to schedule one", fontSize = 12.sp, color = Color(0x88FFFFFF))
                        }
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    items(visible) { meeting ->
                        MeetingCard(meeting, myUid) { selectedMeetingId = meeting.id }
                    }
                    item { Spacer(modifier = Modifier.height(80.dp)) }
                }
            }
        }

        if (isAdmin) {
            FloatingActionButton(
                onClick = { showCreateDialog = true },
                containerColor = GoldSoft,
                contentColor = Color(0xFF1A1228),
                modifier = Modifier.align(Alignment.BottomEnd).padding(20.dp),
            ) {
                Icon(Icons.Filled.Add, contentDescription = "Schedule meeting")
            }
        }
    }

    if (showCreateDialog) CreateMeetingDialog { showCreateDialog = false }
}

@Composable
private fun MeetingCountCard(modifier: Modifier, label: String, count: Int) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(14.dp))
            .background(Color(0x33FFFFFF))
            .border(1.dp, Color(0x44FFD54F), RoundedCornerShape(14.dp))
            .padding(14.dp),
    ) {
        Column {
            Text(label, fontSize = 11.sp, color = GoldTitle, letterSpacing = 1.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(6.dp))
            Text(count.toString(), fontSize = 22.sp, fontWeight = FontWeight.Bold, color = Color.White)
        }
    }
}

@Composable
private fun MeetingCard(meeting: FireMeeting, myUid: String, onClick: () -> Unit) {
    val isUpcoming = meeting.date > System.currentTimeMillis() && meeting.status == "scheduled"
    val present = meeting.attendance.values.count { it == "present" }
    val absent = meeting.attendance.values.count { it == "absent" }
    val pending = meeting.attendance.values.count { it == "pending" }
    val myStatus = meeting.attendance[myUid] ?: "pending"

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(Color(0x33FFFFFF))
            .border(if (isUpcoming) 1.5.dp else 1.dp, if (isUpcoming) GoldSoft else Color(0x33FFD54F), RoundedCornerShape(16.dp))
            .clickable { onClick() }
            .padding(16.dp),
    ) {
        Column {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = if (isUpcoming) Icons.Filled.Event else Icons.Filled.CalendarMonth,
                    contentDescription = null,
                    tint = GoldSoft,
                    modifier = Modifier.size(22.dp),
                )
                Spacer(modifier = Modifier.width(10.dp))
                Column(Modifier.weight(1f)) {
                    Text(meeting.title.ifBlank { "Meeting" }, fontSize = 15.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(formatMeetingDateTime(meeting.date), fontSize = 12.sp, color = if (isUpcoming) GoldSoft else CreamText)
                }
                if (meeting.status == "completed") {
                    Text("DONE", fontSize = 10.sp, color = SuccessGreen, fontWeight = FontWeight.Bold)
                } else if (meeting.status == "cancelled") {
                    Text("CANCELLED", fontSize = 10.sp, color = ErrorRed, fontWeight = FontWeight.Bold)
                }
            }
            Spacer(modifier = Modifier.height(10.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                if (meeting.location.isNotBlank()) {
                    Icon(Icons.Filled.LocationOn, contentDescription = null, tint = CreamText, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(meeting.location, fontSize = 11.sp, color = CreamText)
                    Spacer(modifier = Modifier.width(12.dp))
                }
                Icon(Icons.Filled.People, contentDescription = null, tint = CreamText, modifier = Modifier.size(14.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("${present + absent + pending} invited", fontSize = 11.sp, color = CreamText)
            }
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = if (isUpcoming) {
                    when (myStatus) {
                        "present" -> "✅  YOU ARE ATTENDING"
                        "absent" -> "❌  YOU MARKED ABSENT"
                        else -> "RSVP: OPEN MEETING"
                    }
                } else {
                    "✅ $present present  ·  ❌ $absent absent  ·  ⏳ $pending pending"
                },
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = when (myStatus) {
                    "present" -> SuccessGreen
                    "absent" -> ErrorRed
                    else -> if (isUpcoming) GoldSoft else CreamText
                },
            )
        }
    }
}

@Composable
private fun MeetingDetailScreen(
    meeting: FireMeeting,
    myRole: String,
    myUid: String,
    onBack: () -> Unit,
) {
    val context = LocalContext.current
    val isAdmin = myRole == "admin" || myRole == "treasurer"
    val isUpcoming = meeting.date > System.currentTimeMillis() && meeting.status == "scheduled"
    val myStatus = meeting.attendance[myUid] ?: "pending"
    var members by remember { mutableStateOf<List<UserProfile>>(emptyList()) }
    var minutesDraft by remember(meeting.id) { mutableStateOf(meeting.minutes) }
    var isEditingMinutes by remember { mutableStateOf(false) }
    var confirmDelete by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        db.collection("users").addSnapshotListener { snapshot, error ->
            if (error == null && snapshot != null) {
                members = snapshot.documents.mapNotNull { doc ->
                    doc.toObject(UserProfile::class.java)?.let { it.copy(uid = it.uid.ifBlank { doc.id }) }
                }
            }
        }
    }

    Column(
        modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(20.dp),
    ) {
        IconButton(onClick = onBack, modifier = Modifier.size(36.dp)) {
            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
        }
        Spacer(modifier = Modifier.height(12.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(18.dp))
                .background(Brush.linearGradient(listOf(Color(0xFF3A2A0A), Color(0xFF6B4E10), Color(0xFF3A2A0A))))
                .border(1.5.dp, Color(0x88FFD54F), RoundedCornerShape(18.dp))
                .padding(20.dp),
        ) {
            Column {
                Icon(Icons.Filled.Event, contentDescription = null, tint = GoldTitle, modifier = Modifier.size(30.dp))
                Spacer(modifier = Modifier.height(10.dp))
                Text(meeting.title.ifBlank { "Meeting" }, fontSize = 22.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Serif, color = GoldTitle)
                Spacer(modifier = Modifier.height(6.dp))
                Text(formatMeetingDateTime(meeting.date), fontSize = 15.sp, color = Color.White)
                Text("Duration: ${meeting.durationMinutes} min", fontSize = 12.sp, color = CreamText)
                if (meeting.location.isNotBlank()) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Filled.LocationOn, contentDescription = null, tint = GoldSoft, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(meeting.location, fontSize = 12.sp, color = GoldSoft)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))
        if (isUpcoming && myUid.isNotBlank()) {
            MeetingSectionTitle("YOUR RSVP")
            Spacer(modifier = Modifier.height(8.dp))
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                MeetingActionButton(Modifier.weight(1f), "✅  Attending", SuccessGreen, myStatus == "present") {
                    db.collection("meetings").document(meeting.id).update("attendance.$myUid", "present")
                }
                MeetingActionButton(Modifier.weight(1f), "❌  Can't attend", ErrorRed, myStatus == "absent") {
                    db.collection("meetings").document(meeting.id).update("attendance.$myUid", "absent")
                }
            }
            Spacer(modifier = Modifier.height(20.dp))
        }

        if (meeting.agenda.isNotBlank()) {
            MeetingSectionTitle("AGENDA")
            Spacer(modifier = Modifier.height(8.dp))
            MeetingTextCard(meeting.agenda)
            Spacer(modifier = Modifier.height(20.dp))
        }

        MeetingSectionTitle("ATTENDANCE (${members.size})")
        Spacer(modifier = Modifier.height(8.dp))
        members.forEach { member ->
            val status = meeting.attendance[member.uid] ?: "pending"
            AttendanceRow(
                memberName = member.fullName.ifBlank { "Unnamed" },
                status = status,
                canEdit = isAdmin && isUpcoming,
            ) {
                val next = when (status) {
                    "pending" -> "present"
                    "present" -> "absent"
                    else -> "pending"
                }
                db.collection("meetings").document(meeting.id).update("attendance.${member.uid}", next)
            }
            Spacer(modifier = Modifier.height(8.dp))
        }

        Spacer(modifier = Modifier.height(20.dp))
        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            MeetingSectionTitle("MINUTES", Modifier.weight(1f))
            if (isAdmin && !isUpcoming) {
                Text(
                    if (isEditingMinutes) "Cancel" else "Edit",
                    fontSize = 12.sp,
                    color = BlueLink,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.clickable {
                        if (isEditingMinutes) minutesDraft = meeting.minutes
                        isEditingMinutes = !isEditingMinutes
                    },
                )
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
        if (isEditingMinutes) {
            OutlinedTextField(
                value = minutesDraft,
                onValueChange = { minutesDraft = it },
                modifier = Modifier.fillMaxWidth().height(180.dp),
                shape = RoundedCornerShape(12.dp),
                placeholder = { Text("Enter meeting minutes, decisions, and action items...", color = Color(0x88FFFFFF)) },
                colors = meetingTextFieldColors(),
            )
            Spacer(modifier = Modifier.height(10.dp))
            GradientBlueButton("SAVE MINUTES") {
                db.collection("meetings").document(meeting.id).update("minutes", minutesDraft)
                isEditingMinutes = false
            }
        } else {
            MeetingTextCard(
                meeting.minutes.ifBlank {
                    if (isAdmin && !isUpcoming) "No minutes recorded yet. Tap Edit to add them."
                    else "Minutes will be recorded after the meeting."
                },
                if (meeting.minutes.isBlank()) CreamText else Color.White,
            )
        }

        Spacer(modifier = Modifier.height(20.dp))
        if (isAdmin) {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                if (isUpcoming) {
                    MeetingActionButton(Modifier.weight(1f), "✅  MARK DONE", SuccessGreen) {
                        db.collection("meetings").document(meeting.id).update("status", "completed")
                    }
                }
                MeetingActionButton(Modifier.weight(1f), "DELETE", ErrorRed) { confirmDelete = true }
            }
            Spacer(modifier = Modifier.height(14.dp))
        }
        MeetingActionButton(Modifier.fillMaxWidth(), "SHARE MEETING DETAILS", GoldSoft) {
            val text = buildString {
                appendLine(meeting.title)
                appendLine(formatMeetingDateTime(meeting.date))
                if (meeting.location.isNotBlank()) appendLine("Location: ${meeting.location}")
                if (meeting.agenda.isNotBlank()) append("Agenda:\n${meeting.agenda}")
            }
            val intent = android.content.Intent(android.content.Intent.ACTION_SEND).apply {
                type = "text/plain"
                putExtra(android.content.Intent.EXTRA_TEXT, text)
            }
            context.startActivity(android.content.Intent.createChooser(intent, "Share meeting"))
        }
        Spacer(modifier = Modifier.height(30.dp))
    }

    if (confirmDelete) {
        AlertDialog(
            onDismissRequest = { confirmDelete = false },
            containerColor = Color(0xFF1A1228),
            titleContentColor = GoldTitle,
            textContentColor = CreamText,
            title = { Text("Delete meeting?") },
            text = { Text("This will permanently delete \"${meeting.title}\" and its attendance records.") },
            confirmButton = {
                TextButton(onClick = {
                    db.collection("meetings").document(meeting.id).delete()
                    confirmDelete = false
                    onBack()
                }) { Text("DELETE", color = ErrorRed, fontWeight = FontWeight.Bold) }
            },
            dismissButton = {
                TextButton(onClick = { confirmDelete = false }) { Text("CANCEL", color = GoldSoft) }
            },
        )
    }
}

@Composable
private fun MeetingSectionTitle(text: String, modifier: Modifier = Modifier) {
    Text(text, fontSize = 12.sp, color = GoldTitle, letterSpacing = 1.sp, fontWeight = FontWeight.Bold, modifier = modifier)
}

@Composable
private fun MeetingTextCard(text: String, textColor: Color = Color.White) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(Color(0x22FFFFFF))
            .border(1.dp, Color(0x33FFD54F), RoundedCornerShape(12.dp))
            .padding(14.dp),
    ) {
        Text(text, fontSize = 13.sp, color = textColor, lineHeight = 20.sp)
    }
}

@Composable
private fun MeetingActionButton(
    modifier: Modifier,
    label: String,
    color: Color,
    selected: Boolean = false,
    onClick: () -> Unit,
) {
    Box(
        modifier = modifier
            .height(48.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(if (selected) color.copy(alpha = 0.25f) else Color(0x33FFFFFF))
            .border(1.dp, color, RoundedCornerShape(12.dp))
            .clickable { onClick() }
            .padding(horizontal = 8.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(label, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = color, textAlign = TextAlign.Center)
    }
}

@Composable
private fun AttendanceRow(
    memberName: String,
    status: String,
    canEdit: Boolean,
    onCycle: () -> Unit,
) {
    val (emoji, label, color) = when (status) {
        "present" -> Triple("✅", "Present", SuccessGreen)
        "absent" -> Triple("❌", "Absent", ErrorRed)
        else -> Triple("⏳", "Pending", PendingAmber)
    }
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .background(Color(0x22FFFFFF))
            .border(1.dp, Color(0x22FFD54F), RoundedCornerShape(10.dp))
            .then(if (canEdit) Modifier.clickable { onCycle() } else Modifier)
            .padding(horizontal = 12.dp, vertical = 10.dp),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(emoji, fontSize = 16.sp)
            Spacer(modifier = Modifier.width(10.dp))
            Text(memberName, fontSize = 13.sp, color = Color.White, modifier = Modifier.weight(1f))
            Text(label, fontSize = 11.sp, color = color, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
private fun CreateMeetingDialog(onDismiss: () -> Unit) {
    val context = LocalContext.current
    var title by remember { mutableStateOf("Weekly Savings Meeting") }
    var dateMillis by remember { mutableStateOf(System.currentTimeMillis() + 86_400_000L) }
    var duration by remember { mutableStateOf("120") }
    var location by remember { mutableStateOf("") }
    var agenda by remember {
        mutableStateOf("1. Opening prayer\n2. Savings contributions\n3. Loans & penalties\n4. AOB")
    }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var isSaving by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = { if (!isSaving) onDismiss() },
        containerColor = Color(0xFF1A1228),
        titleContentColor = GoldTitle,
        textContentColor = CreamText,
        title = { Text("Schedule Meeting", fontFamily = FontFamily.Serif, fontWeight = FontWeight.Bold) },
        text = {
            Column(Modifier.fillMaxWidth().verticalScroll(rememberScrollState())) {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it; errorMessage = null },
                    label = { Text("Title") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp),
                    colors = meetingTextFieldColors(),
                )
                Spacer(modifier = Modifier.height(10.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    MeetingPickerButton(Modifier.weight(1f), "Pick date", Icons.Filled.Event) {
                        val cal = Calendar.getInstance().apply { timeInMillis = dateMillis }
                        DatePickerDialog(
                            context,
                            { _, year, month, day ->
                                dateMillis = Calendar.getInstance().apply {
                                    timeInMillis = dateMillis
                                    set(Calendar.YEAR, year)
                                    set(Calendar.MONTH, month)
                                    set(Calendar.DAY_OF_MONTH, day)
                                }.timeInMillis
                            },
                            cal.get(Calendar.YEAR),
                            cal.get(Calendar.MONTH),
                            cal.get(Calendar.DAY_OF_MONTH),
                        ).show()
                    }
                    MeetingPickerButton(Modifier.weight(1f), "Pick time", Icons.Filled.Schedule) {
                        val cal = Calendar.getInstance().apply { timeInMillis = dateMillis }
                        TimePickerDialog(
                            context,
                            { _, hour, minute ->
                                dateMillis = Calendar.getInstance().apply {
                                    timeInMillis = dateMillis
                                    set(Calendar.HOUR_OF_DAY, hour)
                                    set(Calendar.MINUTE, minute)
                                }.timeInMillis
                            },
                            cal.get(Calendar.HOUR_OF_DAY),
                            cal.get(Calendar.MINUTE),
                            false,
                        ).show()
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text(formatMeetingDateTime(dateMillis), fontSize = 12.sp, color = Color.White, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(10.dp))
                OutlinedTextField(
                    value = duration,
                    onValueChange = { duration = it.filter(Char::isDigit) },
                    label = { Text("Duration (minutes)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp),
                    colors = meetingTextFieldColors(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                )
                Spacer(modifier = Modifier.height(10.dp))
                OutlinedTextField(
                    value = location,
                    onValueChange = { location = it },
                    label = { Text("Location (optional)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp),
                    colors = meetingTextFieldColors(),
                )
                Spacer(modifier = Modifier.height(10.dp))
                OutlinedTextField(
                    value = agenda,
                    onValueChange = { agenda = it },
                    label = { Text("Agenda") },
                    modifier = Modifier.fillMaxWidth().height(140.dp),
                    shape = RoundedCornerShape(10.dp),
                    colors = meetingTextFieldColors(),
                )
                if (errorMessage != null) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(errorMessage.orEmpty(), color = ErrorRed, fontSize = 12.sp)
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    when {
                        title.isBlank() -> errorMessage = "Enter a title"
                        dateMillis <= System.currentTimeMillis() -> errorMessage = "Pick a future date"
                        duration.toIntOrNull() == null || duration.toInt() <= 0 ->
                            errorMessage = "Enter a valid meeting duration"
                        else -> {
                            errorMessage = null
                            isSaving = true
                        }
                    }
                },
                enabled = !isSaving,
            ) {
                Text(if (isSaving) "Saving..." else "SCHEDULE", color = GoldSoft, fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss, enabled = !isSaving) { Text("CANCEL", color = CreamText) }
        },
    )

    if (isSaving) {
        LaunchedEffect(Unit) {
            try {
                val users = db.collection("users").get().await()
                val attendance = users.documents.associate { it.id to "pending" }
                db.collection("meetings").add(
                    FireMeeting(
                        title = title.trim(),
                        date = dateMillis,
                        durationMinutes = duration.toInt(),
                        location = location.trim(),
                        agenda = agenda.trim(),
                        createdBy = Session.currentUser?.uid.orEmpty(),
                        createdByName = Session.currentProfile?.fullName ?: "Admin",
                        createdAt = System.currentTimeMillis(),
                        attendance = attendance,
                    )
                ).await()
                isSaving = false
                onDismiss()
            } catch (e: Exception) {
                isSaving = false
                errorMessage = "Failed to schedule meeting: ${e.message}"
            }
        }
    }
}

@Composable
private fun MeetingPickerButton(
    modifier: Modifier,
    label: String,
    icon: ImageVector,
    onClick: () -> Unit,
) {
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(10.dp))
            .background(Color(0x33000000))
            .border(1.dp, GoldSoft, RoundedCornerShape(10.dp))
            .clickable { onClick() }
            .padding(horizontal = 8.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(icon, contentDescription = null, tint = GoldSoft, modifier = Modifier.size(16.dp))
        Spacer(modifier = Modifier.width(6.dp))
        Text(label, fontSize = 13.sp, color = GoldSoft, fontWeight = FontWeight.Bold)
    }
}

@Composable
private fun meetingTextFieldColors() = OutlinedTextFieldDefaults.colors(
    focusedContainerColor = Color(0x33000000),
    unfocusedContainerColor = Color(0x22000000),
    focusedBorderColor = GoldSoft,
    unfocusedBorderColor = Color(0x66FFFFFF),
    focusedLabelColor = GoldTitle,
    unfocusedLabelColor = Color(0xCCFFFFFF),
    focusedTextColor = Color.White,
    unfocusedTextColor = Color.White,
    cursorColor = GoldSoft,
)

private fun formatMeetingDateTime(millis: Long): String {
    if (millis == 0L) return "—"
    return SimpleDateFormat("EEE, dd MMM yyyy · HH:mm", Locale.getDefault()).format(Date(millis))
}

// =====================================================
//                     PREVIEWS
// =====================================================
@Preview(showBackground = true)
@Composable
fun WelcomeScreenPreview() {
    SavingsGroupTheme {
        WelcomeScreen(onSignInClick = {}, onCreateAccountClick = {})
    }
}

@Preview(showBackground = true)
@Composable
fun LoginScreenPreview() {
    SavingsGroupTheme {
        LoginScreen(onBackClick = {}, onSignInSuccess = {}, onRegisterClick = {})
    }
}

@Preview(showBackground = true)
@Composable
fun RegisterScreenPreview() {
    SavingsGroupTheme {
        RegisterScreen(onBackClick = {}, onRegisterSuccess = {}, onSignInClick = {})
    }
}