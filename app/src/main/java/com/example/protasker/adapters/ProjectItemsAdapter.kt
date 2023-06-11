package com.example.protasker.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.protasker.R

import com.example.protasker.models.Project
/**
Adapter class for displaying people in a RecyclerView.
@param context The context of the activity or fragment.
@param list The list of Project objects to be displayed.
 */
open class ProjectItemsAdapter(private val context: Context,
                                private val list: ArrayList<Project>):
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private var onClickListener: OnClickListener? = null
    /**
    Creates a RecyclerView ViewHolder by inflating the item_project layout.
    @param parent The parent ViewGroup.
    @param viewType The type of the view.
    @return The created ViewHolder.
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {


        return MyViewHolder(LayoutInflater.from(context).inflate(R.layout.item_project, parent, false))
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
        val model =list[position]
        if (holder is MyViewHolder){
            Glide
                .with(context)
                .load(model.image)
                .centerCrop()
                .placeholder(R.drawable.ic_user_place_holder)
                .into(holder.itemView.findViewById(R.id.iv_project_image))

            holder.itemView.findViewById<TextView>(R.id.tv_name).text =model.name
            holder.itemView.findViewById<TextView>(R.id.tv_created_by).text ="Created by: ${model.createdBy}"

            holder.itemView.setOnClickListener{
                if (onClickListener != null) {
                    onClickListener!!.onClick(position, model)
                }
            }
        }
    }

    interface OnClickListener{
        fun onClick(position: Int, model: Project)
    }

    fun setOnClickListener(onClickListener: OnClickListener){
        this.onClickListener=onClickListener
    }

    private class MyViewHolder(view: View): RecyclerView.ViewHolder(view)


}