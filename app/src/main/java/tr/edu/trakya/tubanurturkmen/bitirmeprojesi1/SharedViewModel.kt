package tr.edu.trakya.tubanurturkmen.bitirmeprojesi1

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class SharedViewModel : ViewModel() {
    private val _selectedInterests = MutableStateFlow<List<String>>(emptyList())
    val selectedInterests: StateFlow<List<String>> = _selectedInterests

    fun updateSelectedInterests(interests: List<String>) {
        _selectedInterests.value = interests
    }
}
