package com.binar.mynote


import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.binar.mynote.data.Note
import com.binar.mynote.databinding.ItemNoteBinding



class NoteAdapter(private val note: List<Note>,
private val delete : (Note)-> Unit,
private val edit : (Note)->Unit
) :
    RecyclerView.Adapter<NoteAdapter.NoteViewHolder>() {
    class NoteViewHolder(val binding: ItemNoteBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoteViewHolder {
        return NoteViewHolder(
            ItemNoteBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun onBindViewHolder(holder: NoteViewHolder, position: Int) {
        val notes = note[position]
        holder.binding.titleTextView.text = notes.title
        holder.binding.noteTextView.text = notes.note

        holder.binding.deleteImageButton.setOnClickListener {
            delete.invoke(note[position])
        }
        holder.binding.editImageButton.setOnClickListener {
            edit.invoke(note[position])
        }
    }
        override fun getItemCount(): Int {
            return note.size
        }

    }
