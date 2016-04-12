package com.sqsmv.sqsscanner;

import android.app.ActivityManager;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.media.AudioManager;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.ConnectivityManager;
import android.net.DhcpInfo;
import android.net.NetworkInfo;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.Vibrator;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

/**
 * Library class for various methods for SQSScanner.
 */
public class Utilities
{
    /**
     * Checks and deletes files in a directory that are older than a number of days.
     * @param dir     The directory to delete files from.
     * @param days    The age in number of days a file has to be for deletion.
     */
    public static void cleanFolder(File dir, long days)
    {
        if (dir.exists())
        {
            File[] fileList = dir.listFiles();

            long eligibleForDeletion = System.currentTimeMillis()
                    - (days * 24 * 60 * 60 * 1000L);

            for (File listFile : fileList)
            {
                if(listFile.lastModified() < eligibleForDeletion)
                {
                    listFile.delete();
                }
            }
        }
    }

    /**
     * Copies a file from a source file to a destination file.
     * @param src     The source file to copy from.
     * @param dest    The destination file to copy to.
     * @throws IOException
     */
    public static void copyFile(File src, File dest) throws IOException
    {
        InputStream in = new FileInputStream(src);
        OutputStream out = new FileOutputStream(dest);

        byte[] buf = new byte[1024];
        int len;

        while ((len = in.read(buf)) > 0)
        {
            out.write(buf, 0, len);
        }

        in.close();
        out.close();
    }

    /**
     * Makes a Toast and displays it for the length of time defined in Toast's LENGTH_SHORT attribute.
     * @param callingContext    The Context of the Activity or Service making the Toast.
     * @param message           The message to Toast to the user.
     */
    public static void makeToast(Context callingContext, String message)
    {
        Toast.makeText(callingContext, message, Toast.LENGTH_SHORT).show();
    }

    /**
     * Makes a Toast and displays it for the length of time defined in Toast's LENGTH_LONG attribute.
     * @param callingContext    The Context of the Activity or Service making the Toast.
     * @param message           The message to Toast to the user.
     */
    public static void makeLongToast(Context callingContext, String message)
    {
        Toast.makeText(callingContext, message, Toast.LENGTH_LONG).show();
    }

    /**
     * Gets the current version name of the app.
     * @param callingContext    The Context of the Activity or Service making the request.
     * @return The version name of the app.
     */
    public static String getVersion(Context callingContext)
    {
        String version = "";

        try
        {
            PackageInfo packageInfo = callingContext.getPackageManager().getPackageInfo(callingContext.getPackageName(), 0);
            version = packageInfo.versionName;
        }
        catch (PackageManager.NameNotFoundException e)
        {
            e.printStackTrace();
        }

        return version;
    }

    /**
     * Gets the bluetooth name set for the Android device.
     * @return The bluetooth name for the device.
     */
    public static String getDeviceName()
    {
        return BluetoothAdapter.getDefaultAdapter().getName();
    }

    /**
     * Gets the current gateway information for the Android device.
     * @param callingContext    The Context of the Activity or Service making the request.
     * @return The IP address of the current gateway in use.
     */
    public static String getDefaultGateway(Context callingContext)
    {
        WifiManager wifi = (WifiManager)callingContext.getSystemService(callingContext.WIFI_SERVICE);
        DhcpInfo d = wifi.getDhcpInfo();
        String s_gateway = intToIp(d.gateway);
        return s_gateway;
    }

    /**
     * Converts an int value to the matching IP address.
     * @param i    The int value to convert.
     * @return The converted IP address.
     */
    private static String intToIp(int i)
    {
        return ((i >> 24 ) & 0xFF ) + "." +
                ((i >> 16 ) & 0xFF) + "." +
                ((i >> 8 ) & 0xFF) + "." +
                ( i & 0xFF);
    }

    /**
     * Checks to see if the WiFi is connected on the Android device.
     * @param callingContext    The context of the Activity or Service making the request.
     * @return true if WiFi is connected, otherwise false.
     */
    public static boolean checkWifi(Context callingContext)
    {
        ConnectivityManager connManager = (ConnectivityManager)callingContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo wifi = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        return wifi.isConnected();
    }

    /**
     * Gets the total memory available on the current Android device.
     * @param callingContext    The context of the Activity or Service making the request.
     * @return The total memory available on the device.
     */
    public static long totalDeviceMemory(Context callingContext)
    {
        ActivityManager.MemoryInfo mi = new ActivityManager.MemoryInfo();
        ActivityManager activityManager = (ActivityManager)callingContext.getSystemService(callingContext.ACTIVITY_SERVICE);
        activityManager.getMemoryInfo(mi);
        return mi.totalMem / 1048576;
    }

    /**
     * Formats a Date as a String in the format of yyMMdd.
     * @param formateDate    The Date to format.
     * @return The formatted String.
     */
    public static String formatYYMMDDDate(Date formateDate)
    {
        return new SimpleDateFormat("yyMMdd", Locale.US).format(formateDate);
    }

    /**
     * Parses a String in yyMMdd format to a Date.
     * @param parseString    The String to parse.
     * @return The parsed Date.
     * @throws ParseException
     */
    public static Date parseYYMMDDString(String parseString) throws ParseException
    {
        return new SimpleDateFormat("yyMMdd", Locale.US).parse(parseString);
    }

    /**
     * Builds a timestamp for the time the method was called.
     * @return The timestamp that was built.
     */
    public static String buildCurrentTimestamp()
    {
        Date now = new Date();
        SimpleDateFormat timestampFormat = new SimpleDateFormat("yyMMdd_kkmm", Locale.US);
        return timestampFormat.format(now);
    }

    /**
     * Creates an ArrayAdapter for a Spinner using resources defined in SQSScanner.
     * @param callingContext    The Context for the Activity that needs the ArrayAdapter.
     * @param spinnerItems      The List of Strings to put in the ArrayAdapter.
     * @return The created ArrayAdapter.
     */
    public static ArrayAdapter<String> createSpinnerAdapter(Context callingContext, ArrayList<String> spinnerItems)
    {
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(callingContext, R.layout.spinner_list, spinnerItems);
        adapter.setDropDownViewResource(R.layout.spinner_item);
        return adapter;
    }

    /**
     * Sounds the Android device's set notification sound to alert the user.
     * @param callingContext    The Context for the Activity or Service making the alert.
     */
    public static void alertNotificationSound(Context callingContext)
    {
        AudioManager audioManager = (AudioManager)callingContext.getSystemService(Context.AUDIO_SERVICE);
        audioManager.setStreamVolume(AudioManager.STREAM_RING, audioManager.getStreamMaxVolume(AudioManager.STREAM_RING),
                AudioManager.FLAG_ALLOW_RINGER_MODES);
        Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        RingtoneManager.getRingtone(callingContext, notification).play();
    }

    /**
     * Sounds the Android device's set alarm sound to alert the user.
     * @param callingContext           The Context for the Activity or Service making the alert.
     * @param alertTimeMilliseconds    The time in milliseconds that the alarm should ring for.
     */
    public static void alertAlarm(Context callingContext, final long alertTimeMilliseconds)
    {
        AudioManager audioManager = (AudioManager)callingContext.getSystemService(Context.AUDIO_SERVICE);
        audioManager.setStreamVolume(AudioManager.STREAM_ALARM, audioManager.getStreamMaxVolume(AudioManager.STREAM_ALARM),
                AudioManager.FLAG_ALLOW_RINGER_MODES);
        Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
        final Ringtone ringTone = RingtoneManager.getRingtone(callingContext, notification);
        ringTone.play();
        new Thread()
        {
            @Override
            public void run()
            {
                try
                {
                    sleep(alertTimeMilliseconds);
                }
                catch(InterruptedException e)
                {
                    e.printStackTrace();
                }
                finally
                {
                    ringTone.stop();
                }
            }
        }.start();
    }

    /**
     * Vibrates the Android device to alert the user.
     * @param callingContext    The Context for the Activity or Service making the alert.
     * @param vibratePattern    The long[] containing the pattern the vibration should be performed with.
     */
    public static void alertVibrate(Context callingContext, long[] vibratePattern)
    {
        ((Vibrator)callingContext.getSystemService(Context.VIBRATOR_SERVICE)).vibrate(vibratePattern, -1);
    }
}
