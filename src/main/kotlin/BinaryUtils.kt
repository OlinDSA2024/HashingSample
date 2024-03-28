package org.example

import java.io.ByteArrayOutputStream
import java.io.IOException


/**
 *  The <code>BinaryStdOut</code> class provides methods for converting
 *  primitive type variables ({@code boolean}, {@code byte}, {@code char},
 *  {@code int}, {@code long}, {@code float}, and {@code double})
 *  to sequences of bits and writing them to standard output.
 *  Uses big-endian (most-significant byte first).
 *  <p>
 *  The client must {@code flush()} the output stream when finished writing bits.
 *  Adapted from code by:
 *  @author Robert Sedgewick
 *  @author Kevin Wayne
 */
class BinaryStdOut(val keepTrackOfBinaryString: Boolean=false) {
    private var out = ByteArrayOutputStream()
    private var binaryStringOut = ByteArrayOutputStream()
    private var buffer = 0 // 8-bit buffer of bits to write
    private var n = 0 // number of bits remaining in buffer

    /**
     * Writes the specified bit to standard output.
     */
    fun writeBit(bit: Boolean) {
        if (keepTrackOfBinaryString) {
            if (bit) {
                binaryStringOut.write("1".toByteArray())
            } else {
                binaryStringOut.write("0".toByteArray())
            }
        }

        // add bit to buffer
        buffer = buffer shl 1
        if (bit) buffer = buffer or 1

        // if buffer is full (8 bits), write out as a single byte
        n++
        if (n == 8) clearBuffer()
    }

    /**
     * Writes the 8-bit byte to standard output.
     */
    private fun writeByte(x: Int) {
        assert(x in 0..255)
        // optimized if byte-aligned
        if (n == 0) {
            try {
                out.write(x)
            } catch (e: IOException) {
                e.printStackTrace()
            }
            return
        }

        // otherwise write one bit at a time
        for (i in 0..7) {
            val bit = ((x ushr (8 - i - 1)) and 1) == 1
            writeBit(bit)
        }
    }

    // write out any remaining bits in buffer to standard output, padding with 0s
    private fun clearBuffer() {
        if (n == 0) return
        if (n > 0) buffer = buffer shl (8 - n)
        try {
            out.write(buffer)
        } catch (e: IOException) {
            e.printStackTrace()
        }
        n = 0
        buffer = 0
    }

    /**
     * Flushes standard output, padding 0s if number of bits written so far
     * is not a multiple of 8.
     */
    fun flush() {
        clearBuffer()
        try {
            out.flush()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }


    /**
     * Writes the specified bit to standard output.
     * @param x the `boolean` to write.
     */
    fun write(x: Boolean) {
        writeBit(x)
    }

    /**
     * Writes the 8-bit byte to standard output.
     * @param x the `byte` to write.
     */
    fun write(x: Byte) {
        writeByte(x.toInt() and 0xff)
    }

    /**
     * Writes the 32-bit int to standard output.
     * @param x the `int` to write.
     */
    fun write(x: Int) {
        writeByte((x ushr 24) and 0xff)
        writeByte((x ushr 16) and 0xff)
        writeByte((x ushr 8) and 0xff)
        writeByte((x ushr 0) and 0xff)
    }

    /**
     * Writes the *r*-bit int to standard output.
     * @param x the `int` to write.
     * @param r the number of relevant bits in the char.
     * @throws IllegalArgumentException if `r` is not between 1 and 32.
     * @throws IllegalArgumentException if `x` is not between 0 and 2<sup>r</sup> - 1.
     */
    fun write(x: Int, r: Int) {
        if (r == 32) {
            write(x)
            return
        }
        require(!(r < 1 || r > 32)) { "Illegal value for r = $r" }
        require(!(x < 0 || x >= (1 shl r))) { "Illegal $r-bit char = $x" }
        for (i in 0 until r) {
            val bit = ((x ushr (r - i - 1)) and 1) == 1
            writeBit(bit)
        }
    }


    /**
     * Writes the 64-bit double to standard output.
     * @param x the `double` to write.
     */
    fun write(x: Double) {
        write(java.lang.Double.doubleToRawLongBits(x))
    }

    /**
     * Writes the 64-bit long to standard output.
     * @param x the `long` to write.
     */
    fun write(x: Long) {
        writeByte(((x ushr 56) and 0xffL).toInt())
        writeByte(((x ushr 48) and 0xffL).toInt())
        writeByte(((x ushr 40) and 0xffL).toInt())
        writeByte(((x ushr 32) and 0xffL).toInt())
        writeByte(((x ushr 24) and 0xffL).toInt())
        writeByte(((x ushr 16) and 0xffL).toInt())
        writeByte(((x ushr 8) and 0xffL).toInt())
        writeByte(((x ushr 0) and 0xffL).toInt())
    }

    /**
     * Writes the 32-bit float to standard output.
     * @param x the `float` to write.
     */
    fun write(x: Float) {
        write(java.lang.Float.floatToRawIntBits(x))
    }

    /**
     * Writes the 16-bit int to standard output.
     * @param x the `short` to write.
     */
    fun write(x: Short) {
        writeByte((x.toInt() ushr 8) and 0xff)
        writeByte((x.toInt() ushr 0) and 0xff)
    }

    /**
     * Writes the 8-bit char to standard output.
     * @param x the `char` to write.
     * @throws IllegalArgumentException if `x` is not between 0 and 255.
     */
    fun write(x: Char) {
        require(!(x.code < 0 || x.code >= 256)) { "Illegal 8-bit char = $x" }
        writeByte(x.code)
    }

    /**
     * Writes the *r*-bit char to standard output.
     * @param x the `char` to write.
     * @param r the number of relevant bits in the char.
     * @throws IllegalArgumentException if `r` is not between 1 and 16.
     * @throws IllegalArgumentException if `x` is not between 0 and 2<sup>r</sup> - 1.
     */
    fun write(x: Char, r: Int) {
        if (r == 8) {
            write(x)
            return
        }
        require(!(r < 1 || r > 16)) { "Illegal value for r = $r" }
        require(x.code < (1 shl r)) { "Illegal $r-bit char = $x" }
        for (i in 0 until r) {
            val bit = ((x.code ushr (r - i - 1)) and 1) == 1
            writeBit(bit)
        }
    }

    /**
     * Writes the string of 8-bit characters to standard output.
     * @param s the `String` to write.
     * @throws IllegalArgumentException if any character in the string is not
     * between 0 and 255.
     */
    fun write(s: String) {
        for (element in s) write(element)
    }

    /**
     * Writes the string of *r*-bit characters to standard output.
     * @param s the `String` to write.
     * @param r the number of relevant bits in each character.
     * @throws IllegalArgumentException if r is not between 1 and 16.
     * @throws IllegalArgumentException if any character in the string is not
     * between 0 and 2<sup>r</sup> - 1.
     */
    fun write(s: String, r: Int) {
        for (element in s) write(element, r)
    }

    /**
     * @return the stream as a byte array
     */
    fun toByteArray(): ByteArray {
        return out.toByteArray()
    }

    fun toBinaryString():ByteArray? {
        return if (keepTrackOfBinaryString) {
            binaryStringOut.toByteArray()
        } else {
            null
        }
    }
}