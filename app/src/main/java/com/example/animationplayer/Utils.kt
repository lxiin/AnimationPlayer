package com.example.animationplayer

import android.content.Context

/**
 *
 *****************************************
 *  Created By LiXin  2/23/21 2:38 PM
 *****************************************
 *
 */
object Utils {
    fun dp2px(context: Context, dp: Int): Int {
        val scale: Float = context.resources.displayMetrics.density
        return (dp * scale + 0.5f).toInt()
    }
}
