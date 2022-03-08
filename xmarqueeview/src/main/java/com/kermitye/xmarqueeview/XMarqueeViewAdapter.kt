package com.kermitye.xmarqueeview

import android.view.View

/**
 * Created by kermitye on 2022/3/8 14:18
 * desc:
 */
abstract class XMarqueeViewAdapter<T>(datas: List<T>) {
    protected var mDatas: List<T>
    private var mOnDataChangedListener: OnDataChangedListener? = null
    fun setData(datas: List<T>) {
        mDatas = datas
        notifyDataChanged()
    }

    fun getItemCount(): Int {
        return if (mDatas == null) 0 else mDatas!!.size
    }

    abstract fun onCreateView(parent: XMarqueeView): View
    abstract fun onBindView(parent: View, view: View, position: Int)

    fun setOnDataChangedListener(onDataChangedListener: OnDataChangedListener?) {
        mOnDataChangedListener = onDataChangedListener
    }

    fun notifyDataChanged() {
        if (mOnDataChangedListener != null) {
            mOnDataChangedListener!!.onChanged()
        }
    }

    interface OnDataChangedListener {
        fun onChanged()
    }

    init {
        mDatas = datas
        if (datas == null) {
            throw RuntimeException("XMarqueeView datas is Null")
        }
    }
}