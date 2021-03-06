package com.itangcent.common.utils


import com.itangcent.common.logger.ILogger
import com.itangcent.common.logger.ILoggerProvider
import com.itangcent.common.spi.SpiUtils
import java.util.*
import kotlin.collections.HashMap

open class KV<K, V> : LinkedHashMap<K, V>() {

    open operator fun set(key: K, value: V): KV<K, V> {
        super.put(key, value)
        return this
    }

    fun set(map: Map<K, V>): KV<K, V> {
        super.putAll(map)
        return this
    }

    fun set(KV: KV<K, V>): KV<K, V> {
        super.putAll(KV)
        return this
    }

    fun delete(key: K): KV<K, V> {
        super.remove(key)
        return this
    }

    @Suppress("UNCHECKED_CAST")
    fun <T> getAs(key: K): T {
        return get(key) as T
    }

    override fun equals(other: Any?): Boolean {
        return other is KV<*, *> && super.equals(other)
    }

    override fun hashCode(): Int {
        return super.hashCode().xor(1)
    }

    override fun clone(): KV<K, V> {
        return create<K, V>().set(this)
    }

    companion object {

        fun <K, V> by(key: K, value: V): KV<K, V> {
            val kv: KV<K, V> = KV()
            return kv.set(key, value)
        }

        fun <K, V> create(): KV<K, V> {
            return KV()
        }

        fun any(): KV<Any?, Any?> {
            return KV()
        }
    }
}

@Suppress("UNCHECKED_CAST")
inline fun <reified T> Map<*, *>.getAs(key: Any?): T? {
    return this[key] as? T
}

@Suppress("UNCHECKED_CAST")
inline fun <reified T> Map<*, *>.getAs(key: Any?, subKey: Any?): T? {
    return this.getAs<Map<*, *>>(key)?.getAs(subKey)
}

@Suppress("UNCHECKED_CAST")
inline fun <reified T> Map<*, *>.getAs(key: Any?, subKey: Any?, grandKey: Any?): T? {
    return this.getAs<Map<*, *>>(key)?.getAs<Map<*, *>>(subKey)?.getAs(grandKey)
}

@Suppress("UNCHECKED_CAST")
fun Map<*, *>.sub(key: String): Map<String, Any?> {
    return this[key].asMap {
        (this as? MutableMap<String, Any?>)?.set(key, it)
    }
}

@Suppress("UNCHECKED_CAST")
fun KV<*, *>.getAsKv(key: String): KV<String, Any?>? {
    return this[key]?.asKV()
}

@Suppress("UNCHECKED_CAST")
fun KV<*, *>.sub(key: String): KV<String, Any?> {
    return this[key].asKV {
        (this as KV<String, Any?>)[key] = it
    }
}

@Suppress("UNCHECKED_CAST")
fun <K, V> Map<out K, V>.mutable(copy: Boolean = false): MutableMap<K, V> {
    if (!copy && this is MutableMap) {
        return this as MutableMap<K, V>
    }
    return LinkedHashMap(this)
}

@Suppress("UNCHECKED_CAST")
fun Any?.asKV(onCastFailed: (KV<String, Any?>) -> Unit = {}): KV<String, Any?> {
    if (this == null) {
        val ret = KV.create<String, Any?>()
        onCastFailed(ret)
        return ret
    }
    if (this is KV<*, *>) {
        return this as KV<String, Any?>
    }
    if (this is Map<*, *>) {
        return KV<String, Any?>().set(this as Map<String, Any?>)
    }
    LOG?.warn("can not cast ${GsonUtils.toJson(this)} as KV")
    val ret = KV.create<String, Any?>()
    onCastFailed(ret)
    return ret
}

@Suppress("UNCHECKED_CAST")
fun Any?.asMap(onCastFailed: (Map<String, Any?>) -> Unit = {}): Map<String, Any?> {
    if (this == null) {
        val ret = HashMap<String, Any?>()
        onCastFailed(ret)
        return ret
    }
    if (this is Map<*, *>) {
        return this as Map<String, Any?>
    }
    LOG?.warn("can not cast ${GsonUtils.toJson(this)} as KV")
    val ret = HashMap<String, Any?>()
    onCastFailed(ret)
    return ret
}

private val LOG: ILogger? = SpiUtils.loadService(ILoggerProvider::class)?.getLogger(KV::class)