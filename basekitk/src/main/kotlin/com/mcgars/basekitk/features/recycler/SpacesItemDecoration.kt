package ru.altarix.mos.reception.tools

import android.graphics.Rect
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View

class SpacesItemDecoration(private val space: Int, private val orientation: Int = LinearLayoutManager.VERTICAL) : RecyclerView.ItemDecoration() {

    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State?) {
        outRect.right = space
        outRect.bottom = space

        // Add top margin only for the first item to avoid double space between items
        if (parent.getChildAdapterPosition(view) == 0) {
            if (orientation == LinearLayoutManager.VERTICAL)
                outRect.top = space
            else {
                outRect.left = space
            }
        }
    }
}