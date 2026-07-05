package dev.hau.foldcheck

import android.graphics.Typeface
import android.os.Bundle
import android.widget.TextView
import androidx.activity.ComponentActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.window.layout.FoldingFeature
import androidx.window.layout.WindowInfoTracker
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    private lateinit var textView: TextView

    private var lastFold: FoldingFeature? = null
    private var foldDataReceived: Boolean = false
    private var lastWidthPx: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        textView = TextView(this).apply {
            textSize = 22f
            setPadding(48, 96, 48, 48)
            typeface = Typeface.MONOSPACE
        }
        setContentView(textView)

        textView.addOnLayoutChangeListener { _, left, _, right, _, _, _, _, _ ->
            lastWidthPx = right - left
            render()
        }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                WindowInfoTracker.getOrCreate(this@MainActivity)
                    .windowLayoutInfo(this@MainActivity)
                    .collect { info ->
                        lastFold = info.displayFeatures.filterIsInstance<FoldingFeature>().firstOrNull()
                        foldDataReceived = true
                        render()
                    }
            }
        }

        render()
    }

    private fun render() {
        val foldState = if (!foldDataReceived) {
            "等待資料…"
        } else {
            when (lastFold?.state) {
                FoldingFeature.State.FLAT -> "FLAT"
                FoldingFeature.State.HALF_OPENED -> "HALF_OPENED"
                else -> "無折疊特徵"
            }
        }

        val orientation = lastFold?.orientation?.toString() ?: "無"
        val bounds = lastFold?.bounds?.toString() ?: "無"

        val widthPx = lastWidthPx
        val widthDp = widthPx / resources.displayMetrics.density

        val widthLine = if (widthPx == 0) {
            "量測中…"
        } else {
            "${widthPx}px / ${"%.1f".format(widthDp)}dp"
        }

        val expandedLine = if (widthPx == 0) {
            "量測中…"
        } else if (widthDp >= 600f) {
            "展開"
        } else {
            "收合"
        }

        textView.text = buildString {
            append("折疊狀態：").append(foldState).append('\n')
            append("鉸鏈方向：").append(orientation).append('\n')
            append("鉸鏈位置：").append(bounds).append('\n')
            append("視窗寬度：").append(widthLine).append('\n')
            append("是否展開：").append(expandedLine)
        }
    }
}
