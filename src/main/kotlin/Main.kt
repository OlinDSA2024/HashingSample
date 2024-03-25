package org.example

fun main() {
    val m = AssociativeArray<String, String>()
    m["asdf"] = "f"
    println("asdf" in m)
    println("asfdf" in m)

    val q = AssociativeArray<Int, Int>()

    for (i in 0 until 300000) {
        q[i] = 2*i
    }
    println(q[30003])
    println(q.keyValuePairs().size)
    println(300000000 in q)
}