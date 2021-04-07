package framework.component.functional

import framework.BuildConfig
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

abstract class ViewModel {

    protected lateinit var viewModelScope: CoroutineScope
    protected val _isSyncFinished = MutableStateFlow(false)
    val isSyncFinished: StateFlow<Boolean> = _isSyncFinished

    private val _shouldUpdate = MutableStateFlow(false)
    val shouldUpdate: StateFlow<Boolean> = _shouldUpdate

    // todo configure for firebase remote config
    private fun verifyConfig(config: BuildConfig): Boolean {
        val isUpdateNeeded = false // currentVersion < mandatoryVersion
        return if (isUpdateNeeded) {
            _shouldUpdate.value = true
            false
        } else {
            true
        }
    }

    open fun onRetry() {
        syncData()
    }

    fun init(defaultScope: CoroutineScope) {
        viewModelScope = defaultScope
    }

    abstract fun syncData()
}