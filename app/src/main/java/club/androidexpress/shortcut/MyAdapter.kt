package club.androidexpress.shortcut

import android.content.pm.ShortcutInfo
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class MyAdapter(private val items: List<ShortcutInfo>) : RecyclerView.Adapter<MyAdapter.MyHolder>() {
   inner class MyHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
       fun bind(shortcutInfo: ShortcutInfo) {
           itemView.findViewById<TextView>(android.R.id.text1).text = shortcutInfo.shortLabel
       }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyHolder =
        MyHolder(LayoutInflater.from(parent.context).inflate(android.R.layout.simple_list_item_1, parent, false))

    override fun onBindViewHolder(holder: MyHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int = items.size
}