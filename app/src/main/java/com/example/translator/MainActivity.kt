package com.example.translator

import android.app.ProgressDialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.translation.Translator
import android.widget.Button
import android.widget.EditText
import android.widget.PopupMenu
import android.widget.TextView
import com.google.android.material.button.MaterialButton
import com.google.mlkit.nl.translate.TranslateLanguage
import java.net.SocketOptions
import java.util.Locale

class MainActivity : AppCompatActivity() {

    private lateinit var edtSrcLang: EditText
    private lateinit var txtDstLang: TextView
    private lateinit var btnSrc: MaterialButton
    private lateinit var btnTgt: MaterialButton
    private lateinit var btnTranslate: MaterialButton

    companion object{
        private const val TAG = "MAIN_TAG"
    }

    private var languageArrayList: ArrayList<ModelLanguage>? = null

    private var sourceLanguageCode = "en"
    private var sourceLanguageTitle = "English"
    private var targetLanguageCode = "fil"
    private var targetLanguageTitle = "Filipino"


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        edtSrcLang = findViewById(R.id.edtSrcLang)
        txtDstLang = findViewById(R.id.txtDstLang)
        btnSrc = findViewById(R.id.btnSrc)
        btnTgt = findViewById(R.id.btnTgt)
        btnTranslate = findViewById(R.id.btnTranslate)


        loadAvailableLanguages()

        btnSrc.setOnClickListener{
            sourceLanguageChoose()
        }

        btnTgt.setOnClickListener{
            targetLanguageChoose()
        }

        btnTranslate.setOnClickListener{

        }


    }

    private fun loadAvailableLanguages(){

        languageArrayList = ArrayList()

        val languageCodeList = TranslateLanguage.getAllLanguages()

        for(languageCode in languageCodeList){

            val languageTitle = Locale(languageCode).displayLanguage

            Log.d(TAG, "loadAvailableLanguages: languageCode: $languageCode")
            Log.d(TAG, "loadAvailableLanguages: languageTitle: $languageTitle")

            val modelLanguage = ModelLanguage(languageCode, languageTitle)

            languageArrayList!!.add(modelLanguage)
        }
    }

    private fun sourceLanguageChoose() {
        val popupMenu = PopupMenu(this, btnSrc)

        for(i in languageArrayList!!.indices){
            popupMenu.menu.add(Menu.NONE, i, i, languageArrayList!![i].languageTitle)
        }

        popupMenu.show()

        popupMenu.setOnMenuItemClickListener {menuItem ->
            val position = menuItem.itemId

            sourceLanguageCode = languageArrayList!![position].languageCode
            sourceLanguageTitle = languageArrayList!![position].languageTitle

            btnSrc.text = sourceLanguageTitle
            edtSrcLang.hint = "Enter $sourceLanguageTitle"

            Log.d(TAG, "sourceLanguageChoose: sourceLanguageCode: $sourceLanguageCode")
            Log.d(TAG, "sourceLanguageChoose: sourceLanguageTitle: $sourceLanguageTitle")

            false
        }
    }

    private fun targetLanguageChoose() {
        val popupMenu = PopupMenu(this, btnTgt)

        for (i in languageArrayList!!.indices){

            popupMenu.menu.add(Menu.NONE, i,i, languageArrayList!![i].languageTitle)
        }

        popupMenu.show()

        popupMenu.setOnMenuItemClickListener {menuItem ->
            val position = menuItem.itemId

            targetLanguageCode = languageArrayList!![position].languageCode
            targetLanguageTitle = languageArrayList!![position].languageTitle

            btnTgt.text = targetLanguageTitle

            Log.d(TAG, "targetLanguageChoose: targetLanguageCode: $targetLanguageCode")
            Log.d(TAG, "targetLanguageChoose: targetLanguageTitle: $targetLanguageTitle")

            false
        }
    }
}