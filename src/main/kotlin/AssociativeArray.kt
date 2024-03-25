package org.example

/**
 * Represents a mapping of keys to values.
 */
class AssociativeArray<K, V> {
    private var n = 0
    private var mIndex = 0
    private val primeList = mutableListOf(53, 97, 193, 389, 769, 1543, 3079, 6151, 12289, 24593, 49157, 98317, 196613,
        393241, 786433, 1572869, 3145739, 6291469, 12582917, 25165843, 50331653, 100663319, 201326611, 402653189,
        402653189, 1610612741)
    private var buckets = MutableList(primeList[mIndex]) { AssociationList<K, V>() }

    /**
     * Insert the mapping from the key, [k], to the value, [v].
     * If the key already maps to a value, replace the mapping.
     */
    operator fun set(k: K, v: V) {
        if (n == 3*m()) {
            rehash()
        }
        val bucket = h(k)
        if (k !in buckets[bucket]) {
            n++
        }
        buckets[bucket][k] = v
    }

    /**
     * @return true if [k] is a key in the associative array
     */
    operator fun contains(k: K): Boolean {
        return k in buckets[h(k)]
    }

    /**
     * @return the value associated with the key [k] or null if it doesn't exist
     */
    operator fun get(k: K): V? {
        return buckets[h(k)][k]
    }

    /**
     * Remove the key, [k], from the associative array
     * @param k the key to remove
     * @return true if the item was successfully removed and false if the element was not found
     */
    fun remove(k: K): Boolean {
        val success = buckets[h(k)].remove(k)
        if (success) {
            n--
        }
        return success
    }

    /**
     * Increase the size of the hash table and rehash all existing elements
     */
    private fun rehash() {
        mIndex++
        val newBuckets = MutableList(primeList[mIndex]) { AssociationList<K, V>() }

        buckets.forEach() {
            for ((k, v) in it.keyValuePairs()) {
                newBuckets[h(k)][k] = v
            }
        }
        buckets = newBuckets
    }

    /**
     * @return the number of elements stored in the hash table
     */
    fun size(): Int {
        return n
    }

    /**
     * @return the full list of key value pairs for the associative array
     */
    fun keyValuePairs(): List<Pair<K, V>> {
        val allKeyValuePairs: MutableList<Pair<K, V>> = mutableListOf()
        buckets.forEach() {
            allKeyValuePairs += it.keyValuePairs()
        }
        return allKeyValuePairs
    }

    /**
     * The hash function using the division method
     * @param k the key to hash
     * @return the hash bucket
     */
    private fun h(k: K): Int {
        return k.hashCode().mod(m())
    }

    /**
     * @return the number of buckets in the hash map
     */
    private fun m(): Int {
        return primeList[mIndex]
    }
}

/**
 * Stores a set of key value pair associations using an association list
 */
private class AssociationList<K, V> {
    private val elements: MutableList<Pair<K, V>> = mutableListOf()

    /**
     * @return true if [k] is a key in the associative array
     */
    operator fun contains(k: K): Boolean {
        return get(k) != null
    }

    /**
     * @return the value associated with the key [k] or null if it doesn't exist
     */
    operator fun get(k: K): V? {
        for (element in elements) {
            if (element.first == k) {
                return element.second
            }
        }
        return null
    }

    /**
     * Insert the mapping from the key, [k], to the value, [v].
     * If the key already maps to a value, replace the mapping.
     */
    operator fun set(k: K, v: V) {
        for ((index, keyValuePair) in elements.withIndex()) {
            if (keyValuePair.first == k) {
                // replace the pair with a new pair
                elements[index] = Pair(k, v)
                return
            }
        }
        elements.add(Pair(k,v))
    }

    /**
     * Remove the key, [k], from the associative array
     * @param k the key to remove
     * @return true if the item was successfully removed and false if the element was not found
     */
    fun remove(k: K): Boolean {
        for ((index, keyValuePair) in elements.withIndex()) {
            if (keyValuePair.first == k) {
                elements.removeAt(index)
                return true
            }
        }
        return false
    }

    /**
     * @return the full list of key value pairs for the associative array
     */
    fun keyValuePairs(): List<Pair<K, V>> {
        return elements
    }
}