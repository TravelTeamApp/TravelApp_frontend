package tr.edu.trakya.tubanurturkmen.bitirmeprojesi1
import androidx.compose.material3.CircularProgressIndicator
import retrofit2.Response
import retrofit2.Call
import retrofit2.Callback
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import tr.edu.trakya.tubanurturkmen.bitirmeprojesi1.ui.theme.BitirmeProjesi1Theme
import android.util.Log
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(navController: NavController) {
    val email = remember { mutableStateOf("") }
    val password = remember { mutableStateOf("") }
    val context = LocalContext.current
    var isPasswordVisible by remember { mutableStateOf(false) }

    // Giriş işlemi sırasında yükleniyor durumu
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
                text = "Hoşgeldiniz",
                style = androidx.compose.material3.MaterialTheme.typography.headlineLarge.copy(color = MaterialTheme.colorScheme.primary)
            )

            // Email alanı
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

            // Şifre alanı
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

            // Eğer yükleme yapılırken buton, kullanıcıyı bilgilendirecek şekilde değiştirilebilir.
            if (isLoading) {
                CircularProgressIndicator(modifier = Modifier.size(48.dp))
            } else {
                // Giriş Butonu
                Button(
                    onClick = {
                        if (email.value.isNotEmpty() && password.value.isNotEmpty()) {
                            isLoading = true

                            // RetrofitClient üzerinden apiService'e erişim
                            val loginRequest = LoginRequest(email.value, password.value)

                            RetrofitClient.apiService.login(loginRequest).enqueue(object : retrofit2.Callback<LoginResponse> {
                                override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                                    isLoading = false
                                    if (response.isSuccessful) {
                                        // Başarılı giriş işlemi
                                        val token = response.body()
                                        Log.d("LoginSuccess", "Giriş Başarılı: Kullanıcı Token'ı: $token")  // Başarılı giriş logu
                                        if (token != null) {
                                            // Token ile işlemler
                                            Toast.makeText(context, "Giriş Başarılı", Toast.LENGTH_SHORT).show()

                                            // Başarıyla giriş yaptıktan sonra Home sayfasına yönlendir
                                            navController.navigate("home") // Home ekranına yönlendirme

                                            // Token'ı saklama veya diğer işlemler yapılabilir
                                        }
                                    } else {
                                        val errorBody = response.errorBody()?.string()
                                        val errorCode = response.code()
                                        Log.e("LoginError", "Hata Kodu: $errorCode, Hata Mesajı: $errorBody")
                                        Toast.makeText(context, "Giriş Başarısız: $errorBody", Toast.LENGTH_SHORT).show()
                                    }
                                }

                                override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                                    isLoading = false
                                    // Bağlantı hatası durumunda
                                    Log.e("LoginError", "Bağlantı Hatası: ${t.message}")
                                    Toast.makeText(context, "Bağlantı Hatası: ${t.message}", Toast.LENGTH_SHORT).show()
                                }
                            })
                        } else {
                            Toast.makeText(context, "Lütfen tüm alanları doldurun", Toast.LENGTH_SHORT).show()
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text(text = "Giriş Yap")
                }
                // Kayıt Ol linki
                Text(
                    text = "Hesabınız yok mu? Kayıt olun",
                    style = MaterialTheme.typography.bodyLarge.copy(color = MaterialTheme.colorScheme.primary),
                    modifier = Modifier
                        .clickable {
                            // Kayıt ekranına yönlendir
                            navController.navigate("register")
                        }
                )
                // Şifremi Unuttum linki
                Text(
                    text = "Şifremi Unuttum?",
                    style = MaterialTheme.typography.bodyLarge.copy(color = MaterialTheme.colorScheme.primary),
                    modifier = Modifier
                        .clickable {
                            // Şifremi Unuttum ekranına yönlendir
                            navController.navigate("forgotPassword")
                        }
                )
            }
        }
    }
}
