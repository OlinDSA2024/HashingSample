package org.example

import java.io.ByteArrayOutputStream
import java.io.IOException

class LZAlgorithm {
    companion object {
        /**
         * Use LZ compression to represent the input bytes as a string
         *   of 1's and 0's (one byte per binary digit)
         * @param bytes the data to encode
         * @param encodeABBinary true if A should map to 0 and B map 1
         */
        fun encodeAsBinaryString(bytes: ByteArray, encodeABBinary: Boolean=false): String? {
            return compressionHelper(bytes, encodeABBinary, keepTrackOfBinaryString = true).toBinaryString()
        }

        /**
         * Use LZ compression to represent the input bytes as a ByteArray
         *   of 1's and 0's (one byte per binary digit)
         * @param bytes the data to encode
         * @param encodeABBinary true if A should map to 0 and B map 1
         */
        fun encodeAsByteArray(bytes: ByteArray, encodeABBinary: Boolean=false): ByteArray {
            return compressionHelper(bytes, encodeABBinary).toByteArray()
        }

        private fun compressionHelper(bytes: ByteArray,
                                      encodeABBinary: Boolean=false,
                                      keepTrackOfBinaryString: Boolean = false): BinaryWriter {
            val codeBook = AssociativeArray<List<Byte>, Int>()
            // encode the empty string
            codeBook[listOf()] = codeBook.size()
            val encodedData = BinaryWriter(keepTrackOfBinaryString)
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
                            encodedData.write(false)
                        } else {
                            encodedData.write(true)
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
            return encodedData
        }
    }
}