package com.example.chexanhfe.utils

import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.TranslateAnimation
import android.animation.ObjectAnimator
import android.animation.AnimatorListenerAdapter
import android.os.Handler
import android.os.Looper

object AnimationUtils {
    fun slideUpPanel(view: View, distance: Float, bottomBar: View, duration: Long = 500) {
        // Hiển thị view (panel) khi thực hiện thao tác trượt lên
        view.visibility = View.VISIBLE

        // Sử dụng ObjectAnimator để đảm bảo vị trí của view được cập nhật trong quá trình animation
        val slideUpAnimator = ObjectAnimator.ofFloat(view, "translationY", distance, 0f)
        slideUpAnimator.duration = duration
        slideUpAnimator.interpolator = AccelerateDecelerateInterpolator()

        slideUpAnimator.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationStart(animation: android.animation.Animator) {
                // Ẩn thanh bottom ngay khi bắt đầu animation
                bottomBar.visibility = View.GONE
            }

            override fun onAnimationEnd(animation: android.animation.Animator) {
                // Đảm bảo view đã được đặt ở vị trí cuối cùng
                view.translationY = 0f
            }
        })

        slideUpAnimator.start()
    }

    fun slideDownPanel(view: View, distance: Float, bottomBar: View, duration: Long = 500) {
        // Xóa bất kỳ animation hiện tại nào đang áp dụng trên view
        view.clearAnimation()

        // Sử dụng ObjectAnimator để đảm bảo vị trí của view được cập nhật đúng
        val slideDownAnimator = ObjectAnimator.ofFloat(view, "translationY", view.translationY, distance)
        slideDownAnimator.duration = duration
        slideDownAnimator.interpolator = AccelerateDecelerateInterpolator()

        slideDownAnimator.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: android.animation.Animator) {
                // Hiển thị thanh bottom sau khi animation hoàn tất
                bottomBar.visibility = View.VISIBLE

                // Thêm một chút delay để đảm bảo hiệu ứng mượt mà
                Handler(Looper.getMainLooper()).postDelayed({
                    // Đảm bảo panel ở đúng vị trí sau khi animation kết thúc
                    view.translationY = distance
                }, 50)
            }
        })

        slideDownAnimator.start()
    }
}