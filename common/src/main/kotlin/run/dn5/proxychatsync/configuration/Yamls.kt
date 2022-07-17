package run.dn5.proxychatsync.configuration

import com.charleskorn.kaml.YamlNode
import com.charleskorn.kaml.YamlScalar
import com.charleskorn.kaml.yamlMap
import com.charleskorn.kaml.yamlScalar

object Yamls {
    private fun getContent(node: YamlNode, key: String): String? {
        return node.yamlMap.get<YamlScalar>(key)?.yamlScalar?.content
    }
    fun getBoolean(node: YamlNode, key: String): Boolean {
        return getContent(node, key)?.toBoolean() ?: false
    }
    fun getString(node: YamlNode, key: String): String? {
        return getContent(node, key)
    }
    fun getInt(node: YamlNode, key: String): Int {
        return getContent(node, key)?.toInt() ?: 0
    }
    fun getLong(node: YamlNode, key: String): Long {
        return getContent(node, key)?.toLong() ?: 0
    }
    fun getDouble(node: YamlNode, key: String): Double {
        return getContent(node, key)?.toDouble() ?: 0.0
    }
}