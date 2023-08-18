package com.example.finaltest

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.example.finaltest.adapter.FamousPeopleAdapter
import com.example.finaltest.dataclass.FamousPeople
import com.google.firebase.firestore.FirebaseFirestore

class AddEditFamousPeopleActivity : AppCompatActivity() {
    private lateinit var peopleIDEditText: EditText
    private lateinit var nameEditText: EditText
    private lateinit var occupationEditText: EditText
    private lateinit var nationalityEditText: EditText
    private lateinit var birthPlaceEditText: EditText
    private lateinit var birthDateEditText: EditText
    private lateinit var bioEditText: EditText
    private lateinit var achievementsEditText: EditText
    private lateinit var txtViewAddEditFamousPeople: TextView
    private lateinit var updateButton: Button
    private var userArrayList: MutableList<FamousPeople> = mutableListOf()
    private lateinit var cancelButton: Button
    private var famousPeople: FamousPeople? = null
    private lateinit var myAdapter: FamousPeopleAdapter
    private lateinit var db: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_edit_famous_people)
        db = FirebaseFirestore.getInstance()
        txtViewAddEditFamousPeople = findViewById(R.id.txtViewAddEditFamousPeople)
        peopleIDEditText = findViewById(R.id.editTextPeopleId)
        nameEditText = findViewById(R.id.editTextName)
        occupationEditText = findViewById(R.id.editTextOccupation)
        nationalityEditText = findViewById(R.id.editTextNationality)
        birthPlaceEditText = findViewById(R.id.editTextBirthPlace)
        birthDateEditText = findViewById(R.id.editTextBirthDate)
        bioEditText = findViewById(R.id.editTextBio)
        achievementsEditText = findViewById(R.id.editTextAchievements)
        updateButton = findViewById(R.id.buttonUpdate)
        cancelButton = findViewById(R.id.btnCancel)
        val context: Context = applicationContext
        myAdapter = FamousPeopleAdapter(context, userArrayList)

        cancelButton.setOnClickListener {
            val intent = Intent(this, FamousPeopleListActivity::class.java)
            startActivity(intent)
        }
        famousPeople = intent.getParcelableExtra("famouspeople")
        if (famousPeople != null) {
            // Editing existing persom
            peopleIDEditText.setText(famousPeople!!.peopleID.toString())
            nameEditText.setText(famousPeople!!.name)
            occupationEditText.setText(famousPeople!!.occupation)
            nationalityEditText.setText(famousPeople!!.nationality)
            birthDateEditText.setText(famousPeople!!.birthDate)
            birthPlaceEditText.setText(famousPeople!!.birthPlace)
            bioEditText.setText(famousPeople!!.bio.toString())
            achievementsEditText.setText(famousPeople!!.achievements.toString())
            updateButton.text = "Update"
            txtViewAddEditFamousPeople.text = "Update Famous People "
        } else {
            updateButton.text = "Add"
            txtViewAddEditFamousPeople.text = "Add Famous People"
        }
        updateButton.setOnClickListener {
            updateFamousPeople()

        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun updateFamousPeople() {
        val db = FirebaseFirestore.getInstance()

        val peopleID = peopleIDEditText.text.toString().toLong()
        val name = nameEditText.text.toString()
        val nationality = nationalityEditText.text.toString()
        val occupation = occupationEditText.text.toString()
        val birthDate = birthDateEditText.text.toString()
        val birthPlace = birthPlaceEditText.text.toString()
        val bio = bioEditText.text.toString()
        val achievements = achievementsEditText.text.split(",").map { it.trim() }

        if (famousPeople != null) {
            val movieDocumentRef = db.collection("famouspeople").document(famousPeople!!.documentID!!)
            movieDocumentRef.update(
                "peopleID", peopleID,
                "name", name,
                "nationality", nationality,
                "occupation", occupation,
                "birthDate", birthDate,
                "birthPlace", birthPlace,
                "bio", bio,
                "achievements", achievements
            )
                .addOnSuccessListener {
                    myAdapter.famousPeopleUpdateCallback!!.invoke(famousPeople!!)
                    Toast.makeText(this, "Person updated successfully.", Toast.LENGTH_SHORT).show()
                    finish()
                }
                .addOnFailureListener { e ->
                    Toast.makeText(this, "Error updating Person: ${e.message}", Toast.LENGTH_SHORT)
                        .show()
                }
        } else {
            val newPerson = hashMapOf(
                "peopleID" to peopleID,
                "name" to name,
                "nationality" to nationality,
                "occupation" to occupation,
                "birthDate" to birthDate,
                "birthPlace" to birthPlace,
                "bio" to bio,
                "achievements" to achievements
            )
            db.collection("famouspeople")
                .add(newPerson)
                .addOnSuccessListener { documentReference ->
                    myAdapter.notifyDataSetChanged()
                    Toast.makeText(this, "Person added successfully.", Toast.LENGTH_SHORT).show()
                    finish()
                }
                .addOnFailureListener { e ->
                    Toast.makeText(this, "Error adding Person: ${e.message}", Toast.LENGTH_SHORT)
                        .show()
                }
        }
    }
}