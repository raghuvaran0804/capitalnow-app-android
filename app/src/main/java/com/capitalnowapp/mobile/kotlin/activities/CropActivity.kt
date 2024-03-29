package com.capitalnowapp.mobile.kotlin.activities

import android.content.ContextWrapper
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.capitalnowapp.mobile.databinding.ActivityCropBinding
import com.capitalnowapp.mobile.kotlin.config.CropViewConfigurator
import com.theartofdev.edmodo.cropper.CropImageView
import kotlinx.android.synthetic.main.activity_crop.cropImageView
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.OutputStream
import java.text.SimpleDateFormat
import java.util.Date


class CropActivity : AppCompatActivity() {
    private var binding: ActivityCropBinding? = null
    private var configurator: CropViewConfigurator? = null
    private var isProfilePic = false
    private var isPanPicture = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCropBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        try {
            /* val imageUri: Uri = intent.getParcelableExtra("selectedImage")
             binding?.cropView?.setImageUri(imageUri)
             binding?.cropView?.configureOverlay()?.setBorderColor(R.color.color_primary)?.apply()

             configurator = CropViewConfigurator()
             binding?.cropView?.let { configurator?.createCropViewConfigurator(it) }

             binding?.ivDone?.setOnClickListener {
                 binding?.cropView?.crop(configurator?.getSelectedSaveConfig())
                 finish()
             }
             binding?.ivCross?.setOnClickListener{
                 finish()
             }*/

            // start picker to get image for cropping and then use the image in cropping activity
            cropImageView.setImageUriAsync(intent.getParcelableExtra("selectedPanImage"))
            cropImageView.setImageUriAsync(intent.getParcelableExtra("selectedImage"))
            if(intent.hasExtra("isPanPicture")){
                isPanPicture = intent.getBooleanExtra("isPanPicture", false)
            }

            if(intent.hasExtra("isProfilePic")){
                isProfilePic = intent.getBooleanExtra("isProfilePic", false)
            }

            when {
                isPanPicture -> {
                    cropImageView.setAspectRatio(3, 2)
                }
                isProfilePic -> {
                    cropImageView.setAspectRatio(2, 2)
                }
                else -> {
                    cropImageView.setAspectRatio(2, 1)
                }
            }
            cropImageView.guidelines = CropImageView.Guidelines.ON
            cropImageView.cropShape = CropImageView.CropShape.RECTANGLE //shaping the image

            cropImageView.setOnCropImageCompleteListener { view, result ->
                if (result.uri != null) Log.e("tes-result", result.uri.toString()) //null
                val uri = saveImageToInternalStorage(result.bitmap)
                val intent = Intent()
                val bundle = Bundle()
                bundle.putString("crop", uri.toString())
                intent.putExtras(bundle)
                setResult(RESULT_OK, intent)
                finish()
            }

            /*CropImage.activity()
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .setOutputUri(intent.getParcelableExtra("selectedImage"))
                    .setAspectRatio(2, 1)
                    .setCropShape(CropImageView.CropShape.RECTANGLE)
                    .setRequestedSize(300, 150)
                    .start(this);*/

            binding?.ivDone?.setOnClickListener {
                cropImageView.getCroppedImageAsync()
            }
            binding?.ivCross?.setOnClickListener {
                finish()
            }

            binding?.ivRotate?.setOnClickListener {
                cropImageView.rotateImage(90)
            }

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun saveImageToInternalStorage(bitmap: Bitmap): Uri? {
        val mTimeStamp: String = SimpleDateFormat("dd MMM yyyy hh:mm:ss").format(Date())
        val mImageName = "snap_$mTimeStamp.jpg"
        val wrapper = ContextWrapper(this)
        var file: File = wrapper.getDir("Images", MODE_PRIVATE)
        file = File(file, "snap_$mImageName.jpg")
        try {
            var stream: OutputStream? = null
            stream = FileOutputStream(file)
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream)
            stream.flush()
            stream.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return Uri.parse(file.getAbsolutePath())
    }
}
