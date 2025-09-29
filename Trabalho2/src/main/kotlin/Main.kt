import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application

@Composable
@Preview
fun App() {
    var text by remember { mutableStateOf("Hello, World!") }
    var text2 by remember { mutableStateOf("Hello, World2!") }

    MaterialTheme {
        Column{
            Row {
                log("App Start")
                Button(onClick = {
                    text = "Hello, Desktop!"
                }, modifier = Modifier.size(500.dp)) {
                    Text(text)
                }
                log("App End")
            }

            Row {
                log("App Start2")
                Button(onClick = {
                    text2 = "Hello, Desktop2!"
                }) {
                    Text(text2)
                }
                log("App End2")
            }
        }
    }
}
fun log(label: String) {
    println("$label thread=${Thread.currentThread().name}")
}

fun main() = application {
    Window(onCloseRequest = ::exitApplication) {
        this.window.title = "test compose"
        App() //lamda que é passado como parâmetro ao Window
    }
}