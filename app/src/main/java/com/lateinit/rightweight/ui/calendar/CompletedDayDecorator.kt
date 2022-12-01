package com.lateinit.rightweight.ui.calendar

import android.content.Context
import androidx.appcompat.content.res.AppCompatResources
import com.lateinit.rightweight.R
import com.lateinit.rightweight.data.database.intermediate.HistoryWithHistoryExercises
import com.prolificinteractive.materialcalendarview.CalendarDay
import com.prolificinteractive.materialcalendarview.DayViewDecorator
import com.prolificinteractive.materialcalendarview.DayViewFacade
import java.time.LocalDate

class CompletedDayDecorator(context: Context) : DayViewDecorator {

    private var completedHistories: Map<LocalDate, HistoryWithHistoryExercises> = mapOf()
    private val completeHistoryDrawable =
        AppCompatResources.getDrawable(context, R.drawable.bg_calendar_day_completed)

    override fun shouldDecorate(day: CalendarDay?): Boolean {
        return day?.date in completedHistories
    }

    override fun decorate(view: DayViewFacade?) {
        completeHistoryDrawable ?: return
        view?.setBackgroundDrawable(completeHistoryDrawable)
    }

    fun changeCompletedHistories(completedHistories: Map<LocalDate, HistoryWithHistoryExercises>) {
        this.completedHistories = completedHistories
    }
}