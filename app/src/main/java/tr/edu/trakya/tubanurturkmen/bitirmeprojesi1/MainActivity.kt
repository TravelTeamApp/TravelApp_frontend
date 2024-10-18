package tr.edu.trakya.tubanurturkmen.bitirmeprojesi1

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import tr.edu.trakya.tubanurturkmen.bitirmeprojesi1.ui.theme.BitirmeProjesi1Theme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            BitirmeProjesi1Theme {
                LoginScreen()
            }
        }
    }
}

@Composable
fun LoginScreen() {
    val email = remember { mutableStateOf("") }
    val password = remember { mutableStateOf("") }
    val context = LocalContext.current
    var isPasswordVisible by remember { mutableStateOf(false) }
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = "Hoşgeldiniz", style = androidx.compose.material3.MaterialTheme.typography.headlineLarge)

            Spacer(modifier = Modifier.height(32.dp))

            TextField(
                value = email.value,
                onValueChange = { email.value = it },
                label = { Text("Email") },
                keyboardOptions = KeyboardOptions.Default.copy(
                    keyboardType = KeyboardType.Email,
                    imeAction = ImeAction.Next
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            TextField(
                value = password.value,
                onValueChange = { password.value = it },
                label = { Text("Şifre") },
                keyboardOptions = KeyboardOptions.Default.copy(
                    keyboardType = KeyboardType.Password,
                    imeAction = ImeAction.Done
                ),
                visualTransformation = if (isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                trailingIcon = {
                    Icon(
                        imageVector = if (isPasswordVisible) {
                            ImageVector.vectorResource(id = R.drawable.baseline_visibility_24)
                        } else {
                            ImageVector.vectorResource(id = R.drawable.baseline_visibility_off_24)
                        },
                        contentDescription = if (isPasswordVisible) "Hide password" else "Show password",
                        modifier = Modifier
                            .clickable { isPasswordVisible = !isPasswordVisible }
                    )
                }
            )

            Spacer(modifier = Modifier.height(32.dp))

            Button(onClick = {
                if (validateLogin(email.value, password.value)) {
                    Toast.makeText(context, "Giriş Başarılı", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(context, "Hatalı Giriş", Toast.LENGTH_SHORT).show()
                }
            }) {
                Text(text = "Giriş")
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Hesabın yok mu? Hemen Kaydol",
                color = androidx.compose.material3.MaterialTheme.colorScheme.primary,
                modifier = Modifier.clickable { /* Kayıt olma ekranına yönlendirme */ }
            )
        }
    }
}

private fun validateLogin(email: String, password: String): Boolean {
    return email == "test@gmail.com" && password == "password123"
}

@Preview(showBackground = true)
@Composable
fun PreviewLoginScreen() {
    BitirmeProjesi1Theme {
        LoginScreen()
    }
}
