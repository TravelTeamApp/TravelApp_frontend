package tr.edu.trakya.tubanurturkmen.bitirmeprojesi1.screens

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(navController: NavController) {
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
            Text(text = "Hoşgeldiniz", style = MaterialTheme.typography.headlineLarge)

            Spacer(modifier = Modifier.height(32.dp))

            // Email TextField
            TextField(
                value = email.value,
                onValueChange = { email.value = it },
                label = { Text("Email") },
                keyboardOptions = KeyboardOptions.Default.copy(
                    keyboardType = KeyboardType.Email,
                    imeAction = ImeAction.Next
                ),
                colors = TextFieldDefaults.textFieldColors(
                    containerColor = Color(0xFFF3E5F5), // Lavender-like background
                    cursorColor = Color(0xFF1A237E),
                    focusedIndicatorColor = Color(0xFF5C6BC0),
                    unfocusedIndicatorColor = Color(0xFF9FA8DA)
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp)) // Oval köşe ekleniyor
                    .border(1.dp, Color.Gray, RoundedCornerShape(16.dp)) // Kenarlık ekleniyor
            )

            Spacer(modifier = Modifier.height(16.dp))

// Password TextField
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
                        imageVector = if (isPasswordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff,
                        contentDescription = if (isPasswordVisible) "Hide password" else "Show password",
                        modifier = Modifier.clickable { isPasswordVisible = !isPasswordVisible }
                    )
                },
                colors = TextFieldDefaults.textFieldColors(
                    containerColor = Color(0xFFF3E5F5),
                    cursorColor = Color(0xFF1A237E),
                    focusedIndicatorColor = Color(0xFF5C6BC0),
                    unfocusedIndicatorColor = Color(0xFF9FA8DA)

                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp)) // Oval köşe ekleniyor
                    .border(1.dp, Color.Gray, RoundedCornerShape(16.dp)) // Kenarlık ekleniyor
            )



            Spacer(modifier = Modifier.height(32.dp))


            Button(
                onClick = {
                    if (validateLogin(email.value, password.value)) {
                        Toast.makeText(context, "Giriş Başarılı", Toast.LENGTH_SHORT).show()
                        navController.navigate("citySelector") // Navigate to CitySelector screen
                    } else {
                        Toast.makeText(context, "Hatalı Giriş", Toast.LENGTH_SHORT).show()
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF5C6BC0)) // Lavender-like button
            ) {
                Text(text = "Giriş", color = Color.White)
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Signup Link
            Text(
                text = "Hesabınız yok mu? Hemen Kaydol",
                color = Color(0xFF3949AB),
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.clickable { /* Navigate to signup screen */ }
            )

            Spacer(modifier = Modifier.height(16.dp))

           /* // Social Media Logins
            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(
                    painter = painterResource(id = R.drawable.google),
                    contentDescription = "Google",
                    modifier = Modifier.size(48.dp)
                )
                Image(
                    painter = painterResource(id = R.drawable.facebook),
                    contentDescription = "Facebook",
                    modifier = Modifier.size(48.dp)
                )
                Image(
                    painter = painterResource(id = R.drawable.apple),
                    contentDescription = "Apple",
                    modifier = Modifier.size(48.dp)
                )
            } */
        }
    }
}

private fun validateLogin(email: String, password: String): Boolean {
    return email == "test@gmail.com" && password == "password123"
}
