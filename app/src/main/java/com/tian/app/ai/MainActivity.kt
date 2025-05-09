package com.tian.app.ai

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import com.tian.app.ai.R
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.painter.Painter
import com.tian.app.ai.ui.theme.ScanAuthTheme
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Person
import com.journeyapps.barcodescanner.ScanOptions
import com.journeyapps.barcodescanner.ScanContract
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import android.widget.Toast
import androidx.compose.ui.platform.LocalContext
import com.tian.app.ai.CustomCaptureActivity
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.google.accompanist.permissions.isGranted
import android.os.Build
import com.tian.app.ai.utils.LoginManager
import com.tian.app.ai.ui.login.LoginActivity
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import com.google.zxing.client.android.Intents
import com.tian.app.ai.ui.authorization.history.AuthRecord
import com.tian.app.ai.ui.authorization.history.AuthorizationHistoryActivity

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    
    @Inject
    lateinit var loginManager: LoginManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // 检查登录状态
        if (!loginManager.isLoggedIn()) {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
            return
        }

        enableEdgeToEdge()
        setContent {
            ScanAuthTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    var currentTab by remember { mutableStateOf(0) }
                    MainScreen(
                        currentTab = currentTab,
                        onTabChange = { currentTab = it },
                        onLogout = {
                            loginManager.clearLoginInfo()
                            startActivity(Intent(this, LoginActivity::class.java))
                            finish()
                        }
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun AppEntry() {
    // 适配Android 13+的图片权限
    val permissions = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        listOf(
            android.Manifest.permission.CAMERA,
            android.Manifest.permission.READ_MEDIA_IMAGES
        )
    } else {
        listOf(
            android.Manifest.permission.CAMERA,
            android.Manifest.permission.READ_EXTERNAL_STORAGE
        )
    }
    val permissionsState = rememberMultiplePermissionsState(permissions)

    LaunchedEffect(Unit) {
        if (!permissionsState.allPermissionsGranted) {
            permissionsState.launchMultiplePermissionRequest()
        }
    }
    var showSplash by remember { mutableStateOf(true) }
    var isLoggedIn by remember { mutableStateOf(false) }
    var currentTab by remember { mutableStateOf(0) } // 0:授权 1:套餐 2:个人中心

    LaunchedEffect(Unit) {
        delay(2000)
        showSplash = false
    }

    when {
        showSplash -> SplashScreen()
        !isLoggedIn -> LoginScreen(onLoginSuccess = { isLoggedIn = true })
        else -> MainScreen(currentTab, onTabChange = { currentTab = it }, onLogout = {
            isLoggedIn = false
            currentTab = 0
        })
    }
}

@Composable
fun MainScreen(currentTab: Int, onTabChange: (Int) -> Unit, onLogout: () -> Unit) {
    Scaffold(
        bottomBar = {
            NavigationBar {
                NavigationBarItem(
                    selected = currentTab == 0,
                    onClick = { onTabChange(0) },
                    icon = { Icon(Icons.Default.Check, contentDescription = "授权") },
                    label = { Text("授权") }
                )
                NavigationBarItem(
                    selected = currentTab == 1,
                    onClick = { onTabChange(1) },
                    icon = { Icon(Icons.Default.List, contentDescription = "套餐") },
                    label = { Text("套餐") }
                )
                NavigationBarItem(
                    selected = currentTab == 2,
                    onClick = { onTabChange(2) },
                    icon = { Icon(Icons.Default.Person, contentDescription = "个人中心") },
                    label = { Text("我的") }
                )
            }
        }
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding).fillMaxSize()) {
            when (currentTab) {
                0 -> AuthPage()
                1 -> PackagePage()
                2 -> ProfileScreen(onLogout = onLogout)
            }
        }
    }
}

@Composable
fun ProfileScreen(onLogout: () -> Unit) {
    var showDisclaimer by remember { mutableStateOf(false) }
    var showChangePwd by remember { mutableStateOf(false) }
    var pwdInput by remember { mutableStateOf("") }
    var pwdMsg by remember { mutableStateOf("") }
    val appVersion = "1.0" // 可自动读取
    val userName = "超级用户"
    val userAccount = "AdminTest"

    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(id = R.drawable.jcc),
            contentDescription = "头像",
            modifier = Modifier.size(80.dp)
        )
        Spacer(modifier = Modifier.height(12.dp))
        Text(userName, fontSize = 20.sp, fontWeight = FontWeight.Bold)
        Text("账号：$userAccount", color = Color.Gray, fontSize = 14.sp)
        Spacer(modifier = Modifier.height(24.dp))
        Button(onClick = { showChangePwd = true }, modifier = Modifier.fillMaxWidth()) {
            Text("修改密码")
        }
        Spacer(modifier = Modifier.height(12.dp))
        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Text("App版本号：$appVersion", modifier = Modifier.weight(1f))
        }
        Spacer(modifier = Modifier.height(12.dp))
        Button(onClick = { showDisclaimer = true }, modifier = Modifier.fillMaxWidth()) {
            Text("免责声明")
        }
        Spacer(modifier = Modifier.height(24.dp))
        Button(onClick = onLogout, colors = ButtonDefaults.buttonColors(containerColor = Color.Red), modifier = Modifier.fillMaxWidth()) {
            Text("退出登录", color = Color.White)
        }
    }
    if (showDisclaimer) {
        AlertDialog(
            onDismissRequest = { showDisclaimer = false },
            title = { Text("免责声明") },
            text = { Text("本应用仅供学习与测试使用，所有数据为模拟，后续请对接真实后端。") },
            confirmButton = {
                TextButton(onClick = { showDisclaimer = false }) { Text("确定") }
            }
        )
    }
    if (showChangePwd) {
        AlertDialog(
            onDismissRequest = { showChangePwd = false; pwdInput = ""; pwdMsg = "" },
            title = { Text("修改密码") },
            text = {
                Column {
                    OutlinedTextField(
                        value = pwdInput,
                        onValueChange = { pwdInput = it },
                        label = { Text("新密码") },
                        singleLine = true,
                        visualTransformation = PasswordVisualTransformation()
                    )
                    if (pwdMsg.isNotEmpty()) Text(pwdMsg, color = Color.Red)
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    // 模拟修改密码，预留接口
                    if (pwdInput.length < 6) {
                        pwdMsg = "密码长度不能少于6位"
                    } else {
                        pwdMsg = "修改成功（模拟）"
                        // TODO: 对接后端接口
                        showChangePwd = false
                        pwdInput = ""
                    }
                }) { Text("确定") }
            },
            dismissButton = {
                TextButton(onClick = { showChangePwd = false; pwdInput = ""; pwdMsg = "" }) { Text("取消") }
            }
        )
    }
}

@Composable
fun SplashScreen() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Image(
                painter = painterResource(id = R.drawable.ic_launcher_round),
                contentDescription = "App Icon",
                modifier = Modifier.size(120.dp)
            )
            Spacer(modifier = Modifier.height(24.dp))
            Text("超级授权", style = MaterialTheme.typography.headlineMedium)
        }
    }
}

@Composable
fun LoginScreen(onLoginSuccess: () -> Unit) {
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var errorMsg by remember { mutableStateOf("") }
    var loading by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(32.dp)
        ) {
            Image(
                painter = painterResource(id = R.drawable.ic_launcher),
                contentDescription = "App Icon",
                modifier = Modifier.size(80.dp)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text("超级授权", style = MaterialTheme.typography.headlineSmall)
            Spacer(modifier = Modifier.height(32.dp))
            OutlinedTextField(
                value = username,
                onValueChange = { username = it },
                label = { Text("账号") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(16.dp))
            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("密码") },
                singleLine = true,
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(24.dp))
            Button(
                onClick = {
                    loading = true
                    errorMsg = ""
                    // 模拟登录校验，后续可替换为真实接口
                    // suspend fun login(username: String, password: String): Result<User>
                    // TODO: 对接后端接口
                    if (username == "admin" && password == "123456") {
                        onLoginSuccess()
                    } else {
                        errorMsg = "账号或密码错误（测试账号：admin 密码：123456）"
                    }
                    loading = false
                },
                enabled = !loading,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(if (loading) "登录中..." else "登录")
            }
            if (errorMsg.isNotEmpty()) {
                Spacer(modifier = Modifier.height(16.dp))
                Text(errorMsg, color = Color.Red)
            }
        }
    }
}

// 授权页面相关数据模型
data class AppAuthItem(
    val id: String,
    val name: String,
    val iconRes: Int,
    val authRecords: List<AuthRecord>
)

@Composable
fun AuthPage() {
    val context = LocalContext.current
    val appList = remember {
        listOf(
            AppAuthItem(
                id = "1",
                name = "金铲铲之战",
                iconRes = R.drawable.jcc,
                authRecords = listOf(
                    AuthRecord("1", "QR2025072251RQQQQ", "A123", "R001", "S001", "2024-06-01 12:00", "2024-06-01 11:00", "首次授权", true),
                    AuthRecord("2", "QR2025072251RQQQQ", "A124", "R001", "S002", "2024-06-02 12:00", "2024-06-02 11:00", "二次授权", false)
                )
            ),
            AppAuthItem(
                id = "2",
                name = "王者荣耀",
                iconRes = R.drawable.jcc,
                authRecords = listOf(
                    AuthRecord("3", "QR2025072251RQQQW", "A125", "R002", "S003", "2024-06-03 12:00", "2024-06-03 11:00", "首次授权", true)
                )
            )
        )
    }
    val scanLauncher = rememberLauncherForActivityResult(ScanContract()) { result ->
        if (result.contents != null) {
            Toast.makeText(context, "扫码结果：${result.contents}", Toast.LENGTH_LONG).show()
            // 这里可以添加扫码成功后的业务逻辑
        }
    }
    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text("应用授权", style = MaterialTheme.typography.headlineSmall)
        Spacer(modifier = Modifier.height(16.dp))
        appList.forEach { app ->
            Card(
                modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                elevation = CardDefaults.cardElevation(4.dp)
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Image(
                        painter = painterResource(id = app.iconRes),
                        contentDescription = app.name,
                        modifier = Modifier.size(48.dp)
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(app.name, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                    }
                    Button(onClick = {
                        val options = ScanOptions()
                        options.setPrompt("识别二维码")
                        options.setBeepEnabled(true)
                        options.setOrientationLocked(true)
                        options.setBarcodeImageEnabled(true)
                        options.setDesiredBarcodeFormats(ScanOptions.QR_CODE)
                        options.setCaptureActivity(CustomCaptureActivity::class.java)
                        scanLauncher.launch(options)
                    }) { Text("扫码授权") }
                    Spacer(modifier = Modifier.width(8.dp))
                    OutlinedButton(onClick = {
                        val intent = Intent(context, Class.forName("com.tian.app.ai.ui.authorization.history.AuthorizationHistoryActivity"))
                        context.startActivity(intent)
                    }) { Text("授权历史") }
                }
            }
        }
    }
}

// 套餐页面相关数据模型

data class PackageItem(
    val id: String,
    val packageNo: String,
    val name: String,
    val remainCount: Int,
    val robotNo: String,
    val authTime: String,
    val expireTime: String,
    val code: String,
    val status: Int, // 0=未授权 1=已授权 2=已过期
    val authRecords: List<AuthRecord>
)

@Composable
fun PackagePage() {
    val context = LocalContext.current
    val allPackages = remember {
        listOf(
            PackageItem(
                id = "1",
                packageNo = "QR2025072251RQQQQ",
                name = "金铲铲之战",
                remainCount = 30,
                robotNo = "R001",
                authTime = "2024-06-01 12:00",
                expireTime = "2024-12-01 12:00",
                code = "78",
                status = 1,
                authRecords = listOf(
                    AuthRecord("1", "QR2025072251RQQQQ", "A123", "R001", "S001", "2024-06-01 12:00", "2024-06-01 11:00", "首次授权", true),
                    AuthRecord("2", "QR2025072251RQQQQ", "A124", "R001", "S002", "2024-06-02 12:00", "2024-06-02 11:00", "二次授权", false)
                )
            ),
            PackageItem(
                id = "2",
                packageNo = "QR2025072251RQQQW",
                name = "王者荣耀",
                remainCount = 0,
                robotNo = "R002",
                authTime = "2024-05-01 12:00",
                expireTime = "2024-06-01 12:00",
                code = "79",
                status = 2,
                authRecords = listOf(
                    AuthRecord("3", "QR2025072251RQQQW", "A125", "R002", "S003", "2024-05-01 12:00", "2024-05-01 11:00", "已过期授权", false)
                )
            ),
            PackageItem(
                id = "3",
                packageNo = "QR2025072251RQQQE",
                name = "和平精英",
                remainCount = 10,
                robotNo = "R003",
                authTime = "2024-06-10 12:00",
                expireTime = "2024-12-10 12:00",
                code = "80",
                status = 0,
                authRecords = emptyList()
            )
        )
    }
    val tabTitles = listOf("全部套餐", "未授权套餐", "已授权套餐", "已过期套餐")
    var selectedTab by remember { mutableStateOf(0) }
    var searchText by remember { mutableStateOf("") }
    val scanLauncher = rememberLauncherForActivityResult(ScanContract()) { result ->
        if (result.contents != null) {
            Toast.makeText(context, "扫码结果：${result.contents}", Toast.LENGTH_LONG).show()
            // 这里可以添加扫码成功后的业务逻辑
        }
    }
    val filteredList = allPackages.filter {
        (selectedTab == 0 || it.status == selectedTab - 1) &&
        (searchText.isBlank() || it.packageNo.contains(searchText) || it.name.contains(searchText))
    }
    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text("套餐列表", style = MaterialTheme.typography.headlineSmall)
        Spacer(modifier = Modifier.height(8.dp))
        TabRow(selectedTabIndex = selectedTab) {
            tabTitles.forEachIndexed { idx, title ->
                Tab(
                    selected = selectedTab == idx,
                    onClick = { selectedTab = idx },
                    text = { Text(title) }
                )
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value = searchText,
            onValueChange = { searchText = it },
            label = { Text("搜索套餐号/名称") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))
        if (filteredList.isEmpty()) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("暂无数据")
            }
        } else {
            Column(Modifier.verticalScroll(rememberScrollState())) {
                filteredList.forEach { pkg ->
                    Card(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                        elevation = CardDefaults.cardElevation(4.dp)
                    ) {
                        Column(Modifier.padding(16.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Image(
                                    painter = painterResource(id = R.drawable.jcc),
                                    contentDescription = pkg.name,
                                    modifier = Modifier.size(40.dp)
                                )
                                Spacer(modifier = Modifier.width(12.dp))
                                Column(Modifier.weight(1f)) {
                                    Text("套餐号：${pkg.packageNo}", fontWeight = FontWeight.Bold)
                                    Text("名称：${pkg.name}")
                                    Text("剩余授权次数：${pkg.remainCount}")
                                    Text("机器人编号：${pkg.robotNo}")
                                    Text("授权时间：${pkg.authTime}")
                                    Text("到期时间：${pkg.expireTime}")
                                }
                                Text(pkg.code, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            Row {
                                Button(onClick = {
                                    val options = ScanOptions()
                                    options.setPrompt("识别二维码")
                                    options.setBeepEnabled(true)
                                    options.setOrientationLocked(true)
                                    options.setBarcodeImageEnabled(true)
                                    options.setDesiredBarcodeFormats(ScanOptions.QR_CODE)
                                    options.setCaptureActivity(CustomCaptureActivity::class.java)
                                    scanLauncher.launch(options)
                                }) { Text("扫码授权") }
                                Spacer(modifier = Modifier.width(8.dp))
                                OutlinedButton(onClick = {
                                    val intent = Intent(context, Class.forName("com.tian.app.ai.ui.authorization.history.AuthorizationHistoryActivity"))
                                    context.startActivity(intent)
                                }) { Text("授权记录") }
                            }
                        }
                    }
                }
            }
        }
    }
}