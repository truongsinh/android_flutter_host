package pro.truongsinh.flutter.android_flutter_host

import android.content.Context
import io.flutter.view.FlutterMain
import io.flutter.embedding.engine.FlutterEngine
import io.flutter.embedding.android.FlutterActivity
import io.flutter.embedding.android.FlutterFragment
import android.os.Bundle
import android.content.Intent
import androidx.core.content.ContextCompat.startActivity
import io.flutter.plugin.common.EventChannel
import io.flutter.plugin.common.MethodChannel

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

        MethodChannel(cachedFlutterEngine.dartExecutor, METHOD_CHANNEL_NAME).setMethodCallHandler { call, result ->
            when {
                call.method == "navigatorPop" -> {
                    val returnIntent = Intent()
                    val args = call.arguments
                    if (args is Map<*, *>) {
                        args.keys.forEach {
                            if(it is String) {
                                when (val value = args[it]) {
                                    // see https://flutter.dev/docs/development/platform-integration/platform-channels#platform-channel-data-types-support-and-codecs
                                    null -> {}
                                    is Boolean -> returnIntent.putExtra(it, value)
                                    is Integer -> returnIntent.putExtra(it, value)
                                    is Long -> returnIntent.putExtra(it, value)
                                    is Double -> returnIntent.putExtra(it, value)
                                    is String -> returnIntent.putExtra(it, value)
                                    is ByteArray -> returnIntent.putExtra(it, value)
                                    is IntArray -> returnIntent.putExtra(it, value)
                                    is LongArray -> returnIntent.putExtra(it, value)
                                    is DoubleArray -> returnIntent.putExtra(it, value)
                                    is ArrayList<*> -> returnIntent.putExtra(it, value)
                                    is HashMap<*, *> -> returnIntent.putExtra(it, value)
                                    else -> {}
                                }

                            }
                        }
                    }
                    setResult(24, returnIntent)
                    finish()
                }
                else -> result.notImplemented()
            }
        }
    }

    // This is the method where you provide your existing cachedFlutterEngine instance.
    override fun getFlutterEngine(context: Context): FlutterEngine {
        return cachedFlutterEngine
    }

    companion object {
        private lateinit var cachedFlutterEngine: FlutterEngine
        private const val EVENT_CHANNEL_NAME = "pro.truongsinh.flutter.android_flutter_host/event"
        private const val METHOD_CHANNEL_NAME = "pro.truongsinh.flutter.android_flutter_host/method"
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
