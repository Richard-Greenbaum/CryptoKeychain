package hu.ait.cryptokeychain

import android.content.Intent
import android.os.Bundle
import android.preference.PreferenceManager
import android.support.v7.app.AppCompatActivity
import kotlinx.android.synthetic.main.scrolling_activity.*
import javax.crypto.spec.SecretKeySpec

class ScrollingActivity : AppCompatActivity() {

    private lateinit var passwordKey : SecretKeySpec

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.scrolling_activity)

        if (intent.extras.containsKey("passwordKey")) {
                var byte_array = intent.getByteArrayExtra("passwordKey")
            passwordKey = SecretKeySpec(byte_array, "AES")
        }

        resetAppBtn.setOnClickListener {
            var sharedPref = PreferenceManager.getDefaultSharedPreferences(this)
            var editor = sharedPref.edit()
            editor.putBoolean("HAS_PASSWORD", false)
            editor.putString("PASSWORD_HASH", "")
            editor.apply()

            val intent = Intent(applicationContext, NewUserActivity::class.java)
            startActivity(intent)
        }


    }


}