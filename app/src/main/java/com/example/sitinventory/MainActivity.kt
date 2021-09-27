package com.example.sitinventory

import android.content.Intent
import android.os.Bundle
import android.view.KeyEvent
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import java.io.File


class MainActivity : AppCompatActivity() {
    lateinit var barcodeEdit:EditText
    lateinit var resultEdit:TextView
    lateinit var addButton:Button
    lateinit var dataFile: File
    lateinit var resultFile: File

    lateinit var viewModel: MainViewModel

    val openDocLauncher = registerForActivityResult( ActivityResultContracts.OpenDocument()){
        copyFile(this, it, dataFile)
        viewModel.load(dataFile)
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        viewModel = ViewModelProvider(this).get(com.example.sitinventory.MainViewModel::class.java)
        barcodeEdit = findViewById(R.id.barcode_text)
        resultEdit = findViewById(R.id.result_text)
        addButton = findViewById(R.id.add_button)
        initData()
        addButton.setOnClickListener{addCurBarcode()}
        barcodeEdit.setOnKeyListener{ _, keyCode, event ->
            if (event.action == KeyEvent.ACTION_DOWN &&
                keyCode == KeyEvent.KEYCODE_ENTER
            ) {
                addCurBarcode()
                return@setOnKeyListener true
            }
            false
        }
        resultEdit.text = viewModel.info
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.load_data_item -> {
                selectFile()
                true
            }
            R.id.save_result_item -> {
                exportResultAsText()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }


    fun initData(){
        resultFile = File(filesDir,"result.csv")
        if (!resultFile.exists()){
            resultFile.createNewFile()
        }
        viewModel.loadResultIfNoData(resultFile)
        dataFile = File(filesDir,"items.csv")
        if (!dataFile.exists()){
           selectFile()
        }
        else viewModel.loadIfNoData(dataFile)
    }

    fun selectFile(){
        openDocLauncher.launch(arrayOf("*/*"))
    }

    fun addCurBarcode(){
        val s = barcodeEdit.text.toString().trim()
        if (s!="") {
            addBarcode(s)
            barcodeEdit.setText("")
        }
    }

    fun addBarcode(s:String){
        val res = viewModel.result!!
        if (res.contains(s)){
            addLine("$s - уже инвентаризирован!")
            return
        }
        val info = viewModel.data!!.get(s)
        if (info==null) {
            addLine("$s - не найден!")
            return
        }
        val ss = (if (!res.isEmpty()) "\n" else "")+s
        res.add(s)
        resultFile.appendText(ss)

        addLine("${info.barcode} \n${info.code} \n"+
                "${info.name} \n${info.department} \n${info.worker}")
    }

  fun addLine(s:String){
      viewModel.info.appendLine(s)
      resultEdit.text = viewModel.info.toString()
  }


  fun exportResultAsText(){
      val sendIntent: Intent = Intent().apply {
          action = Intent.ACTION_SEND
          putExtra(Intent.EXTRA_TEXT, viewModel.result!!.joinToString("\n"))
          type = "text/plain"
      }
      val shareIntent = Intent.createChooser(sendIntent, null)
      startActivity(shareIntent)

  }


    fun exportResultAsFile(){
        val shareIntent: Intent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_STREAM, resultFile.toURI())
            type = "text/plain"
        }
        startActivity(Intent.createChooser(shareIntent, resources.getText(R.string.send_to)))
    }


}

