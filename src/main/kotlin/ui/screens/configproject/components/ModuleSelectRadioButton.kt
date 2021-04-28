package ui.screens.configproject.components

import Themes.Green
import Themes.dp16
import Themes.dp8
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.selection.selectable
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import ui.data.ProjectModuleType


@Composable
fun ModuleSelectRadioButton(
    modifier: Modifier = Modifier,
    moduleOptions: List<ProjectModuleType>,
    onModuleOptionSelected: (module: ProjectModuleType) -> Unit
) {

    val (selectedOption, onOptionSelected) = remember { mutableStateOf(moduleOptions.get(0)) }

    Row {
        moduleOptions.onEach { moduleType ->
            Row(modifier = Modifier
                .selectable(
                    selected = (moduleType == selectedOption),
                    onClick = {
                        onOptionSelected.invoke(moduleType)
                        onModuleOptionSelected.invoke(moduleType)
                    }
                ).padding(end = dp8)
            ) {
                RadioButton(
                    selected = (moduleType == selectedOption),
                    onClick = null,
                    colors = RadioButtonDefaults.colors(selectedColor = Green)
                )
                Text(
                    text = moduleType.label,
                    style = MaterialTheme.typography.body1.merge(),
                    modifier = Modifier.padding(start = dp16)
                )
            }
        }

    }
}