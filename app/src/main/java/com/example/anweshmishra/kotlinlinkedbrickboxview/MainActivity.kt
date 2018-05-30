package com.example.anweshmishra.kotlinlinkedbrickboxview

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.example.linkedbrickboxview.LinkedBrickBoxView

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        LinkedBrickBoxView.create(this)
    }
}
