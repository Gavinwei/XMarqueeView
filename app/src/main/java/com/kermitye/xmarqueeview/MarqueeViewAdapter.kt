package com.kermitye.xmarqueeview

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast


/**
 * Created by kermitye on 2022/3/8 14:36
 * desc:
 */
class MarqueeViewAdapter(var context: Context, data: List<String>): XMarqueeViewAdapter<String>(data) {

    override fun onCreateView(parent: XMarqueeView): View {
        return LayoutInflater.from(parent.context).inflate(R.layout.item_marquee, null)
    }


    override fun onBindView(parent: View, view: View, position: Int) {
        //布局内容填充
        val tvOne = view.findViewById(R.id.marquee_tv_one) as TextView
        tvOne.text = mDatas[position]
        view.setOnClickListener {
            Toast.makeText(context, "position$position", Toast.LENGTH_SHORT).show()
        }
        view.findViewById<Button>(R.id.btn_jump).setOnClickListener {
            Toast.makeText(context, "跳转: $position", Toast.LENGTH_SHORT).show()
        }

    }
}