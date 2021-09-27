package com.example.sitinventory

import android.content.Context
import android.net.Uri
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.io.OutputStream

fun copyFile(context: Context, uri: Uri, destFile: File) {
    var source : InputStream? = null
    var destination : OutputStream? = null
    try {
        source = context.contentResolver.openInputStream(uri)!!
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