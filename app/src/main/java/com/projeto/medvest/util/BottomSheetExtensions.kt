package com.projeto.medvest.util

import android.content.res.ColorStateList
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.shape.CornerFamily
import com.google.android.material.shape.MaterialShapeDrawable
import com.google.android.material.shape.ShapeAppearanceModel
import com.projeto.medvest.R

fun Fragment.showBottomSheet(
    message: String,
    title: String? = null,
    confirmText: String? = null,
    cancelText: String? = "OK",
    onConfirm: (() -> Unit)? = null
) {
    val dialog = BottomSheetDialog(requireContext(), R.style.BottomSheetTheme)
    val view = LayoutInflater.from(requireContext())
        .inflate(R.layout.bottom_sheet_simulado, null)

    val tvTitle = view.findViewById<TextView>(R.id.sheetTitle)
    val tvMessage = view.findViewById<TextView>(R.id.sheetMessage)
    val btnConfirm = view.findViewById<TextView>(R.id.btnConfirm)
    val btnCancel = view.findViewById<TextView>(R.id.btnCancel)

    // Título
    if (title.isNullOrBlank()) {
        tvTitle.visibility = View.GONE
    } else {
        tvTitle.visibility = View.VISIBLE
        tvTitle.text = title
    }

    // Mensagem
    tvMessage.text = message

    // Botão Confirmar
    if (confirmText.isNullOrBlank()) {
        btnConfirm.visibility = View.GONE
    } else {
        btnConfirm.visibility = View.VISIBLE
        btnConfirm.text = confirmText
        btnConfirm.setOnClickListener {
            dialog.dismiss()
            onConfirm?.invoke()
        }
    }

    // Botão Cancelar
    if (cancelText.isNullOrBlank()) {
        btnCancel.visibility = View.GONE
    } else {
        btnCancel.visibility = View.VISIBLE
        btnCancel.text = cancelText
        btnCancel.setOnClickListener { dialog.dismiss() }
    }

    dialog.setContentView(view)
    dialog.show()

    // ✨ A PARTIR DAQUI: ESTILIZAÇÃO DO BOTTOM SHEET
    val bottomSheet =
        dialog.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet)

    bottomSheet?.let {
        // aplica arredondamento no topo
        val materialBg = MaterialShapeDrawable(
            ShapeAppearanceModel.builder()
                .setTopLeftCorner(CornerFamily.ROUNDED, 32f)
                .setTopRightCorner(CornerFamily.ROUNDED, 32f)
                .build()
        ).apply {
            fillColor = ColorStateList.valueOf(
                ContextCompat.getColor(requireContext(), R.color.bege_fundo)
            )
        }

        it.background = materialBg
    }

    // muda cor da barra inferior do sistema
    dialog.window?.navigationBarColor =
        ContextCompat.getColor(requireContext(), R.color.bege_fundo)
}
