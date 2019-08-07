package pro.truongsinh.flutter.android_flutter_host

import android.app.Activity
import android.content.Context
import io.flutter.view.FlutterMain
import io.flutter.embedding.engine.FlutterEngine
import io.flutter.embedding.android.FlutterActivity
import io.flutter.embedding.android.FlutterEngineProvider
import android.os.Bundle
import android.content.Intent
import androidx.core.content.ContextCompat.startActivity
import io.flutter.plugin.common.BinaryMessenger
import io.flutter.plugin.common.EventChannel
import io.flutter.plugin.common.MethodChannel
import io.flutter.plugin.common.PluginRegistry
import io.flutter.plugin.platform.PlatformViewRegistry
import io.flutter.plugins.GeneratedPluginRegistrant
import io.flutter.view.TextureRegistry

class FlutterEmbeddingActivity : FlutterActivity(), FlutterEngineProvider {

    // You need to define an IntentBuilder subclass so that the
    // IntentBuilder uses FlutterEmbeddingActivity instead of a regular FlutterActivity.
    private class IntentBuilder// Override the constructor to specify your class.
    internal constructor() : FlutterActivity.IntentBuilder(FlutterEmbeddingActivity::class.java)

    private fun createPluginRegistry(messenger: BinaryMessenger, activity: Activity): PluginRegistry {
        return object : PluginRegistry {
            override fun registrarFor(s: String): PluginRegistry.Registrar {
                return object : PluginRegistry.Registrar {
                    override fun activity(): Activity {
                        return activity
                    }

                    override fun context(): Context {
                        return activity
                    }

                    override fun activeContext(): Context {
                        return activity
                    }

                    override fun messenger(): BinaryMessenger {
                        return messenger
                    }

                    override fun textures(): TextureRegistry? {
                        return null
                    }

                    override fun platformViewRegistry(): PlatformViewRegistry? {
                        return null
                    }

                    override fun view(): io.flutter.view.FlutterView? {
                        return null
                    }

                    override fun lookupKeyForAsset(s: String): String? {
                        return null
                    }

                    override fun lookupKeyForAsset(s: String, s1: String): String? {
                        return null
                    }

                    override fun publish(o: Any): PluginRegistry.Registrar {
                        return this
                    }

                    override fun addRequestPermissionsResultListener(requestPermissionsResultListener: PluginRegistry.RequestPermissionsResultListener): PluginRegistry.Registrar {
                        return this
                    }

                    override fun addActivityResultListener(activityResultListener: PluginRegistry.ActivityResultListener): PluginRegistry.Registrar {
                        return this
                    }

                    override fun addNewIntentListener(newIntentListener: PluginRegistry.NewIntentListener): PluginRegistry.Registrar {
                        return this
                    }

                    override fun addUserLeaveHintListener(userLeaveHintListener: PluginRegistry.UserLeaveHintListener): PluginRegistry.Registrar {
                        return this
                    }

                    override fun addViewDestroyListener(viewDestroyListener: PluginRegistry.ViewDestroyListener): PluginRegistry.Registrar {
                        return this
                    }
                }
            }

            override fun hasPlugin(s: String): Boolean {
                return false
            }

            override fun <T> valuePublishedByPlugin(s: String): T? {
                return null
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        init(this)
        // convert intent extras into a Map, which is then passed to Flutter's Dart side
        val intentExtras = intent.extras.keySet().associateBy({it}, {intent.extras.get(it)})
        eventChannelSink?.success(intentExtras)

        GeneratedPluginRegistrant.registerWith(createPluginRegistry(cachedFlutterEngine.dartExecutor, this))

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
    override fun provideFlutterEngine(context: Context): FlutterEngine {
        return cachedFlutterEngine
    }

    override fun retainFlutterEngineAfterHostDestruction(): Boolean {
        return true
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
