package org.example

import java.io.File

fun main() {
    val m = AssociativeArray<String, String>()
    m["asdf"] = "f"
    val q = AssociativeArray<Int, Int>()

    for (i in 0 until 300000) {
        q[i] = 2*i
    }
    val compressed = LZAlgorithm.encodeAsBinaryString("AABABBBABAABABBBABBABB".toByteArray(), encodeABBinary = true)
    val compressedBinary = LZAlgorithm.encodeAsByteArray("AFFSaaa".toByteArray(), encodeABBinary = false)
    println("compressedBinary ${compressedBinary.size}")
    val text = File("src/main/kotlin/book").readText(Charsets.UTF_8)
    val encoded2 = LZAlgorithm.encodeAsByteArray(text.toByteArray())
    println("Compression ratio: ${1 - encoded2.size.toDouble() / text.length}")
    assert(LZAlgorithm.decompress(encoded2).contentEquals(text.toByteArray()))
    println(LZAlgorithm.decompress(encoded2).decodeToString())
}