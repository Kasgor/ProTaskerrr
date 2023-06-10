package com.example.protasker.firebase

import android.app.Activity
import android.util.Log
import com.example.protasker.activities.*
import com.example.protasker.models.Project
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.example.protasker.models.User
import com.example.protasker.utils.Constants


class FirestoreClass {

    private val mFireStore = FirebaseFirestore.getInstance()

    /**
     * Registers a new user in the Firestore database.
     * @param activity The SignUpActivity instance.
     * @param userInfo The user information to be registered.
     */
    fun registerUser(activity:SingUpActivity, userInfo: User) {

        mFireStore.collection(Constants.USERS)

            .document(getUserID())

            .set(userInfo, SetOptions.merge())
            .addOnSuccessListener {
                activity.userRegisteredSuccess()
            }
            .addOnFailureListener { e ->
                Log.e(
                    activity.javaClass.simpleName,
                    "Error writing document",
                    e
                )
            }
    }
    /**
     * Deletes a project from the Firestore database.
     * @param projectDocumentId The ID of the project document to be deleted.
     */
    fun deleteProject(projectDocumentId: String) {
        val db = FirebaseFirestore.getInstance()
        val collectionRef = db.collection(Constants.POJECTS)

        collectionRef.document(projectDocumentId)
            .delete()
            .addOnSuccessListener {

            }
            .addOnFailureListener { e ->

            }
    }
    /**
     * Retrieves details about a user based on their email address.
     * @param activity The PeopleActivity instance.
     * @param email The email address of the user to retrieve details about.
     */
    fun getDetailsAboutPeople(activity: PeopleActivity, email: String){
        mFireStore.collection(Constants.USERS)
            .whereEqualTo(Constants.EMAIL, email)
            .get()
            .addOnSuccessListener { doc->
                if(doc.documents.size>0){
                    val user = doc.documents[0].toObject(User::class.java)!!
                    activity.getPropleInfo(user)
                }
                else{
                    activity.hideProgressDialog()
                }
            }
            .addOnFailureListener { activity.hideProgressDialog() }
    }
    /**
     * Assigns a user to a project in the Firestore database.
     * @param activity The PeopleActivity instance.
     * @param project The project to assign the user to.
     * @param user The user to be assigned.
     */
    fun assign(activity: PeopleActivity, project: Project, user: User){
        val assignedTo = HashMap<String, Any>()
        assignedTo[Constants.ASSINGED_TO] = project.assignedTo

        mFireStore.collection(Constants.POJECTS)
            .document(project.documentID)
            .update(assignedTo)
            .addOnSuccessListener {
                activity.peopleSuccess(user)
            }
            .addOnFailureListener { activity.hideProgressDialog() }
    }
    /**
     * Deassigns a user from a project in the Firestore database.
     * @param activity The PeopleActivity instance.
     * @param project The project to deassign the user from.
     * @param user The user to be deassigned.
     */
    fun deassign(activity: PeopleActivity, project:Project, user:User){
        project.taskList.removeAt(project.taskList.size-1)
        val hash  = hashMapOf<String, Any>(
            "name" to project.name,
            "image" to project.image,
            "createdBy" to project.createdBy,
            "assignedTo" to project.assignedTo,
            "documentID" to project.documentID,
            "taskList" to project.taskList

        )

        mFireStore.collection(Constants.POJECTS)
            .document(project.documentID)
            .update(hash)
            .addOnSuccessListener {
                activity.peopleSuccess2(user)
            }
            .addOnFailureListener { activity.hideProgressDialog() }
    }


    /**
     * Creates a new project in the Firestore database.
     * @param activity The CreateProjectActivity instance.
     * @param prInfo The project information to be created.
     */
    fun createProject(activity:CreateProjectActivity, prInfo: Project) {

        mFireStore.collection(Constants.POJECTS)

            .document()

            .set(prInfo, SetOptions.merge())
            .addOnSuccessListener {
                activity.projectCreatedSuccessfully()
            }
            .addOnFailureListener { e ->
                Log.e(
                    activity.javaClass.simpleName,
                    "Error writing document",
                    e
                )
            }
    }
    /**
     * Retrieves a list of projects assigned to the current user from the Firestore database.
     * @param activity The calling activity instance.
     */
    fun getProjectsList(activity: Activity){
        mFireStore.collection(Constants.POJECTS)
            .whereArrayContains(Constants.ASSINGED_TO, getUserID())
            .get()
            .addOnSuccessListener {
                document ->
                val projectsList : ArrayList<Project> = ArrayList()
                for(i in document.documents){
                    val project = i.toObject(Project::class.java)!!
                    project.documentID = i.id
                    projectsList.add(project)
                }
                if(activity is MainActivity) {
                    activity.listToUI(projectsList)
                }
                if(activity is MyCardsActivity){
                    activity.setupAdapter(projectsList)
                }
            }. addOnFailureListener {
                if(activity is MainActivity) {
                    activity.hideProgressDialog()
                }
                if(activity is MyCardsActivity){
                    activity.hideProgressDialog()
                }

            }
    }
    /**
     * Updates the user profile data in the Firestore database.
     * @param activity The MyProfileActivity instance.
     * @param userHashMap The updated user data to be stored.
     */
   fun updateUserProfileData(activity: MyProfileActivity, userHashMap: HashMap<String, Any>){
        mFireStore.collection(Constants.USERS)
            .document(getUserID())
            .update(userHashMap)
            .addOnSuccessListener {
                activity.profileUpdateSuccess()
            }.addOnFailureListener {
                exeption->
                activity.hideProgressDialog()
            }
    }
    /**
     * Retrieves details of a project from the Firestore database.
     * @param activity The TaskListActivity instance.
     * @param projectDocumentId The ID of the project document to retrieve details from.
     */
    fun getProjectDetails (activity: TaskListActivity, projectDocumentId: String ){
        mFireStore.collection(Constants.POJECTS)
            .document(projectDocumentId)
            .get()
            .addOnSuccessListener {
                    document ->
                val project = document.toObject(Project::class.java)!!
                project.documentID = document.id
                activity.projectDetails(project)
            }
            .addOnFailureListener { document -> activity.hideProgressDialog()
                Log.d("Firebase", "Document data: ${document.message}")}
    }

    /**
     * Loads data of the current user from the Firestore database.
     * @param activity The calling activity instance.
     * @param readProjectsList Flag indicating whether to read the projects list or not.
     */
    fun loadDataOfUser(activity: Activity, readProjectsList: Boolean = false) {


        mFireStore.collection(Constants.USERS)

            .document(getUserID())
            .get()
            .addOnSuccessListener { document ->
                Log.e(
                    activity.javaClass.simpleName, document.toString()
                )

                val loggedInUser = document.toObject(User::class.java)!!

                when (activity) {
                    is SingInActivity -> {
                        activity.signInSuccess(loggedInUser)
                    }
                    is MainActivity -> {
                        activity.updateNavData(loggedInUser , readProjectsList)
                    }
                    is MyProfileActivity->{
                        activity.setUI(loggedInUser)
                    }
                }

            }
            .addOnFailureListener { e ->

                when (activity) {
                    is SingInActivity -> {
                        activity.hideProgressDialog()
                    }
                    is MainActivity -> {
                        activity.hideProgressDialog()
                    }
                    is MyProfileActivity->{
                        activity.hideProgressDialog()
                    }
                }
            }
    }
    /**
     * Adds or updates a project in the Firestore database.
     * @param activity The calling activity instance.
     * @param project The project to add or update.
     */
    fun addUpdate(activity: Activity, project: Project){
        val hash = HashMap<String, Any>()
        hash[Constants.TASK] = project.taskList

        mFireStore.collection(Constants.POJECTS)
            .document(project.documentID)
            .update(hash)
            .addOnSuccessListener {
                if(activity is TaskListActivity) {
                    activity.hideProgressDialog()
                    activity.addUpdateListOfTasksResult()
                }
                else if(activity is CardActivity){
                    activity.addUpdateListOfTasksResult()
                }
            }
            .addOnFailureListener {e->
                if(activity is TaskListActivity)
                activity.hideProgressDialog()
                else if(activity is CardActivity)
                    activity.hideProgressDialog()
                Log.e(
                    activity.javaClass.simpleName,
                    "Error",
                    e
                )
            }
    }

    /**
     * Retrieves the ID of the current user.
     * @return The ID of the current user.
     */
    fun getUserID(): String {
        val currentUser = FirebaseAuth.getInstance().currentUser


        var currentUserID = ""
        if (currentUser != null) {
            currentUserID = currentUser.uid
        }

        return currentUserID
    }
    /**
     * Retrieves details about people assigned to a project from the Firestore database.
     * @param activity The calling activity instance.
     * @param assignedTo The list of user IDs assigned to the project.
     */
    fun getPeopleDetailsFromProject(activity: Activity, assignedTo: ArrayList<String>){
        mFireStore.collection(Constants.USERS)
            .whereIn(Constants.ID , assignedTo)
            .get()
            .addOnSuccessListener {
                doc->
                val users = ArrayList<User>()
                for (i in doc.documents){
                    val user = i.toObject(User::class.java)
                    if (user != null) {
                        users.add(user)
                    }
                }
                if(activity is PeopleActivity)
                activity.createPeopleList(users)
                else if(activity is TaskListActivity){
                    activity.getPeopleFromProject(users)
                }
            }
    }


}