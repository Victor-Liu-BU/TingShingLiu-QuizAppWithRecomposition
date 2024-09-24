package com.example.tingshingliu_quizappwithrecomposition

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.tingshingliu_quizappwithrecomposition.ui.theme.TingShingLiuQuizAppWithRecompositionTheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            TingShingLiuQuizAppWithRecompositionTheme {
                QuizApp()

            }
        }
    }
}

@Composable
fun QuizApp() {
    val questions = listOf(
        "What is the capital of UK?" to "London",
        "What is 2 + 5?" to "7",
        "What is the largest planet in our solar system?" to "Jupiter",
        "What CS course does this project belong?" to "CS501",
        "Is water wet? (Yes/No)" to "Yes"
    )
    var currentQuestionIndex by remember { mutableStateOf(0) }
    var userAnswer by remember { mutableStateOf("") }
    var quizComplete by remember { mutableStateOf(false) }
    var incorrectAttempts by remember { mutableStateOf(0) }
    val coroutineScope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        content = { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(16.dp)
            ) {
                if (quizComplete) {
                    Text("Quiz Complete!")
                    Spacer(modifier = Modifier.height(8.dp))
                    Button(onClick = {
                        currentQuestionIndex = 0
                        quizComplete = false
                        userAnswer = ""
                        incorrectAttempts = 0
                    }) {
                        Text("Restart Quiz")
                    }
                } else {
                    Card(modifier = Modifier.fillMaxWidth()) {
                        Text(
                            text = questions[currentQuestionIndex].first,
                            modifier = Modifier.padding(16.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    TextField(
                        value = userAnswer,
                        onValueChange = { userAnswer = it },
                        label = { Text("Your Answer") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Button(
                        onClick = {
                            val correctAnswer = questions[currentQuestionIndex].second
                            if (userAnswer.equals(correctAnswer, ignoreCase = true)) {
                                incorrectAttempts = 0 // Reset incorrect attempts
                                if (currentQuestionIndex < questions.size - 1) {
                                    coroutineScope.launch {
                                        showSnackbar(snackbarHostState, "Correct!")
                                    }
                                    currentQuestionIndex++
                                    userAnswer = ""
                                } else {
                                    coroutineScope.launch {
                                        showSnackbar(snackbarHostState, "Quiz Complete!")
                                    }
                                    quizComplete = true
                                }
                            } else {
                                incorrectAttempts++
                                if (incorrectAttempts >= 3) {
                                    coroutineScope.launch {
                                        showSnackbar(snackbarHostState, "Moving to next question after 3 incorrect attempts.")
                                    }
                                    if (currentQuestionIndex < questions.size - 1) {
                                        currentQuestionIndex++
                                        userAnswer = ""
                                        incorrectAttempts = 0
                                    } else {
                                        quizComplete = true
                                    }
                                } else {
                                    coroutineScope.launch {
                                        showSnackbar(snackbarHostState, "Incorrect. Try again. Attempts: $incorrectAttempts/3")
                                    }
                                }
                            }
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Submit Answer")
                    }
                }
            }
        }
    )
}
suspend fun showSnackbar(snackbarHostState: SnackbarHostState, message: String) {
    snackbarHostState.currentSnackbarData?.dismiss()
    snackbarHostState.showSnackbar(message)
}
@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    TingShingLiuQuizAppWithRecompositionTheme {
        QuizApp()
    }
}