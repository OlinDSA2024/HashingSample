package org.example

import java.io.ByteArrayOutputStream
import java.io.IOException


/**
 *  The <code>BinaryWriter</code> class provides methods for converting
 *  primitive type variables ({@code boolean}, {@code byte}, {@code char},
 *  {@code int}, {@code long}, {@code float}, and {@code double})
 *  to sequences of bits and writing them to byte array.
 *  Uses big-endian (most-significant byte first).
 *  <p>
 *  @author Robert Sedgewick
 *  @author Kevin Wayne
 *  (modifications by Paul Ruvolo)
 */
class BinaryWriter(val keepTrackOfBinaryString: Boolean=false) {
    private var out = ByteArrayOutputStream()
    private var binaryStringOut = ByteArrayOutputStream()
    private var buffer = 0 // 8-bit buffer of bits to write
    private var n = 0 // number of bits remaining in buffer

    /**
     * Writes the specified bit to the output.
     */
    private fun writeBit(bit: Boolean) {
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
     * Writes the 8-bit byte to the output.
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

    // write out any remaining bits in buffer to the output, padding with 0s
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
    private fun flush() {
        clearBuffer()
        try {
            out.flush()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }


    /**
     * Writes the specified bit to the output.
     * @param x the `boolean` to write.
     */
    fun write(x: Boolean) {
        writeBit(x)
    }

    /**
     * Writes the 8-bit byte to the output.
     * @param x the `byte` to write.
     */
    fun write(x: Byte) {
        writeByte(x.toInt() and 0xff)
    }

    /**
     * Writes the 32-bit int to the output.
     * @param x the `int` to write.
     */
    fun write(x: Int) {
        writeByte((x ushr 24) and 0xff)
        writeByte((x ushr 16) and 0xff)
        writeByte((x ushr 8) and 0xff)
        writeByte((x ushr 0) and 0xff)
    }

    /**
     * Writes the *r*-bit int to the output.
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
     * Writes the 64-bit double to the output.
     * @param x the `double` to write.
     */
    fun write(x: Double) {
        write(java.lang.Double.doubleToRawLongBits(x))
    }

    /**
     * Writes the 64-bit long to the output.
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
     * Writes the 32-bit float to the output.
     * @param x the `float` to write.
     */
    fun write(x: Float) {
        write(java.lang.Float.floatToRawIntBits(x))
    }

    /**
     * Writes the 16-bit int to the output.
     * @param x the `short` to write.
     */
    fun write(x: Short) {
        writeByte((x.toInt() ushr 8) and 0xff)
        writeByte((x.toInt() ushr 0) and 0xff)
    }

    /**
     * Writes the 8-bit char to the output.
     * @param x the `char` to write.
     * @throws IllegalArgumentException if `x` is not between 0 and 255.
     */
    fun write(x: Char) {
        require(!(x.code < 0 || x.code >= 256)) { "Illegal 8-bit char = $x" }
        writeByte(x.code)
    }

    /**
     * Writes the *r*-bit char to the output.
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
     * Writes the string of 8-bit characters to the output.
     * @param s the `String` to write.
     * @throws IllegalArgumentException if any character in the string is not
     * between 0 and 255.
     */
    fun write(s: String) {
        for (element in s) write(element)
    }

    /**
     * Writes the string of *r*-bit characters to the output.
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
     * @return the output as a byte array
     */
    fun toByteArray(): ByteArray {
        flush()
        return out.toByteArray()
    }

    /**
     * @return the output as a binary string.  If the writer
     *   was not setup with [keepTrackOfBinaryString] as true,
     *   this function returns null.
     */
    fun toBinaryString():String? {
        flush()
        return if (keepTrackOfBinaryString) {
            binaryStringOut.toByteArray().decodeToString()
        } else {
            null
        }
    }
}


/**
 * The `BinaryStdIn` class provides static methods for reading
 * in bits from standard input. It can process the bits
 * one bit at a time (as a `boolean`),
 * 8 bits at a time (as a `byte` or `char`),
 * 16 bits at a time (as a `short`), 32 bits at a time
 * (as an `int` or `float`), or 64 bits at a time (as a
 * `double` or `long`).
 *
 *
 * All primitive types are assumed to be represented using their
 * standard Java representations, in big-endian (most significant
 * byte first) order.
 *
 *
 * The client should not intermix calls to `BinaryStdIn` with calls
 * to `StdIn` or `System.in`;
 * otherwise unexpected behavior will result.
 *
 * @author Robert Sedgewick
 * @author Kevin Wayne
 * modifications by Paul Ruvolo
 */
class BinaryReader(val bytes: ByteArray) {
    private val EOF = -1 // end of file
    private var byteIndex = 0
    private var buffer: Int = 0 // one character buffer
    private var n = 0 // number of bits left in buffer
    var isDone = false
    // fill buffer
    init {
        fillBuffer()
    }

    private fun fillBuffer() {
        if (byteIndex in bytes.indices) {
            buffer = bytes[byteIndex].toUByte().toInt()
            byteIndex++
            n = 8
        } else {
            isDone = true
        }
    }

    /**
     * Reads the next bit of data from standard input and return as a boolean.
     *
     * @return the next bit of data from standard input as a `boolean`
     * @throws NoSuchElementException if standard input is empty
     */
    fun readBoolean(): Boolean {
        if (isDone) throw NoSuchElementException("Reading from empty input stream")
        n--
        val bit = ((buffer shr n) and 1) == 1
        if (n == 0) fillBuffer()
        return bit
    }

    /**
     * Reads the next 8 bits from standard input and return as an 8-bit char.
     * Note that `char` is a 16-bit type;
     * to read the next 16 bits as a char, use `readChar(16)`.
     *
     * @return the next 8 bits of data from standard input as a `char`
     * @throws NoSuchElementException if there are fewer than 8 bits available on standard input
     */
    fun readChar(): Char {
        if (isDone) throw NoSuchElementException("Reading from empty input stream")

        // special case when aligned byte
        if (n == 8) {
            val x = buffer
            fillBuffer()
            return (x and 0xff).toChar()
        }

        // combine last n bits of current buffer with first 8-n bits of new buffer
        var x = buffer
        x = x shl (8 - n)
        val oldN = n
        fillBuffer()
        if (isDone) throw NoSuchElementException("Reading from empty input stream")
        n = oldN
        x = x or (buffer ushr n)
        return (x and 0xff).toChar()
        // the above code doesn't quite work for the last character if n = 8
        // because buffer will be -1, so there is a special case for aligned byte
    }

    /**
     * Reads the next *r* bits from standard input and return as an *r*-bit character.
     *
     * @param  r number of bits to read.
     * @return the next r bits of data from standard input as a `char`
     * @throws NoSuchElementException if there are fewer than `r` bits available on standard input
     * @throws IllegalArgumentException unless `1 <= r <= 16`
     */
    fun readChar(r: Int): Char {
        require(!(r < 1 || r > 16)) { "Illegal value of r = $r" }

        // optimize r = 8 case
        if (r == 8) return readChar()

        var x = 0.toChar()
        for (i in 0 until r) {
            x = (x.code shl 1).toChar()
            val bit = readBoolean()
            if (bit) x = (x.code or 1).toChar()
        }
        return x
    }

    /**
     * Reads the remaining bytes of data from standard input and return as a string.
     *
     * @return the remaining bytes of data from standard input as a `String`
     * @throws NoSuchElementException if standard input is empty or if the number of bits
     * available on standard input is not a multiple of 8 (byte-aligned)
     */
    fun readString(): String {
        if (isDone) throw NoSuchElementException("Reading from empty input stream")

        val sb = StringBuilder()
        while (!isDone) {
            val c = readChar()
            sb.append(c)
        }
        return sb.toString()
    }


    /**
     * Reads the next 16 bits from standard input and return as a 16-bit short.
     *
     * @return the next 16 bits of data from standard input as a `short`
     * @throws NoSuchElementException if there are fewer than 16 bits available on standard input
     */
    fun readShort(): Short {
        var x: Short = 0
        for (i in 0..1) {
            val c = readChar()
            x = (x.toInt() shl 8).toShort()
            x = (x.toInt() or c.code).toShort()
        }
        return x
    }

    /**
     * Reads the next 32 bits from standard input and return as a 32-bit int.
     *
     * @return the next 32 bits of data from standard input as a `int`
     * @throws NoSuchElementException if there are fewer than 32 bits available on standard input
     */
    fun readInt(): Int {
        var x = 0
        for (i in 0..3) {
            val c = readChar()
            x = x shl 8
            x = x or c.code
        }
        return x
    }

    /**
     * Reads the next *r* bits from standard input and return as an *r*-bit int.
     *
     * @param  r number of bits to read.
     * @return the next r bits of data from standard input as a `int`
     * @throws NoSuchElementException if there are fewer than `r` bits available on standard input
     * @throws IllegalArgumentException unless `1 <= r <= 32`
     */
    fun readInt(r: Int): Int {
        require(!(r < 1 || r > 32)) { "Illegal value of r = $r" }

        // optimize r = 32 case
        if (r == 32) return readInt()

        var x = 0
        for (i in 0 until r) {
            x = x shl 1
            val bit = readBoolean()
            if (bit) x = x or 1
        }
        return x
    }

    /**
     * Reads the next 64 bits from standard input and return as a 64-bit long.
     *
     * @return the next 64 bits of data from standard input as a `long`
     * @throws NoSuchElementException if there are fewer than 64 bits available on standard input
     */
    fun readLong(): Long {
        var x: Long = 0
        for (i in 0..7) {
            val c = readChar()
            x = x shl 8
            x = x or c.code.toLong()
        }
        return x
    }


    /**
     * Reads the next 64 bits from standard input and return as a 64-bit double.
     *
     * @return the next 64 bits of data from standard input as a `double`
     * @throws NoSuchElementException if there are fewer than 64 bits available on standard input
     */
    fun readDouble(): Double {
        return java.lang.Double.longBitsToDouble(readLong())
    }

    /**
     * Reads the next 32 bits from standard input and return as a 32-bit float.
     *
     * @return the next 32 bits of data from standard input as a `float`
     * @throws NoSuchElementException if there are fewer than 32 bits available on standard input
     */
    fun readFloat(): Float {
        return java.lang.Float.intBitsToFloat(readInt())
    }


    /**
     * Reads the next 8 bits from standard input and return as an 8-bit byte.
     *
     * @return the next 8 bits of data from standard input as a `byte`
     * @throws NoSuchElementException if there are fewer than 8 bits available on standard input
     */
    fun readByte(): Byte {
        val c = readChar()
        return (c.code and 0xff).toByte()
    }
}