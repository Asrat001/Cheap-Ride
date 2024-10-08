package com.example.betterp.Utils


import android.content.Context
import com.afollestad.materialdialogs.LayoutMode
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.bottomsheets.BottomSheet
import com.afollestad.materialdialogs.bottomsheets.expandBottomSheet
import com.afollestad.materialdialogs.bottomsheets.setPeekHeight
import com.afollestad.materialdialogs.customview.customView


class DialogUtil {


    interface CallBack {
        fun onDismiss()
        fun onApprove()
    }
    interface NormalCallBack{
        fun onDismiss()
    }



    fun createCustomBottomDialog(context: Context, id: Int) =
        MaterialDialog(context, BottomSheet(LayoutMode.MATCH_PARENT)).show {
            setPeekHeight(1000, )
            title(text = "Your Ride Cost You ")
            customView(id, horizontalPadding = true, noVerticalPadding = false)
            cornerRadius(16f)
        }






}

