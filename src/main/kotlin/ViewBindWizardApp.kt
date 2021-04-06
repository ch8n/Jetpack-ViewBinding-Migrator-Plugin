import framework.Application
import framework.Framework
import ui.main.MainActivity


class ViewBindWizardApp : Application() {

    override fun onCreate() {

    }

}


fun main() {
    Framework(ViewBindWizardApp()).init(MainActivity::class)
}
