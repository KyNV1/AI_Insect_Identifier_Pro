package com.kynv1.aiinsectidentifierpro.data.local

import android.content.SharedPreferences
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class UsageStoreTest {

    private lateinit var prefs: InMemorySharedPreferences
    private var day = "20261102"
    private lateinit var store: UsageStore

    @Before
    fun setUp() {
        prefs = InMemorySharedPreferences()
        store = UsageStore(prefs) { day }
    }

    @Test
    fun `allows exactly the free daily quota of scans`() {
        repeat(UsageStore.FREE_SCANS_PER_DAY) {
            assertTrue(store.hasFreeScanLeft())
            store.recordSuccessAndCheckReview()
        }

        assertFalse(store.hasFreeScanLeft())
    }

    @Test
    fun `restores the free quota on a new day`() {
        repeat(UsageStore.FREE_SCANS_PER_DAY) { store.recordSuccessAndCheckReview() }
        assertFalse(store.hasFreeScanLeft())

        day = "20261103"

        assertTrue(store.hasFreeScanLeft())
    }

    @Test
    fun `asks for a review only once, on the second successful scan`() {
        val results = List(4) { store.recordSuccessAndCheckReview() }

        assertEquals(listOf(false, true, false, false), results)
    }

    @Test
    fun `keeps the review flag across days`() {
        store.recordSuccessAndCheckReview()
        day = "20261105"

        assertTrue(store.recordSuccessAndCheckReview())
        day = "20261106"
        assertFalse(store.recordSuccessAndCheckReview())
    }
}

/** Minimal in-memory SharedPreferences, since the android.jar stub has no real implementation. */
private class InMemorySharedPreferences : SharedPreferences {
    private val values = mutableMapOf<String, Any?>()

    override fun getAll(): MutableMap<String, *> = values.toMutableMap()
    override fun getString(key: String, defValue: String?): String? = values[key] as String? ?: defValue
    override fun getStringSet(key: String, defValues: MutableSet<String>?): MutableSet<String>? =
        @Suppress("UNCHECKED_CAST") (values[key] as MutableSet<String>? ?: defValues)
    override fun getInt(key: String, defValue: Int): Int = values[key] as Int? ?: defValue
    override fun getLong(key: String, defValue: Long): Long = values[key] as Long? ?: defValue
    override fun getFloat(key: String, defValue: Float): Float = values[key] as Float? ?: defValue
    override fun getBoolean(key: String, defValue: Boolean): Boolean = values[key] as Boolean? ?: defValue
    override fun contains(key: String): Boolean = key in values
    override fun registerOnSharedPreferenceChangeListener(l: SharedPreferences.OnSharedPreferenceChangeListener?) = Unit
    override fun unregisterOnSharedPreferenceChangeListener(l: SharedPreferences.OnSharedPreferenceChangeListener?) = Unit

    override fun edit(): SharedPreferences.Editor = object : SharedPreferences.Editor {
        private val pending = mutableMapOf<String, Any?>()
        private var clear = false

        override fun putString(key: String, value: String?) = apply { pending[key] = value }
        override fun putStringSet(key: String, values: MutableSet<String>?) = apply { pending[key] = values }
        override fun putInt(key: String, value: Int) = apply { pending[key] = value }
        override fun putLong(key: String, value: Long) = apply { pending[key] = value }
        override fun putFloat(key: String, value: Float) = apply { pending[key] = value }
        override fun putBoolean(key: String, value: Boolean) = apply { pending[key] = value }
        override fun remove(key: String) = apply { pending[key] = null }
        override fun clear() = apply { clear = true }
        override fun commit(): Boolean {
            apply()
            return true
        }

        override fun apply() {
            if (clear) values.clear()
            pending.forEach { (k, v) -> if (v == null) values.remove(k) else values[k] = v }
        }
    }
}
