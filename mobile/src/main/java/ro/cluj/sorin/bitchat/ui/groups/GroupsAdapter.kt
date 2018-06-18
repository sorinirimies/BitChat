package ro.cluj.sorin.bitchat.ui.groups

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.listitem_group.view.contGroupItem
import kotlinx.android.synthetic.main.listitem_group.view.deleteGroup
import kotlinx.android.synthetic.main.listitem_group.view.tvGroupName
import ro.cluj.sorin.bitchat.R
import ro.cluj.sorin.bitchat.model.ChatGroup

internal typealias ChatGroupClicked = (ChatGroup) -> Unit
internal typealias ChatGroupDeletionSelected = (ChatGroup) -> Unit

class GroupsAdapter(private val chatGroupClicked: ChatGroupClicked,
  private val chatGroupDeletionSelected: ChatGroupDeletionSelected
) :
  RecyclerView.Adapter<GroupsAdapter.GroupViewHolder>() {
  private val groups = arrayListOf<ChatGroup>()

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = GroupViewHolder(
      LayoutInflater.from(parent.context).inflate(R.layout.listitem_group, parent, false))

  override fun getItemCount() = groups.size

  override fun onBindViewHolder(holder: GroupViewHolder, position: Int) {
    holder.bind(groups[position], chatGroupClicked, chatGroupDeletionSelected)
  }

  class GroupViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    fun bind(group: ChatGroup, chatGroupClicked: ChatGroupClicked, chatGroupDeletionSelected: ChatGroupDeletionSelected) {
      itemView.apply {
        tvGroupName.text = group.name
        contGroupItem.setOnClickListener { chatGroupClicked.invoke(group) }
        deleteGroup.setOnClickListener { chatGroupDeletionSelected.invoke(group) }
      }
    }
  }

  fun addItem(item: ChatGroup) {
    groups.add(item)
    notifyItemInserted(groups.indexOf(item))
  }

  fun addItems(items: List<ChatGroup>) {
    groups.clear()
    groups.addAll(items)
    notifyDataSetChanged()
  }

  fun clearItems() {
    groups.clear()
    notifyDataSetChanged()
  }

  fun removeItem(item: ChatGroup) {
    val position = groups.indexOf(item)
    groups.removeAt(position)
    notifyItemRemoved(position)
  }
}