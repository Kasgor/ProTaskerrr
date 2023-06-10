
import android.content.Context
import android.graphics.Color
import android.icu.text.SimpleDateFormat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.protasker.R
import com.example.protasker.activities.TaskListActivity

import com.example.protasker.adapters.CardPeopleAdapter
import com.projemanag.model.Card
import com.projemanag.model.SelectedPeople
import java.util.*
import kotlin.collections.ArrayList


open class CardAdapter(
    private val context: Context,
    private var list: ArrayList<Card>
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var onClickListener: OnClickListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        return MyViewHolder(
            LayoutInflater.from(context).inflate(
                R.layout.item_card,
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val model = list[position]

        if (holder is MyViewHolder) {
            if(model.color.isNotEmpty()) {
                holder.itemView.findViewById<View>(R.id.view_label_color).visibility = View.VISIBLE
                holder.itemView.findViewById<View>(R.id.view_label_color)
                    .setBackgroundColor(Color.parseColor(model.color))
            }
            else{
                holder.itemView.findViewById<View>(R.id.view_label_color).visibility = View.GONE
            }
            if(model.dueTo >0) {
                val simpleDateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH)
                val stringDate  = simpleDateFormat.format(Date(model.dueTo))

                holder.itemView.findViewById<TextView>(R.id.tv_card_name).text = model.name + "  "+ stringDate
            }    else{
                holder.itemView.findViewById<TextView>(R.id.tv_card_name).text = model.name
            }
            if(context is TaskListActivity){
                if((context as TaskListActivity).Assignedpeople.size>0){
                    val selected : ArrayList<SelectedPeople> = ArrayList()

                    for(i in context.Assignedpeople.indices){
                        for(j in model.assignedTo){
                            if(context.Assignedpeople[i].id==j){
                                val selectedP = SelectedPeople(context.Assignedpeople[i].id, context.Assignedpeople[i].image)
                                selected.add(selectedP)
                            }
                        }
                    }
                    if(selected.size>0){
                        holder.itemView.findViewById<androidx.recyclerview.widget.RecyclerView>(R.id.rv_card_selected_people).visibility = View.VISIBLE
                        holder.itemView.findViewById<androidx.recyclerview.widget.RecyclerView>(R.id.rv_card_selected_people).layoutManager =
                            GridLayoutManager(context, 4)
                        val adapter = CardPeopleAdapter(context, selected, false)
                        holder.itemView.findViewById<androidx.recyclerview.widget.RecyclerView>(R.id.rv_card_selected_people).adapter= adapter
                        adapter.setOnClickListener(object: CardPeopleAdapter.OnClickListener{
                            override fun onClick() {
                                if(onClickListener!= null){
                                    onClickListener!!.onClick(position)
                                }
                            }

                        })
                    }else{
                        holder.itemView.findViewById<androidx.recyclerview.widget.RecyclerView>(R.id.rv_card_selected_people).visibility = View.GONE
                    }
                }
            }




            holder.itemView.setOnClickListener{
                if(onClickListener!=null)
                    onClickListener!!.onClick(position)
            }
        }
    }


    override fun getItemCount(): Int {
        return list.size
    }


    fun setOnClickListener(onClickListener: OnClickListener) {
        this.onClickListener = onClickListener
    }


    interface OnClickListener {
        fun onClick(position: Int)
    }


    class MyViewHolder(view: View) : RecyclerView.ViewHolder(view)
}
