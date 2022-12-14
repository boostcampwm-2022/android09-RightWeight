package com.lateinit.rightweight.data.model.remote

import com.lateinit.rightweight.data.remote.model.ValueData

data class RunQueryBody(
    val structuredQuery: StructuredQueryData
)

data class StructuredQueryData(
    val from: FromData,
    val where: WhereData? = null,
    val orderBy: List<OrderByData>? = null,
    val limit: Int? = null,
    val startAt: StartAtData? = null
)

data class FromData(
    val collectionId: String,
)

data class WhereData(
    val fieldFilter: FilterData
)

data class FilterData(
    val field: FiledReferenceData,
    val op: String,
    val value: ValueData
)

data class FiledReferenceData(
    val fieldPath: String
)

data class OrderByData(
    val field: FiledReferenceData,
    val direction: String
)

data class StartAtData(
    val values: List<ValueData>
)

enum class SharedRoutineSortType{
    MODIFIED_DATE_FIRST,
    SHARED_COUNT_FIRST
}

enum class FilterOperator {
    //https://firebase.google.com/docs/firestore/reference/rest/v1/StructuredQuery#operator_1
    EQUAL,
    NOT_EQUAL,
}

enum class Direction{
    ASCENDING,
    DESCENDING
}