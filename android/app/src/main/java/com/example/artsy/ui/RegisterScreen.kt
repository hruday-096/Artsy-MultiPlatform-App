package com.example.artsy.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.text.input.*
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.artsy.viewmodel.RegisterViewModel
import com.example.artsy.viewmodel.SessionViewModel
import com.example.artsy.viewmodel.LoginViewModel
import com.example.artsy.viewmodel.LoginViewModelFactory
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterScreen(navController: NavController, sessionViewModel: SessionViewModel) {
    val loginViewModel: LoginViewModel = viewModel(factory = LoginViewModelFactory(sessionViewModel))
    val viewModel: RegisterViewModel = viewModel()
    val isLoading by viewModel.isLoading.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()
    val registerSuccess by viewModel.registerSuccess.collectAsState()

    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    var fullname by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    var fullnameError by remember { mutableStateOf<String?>(null) }
    var emailError by remember { mutableStateOf<String?>(null) }
    var passwordError by remember { mutableStateOf<String?>(null) }

    var fullnameTouched by remember { mutableStateOf(false) }
    var emailTouched by remember { mutableStateOf(false) }
    var passwordTouched by remember { mutableStateOf(false) }

    LaunchedEffect(registerSuccess) {
        if (registerSuccess) {
            loginViewModel.login(email, password)
            navController.navigate("home") {
                popUpTo("register") { inclusive = true }
            }
            coroutineScope.launch {
                snackbarHostState.showSnackbar("Registered successfully")
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Register", color = MaterialTheme.colorScheme.onPrimary) },
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
                value = fullname,
                onValueChange = {
                    fullname = it
                    if (fullnameTouched) {
                        fullnameError = null
                    }
                },
                label = { Text("Enter full Name") },
                isError = fullnameError != null,
                modifier = Modifier
                    .fillMaxWidth()
                    .onFocusChanged {
                        if (it.isFocused) {
                            fullnameTouched = true
                            viewModel.clearAuthError()
                        } else if (fullnameTouched) {
                            fullnameError = if (fullname.isBlank()) "Full name required" else null
                        }
                    }
            )
            if (fullnameError != null) {
                Text(fullnameError!!, color = MaterialTheme.colorScheme.error)
            }

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = email,
                onValueChange = {
                    email = it
                    if (emailTouched) {
                        emailError = null
                    }
                },
                label = { Text("Enter email") },
                isError = emailError != null,
                modifier = Modifier
                    .fillMaxWidth()
                    .onFocusChanged {
                        if (it.isFocused) {
                            emailTouched = true
                            viewModel.clearAuthError()
                        } else if (emailTouched) {
                            emailError = when {
                                email.isBlank() -> "Email required"
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
                onValueChange = {
                    password = it
                    if (passwordTouched) {
                        passwordError = null
                    }
                },
                label = { Text("Password") },
                isError = passwordError != null,
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier
                    .fillMaxWidth()
                    .onFocusChanged {
                        if (it.isFocused) {
                            passwordTouched = true
                            viewModel.clearAuthError()
                        } else if (passwordTouched) {
                            passwordError = if (password.length < 4) "Password too short" else null
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
                    fullnameError = if (fullname.isBlank()) "Full name required" else null
                    emailError = when {
                        email.isBlank() -> "Email required"
                        !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches() -> "Invalid email format"
                        else -> null
                    }
                    passwordError = if (password.length < 4) "Password too short" else null

                    if (fullnameError == null && emailError == null && passwordError == null) {
                        viewModel.register(fullname, email, password)
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
                    Text("Register")
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
                Text(
                    "Already have an account?",
                    color = MaterialTheme.colorScheme.onSurface
                )
                TextButton(
                    onClick = { navController.navigate("login") },
                    contentPadding = PaddingValues(start = 4.dp)
                ) {
                    Text(
                        text = "Login",
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
    }
}
