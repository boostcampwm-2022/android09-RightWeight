package com.lateinit.rightweight.data.model

import com.google.gson.annotations.SerializedName
import com.lateinit.rightweight.data.remote.model.IntValue
import com.lateinit.rightweight.data.remote.model.RemoteData

data class WriteRequestBody(
    val writes: List<WriteModelData>
)


data class WriteModelData(
    val transform: TransformData? = null,
    val update: UpdateData? = null,
    val delete: String? = null,
){
    companion object{
        const val defaultPath = "projects/right-weight/databases/(default)/documents"
    }
}

data class UpdateData(
    val name: String,
    val fields: RemoteData
)

data class TransformData(
    val document: String,
    val fieldTransforms: List<FieldTransformsModelData>
)

data class FieldTransformsModelData(
    val fieldPath: String,
    val increment: IntValue
)