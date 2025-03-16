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

    /*
    Takes a Map from the ViewModel, converts it to a sorted list of pairs, and refreshes the UI.
     */
    fun setItems(items: Map<Int, List<Item>>) {
        /*
        Converts the Map to a set of Map.Entry<Int, List<Item>>.
         */
        groupedItems = items.entries
            // Sorts entries by listId (the key).
            .sortedBy { it.key }
            // Transforms each entry into a Pair<Int, List<Item>>.
            .map { it.key to it.value }
        /*
        Triggers a full UI refresh after updating groupedItems.
        Use DiffUtil for efficient updates instead:
        val diffResult = DiffUtil.calculateDiff(GroupDiffCallback(groupedItems, newItems))
        groupedItems = newItems
        diffResult.dispatchUpdatesTo(this)
         */
        notifyDataSetChanged()
    }

    // Standard RecyclerView method for view creation.
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GroupViewHolder {
        // Creates the view for a group (header + nested RecyclerView).
        val view = LayoutInflater.from(parent.context)
            /*
            Pass false for attachToRoot in RecyclerView adapters to let the system
            manage attachment.
             */
            .inflate(R.layout.item_group, parent, false)
        return GroupViewHolder(view)
    }

    // Standard RecyclerView method for data binding.
    override fun onBindViewHolder(holder: GroupViewHolder, position: Int) {
        val (listId, items) = groupedItems[position]
        // Updates the UI with the group’s data.
        holder.bind(listId, items)
    }

    // Tells the RecyclerView how many items to display.
    override fun getItemCount(): Int = groupedItems.size

    /*
    Defines an inner GroupViewHolder class extending RecyclerView.ViewHolder.
    inner ties it to the adapter, allowing access to outer class properties
    (though not used here).
    Use ViewHolder to cache view references and improve performance.
     */
    inner class GroupViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        // Cached in the ViewHolder to avoid repeated findViewById calls.
        private val tvListId: TextView = itemView.findViewById(R.id.tvListId)
        private val rvItems: RecyclerView = itemView.findViewById(R.id.rvItems)

        // Updates the header and nested RecyclerView with group data.
        fun bind(listId: Int, items: List<Item>) {
            // Use string resources for localization
            tvListId.text = "List ID: $listId"

            val itemAdapter = ItemAdapter()
            // Sets up layout manager, adapter, and decoration in one block.
            rvItems.apply {
                layoutManager = LinearLayoutManager(itemView.context)
                adapter = itemAdapter
                /*
                Adds vertical dividers between items.
                Add decorations in onCreateViewHolder to avoid re-adding on every bind:
                 */
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
