package ui.screens.welcome

import Themes.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.OutlinedButton
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.toxicbakery.logging.Arbor
import framework.Timber
import kotlin.system.exitProcess

@Composable
fun WarningDialog(
    message: String,
    onDialogCancel: () -> Unit,
    onDialogProceeded: () -> Unit,
) {

    Dialog(
        onDismissRequest = {
            Timber.i("Warning Dialog UI -> dissmissed clicked")
            onDialogCancel.invoke()
        }
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            Text("Caution!")
            Text(message)

            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = dp48),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                OutlinedButton(
                    modifier = Modifier
                        .height(46.dp)
                        .padding(horizontal = dp8),
                    colors = ButtonDefaults.textButtonColors(
                        backgroundColor = Red
                    ),
                    onClick = {
                        Timber.i("Warning Dialog UI -> Cancel clicked")
                        onDialogCancel.invoke()
                    },
                ) {
                    Text("I'm Scared! Cancel")
                }

                OutlinedButton(
                    modifier = Modifier
                        .width(152.dp)
                        .height(46.dp)
                        .padding(horizontal = dp8),
                    colors = ButtonDefaults.textButtonColors(
                        backgroundColor = Green
                    ),
                    onClick = {
                        Timber.i("Warning Dialog UI -> Proceed clicked")
                        onDialogProceeded.invoke()
                    }
                ) {
                    Text("Amen! Proceed", color = White1)
                }
            }
        }
    }
}