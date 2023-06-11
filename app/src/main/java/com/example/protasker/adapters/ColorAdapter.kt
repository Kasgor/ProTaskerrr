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
/**
Adapter class for displaying colors in a RecyclerView.
@param context The context of the activity or fragment.
@param list The list of SelectedPeople objects to be displayed.
@param selectedColor selected color.
 */
open class ColorAdapter(
    private val context: Context,
    private var list: ArrayList<String>,
    private var selectedColor: String
) : RecyclerView.Adapter<RecyclerView.ViewHolder>()  {

    var onColorClickListener: OnColorClickListener? = null
    /**
    Creates a RecyclerView ViewHolder by inflating the item_color layout.
    @param parent The parent ViewGroup.
    @param viewType The type of the view.
    @return The created ViewHolder.
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return MyViewHolder(
            LayoutInflater.from(context).inflate(R.layout.item_color, parent, false)
        )
    }
    /**
    Returns the number of items in the list.
    @return The item count.
     */
    override fun getItemCount(): Int {
        return list.size
    }
    /**
    Binds the data of a object to the views in the ViewHolder.
    @param holder The ViewHolder to bind the data to.
    @param position The position of the item in the list.
     */
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