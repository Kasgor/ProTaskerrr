package com.example.protasker.utils

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.protasker.R
import com.example.protasker.adapters.PeopleAdapter
import com.example.protasker.models.User


abstract class DialogSelectUsers(
    context: Context,
    private var list: ArrayList<User>,
    private val title: String = ""
) : Dialog(context) {

    private var adapter: PeopleAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState ?: Bundle())

        val view = LayoutInflater.from(context).inflate(R.layout.dialog_list, null)

        setContentView(view)
        setCanceledOnTouchOutside(true)
        setCancelable(true)
        setUpRecyclerView(view)
    }

    private fun setUpRecyclerView(view: View) {
        view.findViewById<TextView>(R.id.tvTitle).text = title

        if (list.size > 0) {

            view.findViewById<androidx.recyclerview.widget.RecyclerView>(R.id.rvList).layoutManager = LinearLayoutManager(context)
            adapter = PeopleAdapter(context, list)
            view.findViewById<androidx.recyclerview.widget.RecyclerView>(R.id.rvList).adapter = adapter

            adapter!!.setOnClickListener(object :
                PeopleAdapter.OnClickListener {
                override fun onClick(position: Int, user: User, action:String) {
                    dismiss()
                    onItemSelected(user, action)
                }
            })
        }
    }

    protected abstract fun onItemSelected(user: User, action:String)
}