package com.mystore.ui.activities

import android.os.Bundle
import android.text.TextUtils
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.AppCompatCheckBox
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.mystore.R
import com.mystore.firestore.FirestoreClass
import com.mystore.models.User

class RegisterActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        setupActionBar()

        val btnRegister = findViewById<Button>(R.id.btn_register)
        btnRegister.setOnClickListener {
            registerUser()
        }

        val tvLogin = findViewById<TextView>(R.id.tv_login)
        tvLogin.setOnClickListener {
            onBackPressed()
        }
    }

    private fun setupActionBar() {
        val toolbarRegisterActivity  = findViewById<androidx.appcompat.widget.Toolbar>(R.id.toolbar_register_activity)
        setSupportActionBar(toolbarRegisterActivity)
        supportActionBar?.setDisplayShowTitleEnabled(false)

        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_black_color_white_24dp)
        }
        toolbarRegisterActivity.setNavigationOnClickListener { onBackPressed() }
    }

    private fun validateRegisterDetails(): Boolean {
        val etFirstName = findViewById<EditText>(R.id.et_first_name)
        val etLastName = findViewById<EditText>(R.id.et_last_name)
        val etEmail = findViewById<EditText>(R.id.et_email)
        val etPassword = findViewById<EditText>(R.id.et_password)
        val etConfirmPassword = findViewById<EditText>(R.id.et_confirm_password)
        val cbTermsAndCondition = findViewById<AppCompatCheckBox>(R.id.cb_terms_and_condition)
        return when {
            TextUtils.isEmpty(etFirstName.text.toString().trim { it <= ' ' }) -> {
                showErrorSnackBar(
                    resources.getString(R.string.err_msg_enter_first_name),
                    true)
                false
            }

            TextUtils.isEmpty(etLastName.text.toString().trim { it <= ' ' }) -> {
                showErrorSnackBar(
                    resources.getString(R.string.err_msg_enter_last_name),
                    true)
                false
            }

            TextUtils.isEmpty(etEmail.text.toString().trim { it <= ' ' }) ||
                    !etEmail.text.toString().matches(
                        Regex("[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+"))-> {
                showErrorSnackBar(
                    resources.getString(R.string.err_msg_enter_email),
                    true)
                false
            }

            TextUtils.isEmpty(etPassword.text.toString().trim { it <= ' ' }) ||
                    etPassword.text.toString().length < 6-> {
                showErrorSnackBar(
                    resources.getString(R.string.err_msg_enter_password),
                    true)
                false
            }

            TextUtils.isEmpty(etConfirmPassword.text.toString().trim { it <= ' ' }) -> {
                showErrorSnackBar(
                    resources.getString(R.string.err_msg_enter_confirm_password),
                    true)
                false
            }

            etPassword.text.toString().trim { it <= ' ' } != etConfirmPassword.text.toString()
                .trim { it <= ' ' } -> {
                showErrorSnackBar(
                    resources.getString(R.string.err_msg_password_and_confirm_password_mismatch),
                    true)
                false
            }
            !cbTermsAndCondition.isChecked -> {
                showErrorSnackBar(resources.getString(R.string.err_msg_agree_terms_and_condition),
                    true)
                false
            }
            else -> {
//                showErrorSnackBar("Your details are valid.", false)
                true
            }
        }
    }
    private fun registerUser() {
        val et_email = findViewById<EditText>(R.id.et_email)
        val et_password = findViewById<EditText>(R.id.et_password)
        val et_first_name = findViewById<EditText>(R.id.et_first_name)
        val et_last_name = findViewById<EditText>(R.id.et_last_name)

        if (validateRegisterDetails()){

            showProgressDialog(resources.getString(R.string.please_wait))

            val email: String = et_email.text.toString().trim { it <= ' ' }
            val password: String = et_password.text.toString().trim { it <= ' ' }

            FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->

                    if (task.isSuccessful) {
                        val firebaseUser : FirebaseUser = task.result!!.user!!
                        val user = User(
                            firebaseUser.uid,
                            et_first_name.text.toString().trim { it <= ' ' },
                            et_last_name.text.toString().trim { it <= ' ' },
                            et_email.text.toString().trim { it <= ' ' }
                        )
                        FirestoreClass().registerUser(this@RegisterActivity, user)

                    }
                    else {
                        hideProgressDialog()
                        showErrorSnackBar(task.exception!!.message.toString(), true)
                    }
                }
        }
    }
    fun userRegistrationSuccess() {

        hideProgressDialog()

        Toast.makeText(
            this@RegisterActivity,
            resources.getString(R.string.register_success),
            Toast.LENGTH_SHORT
        ).show()

        FirebaseAuth.getInstance().signOut()
        finish()
    }
}