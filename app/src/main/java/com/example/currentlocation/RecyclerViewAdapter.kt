package com.example.currentlocation

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView

class RecyclerViewAdapter(private val dataSet: ArrayList<ModelClass>) :
    RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder>() {

        /**
         * Provide a reference to the type of views that you are using
         * (custom ViewHolder).
         */
        class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            lateinit var username: TextView

            init {
                username = view.findViewById(R.id.userNameId)
            }
        }

        // Create new views (invoked by the layout manager)
        override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
            // Create a new view, which defines the UI of the list item
            val view = LayoutInflater.from(viewGroup.context)
                .inflate(R.layout.recycler_item, viewGroup, false)

            return ViewHolder(view)
        }

        // Replace the contents of a view (invoked by the layout manager)
        override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {

            // Get element from your dataset at this position and replace the
            // contents of the view with that element
            viewHolder.username.text = dataSet[position].name
           var phone = dataSet[position].phone

            // Click on the cardView to move to another Activity
          viewHolder.itemView.setOnClickListener {view: View ->
              var intent= Intent(viewHolder.itemView.context,MapsActivity::class.java)
              intent.putExtra("UserNumber",phone)


          viewHolder.itemView.context.startActivity(intent)


}
        }

        // Return the size of your dataset (invoked by the layout manager)
        override fun getItemCount() = dataSet.size

    }