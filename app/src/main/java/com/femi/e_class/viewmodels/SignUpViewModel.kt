package com.femi.e_class.viewmodels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewModelScope
import com.femi.e_class.data.User
import com.femi.e_class.domain.use_case.*
import com.femi.e_class.presentation.RegistrationFormEvent
import com.femi.e_class.presentation.RegistrationFormState
import com.femi.e_class.repositories.SignUpRepository
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

class SignUpViewModel(
    private val repository: SignUpRepository,
    private val validateFirstName: ValidateName = ValidateName(),
    private val validateLastName: ValidateName = ValidateName(),
    private val validateEmail: ValidateEmail = ValidateEmail(),
    private val validateMatric: ValidateMatric = ValidateMatric(),
    private val validatePassword: ValidatePassword = ValidatePassword(),
//    private val validateRepeatedPassword: ValidateRepeatedPassword = ValidateRepeatedPassword(),
) : BaseViewModel(repository) {

    var registrationFormState by mutableStateOf(RegistrationFormState())

    private val validationEventChannel = Channel<ValidationEvent>()
    val validationEvents = validationEventChannel.receiveAsFlow()

    private val registrationEventChannel = Channel<RegistrationEvent<Any?>>()
    val registrationEvents = registrationEventChannel.receiveAsFlow()

    fun onEvent(event: RegistrationFormEvent) {
        when (event) {
            is RegistrationFormEvent.FirstNameChanged -> {
                registrationFormState = registrationFormState.copy(firstName = event.firstName)
            }
            is RegistrationFormEvent.LastNameChanged -> {
                registrationFormState = registrationFormState.copy(lastName = event.lastName)
            }
            is RegistrationFormEvent.EmailChanged -> {
                registrationFormState = registrationFormState.copy(email = event.email)
            }
            is RegistrationFormEvent.MatricChanged -> {
                registrationFormState = registrationFormState.copy(matric = event.matric)
            }
            is RegistrationFormEvent.PasswordChanged -> {
                registrationFormState = registrationFormState.copy(password = event.password)
            }
//            is RegistrationFormEvent.RepeatedPasswordChanged -> {
//                registrationFormState =
//                    registrationFormState.copy(repeatedPassword = event.repeatedPassword)
//            }
            is RegistrationFormEvent.Submit -> {
                submitRegistrationData()
            }
        }
    }

    private fun submitRegistrationData() {
        val firstNameResult = validateFirstName.execute(registrationFormState.firstName)
        val lastNameResult = validateLastName.execute(registrationFormState.lastName)
        val emailResult = validateEmail.execute(registrationFormState.email)
        val matricResult = validateMatric.execute(registrationFormState.matric)
        val passwordResult = validatePassword.execute(registrationFormState.password)
//        val repeatedPasswordResult = validateRepeatedPassword.execute(
//            registrationFormState.password,
//            registrationFormState.repeatedPassword)

        val hasError = listOf(
            firstNameResult,
            lastNameResult,
            emailResult,
            matricResult,
            passwordResult,
//            repeatedPasswordResult
        ).any { !it.successful }

        registrationFormState = registrationFormState.copy(
            firstNameError = firstNameResult.errorMessage,
            lastNameError = lastNameResult.errorMessage,
            emailError = emailResult.errorMessage,
            matricError = matricResult.errorMessage,
            passwordError = passwordResult.errorMessage,
//            repeatedPasswordError = repeatedPasswordResult.errorMessage
        )

        if (hasError)
            return

        viewModelScope.launch {
            validationEventChannel.send(ValidationEvent.Success)
        }
    }

    fun signUpUser(user: User) {
        viewModelScope.launch {
            registrationEventChannel.send(RegistrationEvent.Loading)
        }
        repository.getAuthReference()
            .createUserWithEmailAndPassword(user.email, user.password)
            .addOnCompleteListener { addUserTask ->
                if (addUserTask.isSuccessful) {
                    viewModelScope.launch {
                        saveUser(
                            firstName = user.firstName,
                            lastName = user.lastName,
                            matric = user.matric,
                            email = user.email,
                            password = user.password
                        )
                    }
                } else {
                    viewModelScope.launch {
                        registrationEventChannel.send(RegistrationEvent.Error(addUserTask.exception))
                    }
                }
            }
    }

    private fun saveUser(
        firstName: String,
        lastName: String,
        matric: String,
        email: String,
        password: String
    ) {
        val userHashMap = hashMapOf(
            "FirstName" to firstName,
            "LastName" to lastName,
            "Matric" to matric,
            "Email" to email,
            "Password" to password)

        repository.getCollectionReference()
            .document(email)
            .set(userHashMap)
            .addOnSuccessListener {
                viewModelScope.launch {
                    registrationEventChannel.send(RegistrationEvent.Success)
                }
            }.addOnFailureListener {
                viewModelScope.launch {
                    registrationEventChannel.send(RegistrationEvent.Error(it))
                }
            }
    }


    sealed class ValidationEvent {
        object Success : ValidationEvent()
    }

    sealed class RegistrationEvent<out T> {
        object Success : RegistrationEvent<Nothing>()
        data class Error(val exception: java.lang.Exception?): RegistrationEvent<Nothing>()
        object Loading : RegistrationEvent<Nothing>()
    }
}