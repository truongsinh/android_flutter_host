package pro.truongsinh.flutter.android_flutter_host

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem

import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        FlutterEmbeddingActivity.init(this)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        fab.setOnClickListener {
            val flutterEmbeddingActivityIntent = FlutterEmbeddingActivity.createBuilder()
                .initialRoute("counter")
                .build(this)
            startActivity(flutterEmbeddingActivityIntent)
        }
        anotherFlutterRouteButton.setOnClickListener {
            val flutterEmbeddingActivityIntent = FlutterEmbeddingActivity.createBuilder()
                .initialRoute("anotherRoute")
                .build(this)
                .putExtra("route_args", hashMapOf("arg1Key" to "arg1Value", "arg2Key" to 2))
            startActivityForResult(flutterEmbeddingActivityIntent, 42)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, intent: Intent?) {
        super.onActivityResult(requestCode, resultCode, intent)
        val data = intent?.extras?.keySet()?.associateBy({it}, {intent.extras.get(it)})
        Log.d("Flutter example", "requestCode: $requestCode, resultCode: $resultCode, data $data")
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
        }
    }
}
