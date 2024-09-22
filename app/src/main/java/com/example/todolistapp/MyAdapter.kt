package com.example.todolistapp

import android.annotation.SuppressLint
import android.app.TimePickerDialog
import android.content.Context
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import java.util.Calendar

class MyAdapter(val context: Context):RecyclerView.Adapter<MyAdapter.ViewHolder>() {
    // val mTaskViewModel=ViewModelProvider(context).get(TaskViewModel::class.java)
    var listTasks= emptyList<Task>()
    class ViewHolder(view:View):RecyclerView.ViewHolder(view) {
        val title=view.findViewById<TextView>(R.id.title_text)
        val description=view.findViewById<TextView>(R.id.desc_text)
        val time=view.findViewById<TextView>(R.id.time_text)
        val delete=view.findViewById<ImageView>(R.id.delete)
        val done=view.findViewById<CheckBox>(R.id.check)
        val rowLayout=view.findViewById<ConstraintLayout>(R.id.row_layout)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_view,parent,false))
    }


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val curItem=listTasks[position]
        holder.title.text=curItem.title.toString()
        holder.description.text=curItem.description.toString()
        holder.time.text=curItem.time.toString()
        holder.delete.setOnClickListener {
            mTaskViewModel.deleteTask(curItem)
        }
        holder.rowLayout.setOnClickListener {
            updateInputDialog(curItem)
        }
        holder.done.isChecked = curItem.done

        holder.done.setOnClickListener {
            curItem.done=!curItem.done
            holder.done.isChecked=curItem.done
            mTaskViewModel.updateTask(curItem)
        }

    }
    override fun getItemCount(): Int {
        return listTasks.size
    }
    fun refreshData(tasks:List<Task>){
        this.listTasks=tasks
        notifyDataSetChanged()
        setVisibleOfSelectAll()
        if(atLeastOneNotSelected()){
            selectionMenuItem?.setIcon(R.drawable.baseline_check_circle_outline_24)
        }else{
            selectionMenuItem?.setIcon(R.drawable.baseline_check_circle_24)
        }
    }

    private fun setVisibleOfSelectAll() {
        var check=false
        listTasks.forEach {
            if(it.done==true){
                check=true
            }
        }
        if(check) {
            selectionMenuItem?.isVisible=true
        }
        else {
            selectionMenuItem?.isVisible=false
        }
    }
    fun atLeastOneNotSelected(): Boolean {
        var check=false
        listTasks.forEach {
            if(it.done==false){
                check=true
            }
        }
        return check

    }
    fun atLeastOneSelected(): Boolean {
        var check=false
        listTasks.forEach {
            if(it.done==true){
                check=true
            }
        }
        return check

    }

    fun selectAll(){
        listTasks.forEach {
            it.done=true
            mTaskViewModel.updateTask(it)
        }
    }
    fun notSelectAll(){
        listTasks.forEach {
            it.done=false
            mTaskViewModel.updateTask(it)
        }
    }
    @SuppressLint("SuspiciousIndentation")
    private fun updateInputDialog(curItem: Task) {
        val dialogView=LayoutInflater.from(context).inflate(R.layout.dialog_view,null)
        val title=dialogView.findViewById<EditText>(R.id.title_in)
        val desc=dialogView.findViewById<EditText>(R.id.desc_in)
        val textTime=dialogView.findViewById<TextView>(R.id.time_in)
        val save=dialogView.findViewById<Button>(R.id.save)
        title.setText(curItem.title)
        desc.setText(curItem.description)
        if(curItem.time.toString().isEmpty())
            textTime.text="SELECT TIME"
        else
            textTime.text=curItem.time
        textTime.setOnClickListener{
            val curTime= Calendar.getInstance()
            val hours=curTime.get(Calendar.HOUR_OF_DAY)
            val minute=curTime.get(Calendar.MINUTE)
            val timePicker= TimePickerDialog(context,R.style.TimePickerTheme,{
                    view,hourOfDay,minute ->
                val selectedTime=String.format("%02d:%02d",hourOfDay,minute)
                textTime.text=selectedTime
            },hours,minute,true)
            timePicker.show()
        }
        val builder= AlertDialog.Builder(context)
        builder.setView(dialogView)
        builder.setTitle("Edit Task")
        val dialog=builder.create()
        save.setOnClickListener {
            val title=title.text.toString()
            val desc=desc.text.toString()
            var time=textTime.text.toString()
            val task= Task(curItem.id,title,desc,time,curItem.done)
            if(CheckInput(title)) {
                if(time[0]=='S') time=""
                val task= Task(curItem.id,title,desc,time,curItem.done)
                mTaskViewModel.updateTask(task)
                Toast.makeText(context,"Successfully updated", Toast.LENGTH_SHORT).show()
                dialog.dismiss()
            }
            else{
                Toast.makeText(context,"please fill out title field", Toast.LENGTH_LONG).show()
            }

        }
        dialog.show()

    }
    private fun CheckInput(title:String):Boolean
    {
        return !(TextUtils.isEmpty(title))
    }



}