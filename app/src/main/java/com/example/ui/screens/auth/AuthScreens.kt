package com.example.ui.screens.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.Laika3DLogo
import com.example.ui.components.LaikaHouseLogo
import com.example.ui.components.PtValeLogo
import com.example.ui.components.TripleLogoBanner
import com.example.ui.theme.*

@Composable
fun LoginScreen(
    onLoginClick: (String, String) -> Unit,
    onRegisterClick: () -> Unit,
    onForgotPasswordClick: () -> Unit
) {
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    val scrollState = rememberScrollState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(
                        ForestGreenPrimary.copy(alpha = 0.08f),
                        MaterialTheme.colorScheme.background
                    )
                )
            )
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .widthIn(max = 460.dp)
                .verticalScroll(scrollState),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // Official Logos Header Display (All 3 Logos)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Laika3DLogo(size = 72.dp)
                    LaikaHouseLogo(size = 64.dp)
                    PtValeLogo(height = 42.dp)
                }

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "LAIKA SAMPAH",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Black,
                        color = MaterialTheme.colorScheme.primary,
                        letterSpacing = 1.sp
                    )
                    Text(
                        text = "Digital Waste Management System",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = EmeraldGreen,
                        textAlign = TextAlign.Center
                    )
                    Text(
                        text = "PT VALE IGP POMALAA • TPS 3R TERPADU",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "“Memilah, Mengumpul, Menyetor”",
                        fontSize = 11.sp,
                        color = TextSecondary,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))

                if (errorMessage != null) {
                    Surface(
                        color = Color(0xFFF8D7DA),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = errorMessage ?: "",
                            color = EcoRed,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier.padding(10.dp),
                            textAlign = TextAlign.Center
                        )
                    }
                }

                // Input Username
                OutlinedTextField(
                    value = username,
                    onValueChange = {
                        username = it
                        errorMessage = null
                    },
                    label = { Text("Username") },
                    leadingIcon = {
                        Icon(Icons.Default.Person, contentDescription = null, tint = EmeraldGreen)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("login_username_input"),
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp)
                )

                // Input Password
                OutlinedTextField(
                    value = password,
                    onValueChange = {
                        password = it
                        errorMessage = null
                    },
                    label = { Text("Password") },
                    leadingIcon = {
                        Icon(Icons.Default.Lock, contentDescription = null, tint = EmeraldGreen)
                    },
                    trailingIcon = {
                        IconButton(onClick = { passwordVisible = !passwordVisible }) {
                            Icon(
                                imageVector = if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                contentDescription = if (passwordVisible) "Sembunyikan" else "Tampilkan"
                            )
                        }
                    },
                    visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("login_password_input"),
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp)
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    Text(
                        text = "Lupa Password?",
                        color = MaterialTheme.colorScheme.primary,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier
                            .clickable { onForgotPasswordClick() }
                            .testTag("link_forgot_password")
                    )
                }

                Button(
                    onClick = {
                        if (username.isBlank() || password.isBlank()) {
                            errorMessage = "Username dan password wajib diisi."
                        } else {
                            onLoginClick(username, password)
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .testTag("login_submit_button"),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = ForestGreenPrimary)
                ) {
                    Text(
                        text = "MASUK (LOGIN)",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Belum memiliki akun? ",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "Daftar Registrasi",
                        color = EmeraldGreen,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier
                            .clickable { onRegisterClick() }
                            .testTag("link_register")
                    )
                }
            }
        }
    }
}

@Composable
fun RegisterScreen(
    onRegisterSubmit: (name: String, username: String, phone: String, pass: String, dept: String, addr: String, role: String) -> Unit,
    onBackToLogin: () -> Unit
) {
    var selectedRole by remember { mutableStateOf("Penyetor") } // "Penyetor" or "Admin"
    var adminSecurityCode by remember { mutableStateOf("") }
    var name by remember { mutableStateOf("") }
    var username by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var department by remember { mutableStateOf("") }
    var address by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    val scrollState = rememberScrollState()

    val departments = if (selectedRole == "Admin") {
        listOf(
            "Environmental & Waste Dept (TPS 3R)",
            "ESG & Sustainability Vale",
            "K3L / Safety Vale",
            "Management Operasional TPS 3R",
            "Process Plant Smelter IGP",
            "Mining Operations IGP"
        )
    } else {
        listOf(
            "Mining Operations IGP",
            "Process Plant Smelter IGP",
            "Environmental & Waste Dept",
            "HR & General Affairs",
            "Maintenance & Workshop",
            "Supply Chain & Logistik",
            "K3L / Safety Vale",
            "Kantin & Mess Pomalaa"
        )
    }
    var expandedDept by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(ForestGreenPrimary.copy(alpha = 0.08f), MaterialTheme.colorScheme.background)
                )
            )
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .widthIn(max = 500.dp)
                .verticalScroll(scrollState),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = if (selectedRole == "Admin") "REGISTRASI ADMIN TPS 3R" else "REGISTRASI PENYETOR",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Black,
                    color = if (selectedRole == "Admin") ForestGreenPrimary else MaterialTheme.colorScheme.primary
                )
                Text(
                    text = if (selectedRole == "Admin") {
                        "Pendaftaran Akun Pengawas & Operator TPS 3R PT VALE IGP Pomalaa"
                    } else {
                        "Daftar sebagai Penyetor Sampah Terpadu PT VALE IGP Pomalaa"
                    },
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center
                )

                // Role Selector Segment
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(4.dp),
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Button(
                            onClick = {
                                selectedRole = "Penyetor"
                                errorMessage = null
                            },
                            modifier = Modifier
                                .weight(1f)
                                .height(38.dp)
                                .testTag("tab_role_penyetor"),
                            shape = RoundedCornerShape(8.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (selectedRole == "Penyetor") ForestGreenPrimary else Color.Transparent,
                                contentColor = if (selectedRole == "Penyetor") Color.White else MaterialTheme.colorScheme.onSurface
                            ),
                            elevation = null
                        ) {
                            Text("🌱 Penyetor", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }

                        Button(
                            onClick = {
                                selectedRole = "Admin"
                                errorMessage = null
                            },
                            modifier = Modifier
                                .weight(1f)
                                .height(38.dp)
                                .testTag("tab_role_admin"),
                            shape = RoundedCornerShape(8.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (selectedRole == "Admin") ForestGreenPrimary else Color.Transparent,
                                contentColor = if (selectedRole == "Admin") Color.White else MaterialTheme.colorScheme.onSurface
                            ),
                            elevation = null
                        ) {
                            Text("👑 Admin / Petugas", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }

                HorizontalDivider(modifier = Modifier.padding(vertical = 2.dp))

                if (errorMessage != null) {
                    Surface(
                        color = Color(0xFFF8D7DA),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = errorMessage ?: "",
                            color = EcoRed,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier.padding(8.dp),
                            textAlign = TextAlign.Center
                        )
                    }
                }

                // Security PIN field for Admin registration
                if (selectedRole == "Admin") {
                    Surface(
                        color = MintContainer.copy(alpha = 0.5f),
                        shape = RoundedCornerShape(12.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, ForestGreenPrimary.copy(alpha = 0.4f)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.padding(12.dp),
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Icon(
                                    Icons.Default.Security,
                                    contentDescription = null,
                                    tint = ForestGreenPrimary,
                                    modifier = Modifier.size(18.dp)
                                )
                                Text(
                                    text = "Otorisasi Khusus Petugas TPS 3R",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = ForestGreenPrimary
                                )
                            }
                            Text(
                                text = "Untuk mendaftar sebagai Admin, masukkan Kode Otorisasi Resmi dari Departemen Lingkungan PT Vale (Default: VALE3R)",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )

                            OutlinedTextField(
                                value = adminSecurityCode,
                                onValueChange = { adminSecurityCode = it; errorMessage = null },
                                label = { Text("Kode PIN Otorisasi Admin *") },
                                placeholder = { Text("Contoh: VALE3R") },
                                leadingIcon = { Icon(Icons.Default.Key, contentDescription = null, tint = ForestGreenPrimary) },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("reg_admin_security_code_input"),
                                singleLine = true,
                                shape = RoundedCornerShape(10.dp)
                            )
                        }
                    }
                }

                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it; errorMessage = null },
                    label = { Text("Nama Lengkap *") },
                    leadingIcon = { Icon(Icons.Default.Badge, contentDescription = null) },
                    modifier = Modifier.fillMaxWidth().testTag("reg_name_input"),
                    singleLine = true,
                    shape = RoundedCornerShape(10.dp)
                )

                OutlinedTextField(
                    value = username,
                    onValueChange = { username = it; errorMessage = null },
                    label = { Text("Username *") },
                    leadingIcon = { Icon(Icons.Default.Person, contentDescription = null) },
                    modifier = Modifier.fillMaxWidth().testTag("reg_username_input"),
                    singleLine = true,
                    shape = RoundedCornerShape(10.dp)
                )

                OutlinedTextField(
                    value = phone,
                    onValueChange = { phone = it; errorMessage = null },
                    label = { Text("Nomor HP (WhatsApp) *") },
                    leadingIcon = { Icon(Icons.Default.Phone, contentDescription = null) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                    modifier = Modifier.fillMaxWidth().testTag("reg_phone_input"),
                    singleLine = true,
                    shape = RoundedCornerShape(10.dp)
                )

                // Department Select
                Box(modifier = Modifier.fillMaxWidth()) {
                    OutlinedTextField(
                        value = department,
                        onValueChange = { department = it },
                        label = { Text("Unit / Departemen *") },
                        leadingIcon = { Icon(Icons.Default.Business, contentDescription = null) },
                        trailingIcon = {
                            IconButton(onClick = { expandedDept = !expandedDept }) {
                                Icon(Icons.Default.ArrowDropDown, contentDescription = null)
                            }
                        },
                        modifier = Modifier.fillMaxWidth().testTag("reg_dept_input"),
                        readOnly = false,
                        shape = RoundedCornerShape(10.dp)
                    )
                    DropdownMenu(
                        expanded = expandedDept,
                        onDismissRequest = { expandedDept = false },
                        modifier = Modifier.fillMaxWidth(0.85f)
                    ) {
                        departments.forEach { dept ->
                            DropdownMenuItem(
                                text = { Text(dept, fontSize = 13.sp) },
                                onClick = {
                                    department = dept
                                    expandedDept = false
                                }
                            )
                        }
                    }
                }

                OutlinedTextField(
                    value = address,
                    onValueChange = { address = it; errorMessage = null },
                    label = { Text(if (selectedRole == "Admin") "Pos / Lokasi Pos Operasional *" else "Alamat / Lokasi Kerja *") },
                    leadingIcon = { Icon(Icons.Default.LocationOn, contentDescription = null) },
                    modifier = Modifier.fillMaxWidth().testTag("reg_address_input"),
                    singleLine = true,
                    shape = RoundedCornerShape(10.dp)
                )

                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it; errorMessage = null },
                    label = { Text("Password (min 6 karakter) *") },
                    leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null) },
                    visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    modifier = Modifier.fillMaxWidth().testTag("reg_pass_input"),
                    singleLine = true,
                    shape = RoundedCornerShape(10.dp)
                )

                OutlinedTextField(
                    value = confirmPassword,
                    onValueChange = { confirmPassword = it; errorMessage = null },
                    label = { Text("Konfirmasi Password *") },
                    leadingIcon = { Icon(Icons.Default.LockReset, contentDescription = null) },
                    visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    modifier = Modifier.fillMaxWidth().testTag("reg_confirm_pass_input"),
                    singleLine = true,
                    shape = RoundedCornerShape(10.dp)
                )

                Button(
                    onClick = {
                        val validAdminCodes = listOf("VALE3R", "TPS3RVALE", "ADMIN2026", "VALE2026", "ADMINVALE")
                        when {
                            selectedRole == "Admin" && adminSecurityCode.trim().uppercase() !in validAdminCodes -> {
                                errorMessage = "Kode PIN Otorisasi Admin salah! Masukkan kode yang valid (Default: VALE3R)."
                            }
                            name.isBlank() -> errorMessage = "Nama wajib diisi."
                            username.isBlank() -> errorMessage = "Username wajib diisi."
                            phone.isBlank() -> errorMessage = "Nomor HP wajib diisi."
                            department.isBlank() -> errorMessage = "Unit/Departemen wajib diisi."
                            address.isBlank() -> errorMessage = "Alamat/Lokasi kerja wajib diisi."
                            password.length < 6 -> errorMessage = "Password minimal 6 karakter."
                            password != confirmPassword -> errorMessage = "Password dan konfirmasi password harus sama."
                            else -> onRegisterSubmit(name, username, phone, password, department, address, selectedRole)
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("reg_submit_button"),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = ForestGreenPrimary)
                ) {
                    Text(
                        text = if (selectedRole == "Admin") "DAFTAR SEBAGAI ADMIN / PETUGAS" else "DAFTAR SEBAGAI PENYETOR",
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }

                TextButton(
                    onClick = onBackToLogin,
                    modifier = Modifier.testTag("reg_back_login_button")
                ) {
                    Text("Sudah punya akun? Kembali ke Login", color = EmeraldGreen, fontWeight = FontWeight.SemiBold)
                }
            }
        }
    }
}

@Composable
fun ForgotPasswordScreen(
    onResetSubmit: (username: String, phone: String) -> Unit,
    onBackToLogin: () -> Unit
) {
    var username by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .widthIn(max = 440.dp),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(28.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(LightMint),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.LockReset,
                        contentDescription = null,
                        tint = ForestGreenPrimary,
                        modifier = Modifier.size(32.dp)
                    )
                }

                Text(
                    text = "LUPA PASSWORD",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Black,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = "Masukkan Username dan Nomor HP Anda untuk mengirim permintaan reset ke Admin TPS 3R.",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center
                )

                if (errorMessage != null) {
                    Text(
                        text = errorMessage ?: "",
                        color = EcoRed,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium
                    )
                }

                OutlinedTextField(
                    value = username,
                    onValueChange = { username = it; errorMessage = null },
                    label = { Text("Username") },
                    leadingIcon = { Icon(Icons.Default.Person, contentDescription = null) },
                    modifier = Modifier.fillMaxWidth().testTag("forgot_username_input"),
                    singleLine = true,
                    shape = RoundedCornerShape(10.dp)
                )

                OutlinedTextField(
                    value = phone,
                    onValueChange = { phone = it; errorMessage = null },
                    label = { Text("Nomor HP Terdaftar") },
                    leadingIcon = { Icon(Icons.Default.Phone, contentDescription = null) },
                    modifier = Modifier.fillMaxWidth().testTag("forgot_phone_input"),
                    singleLine = true,
                    shape = RoundedCornerShape(10.dp)
                )

                Button(
                    onClick = {
                        if (username.isBlank() || phone.isBlank()) {
                            errorMessage = "Username dan Nomor HP wajib diisi."
                        } else {
                            onResetSubmit(username, phone)
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("forgot_submit_button"),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = ForestGreenPrimary)
                ) {
                    Text("RESET PASSWORD", fontWeight = FontWeight.Bold, color = Color.White)
                }

                TextButton(onClick = onBackToLogin) {
                    Text("Kembali ke Login", color = EmeraldGreen)
                }
            }
        }
    }
}
