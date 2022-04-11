package com.binar.mynote.fragment

import android.app.AlertDialog
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.os.Handler
import android.text.TextUtils
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.binar.mynote.NoteAdapter
import com.binar.mynote.data.Note
import com.binar.mynote.databinding.CustomEditDialogBinding
import com.binar.mynote.databinding.DeleteDialogBinding
import com.binar.mynote.databinding.FragmentHomeBinding
import com.binar.mynote.databinding.InsertDialogBinding
import com.binar.mynote.room.AplicationDB
import com.google.android.material.snackbar.Snackbar
import java.util.concurrent.Executors


class HomeFragment : Fragment() {
    private val sharedPreffile = "kotlinsharedpreferance"
    private var mDB: AplicationDB? = null
    private var _binding: FragmentHomeBinding? = null
    private val binding: FragmentHomeBinding get() = _binding!!
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val sharedPreferences: SharedPreferences = requireActivity().getSharedPreferences(
            sharedPreffile,
            Context.MODE_PRIVATE
        )
        val editor: SharedPreferences.Editor = sharedPreferences.edit()
        mDB = AplicationDB.getInstance(requireActivity())
        val usernameValue = sharedPreferences.getString("username_key", "null")

        Log.d("home", usernameValue.toString())
        if (usernameValue == "null") {
            editor.clear()
            editor.apply()
            findNavController().navigate(HomeFragmentDirections.actionHomeFragmentToLoginFragment2())
        } else {
            fetchData()
            val layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
            binding.recycleview.layoutManager = layoutManager
            binding.usernameTextView.text = usernameValue

            binding.addFloatingActionButton.setOnClickListener {
                addData()
            }


            binding.logoutTextView.setOnClickListener {
                AlertDialog.Builder(it.context).setPositiveButton("Yes") { p0, p1 ->
                    editor.clear()
                    editor.apply()
                    findNavController().navigate(HomeFragmentDirections.actionHomeFragmentToLoginFragment2())
                }
                    .setNegativeButton(
                        "No"
                    ) { p0, p1 ->
                        p0.dismiss()
                    }
                    .setMessage("Logout From $usernameValue ?").setTitle("Confirm Logout")
                    .create().show()

            }
        }
    }


    override fun onDestroy() {
        super.onDestroy()
        AplicationDB.destroyInstance()
        _binding = null
    }

    fun fetchData() {
        mDB = AplicationDB.getInstance(requireActivity())
        Log.d("fetch", "berjalan")
        Executors.newSingleThreadExecutor()
        val executor = Executors.newSingleThreadExecutor()
        executor.execute {
            val listNote = mDB?.noteDao()?.getNote()
            Log.d("size", listNote?.size.toString())
            activity?.runOnUiThread {
                if (listNote?.size==0){
                    binding.textView3.visibility = View.VISIBLE
                }else{
                    binding.textView3.visibility = View.GONE
                }
                binding.recycleview.adapter = listNote?.let {
                    NoteAdapter(it,
                        delete = { data ->
                            val mbinding = DeleteDialogBinding.inflate(
                                LayoutInflater.from(activity),
                                null,
                                false
                            )
                            val view = mbinding.root
                            val dialogBuilder = AlertDialog.Builder(activity)
                            dialogBuilder.setView(view)
                            val dialog = dialogBuilder.create()
                            mbinding.deleteTextView.text= "Delete Note ${data.title}"
                            mbinding.deleteButton.setOnClickListener {
                                val launch = Runnable {
                                    Handler().postDelayed({
                                        executor.execute {
                                            val result = mDB?.noteDao()?.deleteNote(data)
                                            activity?.runOnUiThread {
                                                if (result != 0) {
                                                    Toast.makeText(
                                                        activity,
                                                        "success delete note",
                                                        Toast.LENGTH_SHORT
                                                    ).show()
                                                    fetchData()
                                                } else {
                                                    Toast.makeText(
                                                        activity,
                                                        "fail delete note",
                                                        Toast.LENGTH_SHORT
                                                    ).show()
                                                }
                                            }
                                        }

                                    }, 4000)
                                }
                                activity?.runOnUiThread(launch)
                                val snackbar = Snackbar.make(
                                    binding.root,
                                    "Note deleted",
                                    Snackbar.LENGTH_LONG
                                )
                                snackbar.setAction("Undo") {
                                    /*   it.handler.removeCallbacksAndMessages(launch)*/
                                    snackbar.dismiss()
                                }
                                snackbar.show()
                                dialog.dismiss()
                                Log.d("sn", "snackbar")
                            }
                            mbinding.cencelButton.setOnClickListener {
                                dialog.dismiss()
                            }
                                dialog.show()
                        },
                        edit = { data ->
                            val binding = CustomEditDialogBinding.inflate(
                                LayoutInflater.from(activity),
                                null,
                                false
                            )
                            val view = binding.root
                            val dialogBuilder = AlertDialog.Builder(activity)
                            dialogBuilder.setView(view)
                            val dialog = dialogBuilder.create()
                            binding.titleEditTextText.setText(data.title)
                            binding.noteEditTextText.setText(data.note)

                            binding.editButton.setOnClickListener {
                                data.title = binding.titleEditTextText.text.toString()
                                data.note = binding.noteEditTextText.text.toString()
                                if (TextUtils.isEmpty(data.title) || TextUtils.isEmpty(data.note)) {
                                    Toast.makeText(
                                        activity,
                                        "fields could not empty",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                                executor.execute {
                                  val result=  mDB?.noteDao()?.updateNote(data)
                                    activity?.runOnUiThread {
                                        if(result!=0){
                                            Toast.makeText(
                                                activity,
                                                "Update Note Success",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                            fetchData()
                                        }else{
                                            Toast.makeText(
                                                activity,
                                                "Update Note Failed",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        }
                                    }
                                }
                                dialog.dismiss()
                            }
                            dialog.show()
                        }
                    )
                }
            }
        }

    }

    fun addData() {
        mDB = AplicationDB.getInstance(requireActivity())
        val binding = InsertDialogBinding.inflate(LayoutInflater.from(activity), null, false)
        val view = binding.root
        val dialogBuilder = AlertDialog.Builder(activity)
        dialogBuilder.setView(view)
        val dialog = dialogBuilder.create()

        binding.insertButton.setOnClickListener {

            Executors.newSingleThreadExecutor()
            val executor = Executors.newSingleThreadExecutor()
            val objectNote = Note(

                title = binding.titleEditTextText.text.toString(),
                note = binding.noteEditTextText.text.toString()
            )
            if (TextUtils.isEmpty(objectNote.title) || TextUtils.isEmpty(objectNote.note)) {
                Toast.makeText(
                    requireContext(),
                    "fields couldn't empty",
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                executor.execute {
                    val result = mDB?.noteDao()?.addNote(objectNote)
                    Log.d("test", result.toString())
                    activity?.runOnUiThread {
                        if (objectNote.title != null) {
                            Toast.makeText(
                                activity, "Success add ${objectNote.title}",
                                Toast.LENGTH_LONG
                            ).show()
                            fetchData()
                        } else {
                            Toast.makeText(
                                activity, "Failed add ${objectNote.title}",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    }

                }
                dialog.dismiss()
            }


        }
        dialog.show()
    }


}