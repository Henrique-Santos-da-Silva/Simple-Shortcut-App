package club.androidexpress.shortcut

import android.content.Context
import android.content.Intent
import android.content.pm.ShortcutInfo
import android.content.pm.ShortcutManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.Icon
import android.net.Uri
import android.os.PersistableBundle
import kotlinx.coroutines.*
import java.io.BufferedInputStream
import java.io.IOException
import java.io.InputStream
import java.net.URL
import java.net.URLConnection
import kotlin.coroutines.CoroutineContext

class ShortcutWrapper(private val context: Context): CoroutineScope {

    private lateinit var job: Job
    private var downloadJob: Job? = null

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + job

    private var shortManager: ShortcutManager = context.getSystemService(ShortcutManager::class.java)

    fun jobInitialInstance() {
        job = Job()
    }

    fun cancelJobInstance() {
        job.cancel()
    }


    fun addShortcut(url: String, onUriAdded: () -> Unit) {
        val uri: Uri = Uri.parse(url)

        downloadJob = launch {
            val result: Icon = withContext(Dispatchers.IO) {
                downloadFaviconAndSaveUrl(uri)
            }

            val shortcutInfo: ShortcutInfo = ShortcutInfo.Builder(context, url)
                .setShortLabel(uri.host!!)
                .setLongLabel(uri.toString())
                .setIntent(Intent(Intent.ACTION_VIEW, uri))
                .setExtras(PersistableBundle().apply {
                    putLong("refresh", System.currentTimeMillis())
                })
                .setIcon(result)
                .build()

            shortManager.addDynamicShortcuts(arrayListOf(shortcutInfo))
            onUriAdded.invoke()

        }
    }

    fun getShortcuts(): Collection<ShortcutInfo> = shortManager.dynamicShortcuts.filterNot { it.isImmutable }

    private fun downloadFaviconAndSaveUrl(uri: Uri): Icon {
        val icon: Icon = try {
            val iconUri: Uri = uri.buildUpon().path("favicon.ico").build()
            val conn: URLConnection = URL(iconUri.toString()).openConnection()
            conn.connect()
            val stream: InputStream = conn.getInputStream()
            val bis = BufferedInputStream(stream, 8192)
            val bitmap: Bitmap = BitmapFactory.decodeStream(bis)

            Icon.createWithBitmap(bitmap)

        } catch (e: IOException) {
            Icon.createWithResource(context, R.drawable.ic_launcher_background)
        }

        return icon
    }
}
