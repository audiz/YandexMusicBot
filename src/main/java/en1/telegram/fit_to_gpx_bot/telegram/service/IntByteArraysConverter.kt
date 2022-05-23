package en1.telegram.fit_to_gpx_bot.telegram.service

import java.io.*

/**
 * Convert Int[] <=> Byte[]
 * */
object IntByteArraysConverter {
    private const val BYTES_IN_INT = 4

    fun convert(array: IntArray): ByteArray {
        return if (array.isEmpty()) {
            ByteArray(0)
        } else writeInts(array)
    }

    fun convert(array: ByteArray): IntArray {
        return if (array.isEmpty()) {
            IntArray(0)
        } else readInts(array)
    }

    private fun writeInts(array: IntArray): ByteArray {
        return try {
            val bos = ByteArrayOutputStream(array.size * 4)
            val dos = DataOutputStream(bos)
            for (i in array.indices) {
                dos.writeInt(array[i])
            }
            bos.toByteArray()
        } catch (e: IOException) {
            throw RuntimeException(e)
        }
    }

    private fun readInts(array: ByteArray): IntArray {
        return try {
            val bis = ByteArrayInputStream(array)
            val dataInputStream = DataInputStream(bis)
            val size = array.size / BYTES_IN_INT
            val res = IntArray(size)
            for (i in 0 until size) {
                res[i] = dataInputStream.readInt()
            }
            res
        } catch (e: IOException) {
            throw RuntimeException(e)
        }
    }
}