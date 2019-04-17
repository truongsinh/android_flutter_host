package pro.truongsinh.flutter.android_flutter_host

import android.content.Context
import io.flutter.view.FlutterMain
import io.flutter.embedding.engine.FlutterEngine
import io.flutter.embedding.android.FlutterActivity
import io.flutter.embedding.android.FlutterFragment
import android.os.Bundle
import androidx.core.content.ContextCompat.startActivity
import io.flutter.plugin.common.EventChannel

class FlutterEmbeddingActivity : FlutterActivity(), FlutterFragment.FlutterEngineProvider {

    // You need to define an IntentBuilder subclass so that the
    // IntentBuilder uses FlutterEmbeddingActivity instead of a regular FlutterActivity.
    private class IntentBuilder// Override the constructor to specify your class.
    internal constructor() : FlutterActivity.IntentBuilder(FlutterEmbeddingActivity::class.java)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        init(this)
        val intentExtras = intent.extras.keySet().associateBy({it}, {intent.extras.get(it)})
        eventChannelSink?.success(intentExtras)
    }

    // This is the method where you provide your existing cachedFlutterEngine instance.
    override fun getFlutterEngine(context: Context): FlutterEngine {
        return cachedFlutterEngine
    }

    companion object {
        private lateinit var cachedFlutterEngine: FlutterEngine
        private const val EVENT_CHANNEL_NAME = "pro.truongsinh.flutter.android_flutter_host/event"
        private var eventChannelSink: EventChannel.EventSink? = null

        fun init(context: Context) {
            if (::cachedFlutterEngine.isInitialized) {
                return
            }
            // Flutter must be initialized before FlutterEngines can be created.
            FlutterMain.startInitialization(context)
            FlutterMain.ensureInitializationComplete(context, arrayOf())
            // Instantiate a cachedFlutterEngine.
            cachedFlutterEngine = FlutterEngine(context)
            val eventChannel = EventChannel(cachedFlutterEngine.dartExecutor, EVENT_CHANNEL_NAME)
            eventChannel.setStreamHandler(object : EventChannel.StreamHandler {
                override fun onListen(o: Any?, eventSink: EventChannel.EventSink) {
                    eventChannelSink = eventSink
                }
                override fun onCancel(o: Any?) {
                }
            })
            if (context !is FlutterEmbeddingActivity) {
                val flutterEmbeddingActivityIntent = FlutterEmbeddingActivity.createBuilder()
                    .initialRoute("init")
                    .build(context)
                startActivity(context, flutterEmbeddingActivityIntent, null)
            }
        }

        // This is the method that others will use to create
        // an Intent that launches MyFlutterActivity.
        fun createBuilder(): FlutterActivity.IntentBuilder {
            return IntentBuilder()
        }
    }
}
