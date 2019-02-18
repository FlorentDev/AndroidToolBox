package com.isen.salou.androidtoolbox.checkEmulator


import android.Manifest
import android.annotation.SuppressLint

import android.content.pm.PackageManager
import android.os.Build
import android.support.v4.app.ActivityCompat

import android.telephony.TelephonyManager
import android.content.Context
import android.util.Log
import java.io.File
import java.io.FileInputStream
import android.support.v4.content.ContextCompat



class DetectEmulator {




    companion object {

        /**
         * Utility methods related to physical devies and emulators.
         */

        val isEmulatorCheck1: Boolean
            get() = (Build.FINGERPRINT.startsWith("generic")
                    || Build.FINGERPRINT.startsWith("unknown")
                    || Build.MODEL.contains("google_sdk")
                    || Build.MODEL.contains("Emulator")
                    || Build.MODEL.contains("Android SDK built for x86")
                    || Build.MANUFACTURER.contains("Genymotion")
                    || Build.BRAND.startsWith("generic") && Build.DEVICE.startsWith("generic")
                    || "google_sdk" == Build.PRODUCT
                    || Build.PRODUCT == "sdk_x86"  // rajouter par pierre
                    || Build.PRODUCT == "vbox86p"
                    || Build.MANUFACTURER == "unknown"
                    || Build.BRAND == "generic_x86"
                    || Build.HARDWARE == "goldfish"
                    || Build.HARDWARE == "vbox86"
                    || Build.FINGERPRINT.contains("generic/sdk/generic")
                    || Build.FINGERPRINT.contains("generic_x86/sdk_x86/generic_x86")
                    || Build.FINGERPRINT.contains("generic/google_sdk/generic")
                    || Build.FINGERPRINT.contains("generic/vbox86p/vbox86p")
                    || Build.BRAND.contains("test-keys")
                    || Build.PRODUCT.contains("test-keys")
                    || Build.BOARD.contains("unknown")
                    || Build.MANUFACTURER.contains("unknown")
                    || Build.MODEL.contains("sdk")
                    || Build.PRODUCT.contains("sdk"))

        // Need to check the format of these
        // Android emulator support up to 16 concurrent emulator
        // The console of the first emulator instance running on a given
        // machine uses console port 5554
        // Subsequent instances use port numbers increasing by two
        private val known_numbers = arrayOf(
            "+15555215554", // Default emulator phone numbers + VirusTotal
            "+15555215556",
            "+15555215558",
            "+15555215560",
            "+15555215562",
            "+15555215564",
            "+15555215566",
            "+15555215568",
            "+15555215570",
            "+15555215572",
            "+15555215574",
            "+15555215576",
            "+15555215578",
            "+15555215580",
            "+15555215582",
            "+15555215584"
        )
        private val known_device_ids = arrayOf(
            "000000000000000", // Default emulator id
            "e21833235b6eef10", // VirusTotal id
            "012345678912345"
        )
        private val known_imsi_ids = arrayOf(
            "310260000000000" // Default imsi id
        )
        private val known_pipes = arrayOf("/dev/socket/qemud", "/dev/qemu_pipe")

        private val known_files = arrayOf("/system/lib/libc_malloc_debug_qemu.so", "/sys/qemu_trace", "/system/bin/qemu-props")

        private val known_geny_files = arrayOf("/dev/socket/genyd", "/dev/socket/baseband_genyd")

        private val known_qemu_drivers = arrayOf("goldfish")

        /**
         * Known props, in the format of [property name, value to seek] if value to seek is null, then it is assumed that
         * the existence of this property (anything not null) indicates the QEmu environment.
         */
        private val KNOWNN_PROPS = arrayOf(
            Property("init.svc.qemud", null),
            Property("init.svc.qemu-props", null),
            Property("qemu.hw.mainkeys", null),
            Property("qemu.sf.fake_camera", null),
            Property("qemu.sf.lcd_density", null),
            Property("ro.bootloader", "unknown"),
            Property("ro.bootmode", "unknown"),
            Property("ro.hardware", "goldfish"),
            Property("ro.kernel.android.qemud", null),
            Property("ro.kernel.qemu.gles", null),
            Property("ro.kernel.qemu", "1"),
            Property("ro.product.device", "generic"),
            Property("ro.product.model", "sdk"),
            Property("ro.product.name", "sdk")
            // Need to double check that an "empty" string ("") returns null
            //   Property("ro.serialno", null)
        )
        /**
         * The "known" props have the potential for false-positiving due to interesting (see: poorly) made Chinese
         * devices/odd ROMs. Keeping this threshold low will result in better QEmu detection with possible side affects.
         */
        private val MIN_PROPERTIES_THRESHOLD = 0x9

        init {
            // This is only valid for arm
            //     System.loadLibrary("anti")
        }

        /**
         * Check the existence of known pipes used by the Android QEmu environment.
         *
         * @return `true` if any pipes where found to exist or `false` if not.
         */
        fun hasPipes(): Boolean {
            for (pipe in known_pipes) {
                val qemu_socket = File(pipe)
                if (qemu_socket.exists()) {
                    return true
                }
            }

            return false
        }

        /**
         * Check the existence of known files used by the Android QEmu environment.
         *
         * @return `true` if any files where found to exist or `false` if not.
         */
        fun hasQEmuFiles(): Boolean {
            for (pipe in known_files) {
                val qemu_file = File(pipe)
                if (qemu_file.exists()) {
                    return true
                }
            }

            return false
        }

        /**
         * Check the existence of known files used by the Genymotion environment.
         *
         * @return `true` if any files where found to exist or `false` if not.
         */
        fun hasGenyFiles(): Boolean {
            for (file in known_geny_files) {
                val geny_file = File(file)
                if (geny_file.exists()) {
                    return true
                }
            }

            return false
        }

        /**
         * Reads in the driver file, then checks a list for known QEmu drivers.
         *
         * @return `true` if any known drivers where found to exist or `false` if not.
         */
        fun hasQEmuDrivers(): Boolean {
            for (drivers_file in arrayOf(File("/proc/tty/drivers"), File("/proc/cpuinfo"))) {
                if (drivers_file.exists() && drivers_file.canRead()) {
                    // We don't care to read much past things since info we care about should be inside here
                    val data = ByteArray(1024)
                    try {
                        val `is` = FileInputStream(drivers_file)
                        `is`.read(data)
                        `is`.close()
                    } catch (exception: Exception) {
                        exception.printStackTrace()
                    }

                    val driver_data = String(data)
                    for (known_qemu_driver in DetectEmulator.known_qemu_drivers) {
                        if (driver_data.contains(known_qemu_driver)) {
                            return true
                        }
                    }
                }
            }

            return false
        }

        fun hasEmulatorBuild(context: Context): Boolean {
            val BOARD = android.os.Build.BOARD // The name of the underlying board, like "unknown".
            // This appears to occur often on real hardware... that's sad
            // String BOOTLOADER = android.os.Build.BOOTLOADER; // The system bootloader version number.
            val BRAND = android.os.Build.BRAND // The brand (e.g., carrier) the software is customized for, if any.
            // "generic"
            val DEVICE = android.os.Build.DEVICE // The name of the industrial design. "generic"
            val HARDWARE = android.os.Build.HARDWARE // The name of the hardware (from the kernel command line or
            // /proc). "goldfish"
            val MODEL = android.os.Build.MODEL // The end-user-visible name for the end product. "sdk"
            val PRODUCT = android.os.Build.PRODUCT // The name of the overall product.
            return (BOARD.compareTo("unknown") == 0 /* || (BOOTLOADER.compareTo("unknown") == 0) */
                    || BRAND.compareTo("generic") == 0 || DEVICE.compareTo("generic") == 0
                    || MODEL.compareTo("sdk") == 0 || PRODUCT.compareTo("sdk") == 0
                    || HARDWARE.compareTo("goldfish") == 0)
        }

        fun isOperatorNameAndroid(paramContext: Context): Boolean {
            val szOperatorName =
                (paramContext.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager).networkOperatorName
            return szOperatorName.equals("android", ignoreCase = true)
        }

        external fun qemuBkpt(): Int

        fun checkQemuBreakpoint(): Boolean {
            var hit_breakpoint = false

            // Potentially you may want to see if this is a specific value
            val result = qemuBkpt()

            if (result > 0) {
                hit_breakpoint = true
            }

            return hit_breakpoint
        }

        fun hasKnownPhoneNumber(context: Context): Boolean {
            val telephonyManager = context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager

            if (ActivityCompat.checkSelfPermission(
                    context,
                    Manifest.permission.READ_SMS
                ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                    context,
                    Manifest.permission.READ_PHONE_NUMBERS
                ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                    context,
                    Manifest.permission.READ_PHONE_STATE
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.

                Log.i("Permission" , "hasKnownPhoneNumber non accorder")
                return false


            }else {
                val phoneNumber = telephonyManager.line1Number

                for (number in known_numbers) {
                    //            Log.i("Comparation" , ""+ number + " <-->" + phoneNumber)
                    if (number.equals(phoneNumber, ignoreCase = true)) {
                        return true
                    }

                }
            }
            return false
        }


        @SuppressLint("HardwareIds")
        fun hasKnownDeviceId(context: Context): Boolean {
            val telephonyManager = context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager

            if (ActivityCompat.checkSelfPermission(
                    context,
                    Manifest.permission.READ_PHONE_STATE
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return false

            }
            val deviceId = telephonyManager.getDeviceId()

            for (known_deviceId in known_device_ids) {
                if (known_deviceId.equals(deviceId, ignoreCase = true)) {
                    return true
                }

            }
            return false
        }


        fun hasKnownImsi(context: Context): Boolean {

            val telephonyManager = context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager

            if (ActivityCompat.checkSelfPermission(
                    context,
                    Manifest.permission.READ_PHONE_STATE
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return false
            }
            val imsi = telephonyManager.subscriberId

            for (known_imsi in known_imsi_ids) {
                if (known_imsi.equals(imsi, ignoreCase = true)) {
                    return true
                }
            }
            return false
        }

        /**
         * Will query specific system properties to try and fingerprint a QEmu environment. A minimum threshold must be met
         * in order to prevent false positives.
         *
         * @param context A {link Context} object for the Android application.
         * @return `true` if enough properties where found to exist or `false` if not.
         */
        fun hasQEmuProps(context: Context): Boolean {
            var found_props = 0

            for (property in KNOWNN_PROPS) {
                val property_value = Utilities.getProp(context, property.name)
                // See if we expected just a non-null
                if (property.seek_value == null) {

                    Log.i("hasQEmuProps" , property.name +" , " + property.seek_value  )

                    found_props++
                }
                // See if we expected a value to seek
                if (property.seek_value != null && property_value.indexOf(property.seek_value!!) != -1) {

                    Log.i("hasQEmuProps" , property.name + " == " +property.seek_value   )

                    found_props++
                }

            }

            Log.i("hasQEmuProps" ,  found_props.toString()   )

            return if (found_props >= MIN_PROPERTIES_THRESHOLD) {
                true
            } else false

        }
    }


}

