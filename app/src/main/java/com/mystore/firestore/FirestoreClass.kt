package com.mystore.firestore

import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
import android.net.Uri
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.mystore.models.Product
import com.mystore.models.User
import com.mystore.ui.activities.*
import com.mystore.utils.Constants

class FirestoreClass {

    private val mFireStore = FirebaseFirestore.getInstance()
    fun registerUser(activity: RegisterActivity, userInfo: User) {

        mFireStore.collection(Constants.USERS)
            .document(userInfo.id)
            .set(userInfo, SetOptions.merge())
            .addOnSuccessListener {
                activity.userRegistrationSuccess()
            }
            .addOnFailureListener { e ->
                activity.hideProgressDialog()
                Log.e(
                    activity.javaClass.simpleName,
                    "Error while registering the user.",
                    e
                )
            }
    }

    fun getCurrentUserID(): String {
        val currentUser = FirebaseAuth.getInstance().currentUser
        var currentUserID = ""
        if (currentUser != null) {
            currentUserID = currentUser.uid
        }
        return currentUserID
    }

    fun getUserDetails(activity: Activity) {
        mFireStore.collection(Constants.USERS)
            .document(getCurrentUserID())
            .get()
            .addOnSuccessListener { document ->
                Log.i(activity.javaClass.simpleName, document.toString())
                val user = document.toObject(User::class.java)!!
                val sharedPreferences =
                    activity.getSharedPreferences(
                        Constants.MYSTORE_PREFERENCES,
                        Context.MODE_PRIVATE
                    )
                val editor: SharedPreferences.Editor = sharedPreferences.edit()
                editor.putString(
                    Constants.LOGGED_IN_USERNAME,
                    "${user.firstName} ${user.lastName}"
                )
                editor.apply()

                when (activity) {
                    is LoginActivity -> {
                        activity.userLoggedInSuccess(user)
                    }
                    is SettingsActivity -> {
                        activity.userDetailsSuccess(user)
                    }
                }
            }
            .addOnFailureListener { e ->
                when (activity) {
                    is LoginActivity -> {
                        activity.hideProgressDialog()
                    }
                    is SettingsActivity -> {
                        activity.hideProgressDialog()
                    }
                }
                Log.e(
                    activity.javaClass.simpleName,
                    "Error while getting user details.",
                    e
                )
            }
    }

    fun uploadImageToCloudStorage(activity: Activity, imageFileURI: Uri?, imageType: String) {
        val sRef: StorageReference = FirebaseStorage.getInstance().reference.child(
            imageType + System.currentTimeMillis() + "."
                    + Constants.getFileExtension(
                activity,
                imageFileURI
            )
        )

        sRef.putFile(imageFileURI!!)
            .addOnSuccessListener { taskSnapshot ->
                Log.e(
                    "Firebase Image URL",
                    taskSnapshot.metadata!!.reference!!.downloadUrl.toString()
                )
                taskSnapshot.metadata!!.reference!!.downloadUrl
                    .addOnSuccessListener { uri ->
                        Log.e("Downloadable Image URL", uri.toString())
                        when (activity) {
                            is UserProfileActivity -> {
                                activity.imageUploadSuccess(uri.toString())
                            }

                            is AddProductActivity -> {
                                activity.imageUploadSuccess(uri.toString())
                            }
                        }
                    }
            }
            .addOnFailureListener { exception ->
                when (activity) {
                    is UserProfileActivity -> {
                        activity.hideProgressDialog()
                    }
                    is AddProductActivity -> {
                        activity.hideProgressDialog()
                    }
                }
                Log.e(
                    activity.javaClass.simpleName,
                    exception.message,
                    exception
                )
            }
    }

//    fun uploadImageToCloudStorage(activity: Activity, imageFileURI: Uri?) {
//        val sRef: StorageReference = FirebaseStorage.getInstance().reference.child(
//            Constants.USER_PROFILE_IMAGE + System.currentTimeMillis() + "."
//                    + Constants.getFileExtension(
//                activity,
//                imageFileURI
//            )
//        )
//
//        sRef.putFile(imageFileURI!!)
//            .addOnSuccessListener { taskSnapshot ->
//                Log.e(
//                    "Firebase Image URL",
//                    taskSnapshot.metadata!!.reference!!.downloadUrl.toString()
//                )
//
//                taskSnapshot.metadata!!.reference!!.downloadUrl
//                    .addOnSuccessListener { uri ->
//                        Log.e("Downloadable Image URL", uri.toString())
//
//                        when (activity) {
//                            is UserProfileActivity -> {
//                                activity.imageUploadSuccess(uri.toString())
//                            }
//                        }
//                    }
//            }
//            .addOnFailureListener { exception ->
//
//                when (activity) {
//                    is UserProfileActivity -> {
//                        activity.hideProgressDialog()
//                        activity.showErrorSnackBar("User Activity image error", true)
//                    }
//                }
//
//                Log.e(
//                    activity.javaClass.simpleName,
//                    exception.message,
//                    exception
//                )
//            }
//    }

    fun updateUserProfileData(activity: Activity, userHashMap: HashMap<String, Any>) {
        mFireStore.collection(Constants.USERS)
            .document(getCurrentUserID())
            .update(userHashMap)
            .addOnSuccessListener {

                when (activity) {
                    is UserProfileActivity -> {
                        activity.userProfileUpdateSuccess()
                    }
                }
            }
            .addOnFailureListener { e ->

                when (activity) {
                    is UserProfileActivity -> {
                        activity.hideProgressDialog()
                    }
                }

                Log.e(
                    activity.javaClass.simpleName,
                    "Error while updating the user details.",
                    e
                )
            }
    }

    fun uploadProductDetails(activity: AddProductActivity, productInfo: Product) {
        mFireStore.collection(Constants.PRODUCTS)
            .document()
            .set(productInfo, SetOptions.merge())
            .addOnSuccessListener {
                activity.productUploadSuccess()
            }
            .addOnFailureListener { e ->
                activity.hideProgressDialog()
                Log.e(
                    activity.javaClass.simpleName,
                    "Error while uploading the product details.",
                    e
                )
            }
    }

}