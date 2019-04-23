package hu.ait.cryptokeychain

import android.content.Intent
import android.os.Bundle
import android.preference.PreferenceManager
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.widget.Toast
import kotlinx.android.synthetic.main.new_user_activity.*
import java.security.SecureRandom
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.PBEKeySpec

class NewUserActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.new_user_activity)

        doneBtn.setOnClickListener{
            if (passwordEt1.text.toString() == passwordEt2.text.toString()) {
                if (passwordEt1.text.toString().length > 7){
                    passwordCreated(passwordEt1.text.toString())
                } else {
                    Toast.makeText(this, "The password must be at least 8 characters long",
                        Toast.LENGTH_LONG).show()
                }
            } else {
                Toast.makeText(this, "The two passwords must match",
                    Toast.LENGTH_LONG).show()
            }


        }
    }
    fun passwordCreated(password : String) {
        var password_hash = hash(password)
        storeHash(password_hash)

        var key_bytes = createKey(password)
        sendKey(key_bytes)

    }

    private fun createKey(password : String) : ByteArray {
        val random = SecureRandom()
        val salt = ByteArray(256)
        random.nextBytes(salt)
        storeSalt(salt)

        val pbKeySpec = PBEKeySpec(password.toCharArray(), salt, 1324, 256) // 1
        val secretKeyFactory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1") // 2
        val keyBytes = secretKeyFactory.generateSecret(pbKeySpec).encoded // 3
//        val keySpec = SecretKeySpec(keyBytes, "AES")

        return keyBytes

    }

    private fun sendKey(key : ByteArray) {
        var intentDetails = Intent()
        intentDetails.setClass(this@NewUserActivity,
            ScrollingActivity::class.java)

        intentDetails.putExtra("passwordKey", key)

        startActivity(intentDetails)
    }

    private fun storeHash(passwordHash : String) {
        var sharedPref = PreferenceManager.getDefaultSharedPreferences(this)
        var editor = sharedPref.edit()
        editor.putBoolean("HAS_PASSWORD", true)
        editor.putString("PASSWORD_HASH", passwordHash)
        editor.apply()
    }

    private fun storeSalt(salt : ByteArray) {
        val charset = Charsets.UTF_8
        var sharedPref = PreferenceManager.getDefaultSharedPreferences(this)
        var editor = sharedPref.edit()
        editor.putString("SALT", salt.toString(charset))
        editor.apply()
    }



    private fun hash(password: String) : String {
        var passwordHash = password
        for (x in 0..5500) {
            Log.d("middleHash", passwordHash)
            passwordHash = passwordHash.hashCode().toString()
        }
        return passwordHash
    }





}


