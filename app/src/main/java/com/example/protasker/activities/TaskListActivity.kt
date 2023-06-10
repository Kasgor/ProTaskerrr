package com.example.protasker.activities

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.protasker.R
import com.example.protasker.adapters.TaskAdapter
import com.example.protasker.databinding.ActivityTaskListBinding
import com.example.protasker.firebase.FirestoreClass
import com.example.protasker.models.Project
import com.example.protasker.models.Task
import com.example.protasker.models.User
import com.example.protasker.utils.Constants
import com.projemanag.model.Card


class TaskListActivity : BaseActivity() {
    private lateinit var prjDetails: Project
    private lateinit var binding: ActivityTaskListBinding
    private var projectDocumentId = ""
     lateinit var Assignedpeople :ArrayList<User>
    companion object{
        const val REQUEST_CODE_CARD =15
        const val REQUEST_CODE_CARD1 =14
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTaskListBinding.inflate(layoutInflater)
        setContentView(binding.root)


        if (intent.hasExtra(Constants.DOCUMENT_ID)) {
            projectDocumentId=intent.getStringExtra(Constants.DOCUMENT_ID)!!
        }

        showProgressDialog(resources.getString(R.string.please_wait))
        FirestoreClass().getProjectDetails(this, projectDocumentId)


    }

    /**
     * go to card details
     */
    fun card(position: Int, cardPosition: Int){
        val intent = Intent(this, CardActivity::class.java)
        intent.putExtra(Constants.PROJECTDETAIL, prjDetails)
        intent.putExtra(Constants.CARDPOSITION, cardPosition)
        intent.putExtra(Constants.TASKLISTPOSITION, position)
        intent.putExtra(Constants.PEOPLELIST, Assignedpeople)
        startActivityForResult(intent, REQUEST_CODE_CARD)

    }

    /**
     * Callback from to update current page and information
     */
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(resultCode==Activity.RESULT_OK && (requestCode== REQUEST_CODE_CARD ||requestCode== REQUEST_CODE_CARD1) ) {
            showProgressDialog(resources.getString(R.string.please_wait))
            FirestoreClass().getProjectDetails(this, projectDocumentId)
        }
    }
    /**
     * Callback method to get the list of people assigned to the project from the database.
     * @param list The list of people assigned to the project.
     */
    fun getPeopleFromProject(list:ArrayList<User>){
        Assignedpeople = list
        hideProgressDialog()
        val atl = Task("Add List")
        prjDetails.taskList.add(atl)

        binding.rvTaskList.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        binding.rvTaskList.setHasFixedSize(true)

        val a = TaskAdapter(this, prjDetails.taskList)
        binding.rvTaskList.adapter = a
    }

    /**
     * create menu
     */
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_people, menu)
        return super.onCreateOptionsMenu(menu)
    }

    /**
     * setup actions for items selected
     */
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.action_members ->{
                val intent = Intent(this, PeopleActivity::class.java)
                intent.putExtra(Constants.PROJECTDETAIL, prjDetails)
                startActivityForResult(intent, REQUEST_CODE_CARD1)
                return true
            }
            R.id.delete_project->{

                FirestoreClass().deleteProject(prjDetails.documentID)
                finish()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }
    /**
     * setup custom tool bar
     */
    private fun setupActionBar(title: String) {

        setSupportActionBar(binding.toolbarTaskListActivity)

        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_white_color_back_24dp)
            actionBar.title = prjDetails.name
        }

        binding.toolbarTaskListActivity.setNavigationOnClickListener { onBackPressed() }
    }

    /**
     * get informattion about tasklist from certain project
     */
    fun projectDetails(project: Project) {
        prjDetails = project

        hideProgressDialog()

        setupActionBar(project.name)


        showProgressDialog(resources.getString(R.string.please_wait))
        FirestoreClass().getPeopleDetailsFromProject(this, prjDetails.assignedTo)

    }
    fun addUpdateListOfTasksResult(){
        showProgressDialog(resources.getString(R.string.please_wait))
        FirestoreClass().getProjectDetails(this, prjDetails.documentID)
    }

    /**
     * add new tasklist
     */
    fun addTaskFully(name: String){
        val task = Task(name, FirestoreClass().getUserID())
        prjDetails.taskList.add(0, task)
        prjDetails.taskList.removeAt(prjDetails.taskList.size - 1)
        showProgressDialog(resources.getString(R.string.please_wait ))
        FirestoreClass().addUpdate(this, prjDetails)
    }

    /**
     * update tasklist
     */
    fun updateTaskFully(position: Int, name: String, model: Task){
        val task = Task(name, model.createdBy)

        prjDetails.taskList[position] = task
        prjDetails.taskList.removeAt(prjDetails.taskList.size-1)
        showProgressDialog(resources.getString(R.string.please_wait ))
        FirestoreClass().addUpdate(this@TaskListActivity, prjDetails)
    }

    /**
     * delete tasklist
     */
    fun deleteTaskFully(position: Int){
        prjDetails.taskList.removeAt(position)

        prjDetails.taskList.removeAt(prjDetails.taskList.size-1)
        showProgressDialog(resources.getString(R.string.please_wait ))
        FirestoreClass().addUpdate(this@TaskListActivity, prjDetails)
    }

    /**
     * fun to add card to the tasklist
     */
    fun addFullyCard(position: Int, name: String){
        prjDetails.taskList.removeAt(prjDetails.taskList.size-1)
        val users =ArrayList<String>()

        val card: Card =Card(name, FirestoreClass().getUserID(), users)

        val list = prjDetails.taskList[position].cards
        list.add(card)
        val task = Task(prjDetails.taskList[position].title, prjDetails.taskList[position].createdBy, list)

        prjDetails.taskList[position]=task
        showProgressDialog(resources.getString(R.string.please_wait ))
        FirestoreClass().addUpdate(this@TaskListActivity, prjDetails)
    }
}