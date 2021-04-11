import com.toxicbakery.logging.Arbor
import com.toxicbakery.logging.Seedling
import framework.Application
import framework.Timber

class ViewBindWizardApp : Application() {

    override fun onCreate() {
        // init logging
        Timber.sow(Seedling())
        Timber.i("Init Application...")
    }

}


