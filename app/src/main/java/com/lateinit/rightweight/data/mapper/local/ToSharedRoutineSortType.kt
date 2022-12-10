package com.lateinit.rightweight.data.mapper.local

import com.lateinit.rightweight.data.model.remote.SharedRoutineSortType
import com.lateinit.rightweight.ui.model.shared.SharedRoutineSortTypeUiModel

fun SharedRoutineSortTypeUiModel.toSharedRoutineSortType(): SharedRoutineSortType {
    return when (this) {
        SharedRoutineSortTypeUiModel.MODIFIED_DATE_FIRST -> SharedRoutineSortType.MODIFIED_DATE_FIRST
        SharedRoutineSortTypeUiModel.SHARED_COUNT_FIRST -> SharedRoutineSortType.SHARED_COUNT_FIRST
    }
}