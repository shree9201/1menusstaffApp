package com.droptechsolution.menus

import android.Manifest
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModelProvider
import com.droptechsolution.menus.home.HomeViewModel
import com.droptechsolution.menus.home.HomeViewModelFactory
import com.droptechsolution.menus.home.ILoginClicked
import com.droptechsolution.menus.home.details.HomeActivity
import com.droptechsolution.menus.ui.theme._1MenusTheme
import com.droptechsolution.shared.ui.navigation.MenusNavHost
import javax.inject.Inject


class MainActivity : ComponentActivity(), ILoginClicked {



    @Inject
    lateinit var factory: HomeViewModelFactory

    //    @Inject
    lateinit var viewModel: HomeViewModel

    val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
//            viewModel.loadInfo()
        } else {
            // Permission denied. Explain why the feature is unavailable.
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val homeComponent =
            (application as MenusApplication)
                .appComponent
                .homeComponent()
                .create()
        homeComponent.inject(this)
        requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        viewModel = ViewModelProvider(this, factory)
            .get(HomeViewModel::class.java)




        setContent {
            _1MenusTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
//                    Greeting(
//                        name = "Android",
//                        modifier = Modifier.padding(innerPadding),
//                        this
//                    )
                    MenusNavHost(modifier = Modifier.padding(innerPadding))
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.loginState.observe(this,{ isLoogedIn->
            if(isLoogedIn)
                startActivity(Intent(this, HomeActivity::class.java))
        })

    }

    override fun loginClicked(username: String, password: String) {
//        viewModel.loadInfo("cafe99","pune")
        viewModel.loadInfo(username,username)
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier,loginClicked : ILoginClicked?) {

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Welcome Back",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(bottom = 32.dp)
        )

        // Email Input
        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Password Input with hidden visual transformation
        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password") },
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Login Button
        Button(
            onClick = { loginClicked?.loginClicked(email,password) },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(8.dp)
        ) {
            Text("Login")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    _1MenusTheme {
        Greeting("Android",Modifier,null)
    }
}
