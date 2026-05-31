package com.example.kpudata

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView

data class DashboardMenu(val iconResId: Int, val title: String)

class MenuAdapter(context: Context, private val menuItems: List<DashboardMenu>) :
    ArrayAdapter<DashboardMenu>(context, 0, menuItems) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var view = convertView
        if (view == null) {
            view = LayoutInflater.from(context).inflate(R.layout.item_menu_list, parent, false)
        }

        val currentItem = menuItems[position]

        val iconView = view?.findViewById<ImageView>(R.id.menuIcon)
        val titleView = view?.findViewById<TextView>(R.id.menuTitle)

        iconView?.setImageResource(currentItem.iconResId)
        titleView?.text = currentItem.title

        return view!!
    }
}
