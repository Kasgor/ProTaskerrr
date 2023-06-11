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
/**
Adapter class for displaying selected people in a RecyclerView.
@param context The context of the activity or fragment.
@param list The list of SelectedPeople objects to be displayed.
@param assignBoolean A boolean indicating whether the adapter is used for assigning people.
 */
open class CardPeopleAdapter(
    private val context: Context,
    private var list: ArrayList<SelectedPeople>,
    private val assignBoolean: Boolean
) : RecyclerView.Adapter<RecyclerView.ViewHolder>()  {
    private var onClickListener: CardPeopleAdapter.OnClickListener? = null
    /**
    Creates a RecyclerView ViewHolder by inflating the item_card_selected layout.
    @param parent The parent ViewGroup.
    @param viewType The type of the view.
    @return The created ViewHolder.
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        return MyViewHolder(LayoutInflater.from(context).inflate(R.layout.item_card_selected, parent, false))
    }
    /**
    Returns the number of items in the list.
    @return The item count.
     */
    override fun getItemCount(): Int {
        return list.size
    }
    /**
    Binds the data of a SelectedPeople object to the views in the ViewHolder.
    @param holder The ViewHolder to bind the data to.
    @param position The position of the item in the list.
     */
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