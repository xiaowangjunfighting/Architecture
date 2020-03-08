package com.example.architectureinaction

import android.view.View
import java.text.FieldPosition

interface OnItemClickListener<T> {
    fun onItemClick(data: T, itemview: View, position: Int)
}