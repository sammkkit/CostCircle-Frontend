package com.samkit.costcircle.ui.screens.stats.components

import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.dp
import com.patrykandpatryk.vico.compose.axis.horizontal.rememberBottomAxis
import com.patrykandpatryk.vico.compose.axis.vertical.rememberStartAxis
import com.patrykandpatryk.vico.compose.chart.Chart
import com.patrykandpatryk.vico.compose.chart.column.columnChart
import com.patrykandpatryk.vico.compose.component.lineComponent
import com.patrykandpatryk.vico.compose.component.shape.shader.fromBrush
import com.patrykandpatryk.vico.compose.component.shapeComponent
import com.patrykandpatryk.vico.compose.style.ProvideChartStyle
import com.patrykandpatryk.vico.core.axis.AxisPosition
import com.patrykandpatryk.vico.core.axis.formatter.AxisValueFormatter
import com.patrykandpatryk.vico.core.chart.column.ColumnChart
import com.patrykandpatryk.vico.core.component.shape.Shapes
import com.patrykandpatryk.vico.core.entry.entryModelOf
import com.patrykandpatryk.vico.core.entry.entryOf
import com.patrykandpatryk.vico.compose.m3.style.m3ChartStyle
import com.samkit.costcircle.data.group.dto.MemberStatDto

@Composable
fun MemberBarChart(
    stats: List<MemberStatDto>,
    modifier: Modifier = Modifier
) {
    if (stats.isEmpty()) return

    // 1. Data Prep
    val chartEntryModel = remember(stats) {
        entryModelOf(stats.mapIndexed { index, stat ->
            entryOf(index.toFloat(), stat.total.toFloat())
        })
    }

    val horizontalAxisValueFormatter = remember(stats) {
        AxisValueFormatter<AxisPosition.Horizontal.Bottom> { value, _ ->
            stats.getOrNull(value.toInt())?.name?.split(" ")?.firstOrNull() ?: ""
        }
    }

    // 2. Styling the Bars (Gradient + Rounded Top)
    val primaryColor = MaterialTheme.colorScheme.primary
    val secondaryColor = MaterialTheme.colorScheme.tertiary

    val columnComponent = lineComponent(
        color = primaryColor,
        thickness = 22.dp,
        shape = Shapes.roundedCornerShape(
            topLeftPercent = 30,
            topRightPercent = 30
        )
    )




    // 3. Render
    ProvideChartStyle(m3ChartStyle()) {
        Chart(
            chart = columnChart(
                columns = listOf(columnComponent),
                // Important: This prevents single bars from becoming super wide
                spacing = 20.dp,
                innerSpacing = 8.dp
            ),
            model = chartEntryModel,
            startAxis = rememberStartAxis(
                valueFormatter = { value, _ -> "₹${value.toInt()}" }, // Cleaner Y-axis
//                maxLabelCount = 5 // Prevents cramping
            ),
            bottomAxis = rememberBottomAxis(
                valueFormatter = horizontalAxisValueFormatter,
                guideline = null
            ),
            modifier = modifier.height(220.dp)
        )
    }
}