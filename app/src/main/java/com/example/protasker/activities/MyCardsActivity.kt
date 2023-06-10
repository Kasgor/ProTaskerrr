package com.example.protasker.activities

import CardAdapter
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.protasker.R
import com.example.protasker.databinding.ActivityMyCardsBinding
import com.example.protasker.firebase.FirestoreClass
import com.example.protasker.models.Project
import com.projemanag.model.Card

class MyCardsActivity : BaseActivity() {
    private lateinit var binding: ActivityMyCardsBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMyCardsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupActionBar()
        FirestoreClass().getProjectsList(this)
    }

    /**
     * setup custom toolbar
     */
    private fun setupActionBar(){
        setSupportActionBar(binding.toolbarMyCardsActivity)
        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_white_color_back_24dp)
            actionBar.title = "Your Cards"
        }

        binding.toolbarMyCardsActivity.setNavigationOnClickListener { onBackPressed() }
    }

    /**
     * get card of certain user from all projects
     */
    private fun getCards(projects:  ArrayList<Project>):ArrayList<Card>{
        val cards =ArrayList<Card>()
        for(i in projects){
            for(j in i.taskList){
                for(k in j.cards){
                    if(k.assignedTo.contains(getID())){
                        cards.add(k)
                    }
                }
            }
        }
        return cards
    }

    /**
     * setup adapter for displaying list of cards assingned to this usetr
     */
     fun setupAdapter(projects:  ArrayList<Project>){
        var cards: ArrayList<Card> = ArrayList()
        cards = getCards(projects)
        binding.rvMyCardsList.layoutManager= LinearLayoutManager(this)
        binding.rvMyCardsList.setHasFixedSize(true)

        val adapter = CardAdapter(this, cards)
        binding.rvMyCardsList.adapter = adapter
    }
}