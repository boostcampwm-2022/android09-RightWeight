package com.lateinit.rightweight.data.mapper

import com.lateinit.rightweight.data.database.entity.SharedRoutineDay
import com.lateinit.rightweight.data.database.entity.SharedRoutineExercise
import com.lateinit.rightweight.data.database.entity.SharedRoutineExerciseSet
import com.lateinit.rightweight.data.model.local.ExercisePartType
import com.lateinit.rightweight.data.model.remote.DetailResponse
import com.lateinit.rightweight.data.remote.model.DayField
import com.lateinit.rightweight.data.remote.model.ExerciseField
import com.lateinit.rightweight.data.remote.model.ExerciseSetField

fun DetailResponse<DayField>.toSharedRoutineDay(): SharedRoutineDay {
    val splitedName = name.split("/")
    return SharedRoutineDay(
        routineId = fields.routineId.value.toString(),
        dayId = splitedName.last(),
        order = fields.order.value.toString().toInt()
    )
}

fun DetailResponse<ExerciseField>.toSharedRoutineExercise(): SharedRoutineExercise {
    val splitedName = name.split("/")
    return SharedRoutineExercise(
        dayId = fields.dayId.value.toString(),
        exerciseId = splitedName.last(),
        title = fields.title.value.toString(),
        order = fields.order.value.toString().toInt(),
        part = ExercisePartType.valueOf(fields.partType.value.toString())
    )
}

fun DetailResponse<ExerciseSetField>.toSharedRoutineExerciseSet(): SharedRoutineExerciseSet {
    val splitedName = name.split("/")
    return SharedRoutineExerciseSet(
        exerciseId = fields.exerciseId.value.toString(),
        setId = splitedName.last(),
        weight = fields.weight.value.toString(),
        count = fields.count.value.toString(),
        order = fields.order.value.toString().toInt()
    )
}