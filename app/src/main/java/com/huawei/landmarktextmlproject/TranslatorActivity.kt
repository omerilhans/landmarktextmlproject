package com.huawei.landmarktextmlproject

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import com.huawei.hmf.tasks.Task
import com.huawei.hms.mlsdk.langdetect.MLLangDetectorFactory
import com.huawei.hms.mlsdk.langdetect.cloud.MLRemoteLangDetector
import com.huawei.hms.mlsdk.translate.MLTranslatorFactory
import com.huawei.hms.mlsdk.translate.cloud.MLRemoteTranslateSetting
import com.huawei.hms.mlsdk.translate.cloud.MLRemoteTranslator
import kotlinx.android.synthetic.main.activity_translator.*


class TranslatorActivity : AppCompatActivity() ,
AdapterView.OnItemSelectedListener  {
    val country = arrayOf("Chinese", "English", "French", "Arabic", "Thai","Spanish","Turkish","Portuguese","Japanese","German","Italian","Russian")
    var spinner: Spinner ?= null
    companion object{
        var countryName = "France"
        var sourceLangCode="en"
        var targetLangCode="zh"
        var setting = MLRemoteTranslateSetting.Factory() // Set the source language code. The ISO 639-1 standard is used. This parameter is optional. If this parameter is not set, the system automatically detects the language.
                .setSourceLangCode(sourceLangCode) // Set the target language code. The ISO 639-1 standard is used.
                .setTargetLangCode(targetLangCode)
                .create()
        var mlRemoteTranslator:MLRemoteTranslator?=null
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_translator)
        spinner = findViewById(R.id.spinner)
        var landmarkImageView=findViewById<ImageView>(R.id.landmark_image)

        spinner?.onItemSelectedListener = this

        val aa: ArrayAdapter<*> = ArrayAdapter<Any?>(this, android.R.layout.simple_spinner_item, country)
        aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner?.adapter = aa

        mlRemoteTranslator = MLTranslatorFactory.getInstance().getRemoteTranslator(setting)


        translate_button.setOnClickListener {
            val mlRemoteLangDetect: MLRemoteLangDetector = MLLangDetectorFactory.getInstance()
                    .remoteLangDetector
            val firstBestDetectTask: Task<String> = mlRemoteLangDetect.firstBestDetect(transcription_text.text.toString())
            firstBestDetectTask.addOnSuccessListener {
                println("mcmc lang detect is -> $it")
                sourceLangCode=it
                checkLanguageCodeExist("")
                translate()
                // Processing logic for detection success.
            }.addOnFailureListener {
                // Processing logic for detection failure.
            }
        }

        transcription_text.addTextChangedListener(object : TextWatcher {

            override fun afterTextChanged(s: Editable) {

            }

            override fun beforeTextChanged(s: CharSequence, start: Int,
                                           count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence, start: Int,
                                       before: Int, count: Int) {


            }
        })
        checkLanguageCodeExist(countryName)
        setOpenActivityScaleAnimation(landmarkImageView)

        var landmarkImageHeight=0
        landmarkImageView.viewTreeObserver.addOnGlobalLayoutListener {
            landmarkImageHeight = landmarkImageView.height
            val lp = ConstraintLayout.LayoutParams(ConstraintLayout.LayoutParams.MATCH_PARENT, ConstraintLayout.LayoutParams.MATCH_PARENT)
            lp.setMargins(8, landmarkImageHeight-80, 8, 0)
            translator_constraint.layoutParams = lp
        }
    }

    override fun onNothingSelected(p0: AdapterView<*>?) {

    }

    private fun translate(){
     //   Toast.makeText(this,"1- $sourceLangCode 2-$targetLangCode",Toast.LENGTH_LONG).show()
        // sourceText indicates the text to be translated, which cannot exceed 5000 characters.
        val task = mlRemoteTranslator?.asyncTranslate(transcription_text.text.toString())
        task?.addOnSuccessListener {
            text -> println("mcmcmc translation is ->$text")
            translator_text.text=text
        }
                ?.addOnFailureListener { e -> println("mcmcmc translation error is ->" + e.message) }
    }

    override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
        textView6.text=country[p2]
        setting=null
        when {
            country[p2]=="Chinese" -> {
                targetLangCode="zh"
            }
            country[p2]=="Russian" -> {
                targetLangCode="ru"
            }
            country[p2]=="Turkish" -> {
                targetLangCode="tr"
            }
            country[p2]=="Portuguese" -> {
                targetLangCode="pt"
            }
            country[p2]=="Japanese" -> {
                targetLangCode="ja"
            }
            country[p2]=="German" -> {
                targetLangCode="de"
            }
            country[p2]=="Italian" -> {
                targetLangCode="it"
            }
            country[p2]=="English" -> {
                targetLangCode="en"
            }
            country[p2]=="French" -> {
                targetLangCode="fr"
            }
            country[p2]=="Arabic" -> {
                targetLangCode="ar"
            }
            country[p2]=="Thai" -> {
                targetLangCode="th"
            }
            country[p2]=="Spanish" -> {
                targetLangCode="es"
            }
        }
        setting = MLRemoteTranslateSetting.Factory() // Set the source language code. The ISO 639-1 standard is used. This parameter is optional. If this parameter is not set, the system automatically detects the language.
                .setSourceLangCode(sourceLangCode) // Set the target language code. The ISO 639-1 standard is used.
                .setTargetLangCode(targetLangCode)
                .create()
        mlRemoteTranslator = MLTranslatorFactory.getInstance().getRemoteTranslator(setting)
    }

    fun checkLanguageCodeExist(countryName:String){
        setting=null
        when (countryName) {
            "China" -> {
                targetLangCode="zh"
                spinner?.setSelection(0)
            }
            "England" -> {
                targetLangCode="en"
                spinner?.setSelection(1)
            }
            "France" -> {
                targetLangCode="fr"
                spinner?.setSelection(2)
            }
            "Arabic" -> {
                targetLangCode="ar"
                spinner?.setSelection(3)
            }
            "Thailand" -> {
                targetLangCode="th"
                spinner?.setSelection(4)
            }
            "Russia" -> {
                targetLangCode="ru"
                spinner?.setSelection(5)
            }
            "Turkey" -> {
                targetLangCode="tr"
                spinner?.setSelection(6)
            }
            "Portugal" -> {
                targetLangCode="pt"
                spinner?.setSelection(7)
            }
            "Japan" -> {
                targetLangCode="ja"
                spinner?.setSelection(8)
            }
            "Germany" -> {
                targetLangCode="de"
                spinner?.setSelection(9)
            }
            "Italy" -> {
                targetLangCode="it"
                spinner?.setSelection(10)
            }
            "Spain" -> {
                targetLangCode="es"
                spinner?.setSelection(11)
            }
        }
        setting = MLRemoteTranslateSetting.Factory() // Set the source language code. The ISO 639-1 standard is used. This parameter is optional. If this parameter is not set, the system automatically detects the language.
                .setSourceLangCode(sourceLangCode) // Set the target language code. The ISO 639-1 standard is used.
                .setTargetLangCode(targetLangCode)
                .create()
        mlRemoteTranslator = MLTranslatorFactory.getInstance().getRemoteTranslator(setting)
    }
    private fun setOpenActivityScaleAnimation(view:View) {
        var fromMarginTop: Int
        val locs = IntArray(2)
        view.getLocationInWindow(locs)
        fromMarginTop = locs.last()
        val locs2 = IntArray(2)
        mainLayout!!.getLocationOnScreen(locs2)
        //val rootLayoutTopMargin = locs2.last()
        val locs3 = IntArray(2)
        val statusBarHeight = locs3.last()
        fromMarginTop -= statusBarHeight
        if (Math.abs(fromMarginTop) != -1) {
            mainLayout?.alpha = 0f
            val anim = mainLayout?.animate()!!.alpha(1f).setDuration(700L)
            mainLayout?.visibility=View.VISIBLE
        }
        else {
            //val animSet = AnimationSet(false)
            mainLayout?.alpha = 0f
            //animSet.addAnimation(trans)
            var xRatio = 1f
            var yPivot = 0f
            if (view != null) {
                val fromResimWidth = view.width.toFloat()
                val xLocationOnScreen = getXLocationOnScreen(view)
                val fromResimHeight = view.height.toFloat()
                val screenWidth = this.windowManager.defaultDisplay.width
                val toResimHeight = screenWidth / getAspectRatioFromXml()
                val appAspect = getAspectRatioFromXml()
                var prevAspect = fromResimWidth / fromResimHeight

                xRatio = (fromResimWidth / screenWidth) * (appAspect / prevAspect)
                mainLayout?.translationX = xLocationOnScreen - ((screenWidth - fromResimWidth) / 2)
            }
            mainLayout?.translationY = fromMarginTop.toFloat()
            mainLayout?.pivotX = 540f    //   hg
            mainLayout?.scaleX = xRatio
            mainLayout?.pivotY = 0f  //MainActivity.detailActivityContext.app_bar.height * yPivot
            mainLayout?.scaleY = xRatio
            mainLayout?.visibility=View.VISIBLE

            mainLayout?.post {
                val anim = mainLayout!!.animate().setDuration(500L)
                        .scaleX(1f).scaleY(1f).translationY(0f).translationX(0f).alpha(1f)//.withLayer()
            }

        }
    }
    fun getXLocationOnScreen(view: View): Int {
        val location = IntArray(2)
        //view.getLocationOnScreen(location)
        view.getLocationInWindow(location)
        return location[0]
    }

    fun getAspectRatioFromXml() : Float {
        val str = "H,2,2:1"
        val strArray = str.split(",")
        val strRight = strArray.last()
        val strRightArray = strRight.split(":")
        val width = strRightArray.first().toFloat()
        val height = strRightArray.last().toFloat()
        return width / height
    }
}
