package com.example.githubdemo.ui.home.adapter

import android.content.Context
import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView

/**
 * 自定义RecyclerView项目间距装饰器
 */
class ItemDecorationWithMargins(
    private val context: Context,
    private val marginDpValue: Int
) : RecyclerView.ItemDecoration() {

    // 将dp转换为px
    private val marginInPixels = (marginDpValue * context.resources.displayMetrics.density).toInt()

    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        super.getItemOffsets(outRect, view, parent, state)
        
        // 获取项目位置
        val position = parent.getChildAdapterPosition(view)
        val itemCount = parent.adapter?.itemCount ?: 0
        
        // 第一个项目左侧添加间距
        if (position == 0) {
            outRect.left = marginInPixels
        }
        
        // 所有项目右侧添加间距
        outRect.right = marginInPixels
    }
} 