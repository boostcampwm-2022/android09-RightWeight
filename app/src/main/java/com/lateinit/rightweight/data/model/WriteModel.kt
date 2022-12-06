package com.lateinit.rightweight.data.model

import com.lateinit.rightweight.data.remote.model.RemoteData

data class WriteRequestBody(
    val writes: List<WriteModelData>
)


data class WriteModelData(
    val update: UpdateData? = null,
    val delete: String? = null,
)

data class UpdateData(
    val name: String,
    val fields: RemoteData
){
    companion object{
        const val defaultPath = "projects/right-weight/databases/(default)/documents"
    }
}
