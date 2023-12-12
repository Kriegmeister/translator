package com.example.translator

import android.app.ProgressDialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.translation.Translator
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.PopupMenu
import android.widget.TextView
import android.widget.Toast
import com.google.android.material.button.MaterialButton
import com.google.mlkit.common.model.DownloadConditions
import com.google.mlkit.nl.translate.TranslateLanguage
import com.google.mlkit.nl.translate.Translation
import com.google.mlkit.nl.translate.TranslatorOptions
import java.net.SocketOptions
import java.util.Locale

class MainActivity : AppCompatActivity() {

    private lateinit var edtSrcLang: EditText
    private lateinit var txtDstLang: TextView
    private lateinit var btnSrc: MaterialButton
    private lateinit var btnTgt: MaterialButton
    private lateinit var btnTranslate: MaterialButton
    private lateinit var btnBack: ImageButton

    companion object{
        private const val TAG = "MAIN_TAG"
    }

    private var languageArrayList: ArrayList<ModelLanguage>? = null

    private var sourceLanguageCode = "en"
    private var sourceLanguageTitle = "English"
    private var targetLanguageCode = "fil"
    private var targetLanguageTitle = "Filipino"

    private lateinit var translatorOptions: TranslatorOptions

    private lateinit var translator: Translator

    private lateinit var progressDialog: ProgressDialog


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        btnBack = findViewById(R.id.btnBack)
        edtSrcLang = findViewById(R.id.edtSrcLang)
        txtDstLang = findViewById(R.id.txtDstLang)
        btnSrc = findViewById(R.id.btnSrc)
        btnTgt = findViewById(R.id.btnTgt)
        btnTranslate = findViewById(R.id.btnTranslate)

        progressDialog = ProgressDialog(this)
        progressDialog.setTitle("Please wait")
        progressDialog.setCanceledOnTouchOutside(false)


        loadAvailableLanguages()

        btnBack.setOnClickListener{
            //put functionality for back button here
        }

        btnSrc.setOnClickListener{
            sourceLanguageChoose()
        }

        btnTgt.setOnClickListener{
            targetLanguageChoose()
        }

        btnTranslate.setOnClickListener{
            validateData()
        }


    }

    private var sourceLanguageText = ""
    private fun validateData() {
        sourceLanguageText = edtSrcLang.text.toString().trim()

        Log.d(TAG, "validateData: sourceLanguageText: $sourceLanguageText")

        if(sourceLanguageText.isEmpty()) {
            showToast("Enter text to translate...")
        }
        else{
            startTranslation()
        }
    }

    private fun startTranslation() {
        progressDialog.setMessage("Processing language model...")
        progressDialog.show()

        translatorOptions = TranslatorOptions.Builder()
            .setSourceLanguage(sourceLanguageCode)
            .setTargetLanguage(targetLanguageCode)
            .build()
        var translator = Translation.getClient(translatorOptions)

        val downloadConditions = DownloadConditions.Builder()
            .requireWifi()
            .build()

        translator.downloadModelIfNeeded(downloadConditions)
            .addOnSuccessListener {
                Log.d(TAG, "startTranslation: model ready, start translation...")

                progressDialog.setMessage("Translating...")

                translator.translate(sourceLanguageText)
                    .addOnSuccessListener { translatedText ->
                        Log.d(TAG, "startTranslation: translatedText: translatedText: $translatedText")

                        progressDialog.dismiss()

                        txtDstLang.text = translatedText
                    }
                    .addOnFailureListener {e->
                        progressDialog.dismiss()
                        Log.e(TAG, "startTranslation: ", e)

                        showToast("Failed to translate due to ${e.message}")
                    }
            }
            .addOnFailureListener{e ->
                progressDialog.dismiss()
                Log.e(TAG, "startTranslation: ", e)

                showToast("Failed to translate due to ${e.message}")
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

    private fun showToast(message: String){
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }
}