package ru.mos.helloworldk.features.animatorHandlers

import android.view.View
import com.bluelinelabs.conductor.changehandler.FadeChangeHandler
import com.bluelinelabs.conductor.changehandler.TransitionChangeHandlerCompat

class ArcFadeMoveChangeHandlerCompat : TransitionChangeHandlerCompat(ArcFadeMoveChangeHandler(), FadeChangeHandler())
