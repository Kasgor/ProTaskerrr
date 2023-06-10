package com.example.protasker.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.protasker.R

import com.projemanag.model.SelectedPeople

open class CardPeopleAdapter(
    private val context: Context,
    private var list: ArrayList<SelectedPeople>,
    private val assignBoolean: Boolean
) : RecyclerView.Adapter<RecyclerView.ViewHolder>()  {
    private var onClickListener: CardPeopleAdapter.OnClickListener? = null
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        return MyViewHolder(LayoutInflater.from(context).inflate(R.layout.item_card_selected, parent, false))
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val model = list[position]
        if(holder is MyViewHolder){
            if(position==list.size-1&&assignBoolean){
                holder.itemView.findViewById<ImageView>(R.id.iv_add_personn).visibility=View.VISIBLE
                holder.itemView.findViewById<ImageView>(R.id.iv_selected_person_image).visibility= View.GONE
            }
            else{
                holder.itemView.findViewById<ImageView>(R.id.iv_add_personn).visibility=View.GONE
                holder.itemView.findViewById<ImageView>(R.id.iv_selected_person_image).visibility= View.VISIBLE

                Glide
                    .with(context)
                    .load(model.imageUri)
                    .centerCrop()
                    .placeholder(R.drawable.ic_user_place_holder)
                    .into(holder.itemView.findViewById(R.id.iv_selected_person_image))
            }
            holder.itemView.setOnClickListener{
                if(onClickListener!=null){
                    onClickListener!!.onClick()
                }
            }

        }
    }
    class MyViewHolder(view: View) : RecyclerView.ViewHolder(view)

    interface OnClickListener {
        fun onClick()
    }
    fun setOnClickListener(onClickListener: OnClickListener?){
        this.onClickListener = onClickListener
    }
}