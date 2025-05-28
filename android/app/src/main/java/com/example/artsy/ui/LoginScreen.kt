package com.example.artsy.ui

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.*
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.artsy.viewmodel.LoginViewModel
import com.example.artsy.viewmodel.SessionViewModel
import com.example.artsy.viewmodel.LoginViewModelFactory
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    navController: NavController,
    sessionViewModel: SessionViewModel
) {
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    val viewModel: LoginViewModel = viewModel(factory = LoginViewModelFactory(sessionViewModel))

    val isLoading by viewModel.isLoading.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()
    val loginSuccess by viewModel.loginSuccess.collectAsState()

    val context = LocalContext.current

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    var emailError by remember { mutableStateOf<String?>(null) }
    var passwordError by remember { mutableStateOf<String?>(null) }

    var emailWasFocused by remember { mutableStateOf(false) }
    var passwordWasFocused by remember { mutableStateOf(false) }

    LaunchedEffect(loginSuccess) {
        if (loginSuccess) {
            coroutineScope.launch {
                snackbarHostState.showSnackbar("Logged in successfully")
            }
            navController.navigate("home") {
                popUpTo("login") { inclusive = true }
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Login", color = MaterialTheme.colorScheme.onPrimary) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(24.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.Center
        ) {
            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Email") },
                isError = emailError != null,
                modifier = Modifier
                    .fillMaxWidth()
                    .onFocusChanged {
                        if (it.isFocused) {
                            emailWasFocused = true
                            viewModel.clearAuthError()
                        } else if (emailWasFocused) {
                            emailError = when {
                                email.isBlank() -> "Email cannot be empty"
                                !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches() -> "Invalid email format"
                                else -> null
                            }
                        }
                    },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
            )
            if (emailError != null) {
                Text(emailError!!, color = MaterialTheme.colorScheme.error)
            }

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Password") },
                isError = passwordError != null,
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier
                    .fillMaxWidth()
                    .onFocusChanged {
                        if (it.isFocused) {
                            passwordWasFocused = true
                            viewModel.clearAuthError()
                        } else if (passwordWasFocused) {
                            passwordError = if (password.isBlank()) "Password cannot be empty" else null
                        }
                    },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
            )
            if (passwordError != null) {
                Text(passwordError!!, color = MaterialTheme.colorScheme.error)
            }

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {
                    emailError = when {
                        email.isBlank() -> "Email cannot be empty"
                        !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches() -> "Invalid email format"
                        else -> null
                    }

                    passwordError = if (password.isBlank()) "Password cannot be empty" else null

                    if (emailError == null && passwordError == null) {
                        viewModel.login(email, password)
                    }
                },
                enabled = !isLoading,
                modifier = Modifier.fillMaxWidth()
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        color = MaterialTheme.colorScheme.onPrimary,
                        strokeWidth = 2.dp,
                        modifier = Modifier.size(18.dp)
                    )
                } else {
                    Text("Login")
                }
            }

            if (errorMessage != null) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(errorMessage!!, color = MaterialTheme.colorScheme.error)
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Don't have an account yet?", color = MaterialTheme.colorScheme.onSurface)
                TextButton(
                    onClick = { navController.navigate("register") },
                    contentPadding = PaddingValues(start = 4.dp)
                ) {
                    Text(
                        text = "Register",
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
    }
}
