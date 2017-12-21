package ru.mos.helloworldk.features.animatorHandlers

import com.bluelinelabs.conductor.changehandler.FadeChangeHandler
import com.bluelinelabs.conductor.changehandler.TransitionChangeHandlerCompat

class ArcFadeMoveChangeHandlerCompat : TransitionChangeHandlerCompat(ArcFadeMoveChangeHandler(), FadeChangeHandler())
