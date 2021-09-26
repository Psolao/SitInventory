package com.example.sitinventory

import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.widget.Button
import android.widget.EditText
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.io.OutputStream


class MainActivity : AppCompatActivity() {
    private val REQUEST_WRITE_STORAGE_CODE: Int = 1
    private val REQUEST_OPEN_DOC_CODE: Int = 2
    lateinit var barcodeEdit:EditText
    lateinit var resultEdit:EditText
    lateinit var addButton:Button
    lateinit var dataFile: File

    val openDocLauncher = registerForActivityResult( ActivityResultContracts.OpenDocument()){
        resultEdit.editableText.append(it.path)
        copyFile(it, dataFile)
        readFile()
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        barcodeEdit = findViewById(R.id.barcode_text)
        resultEdit = findViewById(R.id.result_text)
        addButton = findViewById(R.id.add_button)
        initData()
        addButton.setOnClickListener{selectFile()}
    }

    fun initData(){
        dataFile = File(filesDir,"items.csv")
        if (!dataFile.exists()){
           selectFile()
        }
        else readFile()
    }

    fun selectFile(){
        openDocLauncher.launch(arrayOf("*/*"))
    }

    fun readFile(){
        var i = 0
        dataFile.forEachLine {
            if (i>5) return@forEachLine
            i++
            resultEdit.editableText.append(it)
        }
    }

    fun copyFile(uri:Uri, destFile: File) {
        var source : InputStream? = null
        var destination : OutputStream? = null
        try {
            source = contentResolver.openInputStream(uri)!!
            destination = FileOutputStream(destFile)
            copy(source,destination)
        } finally {
            source?.close()
            destination?.close()
        }
    }

    private val EOF = -1
    private val DEFAULT_BUFFER_SIZE = 1024 * 4
    private fun copy(input: InputStream, output: OutputStream): Long {
        var count: Long = 0
        var n: Int
        val buffer = ByteArray(DEFAULT_BUFFER_SIZE)
        while (EOF !== input.read(buffer).also { n = it }) {
            output.write(buffer, 0, n)
            count += n.toLong()
        }
        return count
    }

}

