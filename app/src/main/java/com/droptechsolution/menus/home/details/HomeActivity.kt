package com.droptechsolution.menus.home.details

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.ViewModelProvider
import com.droptechsolution.menus.MenusApplication
import com.droptechsolution.menus.home.HomeViewModel
import com.droptechsolution.menus.home.HomeViewModelFactory
import com.droptechsolution.menus.ui.theme._1MenusTheme
import com.droptechsolution.shared.navigation.MenusNavHost
import javax.inject.Inject

class HomeActivity: ComponentActivity() {

    @Inject
    lateinit var factory: HomeViewModelFactory

    //    @Inject
    lateinit var viewModel: HomeViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val homeComponent =
            (application as MenusApplication)
                .appComponent
                .homeComponent()
                .create()
        homeComponent.inject(this)

        viewModel = ViewModelProvider(this, factory)
            .get(HomeViewModel::class.java)


        setContent {
            _1MenusTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) {
                    innerPadding ->
//                    HomeScreen(
//                        modifier = Modifier.padding(innerPadding),
//                        viewModel
//                    )
                    MenusNavHost(modifier = Modifier.padding(innerPadding))
                }
            }
        }

         viewModel.loadStaffs()
    }
}

@Preview
@Composable
fun LoginScreenPreview() {
    MenusNavHost()
}
