package com.baldeventertainment.despacitobutton

import android.content.Intent
import android.media.MediaPlayer
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.google.android.gms.ads.*
import kotlinx.android.synthetic.main.activity_main.*
import com.google.firebase.analytics.FirebaseAnalytics
import android.content.ActivityNotFoundException
import android.net.Uri

class MainActivity : AppCompatActivity() {
    private lateinit var mInterstitialAd: InterstitialAd
    private var part = 0
    private var mFirebaseAnalytics: FirebaseAnalytics? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        MobileAds.initialize(this, getString(R.string.admob_id))
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this)

        initializeInterstitial()

        val mAdView = findViewById<AdView>(R.id.adView)
        val adRequest = AdRequest.Builder().build()
        mAdView.loadAd(adRequest)

        share.setOnClickListener {
            createShareIntent()
        }

        rate.setOnClickListener {
            rateApp()
        }

        val des = MediaPlayer.create(this, R.raw.des)
        val pa = MediaPlayer.create(this, R.raw.pa)
        val cito = MediaPlayer.create(this, R.raw.cito)
        button.setOnClickListener {
            if (part == 0) {
                des.start()
                despacito_text.text = "DES.."
            }

            if (part == 1) {
                des.stop()
                des.prepare()
                pa.start()
                despacito_text.text = "...PA..."
            }

            if (part == 2) {
                pa.stop()
                pa.prepare()
                cito.start()
                despacito_text.text = "DESPACITO!"
            }

            if (part == 3) {
                cito.stop()
                cito.prepare()
                despacito_text.text = ""
            }

            if (part < 3) {
                part++
            } else {
                part = 0
                mInterstitialAd.show()
                despacito_text.text = ""
            }
        }
    }

    private fun rateApp() {
        mFirebaseAnalytics!!.logEvent("rate_app",  Bundle.EMPTY)

        val uri = Uri.parse("market://details?id=com.baldeventertainment.despacitobutton")
        val goToMarket = Intent(Intent.ACTION_VIEW, uri)
        goToMarket.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY or
                Intent.FLAG_ACTIVITY_MULTIPLE_TASK)
        try {
            startActivity(goToMarket)
        } catch (e: ActivityNotFoundException) {
            startActivity(Intent(Intent.ACTION_VIEW,
                    Uri.parse("http://play.google.com/store/apps/details?id=com.baldeventertainment.despacitobutton")))
        }


    }

    private fun createShareIntent() {
        mFirebaseAnalytics!!.logEvent(FirebaseAnalytics.Event.SHARE, Bundle.EMPTY)

        val shareIntent = Intent()
        shareIntent.action = Intent.ACTION_SEND
        shareIntent.type="text/plain"
        shareIntent.putExtra(Intent.EXTRA_TEXT, getString(R.string.share_text))
        startActivity(Intent.createChooser(shareIntent,"Share"))
    }

    private fun initializeInterstitial() {
        mInterstitialAd = InterstitialAd(this)
        mInterstitialAd.adUnitId = resources.getString(R.string.interstitial_id)
        mInterstitialAd.loadAd(AdRequest.Builder().build())
        mInterstitialAd.adListener = object : AdListener() {
            override fun onAdClosed() {
                mInterstitialAd.loadAd(AdRequest.Builder().build())
            }
        }
    }
}
