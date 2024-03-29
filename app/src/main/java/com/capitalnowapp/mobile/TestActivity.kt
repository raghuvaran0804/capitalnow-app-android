package com.capitalnowapp.mobile

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.provider.ContactsContract.CommonDataKinds
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity


class TestActivity : AppCompatActivity() {



    @RequiresApi(Build.VERSION_CODES.P)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_test)
        /*val contacts: Button = findViewById(R.id.btnContacts)
        contacts.setOnClickListener {
            val intent =
                Intent(Intent.ACTION_PICK, ContactsContract.CommonDataKinds.Phone.CONTENT_URI)
            startActivityForResult(intent, 1)
        }*/


        /*var phoneNo: String? = null
        val uri: Uri = data.getData()
        val cursor: Cursor? = contentResolver.query(uri, null, null, null, null)
        if (cursor!!.moveToFirst()) {
            val phoneIndex = cursor!!.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)
            phoneNo = cursor!!.getString(phoneIndex)
        }


        curosr.close()*/
        //tvDisplay.text = getSystemDetail()

    }

    @SuppressLint("SetTextI18n")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == 1 && resultCode == RESULT_OK){
            val contactUri = data?.getData()
            val projection = arrayOf(CommonDataKinds.Phone.NUMBER)
            val cursor = contactUri?.let {
                contentResolver.query(
                    it, projection,
                    null, null, null)
            };
            // If the cursor returned is valid, get the phone number
            if (cursor != null && cursor.moveToFirst()) {
                val numberIndex = cursor.getColumnIndex(CommonDataKinds.Phone.NUMBER)
                //val nameIndex = cursor.getColumnIndex(CommonDataKinds.Phone.DISPLAY_NAME)
                val number = cursor.getString(numberIndex);
                //val name = cursor.getString(nameIndex);
                //val tvDisplay: TextView = findViewById(R.id.tv_displayInfo)
                //tvDisplay.text =  number
            }
        }
    }



    /*@SuppressLint("HardwareIds")
    private fun getSystemDetail(): CharSequence {
        val display = windowManager.defaultDisplay!!
        val size = Point()
        display.getSize(size)
        val width = size.x
        val height = size.y
        val tManager = baseContext
            .getSystemService(TELEPHONY_SERVICE) as TelephonyManager
        val carrierName = tManager.networkOperatorName

        return  "Brand: ${Build.BRAND} \n" +
                "Manufacture: ${Build.MANUFACTURER} \n" +
                "OS Version: ${Build.VERSION.RELEASE} \n"+
                "Model: ${Build.MODEL} \n" +
                "Resolution: $width X $height \n"+
                "SDK: ${Build.VERSION.SDK_INT} \n" +
                "Operator Name: $carrierName \n" +
                "DeviceID: ${
                    Settings.Secure.getString(
                        contentResolver,
                        Settings.Secure.ANDROID_ID
                    )
                } "
    }*/
}