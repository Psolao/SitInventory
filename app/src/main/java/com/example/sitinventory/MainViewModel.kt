package com.example.sitinventory

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import java.io.File
import java.lang.StringBuilder

class MainViewModel(application: Application) : AndroidViewModel(application) {
    private var fData:MutableMap<String,BarcodeInfo>? = null
    val data:Map<String, BarcodeInfo>?
      get() =fData

    var info = StringBuilder("")

    private var fResults:MutableSet<String>? = null
    val result:MutableSet<String>? get() = fResults

   fun load(file:File){
       if (fData==null) fData = mutableMapOf()
       fData!!.clear()
       file.forEachLine {
           val s = it.split(delimiters = arrayOf(";"), ignoreCase = false,limit =  6)
           val barcode = s[0]
           fData!!.put(barcode,
               BarcodeInfo(barcode, getString(s,1),
                           getString(s,2),
                           getString(s,3),
                           getString(s,4),
                           getString(s,5)
                    ))
       }
   }

    private fun getString(list:List<String>, pos:Int):String? =
        if (pos>=0 || pos<list.size) list.get(pos) else null

   fun loadIfNoData(file: File){
       if (fData==null)
           load(file)
   }

    fun loadResult(file:File){
        if (fResults==null) fResults = mutableSetOf()
        file.forEachLine {
            fResults!!.add(it)
        }
    }

    fun loadResultIfNoData(file: File){
        if (fResults==null)
            loadResult(file)
    }


}