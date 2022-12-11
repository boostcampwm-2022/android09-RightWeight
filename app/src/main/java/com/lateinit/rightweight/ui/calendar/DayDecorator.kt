package com.lateinit.rightweight.ui.calendar

import android.content.Context
import android.text.style.TextAppearanceSpan
import androidx.appcompat.content.res.AppCompatResources
import com.lateinit.rightweight.R
import com.prolificinteractive.materialcalendarview.CalendarDay
import com.prolificinteractive.materialcalendarview.DayViewDecorator
import com.prolificinteractive.materialcalendarview.DayViewFacade

class DayDecorator(context: Context) : DayViewDecorator {

    private val selectorDrawable =
        AppCompatResources.getDrawable(context, R.drawable.bg_calendar_day)
    private val textAppearanceSpan = TextAppearanceSpan(context, R.style.DayTextAppearance)

    override fun shouldDecorate(day: CalendarDay?): Boolean {
        return true
    }

    override fun decorate(view: DayViewFacade?) {
        selectorDrawable ?: return
        view?.apply {
            setSelectionDrawable(selectorDrawable)
            addSpan(textAppearanceSpan)
        }
    }
}