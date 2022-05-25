package club.androidexpress.shortcut

import android.content.pm.ShortcutInfo
import android.os.Bundle
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private lateinit var shortcutWrapper: ShortcutWrapper
    private lateinit var adapter: MyAdapter
    private lateinit var shortcuts: MutableList<ShortcutInfo>

    companion object {
        private const val KEY_ADD_FAVORITE = "club.androidexpress.shortcut.ADD_FAVORITE"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        shortcutWrapper = ShortcutWrapper(this)

        if (intent.action == KEY_ADD_FAVORITE) showUrlDialog()

        setupFabButton()
        setupList()
    }



    private fun showUrlDialog() {
        val editText = EditText(this)
        AlertDialog.Builder(this)
            .setTitle(R.string.shortcut_short_label)
            .setMessage(R.string.enter_url)
            .setView(editText)
            .setPositiveButton(android.R.string.ok) { _, _ ->
                val url: String = editText.text.toString().trim()
                if (url.isNotEmpty()) {
                    shortcutWrapper.addShortcut(url) {
                        refreshList()
                    }

                } else {
                    Toast.makeText(this, "URL cannot be empty", Toast.LENGTH_SHORT).show()
                }

            }.show()

    }

    private fun setupList() {
        shortcuts = arrayListOf()
        adapter = MyAdapter(shortcuts)
        rv_main.adapter = adapter
        rv_main.layoutManager = LinearLayoutManager(this)
    }

    private fun setupFabButton() {
        fab_action.setOnClickListener {
            showUrlDialog()
        }
    }

    private fun refreshList() {
        shortcuts.clear()
        shortcuts.addAll(shortcutWrapper.getShortcuts())
        adapter.notifyDataSetChanged()
    }
    
}