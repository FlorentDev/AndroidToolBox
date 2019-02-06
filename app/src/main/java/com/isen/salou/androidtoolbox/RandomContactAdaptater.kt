package com.isen.salou.androidtoolbox

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.squareup.picasso.Picasso

class RandomContactAdaptater(val contactList: Contact) : RecyclerView.Adapter<RandomContactAdaptater.ViewHolder>() {
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val name = contactList.results[position].name.first + " " + contactList.results[position].name.last
        holder.fName.text = name
        holder.fCell.text = contactList.results[position].cell
        holder.fMail.text = contactList.results[position].email
        Picasso.get().load(contactList.results[position].picture.large).into(holder.fImage)
    }

    override fun onCreateViewHolder(parent: ViewGroup, p1: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.model_contact, parent, false)
        return ViewHolder(v)
    }

    override fun getItemCount(): Int {
        return contactList.results.size
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val fName = itemView.findViewById<TextView>(R.id.name)
        val fCell = itemView.findViewById<TextView>(R.id.cell)
        val fMail = itemView.findViewById<TextView>(R.id.mail)
        val fImage = itemView.findViewById<ImageView>(R.id.snap)

    }

}