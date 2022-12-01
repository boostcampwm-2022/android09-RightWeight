package com.lateinit.rightweight.data.model

data class DocumentResponse<T> (val name: String, val fields: T)