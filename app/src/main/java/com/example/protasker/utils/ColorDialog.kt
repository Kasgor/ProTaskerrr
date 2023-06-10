package com.example.protasker.utils

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.protasker.R
import com.example.protasker.adapters.ColorAdapter

abstract class ColorDialog(
    context: Context,
    private var list: ArrayList<String>,
    private val title: String = "",
    private val mSelectedColor: String = ""
) : Dialog(context) {

    private var adapter: ColorAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState ?: Bundle())
        val view = LayoutInflater.from(context).inflate(R.layout.dialog_colors, null)
        setContentView(view)
        setCanceledOnTouchOutside(true)
        setCancelable(true)


        setColors(view)
    }
    protected abstract fun onItemSelected(color: String)

    private fun setColors(view: View) {
        view.findViewById<TextView>(R.id.tvvTitle).text = title
        view.findViewById<androidx.recyclerview.widget.RecyclerView>(R.id.rvvList).layoutManager = LinearLayoutManager(context)
        adapter = ColorAdapter(context, list, mSelectedColor)
        view.findViewById<androidx.recyclerview.widget.RecyclerView>(R.id.rvvList).adapter = adapter

        adapter!!.onColorClickListener = object : ColorAdapter.OnColorClickListener {

            override fun onClick(position: Int, color: String) {
                dismiss()
                onItemSelected(color)
            }
        }
    }


}