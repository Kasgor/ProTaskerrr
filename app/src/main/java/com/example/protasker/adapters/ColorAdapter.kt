package com.example.protasker.adapters

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.example.protasker.R
import com.projemanag.model.SelectedPeople

open class ColorAdapter(
    private val context: Context,
    private var list: ArrayList<String>,
    private var selectedColor: String
) : RecyclerView.Adapter<RecyclerView.ViewHolder>()  {

    var onColorClickListener: OnColorClickListener? = null
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return MyViewHolder(
            LayoutInflater.from(context).inflate(R.layout.item_color, parent, false)
        )
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val model = list[position]
        if(holder is MyViewHolder){

            holder.itemView.findViewById<View>(R.id.view_main).setBackgroundColor(Color.parseColor(model))
            if(model == selectedColor){
                holder.itemView.findViewById<ImageView>(R.id.iv_selected_color).visibility = View.VISIBLE
            }
            else{
                holder.itemView.findViewById<ImageView>(R.id.iv_selected_color).visibility = View.GONE
            }

            holder.itemView.setOnClickListener{
                if(onColorClickListener!= null){
                    onColorClickListener!!.onClick(position, model)
                }
            }
        }
    }

    class MyViewHolder(view: View) : RecyclerView.ViewHolder(view)

    interface OnColorClickListener {
        fun onClick(position: Int, color: String)
    }

}