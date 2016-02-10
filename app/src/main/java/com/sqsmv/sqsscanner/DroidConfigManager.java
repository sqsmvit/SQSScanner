package com.sqsmv.sqsscanner;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Manager class for access from and to the app's shared preferences config file.
 */
public class DroidConfigManager
{
    public final static String DROPBOX_ACCESS_TOKEN = "dropboxAccessToken";
    public final static String BUILD_DATE = "buildDate";
    public final static String SCANNER_LOCK = "scannerLock";
    public final static String CURRENT_APK_REV = "currentAPKRev";
    public final static String PRIOR_VERSION = "priorVersion";
    public final static String PRODUCTLENS_RESET_DATE = "ProductLensResetDate";
    public final static String EXPORT_MODE_CHOICE = "ExportModeChoice";
    public final static String INVENTORY_MODE_CHOICE = "InventoryModeChoice";
    public final static String IS_AUTO_COUNT = "isAutoCount";
    public final static String AUTO_COUNT_IDX = "autoCountIdx";
    public final static String AUTO_COUNT = "autoCount";
    public final static String CUSTOM_AUTO_COUNT = "customAutoCount";
    public final static String IS_BOX_QTY = "isBoxQty";
    public final static String BOX_QTY = "boxQty";
    public final static String LENS_SELECTION_ID = "lensSelectionId";
    public final static String LENS_SELECT_IDX = "lensSelectIdx";
    public final static String LENS_SELECTION = "lensSelection";

    private SharedPreferences config;
    private final static String prefName = "scanConfig";

    /**
     * Constructor.
     * @param context    The Context of the Activity or Service DroidConfigManager was instantiated for.
     */
    public DroidConfigManager(Context context)
    {
        config = context.getSharedPreferences(prefName, 0);
    }

    /**
     * Writes a String value to the config file if the value isn't null, then returns the value stored
     * at the key even if nothing is written.
     * @param key           The key to set the value at and get from in the config file.
     * @param value         The value to write to the key, null if nothing needs to be written.
     * @param defaultVal    The default value used if the key is not present in the config file.
     * @return The String value stored in the config file at the key if it exists, or the defaultVal if not.
     */
    public String accessString(String key, String value, String defaultVal)
    {
        if(value != null)
        {
            config.edit().putString(key, value).apply();
        }
        return config.getString(key, defaultVal);
    }

    /**
     * Writes an int value to the config file if the value isn't null, then returns the value stored
     * at the key even if nothing is written.
     * @param key           The key to set the value at and get from in the config file.
     * @param value         The value to write to the key, null if nothing needs to be written.
     * @param defaultVal    The default value used if the key is not present in the config file.
     * @return The int value stored in the config file at the key if it exists, or the defaultVal if not.
     */
    public int accessInt(String key, Integer value, int defaultVal)
    {
        if(value != null)
        {
            config.edit().putInt(key, value).apply();
        }
        return config.getInt(key, defaultVal);
    }

    /**
     * Writes a float value to the config file if the value isn't null, then returns the value stored
     * at the key even if nothing is written.
     * @param key           The key to set the value at and get from in the config file.
     * @param value         The value to write to the key, null if nothing needs to be written.
     * @param defaultVal    The default value used if the key is not present in the config file.
     * @return The float value stored in the config file at the key if it exists, or the defaultVal if not.
     */
    public float accessFloat(String key, Float value, float defaultVal)
    {
        if(value != null)
        {
            config.edit().putFloat(key, value).apply();
        }
        return config.getFloat(key, defaultVal);
    }

    /**
     * Writes a boolean value to the config file if the value isn't null, then returns the value stored
     * at the key even if nothing is written.
     * @param key           The key to set the value at and get from in the config file.
     * @param value         The value to write to the key, null if nothing needs to be written.
     * @param defaultVal    The default value used if the key is not present in the config file.
     * @return The boolean value stored in the config file at the key if it exists, or the defaultVal if not.
     */
    public boolean accessBoolean(String key, Boolean value, boolean defaultVal)
    {
        if(value != null)
        {
            config.edit().putBoolean(key, value).apply();
        }
        return config.getBoolean(key, defaultVal);
    }
}
