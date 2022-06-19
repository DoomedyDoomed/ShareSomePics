package com.dornhorn.sharesomepics.view

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.dornhorn.sharesomepics.R
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import java.util.*

class SharePhotograph : AppCompatActivity() {

    private var pickedImage : Uri? = null
    private var pickedBitmap : Bitmap? = null
    private lateinit var storage : FirebaseStorage
    private lateinit var auth : FirebaseAuth
    private lateinit var database : FirebaseFirestore

    private var resultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            // There are no request codes
            val data: Intent? = result.data
            pickedImage = data?.data

            if (pickedImage != null){
                if (Build.VERSION.SDK_INT >= 28)
                {
                    val source = ImageDecoder.createSource(this.contentResolver,pickedImage!!)
                    pickedBitmap = ImageDecoder.decodeBitmap(source)
                    val imageSelect = findViewById<ImageView>(R.id.imageView)
                    imageSelect.setImageBitmap(pickedBitmap)
                }
                else
                {
                    pickedBitmap = MediaStore.Images.Media.getBitmap(this.contentResolver,pickedImage)
                    val imageSelect = findViewById<ImageView>(R.id.imageView)
                    imageSelect.setImageBitmap(pickedBitmap)
                }
            }
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_share_photograph)

        val imageSelect = findViewById<ImageView>(R.id.imageView)
        val butShare = findViewById<Button>(R.id.butShare)
        val userCommentText = findViewById<TextView>(R.id.userCommentText)

        storage = FirebaseStorage.getInstance()
        auth = FirebaseAuth.getInstance()
        database = FirebaseFirestore.getInstance()

            imageSelect.setOnClickListener{
            pickImage()
        }

        butShare.setOnClickListener{
            //Storage Process
            //UUID -> Universal Unique ID
            val uuid = UUID.randomUUID()
            val imageName = "${uuid}.jpg"

            val reference = storage.reference
            val imageReference = reference.child("images").child(imageName)

            if  (pickedImage != null){
                imageReference.putFile(pickedImage!!).addOnSuccessListener {
                    val uploadedImageReference = FirebaseStorage.getInstance().reference.child("images").child(imageName)
                    uploadedImageReference.downloadUrl.addOnSuccessListener { uri ->
                        val downloadUrl = uri.toString()
                        val currentUserEmail = auth.currentUser!!.email.toString()
                        val userComment = userCommentText.text.toString()
                        val date = Timestamp.now()

                        //Database Process
                        val postHashMap = hashMapOf<String, Any>()
                        postHashMap["Image URL"] = downloadUrl
                        postHashMap["User Email"] = currentUserEmail
                        postHashMap["User Comment"] = userComment
                        postHashMap["Post Date"] = date

                        database.collection("Post").add(postHashMap).addOnCompleteListener { task ->
                            if(task.isSuccessful){
                                Toast.makeText(this,"Uploaded!",Toast.LENGTH_SHORT).show()
                                finish()
                            }
                        }.addOnFailureListener { exception ->
                            Toast.makeText(applicationContext,exception.localizedMessage,Toast.LENGTH_LONG).show()
                        }
                    }.addOnFailureListener { exception ->
                        Toast.makeText(applicationContext,exception.localizedMessage,Toast.LENGTH_LONG).show()
                    }
                }
            }
        }
    }
    private fun pickImage(){
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            //No Permission
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),1)
        }else {
            openGallery()
        }
    }
    private fun openGallery() {
        val galleryIntent = Intent(Intent.ACTION_PICK,MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        resultLauncher.launch(galleryIntent)
    }
}