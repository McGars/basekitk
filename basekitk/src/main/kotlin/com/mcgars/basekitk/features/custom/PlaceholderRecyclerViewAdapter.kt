package com.mcgars.basekitk.features.custom

import androidx.annotation.IdRes
import androidx.annotation.LayoutRes
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import com.mcgars.basekitk.R
import com.mcgars.basekitk.features.recycler2.AdapterDelegate
import com.mcgars.basekitk.features.recycler2.KitAdapter

/**
 * Адаптер [RecyclerView] для отображения плейсхолдера при пустом списке.
 * Является оберткой для обычного адаптера.
 * Если список в [.originalAdapter] пуст, то показывается плейсхолдер, иначе сам список.
 * Плейсхолдер отобразится только после первого вызова одного из notify- методов, если данных нет.
 * Работа проверена на [GridLayoutManager] и [LinearLayoutManager]

 * Создает плейсхолдер-адаптер с вьюхой по умолчанию
 * @param originalAdapter адаптер, отображающий сам список
 * *
 * @param placeholderViewId - id лейаута плейсхолдера
 * *
 * @param errorTextViewId - id [TextView], в котором будет отображено сообщение об ошибке.
 */

class PlaceholderRecyclerViewAdapter<T>(
        var originalAdapter: RecyclerView.Adapter<RecyclerView.ViewHolder>,
        @LayoutRes
        private val placeholderViewId: Int = R.layout.basekit_view_placeholder,
        @IdRes
        private val errorTextViewId: Int = R.id.tvEmptyListMessage
) : RecyclerView.Adapter<RecyclerView.ViewHolder>(), KitAdapter<T> {

    /**
     * Позволяет при необходимости изменить [Integer], соответствующий viewType. По умолчанию - [Integer.MAX_VALUE]
     * @param placeholderViewType желаемый viewType плейсхолдера.
     */
    var placeholderViewType = Integer.MAX_VALUE

    private var message: String? = null

    private var recyclerView: RecyclerView? = null

    /**
     * В случае если оригинальный адаптер установлен до загрузки данных на сервере
     * то будет показано сообщение что нет данных и после покажутся данные
     * При пером нотифае текст не показывается
     */
    var isNotified: Boolean = true

    private var prevEmpty: Boolean = false

    /**
     * Возвращает текущий модуль для подготовки [RecyclerView] к отображению плейсхолдера
     */
    /**
     * Задает модуль для подготовки [RecyclerView] к отображению плейсхолдера
     * @param placeholderToggleListener имплементация интерфейса
     */
    var placeholderToggleListener: PlaceholderToggleListener? = null

    /**
     * Задает сообщение, отображаемое в плейсхолдере

     * @param mMessage сообщение
     */
    fun setMessage(mMessage: String) {
        this.message = mMessage
    }

    override fun getItemViewType(position: Int): Int {
        return if (isEmpty) placeholderViewType else originalAdapter.getItemViewType(position)
    }

    /**
     * Подготавливает [RecyclerView] к отображению плейсхолдера (переформатирует [android.support.v7.widget.RecyclerView.LayoutManager])
     * @param recyclerView - [RecyclerView], к которому применяем изменения
     */
    private fun prepareRecyclerView(recyclerView: RecyclerView?) {
        isNotified = true
        if (recyclerView != null && (isEmpty || prevEmpty)) { // Делаем перестройки RecyclerView только при смене состояния с пустого на заполненный и назад
            placeholderToggleListener?.onPlaceholderToggle(recyclerView, isEmpty)
        }
        prevEmpty = isEmpty
    }

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        this.recyclerView = recyclerView
        if (message == null) {
            message = recyclerView.context.getString(R.string.no_data)
        }
        if (placeholderToggleListener == null && recyclerView.layoutManager is GridLayoutManager) {
            placeholderToggleListener = GRID_LAYOUT_MANAGER_PREPARATION
        }

        val prevNotify = isNotified
        prepareRecyclerView(recyclerView)
        isNotified = prevNotify
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        if (viewType == placeholderViewType) {
            val layoutInflater = LayoutInflater.from(parent.context)
            val v = layoutInflater.inflate(placeholderViewId, parent, false)
            val holder = ViewHolder(v)

            v.layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.MATCH_PARENT
            )
            return holder
        } else {
            return originalAdapter.createViewHolder(parent, viewType)
        }

    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (getItemViewType(position) == placeholderViewType) {
            holder.itemView.visibility = if (isNotified) View.VISIBLE else View.GONE
            (holder as PlaceholderRecyclerViewAdapter<*>.ViewHolder).setMessage(message)
        } else {
            originalAdapter.bindViewHolder(holder, position)
        }
    }

    override fun getItemCount(): Int {
        return if (isEmpty) 1 else originalAdapter.itemCount
    }

    override fun getItem(position: Int): T {
        if (isEmpty) throw RuntimeException("List is empty")
        if (originalAdapter !is KitAdapter<*>) RuntimeException("originalAdapter must implementation KitAdapter ")
        return (originalAdapter as KitAdapter<T>).getItem(position)
    }

    internal inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private var tvMessage: TextView? = itemView.findViewById<View>(errorTextViewId)?.run {
            if (this is TextView) this else null
        }

        fun setMessage(message: String?) {
            tvMessage?.text = message
        }
    }

    private val isEmpty: Boolean
        get() = originalAdapter.itemCount == 0

    /**
     * Этот [RecyclerView.AdapterDataObserver] обрабатывает вызовы notify* методов, вызванных из
     * оригинального адаптера и обновляет обертку (и тем самым [RecyclerView] т.к. он подписан на нее)
     */
    protected var originalAdapterObserver: RecyclerView.AdapterDataObserver = object : RecyclerView.AdapterDataObserver() {
        override fun onChanged() {
            this@PlaceholderRecyclerViewAdapter.notifyDataSetChanged()
        }

        override fun onItemRangeChanged(positionStart: Int, itemCount: Int) {
            this@PlaceholderRecyclerViewAdapter.notifyItemRangeChanged(positionStart, itemCount)
        }

        override fun onItemRangeChanged(positionStart: Int, itemCount: Int, payload: Any?) {
            this@PlaceholderRecyclerViewAdapter.notifyItemRangeChanged(positionStart, itemCount, payload)
        }

        override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
            this@PlaceholderRecyclerViewAdapter.notifyItemRangeInserted(positionStart, itemCount)
        }

        override fun onItemRangeRemoved(positionStart: Int, itemCount: Int) {
            this@PlaceholderRecyclerViewAdapter.notifyItemRangeRemoved(positionStart, itemCount)
        }

        override fun onItemRangeMoved(fromPosition: Int, toPosition: Int, itemCount: Int) {
            this@PlaceholderRecyclerViewAdapter.notifyDataSetChanged()
        }
    }

    /**
     * Этот [RecyclerView.AdapterDataObserver] обрабатывает вызовы notify* методов перед [RecyclerView]
     * и выполняет все подготовительные операции.
     */
    private val placeholderAdapterObserver = object : RecyclerView.AdapterDataObserver() {
        override fun onChanged() {
            prepareRecyclerView(recyclerView)
        }

        override fun onItemRangeChanged(positionStart: Int, itemCount: Int) {
            prepareRecyclerView(recyclerView)
        }

        override fun onItemRangeChanged(positionStart: Int, itemCount: Int, payload: Any?) {
            prepareRecyclerView(recyclerView)
        }

        override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
            prepareRecyclerView(recyclerView)
        }

        override fun onItemRangeRemoved(positionStart: Int, itemCount: Int) {
            prepareRecyclerView(recyclerView)
        }

        override fun onItemRangeMoved(fromPosition: Int, toPosition: Int, itemCount: Int) {
            prepareRecyclerView(recyclerView)
        }
    }

    /**
     * Интерфейс для кастомизации подготовки [RecyclerView] к отображению плейсхолдера на весь размер.
     * Например для [GridLayoutManager] необходимо изменить SpanCount на 1, а затем вернуть назад при отображении списка.
     * Или спрятать конфликтующие с плейсхолдером [android.support.v7.widget.RecyclerView.ItemDecoration].
     * Или выполнить любые другие операции, изначально не предусмотренные данным адаптером.
     * @see .GRID_LAYOUT_MANAGER_PREPARATION пример использования в случае {@link GridLayoutManager}
     */
    interface PlaceholderToggleListener {
        /**
         * Метод вызывается перед оповещением [RecyclerView] о смене состояния списка.
         * Вызывается **только при смене состояния**** с заполненного (в оригинальном адаптере есть
         * элементы) на пустое (элементов нет, нужно показать плейсхолдер) и обратно.
         * @param recyclerView [RecyclerView], который необходимо подготовить
         * *
         * @param empty состояние адаптера: true - пустой, false - в списке есть элементы.
         ** */
        fun onPlaceholderToggle(recyclerView: RecyclerView, empty: Boolean)
    }

    companion object {

        /**
         * Подготавливает [RecyclerView] с [GridLayoutManager] для отображения плейсхолдера (меняет колчество столбцов на 1 и обратно)
         */
        private val GRID_LAYOUT_MANAGER_PREPARATION: PlaceholderToggleListener = object : PlaceholderToggleListener {
            private var originalGridSpanCount: Int = 0

            override fun onPlaceholderToggle(recyclerView: RecyclerView, empty: Boolean) {
                if (recyclerView.layoutManager is GridLayoutManager) {
                    val gridLayoutManager = recyclerView.layoutManager as GridLayoutManager
                    if (empty) {
                        originalGridSpanCount = gridLayoutManager.spanCount
                        gridLayoutManager.spanCount = 1
                    } else {
                        gridLayoutManager.spanCount = originalGridSpanCount
                    }
                }
            }
        }
    }

    init {
        this.originalAdapter.registerAdapterDataObserver(originalAdapterObserver)
        this.registerAdapterDataObserver(placeholderAdapterObserver)
    }

    override fun removeItemByPosition(position: Int) {
        if (originalAdapter is KitAdapter<*>) {
            (originalAdapter as KitAdapter<*>).removeItemByPosition(position)
        }
    }

    override fun removeItem(item: T) {
        if (originalAdapter is KitAdapter<*>) {
            (originalAdapter as KitAdapter<T>).removeItem(item)
        }
    }

    override fun addItem(item: T) {
        if (originalAdapter is KitAdapter<*>) {
            (originalAdapter as KitAdapter<T>).addItem(item)
        }
    }

    override fun addItem(position: Int, item: T) {
        if (originalAdapter is KitAdapter<*>) {
            (originalAdapter as KitAdapter<T>).addItem(position, item)
        }
    }

    override fun getDelegates(): List<AdapterDelegate<T>>? {
        return if (originalAdapter is KitAdapter<*>) {
            (originalAdapter as KitAdapter<T>).getDelegates()
        } else null
    }

}