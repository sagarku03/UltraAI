package com.termux.app

import android.app.Activity
import android.content.Intent
import android.os.Bundle

class TermuxOpenActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        intent?.data?.let { uri ->
            val viewIntent = Intent(Intent.ACTION_VIEW, uri)
            startActivity(viewIntent)
        }
        finish()
    }
}
