package com.example.protasker.activities

import android.app.Activity
import android.app.DatePickerDialog
import android.graphics.Color
import android.icu.text.SimpleDateFormat
import android.icu.util.Calendar
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.GridLayoutManager
import com.example.protasker.R
import com.example.protasker.adapters.CardPeopleAdapter
import com.example.protasker.databinding.ActivityCardBinding
import com.example.protasker.firebase.FirestoreClass
import com.example.protasker.models.Project
import com.example.protasker.models.User
import com.example.protasker.utils.ColorDialog
import com.example.protasker.utils.Constants
import com.example.protasker.utils.DialogSelectUsers
import com.projemanag.model.Card
import com.projemanag.model.SelectedPeople
import org.w3c.dom.Text
import java.util.*
import kotlin.collections.ArrayList


private lateinit var binding: ActivityCardBinding
private lateinit var prjDetails: Project
private lateinit var peopleDetails: ArrayList<User>
private  var position: Int = -1
private  var cardPosition: Int = -1
private var selectedColor:String =""
private var selectedDueTo :Long =0

class CardActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCardBinding.inflate(layoutInflater)
        setContentView(binding.root)
        if(intent.hasExtra(Constants.PROJECTDETAIL)){
            prjDetails =intent.getParcelableExtra(Constants.PROJECTDETAIL)!!
        }
        if(intent.hasExtra(Constants.TASKLISTPOSITION)){
            position = intent.getIntExtra(Constants.TASKLISTPOSITION, -1)
        }
        if(intent.hasExtra(Constants.CARDPOSITION)){
            cardPosition = intent.getIntExtra(Constants.CARDPOSITION, -1)
        }
        if(intent.hasExtra(Constants.PEOPLELIST)){
            peopleDetails = intent.getParcelableArrayListExtra(Constants.PEOPLELIST)!!
        }
        selectedColor = prjDetails.taskList[position].cards[cardPosition].color
        if(selectedColor.isNotEmpty()){
            setselectedcolor()
        }

        binding.etNameCardDetails.setText(prjDetails.taskList[position].cards[cardPosition].name)

        binding.btnUpdateCard.setOnClickListener{
            if(binding.etNameCardDetails.text.toString().isNotEmpty()) {
                updateCard()
            }
        }
        binding.tvSelectPeople.setOnClickListener {
            peopleDialog()
        }

        binding.tvSelectLabelColor.setOnClickListener {
            createColorsDialog()
        }
        selectedDueTo = prjDetails.taskList[position].cards[cardPosition].dueTo
        if(selectedDueTo> 0){
            val simpleDateFormat =SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH)
            val stringDate  = simpleDateFormat.format(Date(selectedDueTo))
            binding.tvSelectDueDate.text = stringDate
        }

        binding.tvSelectDueDate.setOnClickListener {
            showDataPicker()
        }

        setupActionBar()
        setupPeople()

    }

    /**
     * Function ti start dialog of choosing people for current card/task
     */
    private fun peopleDialog(){
        var assifned = prjDetails.taskList[position].cards[cardPosition].assignedTo
        if (assifned.size>0)
        {
            for (i in peopleDetails.indices){
                for(j in assifned){
                    if(peopleDetails[i].id == j){
                        peopleDetails[i].selected = true
                    }

                }
            }
        }
        else{
            for (i in peopleDetails.indices){
                peopleDetails[i].selected = false
            }

        }
        val dialog = object:DialogSelectUsers(this, peopleDetails, "Select people"){
            override fun onItemSelected(user: User, action: String) {
                if(action == Constants.SELECT){
                    if(!prjDetails.taskList[position].cards[cardPosition].assignedTo.contains(user.id)){
                        prjDetails.taskList[position].cards[cardPosition].assignedTo.add(user.id)
                    }
                }
                else{
                    if(prjDetails.taskList[position].cards[cardPosition].assignedTo.contains(user.id)){
                        prjDetails.taskList[position].cards[cardPosition].assignedTo.remove(user.id)
                    }
                    for (i in peopleDetails.indices) {
                        if (peopleDetails[i].id == user.id){
                            peopleDetails[i].selected=false
                        }
                    }
                }
                setupPeople()
            }
        }
        dialog.show()
    }

    /**
     * setting up certain cards for colors dialog
     */
    private fun colors(): ArrayList<String>{
        val colors =ArrayList<String>()
        colors.add("#43C86F")
        colors.add("#0C90F1")
        colors.add("#F72400")
        return colors
    }

    /**
     * creating dialog for choosing color for card
     */
    private fun createColorsDialog(){
        val colors =colors()
        val dialog = object:ColorDialog(this, colors,"Select", selectedColor){
            override fun onItemSelected(color: String) {
                selectedColor= color
                setselectedcolor()
            }

        }
        dialog.show()
    }

    /**
     * set selected color to the view
     */
    private fun setselectedcolor(){
        findViewById<TextView>(R.id.tv_select_label_color).text =""
        findViewById<TextView>(R.id.tv_select_label_color).setBackgroundColor(Color.parseColor(selectedColor))
    }


    /**
     * function to delete card completely
     */
    private fun deleteCard(){
        val cardList: ArrayList<Card> = prjDetails.taskList[position].cards
        cardList.removeAt(cardPosition)
        val task = prjDetails.taskList
        task.removeAt(task.size-1)
        task[position].cards = cardList

        showProgressDialog(resources.getString(R.string.please_wait))
        FirestoreClass().addUpdate(this, prjDetails)
    }

    fun addUpdateListOfTasksResult(){
        hideProgressDialog()
        setResult(Activity.RESULT_OK)
        finish()
    }

    /**
     * setup people for detail of card using data from current project
     */
    private fun setupPeople(){
        val assigned =prjDetails.taskList[position].cards[cardPosition].assignedTo
        val selectedPeople :ArrayList<SelectedPeople> = ArrayList()
        for (i in peopleDetails.indices){
            for(j in assigned){
                if(peopleDetails[i].id == j){
                    val person = SelectedPeople(
                        peopleDetails[i].id,
                        peopleDetails[i].image
                    )
                    selectedPeople.add(person)
                }

            }
        }
        if(selectedPeople.size>=0){
            selectedPeople.add(SelectedPeople("", ""))
            findViewById<TextView>(R.id.tv_select_people).visibility = View.GONE
            findViewById<androidx.recyclerview.widget.RecyclerView>(R.id.rv_selected_people).visibility =View.VISIBLE
            findViewById<androidx.recyclerview.widget.RecyclerView>(R.id.rv_selected_people).layoutManager = GridLayoutManager(this, 6)
            val adapter = CardPeopleAdapter(this, selectedPeople, true)
            findViewById<androidx.recyclerview.widget.RecyclerView>(R.id.rv_selected_people).adapter = adapter
            adapter.setOnClickListener(
                object: CardPeopleAdapter.OnClickListener{
                    override fun onClick() {
                        peopleDialog()
                    }

                }
            )
        }
        else{
            findViewById<TextView>(R.id.tv_select_people).visibility = View.VISIBLE
            findViewById<androidx.recyclerview.widget.RecyclerView>(R.id.rv_selected_people).visibility =View.GONE
        }

    }

    /**
     * add changes to database of card
     */
    private fun updateCard(){
        val card: Card = Card(binding.etNameCardDetails.text.toString(),
            prjDetails.taskList[position].cards[cardPosition].createdBy,
            prjDetails.taskList[position].cards[cardPosition].assignedTo,
            selectedColor, selectedDueTo
        )
        prjDetails.taskList[position].cards[cardPosition]  = card
        prjDetails.taskList.removeAt(prjDetails.taskList.size-1)
        showProgressDialog(resources.getString(R.string.please_wait))
        FirestoreClass().addUpdate(this, prjDetails)
    }

    /**
     * modification of option menu
     */
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_delete, menu)
        return super.onCreateOptionsMenu(menu)
    }

    /**
     * on choosing delete icon call function deleteCard()
     */
    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        when(item.itemId){
            R.id.delete_card ->{
                deleteCard()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    /**
     * create custom toolbar
     */
    private fun setupActionBar(){
        setSupportActionBar(binding.toolbarCardDetailsActivity)
        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_white_color_back_24dp)
            actionBar.title = "Card"
        }

        binding.toolbarCardDetailsActivity.setNavigationOnClickListener { onBackPressed() }
    }

    /**
     * show dialog of choosing data
     */
    private fun showDataPicker() {

        val c = Calendar.getInstance()
        val year =
            c.get(Calendar.YEAR)
        val month = c.get(Calendar.MONTH)
        val day = c.get(Calendar.DAY_OF_MONTH)


        val dpd = DatePickerDialog(
            this,
            DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
                val sDayOfMonth = if (dayOfMonth < 10) "0$dayOfMonth" else "$dayOfMonth"
                val sMonthOfYear =
                    if ((monthOfYear + 1) < 10) "0${monthOfYear + 1}" else "${monthOfYear + 1}"
                val selectedDate = "$sDayOfMonth/$sMonthOfYear/$year"
                findViewById<TextView>(R.id.tv_select_due_date).text = selectedDate
                val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH)
                val theDate = sdf.parse(selectedDate)
                selectedDueTo = theDate!!.time
            },
            year,
            month,
            day
        )
        dpd.show()
    }
}