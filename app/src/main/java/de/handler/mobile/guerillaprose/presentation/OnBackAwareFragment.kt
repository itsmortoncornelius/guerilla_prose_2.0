package de.handler.mobile.guerillaprose.presentation

import androidx.fragment.app.Fragment

abstract class OnBackAwareFragment: Fragment() {
    abstract fun onBackPressed(): Boolean
}
