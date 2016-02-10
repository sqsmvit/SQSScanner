package com.sqsmv.sqsscanner;

import android.content.Context;

import com.dropbox.client2.DropboxAPI;
import com.dropbox.client2.android.AndroidAuthSession;
import com.dropbox.client2.exception.DropboxException;
import com.dropbox.client2.session.AppKeyPair;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

/**
 * Manager class for the Dropbox API. Controls linking, access, reading, and writing to an account.
 */
public class DropboxManager
{
    private static final String TAG = "DropboxManager";

    private Context context;
    private DropboxAPI<AndroidAuthSession> dropboxAPI;

    private static String oAuth2AccessToken;

    /**
     * Constructor.
     * @param context    The Context of the Activity or Service DropboxManager was instantiated for.
     */
    public DropboxManager(Context context)
    {
        this.context = context;

        String dropboxAppKey = context.getString(R.string.DBX_APP_KEY);
        String dropboxAppSecret = context.getString(R.string.DBX_SECRET_KEY);

        AppKeyPair appKeys = new AppKeyPair(dropboxAppKey, dropboxAppSecret);
        AndroidAuthSession session = new AndroidAuthSession(appKeys);
        dropboxAPI = new DropboxAPI<AndroidAuthSession>(session);

        if(oAuth2AccessToken != null)
        {
            setOAuth2AccessToken(oAuth2AccessToken);
        }
    }

    /**
     * Launches the Dropbox app if it is installed, the browser if not, for linking a Dropbox
     * account. Access token is set when an account is successfully linked.
     */
    public void linkDropboxAccount()
    {
        dropboxAPI.getSession().startOAuth2Authentication(context);
    }

    /**
     * Gets the access token stored in the API session.
     * @return The access token stored in the API session.
     */
    public String getOAuth2AccessToken()
    {
        return dropboxAPI.getSession().getOAuth2AccessToken();
    }

    /**
     * Sets the accessToken stored inside the DropboxManager, then sets the value to the API session's.
     * @param accessToken    The access token to set.
     */
    public void setStaticOAuth2AccessToken(String accessToken)
    {
        oAuth2AccessToken = accessToken;
        setOAuth2AccessToken(oAuth2AccessToken);
    }

    /**
     * Finishes the authentication process for linking a Dropbox account.
     * @return true if linking was successful, otherwise false.
     */
    public boolean finishAuthentication()
    {
        boolean success = true;
        if(dropboxAPI.getSession().authenticationSuccessful())
        {
            try
            {
                dropboxAPI.getSession().finishAuthentication();
            }
            catch(IllegalStateException e)
            {
                e.printStackTrace();
                success = false;
            }
        }
        else
        {
            success = false;
        }
        return success;
    }

    /**
     * Checks if the Dropbox API has a linked account already.
     * @return true if there is a linked account, otherwise false.
     */
    public boolean hasLinkedAccount()
    {
        return dropboxAPI.getSession().isLinked();
    }

    /**
     * Writes a file from Dropbox to local storage.
     * @param dbxFilePath        The path to the file on Dropbox.
     * @param downloadPath       The path to the write location on local storage.
     * @param isAsyncDownload    Whether the write should be done asynchronously or not.
     * @return The Thread used to write the file.
     */
    public Thread writeToStorage(final String dbxFilePath, String downloadPath, boolean isAsyncDownload)
    {
        final File downloadFile = new File(downloadPath);
        Thread downloadThread = new Thread()
        {
            @Override
            public void run()
            {
                try
                {
                    FileOutputStream outputStream = new FileOutputStream(downloadFile);
                    dropboxAPI.getFile(dbxFilePath, null, outputStream, null);
                }
                catch(FileNotFoundException e)
                {
                    e.printStackTrace();
                }
                catch(DropboxException e)
                {
                    e.printStackTrace();
                }
            };
        };
        if(isAsyncDownload)
        {
            downloadThread.start();
        }
        else
        {
            downloadThread.run();
        }
        return downloadThread;
    }

    /**
     * Writes a file from local storage to Dropbox.
     * @param fileToWrite        The File to write to Dropbox.
     * @param dbxFilePath        The path to the write location on Dropbox.
     * @param shouldSteal        Whether the file should be removed from local storage when the write is complete.
     * @param isAsyncDownload    Whether the write should be done asynchronously or not.
     * @return The Thread used to write the file.
     */
    public Thread writeToDropbox(final File fileToWrite, final String dbxFilePath, final boolean shouldSteal, boolean isAsyncDownload)
    {
        Thread uploadThread = new Thread()
        {
            @Override
            public void run()
            {
                try
                {
                    FileInputStream inputStream = new FileInputStream(fileToWrite);
                    dropboxAPI.putFile(dbxFilePath, inputStream, fileToWrite.length(), null, null);
                    if(shouldSteal)
                    {
                        fileToWrite.delete();
                    }
                }
                catch(FileNotFoundException e)
                {
                    e.printStackTrace();
                }
                catch(DropboxException e)
                {
                    e.printStackTrace();
                }
            };
        };
        if(isAsyncDownload)
        {
            uploadThread.start();
        }
        else
        {
            uploadThread.run();
        }

        return uploadThread;
    }

    /**
     * Gets the revision id metadata info of a file on Dropbox.
     * @param dbxFilePath    The path on Dropbox to check.
     * @return The revision id metadata info of the file.
     */
    public String getDbxFileRev(final String dbxFilePath)
    {
        final dbxFileMetadata metadata = new dbxFileMetadata();
        Thread metadataThread = new Thread()
        {
            @Override
            public void run()
            {
                try
                {
                    metadata.setDropboxEntry(dropboxAPI.metadata(dbxFilePath, 1, null, false, null));
                }
                catch(DropboxException e)
                {
                    e.printStackTrace();
                }
            }
        };
        metadataThread.start();
        try
        {
            metadataThread.join();
        }
        catch(InterruptedException e)
        {
            e.printStackTrace();
        }

        return metadata.getDropboxEntry().rev;
    }

    /**
     * Sets the access token to the API session.
     * @param accessToken    The access token to set.
     */
    private void setOAuth2AccessToken(String accessToken)
    {
        dropboxAPI.getSession().setOAuth2AccessToken(accessToken);
    }

    /**
     * Container class for storing metadata of a file on Dropbox.
     */
    private class dbxFileMetadata
    {
        DropboxAPI.Entry dropboxEntry;

        /**
         * Gets the stored metadata.
         * @return The stored metadata.
         */
        public DropboxAPI.Entry getDropboxEntry()
        {
            return dropboxEntry;
        }

        /**
         * Sets the stored metadata.
         * @param dropboxEntry    The metadata to store.
         */
        public void setDropboxEntry(DropboxAPI.Entry dropboxEntry)
        {
            this.dropboxEntry = dropboxEntry;
        }
    }
}
