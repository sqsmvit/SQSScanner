package com.example.sqsscanner;

import android.app.Application;
import android.content.Context;
import android.content.Intent;

import com.SocketMobile.ScanAPI.ISktScanDecodedData;
import com.SocketMobile.ScanAPI.ISktScanObject;
import com.SocketMobile.ScanAPI.SktScanApiOwnership;
import com.SocketMobile.ScanAPI.SktScanApiOwnership.Debug;
import com.SocketMobile.ScanAPI.SktScanApiOwnership.Notification;
import com.SocketMobile.ScanAPI.SktScanErrors;
import com.example.sqsscanner.ScanApiHelper.ScanApiHelperNotification;

public class ScanApiApplication extends Application {
	
	class Event{
		private boolean _set;
		public Event(boolean set){
			_set=set;
		}
		public synchronized void  set()
		{
			_set=true;
			notify();
		}
		public synchronized void reset(){
			_set=false;
		}
		public synchronized boolean waitFor(long timeoutInMilliseconds)
		{
			long t1,t2=0;
			for(;_set==false;){
				t1=System.currentTimeMillis();
				try {
					wait(timeoutInMilliseconds);
				} catch (InterruptedException e) {
					break;
				}
				t2=System.currentTimeMillis();
				if(_set==false)
				{
					if(t2>=(t1+timeoutInMilliseconds))
						break;
					else
						timeoutInMilliseconds=(t1+timeoutInMilliseconds)-t2;
				}
				else
					break;
			}
			return _set;
		}
	}
		
	public static final String NOTIFY_SCANPI_INITIALIZED = "ScanApiApplication.NotifyScanApiInitialized";   
	public static final String NOTIFY_SCANNER_ARRIVAL = "ScanApiApplication.NotifyScannerArrival";   
	public static final String NOTIFY_SCANNER_REMOVAL = "ScanApiApplication.NotifyScannerRemoval";
	public static final String NOTIFY_DECODED_DATA = "ScanApiApplication.NotifyDecodedData";
	public static final String NOTIFY_ERROR_MESSAGE = "ScanApiApplication.NotifyErrorMessage";
	public static final String NOTIFY_CLOSE_ACTIVITY = "ScanApiApplication.NotifyCloseActivity";
	public static final String NOTIFY_SCANAPI_INIT = "ScanApiApplication.NotifyScanApiInit";
	public static final String NOTIFY_SCANAPI_STOP = "ScanApiApplication.NotifyScanApiStop";
	public static final String EXTRA_ERROR_MESSAGE = "ScanApiApplication.ErrorMessage";
	public static final String EXTRA_DECODEDDATA="ScanApiApplication.DecodedData";
	public static final String EXTRA_SCANNER_ARRIVAL = "ScanApiApplication.ScannerArrival";
	public static final String EXTRA_SCANPI_INITIALIZED = "ScanApiApplication.ExtraScanApiInitialized";
	public static final String EXTRA_CLOSE_ACTIVITY = "ScanApiApplication.ExtraCloseActivity";
	public static final String EXTRA_SCANAPI_INIT = "ScanApiApplication.ExtraScanApiInit";
	public static final String EXTRA_SCANAPI_STOP = "ScanApiApplication.ExtraNotifyScanApiStop";
	
	protected static final int defaultConnectedTimeout = 0;


	private ScanApiHelper _scanApiHelper;
	private SktScanApiOwnership _scanApiOwnership;
	private Event _consumerTerminatedEvent;// event to know when the ScanAPI terminate event has been received

	public String currPullNum;
	private static ScanApiApplication instance = null; 
	


	@Override
	public void onCreate() {
		super.onCreate();
		instance = this;
		_consumerTerminatedEvent=new Event(true);
				
		// create a ScanAPI Helper 
		_scanApiHelper=new ScanApiHelper();
		_scanApiHelper.setNotification(_scanApiHelperNotification);
		
		// create a ScanAPI ownership
		_scanApiOwnership=new SktScanApiOwnership(_scanApiOwnershipNotification,
				getString(R.string.app_name));
		
		
		
		openScanApi();

		
	}

	public static ScanApiApplication getInstance(){
		
		return instance;
	}
	
	
	@Override
	public void onTerminate() {
		super.onTerminate();
	}

	

	/**
	 * Notification helping to manage ScanAPI ownership.
	 * Only one application at a time can have access to ScanAPI.
	 * When another application is claiming ScanAPI ownership, this
	 * callback is called with release set to true asking this application
	 * to release scanAPI. When the other application is done with ScanAPI 
	 * it calls releaseOwnership, causing this callback to be called again
	 * but this time with release set to false. At that moment this application
	 * can reclaim the ScanAPI ownership.
	 */
	private Notification _scanApiOwnershipNotification=new Notification() {
		
		public void onScanApiOwnershipChange(Context context, boolean release) {
			if(release==true){
				closeScanApi();
			}
			else{
				openScanApi();
			}
		}
	};

/*
	*//**
	 * register for ScanAPI ownership
	 *//*
	private void registerScanApiOwnership(){
		_scanApiOwnership.register(this);
	}
	
	*//**
	 * unregister from ScanAPI ownership
	 *//*
	private void unregisterScanApiOwnership(){
		_scanApiOwnership.unregister();
	}
*/
	/**
	 * open ScanAPI by first claiming its ownership
	 * then checking if the previous instance of ScanAPI has
	 * been correctly close. ScanAPI initialization is done in a
	 * separate thread, because it performs some internal testing
	 * that requires some time to complete and we want the UI to be
	 * responsive and present on the screen during that time.
	 */
	private void openScanApi(){
		_scanApiOwnership.claimOwnership();

		// check this event to be sure the previous 
		// ScanAPI consumer has been shutdown
		Debug.Msg(Debug.kTrace,"Wait for the previous terminate event to be set");
		
		if(_consumerTerminatedEvent.waitFor(3000)==true){
			Debug.Msg(Debug.kTrace,"the previous terminate event has been set");
			_consumerTerminatedEvent.reset();
			_scanApiHelper.removeCommands(null);// remove all the commands
			_scanApiHelper.open();
		}
		else{
			Debug.Msg(Debug.kTrace,"the previous terminate event has NOT been set");
			Intent intent=new Intent(NOTIFY_ERROR_MESSAGE);
			intent.putExtra(EXTRA_ERROR_MESSAGE,"Unable to start ScanAPI because the previous close hasn't been completed. Restart this application.");
			sendBroadcast(intent);
		}
	}
	
	/**
	 * close ScanAPI by first releasing its ownership and 
	 * by sending an abort. This allows ScanAPI to shutdown 
	 * gracefully by asking to close any Scanner Object if 
	 * they were opened. When ScanAPI is done a kSktScanTerminate event
	 * is received in the ScanObject consumer timer thread.
	 */
	private void closeScanApi(){
		_scanApiOwnership.releaseOwnership();

		_scanApiHelper.close();
	}
	
	private ScanApiHelperNotification _scanApiHelperNotification=new ScanApiHelperNotification() {
		/**
		 * receive a notification indicating ScanAPI has terminated,
		 * then send an intent to finish the activity if it is still
		 * running
		 */
		public void onScanApiTerminated() {
			_consumerTerminatedEvent.set();

			Intent intent=new Intent(NOTIFY_CLOSE_ACTIVITY);
			sendBroadcast(intent);
		}

		/**
		 * ScanAPI is now initialized, if there is an error
		 * then ask the activity to display it
		 */
		public void onScanApiInitializeComplete(long result) {
			// if ScanAPI couldn't be initialized
			// then display an error
			if(!SktScanErrors.SKTSUCCESS(result)){
				_consumerTerminatedEvent.set();
	    		_scanApiOwnership.releaseOwnership();
				String text="ScanAPI failed to initialize with error: "+result;
	    		Intent intent=new Intent(NOTIFY_ERROR_MESSAGE);
	    		intent.putExtra(EXTRA_ERROR_MESSAGE,text);
	        	sendBroadcast(intent);
			}
			else{
	    		Intent intent=new Intent(NOTIFY_SCANPI_INITIALIZED);
	        	sendBroadcast(intent);
	        	
			}
		}
		/**
		 * ask the activity to display any asynchronous error
		 * received from ScanAPI
		 */
		public void onError(long result) {
			Debug.Msg(Debug.kError,"receive an error:"+result);
			String text="ScanAPI is reporting an error: "+result;
			if(result==SktScanErrors.ESKT_UNABLEINITIALIZE)
				text="Unable to initialize the scanner. Please power cycle the scanner.";
    		Intent intent=new Intent(NOTIFY_ERROR_MESSAGE);
    		intent.putExtra(EXTRA_ERROR_MESSAGE,text);
        	sendBroadcast(intent);
		}
		

		/**
		 * ScanAPI is delivering some decoded data
		 * as the activity to display them
		 */
		public void onDecodedData(DeviceInfo deviceInfo,
				ISktScanDecodedData decodedData) {
			Intent intent=new Intent(NOTIFY_DECODED_DATA);
			intent.putExtra(EXTRA_DECODEDDATA,decodedData.getData());
			sendBroadcast(intent);
		}
		/**
		 * an error occurs during the retrieval of ScanObject
		 * from ScanAPI, this is critical error and only a restart
		 * can fix this.
		 */
		public void onErrorRetrievingScanObject(long result) {
			Intent intent=new Intent(NOTIFY_ERROR_MESSAGE);
			String text="Error unable to retrieve ScanAPI message: ";
			text+="("+result+")";
			text+="Please close this application and restart it";
			intent.putExtra(EXTRA_ERROR_MESSAGE,text);
			sendBroadcast(intent);
		}
		@Override
		public void onDeviceArrival(long result, DeviceInfo newDevice) {
			// TODO Auto-generated method stub
			Intent intent=new Intent(NOTIFY_SCANNER_ARRIVAL);
			String text="Scanner " + newDevice.getName() + " Connected";
			intent.putExtra(EXTRA_SCANNER_ARRIVAL, text);
			sendBroadcast(intent);
		}
		@Override
		public void onDeviceRemoval(DeviceInfo deviceRemoved) {
			// TODO Auto-generated method stub
			
		}
		

		
		
	};

	
	protected ICommandContextCallback _onSetScanApiConfiguration=new ICommandContextCallback() {
		
		@Override
		public void run(ISktScanObject scanObj) {
			long result=scanObj.getMessage().getResult();
			if(!SktScanErrors.SKTSUCCESS(result)){
				String text="Error "+result+
						" setting ScanAPI configuration";
					Intent intent=new Intent(NOTIFY_ERROR_MESSAGE);
					intent.putExtra(EXTRA_ERROR_MESSAGE,text);
					sendBroadcast(intent);
			}
		}
	};
	
	protected ICommandContextCallback _onSetProfileConfigDevice=new ICommandContextCallback() {
		
		@Override
		public void run(ISktScanObject scanObj) {
			long result=scanObj.getMessage().getResult();
			if(!SktScanErrors.SKTSUCCESS(result)){
				String text="Error "+result+
						" setting Device profile Configuration";
					Intent intent=new Intent(NOTIFY_ERROR_MESSAGE);
					intent.putExtra(EXTRA_ERROR_MESSAGE,text);
					sendBroadcast(intent);
			}
		}
	};
	
	protected ICommandContextCallback _onSetDisconnectDevice=new ICommandContextCallback() {
		
		@Override
		public void run(ISktScanObject scanObj) {
			long result=scanObj.getMessage().getResult();
			if(!SktScanErrors.SKTSUCCESS(result)){
				String text="Error "+result+
						" disconnecting the device";
					Intent intent=new Intent(NOTIFY_ERROR_MESSAGE);
					intent.putExtra(EXTRA_ERROR_MESSAGE,text);
			}
		}
	};

	

	
}
