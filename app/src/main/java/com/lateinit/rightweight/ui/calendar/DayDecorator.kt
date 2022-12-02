package com.lateinit.rightweight.ui.calendar

import android.content.Context
import androidx.appcompat.content.res.AppCompatResources
import com.lateinit.rightweight.R
import com.prolificinteractive.materialcalendarview.CalendarDay
import com.prolificinteractive.materialcalendarview.DayViewDecorator
import com.prolificinteractive.materialcalendarview.DayViewFacade

class DayDecorator(context: Context) : DayViewDecorator {

    private val selectorDrawable =
        AppCompatResources.getDrawable(context, R.drawable.bg_calendar_day)

    override fun shouldDecorate(day: CalendarDay?): Boolean {
        return true
    }

    override fun decorate(view: DayViewFacade?) {
        selectorDrawable ?: return
        view?.setSelectionDrawable(selectorDrawable)
    }
}