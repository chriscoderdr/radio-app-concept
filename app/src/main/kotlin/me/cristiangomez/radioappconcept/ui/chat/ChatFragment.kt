package me.cristiangomez.radioappconcept.ui.chat

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.chat_fragment.*
import me.cristiangomez.radioappconcept.ui.AuthActivity
import me.cristiangomez.radioappconcept.R
import me.cristiangomez.radioappconcept.data.model.ChatMessage
import me.cristiangomez.radioappconcept.data.model.User


class ChatFragment : Fragment() {

    companion object {
        const val AUTH_REQUEST_CODE = 2590
        fun newInstance() = ChatFragment()
    }

    private lateinit var viewModel: ChatViewModel
    private lateinit var auth: FirebaseAuth

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.chat_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(ChatViewModel::class.java)

        auth = FirebaseAuth.getInstance()

        val liveData = viewModel.chatMessagesLiveData
        rv_chat_messages.adapter = ChatMessagesAdapter(emptyList())
        rv_chat_messages.layoutManager = LinearLayoutManager(requireContext())
        liveData.observe(this, Observer { it ->
            rv_chat_messages.adapter = ChatMessagesAdapter(it)
            rv_chat_messages.layoutManager?.scrollToPosition(it.size - 1)
        })
        chat_text_input?.editText?.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {
                if (chat_text_input.isErrorEnabled) {
                    chat_text_input.isErrorEnabled = false
                    chat_text_input.error = null
                }
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }
        })
        chat_input_send.setOnClickListener {
            val messageText = chat_text_input.editText?.editableText.toString()
            if (!messageText.isBlank()) {
                chat_text_input?.editText?.isEnabled = false
                chat_input_send?.isEnabled = false
                viewModel.sendMessage(ChatMessage(messageText,
                        User(auth.currentUser!!.uid,
                                auth.currentUser!!.displayName,
                                auth.currentUser!!.email)), {
                    chat_text_input?.editText?.text = null
                    chat_text_input?.editText?.isEnabled = true
                    chat_input_send?.isEnabled = true
                }, {
                    chat_input_send?.isEnabled = true
                    chat_text_input?.editText?.isEnabled = true
                    chat_text_input?.isErrorEnabled = true
                    chat_text_input?.error = "Error Sending"
                })
            }
        }
    }

    override fun onResume() {
        super.onResume()
        if (FirebaseAuth.getInstance().currentUser == null) {
            startActivityForResult(Intent(requireActivity(), AuthActivity::class.java), AUTH_REQUEST_CODE)
        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == AUTH_REQUEST_CODE && resultCode != AuthActivity.AUTH_SUCCESS_RESULT_CODE) {
            view?.findNavController()?.popBackStack()
        }
    }

}
