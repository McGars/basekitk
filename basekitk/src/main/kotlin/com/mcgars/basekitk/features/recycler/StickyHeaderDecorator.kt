package com.mcgars.basekitk.features.recycler

import android.graphics.Canvas
import android.graphics.Rect
import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import java.util.*

/**
 * Created by Владимир on 17.10.2016.
 * https://matalamaki.fi/2015/09/24/implementing-sticky-recyclerview-grid-headers-on-android/
 */

class StickyHeaderDecorator(var adapter: StickyHeaderAdapter<RecyclerView.ViewHolder>, var spanCount: Int = 1) : RecyclerView.ItemDecoration() {
    private val mHeaderCache = HashMap<Long, RecyclerView.ViewHolder>()
    internal var tempRect = Rect()
    private var isCache: Boolean = false

    fun setCache(cache: Boolean) {
        isCache = cache
    }

    /**
     * checks if item is last before next header
     * aka if next items id is different than current
     * @param itemPosition
     * @return
     */
    private fun isLastBeforeNextHeader(itemPosition: Int): Boolean {
        //header id of item in question
        val headerId = adapter.getHeaderId(itemPosition)

        //next header id, -1 if out of bounds, aka the rest of the row must be filled!
        var nextHeaderId: Long = -1

        //next item position, aka next item in question
        val nextItemPosition = itemPosition + 1

        //checking if is within the bounds of the adapter
        if (nextItemPosition >= 0 && nextItemPosition < (adapter as RecyclerView.Adapter<*>).itemCount) {
            nextHeaderId = adapter.getHeaderId(nextItemPosition)
        }

        //checking if the ids different
        return headerId != nextHeaderId
    }

    fun getSpanSize(position: Int): Int {
        if (isLastBeforeNextHeader(position)) {

            //gets the number of items before this particular position in range of 0..spancount - 1
            val categoryOffset = getNumberOfItemsBeforePositionInCategory(position)

            //gets column index in range of 0..spancount - 1
            val columnIndex = categoryOffset % spanCount

            //gets number of extra columns in range of 0..spancount - 1
            val extraColumns = spanCount - (columnIndex + 1)

            return 1 + extraColumns
        } else {
            //is just any ordinary item, takes one column width..
            return 1
        }
    }

    private fun getNumberOfItemsBeforePositionInCategory(position: Int): Int {
        val categoryId = adapter.getHeaderId(position)

        return (1..position - 1)
                .firstOrNull { adapter.getHeaderId(position - it) != categoryId }
                ?.let { it - 1 }
                ?: position
    }

    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State?) {
        super.getItemOffsets(outRect, view, parent, state)

        val itemPosition = parent.getChildAdapterPosition(view)

        if (itemPosition == RecyclerView.NO_POSITION) {
            return
        }

        val underHeader = isUnderHeader(itemPosition)

        if (underHeader) {
            //            Log.d(getClass().getSimpleName(), "getItemOffsets: " + itemPosition);
            //            View header = parent.getChildViewHolder(parent.getChildAt(itemPosition)).itemView;
            //            View header = parent.getChildAt(itemPosition);
            val hHeader = getHeader(parent, itemPosition)
            if (hHeader != null) {
                val header = hHeader.itemView
                if (header != null)
                    outRect.top = header.height
            }
        }
    }

    /**
     * checks if item is "under header"
     *
     *
     * Items is under header if any of the following conditions are true:
     *
     *
     * a) within spanCount the header id has changed once

     * @param itemPosition
     * *
     * @return
     */
    private fun isUnderHeader(itemPosition: Int): Boolean {
        if (itemPosition == 0) {
            return true
        }

        //get current items header id
        val headerId = adapter.getHeaderId(itemPosition)

        //loop through each item within spancount
        for (i in 1..spanCount + 1 - 1) {
            var previousHeaderId: Long = -1

            val previousItemPosition = itemPosition - i

            //gets previous items headerId
            if (previousItemPosition >= 0 && previousItemPosition < (adapter as RecyclerView.Adapter<*>).itemCount) {
                previousHeaderId = adapter.getHeaderId(previousItemPosition)
            }

            //checks if header id at given position is different from previous header id and if so, returns true to indicate this item belongs under the header
            if (headerId != previousHeaderId) {
                return true
            }
        }

        return false
    }

    override fun onDrawOver(c: Canvas, parent: RecyclerView, state: RecyclerView.State?) {
        super.onDrawOver(c, parent, state)

        val childCount = parent.childCount

        //checks if there's any childs, aka can we even have any header?
        if (childCount <= 0 || (adapter as RecyclerView.Adapter<*>).itemCount <= 0) {
            return
        }

        //stores the "highest" seen top value of any header to perform the pusheroo of topmost header
        var highestTop = Integer.MAX_VALUE
        //loops through childs in the recyclerview on reverse order to perform the pushing of uppermost header faster, because before it, there is the next headers top stored to highestTop
        for (i in childCount - 1 downTo 0) {
            val itemView = parent.getChildAt(i)

            //fetches the position within adapter
            val position = parent.getChildAdapterPosition(itemView)

            if (position == RecyclerView.NO_POSITION) {
                continue
            }

            //only draw if is the first withing recyclerview, aka is the first view in whole tree or if the item in question is the first under its category(or header..)
            if (i == 0 || isFirstUnderHeader(position)) {

                val headerHolder = getHeader(parent, position)
                if (headerHolder?.itemView == null)
                    continue

                //fetches the header from header provider, which is basically just call to adapters getHeader/bindHeader
                val header = headerHolder.itemView

                //calculates the translations of the header within view, which is on top of the give item
                val translationX = parent.left + parent.paddingLeft
                val translationY = Math.max(itemView.top - header.height, 0)

                tempRect.set(translationX, translationY, translationX + header.width,
                        translationY + header.height)

                //moves the header so it is pushed by the following header upwards
                if (tempRect.bottom > highestTop) {
                    tempRect.offset(0, highestTop - tempRect.bottom)
                }

                //draws the actual header
                drawHeader(parent, c, header, tempRect)

                //stores top of the header to help with the pushing of topmost header
                highestTop = tempRect.top
            }
        }
    }

    private fun isFirstUnderHeader(position: Int): Boolean {
        val headerId = adapter.getHeaderId(position)
        if (headerId == RecyclerView.NO_POSITION.toLong())
            return false
        return position == 0 || headerId != adapter.getHeaderId(position - 1)
    }

    fun invalidate() {
        mHeaderCache.clear()
    }

    private fun getHeader(parent: RecyclerView, position: Int): RecyclerView.ViewHolder? {
        val key = adapter.getHeaderId(position)
        if (key == RecyclerView.NO_POSITION.toLong())
            return null

        if (isCache && mHeaderCache.containsKey(key)) {
            return mHeaderCache[key]
        } else {
            val holder = adapter.onCreateHeaderViewHolder(parent)
            val header = holder.itemView


            adapter.onBindHeaderViewHolder(holder, position)

            val widthSpec = View.MeasureSpec.makeMeasureSpec(parent.measuredWidth, View.MeasureSpec.EXACTLY)
            val heightSpec = View.MeasureSpec.makeMeasureSpec(parent.measuredHeight, View.MeasureSpec.UNSPECIFIED)

            val childWidth = ViewGroup.getChildMeasureSpec(widthSpec,
                    parent.paddingLeft + parent.paddingRight, header.layoutParams.width)
            val childHeight = ViewGroup.getChildMeasureSpec(heightSpec,
                    parent.paddingTop + parent.paddingBottom, header.layoutParams.height)

            header.measure(childWidth, childHeight)
            header.layout(0, 0, header.measuredWidth, header.measuredHeight)

            mHeaderCache.put(key, holder)

            return holder
        }
    }


    fun drawHeader(recyclerView: RecyclerView, canvas: Canvas, header: View, offset: Rect) {
        canvas.save()

//        if(offset.top == 0) {
//            header.setBackgroundColor(Color.BLUE)
////            ViewCompat.setElevation(header, 10f)
//        } else {
//            header.setBackgroundColor(Color.WHITE)
//        }

        canvas.translate(offset.left.toFloat(), offset.top.toFloat())
        header.draw(canvas)
        canvas.restore()
    }
}
