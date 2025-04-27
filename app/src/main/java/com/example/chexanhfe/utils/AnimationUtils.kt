package com.example.chexanhfe.utils

import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.TranslateAnimation

object AnimationUtils {
    fun slideUpPanel(view: View, distance: Float, bottomBar: View, duration: Long = 500) {
        // Hiển thị view (panel) khi thực hiện thao tác trượt lên
        view.visibility = View.VISIBLE

        // Tạo một animation để trượt view từ vị trí ban đầu tới vị trí 0 (trượt lên)
        val slideUp = TranslateAnimation(0f, 0f, distance, 0f)
        slideUp.duration = duration // Thời gian thực hiện animation, mặc định là 500ms
        view.startAnimation(slideUp) // Bắt đầu animation trượt lên

        // Cập nhật vị trí cuối cùng của view để đảm bảo nó ở đúng vị trí
        view.translationY = 0f

        // Ẩn thanh "bottomBar" khi panel trượt lên
        bottomBar.visibility = View.GONE
    }

    fun slideDownPanel(view: View, distance: Float, bottomBar: View, duration: Long = 500) {
        // Xóa bất kỳ animation hiện tại nào đang áp dụng trên view (nếu có)
        view.clearAnimation()

        // Bắt đầu animation trượt xuống thông qua phương thức animate()
        view.animate()
            .translationY(distance) // Di chuyển panel xuống vị trí cách điểm gốc một khoảng bằng distance
            .setDuration(duration) // Thời gian thực hiện animation, mặc định là 500ms
            .setInterpolator(AccelerateDecelerateInterpolator()) // Đặt bộ nội suy để tạo hiệu ứng chuyển động mượt mà

            // Tùy chọn: Thực hiện hành động khi bắt đầu animation
            .withStartAction {
                /* Tùy chọn: Hành động khi bắt đầu */ // Thêm các tác vụ nếu cần khi animation bắt đầu
            }

            // Thực hiện hành động sau khi animation kết thúc
            .withEndAction {
                // Hiển thị thanh "bottomBar" sau khi panel đã trượt xuống
                bottomBar.visibility = View.VISIBLE
            }
            .start() // Bắt đầu animation
    }
}