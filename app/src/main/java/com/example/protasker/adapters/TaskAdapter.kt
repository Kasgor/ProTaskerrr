package com.example.protasker.adapters

import CardAdapter
import android.content.Context
import android.content.res.Resources
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.protasker.R
import com.example.protasker.activities.TaskListActivity
import com.example.protasker.models.Task
/**
Adapter class for displaying people in a RecyclerView.
@param context The context of the activity or fragment.
@param list The list of Tasks objects to be displayed.
 */
open class TaskAdapter(private val context: Context,
                       private val list: ArrayList<Task>):
    RecyclerView.Adapter<RecyclerView.ViewHolder>()  {
    /**
    Creates a RecyclerView ViewHolder by inflating the item_task layout.
    @param parent The parent ViewGroup.
    @param viewType The type of the view.
    @return The created ViewHolder.
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        val view = LayoutInflater.from(context).inflate(R.layout.item_task, parent, false)
        val layoutParams = LinearLayout.LayoutParams(
            (parent.width * 0.7).toInt(),
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        layoutParams.setMargins((15.toDp()).toPx(), 0, (40.toDp()).toPx(), 0)
        view.layoutParams = layoutParams

        return MyViewHolder(view)
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

        if (holder is MyViewHolder) {

            if (position == list.size - 1) {
                holder.itemView.findViewById<TextView>(R.id.addtaask).visibility = View.VISIBLE
                holder.itemView.findViewById<LinearLayout>(R.id.ll_task_item).visibility = View.GONE
            } else {
                holder.itemView.findViewById<TextView>(R.id.addtaask).visibility = View.GONE
                holder.itemView.findViewById<LinearLayout>(R.id.ll_task_item).visibility = View.VISIBLE
            }

            holder.itemView.findViewById<TextView>(R.id.tv_tltitle).text = model.title
            holder.itemView.findViewById<TextView>(R.id.addtaask).setOnClickListener{
                holder.itemView.findViewById<TextView>(R.id.addtaask).visibility = View.GONE
                holder.itemView.findViewById<androidx.cardview.widget.CardView>(R.id.cv_add_task_list_name).visibility = View.VISIBLE

            }
            holder.itemView.findViewById<ImageButton>(R.id.ib_close_list_name).setOnClickListener{
                holder.itemView.findViewById<TextView>(R.id.addtaask).visibility = View.VISIBLE
                holder.itemView.findViewById<androidx.cardview.widget.CardView>(R.id.cv_add_task_list_name).visibility = View.GONE
            }
            holder.itemView.findViewById<ImageButton>(R.id.ib_done_list_name).setOnClickListener{
                val name = holder.itemView.findViewById<EditText>(R.id.et_task_list_name).text.toString()
                if (name.isNotEmpty()){
                    if(context is TaskListActivity){
                        context.addTaskFully(name)
                    }
                    else{

                    }
                }
            }

            holder.itemView.findViewById<ImageButton>(R.id.ib_edit_list_name).setOnClickListener{
                holder.itemView.findViewById<EditText>(R.id.et_edit_task_list_name).setText(model.title)
                holder.itemView.findViewById<LinearLayout>(R.id.ll_title_view).visibility = View.GONE
                holder.itemView.findViewById<androidx.cardview.widget.CardView>(R.id.cv_edit_task_list_name).visibility = View.VISIBLE
            }

            holder.itemView.findViewById<ImageButton>(R.id.ib_close_editable_view).setOnClickListener{
                holder.itemView.findViewById<LinearLayout>(R.id.ll_title_view).visibility = View.VISIBLE
                holder.itemView.findViewById<androidx.cardview.widget.CardView>(R.id.cv_edit_task_list_name).visibility = View.GONE
            }
            holder.itemView.findViewById<ImageButton>(R.id.ib_done_edit_list_name).setOnClickListener{
                val name = holder.itemView.findViewById<EditText>(R.id.et_edit_task_list_name).text.toString()
                if (name.isNotEmpty()) {
                    if (context is TaskListActivity) {
                        context.updateTaskFully(position, name, model)
                    }
                }
            }

            holder.itemView.findViewById<ImageButton>(R.id.ib_delete_list).setOnClickListener{
                if (context is TaskListActivity) {
                    context.deleteTaskFully(position)
                }
            }

            holder.itemView.findViewById<TextView>(R.id.tv_add_card).setOnClickListener{
                holder.itemView.findViewById<TextView>(R.id.tv_add_card).visibility = View.GONE
                holder.itemView.findViewById<CardView>(R.id.cv_add_card).visibility = View.VISIBLE
            }

            holder.itemView.findViewById<ImageButton>(R.id.ib_done_card_name).setOnClickListener{
                val name = holder.itemView.findViewById<EditText>(R.id.et_card_name).text.toString()
                if (name.isNotEmpty()) {
                    if (context is TaskListActivity) {
                        context.addFullyCard(position, name)
                    }
                }
            }
            holder.itemView.findViewById<ImageButton>(R.id.ib_close_card_name).setOnClickListener{
                holder.itemView.findViewById<TextView>(R.id.tv_add_card).visibility = View.VISIBLE
                holder.itemView.findViewById<CardView>(R.id.cv_add_card).visibility = View.GONE
            }

            holder.itemView.findViewById<androidx.recyclerview.widget.RecyclerView>(R.id.rv_card_list).layoutManager= LinearLayoutManager(context)
            holder.itemView.findViewById<androidx.recyclerview.widget.RecyclerView>(R.id.rv_card_list).setHasFixedSize(true)

            val adapter = CardAdapter(context, model.cards)
            holder.itemView.findViewById<androidx.recyclerview.widget.RecyclerView>(R.id.rv_card_list).adapter = adapter
            adapter.setOnClickListener(
                object : CardAdapter.OnClickListener {
                    override fun onClick(Cposition: Int){
                        if(context is TaskListActivity){
                            context.card(position, Cposition)
                        }
                    }
                }
            )
        }
    }

    private fun Int.toDp(): Int =
        (this / Resources.getSystem().displayMetrics.density).toInt()


    private fun Int.toPx(): Int =
        (this * Resources.getSystem().displayMetrics.density).toInt()

    class MyViewHolder(view: View) : RecyclerView.ViewHolder(view)
}