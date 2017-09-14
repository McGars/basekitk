package com.mcgars.basekitk.features.simple

import android.os.Bundle
import com.mcgars.basekitk.features.base.BaseKitActivity


/**
 * Created by Владимир on 16.07.2015.
 * Запускаеться SimpleActivity, вытаскиваем getExtras(),
 * вызывается PageControllerGetValueOldFragment.newInstance() <- этот метод есть у object поэтому в классе его не надо прописывать
 * передаеться PageControllerGetValueOldFragment.setArguments(SimpleActivity.getExtras())
 * отображается фрагмент
 * SimpleActivity.ladPage(PageControllerGetValueOldFragment)
 * все это делается автоматом
 * [ru.altarix.basekit.library.tools.pagecontroller.PageController]
 * [ru.altarix.basekit.library.tools.pagecontroller.Page]
 * Можно передавать от 0 до 3 параметров (простых boolean, int, string)
 * Если нужно передать сложные параметры то воспользуйтесь методом .getParams() (Bundle)
 * У билдера есть множество разных комбинаций
 * Можно также не запускать отдельную активити, а загрузить фрагмет в текущей .loadPage()
 */
open class SimpleActivity : BaseKitActivity<ActivityController<*>>() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        loadViewController()
    }

    protected open fun loadViewController() {
        pageController.loadPage()
    }

    override fun initActivityController(): ActivityController<*>? {
        return pageController.initParamsFromActivity()
                .getActivityController()
    }

    override fun isShowArrow() = alwaysArrow
}
