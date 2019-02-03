package ro.cluj.sorin.bitchat.ui.groups

import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import android.widget.EditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.sorinirimies.kotlinx.*
import kotlinx.android.synthetic.main.fragment_groups.*
import org.koin.android.ext.android.inject
import ro.cluj.sorin.bitchat.R
import ro.cluj.sorin.bitchat.model.ChatGroup
import ro.cluj.sorin.bitchat.ui.BaseFragment
import ro.cluj.sorin.bitchat.ui.chat.ChatActivity
import ro.cluj.sorin.bitchat.ui.chat.PARAM_CHAT_GROUP
import ro.cluj.sorin.bitchat.ui.nearby.NEARBY_SERVICE_ID
import ro.cluj.sorin.bitchat.ui.nearby.PREF_IS_NEARBY_ENABLED
import java.util.*

const val DEFAULT_GROUP_ID = "ro.cluj.sorin.bitchat.123ertg"

class GroupsFragment : BaseFragment(), GroupsView {

    private val presenter: GroupsPresenter by inject()
    private val groupsRef by lazy { db.collection("group") }
    private lateinit var groupsAdapter: GroupsAdapter
    override fun getLayoutId() = R.layout.fragment_groups

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        presenter.attachView(this)
        fabCreateGroup.setOnClickListener {
            showCreateGroupDialog()
        }
        rvGroups.apply {
            layoutManager = LinearLayoutManager(activity)
            groupsAdapter = GroupsAdapter(object : GroupActionsListener {
                override fun selectGroup(group: ChatGroup) {
                    startActivity(Intent(activity, ChatActivity::class.java).apply {
                        putExtra(PARAM_CHAT_GROUP, group)
                    })
                }

                override fun deleteGroup(group: ChatGroup) {
                    showDeleteGroupDialog(group)
                }

                override fun editGroup(group: ChatGroup) {
                    showEditGroupDialog(group)
                }
            })
            adapter = groupsAdapter
        }
        firebaseAuth.addAuthStateListener(authStateListener)

        groupsAdapter.clearItems()
        groupsAdapter.addItem(ChatGroup(DEFAULT_GROUP_ID, getString(R.string.group_cryptonarii)))
    }

    fun enableNearbyChatGroup(isEnabled: Boolean) {
        if (isEnabled) {
            groupsAdapter.clearItems()
            groupsAdapter.addItem(ChatGroup(NEARBY_SERVICE_ID, getString(R.string.nearby_chat_group)))
            fabCreateGroup.animateGone()
        } else {
            addGroupsDbChangeListener()
            fabCreateGroup.animateVisible()
        }
    }

    private fun addGroupsDbChangeListener() {
        groupsRef.addSnapshotListener { querySnapshot, firebaseFirestoreException ->
            if (firebaseFirestoreException != null) return@addSnapshotListener
            groupsAdapter.clearItems()
            querySnapshot?.forEach { message ->
                groupsAdapter.addItem(ChatGroup(message.data["id"].toString(), message.data["name"].toString()))
            }
        }
    }

    override fun showUserIsLoggedIn(user: FirebaseUser) {
        enableNearbyChatGroup(sharedPrefs.getBoolean(PREF_IS_NEARBY_ENABLED, false))
    }

    override fun showUserIsLoggedOut() {
        groupsAdapter.clearItems()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        firebaseAuth.removeAuthStateListener(authStateListener)
        presenter.detachView()
    }

    private val authStateListener = FirebaseAuth.AuthStateListener { firebaseAuth ->
        val user = firebaseAuth.currentUser
        if (user != null) {
            presenter.userIsLoggedIn(user)
        } else {
            presenter.userIsLoggedOut()
        }
    }

    private fun showCreateGroupDialog() = alertDialog(requireContext()) {
        val v = layoutInflater.init(R.layout.dialog_create_group)
        setView(v)
        setTitle(R.string.create_group_label)
        setMessage("${getString(R.string.create_group_message)} ${v.findViewById<EditText>(R.id.edtGroupName).text}")
        setPositiveButton(R.string.create_group) { _, _ ->
            val groupName = v.findViewById<EditText>(R.id.edtGroupName).text.toString()
            val group = ChatGroup(UUID.randomUUID().toString(), groupName)
            presenter.createChatGroup(group)
        }
        setNegativeButton(R.string.cancel) { _, _ -> }
    }

    private fun showEditGroupDialog(group: ChatGroup) = alertDialog(requireContext()) {
        val v = layoutInflater.init(R.layout.dialog_create_group)
        setView(v)
        setTitle(R.string.edit_group_label)
        setMessage(getString(R.string.edit_group_message))
        setPositiveButton(R.string.edit_group) { _, _ ->
            val groupName = v.findViewById<EditText>(R.id.edtGroupName).text.toString()
            group.name = groupName
            presenter.editChatGroup(group)
        }
        setNegativeButton(R.string.cancel) { _, _ -> }
    }

    private fun showDeleteGroupDialog(group: ChatGroup) = alertDialog(requireContext()) {
        setTitle(R.string.delete_group_label)
        setMessage("${getString(R.string.delete_group_label)} ${group.name} ?")
        setPositiveButton(R.string.delete_group) { _, _ ->
            presenter.deleteChatGroup(group)
        }
        setNegativeButton(R.string.cancel) { _, _ -> }
    }


    override fun showChatGroupCreated(group: ChatGroup) {
        snack(contGroupsFragment, "${group.name} ${getString(R.string.group_created_confirmation)}")
    }

    override fun showChatGroupDeleted(group: ChatGroup) {
        snack(contGroupsFragment, "${group.name} ${getString(R.string.group_deleted_confirmation)}")
    }

    override fun showChatGroupEdited(group: ChatGroup) {
        snack(contGroupsFragment, "${group.name} ${getString(R.string.group_edit_confirmation)}")
    }
}
