package com.example.hiringdataapp.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.hiringdataapp.R
import com.example.hiringdataapp.model.Item

class GroupItemAdapter : RecyclerView.Adapter<GroupItemAdapter.GroupViewHolder>() {

    private var groupedItems: List<Pair<Int, List<Item>>> = emptyList()

    fun setItems(items: Map<Int, List<Item>>) {
        groupedItems = items.entries.sortedBy { it.key }.map { it.key to it.value }
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GroupViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_group, parent, false)
        return GroupViewHolder(view)
    }

    override fun onBindViewHolder(holder: GroupViewHolder, position: Int) {
        val (listId, items) = groupedItems[position]
        holder.bind(listId, items)
    }

    override fun getItemCount(): Int = groupedItems.size

    inner class GroupViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvListId: TextView = itemView.findViewById(R.id.tvListId)
        private val rvItems: RecyclerView = itemView.findViewById(R.id.rvItems)

        fun bind(listId: Int, items: List<Item>) {
            tvListId.text = "List ID: $listId"

            val itemAdapter = ItemAdapter()
            rvItems.apply {
                layoutManager = LinearLayoutManager(itemView.context)
                adapter = itemAdapter
                addItemDecoration(
                    DividerItemDecoration(
                        itemView.context,
                        DividerItemDecoration.VERTICAL
                    )
                )
            }
            itemAdapter.setItems(items)
        }
    }
}
