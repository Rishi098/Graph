package com.itamazons.graphandchart

import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import lecho.lib.hellocharts.animation.ChartAnimationListener
import lecho.lib.hellocharts.gesture.ZoomType
import lecho.lib.hellocharts.listener.LineChartOnValueSelectListener
import lecho.lib.hellocharts.model.*
import lecho.lib.hellocharts.util.ChartUtils
import lecho.lib.hellocharts.view.Chart
import lecho.lib.hellocharts.view.LineChartView


class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction().add(R.id.container, PlaceholderFragment()).commit()
        }
    }


    class PlaceholderFragment : Fragment() {
        private var chart: LineChartView? = null
        private var data: LineChartData? = null
        private var numberOfLines = 1
        private val maxNumberOfLines = 4
        private val numberOfPoints = 8
        var randomNumbersTab = Array(maxNumberOfLines) { FloatArray(numberOfPoints) }
        private var hasAxes = true
        private var hasAxesNames = true
        private var hasLines = true
        private var hasPoints = true
        private var shape = ValueShape.CIRCLE
        private var isFilled = false
        private var hasLabels = false
        private var isCubic = false
        private var hasLabelForSelected = false
        private var pointsHaveDifferentColor = false
        private var hasGradientToTransparent = false


        override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
            setHasOptionsMenu(true)
            val rootView: View = inflater.inflate(R.layout.fragment_line_chart, container, false)
            chart = rootView.findViewById(R.id.chart)
            chart!!.onValueTouchListener = ValueTouchListener()

            // Generate some random values.
            generateValues()
            generateData()

            // Disable viewport recalculations, see toggleCubic() method for more info.
            chart!!.isViewportCalculationEnabled = false
            resetViewport()
            return rootView
        }

        // MENU

        override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
            super.onCreateOptionsMenu(menu, inflater)
            inflater.inflate(R.menu.line_chart, menu)
        }

        override fun onOptionsItemSelected(item: MenuItem): Boolean {
            val id: Int = item.getItemId()
            if (id == R.id.action_toggle_labels) {
                toggleLabels()
                return true
            }
            if (id == R.id.action_toggle_selection_mode) {
                toggleLabelForSelected()
                Toast.makeText(getActivity(),
                        "Selection mode set to " + chart!!.isValueSelectionEnabled + " select any point.",
                        Toast.LENGTH_SHORT).show()
                return true
            }
            if (id == R.id.action_toggle_touch_zoom) {
                chart!!.isZoomEnabled = !chart!!.isZoomEnabled
                Toast.makeText(getActivity(), "IsZoomEnabled " + chart!!.isZoomEnabled, Toast.LENGTH_SHORT).show()
                return true
            }
            return super.onOptionsItemSelected(item)
        }

        private fun generateValues() {
            for (i in 0 until maxNumberOfLines) {
                for (j in 0 until numberOfPoints) {
                    randomNumbersTab[i][j] = Math.random().toFloat() * 6
                }
            }
        }


        private fun resetViewport() {
            val v = Viewport(chart!!.maximumViewport)
            v.bottom = 0f
            v.top = 1.96f
            v.left = 0f
            v.right = numberOfPoints - 1.toFloat()
            chart!!.maximumViewport = v
            chart!!.currentViewport = v
        }

        private fun generateData() {
            val lines: MutableList<Line> = ArrayList<Line>()
            for (i in 0 until numberOfLines) {
                val values: MutableList<PointValue> = ArrayList()
                for (j in 0 until numberOfPoints) {
                    values.add(PointValue(j.toFloat(), randomNumbersTab[i][j]))
                }
                val line = Line(values)
                line.setColor(ChartUtils.COLORS[i])
                line.setShape(shape)
                line.setCubic(isCubic)
                line.setFilled(isFilled)
                line.setHasLabels(hasLabels)
                line.setHasLabelsOnlyForSelected(hasLabelForSelected)
                line.setHasLines(hasLines)
                line.setHasPoints(hasPoints)
//                line.setHasGradientToTransparent(hasGradientToTransparent)
                if (pointsHaveDifferentColor) {
                    line.setPointColor(ChartUtils.COLORS[(i + 1) % ChartUtils.COLORS.size])
                }
                lines.add(line)
            }
            data = LineChartData(lines)
            if (hasAxes) {
                val axisX = Axis()
                val axisY: Axis = Axis().setHasLines(false)
                if (hasAxesNames) {
                    axisX.setName("Axis X")
                    axisY.setName("Axis Y")
                }
                data!!.axisXBottom = axisX
                data!!.axisYLeft = axisY
            } else {
                data!!.axisXBottom = null
                data!!.axisYLeft = null
            }
            data!!.baseValue = Float.NEGATIVE_INFINITY
            chart!!.lineChartData = data
        }




        private fun toggleLabels() {
            hasLabels = !hasLabels
            if (hasLabels) {
                hasLabelForSelected = false
                chart!!.isValueSelectionEnabled = hasLabelForSelected
            }
            generateData()
        }

        private fun toggleLabelForSelected() {
            hasLabelForSelected = !hasLabelForSelected
            chart!!.isValueSelectionEnabled = hasLabelForSelected
            if (hasLabelForSelected) {
                hasLabels = false
            }
            generateData()
        }

        private fun prepareDataAnimation() {
            for (line in data!!.lines) {
                for (value in line.values) {
                    // Here I modify target only for Y values but it is OK to modify X targets as well.
                    value.setTarget(value.x, Math.random().toFloat() * 100)
                }
            }
        }

        private inner class ValueTouchListener : LineChartOnValueSelectListener {
            override fun onValueSelected(lineIndex: Int, pointIndex: Int, value: PointValue) {
                Toast.makeText(getActivity(), "Selected: $value", Toast.LENGTH_SHORT).show()
            }

            override fun onValueDeselected() {
                // TODO Auto-generated method stub
            }
        }
    }
}