package com.capitalnowapp.mobile.kotlin.config

import android.net.Uri
import com.capitalnowapp.mobile.CapitalNowApp
import com.steelkiwi.cropiwa.CropIwaView
import com.steelkiwi.cropiwa.config.ConfigChangeListener
import com.steelkiwi.cropiwa.config.CropIwaSaveConfig

import java.io.File

class CropViewConfigurator : ConfigChangeListener  {

    private var cropIwaView: CropIwaView? = null
    private var saveConfig: CropIwaSaveConfig.Builder? = null

    fun createCropViewConfigurator(cropIwaView: CropIwaView) {
        this.cropIwaView = cropIwaView
        saveConfig = CropIwaSaveConfig.Builder(createNewEmptyFile()).setSize(450, 450).setQuality(100)

        cropIwaView.configureImage().addConfigChangeListener(this)
    }

    private fun createNewEmptyFile(): Uri? {
        return Uri.fromFile(File(
                CapitalNowApp.getInstance().filesDir, System.currentTimeMillis().toString() + ".png"))
    }

    override fun onConfigChanged() {
    }

    fun getSelectedSaveConfig(): CropIwaSaveConfig? {
        return saveConfig!!.build()
    }
}