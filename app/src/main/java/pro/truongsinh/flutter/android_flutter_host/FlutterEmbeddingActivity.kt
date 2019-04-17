package pro.truongsinh.flutter.android_flutter_host

import android.content.Context
import io.flutter.view.FlutterMain
import io.flutter.embedding.engine.FlutterEngine
import io.flutter.embedding.android.FlutterActivity
import io.flutter.embedding.android.FlutterFragment

class FlutterEmbeddingActivity : FlutterActivity(), FlutterFragment.FlutterEngineProvider {

    // You need to define an IntentBuilder subclass so that the
    // IntentBuilder uses FlutterEmbeddingActivity instead of a regular FlutterActivity.
    private class IntentBuilder// Override the constructor to specify your class.
    internal constructor() : FlutterActivity.IntentBuilder(FlutterEmbeddingActivity::class.java)

    // This is the method where you provide your existing cachedFlutterEngine instance.
    override fun getFlutterEngine(context: Context): FlutterEngine {
        init(context)
        return cachedFlutterEngine
    }

    companion object {
        private lateinit var cachedFlutterEngine: FlutterEngine

        fun init(context: Context) {
            if (::cachedFlutterEngine.isInitialized) {
                return
            }
            // Flutter must be initialized before FlutterEngines can be created.
            FlutterMain.startInitialization(context)
            FlutterMain.ensureInitializationComplete(context, arrayOf())
            // Instantiate a cachedFlutterEngine.
            cachedFlutterEngine = FlutterEngine(context)

        }

        // This is the method that others will use to create
        // an Intent that launches MyFlutterActivity.
        fun createBuilder(): FlutterActivity.IntentBuilder {
            return IntentBuilder()
        }
    }
}
