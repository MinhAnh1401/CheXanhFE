package com.example.chexanhfe.screen

import android.annotation.SuppressLint
import android.graphics.Rect
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.viewpager2.widget.ViewPager2
import com.example.chexanhfe.R
import com.example.chexanhfe.utils.AnimationUtils
import com.example.chexanhfe.utils.BannerAdapter
import com.example.chexanhfe.utils.PosterPagerAdapter


class HomeFragment : Fragment() {
    private lateinit var bannerViewPager: ViewPager2
    private lateinit var slidingPanel: ConstraintLayout
    private lateinit var arrowIcon: ImageView
    private lateinit var bottomBar: LinearLayout
    private lateinit var indicatorContainer: LinearLayout

    private val poster = listOf(
        R.drawable.banner1,
        R.drawable.banner2,
        R.drawable.banner1,
        R.drawable.banner2)
    private var adapter: PosterPagerAdapter? = null

    private val handler = Handler(Looper.getMainLooper())
    private val scrollInterval = 4000L
    private var currentPage = 0

    private val autoScrollRunnable = object : Runnable {
        override fun run() {
            val itemCount = adapter?.itemCount ?: 0
            if (itemCount == 0) return

            val nextPage = (bannerViewPager.currentItem + 1) % itemCount
            val pageWidth = bannerViewPager.width

            if (nextPage != 0) {
                smoothScrollByFakeDrag(pageWidth, 400L) // 500ms trượt nhẹ
            } else {
                bannerViewPager.setCurrentItem(0, true) // reset về đầu không animation
            }

            currentPage = nextPage
            handler.postDelayed(this, scrollInterval)
        }

        fun smoothScrollByFakeDrag(distance: Int, duration: Long) {
            if (bannerViewPager.beginFakeDrag()) {
                val interval = 10L
                val steps = (duration / interval).toInt()
                val delta = distance / steps

                var step = 0
                handler.post(object : Runnable {
                    override fun run() {
                        if (bannerViewPager.isFakeDragging && step < steps) {
                            bannerViewPager.fakeDragBy(-delta.toFloat()) // -delta = cuộn sang phải
                            step++
                            handler.postDelayed(this, interval)
                        } else {
                            if (bannerViewPager.isFakeDragging) {
                                bannerViewPager.endFakeDrag()
                            }
                        }
                    }
                })
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        bannerViewPager = view.findViewById(R.id.bannerViewPager)
        slidingPanel = view.findViewById(R.id.sliding_panel)
        arrowIcon = view.findViewById(R.id.arrow_down)
        bottomBar = view.findViewById(R.id.bottom_bar)
        indicatorContainer = view.findViewById(R.id.indicatorContainer)

        adapter = PosterPagerAdapter(poster)
        bannerViewPager.adapter = adapter

        // Thiết lập indicators
        setupIndicators()
        updateIndicators(0, 0f) // Cập nhật trạng thái ban đầu

        bannerViewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
                super.onPageScrolled(position, positionOffset, positionOffsetPixels)
                updateIndicators(position, positionOffset) // Cập nhật indicators khi cuộn
            }

            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                currentPage = position
            }

            override fun onPageScrollStateChanged(state: Int) {
                if (state == ViewPager2.SCROLL_STATE_IDLE) {
                    handler.postDelayed(autoScrollRunnable, scrollInterval)
                } else if (state == ViewPager2.SCROLL_STATE_DRAGGING) {
                    handler.removeCallbacks(autoScrollRunnable)
                }
            }
        })
    }

    // Hàm thiết lập các indicator dots
    private fun setupIndicators() {
        val indicatorCount = adapter?.itemCount ?: 0
        indicatorContainer.removeAllViews() // Xóa các indicator cũ (nếu có)

        for (i in 0 until indicatorCount) {
            val dot = ImageView(context).apply {
                setImageResource(R.drawable.indicator_inactive) // Mặc định là inactive
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                ).apply {
                    setMargins(8, 0, 8, 0) // Khoảng cách giữa các chấm
                }
            }
            indicatorContainer.addView(dot)
        }
    }

    // Hàm cập nhật trạng thái active/inactive của indicators
    private fun updateIndicators(position: Int, positionOffset: Float) {
        for (i in 0 until indicatorContainer.childCount) {
            val dot = indicatorContainer.getChildAt(i) as ImageView
            when (i) {
                position -> {
                    // Chấm hiện tại: chuyển từ active sang inactive khi cuộn
                    dot.scaleX = 1f - positionOffset * 0.5f // Thu nhỏ dần
                    dot.scaleY = 1f - positionOffset * 0.5f
                    dot.alpha = 1f - positionOffset * 0.3f // Mờ dần
                    dot.setImageResource(R.drawable.indicator_active)
                }
                position + 1 -> {
                    // Chấm tiếp theo: chuyển từ inactive sang active khi cuộn
                    dot.scaleX = 0.5f + positionOffset * 0.5f // Phóng to dần
                    dot.scaleY = 0.5f + positionOffset * 0.5f
                    dot.alpha = 0.7f + positionOffset * 0.3f // Rõ dần
                    dot.setImageResource(R.drawable.indicator_active)
                }
                else -> {
                    // Các chấm khác: inactive
                    dot.scaleX = 0.5f
                    dot.scaleY = 0.5f
                    dot.alpha = 0.7f
                    dot.setImageResource(R.drawable.indicator_inactive)
                }
            }
        }
    }

    override fun onPause() {
        super.onPause()
        handler.removeCallbacks(autoScrollRunnable)
    }

    override fun onResume() {
        super.onResume()
        handler.postDelayed(autoScrollRunnable, scrollInterval)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        handler.removeCallbacks(autoScrollRunnable)
    }
}
