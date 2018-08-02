package me.cristiangomez.radioappconcept.ui.signin

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.findNavController
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.sign_in_fragment.*
import me.cristiangomez.radioappconcept.ui.AuthActivity
import me.cristiangomez.radioappconcept.R
import me.cristiangomez.radioappconcept.util.TextInputLayoutErrorClearer
import me.cristiangomez.radioappconcept.util.validator.EmailValidator
import me.cristiangomez.radioappconcept.util.validator.LengthValidator
import me.cristiangomez.radioappconcept.util.validator.RequiredValidator
import me.cristiangomez.radioappconcept.util.validator.TextInputValidator


class SignInFragment : Fragment() {
    companion object {
        fun newInstance() = SignInFragment()
    }

    private lateinit var viewModel: SignInViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.sign_in_fragment, container, false)
    }


    private fun canTrySignInValid(): Boolean {
        return TextInputValidator(mapOf(
                Pair(emailTextInputLayout, listOf(RequiredValidator(), EmailValidator())),
                Pair(passwordTextInputLayout, listOf(RequiredValidator(),
                        LengthValidator(6)))
        ), requireContext()).validate()
    }

    private fun toggleFormState(isLoading: Boolean) {
        progressBar.visibility = View.VISIBLE
        emailTextInputLayout.isEnabled = !isLoading
        passwordTextInputLayout.isEnabled = !isLoading
        signInButton.isEnabled = !isLoading
        signInWithGoogleButton.isEnabled = !isLoading
        signInWithFacebookButton.isEnabled = !isLoading
        dontHaveAccountText.isEnabled = !isLoading
        if (isLoading) {
            progressBar.visibility = View.VISIBLE
        } else {
            progressBar.visibility = View.INVISIBLE
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(SignInViewModel::class.java)
        signInButton.setOnClickListener {
            if (canTrySignInValid()) {
                toggleFormState(true)
                viewModel.signIn(emailTextInputLayout.editText!!.text.toString(),
                        passwordTextInputLayout.editText!!.text.toString(), {
                    toggleFormState(false)
                    requireActivity().setResult(AuthActivity.AUTH_SUCCESS_RESULT_CODE)
                    requireActivity().finish()
                }, {
                    toggleFormState(false)
                    when (it) {
                        SignInViewModel.SignInError.NETWORK_EXCEPTION -> {
                            toggleFormState(false)
                            Snackbar.make(view!!, R.string.sign_in_network_error,
                                    Snackbar.LENGTH_SHORT).show()
                        }
                        SignInViewModel.SignInError.INVALID_CREDENTIALS -> emailTextInputLayout.error = getString(R.string.error_invalid_credentials)
                        else -> Snackbar.make(view!!, R.string.sign_in_generic_error,
                                Snackbar.LENGTH_SHORT).show()
                    }
                })
            }
        }
        signInWithFacebookButton.setOnClickListener {
            toggleFormState(true)
            viewModel.signInWithFacebook(this, {
                toggleFormState(false)
                requireActivity().setResult(AuthActivity.AUTH_SUCCESS_RESULT_CODE)
                requireActivity().finish()
            }, {
                toggleFormState(false)
                when (it) {
                    SignInViewModel.SignInError.NETWORK_EXCEPTION -> {
                        toggleFormState(false)
                        Snackbar.make(view!!, R.string.sign_in_network_error,
                                Snackbar.LENGTH_SHORT).show()
                    }
                    SignInViewModel.SignInError.INVALID_CREDENTIALS -> emailTextInputLayout.error = getString(R.string.error_invalid_credentials)
                    else -> Snackbar.make(view!!, R.string.sign_in_generic_error,
                            Snackbar.LENGTH_SHORT).show()
                }
            }, {
                toggleFormState(false)
            })
        }
        signInWithGoogleButton.setOnClickListener {
            toggleFormState(true)
            viewModel.signInWithGoogle(this, {
                toggleFormState(false)
                requireActivity().setResult(AuthActivity.AUTH_SUCCESS_RESULT_CODE)
                requireActivity().finish()
            }, {
                toggleFormState(false)
                when (it) {
                    SignInViewModel.SignInError.NETWORK_EXCEPTION -> {
                        toggleFormState(false)
                        Snackbar.make(view!!, R.string.sign_in_network_error,
                                Snackbar.LENGTH_SHORT).show()
                    }
                    SignInViewModel.SignInError.INVALID_CREDENTIALS -> emailTextInputLayout.error = getString(R.string.error_invalid_credentials)
                    else -> Snackbar.make(view!!, R.string.sign_in_generic_error,
                            Snackbar.LENGTH_SHORT).show()
                }
            }, {
                toggleFormState(false)
            })
        }
        dontHaveAccountText.setOnClickListener {
            view?.findNavController()?.navigate(R.id.signUpFragment)
        }
        emailTextInputLayout.isErrorEnabled = true
        passwordTextInputLayout.isErrorEnabled = true
        emailTextInputLayout?.editText?.addTextChangedListener(TextInputLayoutErrorClearer(emailTextInputLayout))
        passwordTextInputLayout?.editText?.addTextChangedListener(TextInputLayoutErrorClearer(passwordTextInputLayout))
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == SignInViewModel.ACTIVITY_RESULT_GOOGLE_SIGN_IN) {
            viewModel.onGoogleResult(requestCode, data)
        } else {
            viewModel.onFacebookResult(requestCode, resultCode, data)
        }
    }
}
