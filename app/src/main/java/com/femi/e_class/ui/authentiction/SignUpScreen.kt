package com.femi.e_class.ui.authentiction

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.ClickableText
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.*
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.femi.e_class.R
import com.femi.e_class.data.User
import com.femi.e_class.handleNetworkExceptions
import com.femi.e_class.navigation.Screen
import com.femi.e_class.presentation.SignUpFormEvent
import com.femi.e_class.viewmodels.AuthenticationViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SignUpScreen(
    navController: NavHostController,
    viewModel: AuthenticationViewModel,
) {
    val scrollState = rememberScrollState()
    val context = LocalContext.current
    val state = viewModel.signUpFormState
    var showPassword by remember { mutableStateOf(false) }
    var accountCreated by remember { mutableStateOf(false) }
    var loading by remember { mutableStateOf(false) }
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier
            .verticalScroll(scrollState)
            .fillMaxSize()
            .padding(30.dp, 60.dp, 30.dp, 30.dp),
    ) {
        LaunchedEffect(key1 = context) {
            viewModel.signUpFormEvents.collect { event ->
                when (event) {
                    is AuthenticationViewModel.FormEvent.Success -> {
                        viewModel.signUserUp(
                            User(
                                firstName = viewModel.signUpFormState.firstName,
                                lastName = viewModel.signUpFormState.lastName,
                                email = viewModel.signUpFormState.email,
                                matric = viewModel.signUpFormState.matric,
                                password = viewModel.signUpFormState.password
                            )
                        )
                    }
                }
            }
        }
        LaunchedEffect(key1 = viewModel.signUpEvents) {
            viewModel.signUpEvents.collect { event ->
                loading =
                    (event is AuthenticationViewModel.SignUpEvent.Loading)
                when (event) {
                    is AuthenticationViewModel.SignUpEvent.Success -> {
                        accountCreated = true
                    }
                    is AuthenticationViewModel.SignUpEvent.Error -> {
                        context.handleNetworkExceptions(
                            exception = event.exception,
                            retry = {
                                viewModel.onEvent(SignUpFormEvent.Submit)
                            })
                    }
                    is AuthenticationViewModel.SignUpEvent.Loading -> {}
                }
            }
        }
        Text(
            modifier = Modifier
                .align(Alignment.Start),
            text = "Create your account",
            fontWeight = FontWeight.ExtraBold,
            fontSize = 24.sp,
            textAlign = TextAlign.Start
        )
        Spacer(modifier = Modifier.height(1.dp))
        Text(
            modifier = Modifier
                .align(Alignment.Start),
            text = "Enter your details to get started",
            fontWeight = FontWeight.Normal,
            fontSize = 14.sp,
            textAlign = TextAlign.Start
        )
        Spacer(modifier = Modifier.height(30.dp))
        OutlinedTextField(
            value = state.firstName,
            label = { Text(text = "First Name") },
            onValueChange = {
                viewModel.onEvent(SignUpFormEvent.FirstNameChanged(
                    it))
            },
            isError = state.firstNameError != null,
            modifier = Modifier.fillMaxWidth(),
            maxLines = 2,
            leadingIcon = {
                Icon(Icons.Filled.Person, "First Name Icon")
            },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Text,
                capitalization = KeyboardCapitalization.Words,
                autoCorrect = false,
                imeAction = ImeAction.Next
            )
        )
        if (state.firstNameError != null) {
            Text(
                text = state.firstNameError,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.align(Alignment.End)
            )
        }
        Spacer(modifier = Modifier.height(40.dp))
        OutlinedTextField(
            value = state.lastName,
            label = { Text(text = "Last Name") },
            onValueChange = {
                viewModel.onEvent(SignUpFormEvent.LastNameChanged(
                    it))
            },
            isError = state.lastNameError != null,
            modifier = Modifier.fillMaxWidth(),
            maxLines = 2,
            leadingIcon = {
                Icon(Icons.Filled.Person, "Last Name Icon")
            },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Text,
                capitalization = KeyboardCapitalization.Words,
                autoCorrect = false,
                imeAction = ImeAction.Next
            )
        )
        if (state.lastNameError != null) {
            Text(
                text = state.lastNameError,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.align(Alignment.End)
            )
        }
        Spacer(modifier = Modifier.height(40.dp))
        OutlinedTextField(
            value = state.matric,
            label = { Text(text = "Matric") },
            onValueChange = { newText ->
                if (newText.length > 9)
                    return@OutlinedTextField

                viewModel.onEvent(SignUpFormEvent.MatricChanged(
                    newText))
            },
            isError = state.matricError != null,
            modifier = Modifier.fillMaxWidth(),
            maxLines = 2,
            leadingIcon = {
                Icon(
                    painter = painterResource(id = R.drawable.ic_school),
                    contentDescription = "Matric Number Icon")
            },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.NumberPassword,
                capitalization = KeyboardCapitalization.None,
                autoCorrect = false,
                imeAction = ImeAction.Next
            )
        )
        if (state.matricError != null) {
            Text(
                text = state.matricError,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.align(Alignment.End)
            )
        }
        Spacer(modifier = Modifier.height(40.dp))
        OutlinedTextField(
            value = state.email,
            label = { Text(text = "Email") },
            onValueChange = {
                viewModel.onEvent(SignUpFormEvent.EmailChanged(it))
            },
            isError = state.emailError != null,
            modifier = Modifier.fillMaxWidth(),
            maxLines = 2,
            leadingIcon = {
                Icon(Icons.Filled.Email, "Email Icon")
            },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Email,
                capitalization = KeyboardCapitalization.None,
                autoCorrect = false,
                imeAction = ImeAction.Next
            )
        )
        if (state.emailError != null) {
            Text(
                text = state.emailError,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.align(Alignment.End)
            )
        }
        Spacer(modifier = Modifier.height(40.dp))
        OutlinedTextField(
            value = state.password,
            label = { Text(text = "Password") },
            onValueChange = {
                viewModel.onEvent(SignUpFormEvent.PasswordChanged(
                    it))
            },
            isError = state.passwordError != null,
            modifier = Modifier.fillMaxWidth(),
            maxLines = 2,
            visualTransformation = if (showPassword) {
                VisualTransformation.None
            } else {
                PasswordVisualTransformation()
            },
            trailingIcon = {
                if (showPassword) {
                    IconButton(onClick = { showPassword = false }) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_visibility),
                            contentDescription = "Show Password"
                        )
                    }
                } else {
                    IconButton(onClick = { showPassword = true }) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_visibility_off),
                            contentDescription = "Hide Password"
                        )
                    }
                }
            },
            leadingIcon = {
                Icon(Icons.Filled.Lock, "Password Icon")
            },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Password,
                capitalization = KeyboardCapitalization.None,
                autoCorrect = false,
                imeAction = ImeAction.Done
            )
        )
        if (state.passwordError != null) {
            Text(
                text = state.passwordError,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.align(Alignment.End)
            )
        }
        Spacer(modifier = Modifier.height(40.dp))
        if (loading) {
            CircularProgressIndicator()
            Spacer(modifier = Modifier.height(40.dp))
        }
        Button(
            onClick = {
                viewModel.onEvent(SignUpFormEvent.Submit)
            },
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .fillMaxWidth(),
            enabled = !loading
        ) {
            Text(text = "Sign Up")
        }
        Spacer(modifier = Modifier.height(40.dp))
        val annotatedText = buildAnnotatedString {
            pushStringAnnotation(tag = "Sign In Button",
                annotation = "Sign In")

            withStyle(
                style = SpanStyle(
                    color = MaterialTheme.colorScheme.onSurface,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 15.sp,
                ),
            ) {
                append("Already have an account? ")
            }

            withStyle(
                style = SpanStyle(
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 15.sp,
                ),
            ) {
                append("Sign In")
            }

            pop()
        }
        ClickableText(
            text = annotatedText,
            onClick = { offset ->
                annotatedText.getStringAnnotations(tag = "Sign In Button",
                    start = offset,
                    end = offset)
                    .firstOrNull()?.let {
                        navController.navigate(Screen.LogInScreen.route) {
                            popUpTo(Screen.OnBoardingScreen.route)
                            launchSingleTop = true
                        }
                    }
            }
        )
        if (accountCreated) {
            AlertDialog(
                confirmButton = {
                },
                modifier = Modifier
                    .fillMaxWidth(0.9f)
                    .padding(0.dp, 30.dp, 0.dp, 0.dp)
                    .align(Alignment.CenterHorizontally),
                text = {
                    Column(
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Image(modifier = Modifier
                            .fillMaxWidth()
                            .fillMaxHeight(0.4f),
                            painter = painterResource(id = R.drawable.leader_pana),
                            contentDescription = "Account Created Icon"
                        )
                        Text(
                            text = "Successful!",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center,
                            modifier = Modifier
                                .fillMaxWidth()
                        )
                        Text(
                            text = "Your account is ready to use. Click the Log In button below to get started",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Normal,
                            textAlign = TextAlign.Center,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(0.dp, 20.dp, 0.dp, 30.dp),
                        )
                        Button(
                            onClick = {
                                navController.navigate(
                                    Screen.LogInScreen.withArgs(
                                        Pair("email", viewModel.signUpFormState.email),
                                        Pair("password", viewModel.signUpFormState.password)
                                    )
                                ) {
                                    popUpTo(Screen.OnBoardingScreen.route)
                                    launchSingleTop = true
                                }
                            },
                            modifier = Modifier
                                .align(Alignment.CenterHorizontally)
                                .fillMaxWidth(),
                        ) {
                            Text(text = "Log In")
                        }
                    }
                },
                containerColor = MaterialTheme.colorScheme.surface,
                tonalElevation = 20.dp,
                onDismissRequest = {}
            )
        }
    }
}