package com.example.finaltest

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.finaltest.adapter.FamousPeopleAdapter
import com.example.finaltest.dataclass.FamousPeople
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.EventListener
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.QuerySnapshot

class FamousPeopleListActivity : AppCompatActivity() {

    private lateinit var db: FirebaseFirestore
    private lateinit var userArrayList: MutableList<FamousPeople>
    private lateinit var myAdapter: FamousPeopleAdapter
    lateinit var recyclerView: RecyclerView
    lateinit var btnAdd: Button
    lateinit var btnClose: Button
    private lateinit var userId: String

    companion object {
        private const val ADD_EDIT_REQUEST_CODE = 1
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_famous_people_list)
        val context: android.content.Context = applicationContext
        recyclerView = findViewById(R.id.recyclerView)
        btnAdd = findViewById(R.id.btnAdd)
        btnClose = findViewById(R.id.btnClose)
        btnClose.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)

        }
        userId = FirebaseAuth.getInstance().currentUser!!.uid
        // Set up RecyclerView
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.setHasFixedSize(true)
        userArrayList = mutableListOf()
        myAdapter = FamousPeopleAdapter(context, userArrayList)
        recyclerView.adapter = myAdapter
        myAdapter.famousPeopleUpdateCallback = { famousPeople ->
            val intent = Intent(this, AddEditFamousPeopleActivity::class.java)
            intent.putExtra("famouspeople", famousPeople)
            startActivityForResult(intent, ADD_EDIT_REQUEST_CODE)
        }
        EventChangeListener()
        btnAdd.setOnClickListener {
            val intent = Intent(this, AddEditFamousPeopleActivity::class.java)
            startActivity(intent)
        }
        onDeleteFamousPeople()
    }
    private fun onDeleteFamousPeople() {
        myAdapter.onDeleteClickListener = {
            val dialog = AlertDialog.Builder(this)
                .setTitle("Delete Famous Person")
                .setMessage("Are you sure you want to delete this person?")
                .setPositiveButton("Delete") { _, _ ->
                    deleteFamousPeople(it)
                }
                .setNegativeButton("Cancel", null)
                .create()
            dialog.show()
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun deleteFamousPeople(famousPeople: FamousPeople) {
        db = FirebaseFirestore.getInstance()
        db.collection("famouspeople").document(famousPeople.documentID ?: "")
            .delete()
            .addOnSuccessListener {
                userArrayList.remove(famousPeople)
                myAdapter.notifyDataSetChanged()
                Toast.makeText(this, "Person Deleted Successfully...", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener {
                Toast.makeText(this, "Failed to delete....", Toast.LENGTH_SHORT).show()
            }
    }

    private fun EventChangeListener() {
        db = FirebaseFirestore.getInstance()
        db.collection("famouspeople").addSnapshotListener(object : EventListener<QuerySnapshot> {
            @SuppressLint("NotifyDataSetChanged")
            override fun onEvent(
                value: QuerySnapshot?,
                error: FirebaseFirestoreException?
            ) {
                if (error != null) {
                    Log.d(ContentValues.TAG, "onEvent: ${error.message.toString()}")
                    return
                }
                for (dc: DocumentChange in value?.documentChanges!!) {
                    if (dc.type == DocumentChange.Type.ADDED) {
                        val famousPeople = dc.document.toObject(FamousPeople::class.java)
                        famousPeople.documentID = dc.document.id
                        userArrayList.add(famousPeople)
                    }
                }
                myAdapter.notifyDataSetChanged()
            }
        })
    }
}