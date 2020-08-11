package com.eit.enhancedconsultant.view

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.eit.enhancedconsultant.R
import com.eit.enhancedconsultant.model.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase


class MainActivity : AppCompatActivity() {

    // Firebase instance variables
    private val database = Firebase.database.reference // Database (Firebase) reference for entire project
    private val auth = Firebase.auth
    private var user: FirebaseUser? = null

    // User properties (pre-defined by Google; we CANNOT add more fields)
    private var uid: String? = null
    private var email: String? = null
    private var displayName: String? = null
    private var photoUrl: Uri? = null
    private var isEmailVerified: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        user = auth.currentUser

        if (user == null) {
            // Not signed in, launch the Sign In activity
            startActivity(Intent(this, EmailPasswordActivity::class.java))
            finish()
            return
        } else {
            getUserProfile()
        }

    }

    override fun onStart() {
        super.onStart()
    }

    private fun getUserProfile() {
        // [START get_user_profile]
        user = Firebase.auth.currentUser
        user?.let {
            // The user's ID, unique to the Firebase project
            uid = (user as FirebaseUser).uid
            database
                .child("users")
                .child(uid as String)
                .child("tasks")
                .addValueEventListener(taskListener)

            // Name, email address, and profile photo Url
            displayName = (user as FirebaseUser).displayName
            email = (user as FirebaseUser).email
            photoUrl = (user as FirebaseUser).photoUrl

            // Check if user's email is verified
            isEmailVerified = (user as FirebaseUser).isEmailVerified
        }
        // [END get_user_profile]
    }

    private fun addUser(uid: String, contactNumber: String, email: String, firstName: String,
                        lastName: String, level: Int, loggedIn: Boolean, password: String,
                        userName: String) {
        val user =
            User(contactNumber, email, firstName, lastName, level, loggedIn, password, userName)
        val completeUser = CompleteUser(user = user)
        val completeUserValues = completeUser.toMap()

        val childUpdates = hashMapOf<String, Any>(
            "/users/$uid" to completeUserValues
        )
        database.updateChildren(childUpdates) // Try .updateChildrenAsync here-------------------------------------------------
    }

    private fun addTask(accepted: Boolean, assignedBy: String, assignedTo: String,
                        completed: Boolean, declined: Boolean, dueDate: Int,
                        name: String, timeStamp: Int) {
        // Reuse same key for all three "tables"
        val key = database.child("members").push().key
        if (key == null) {
            Log.w(TAG, "Couldn't get push key")
            return
        }
        // members "table"
        val membersTask = MembersTask(assignedBy, assignedTo)
        val membersTaskValues = membersTask.toMap()

        // meta "table"
        val metaTask = MetaTask(dueDate, name, timeStamp)
        val metaTaskValues = metaTask.toMap()

        // tasks "table"
        val completeTask =
            CompleteTask(accepted, assignedBy, assignedTo,
                completed, declined, dueDate, name, timeStamp
            )
        val completeTaskValues = completeTask.toMap()

        val childUpdates = hashMapOf<String, Any>(
            "/members/$key" to membersTaskValues,
            "/meta/$key" to metaTaskValues,
            "/tasks/$key" to completeTaskValues
        )
        database.updateChildren(childUpdates) // Try .updateChildrenAsync here-------------------------------------------------
    }

    // Add value event listener to the post
    private val taskListener = object : ValueEventListener {
        override fun onDataChange(dataSnapshot: DataSnapshot) {
            // Get Post object and use the values to update the UI
            val task = dataSnapshot.getValue<CompleteTask>()
            task?.let {
                // Update UI with new task information and send push notification-----------------------------------------------------------------------
            }
        }

        override fun onCancelled(databaseError: DatabaseError) {
            // Getting Post failed, log a message
            Log.w(TAG, "loadPost:onCancelled", databaseError.toException())
            Toast.makeText(this@MainActivity, "Failed to load task.",
                Toast.LENGTH_SHORT).show()
        }
    }

    private fun deleteUser(uid: String) {
        database
            .child("users")
            .child(uid)
            .removeValue()
    }

    private fun deleteTask(uid: String, key: String) {
        database
            .child("users")
            .child(uid).child("tasks")
            .child(key)
            .removeValue()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.sign_out_menu -> {
                auth.signOut()
                user = null
                uid = null
                email = null
                displayName = null
                photoUrl = null
                isEmailVerified = false
                startActivity(Intent(this, EmailPasswordActivity::class.java))
                finish()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    companion object {
        private const val TAG = "MainActivity"
    }
}