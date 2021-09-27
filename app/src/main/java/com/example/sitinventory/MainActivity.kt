package com.example.sitinventory

import android.os.Bundle
import android.view.KeyEvent
import android.widget.Button
import android.widget.EditText
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import java.io.File


class MainActivity : AppCompatActivity() {
    lateinit var barcodeEdit:EditText
    lateinit var resultEdit:EditText
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
        resultFile.writeText(ss)

        addLine("${info.barcode} \n${info.code} \n${info.department} \n${info.worker}")
    }

  fun addLine(s:String){
      resultEdit.append(s+"\n")
  }


}

