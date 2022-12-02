package com.lateinit.rightweight.data.remote.model.sharedRoutine

import com.google.gson.annotations.SerializedName

data class SharedRoutineResponse(
    @field:SerializedName("documents")
    val documents: List<SharedRoutineItem?>? = null
)