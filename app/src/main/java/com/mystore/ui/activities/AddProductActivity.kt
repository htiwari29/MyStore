package com.mystore.ui.activities

import android.os.Bundle
import com.mystore.R
import kotlinx.android.synthetic.main.activity_add_product.*

@Suppress("DEPRECATION")
class AddProductActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_product)
        setupActionBar()
    }

    private fun setupActionBar() {
        setSupportActionBar(toolbar_add_product_activity)
        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_black_color_white_24dp)
        }
        toolbar_add_product_activity.setNavigationOnClickListener { onBackPressed() }
    }

}