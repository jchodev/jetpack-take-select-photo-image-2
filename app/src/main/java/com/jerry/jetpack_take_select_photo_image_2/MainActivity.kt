package com.jerry.jetpack_take_select_photo_image_2

import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.view.WindowCompat
import com.jerry.jetpack_take_select_photo_image_2.components.MyImageArea
import com.jerry.jetpack_take_select_photo_image_2.ui.theme.Jetpacktakeselectphotoimage2Theme
import com.jerry.jetpack_take_select_photo_image_2.viewmodel.MainViewModel
import java.io.File

class MainActivity : ComponentActivity() {

    private val viewModel by viewModels<MainViewModel>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        WindowCompat.setDecorFitsSystemWindows(window, false)

        viewModel.setContext(this)

        setContent {
            Jetpacktakeselectphotoimage2Theme {

                Scaffold { paddingValues ->
                    Box (
                        modifier = Modifier
                            .padding(paddingValues)
                    ){
                        Column(
                            modifier = Modifier.fillMaxSize()
                        ) {

                            val uri = remember { mutableStateOf<Uri?>(null) }

                            //image to show bottom sheet
                            MyImageArea(
                                directory = File(cacheDir, "images"),
                                uri = uri.value,
                                onSetUri = {
                                    uri.value = it
                                },
                                upload = {
                                    viewModel.upload(it)
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    Jetpacktakeselectphotoimage2Theme {
        Greeting("Android")
    }
}