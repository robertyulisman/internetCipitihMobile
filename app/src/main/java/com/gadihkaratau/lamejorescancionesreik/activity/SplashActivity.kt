package com.gadihkaratau.lamejorescancionesreik.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.NewInstanceFactory
import com.gadihkaratau.lamejorescancionesreik.BuildConfig
import com.gadihkaratau.lamejorescancionesreik.R
import com.gadihkaratau.lamejorescancionesreik.barqiads.AdsManager
import com.gadihkaratau.lamejorescancionesreik.utils.Mapper.mapSettingToConfigAdsModel
import com.gadihkaratau.lamejorescancionesreik.utils.Tools
import com.gadihkaratau.lamejorescancionesreik.viewModel.MainViewModel
import kotlinx.android.synthetic.main.activity_splash.*
import org.jetbrains.anko.startActivity
import org.koin.java.KoinJavaComponent

class SplashActivity : AppCompatActivity() {

    private val adsManager = KoinJavaComponent.inject(AdsManager::class.java)
    private var mainViewModel: MainViewModel? = null
    private val context = this

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        Tools.setSystemBarColor(this, android.R.color.white)
        Tools.setSystemBarLight(this)

        tvAppVersion.text = getString(R.string.app_version, BuildConfig.VERSION_NAME)

        mainViewModel = ViewModelProvider(this, NewInstanceFactory()).get(MainViewModel::class.java)
        mainViewModel?.let {
            with(it) {
                setSettingMutableLiveData(BuildConfig.APPLICATION_ID)
                settingMutableLiveData?.observe(context, Observer { setting ->
                    setting?.let { ads ->
                        adsManager.value.setUp(mapSettingToConfigAdsModel(ads))
                        adsManager.value.initialize()
                        setListSongMutableLiveData(BuildConfig.APPLICATION_ID)
                    }
                })
                listSongMutableLiveData.observe(context, Observer { songs ->
                    songs?.let {
                        startActivity<HomeActivity>(HomeActivity.EXTRA_ARRAY_LIST_SONG to ArrayList(songs))
                    }
                })
            }
        }
    }
}