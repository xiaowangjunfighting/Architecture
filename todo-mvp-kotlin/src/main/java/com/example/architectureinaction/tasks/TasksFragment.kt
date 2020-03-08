package com.example.architectureinaction.tasks

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.architectureinaction.OnItemClickListener
import com.example.architectureinaction.R
import com.example.architectureinaction.data.Task

class TasksFragment : Fragment(), TasksContract.View {
    override lateinit var presenter: TasksContract.Presenter

    private lateinit var tvAddTodo: TextView
    private lateinit var listener: OnItemClickListener<Task>

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_tasks, container)
        tvAddTodo = root.findViewById<TextView>(R.id.tv_add_todo)
        root.findViewById<RecyclerView>(R.id.recyclerView)
            .apply {
                layoutManager = LinearLayoutManager(context)
                adapter = MyAdapter(arrayListOf())
                setOnItemClickListener(object : OnItemClickListener<Task> {
                    override fun onItemClick(data: Task, view: View, position: Int) {
                        if (view.id == R.id.check_state) {
                            //todo save data in db
                        } else {
                            //todo start detail activity
                        }
                    }
                })
            }
        return root
    }


    inner class MyAdapter(var data: MutableList<Task>) : RecyclerView.Adapter<MyViewHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
            return MyViewHolder(layoutInflater.inflate(R.layout.item_todo, parent, false))
        }

        override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
            holder.tv.text = data[position].titleForList
            holder.checkState.isChecked = data[position].isComplete
            holder.itemView.setOnClickListener { view ->
                listener?.onItemClick(data[position], view, position)
            }
            holder.checkState.setOnClickListener {view ->
                listener?.onItemClick(data[position], view, position)
            }
        }

        override fun getItemCount() = data.size
    }

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tv = itemView.findViewById<TextView>(R.id.text)
        val checkState = itemView.findViewById<CheckBox>(R.id.check_state)
    }

    fun setOnItemClickListener(listener: OnItemClickListener<Task>) {
        this.listener = listener
    }

    companion object {
        fun newIntance(): TasksFragment = TasksFragment()
    }
}