package com.example.protasker.activities

import android.app.Activity
import android.app.Dialog
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.protasker.R
import com.example.protasker.adapters.PeopleAdapter
import com.example.protasker.databinding.ActivityPeopleBinding
import com.example.protasker.firebase.FirestoreClass
import com.example.protasker.models.Project
import com.example.protasker.models.User
import com.example.protasker.utils.Constants

class PeopleActivity : BaseActivity() {
    private lateinit var prjDetails: Project
    private lateinit var binding: ActivityPeopleBinding
    private lateinit var PeopleList: ArrayList<User>
    private var position =-1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPeopleBinding.inflate(layoutInflater)
        if(intent.hasExtra(Constants.PROJECTDETAIL)){
            prjDetails = intent.getParcelableExtra<Project>(Constants.PROJECTDETAIL)!!
        }

        setContentView(binding.root)
        setupActionBar()
        showProgressDialog(resources.getString(R.string.please_wait))
        FirestoreClass().getPeopleDetailsFromProject(this, prjDetails.assignedTo)
    }

    override fun onBackPressed() {
        setResult(Activity.RESULT_OK)
        super.onBackPressed()
    }

    /**
     * setup custom tool bar
     */
    private fun setupActionBar(){
        setSupportActionBar(binding.toolbarPeopleActivity)
        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_white_color_back_24dp)
            actionBar.title = "People"
        }

        binding.toolbarPeopleActivity.setNavigationOnClickListener { onBackPressed() }
    }

    /**
     * Create the list of people for the project
     */
    fun createPeopleList(list: ArrayList<User>){
        hideProgressDialog()
        PeopleList = list

        findViewById<androidx.recyclerview.widget.RecyclerView>(R.id.rv_members_list).layoutManager = LinearLayoutManager(this)
        findViewById<androidx.recyclerview.widget.RecyclerView>(R.id.rv_members_list).setHasFixedSize(true)
        val adapter  = PeopleAdapter(this, list)
        findViewById<androidx.recyclerview.widget.RecyclerView>(R.id.rv_members_list).adapter = adapter
        adapter!!.setOnClickListener(object :
            PeopleAdapter.OnClickListener {
            override fun onClick(position: Int, user: User, action:String) {
                deleteUser(user)
            }
        })
    }

    /**
     * Delete a user from the project
     */
    fun deleteUser(user: User){
        if(PeopleList.size>=2) {
            prjDetails.assignedTo.remove(user.id)
            for (i in prjDetails.taskList.indices) {
                for (j in prjDetails.taskList[i].cards.indices) {
                    prjDetails.taskList[i].cards[j].assignedTo.remove(getID())
                }
            }
            FirestoreClass().deassign(this, prjDetails, user)

        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_person, menu)
        return super.onCreateOptionsMenu(menu)
    }

    /**
     * Add a person to the project
     */
    fun getPropleInfo(user: User){
        prjDetails.assignedTo.add(user.id)
        FirestoreClass().assign(this, prjDetails, user)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        when(item.itemId){
            R.id.add_person->{
                dialogPopup()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    /**
     * Callback method when adding a person to the project is successful
     */
    fun peopleSuccess(user: User){
        hideProgressDialog()
        PeopleList.add(user)

        createPeopleList(PeopleList)

    }

    /**
     * Callback method when removing a person from the project is successful
     */
    fun peopleSuccess2(user: User){
        hideProgressDialog()
        PeopleList.remove(user)
        createPeopleList(PeopleList)

    }

    /**
     * Show a dialog to search and add a person
     */
    private fun dialogPopup(){
        val dialog = Dialog(this)
        dialog.setContentView(R.layout.dialog_search_person)
        dialog.findViewById<TextView>(R.id.tv_add).setOnClickListener{
            val email = dialog.findViewById<androidx.appcompat.widget.AppCompatEditText>(R.id.et_email_search_person).text.toString()
            if(email.isNotEmpty()){
                dialog.dismiss()
                showProgressDialog(resources.getString(R.string.please_wait))
                FirestoreClass().getDetailsAboutPeople(this, email)
            }
        }
        dialog.findViewById<TextView>(R.id.tv_cancel).setOnClickListener{
            dialog.dismiss()
        }
        dialog.show()
    }
}