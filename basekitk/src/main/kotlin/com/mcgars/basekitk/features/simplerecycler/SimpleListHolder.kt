package com.mcgars.basekitk.features.simplerecycler

import androidx.recyclerview.widget.RecyclerView
import android.view.View
import android.widget.TextView
import com.mcgars.basekitk.R
import com.mcgars.basekitk.tools.find

/**
 * Created by gars on 05.01.2017.

 */
class SimpleListHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    val tvTitle: TextView? = itemView.find(R.id.tvTitle)
    val tvDescription: TextView? = itemView.find(R.id.tvDescription)
}
