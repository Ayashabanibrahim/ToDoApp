package com.example.todolistapp

import android.app.TimePickerDialog
import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import android.widget.EditText
import android.widget.SearchView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.todolistapp.databinding.ActivityMainBinding
import java.util.Calendar

lateinit var mTaskViewModel: com.example.todolistapp.TaskViewModel
lateinit var adapter: MyAdapter
var selectionMenuItem:MenuItem?=null
class MainActivity : AppCompatActivity()  {
    private lateinit var binding:ActivityMainBinding
    private var selectAllMode=false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        mTaskViewModel=ViewModelProvider(this).get(com.example.todolistapp.TaskViewModel::class.java)
        adapter=MyAdapter(this)
        binding.recycler.adapter=adapter
        binding.recycler.layoutManager=LinearLayoutManager(this)
        mTaskViewModel.readAllData.observe(this, Observer { task ->
            adapter.refreshData(task)
        })

        binding.floatingActionButton.setOnClickListener {
            showInputDialog()
        }

        val searchView = binding.search
        searchView.apply {
            isIconified = false
            isFocusable = true
            requestFocusFromTouch()
            queryHint = "Search .."
            setOnQueryTextListener(object : SearchView.OnQueryTextListener,
                androidx.appcompat.widget.SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query: String?): Boolean {
                    // Handle query submission if needed
                    return false
                }

                override fun onQueryTextChange(newText: String?): Boolean {
                    searchDatabase(newText)
                    return true
                }
            })
        }








    }



    private fun showInputDialog() {
        val dialogView=LayoutInflater.from(this).inflate(R.layout.dialog_view,null)
        val title=dialogView.findViewById<EditText>(R.id.title_in)
        val desc=dialogView.findViewById<EditText>(R.id.desc_in)
        val textTime=dialogView.findViewById<TextView>(R.id.time_in)
        val save=dialogView.findViewById<Button>(R.id.save)
        textTime.setOnClickListener{
            val curTime=Calendar.getInstance()
            val hours=curTime.get(Calendar.HOUR_OF_DAY)
            val minute=curTime.get(Calendar.MINUTE)
            var timePicker=TimePickerDialog(this, R.style.TimePickerTheme, { _, hourOfDay, minute ->
                val selectedTime=String.format("%02d:%02d",hourOfDay,minute)
                textTime.text=selectedTime
                // desc.setText(selectedTime)
            },hours,minute,true)
            timePicker.show()
        }
        val builder=AlertDialog.Builder(this)//,R.style.CustomDialogeTheme)
        builder.setView(dialogView)
        builder.setTitle("New Task")
        val dialog=builder.create()
        save.setOnClickListener {
            val title=title.text.toString()
            val desc=desc.text.toString()
            var time=textTime.text.toString()
            if(CheckInput(title)) {
                if(time[0]=='S') time=""
                val task= Task(0,title,desc,time,false)
                mTaskViewModel.addTask(task)
                mTaskViewModel.readAllData.observe(this, Observer { task ->
                    adapter.refreshData(task)
                })
                Toast.makeText(this, "Successfully added", Toast.LENGTH_SHORT).show()
                dialog.dismiss()
            }
            else{
                Toast.makeText(this,"please fill out title field", Toast.LENGTH_LONG).show()
            }

        }
        dialog.show()

    }


    private fun CheckInput(title:String):Boolean
    {
        return !(TextUtils.isEmpty(title))
    }
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu,menu)
        selectionMenuItem=menu?.findItem(R.id.selsectAll)!!
        selectionMenuItem?.isVisible=false
        selectionMenuItem?.setIcon(R.drawable.baseline_check_circle_outline_24)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.deleteAll ->{
                deleteAllTasks()
                return true
            }
            R.id.selsectAll->{
                toggleSelectionMode()
                if(selectAllMode && selectionMenuItem?.isVisible==true){
                    adapter.selectAll()
                    selectionMenuItem?.setIcon(R.drawable.baseline_check_circle_24)
                }
                if(selectAllMode==false &&selectionMenuItem?.isVisible==true){
                    val check= adapter.atLeastOneNotSelected()
                    if(!check)
                    {
                        adapter.notSelectAll()
                        selectionMenuItem?.setIcon(R.drawable.baseline_check_circle_outline_24)
                        selectionMenuItem?.isVisible = false
                    }else{
                        adapter.selectAll()
                        // selectionMenuItem?.setIcon(R.drawable.baseline_check_circle_outline_24)
                        selectionMenuItem?.setIcon(R.drawable.baseline_check_circle_24)
                    }
                }
                return true
            }
            else -> {
                return super.onOptionsItemSelected(item)
            }

        }
    }



    private fun toggleSelectionMode() {
        selectAllMode=!selectAllMode

    }


    private fun searchDatabase(query: String?){
        val searchQuery="%$query%"
        if(query!=null) {
            mTaskViewModel.searchTask(query).observe(this,Observer{
                adapter.refreshData(it)
            })
        }else{
            mTaskViewModel.readAllData.observe(this, Observer {
                adapter.refreshData(it)
            })
        }

    }
    private fun deleteAllTasks() {
        val builder= AlertDialog.Builder(this)

        builder.setPositiveButton("Yes"){_,_->
            if(!adapter.atLeastOneSelected()){
                Toast.makeText(this,"Not found any done tasks to remove!",
                    Toast.LENGTH_SHORT).show()
            }
            else
            {
                mTaskViewModel.deleteAllTasks()
                Toast.makeText(this,"Successfully removed done tasks",
                    Toast.LENGTH_SHORT).show()
            }
        }
        builder.setNegativeButton("No"){_,_-> }
        builder.setTitle("Delete done tasks?")
        builder.setMessage("Are you sure you want to delete done tasks?")
        builder.create().show()
    }

}