package org.example

import java.io.File

fun main() {
    val m = AssociativeArray<String, String>()
    m["asdf"] = "f"
    println("asdf" in m)
    println("asfdf" in m)

    val q = AssociativeArray<Int, Int>()

    for (i in 0 until 300000) {
        q[i] = 2*i
    }
    val compressed = LZAlgorithm.encodeAsBinaryString("AABABBBABAABABBBABBABB".toByteArray(), encodeABBinary = true)
    println("binary string: $compressed")
    val text = File("src/main/kotlin/book").readText(Charsets.UTF_8)
    val encoded2 = LZAlgorithm.encodeAsByteArray(text.toByteArray())
    print("Compression ratio: ${1 - encoded2.size.toDouble() / text.length}")
}