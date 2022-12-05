package com.lateinit.rightweight.data.model

data class DocumentResponse<T>(val document: DetailResponse<T>?)

data class DocumentsResponse<T>(val documents: List<DetailResponse<T>>?)

data class DetailResponse<T> (val name: String, val fields: T)
