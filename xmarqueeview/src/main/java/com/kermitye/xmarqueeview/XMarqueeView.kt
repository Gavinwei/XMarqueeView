package com.kermitye.xmarqueeview

import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import android.view.Gravity
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.LinearLayout
import android.widget.ViewFlipper


/**
 * Created by kermitye on 2022/3/8 14:17
 * desc:
 */
class XMarqueeView @JvmOverloads constructor(context: Context?, attrs: AttributeSet? = null) :
    ViewFlipper(context, attrs), XMarqueeViewAdapter.OnDataChangedListener {
    /**
     * 是否设置动画时间间隔
     */
    private var isSetAnimDuration = false

    /**
     * 是否单行显示
     */
    private var isSingleLine = true

    /**
     * 轮播间隔
     */
    private var interval = 3000

    /**
     * 动画时间
     */
    private var animDuration = 1000
    private var textSize = 14
    private var textColor: Int = Color.parseColor("#888888")

    /**
     * 一次性显示多少个
     */
    private var itemCount = 1

    private var mMarqueeViewAdapter: XMarqueeViewAdapter<*>? = null

    /**
     * 只有一条数据时是否轮播动画显示
     */
    private var isFlippingLessCount = true


    init {
        val typedArray =
            context!!.obtainStyledAttributes(attrs, R.styleable.XMarqueeView, 0, 0)
        if (typedArray != null) {
            isSetAnimDuration =
                typedArray.getBoolean(R.styleable.XMarqueeView_isSetAnimDuration, false)
            isSingleLine = typedArray.getBoolean(R.styleable.XMarqueeView_isSingleLine, true)
            isFlippingLessCount =
                typedArray.getBoolean(R.styleable.XMarqueeView_isFlippingLessCount, true)
            interval =
                typedArray.getInteger(R.styleable.XMarqueeView_marquee_interval, interval)
            animDuration = typedArray.getInteger(
                R.styleable.XMarqueeView_marquee_animDuration,
                animDuration
            )
            if (typedArray.hasValue(R.styleable.XMarqueeView_marquee_textSize)) {
                textSize = typedArray.getDimension(
                    R.styleable.XMarqueeView_marquee_textSize,
                    textSize.toFloat()
                )
                    .toInt()
                textSize = Utils.px2sp(context, textSize.toFloat())
            }
            textColor =
                typedArray.getColor(R.styleable.XMarqueeView_marquee_textColor, textColor)
            itemCount = typedArray.getInt(R.styleable.XMarqueeView_marquee_count, itemCount)
            typedArray.recycle()
        }
        isSingleLine = itemCount == 1
        val animIn: Animation = AnimationUtils.loadAnimation(context, R.anim.anim_marquee_in)
        val animOut: Animation = AnimationUtils.loadAnimation(context, R.anim.anim_marquee_out)
        if (isSetAnimDuration) {
            animIn.duration = animDuration.toLong()
            animOut.duration = animDuration.toLong()
        }
        inAnimation = animIn
        outAnimation = animOut
        flipInterval = interval
        measureAllChildren = false
    }

    fun setAdapter(adapter: XMarqueeViewAdapter<*>) {
        if (adapter == null) {
            throw RuntimeException("adapter must not be null")
        }
        if (mMarqueeViewAdapter != null) {
            throw RuntimeException("you have already set an Adapter")
        }
        mMarqueeViewAdapter = adapter
        mMarqueeViewAdapter!!.setOnDataChangedListener(this)
        setData()
    }

    private fun setData() {
        removeAllViews()
        var currentIndex = 0
        val loopconunt =
            if (mMarqueeViewAdapter!!.getItemCount() % itemCount == 0) mMarqueeViewAdapter!!.getItemCount() / itemCount else mMarqueeViewAdapter!!.getItemCount() / itemCount + 1
        for (i in 0 until loopconunt) {
            if (isSingleLine) {
                val view: View = mMarqueeViewAdapter!!.onCreateView(this)
                if (currentIndex < mMarqueeViewAdapter!!.getItemCount()) {
                    mMarqueeViewAdapter!!.onBindView(view, view, currentIndex)
                }
                currentIndex = currentIndex + 1
                addView(view)
            } else {
                val parentView = LinearLayout(context)
                parentView.orientation = LinearLayout.VERTICAL
                parentView.gravity = Gravity.CENTER
                parentView.removeAllViews()
                for (j in 0 until itemCount) {
                    val view: View = mMarqueeViewAdapter!!.onCreateView(this)
                    parentView.addView(view)
                    currentIndex = getRealPosition(j, currentIndex)
                    if (currentIndex < mMarqueeViewAdapter!!.getItemCount()) {
                        mMarqueeViewAdapter!!.onBindView(parentView, view, currentIndex)
                    }
                }
                addView(parentView)
            }
        }
        if (isFlippingLessCount || itemCount >= mMarqueeViewAdapter!!.getItemCount()) {
            startFlipping()
        }
    }

    fun setItemCount(itemCount: Int) {
        this.itemCount = itemCount
        isSingleLine = itemCount == 1
    }

   /* fun setSingleLine(singleLine: Boolean) {
        isSingleLine = singleLine
    }*/

    /**
     * 只有一条数据时是否轮播动画显示
     * [flippingLessCount]
     */
    fun setFlippingLessCount(flippingLessCount: Boolean) {
        isFlippingLessCount = flippingLessCount
    }

    /**
     * 获取当前显示的页面的index
     * 注意，如果itemCount大于1的话，则此方法返回的是页数，而不是data的position
     */
    fun getCurrentIndex() :Int {
        return displayedChild
    }

    private fun getRealPosition(index: Int, currentIndex: Int): Int {
        return if (index == 0 && currentIndex == 0 || currentIndex == mMarqueeViewAdapter!!.getItemCount() - 1) {
            0
        } else {
            currentIndex + 1
        }
    }

    override fun onChanged() {
        setData()
    }

    override fun onVisibilityChanged(changedView: View, visibility: Int) {
        super.onVisibilityChanged(changedView, visibility)
        if (VISIBLE == visibility) {
            startFlipping()
        } else if (GONE == visibility || INVISIBLE == visibility) {
            stopFlipping()
        }
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        startFlipping()
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        stopFlipping()
    }

}