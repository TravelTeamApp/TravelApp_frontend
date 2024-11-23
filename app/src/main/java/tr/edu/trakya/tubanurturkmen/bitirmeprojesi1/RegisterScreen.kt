package tr.edu.trakya.tubanurturkmen.bitirmeprojesi1
import com.google.gson.stream.JsonReader
import java.io.StringReader
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import android.util.Log

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterScreen(navController: NavController) {
    // Durumlar
    val context = LocalContext.current
    val userName = remember { mutableStateOf("") }
    val email = remember { mutableStateOf("") }
    val password = remember { mutableStateOf("") }
    var isPasswordVisible by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(16.dp))
            .border(BorderStroke(1.dp, MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f)), RoundedCornerShape(16.dp))
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = "Kayıt Ol",
                style = androidx.compose.material3.MaterialTheme.typography.headlineLarge.copy(color = MaterialTheme.colorScheme.primary)
            )

            TextField(
                value = userName.value,
                onValueChange = { userName.value = it },
                label = { Text("Kullanıcı Adı") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(10.dp),
                keyboardOptions = KeyboardOptions.Default.copy(
                    keyboardType = KeyboardType.Text,
                    imeAction = ImeAction.Next
                )
            )

            TextField(
                value = email.value,
                onValueChange = { email.value = it },
                label = { Text("Email") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(10.dp),
                keyboardOptions = KeyboardOptions.Default.copy(
                    keyboardType = KeyboardType.Email,
                    imeAction = ImeAction.Next
                )
            )

            TextField(
                value = password.value,
                onValueChange = { password.value = it },
                label = { Text("Şifre") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(10.dp),
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

            Button(
                onClick = {
                    if (email.value.isNotEmpty() && password.value.isNotEmpty() && userName.value.isNotEmpty()) {
                        isLoading = true

                        val registerRequest = RegisterRequest(
                            userName = userName.value,
                            email = email.value,
                            password = password.value
                        )

                        RetrofitClient.apiService.register(registerRequest).enqueue(object : Callback<RegisterResponse> {
                            override fun onFailure(call: Call<RegisterResponse>, t: Throwable) {
                                isLoading = false
                                Log.e("RegisterError", "Bağlantı Hatası: ${t.message}")
                                Toast.makeText(context, "Bağlantı Hatası: ${t.message}", Toast.LENGTH_SHORT).show()
                        }

                            override fun onResponse(call: Call<RegisterResponse>, response: Response<RegisterResponse>) {
                                if (!response.isSuccessful) {
                                    val rawError = response.errorBody()?.string()
                                    Log.e("RegisterError", "Sunucudan Gelen Yanıt: $rawError")
                                    Toast.makeText(context, "Kayıt Hatası: $rawError", Toast.LENGTH_SHORT).show()

                                } else {
                                    Log.d("RegisterSuccess", "Kayıt başarılı: ${response.body()}")
                                    navController.navigate("home") // Home ekranına yönlendirme

                                }
                            }

                        })
                    } else {
                        Toast.makeText(context, "Lütfen tüm alanları doldurun", Toast.LENGTH_SHORT).show()
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = !isLoading
            ) {
                if (isLoading) {
                    CircularProgressIndicator(color = Color.White)
                } else {
                    Text(text = "Kayıt Ol")
                }
            }
        }
    }
}
