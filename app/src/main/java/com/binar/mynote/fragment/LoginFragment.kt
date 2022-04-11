package com.binar.mynote.fragment

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.binar.mynote.databinding.FragmentLoginBinding
import com.binar.mynote.room.AplicationDB
import java.util.concurrent.Executors


class LoginFragment : Fragment() {
    private val sharedPreffile = "kotlinsharedpreferance"
    private var _binding: FragmentLoginBinding? = null
    private val binding: FragmentLoginBinding get() = _binding!!
    private var mDB: AplicationDB? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mDB = AplicationDB.getInstance(requireActivity())
        val sharedPreferences: SharedPreferences = requireActivity().getSharedPreferences(
            sharedPreffile,
            Context.MODE_PRIVATE
        )
        val editor: SharedPreferences.Editor = sharedPreferences.edit()

        binding.createTextView.setOnClickListener {
            findNavController().navigate(LoginFragmentDirections.actionLoginFragment2ToRegisterFragment())

        }

        binding.loginButton.setOnClickListener {
            val username = binding.usernameEditTextText.text.toString()
            val password = binding.passwordEditText.text.toString()
            if (TextUtils.isEmpty(username) || TextUtils.isEmpty(password)) {
                Toast.makeText(
                    requireContext(),
                    "Username or password cooldn't empty",
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                Executors.newSingleThreadExecutor()
                val executor = Executors.newSingleThreadExecutor()
                val uname = StringBuffer()
                val pass = StringBuffer()
                executor.execute {
                    val user = mDB?.userDao()?.getUsername(username)
                    activity?.runOnUiThread {
                        user?.forEach {
                            uname.append(
                                it.username
                            )
                            pass.append(
                                it.password
                            )
                        }
                        Log.d("login", uname.toString())
                        Log.d("login", pass.toString())
                        if (username == uname.toString() && password == pass.toString()) {
                            findNavController().navigate(
                                LoginFragmentDirections.actionLoginFragment2ToHomeFragment(
                                )
                            )
                            editor.putString("username_key", username)
                            editor.apply()
                        } else if (username == uname.toString() && password != pass.toString()) {
                            Toast.makeText(requireContext(), "Wrong password", Toast.LENGTH_SHORT)
                                .show()
                        } else {
                            Toast.makeText(requireContext(), "User Not Found ", Toast.LENGTH_SHORT)
                                .show()
                        }
                    }
                }
            }
        }

    }

}



