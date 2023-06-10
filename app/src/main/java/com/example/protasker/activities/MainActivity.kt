package com.example.protasker.activities

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.core.view.GravityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.protasker.R
import com.example.protasker.adapters.ProjectItemsAdapter
import com.example.protasker.databinding.ActivityMainBinding
import com.example.protasker.firebase.FirestoreClass
import com.example.protasker.models.Project
import com.example.protasker.models.User
import com.example.protasker.utils.Constants
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth


class MainActivity : BaseActivity(), NavigationView.OnNavigationItemSelectedListener {
    private lateinit var binding: ActivityMainBinding
    companion object{
        const val PROFILE_CODE = 11
        const val CREATE_PROJECT_CODE =12
    }

    private lateinit var userName: String
    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityMainBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        setupActionBar()

        binding.navView.setNavigationItemSelectedListener (this)
        FirestoreClass().loadDataOfUser(this, true)
        findViewById<com.google.android.material.floatingactionbutton.FloatingActionButton>(R.id.fab_create_board)
            .setOnClickListener{
                val intent = Intent(this@MainActivity, CreateProjectActivity::class.java)
                intent.putExtra(Constants.NAME, userName)
                startActivityForResult(intent, CREATE_PROJECT_CODE)
            }

    }

    /**
     * transfer list of project to UI
     */

    fun listToUI(projects: ArrayList<Project>){
        hideProgressDialog()

        if(projects.size>0){
            findViewById<androidx.recyclerview.widget.RecyclerView>(R.id.rv_projects_list).visibility=
                View.VISIBLE
            findViewById<TextView>(R.id.tv_no_projects_available).visibility = View.GONE
            findViewById<androidx.recyclerview.widget.RecyclerView>(R.id.rv_projects_list).layoutManager=LinearLayoutManager(this)
            findViewById<androidx.recyclerview.widget.RecyclerView>(R.id.rv_projects_list).setHasFixedSize(true)

            val adapter = ProjectItemsAdapter(this, projects)
            findViewById<androidx.recyclerview.widget.RecyclerView>(R.id.rv_projects_list).adapter = adapter

            adapter.setOnClickListener(object : ProjectItemsAdapter.OnClickListener{
                override fun onClick(position: Int, model: Project) {
                    val intent =Intent(this@MainActivity, TaskListActivity::class.java)
                    intent.putExtra(Constants.DOCUMENT_ID, model.documentID)
                    startActivity(intent)
                }
            })
        }else{
            findViewById<androidx.recyclerview.widget.RecyclerView>(R.id.rv_projects_list).visibility=
                View.GONE
            findViewById<TextView>(R.id.tv_no_projects_available).visibility = View.VISIBLE
        }
    }

    /**
     * update data of navigation panel
     */
    fun updateNavData(user: User, readProjectList: Boolean) {

        val headerView = binding.navView.getHeaderView(0)

        val navUserImage = headerView.findViewById<ImageView>(R.id.iv_user_image)

        userName = user.name

        Glide
            .with(this@MainActivity)
            .load(user.image)
            .centerCrop()
            .placeholder(R.drawable.ic_user_place_holder)
            .into(navUserImage)

        val navUsername = headerView.findViewById<TextView>(R.id.tv_username)
        navUsername.text = user.name

        if(readProjectList){
            showProgressDialog(resources.getString(R.string.please_wait))
            FirestoreClass().getProjectsList(this)
        }
    }

    /**
     * setup custom toolbar
     */
    private fun setupActionBar(){
        setSupportActionBar(findViewById(R.id.toolbar_main_activity))
        findViewById<androidx.appcompat.widget.Toolbar>(R.id.toolbar_main_activity).setNavigationIcon(R.drawable.ic_navigation_menu)
        findViewById<androidx.appcompat.widget.Toolbar>(R.id.toolbar_main_activity).setNavigationOnClickListener{
            toggleDrawer()
        }
    }

    /**
     * toggle a drawer/panel
     */
    private fun toggleDrawer() {

        if (binding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
            binding.drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            binding.drawerLayout.openDrawer(GravityCompat.START)
        }
    }

    override fun onBackPressed() {
        if (binding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
            binding.drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            doubleBackToExit()
        }
    }

    /**
     * update information after certain actions
     */
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(resultCode==Activity.RESULT_OK&& requestCode== PROFILE_CODE){
            FirestoreClass().loadDataOfUser(this, true)
        }
        else if(resultCode == Activity.RESULT_OK &&
                requestCode == CREATE_PROJECT_CODE){
            FirestoreClass().getProjectsList(this)
        }
        else{
            Log.e("Cancelled", "Cancelle")
        }
    }

    /**
     * update information on resume of this activity
     */
    override fun onResume() {
        super.onResume()

        FirestoreClass().getProjectsList(this)
    }

    /**
     * setting up action on choosing navigation items
     */
    override fun onNavigationItemSelected(menuItem: MenuItem): Boolean {
        when (menuItem.itemId) {
            R.id.nav_my_profile -> {
               startActivityForResult(Intent(this, MyProfileActivity::class.java), PROFILE_CODE)
            }
            R.id.nav_sign_out -> {
                FirebaseAuth.getInstance().signOut()

                val intent = Intent(this, IntroActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
                finish()
            }
            R.id.nav_my_cards->{
                val intent = Intent(this, MyCardsActivity::class.java)
                startActivity(intent)

            }
        }
        binding.drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }
}