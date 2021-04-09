package framework.component.functional

import framework.BuildConfig
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

// todo make lifecycle aware
abstract class ViewModel {

    protected lateinit var viewModelScope: CoroutineScope

    fun init(defaultScope: CoroutineScope) {
        viewModelScope = defaultScope
    }
}