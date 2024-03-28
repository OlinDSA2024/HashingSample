package org.example

import java.io.ByteArrayOutputStream
import java.io.IOException

class LZAlgorithm {
    companion object {
        fun encode(bytes: ByteArray, encodeABBinary: Boolean=false): ByteArray {
            val codeBook = AssociativeArray<List<Byte>, Int>()
            // encode the empty string
            codeBook[listOf()] = codeBook.size()
            val encodedData = BinaryStdOut(keepTrackOfBinaryString = true)
            var startPos = 0
            var codeWidth = 1
            var transition = 2
            for (i in bytes.indices) {
                val currSequence = bytes.slice(startPos until i)
                val codeWord = codeBook[currSequence + listOf(bytes[i])]
                if (codeWord == null) {
                    encodedData.write(codeBook[currSequence]!!, codeWidth)
                    if (encodeABBinary) {
                        // kludge to match Peter Schor Notes
                        if (listOf(bytes[i]).toByteArray()
                                .decodeToString() == "A"
                        ) {
                            encodedData.writeBit(false)
                        } else {
                            encodedData.writeBit(true)
                        }
                    } else {
                        encodedData.write(bytes[i])
                    }
                    if (codeBook.size() == transition) {
                        transition *= 2
                        codeWidth++
                    }
                    codeBook[currSequence + listOf(bytes[i])] = codeBook.size()
                    startPos = i+1
                }
            }
            val currSequence = bytes.slice(startPos until bytes.size)
            encodedData.write(codeBook[currSequence]!!, codeWidth)
            //encodedData.toBinaryString()
            return encodedData.toByteArray()
        }
    }
}