package co.kr.taehoon.coroutine_sample.util

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView

class GridSpaceDecoration(
    private val spaceHorizontal: Int = 0,
    private val spaceVertical: Int = 0,
    private val spanCount: Int = 0
) : RecyclerView.ItemDecoration() {


    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
        val position: Int = parent.getChildAdapterPosition(view) // item position

        var spanSize: Int = 0
        var spanIndex: Int = 0

        (view.layoutParams as? GridLayoutManager.LayoutParams)?.let {
            spanIndex = it.spanIndex
            spanSize = it.spanSize
        } ?: 0

        if (spanSize == spanCount) {
            return
        }
        val column: Int = position / spanCount // item column


        if (column != 0)//최상단
            outRect.top = spaceVertical
        if (spanIndex > 0 ) {//가장왼쪽 제외
            outRect.left = spaceHorizontal
        }
    }
}