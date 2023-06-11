package com.example.protasker.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.protasker.R
import com.example.protasker.models.User
import com.example.protasker.utils.Constants
import com.projemanag.model.Card
/**
Adapter class for displaying people in a RecyclerView.
@param context The context of the activity or fragment.
@param list The list of Users objects to be displayed.
 */
open class PeopleAdapter (
    private val context: Context,
    private var list: ArrayList<User>
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private var onClickListener: OnClickListener? = null
    /**
    Creates a RecyclerView ViewHolder by inflating the item_person layout.
    @param parent The parent ViewGroup.
    @param viewType The type of the view.
    @return The created ViewHolder.
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return MyViewHolder(LayoutInflater.from(context).inflate(R.layout.item_person, parent, false))
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
        val model  = list[position]

        if(holder is MyViewHolder){
            Glide
                .with(context)
                .load(model.image)
                .centerCrop()
                .placeholder(R.drawable.ic_user_place_holder)
                .into(holder.itemView.findViewById(R.id.iv_member_image))

            holder.itemView.findViewById<TextView>(R.id.tv_member_name).text = model.name
            holder.itemView.findViewById<TextView>(R.id.tv_member_email).text = model.email
            if(model.selected){
                holder.itemView.findViewById<ImageView>(R.id.iv_selected).visibility = View.VISIBLE
            }
            else{
                holder.itemView.findViewById<ImageView>(R.id.iv_selected).visibility = View.GONE
            }
            holder.itemView.setOnClickListener{
                if(onClickListener!=null){
                    if(model.selected){
                        onClickListener!!.onClick(position, model, Constants.UN_SELECT)
                    }
                    else{
                        onClickListener!!.onClick(position, model, Constants.SELECT)
                    }
                }
            }
        }
    }
    interface OnClickListener {
        fun onClick(position: Int, user: User, action:String)
    }
    fun setOnClickListener(onClickListener: OnClickListener){
        this.onClickListener=onClickListener
    }

    class MyViewHolder(view: View) : RecyclerView.ViewHolder(view)
}