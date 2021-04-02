package dev.superboring.aosp.chakonati.components.fragments.chat

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.contentColorFor
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import dev.superboring.aosp.chakonati.activities.ui.theme.colors

private val minHeight = 32.dp
private val minHeightCornerRadius = minHeight / 2
private val bubbleMinWidth = 96.dp

enum class MessageFrom(
    val backgroundColor: @Composable () -> Color,
) {
    MYSELF({ colors().secondary }),
    THEM({ colors().primaryVariant }),
}

data class Message(
    val from: MessageFrom,
    val text: String,
    var last: Message? = null,
    var next: Message? = null,
)

@Composable
fun MessageRow(
    message: Message,
) {
    ConstraintLayout(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 2.dp)
    ) {
        val (bubble) = createRefs()

        MessageBubble(
            modifier = Modifier
                .widthIn(bubbleMinWidth, Dp.Infinity)
                .heightIn(minHeight, Dp.Infinity)
                .constrainAs(bubble) {
                    when (message.from) {
                        MessageFrom.MYSELF -> {
                            end.linkTo(parent.end)
                        }
                        MessageFrom.THEM -> {
                            start.linkTo(parent.start)
                        }
                    }
                },
            message = message,
        )
    }
}

val lessRoundedEdgesRadius = 3.dp

@Composable
private fun MessageBubble(
    modifier: Modifier = Modifier,
    message: Message,
) {
    val variableCornerRadius =
        if (message.next?.from != message.from) {
            minHeightCornerRadius
        } else {
            lessRoundedEdgesRadius
        }
    val shape = when (message.from) {
        MessageFrom.THEM -> RoundedCornerShape(
            topStart = lessRoundedEdgesRadius,
            topEnd = minHeightCornerRadius,
            bottomStart = variableCornerRadius,
            bottomEnd = minHeightCornerRadius,
        )
        MessageFrom.MYSELF -> RoundedCornerShape(
            topStart = minHeightCornerRadius,
            topEnd = lessRoundedEdgesRadius,
            bottomStart = minHeightCornerRadius,
            bottomEnd = variableCornerRadius,
        )
    }

    Surface(
        modifier = modifier,
        color = message.from.backgroundColor(),
        shape = shape,
        contentColor = colors().background,
    ) {
        ConstraintLayout {
            val (text) = createRefs()

            Text(
                modifier = Modifier
                    .widthIn(0.dp, Dp.Infinity)
                    .constrainAs(text) {
                        top.linkTo(parent.top)
                        bottom.linkTo(parent.bottom)
                        when (message.from) {
                            MessageFrom.MYSELF -> {
                                end.linkTo(parent.end)
                            }
                            MessageFrom.THEM -> {
                                start.linkTo(parent.start)
                            }
                        }
                    }
                    .padding(horizontal = minHeightCornerRadius),
                color = contentColorFor(message.from.backgroundColor()),
                text = message.text,
                textAlign =
                if (message.from == MessageFrom.MYSELF) TextAlign.Start else TextAlign.End
            )
        }
    }
}

@Composable
private fun MessageText() {

}