package com.example.noteapp.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.core.content.ContextCompat
import androidx.core.view.MenuProvider
import androidx.lifecycle.Lifecycle
import androidx.navigation.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.example.noteapp.MainActivity
import com.example.noteapp.R
import com.example.noteapp.adapter.NoteAdapter
import com.example.noteapp.databinding.FragmentHomeBinding
import com.example.noteapp.model.Note
import com.example.noteapp.viewmodel.NoteViewModel
import com.google.android.material.snackbar.Snackbar

class HomeFragment : Fragment(R.layout.fragment_home), SearchView.OnQueryTextListener,
    MenuProvider {
    private var homeBinding: FragmentHomeBinding? = null
    private val binding get() = homeBinding!!

    private lateinit var notesViewModel: NoteViewModel
    private lateinit var noteAdapter: NoteAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        homeBinding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val menuHost = requireActivity()
        menuHost.addMenuProvider(this, viewLifecycleOwner, Lifecycle.State.RESUMED)

        notesViewModel = (activity as MainActivity).noteViewModel

        setupHomeRecyclerView()

        binding.addNoteFab.setOnClickListener {
            it.findNavController().navigate(R.id.action_homeFragment_to_addNoteFragment)

        }
    }

    private fun updateUI(note: List<Note>?) {
        if (note != null) {
            if (note.isNotEmpty()) {
                binding.emptyNotesImage.visibility = View.GONE
                binding.homeRecyclerView.visibility = View.VISIBLE
            } else {
                binding.emptyNotesImage.visibility = View.VISIBLE
                binding.homeRecyclerView.visibility = View.GONE

            }
        }
    }

    private fun setupHomeRecyclerView() {
        noteAdapter = NoteAdapter()
        binding.homeRecyclerView.apply {
            layoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
            setHasFixedSize(true)
            adapter = noteAdapter
        }
        activity?.let {
            notesViewModel.getAllNotes()
                .observe(viewLifecycleOwner) { note: List<Note>? ->
                    try {
                        noteAdapter.differ.submitList(note)
                        updateUI(note)
                    } catch (e: Exception) {
                        Log.e("HomeFragment", "Error loading notes", e)
                        Toast.makeText(activity, "Sorry, can't load notes", Toast.LENGTH_SHORT)
                            .show()
                    }
                }
        }

        // Attach ItemTouchHelper for swipe-to-delete functionality
        val itemTouchHelper =
            ItemTouchHelper(object : ItemTouchHelper.SimpleCallback(2, ItemTouchHelper.LEFT) {
                override fun onMove(
                    recyclerView: RecyclerView,
                    viewHolder: RecyclerView.ViewHolder,
                    target: RecyclerView.ViewHolder
                ): Boolean {
                    return false // Disable drag-and-drop functionality
                }

                override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                    val position = viewHolder.adapterPosition
                    val deletedNote = noteAdapter.differ.currentList[position]

                    // Delete the note from the ViewModel
                    notesViewModel.deleteNote(deletedNote)

                    // Show a Snackbar with undo option
                    val snackbar = Snackbar.make(
                        binding.homeRecyclerView,
                        "Note deleted!",
                        Snackbar.LENGTH_LONG
                    )
                        .setAction("Undo") {
                            notesViewModel.addNote(deletedNote) // Add the note back if undone
                        }
                        .setActionTextColor(ContextCompat.getColor(requireContext(), R.color.white))
                    val snackbarView = snackbar.view
                    snackbarView.setBackgroundColor(
                        ContextCompat.getColor(
                            requireContext(),
                            R.color.pink
                        )
                    )
                    snackbar.show()


                    // Update the adapter with the remaining notes
                    noteAdapter.differ.submitList(noteAdapter.differ.currentList.filterNot { it.id == deletedNote.id })
                }

                override fun onSelectedChanged(
                    viewHolder: RecyclerView.ViewHolder?,
                    actionState: Int
                ) {
                    super.onSelectedChanged(viewHolder, actionState)
                }
            })
        itemTouchHelper.attachToRecyclerView(binding.homeRecyclerView)
    }


    private fun searchNote(query: String?) {
        val searchQuery = "%$query%"
        notesViewModel.searchNote(searchQuery).observe(this) { list ->
            noteAdapter.differ.submitList(list)
        }
        Log.d("trace", "searchNote: $query")
    }

    override fun onQueryTextSubmit(p0: String?): Boolean {
        return false
    }

    override fun onQueryTextChange(newtext: String?): Boolean {
        if (newtext != null)
            searchNote(newtext)
        return true
    }

    override fun onDestroy() {
        super.onDestroy()
        homeBinding = null
    }

    override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
        menu.clear()
        menuInflater.inflate(R.menu.home_menu, menu)
        val menuSearch = menu.findItem(R.id.searchMenu).actionView as SearchView
        menuSearch.isSubmitButtonEnabled = false
        menuSearch.setOnQueryTextListener(this)
    }

    override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
        return false
    }
}