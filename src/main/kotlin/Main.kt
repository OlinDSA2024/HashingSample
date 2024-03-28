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
    val encoded = LZAlgorithm.encode("AABABBBABAABABBBABBABB".toByteArray(), encodeABBinary = true)
    println(encoded)
    val text = File("src/main/kotlin/book").readText(Charsets.UTF_8)
    val encoded2 = LZAlgorithm.encode(text.toByteArray())
    println("Compression ratio: ${1 - encoded2.size.toDouble() / text.length}")
    val br = BinaryReader(encoded)
    while (!br.isDone) {
        if (br.readBoolean()) {
            print("1")
        } else {
            print("0")
        }
    }
    println()
}