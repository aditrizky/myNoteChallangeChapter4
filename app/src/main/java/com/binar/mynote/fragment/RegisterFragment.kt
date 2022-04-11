package com.binar.mynote.fragment

import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.binar.mynote.data.User
import com.binar.mynote.databinding.FragmentRegisterBinding
import com.binar.mynote.room.AplicationDB
import java.util.concurrent.Executors

class RegisterFragment : Fragment() {
    private var _binding: FragmentRegisterBinding? = null
    private val binding: FragmentRegisterBinding get() = _binding!!
    var mDB: AplicationDB? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentRegisterBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mDB = AplicationDB.getInstance(requireActivity())
        var uname = "null"
        Executors.newSingleThreadExecutor()
        val executor = Executors.newSingleThreadExecutor()




        binding.registerButton.setOnClickListener {
            val userName = binding.usernameEditTextText.text.toString()
            val confirmPassword = binding.confirmPasswordEditText.text.toString()
            val objectUser = User(
                name = binding.nameEditTextText.text.toString(),
                username = userName,
                password = binding.passwordEditText.text.toString()
            )
            executor.execute {
                val user = mDB?.userDao()?.getUsername(userName)
                activity?.runOnUiThread {
                    user?.forEach {

                        uname= it.username.toString()
                    }
                    Log.d("reg,", user.toString())
                    Log.d("reg,", uname.toString())

                    if (TextUtils.isEmpty(objectUser.name) || TextUtils.isEmpty(objectUser.username) || TextUtils.isEmpty(
                            objectUser.password
                        )
                    ) {
                        Toast.makeText(
                            requireContext(),
                            "fields cooldn't empty",
                            Toast.LENGTH_SHORT
                        ).show()
                    } else {
                        if (objectUser.username == uname.toString()) {
                            Toast.makeText(
                                activity,
                                "$uname is not available",
                                Toast.LENGTH_SHORT
                            ).show()
                            uname="null"
                        } else {
                            if (objectUser.password != confirmPassword) {
                                Toast.makeText(
                                    activity,
                                    "Confirm Password Error,it's Diffrent with Password",
                                    Toast.LENGTH_SHORT
                                ).show()
                            } else {
                                executor.execute {
                                    mDB?.userDao()?.addUser(objectUser)

                                }
                                if (objectUser.username != null) {
                                    Toast.makeText(
                                        activity,
                                        "Success add user ${objectUser.username}",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    findNavController().navigate(RegisterFragmentDirections.actionRegisterFragmentToLoginFragment2())

                                } else {
                                    Toast.makeText(
                                        requireActivity(),
                                        "Failed add user ${objectUser.username}",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            }
                            onDestroyView()
                        }
                    }
                }

            }
        }
    }

}