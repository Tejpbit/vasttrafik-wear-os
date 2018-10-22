package com.example.tejp.basttrafik

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Adapter
import android.widget.ArrayAdapter
import android.widget.BaseAdapter
import android.widget.TextView

class ListViewAdapter(private val context: Context,
                      val objects: List<Departure>) : BaseAdapter() {
    private val inflater: LayoutInflater
            = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val obj = objects[position]

        val rowView = inflater.inflate(R.layout.departure_list_item, parent, false)

        // Get title element
        val titleTextView = rowView.findViewById(R.id.departure_list_row_title) as TextView
        val directionTextView = rowView.findViewById(R.id.departure_list_row_direction) as TextView


        titleTextView.text = obj.name
        directionTextView.text = obj.towards

        return rowView
    }

    override fun getItem(position: Int): Any {
        return objects[position]
    }

    override fun getItemId(position: Int): Long {
        return objects[position].id
    }

    override fun getCount(): Int {
        return objects.size
    }


}