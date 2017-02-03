package ru.mos.helloworldk.features.animatorHandlers

import android.annotation.TargetApi
import android.os.Build
import android.transition.*
import android.view.View
import android.view.ViewGroup
import com.bluelinelabs.conductor.changehandler.TransitionChangeHandler

@TargetApi(Build.VERSION_CODES.LOLLIPOP)
class ArcFadeMoveChangeHandler : TransitionChangeHandler() {

    override fun getTransition(container: ViewGroup, from: View?, to: View?, isPush: Boolean): Transition {
        val transition = TransitionSet()
                .setOrdering(TransitionSet.ORDERING_SEQUENTIAL)
                .addTransition(Fade(Fade.OUT))
                .addTransition(TransitionSet().addTransition(ChangeBounds()).addTransition(ChangeClipBounds()).addTransition(ChangeTransform()))
                .addTransition(Fade(Fade.IN))

        transition.pathMotion = ArcMotion()

        return transition
    }

}
