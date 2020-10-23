package com.dimaslanjaka.gradle.versioning

internal class VersionTokenizer(versionString: String?) {
    private val _versionString: String?
    private val _length: Int
    private var _position = 0
    private var _number = 0
    private var _suffix: String? = null
    private var _hasValue = false
    fun getNumber(): Int {
        return _number
    }

    fun getSuffix(): String? {
        return _suffix
    }

    fun hasValue(): Boolean {
        return _hasValue
    }

    fun MoveNext(): Boolean {
        _number = 0
        _suffix = ""
        _hasValue = false

        // No more characters
        if (_position >= _length) return false
        _hasValue = true
        while (_position < _length) {
            val c = _versionString!![_position]
            if (c < '0' || c > '9') break
            _number = _number * 10 + (c - '0')
            _position++
        }
        val suffixStart = _position
        while (_position < _length) {
            val c = _versionString?.get(_position)
            if (c == '.') break
            _position++
        }
        if (_versionString != null) {
            _suffix = _versionString.substring(suffixStart, _position)
        }
        if (_position < _length) _position++
        return true
    }

    init {
        requireNotNull(versionString) { "versionString is null" }
        _versionString = versionString
        _length = versionString.length
    }
}