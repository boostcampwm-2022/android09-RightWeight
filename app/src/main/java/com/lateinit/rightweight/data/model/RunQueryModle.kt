package com.lateinit.rightweight.data.model

import com.lateinit.rightweight.data.remote.model.ValueData

data class RunQueryBody(
    val structuredQuery: StructuredQueryData
)

data class StructuredQueryData(
    val from: FromData,
    val where: WhereData? = null,
    val orderBy: OrderByData? = null,
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

enum class FilterOperator {
    //https://firebase.google.com/docs/firestore/reference/rest/v1/StructuredQuery#operator_1
    EQUAL,
    NOT_EQUAL,
}

data class FiledReferenceData(
    val fieldPath: String
)

data class OrderByData(
    val field: FieldData,
    val direction: String
)

data class FieldData(
    val fieldPath: String
)

data class StartAtData(
    val values: ValuesData
)

data class ValuesData(
    val timestampValue: String
)