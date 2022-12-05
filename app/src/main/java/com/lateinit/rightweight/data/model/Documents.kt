package com.lateinit.rightweight.data.model

data class DocumentResponse<T>(val document: DetailResponse<T>)

data class DetailResponse<T> (val name: String, val fields: T)

data class DocumentsListResponse<T> (val documents: List<DetailResponse<T>>)

