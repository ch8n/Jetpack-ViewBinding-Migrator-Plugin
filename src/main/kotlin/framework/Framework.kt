package framework

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlin.reflect.KClass
import kotlin.reflect.full.createInstance


data class BuildConfig(
    val appName: String = "ViewBinderWizard",
    val author: String = "Chetan Gupta",
    val version: String = "1.0.1",
    val versionCode: Long = 0
)

// TODO can be improved by using delegates
class Framework(
    private val application: Application
) {

    private val request = object : FrameworkContext {

        override val buildConfig: BuildConfig = BuildConfig()

        override fun <T : Activity> startActivity(intent: Intent<T>) {
            this@Framework.startActivity(intent)
        }

        override fun finish(activity: Activity) {
            this@Framework.destroyActivity(activity)
        }

    }

    fun <T : Activity> init(initActivity: KClass<T>) {
        application.onCreate()
        startActivity(Intent(initActivity))
    }

    private val activityManager = ActivityManager(request)

    fun <T : Activity> startActivity(intent: Intent<T>) {
        activityManager.createActivity(intent)
    }

    fun pauseActivity() {
        activityManager.pauseVisibleActivity()
    }

    fun restartActivity() {
        activityManager.restartVisibleActivity()
    }

    fun destroyActivity(activity: Activity) {
        activityManager.destroyActivity(activity)
    }

    fun murderAll() {
        activityManager.evictAll()
        application.onDestroy()
    }
}

interface FrameworkContext {
    val buildConfig: BuildConfig
    fun <T : Activity> startActivity(intent: Intent<T>)
    fun finish(activity: Activity)
}


abstract class Application() {

    abstract fun onCreate()

    open fun onDestroy() {

    }
}

class Bundle {
    private val store by lazy { mutableMapOf<String, Any>() }

    @Suppress("UNCHECKED_CAST")
    fun <T> getExtra(key: String): T? = store[key] as? T
    fun putExtra(key: String, value: Any) = store.put(key, value)
    fun clear() = store.clear()
}

abstract class Lifecycle {

    private val _lifecycleState = MutableStateFlow<LifecycleState>(LifecycleState.NonExist)
    val lifecycleState: StateFlow<LifecycleState> = _lifecycleState.asStateFlow()

    open fun onCreate() {
        _lifecycleState.value = LifecycleState.Created
        onStart()
    }

    open fun onRestart() {
        onStart()
    }

    open fun onStart() {
        _lifecycleState.value = LifecycleState.Started
        onResume()
    }

    open fun onResume() {
        _lifecycleState.value = LifecycleState.Resumed
    }

    open fun onPause() {
        _lifecycleState.value = LifecycleState.Paused
        onStop()
    }

    open fun onStop() {
        _lifecycleState.value = LifecycleState.Stopped
    }

    open fun onDestroy() {
        _lifecycleState.value = LifecycleState.NonExist
    }

}

sealed class LifecycleState {
    object NonExist : LifecycleState()
    object Created : LifecycleState()
    object Started : LifecycleState()
    object Resumed : LifecycleState()
    object Paused : LifecycleState()
    object Stopped : LifecycleState()
}

abstract class Activity : Lifecycle() {
    var extra: Bundle? = null
    var frameworkContext: FrameworkContext? = null

    fun <T : Activity> startActivity(intent: Intent<T>) {
        requireNotNull(frameworkContext).startActivity(intent)
    }

    fun finish() {
        requireNotNull(frameworkContext).finish(this)
    }

}


class Intent<T : Activity>(val targetActivity: KClass<T>) {
    val bundle: Bundle = Bundle()

    fun putExtra(key: String, value: Any) {
        bundle.putExtra(key, value)
    }
}


class ActivityManager(private val context: FrameworkContext) {
    private val _backStack = mutableListOf<Activity>()
    val backStack = _backStack.toList()

    val visibleActivity: Activity
        get() = _backStack.last()

    fun <T : Activity> createActivity(intent: Intent<T>) {
        val activity: Activity = intent.targetActivity.createInstance()
        pauseVisibleActivity()
        _backStack.add(activity)
        activity.frameworkContext = context
        activity.extra = intent.bundle
        activity.onCreate()
    }

    fun destroyActivity(activity: Activity) {
        activity.extra = null
        activity.frameworkContext = null
        val state = activity.lifecycleState.value
        when (state) {
            LifecycleState.NonExist -> {
                /*doNothing*/
            }
            LifecycleState.Created -> {
                activity.onDestroy()
            }
            LifecycleState.Started -> {
                activity.onStop()
            }
            LifecycleState.Resumed -> {
                activity.onPause()
            }
            LifecycleState.Paused -> {
                activity.onStop()
            }
            LifecycleState.Stopped -> {
                activity.onDestroy()
            }
        }
        _backStack.remove(activity)
    }

    fun evictAll() {
        _backStack.forEach { destroyActivity(it) }
        _backStack.clear()
    }

    fun pauseVisibleActivity() {
        visibleActivity.onPause()
    }

    fun restartVisibleActivity() {
        visibleActivity.onRestart()
    }
}