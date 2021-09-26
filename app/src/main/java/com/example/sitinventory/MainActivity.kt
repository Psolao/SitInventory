package com.example.sitinventory

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.widget.Button
import android.widget.EditText
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import java.io.File


class MainActivity : AppCompatActivity() {
    private val REQUEST_WRITE_STORAGE_CODE: Int = 1
    private val REQUEST_OPEN_DOC_CODE: Int = 2
    lateinit var barcodeEdit:EditText
    lateinit var resultEdit:EditText
    lateinit var addButton:Button
    val openDocLauncher = registerForActivityResult( ActivityResultContracts.OpenDocument()){
        resultEdit.editableText.append(it.toString())
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        requestAppPermissions()
        barcodeEdit = findViewById(R.id.barcode_text)
        resultEdit = findViewById(R.id.result_text)
        addButton = findViewById(R.id.add_button)
        initData()
    }

    fun initData(){
        val file = File(filesDir,"items.csv")
        if (!file.exists()){
           openDocLauncher.launch(arrayOf("*/*"))
        }
    }

    fun testData(){
        showDirInfo(Environment.getExternalStorageDirectory())
    }

    fun showDirInfo(file: File?){
        if (file==null) resultEdit.editableText.appendLine("not found")
        else {
            resultEdit.editableText.appendLine(file.absolutePath)
            file.list().forEach {
                resultEdit.editableText.appendLine(it)
            }
        }
    }


    private fun requestAppPermissions() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            return
        }
        if (hasReadPermissions() && hasWritePermissions()) {
            return
        }
        ActivityCompat.requestPermissions(
            this, arrayOf(
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ), REQUEST_WRITE_STORAGE_CODE
        ) // your request code
    }

    private fun hasReadPermissions(): Boolean {
        return ContextCompat.checkSelfPermission(
            baseContext,
            Manifest.permission.READ_EXTERNAL_STORAGE
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun hasWritePermissions(): Boolean {
        return ContextCompat.checkSelfPermission(
            baseContext,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        ) == PackageManager.PERMISSION_GRANTED
    }
}

