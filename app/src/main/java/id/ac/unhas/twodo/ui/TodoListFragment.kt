package id.ac.unhas.twodo.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import id.ac.unhas.twodo.R
import id.ac.unhas.twodo.model.Todo
import id.ac.unhas.twodo.ui.dialog.EditDialogFragment
import kotlinx.android.synthetic.main.fragment_todo_list.*

class TodoListFragment : Fragment() {
    companion object {
        lateinit var viewModel: TodoViewModel
    }

    private lateinit var adapter: TodoAdapter
    private lateinit var viewModelFactory: TodoViewModelFactory
    private lateinit var linearLayoutManager: LinearLayoutManager

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewModelFactory = TodoViewModelFactory(TodoRepository())
        viewModel = ViewModelProvider(this, viewModelFactory).get(TodoViewModel::class.java)

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_todo_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.getData().observe(viewLifecycleOwner, Observer {
            adapter = TodoAdapter(ArrayList(it))
            rv_todo.adapter = adapter

            adapter.setOnItemClickCallback(object : TodoAdapter.OnItemClickCallback {
                override fun onItemClicked(data: Todo) {
                    showDialog(data)
                }
            })
        })

        linearLayoutManager = LinearLayoutManager(context)
        rv_todo.layoutManager = linearLayoutManager
    }

    fun showDialog(data: Todo) {
        val items = arrayOf("Edit", "Delete")
        MaterialAlertDialogBuilder(context)
            .setItems(items) { dialog, which ->
                when (items[which]) {
                    "Edit" -> showForm(data)
                    "Delete" -> showDelete(data)
                    else -> Unit
                }
            }
            .show()
    }

    private fun showForm(data: Todo?) {
        val fragmentManager = childFragmentManager
        val newFragment = EditDialogFragment(data, view)
        newFragment.show(fragmentManager, "showForm")
    }

    private fun showDelete(data: Todo) {
        MaterialAlertDialogBuilder(context)
            .setTitle(resources.getString(R.string.delete_alert_title))
            .setMessage(resources.getString(R.string.delete_alert_msg))
            .setNegativeButton(resources.getString(R.string.decline)) { dialog, which ->
                dialog.dismiss()
            }
            .setPositiveButton(resources.getString(R.string.accept)) { dialog, which ->
                viewModel.deleteData(data, requireView())
            }
            .show()
    }
}
