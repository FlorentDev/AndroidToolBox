package com.isen.salou.androidtoolbox.checkEmulator


import android.content.Context
import android.content.pm.PackageManager

import java.lang.reflect.Method

object Utilities {
    /**
     * Method to reflectively invoke the SystemProperties.get command - which is the equivalent to the adb shell getProp
     * command.
     *
     * @param context
     * A [Context] object used to get the proper ClassLoader (just needs to be Application Context
     * object)
     * @param property
     * A `String` object for the property to retrieve.
     * @return `String` value of the property requested.
     */
    fun getProp(context: Context, property: String): String {
        try {
            val classLoader = context.classLoader
            val systemProperties = classLoader.loadClass("android.os.SystemProperties")

            val get = systemProperties.getMethod("get", String::class.java)

            val params = arrayOfNulls<Any>(1)
            params[0] = property

            return get.invoke(systemProperties, *params) as String
        } catch (iAE: IllegalArgumentException) {
            throw iAE
        } catch (exception: Exception) {
            throw  exception
        }

    }

    fun hasPackageNameInstalled(context: Context, packageName: String): Boolean {
        val packageManager = context.packageManager

        // In theory, if the package installer does not throw an exception, package exists
        try {
            packageManager.getInstallerPackageName(packageName)
            return true
        } catch (exception: IllegalArgumentException) {
            return false
        }

    }
}
