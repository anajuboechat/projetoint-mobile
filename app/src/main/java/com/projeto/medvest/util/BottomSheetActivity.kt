package com.projeto.medvest.util

import androidx.appcompat.app.AppCompatActivity
import com.projeto.medvest.R

class BottomSheetActivity {
    fun AppCompatActivity.showBottomSheetActivity(
        title: String,
        message: String,
        confirmText: String,
        cancelText: String,
        onConfirm: (() -> Unit)? = null
    ) {
        supportFragmentManager.findFragmentById(R.id.nav_host_fragment)
            ?.childFragmentManager
            ?.fragments
            ?.firstOrNull()
            ?.showBottomSheet(title, message, confirmText, cancelText, onConfirm)
    }

}