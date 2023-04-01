import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState

const val componentWidth = 128

val backgroundColor = Color(49, 51, 53)
val dropDownColor = Color(43, 43, 43)
val highlightColor = Color(0xFF03DAC6)

fun main() = application {
    Window(
        title = "Shutdown Timer",
        icon = rememberVectorPainter(Icons.Default.Info),
        state = rememberWindowState(size = DpSize(600.dp, 300.dp)),
        resizable = false,
        onCloseRequest = ::exitApplication,
    ) {
        MaterialTheme {
            ShutdownTimer()
        }
    }
}

@Preview
@Composable
fun ShutdownTimer() {
    var hours by remember { mutableStateOf(0) }
    var minutes by remember { mutableStateOf(0) }
    var seconds by remember { mutableStateOf(0) }

    val shutdownEnabled by remember { derivedStateOf { hours + minutes + seconds > 0 } }

    ShutdownTimer(
        hours = hours,
        minutes = minutes,
        seconds = seconds,
        shutdownEnabled = shutdownEnabled,
        onHoursSelected = { selection -> hours = selection },
        onMinutesSelected = { selection -> minutes = selection },
        onSecondsSelected = { selection -> seconds = selection },
        onShutdownClicked = {
            val secondsUntilShutdown = seconds + (minutes * 60) + (hours * 60 * 60)
            Runtime.getRuntime().exec("shutdown -s -t $secondsUntilShutdown")
        },
        onAbortClicked = {
            Runtime.getRuntime().exec("shutdown -a")
        },
    )
}

@Composable
fun ShutdownTimer(
    hours: Int,
    minutes: Int,
    seconds: Int,
    shutdownEnabled: Boolean,
    onHoursSelected: (Int) -> Unit,
    onMinutesSelected: (Int) -> Unit,
    onSecondsSelected: (Int) -> Unit,
    onShutdownClicked: () -> Unit,
    onAbortClicked: () -> Unit,
) {
    Box(
        modifier = Modifier.background(color = backgroundColor).fillMaxSize()
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier
                .border(border = BorderStroke(2.dp, highlightColor), shape = RoundedCornerShape(16.dp))
                .padding(16.dp)
                .align(Alignment.Center),
        ) {
            InputFields(
                selectedHours = hours,
                selectedMinutes = minutes,
                selectedSeconds = seconds,
                onHoursSelected = onHoursSelected,
                onMinutesSelected = onMinutesSelected,
                onSecondsSelected = onSecondsSelected,
            )
            Buttons(
                shutdownEnabled = shutdownEnabled,
                onShutdownClicked = onShutdownClicked,
                onAbortClicked = onAbortClicked,
            )
        }
    }
}

@Composable
private fun InputFields(
    selectedHours: Int,
    selectedMinutes: Int,
    selectedSeconds: Int,
    onHoursSelected: (Int) -> Unit,
    onMinutesSelected: (Int) -> Unit,
    onSecondsSelected: (Int) -> Unit,
) {
    require(selectedHours in 0..24) { "invalid hours" }
    require(selectedMinutes in 0..60) { "invalid minutes" }
    require(selectedSeconds in 0..60) { "invalid seconds" }

    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        NumberDropdown(
            value = selectedHours,
            options = (0..24).toList(),
            onSelection = onHoursSelected,
            label = "Hours",
            modifier = Modifier
                .width(componentWidth.dp)
                .background(dropDownColor),
        )
        NumberDropdown(
            value = selectedMinutes,
            options = (0..60).toList(),
            onSelection = onMinutesSelected,
            label = "Minutes",
            modifier = Modifier
                .width(componentWidth.dp)
                .background(dropDownColor),
        )
        NumberDropdown(
            value = selectedSeconds,
            options = (0..60).toList(),
            onSelection = onSecondsSelected,
            label = "Seconds",
            modifier = Modifier
                .width(componentWidth.dp)
                .background(dropDownColor),
        )
    }
}

@Composable
private fun ColumnScope.Buttons(
    shutdownEnabled: Boolean,
    onShutdownClicked: () -> Unit,
    onAbortClicked: () -> Unit,
) {
    Row(
        modifier = Modifier.align(alignment = Alignment.End),
        horizontalArrangement = Arrangement.spacedBy(space = 8.dp, alignment = Alignment.End),
    ) {
        Button(
            enabled = shutdownEnabled,
            onClick = onShutdownClicked,
            modifier = Modifier.width(componentWidth.dp),
        ) {
            Text("Shutdown")
        }
        Button(
            onClick = onAbortClicked,
            modifier = Modifier.width(componentWidth.dp),
        ) {
            Text("Abort")
        }
    }
}

@Composable
private fun NumberDropdown(
    value: Int,
    options: List<Int>,
    label: String,
    onSelection: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    require(options.contains(value)) { "value: $value not in provided options: $options" }

    var expanded by remember { mutableStateOf(false) }

    Column(modifier = modifier) {
        TextField(
            value = value.toString(),
            colors = TextFieldDefaults.textFieldColors(disabledTextColor = Color.White),
            label = { Text(label, color = highlightColor) },
            onValueChange = { },
            enabled = false,
            modifier = Modifier
                .fillMaxWidth()
                .clickable { expanded = true },
        )

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier
                .background(dropDownColor)
                .height(250.dp),
        ) {
            options.forEach { option ->
                DropdownMenuItem(
                    onClick = {
                        onSelection(option)
                        expanded = false
                    },
                ) {
                    Text(text = option.toString(), textAlign = TextAlign.End, color = Color.White)
                }
            }
        }
    }
}

@Preview
@Composable
fun ShutdownTimerEnabled() {
    MaterialTheme {
        ShutdownTimer(
            hours = 1,
            minutes = 20,
            seconds = 54,
            shutdownEnabled = true,
            onHoursSelected = { },
            onMinutesSelected = { },
            onSecondsSelected = { },
            onShutdownClicked = { },
            onAbortClicked = { },
        )
    }
}

@Preview
@Composable
fun ShutdownTimerDisabled() {
    MaterialTheme {
        ShutdownTimer(
            hours = 0,
            minutes = 0,
            seconds = 0,
            shutdownEnabled = false,
            onHoursSelected = { },
            onMinutesSelected = { },
            onSecondsSelected = { },
            onShutdownClicked = { },
            onAbortClicked = { },
        )
    }
}
