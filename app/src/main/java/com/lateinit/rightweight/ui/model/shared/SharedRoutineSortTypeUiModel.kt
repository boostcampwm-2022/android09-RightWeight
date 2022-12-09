package com.lateinit.rightweight.ui.model.shared

import androidx.annotation.StringRes
import com.lateinit.rightweight.R

enum class SharedRoutineSortTypeUiModel(@StringRes val sortTypeName: Int){
    MODIFIED_DATE_FIRST(R.string.modified_date_sort), SHARED_COUNT_FIRST(R.string.shared_count_sort)
}
