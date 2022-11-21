package com.lateinit.rightweight.ui.routine.editor

import com.lateinit.rightweight.data.ExercisePartType

class RoutineDayAdapter(val routineEventListener: RoutineEventListener) {

    interface RoutineEventListener {

        fun onDayAdd()

        fun onDayRemove(position: Int)

        fun onDayMoveUp(position: Int)

        fun onDayMoveDown(position: Int)

        fun onExerciseAdd(position: Int)

        fun onExerciseRemove(dayId: String, position: Int)

        fun onExercisePartChange(dayId: String, position: Int, exercisePartType: ExercisePartType)

        fun onSetAdd(exerciseId: String)

        fun onSetRemove(exerciseId: String, position: Int)
    }
}