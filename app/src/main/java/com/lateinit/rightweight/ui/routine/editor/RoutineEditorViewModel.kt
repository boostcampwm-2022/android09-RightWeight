package com.lateinit.rightweight.ui.routine.editor

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.lateinit.rightweight.data.repository.RoutineRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class RoutineEditorViewModel @Inject constructor(
    private val routineRepository: RoutineRepository
) : ViewModel() {

    val routineTitle = MutableLiveData<String>()
    val routineDescription = MutableLiveData<String>()

}