package hu.ait.cryptokeychain

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.support.v7.app.AlertDialog
import android.util.Log
import android.widget.*
import kotlinx.android.synthetic.main.new_user_activity.*
import kotlinx.android.synthetic.main.password_generate_dialogue.*
import kotlinx.android.synthetic.main.password_generate_dialogue.view.*

class PassGenDialogue : DialogFragment() {

    private lateinit var upperLetterCB: CheckBox
    private lateinit var numberCB: CheckBox
    private lateinit var symbolCB: CheckBox
    private lateinit var generatePasswordBtn2: Button
    private lateinit var lengthPicker: NumberPicker




    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(requireContext())

        builder.setTitle("Password Generator")

        val rootView = requireActivity().layoutInflater.inflate(
            R.layout.password_generate_dialogue, null
        )
        upperLetterCB = rootView.uppercaseLetterCb
        numberCB = rootView.numbersCb
        symbolCB = rootView.symbolsCb
        generatePasswordBtn2 = rootView.generatePasswordBtn2
        lengthPicker = rootView.lengthPicker

        builder.setView(rootView)

        lengthPicker.setMinValue(8)
        lengthPicker.setMaxValue(12)
        lengthPicker.setWrapSelectorWheel(false)


        generatePasswordBtn2.setOnClickListener {
            generatePassword()
        }

        return builder.create()
    }

    fun generatePassword() {
        var base = arrayOf("a","b","c","d","e","f","g","h","i","j","k","l","m","n","o","p","q","r","s","t","u","v","w","x","y","z")
        val upper = arrayOf("A","B","C","D","E","F","G","H","I","J","K","L","M","N","O","P","Q","R","S","T","U","V","W","X","Y","Z")
        val num = arrayOf("0","1","2","3","4","5","6","7","8","9")
        val symbol = arrayOf("`","~",",","<",".",">","/","?",";",":","\\","|","[","{","]","}","!","@","#","$","%","^","&","*","(",")","-","_","=","+", "\"", "'")

        if (upperLetterCB.isChecked) {
            base += upper
        }
        if (numberCB.isChecked) {
            base += num
        }
        if (symbolCB.isChecked) {
            base += symbol
        }

        var password = ""
        for (i in 0..lengthPicker.value-1) {
            Log.d("num", i.toString())

            var index = (0..base.size-1).random()
            password += base.get(index)
        }

        (context as NewUserActivity).setPassword(password)

        dialog.dismiss()
    }
}

