package club.androidexpress.shortcut

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.ShortcutInfo
import android.content.pm.ShortcutManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.Icon
import android.net.Uri
import android.os.AsyncTask
import android.os.PersistableBundle
import java.io.BufferedInputStream
import java.io.IOException
import java.io.InputStream
import java.net.URL
import java.net.URLConnection

class ShortcutWrapper(private val context: Context) {

    private var shortManager: ShortcutManager = context.getSystemService(ShortcutManager::class.java)

    fun addShortcut(url: String, onUriAdded: () -> Unit) {
        InsertShortcutTask.run(shortManager, context, url, onUriAdded)
    }

    fun getShortcuts(): Collection<ShortcutInfo> = shortManager.dynamicShortcuts.filterNot { it.isImmutable }
}

private class InsertShortcutTask(
    private val shortcutManager: ShortcutManager,
    private val context: Context,
    private val onUriAdded: () -> Unit
) : AsyncTask<String, Void, ShortcutInfo>() {

    companion object {
        fun run(shortcutManager: ShortcutManager, context: Context, url: String, onUriAdded: () -> Unit): InsertShortcutTask =
            InsertShortcutTask(shortcutManager, context, onUriAdded).apply { execute(url) }
    }

    override fun doInBackground(vararg urls: String?): ShortcutInfo {
        val url: String? = urls.first()
        val uri: Uri = Uri.parse(url)
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

        return ShortcutInfo.Builder(context, url)
            .setShortLabel(uri.host!!)
            .setLongLabel(uri.toString())
            .setIntent(Intent(Intent.ACTION_VIEW, uri))
            .setExtras(PersistableBundle().apply {
                putLong("refresh", System.currentTimeMillis())
            })
            .setIcon(icon)
            .build()
    }

    @SuppressLint("WrongThread")
    override fun onPostExecute(result: ShortcutInfo?) {
        super.onPostExecute(result)
        shortcutManager.addDynamicShortcuts(arrayListOf(result))
        onUriAdded.invoke()
    }
}


