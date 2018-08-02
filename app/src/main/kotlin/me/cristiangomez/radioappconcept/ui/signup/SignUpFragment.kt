package me.cristiangomez.radioappconcept.ui.signup

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.findNavController
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.sign_up_fragment.*
import me.cristiangomez.radioappconcept.ui.AuthActivity
import me.cristiangomez.radioappconcept.R
import me.cristiangomez.radioappconcept.util.TextInputLayoutErrorClearer
import me.cristiangomez.radioappconcept.util.validator.EmailValidator
import me.cristiangomez.radioappconcept.util.validator.LengthValidator
import me.cristiangomez.radioappconcept.util.validator.RequiredValidator
import me.cristiangomez.radioappconcept.util.validator.TextInputValidator


class SignUpFragment : Fragment() {

    companion object {
        fun newInstance() = SignUpFragment()
    }

    private lateinit var viewModel: SignUpViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.sign_up_fragment, container, false)
    }
    private fun canSignUp(): Boolean {
        val fields = mapOf(
                Pair(emailTextInputLayout, listOf(RequiredValidator(), EmailValidator())),
                Pair(passwordTextInputLayout, listOf(RequiredValidator(), LengthValidator(6))),
                Pair(displayNameTextInputLayout, listOf(RequiredValidator(), LengthValidator()))
        )
        return TextInputValidator(fields, requireContext()).validate()
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(SignUpViewModel::class.java)
        displayNameTextInputLayout.isErrorEnabled = true
        emailTextInputLayout.isErrorEnabled = true
        passwordTextInputLayout.isErrorEnabled = true
        displayNameTextInputLayout?.editText?.addTextChangedListener(TextInputLayoutErrorClearer(
                displayNameTextInputLayout
        ))
        emailTextInputLayout?.editText?.addTextChangedListener(
                TextInputLayoutErrorClearer(emailTextInputLayout)
        )
        passwordTextInputLayout?.editText?.addTextChangedListener(
                TextInputLayoutErrorClearer(passwordTextInputLayout)
        )
        alreadyHaveAccountText.setOnClickListener {
            view?.findNavController()?.popBackStack()
        }
        signUpButton.setOnClickListener {
            if (canSignUp()) {
                toggleFormState(true)
                val email = emailTextInputLayout!!.editText!!.text!!.toString()
                val password = passwordTextInputLayout!!.editText!!.text!!.toString()
                val displayName = displayNameTextInputLayout!!.editText!!.text.toString()
                viewModel.signUp(displayName, email, password, {
                    toggleFormState(false)
                    requireActivity().setResult(AuthActivity.AUTH_SUCCESS_RESULT_CODE)
                    requireActivity().finish()
                }, {
                    toggleFormState(false)
                    when (it) {
                        SignUpViewModel.SignUpError.UserCollision -> {
                            emailTextInputLayout.error = getString(R.string.error_email_user_collision)
                        }
                        SignUpViewModel.SignUpError.Error -> Snackbar.make(view!!,
                                R.string.sign_up_generic_error, Snackbar.LENGTH_SHORT).show()
                        SignUpViewModel.SignUpError.NetworkError -> Snackbar.make(view!!,
                                R.string.sign_up_network_error, Snackbar.LENGTH_SHORT).show()
                    }
                })
            }
        }
    }

    private fun toggleFormState(isLoading: Boolean) {
        progressBar.visibility = View.VISIBLE
        emailTextInputLayout.isEnabled = !isLoading
        passwordTextInputLayout.isEnabled = !isLoading
        signUpButton.isEnabled = !isLoading
        alreadyHaveAccountText.isEnabled = !isLoading
        displayNameTextInputLayout.isEnabled = !isLoading
        if (isLoading) {
            progressBar.visibility = View.VISIBLE
        } else {
            progressBar.visibility = View.INVISIBLE
        }
    }

}
