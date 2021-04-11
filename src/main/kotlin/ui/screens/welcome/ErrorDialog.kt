package ui.screens.welcome


import Themes.Green
import Themes.White1
import Themes.dp8
import androidx.compose.foundation.layout.*
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.OutlinedButton
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import framework.Timber

@Composable
fun ErrorDialog(
    errorMessage: String,
    onDialogCancel: () -> Unit,
    onDialogProceeded: () -> Unit,
) {

    Dialog(
        onDismissRequest = {
            Timber.i("ErrorDialog Dialog UI -> dissmissed clicked")
            onDialogCancel.invoke()
        }
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            Text("Error!")
            Text("$errorMessage")
            OutlinedButton(
                modifier = Modifier
                    .width(152.dp)
                    .height(46.dp)
                    .padding(horizontal = dp8),
                colors = ButtonDefaults.textButtonColors(
                    backgroundColor = Green
                ),
                onClick = {
                    Timber.i("Warning Dialog UI -> Retry clicked")
                    onDialogProceeded.invoke()
                }
            ) {
                Text("Retry!", color = White1)
            }
        }
    }
}