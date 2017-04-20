/*
 * Copyright (c) 2015 Hannes Dorfmann.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package com.mcgars.basekitk.features.recycler2

import android.support.v7.widget.RecyclerView
import android.view.ViewGroup

/**
 * This delegate provide method to hook in this delegate to [RecyclerView.Adapter] lifecycle.
 * This "hook in" mechanism is provided by [AdapterDelegatesManager] and that is the
 * component
 * you have to use.

 * @param <T> The type of the data source
 * *
 * @author Hannes Dorfmann
 * *
 * @since 1.0
</T> */
interface AdapterDelegate<T> {

    /**
     * Called to determine whether this AdapterDelegate is the responsible for the given data
     * element.

     * @param items The data source of the Adapter
     * *
     * @param position The position in the datasource
     * *
     * @return true, if this item is responsible,  otherwise false
     */
    fun isForViewType(items: T, position: Int): Boolean

    /**
     * Creates the  [RecyclerView.ViewHolder] for the given data source item

     * @param parent The ViewGroup parent of the given datasource
     * *
     * @return The new instantiated [RecyclerView.ViewHolder]
     */
    fun onCreateViewHolder(parent: ViewGroup): RecyclerView.ViewHolder

    /**
     * Called to bind the [RecyclerView.ViewHolder] to the item of the datas source set

     * @param items The data source
     * *
     * @param position The position in the datasource
     * *
     * @param holder The [RecyclerView.ViewHolder] to bind
     * *
     * @param payloads A non-null list of merged payloads. Can be empty list if requires full update.
     */
    fun onBindViewHolder(items: T, position: Int,
                                            holder: RecyclerView.ViewHolder, payloads: List<Any>)

    /**
     * Called when a view created by this adapter has been recycled.

     *
     * A view is recycled when a [RecyclerView.LayoutManager] decides that it no longer
     * needs to be attached to its parent [RecyclerView]. This can be because it has
     * fallen out of visibility or a set of cached views represented by views still
     * attached to the parent RecyclerView. If an item view has large or expensive data
     * bound to it such as large bitmaps, this may be a good place to release those
     * resources.
     *
     *
     * RecyclerView calls this method right before clearing ViewHolder's internal data and
     * sending it to RecycledViewPool. This way, if ViewHolder was holding valid information
     * before being recycled, you can call [RecyclerView.ViewHolder.getAdapterPosition] to
     * get
     * its adapter position.

     * @param viewHolder The ViewHolder for the view being recycled
     */
    fun onViewRecycled(viewHolder: RecyclerView.ViewHolder) {
    }

    /**
     * Called by the RecyclerView if a ViewHolder created by this Adapter cannot be recycled
     * due to its transient state. Upon receiving this callback, Adapter can clear the
     * animation(s) that effect the View's transient state and return `true` so that
     * the View can be recycled. Keep in mind that the View in question is already removed from
     * the RecyclerView.
     *
     *
     * In some cases, it is acceptable to recycle a View although it has transient state. Most
     * of the time, this is a case where the transient state will be cleared in
     * [RecyclerView.Adapter.onBindViewHolder] call when View is
     * rebound to a new position.
     * For this reason, RecyclerView leaves the decision to the Adapter and uses the return
     * value of this method to decide whether the View should be recycled or not.
     *
     *
     * Note that when all animations are created by [RecyclerView.ItemAnimator], you
     * should never receive this callback because RecyclerView keeps those Views as children
     * until their animations are complete. This callback is useful when children of the item
     * views create animations which may not be easy to implement using an [ ].
     *
     *
     * You should *never* fix this issue by calling
     * `holder.itemView.setHasTransientState(false);` unless you've previously called
     * `holder.itemView.setHasTransientState(true);`. Each
     * `View.setHasTransientState(true)` call must be matched by a
     * `View.setHasTransientState(false)` call, otherwise, the state of the View
     * may become inconsistent. You should always prefer to end or cancel animations that are
     * triggering the transient state instead of handling it manually.
     * @param holder The ViewHolder containing the View that could not be recycled due to its
     * * transient state.
     * @return True if the View should be recycled, false otherwise. Note that if this method
     * * returns `true`, RecyclerView *will ignore* the transient state of
     * * the View and recycle it regardless. If this method returns `false`,
     * * RecyclerView will check the View's transient state again before giving a final decision.
     * * Default implementation returns false.
     */
    fun onFailedToRecycleView(holder: RecyclerView.ViewHolder) = true

    /**
     * Called when a view created by this adapter has been attached to a window.

     *
     * This can be used as a reasonable signal that the view is about to be seen
     * by the user. If the adapter previously freed any resources in
     * [ onViewDetachedFromWindow][RecyclerView.Adapter.onViewDetachedFromWindow]
     * those resources should be restored here.

     * @param holder Holder of the view being attached
     */
    fun onViewAttachedToWindow(holder: RecyclerView.ViewHolder) {
    }

    /**
     * Called when a view created by this adapter has been detached from its window.

     *
     * Becoming detached from the window is not necessarily a permanent condition;
     * the consumer of an Adapter's views may choose to cache views offscreen while they
     * are not visible, attaching an detaching them as appropriate.

     * @param holder Holder of the view being detached
     */
    fun onViewDetachedFromWindow(holder: RecyclerView.ViewHolder) {
    }
}