package com.capitalnowapp.mobile

import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import com.capitalnowapp.mobile.kotlin.activities.DashboardActivity
import com.google.firebase.crashlytics.FirebaseCrashlytics
import java.io.PrintWriter
import java.io.StringWriter
import java.util.Calendar
import kotlin.system.exitProcess

class CrashHandler (private val context: Context) : Thread.UncaughtExceptionHandler  {
    private val newLine = "\n"
    private var errorMessage = StringBuilder()
    private val softwareInfo = StringBuilder()
    private val dateInfo = StringBuilder()

    override fun uncaughtException(thread: Thread, exception: Throwable) {

        val stackTrace = StringWriter()
        exception.printStackTrace(PrintWriter(stackTrace))

        errorMessage.append(stackTrace.toString())

        FirebaseCrashlytics.getInstance().recordException(Exception(errorMessage.toString()))

        softwareInfo.append("SDK: ")
        softwareInfo.append(Build.VERSION.SDK_INT)
        softwareInfo.append(newLine)
        softwareInfo.append("Release: ")
        softwareInfo.append(Build.VERSION.RELEASE)
        softwareInfo.append(newLine)
        softwareInfo.append("Incremental: ")
        softwareInfo.append(Build.VERSION.INCREMENTAL)
        softwareInfo.append(newLine)

        dateInfo.append(Calendar.getInstance().time)
        dateInfo.append(newLine)

        Log.d("Error" , errorMessage.toString())
        Log.d("Software" , softwareInfo.toString())
        Log.d("Date" , dateInfo.toString())


        val intent = Intent(context , DashboardActivity::class.java)
        intent.putExtra("from" , "Exception")
        //intent.putExtra("errorMessage" , finalErrorMessage)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or
                Intent.FLAG_ACTIVITY_CLEAR_TASK or
                Intent.FLAG_ACTIVITY_NEW_TASK
        context.startActivity(intent)

        android.os.Process.killProcess(android.os.Process.myPid())
        exitProcess(2)

    }
}