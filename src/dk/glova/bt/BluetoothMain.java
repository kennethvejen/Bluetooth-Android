package dk.glova.bt;

import java.io.IOException;
import java.util.UUID;
import java.io.InputStream;
import java.io.OutputStream;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.Toast;
import android.widget.Button;

public class BluetoothMain extends Activity 
{
	
	
	// Debugging
    private static final String TAG = "BluetoothMain";
    
	// Local Bluetooth adapter.
	private BluetoothAdapter mBluetoothAdapter = null;
	
	// Local Bluetooth device.
	private BluetoothDevice  mBluetoothDevice = null;
	
	// Bluetooth Socket.
	protected BluetoothSocket mySocket;
	
	// Input and Output stream.
	private InputStream MyInStream; // Used for further purpose - getting values from toilet.
	private OutputStream MyOutStream;
    
	// Intent request code. Used in onStart.
	private static final int REQUEST_ENABLE_BT = 1;
	
	// Assigns a unique UUID for the Android Device. This ID is specifically used for SPP.
	private static final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
	
	Thread t = null;
	
	byte[] bufferIn = new byte[100];
	
	int bytesIn = 0;
	
	String strTempIn;
	String strBufferIn;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) 
    {
        super.onCreate(savedInstanceState);
        
        // Set up window layout
        setContentView(R.layout.main);
        
        // Initializes the buttons from main.xml.
        Button Button1 = (Button)findViewById(R.id.bV1);
        Button1.setOnTouchListener(ValveButton1);
        Button Button2 = (Button)findViewById(R.id.bV2);
        Button2.setOnTouchListener(ValveButton2);
        Button Button3 = (Button)findViewById(R.id.bV3);
        Button3.setOnTouchListener(ValveButton3);
        Button Button4 = (Button)findViewById(R.id.bV4);
        Button4.setOnTouchListener(ValveButton4);
        Button Button5 = (Button)findViewById(R.id.bV5);
        Button5.setOnTouchListener(ValveButton5);
        Button Button6 = (Button)findViewById(R.id.bV6);
        Button6.setOnTouchListener(ValveButton6);
        
        // Get local Bluetooth adapter
     	mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        
        if( mBluetoothAdapter == null )
        {
        	// Device does not support Bluetooth.
        	Toast.makeText(this, "Bluetooth is not available", Toast.LENGTH_LONG).show();
            finish();
            return;
        }
    }
    
	@Override
	protected void onStart() 
	{
		super.onStart();
		
        // See if any local Bluetooth adapter is available.
        BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
		
		// If bluetooth is supported on current device, continue.
		if (mBluetoothAdapter != null) 
     	{	        
			// See whether Bluetooth is turned on or off.	
			if (!mBluetoothAdapter.isEnabled()) 
			{
			    Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
			    startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
			}
    	}
		else Toast.makeText(this, "Device does not support Bluetooth", Toast.LENGTH_LONG).show();  
    }
	
	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		// If Bluetooth is enabled, establish connection and be process.
		if (mBluetoothAdapter.isEnabled()) 
        {
			// Request discover from BluetoothAdapter
			mBluetoothAdapter.startDiscovery();
    		
    		String address ="00:07:80:4B:37:FB";
         	mBluetoothDevice = mBluetoothAdapter.getRemoteDevice(address);
         	 
            BluetoothSocket tmp = null;
            
            // Try to create unique ID for Android device.
            try 
            {
                tmp = mBluetoothDevice.createRfcommSocketToServiceRecord(MY_UUID);
            } 
            
            catch (IOException e)
            {
                Log.e(TAG, "CONNECTION IN THREAD DIDNT WORK");
            }
            mySocket = tmp;
            
            // Try to connect to Bluetooth address (00:07...)
            try 
            {
				mySocket.connect();
			} 
            
            catch (IOException e) 
			{
            	Log.e(TAG, "CONNECTION IN THREAD DIDNT WORK 2");
			}   
			
            // Try to get input from connected Bluetooth module.
            try 
			{
				MyInStream = mySocket.getInputStream();
			} 
			
            catch (IOException e) 
			{
				e.printStackTrace();
			}
            
            // ****************************************************** //
            //														  //
            // Used for further purpose - getting values from toilet. //
            //														  //
            // ****************************************************** //
            /*if (t == null)
     		{
            	t = new Thread() 
         	    {
            		public void run() 
         	        {
            			while (true) 
         	            {
            				try 
         	            	{
         	            		bytesIn = MyInStream.read(bufferIn);
         	            		strTempIn = new String(bufferIn, 0, bytesIn); 
         	            		strBufferIn += strTempIn;
         	            	} 
         	            	  
         	            	catch (IOException e) 
         	            	{
         	            		e.printStackTrace();
         	            	}
         	            }
         	        };
         	    };
     		}
     	    t.start();  */
        }
	}

	
	// Waits for Valve1 button to be pressed.
	private OnTouchListener ValveButton1 = new OnTouchListener()
	{
		public boolean onTouch(View arg01, MotionEvent arg11) 
		{
			// TODO Auto-generated method stub
			if (mBluetoothAdapter != null) 
         	{
        	    // Device does not support Bluetooth
         		try 
         		{
					MyOutStream = mySocket.getOutputStream();
				} 
         		
         		catch (IOException e) 
				{
					// TODO Auto-generated catch block
				}     
			
				try 
				{
					// Button PRESSED and flag is true, send text through BT.
					if (arg11.getAction() == 0)
					{ 
						MyOutStream.write("11\r".getBytes());
					}
					
					// Button RELEASED and flag is true, send text through BT.
					if (arg11.getAction() == 1)
					{						
						MyOutStream.write("10\r".getBytes());
					};
				} 
				
				catch (IOException e) 
				{
					// TODO Auto-generated catch block
				}	
        	}
			return true;
			//else Toast.makeText(this, "Device does not support Bluetooth", Toast.LENGTH_LONG).show();
		}
	};
	
	// Waits for Valve2 button to be pressed.
	private OnTouchListener ValveButton2 = new OnTouchListener()
	{
		public boolean onTouch(View arg02, MotionEvent arg12) 
		{
			// TODO Auto-generated method stub
			if (mBluetoothAdapter != null) 
         	{
        	    // Device does not support Bluetooth
         		try 
         		{
					MyOutStream = mySocket.getOutputStream();
				} 
         		
         		catch (IOException e) 
				{
					// TODO Auto-generated catch block
				}     
			
				try 
				{
					// Button PRESSED and flag is true, send text through BT.
					if (arg12.getAction() == 0) 
					{ 
						MyOutStream.write("21\r".getBytes());
					}
					
					// Button RELEASED and flag is true, send text through BT.
					if (arg12.getAction() == 1)
					{						
						MyOutStream.write("20\r".getBytes());
					};
				} 
				
				catch (IOException e) 
				{
					// TODO Auto-generated catch block
				}	
        	}
			return true;
			//else Toast.makeText(this, "Device does not support Bluetooth", Toast.LENGTH_LONG).show();
		}
	};
	
	// Waits for Valve3 button to be pressed.
	private OnTouchListener ValveButton3 = new OnTouchListener()
	{
		public boolean onTouch(View arg03, MotionEvent arg13) 
		{
			// TODO Auto-generated method stub
			if (mBluetoothAdapter != null) 
         	{
        	    // Device does not support Bluetooth
         		try 
         		{
					MyOutStream = mySocket.getOutputStream();
				} 
         		
         		catch (IOException e) 
         		{
					// TODO Auto-generated catch block
				}     
			
				try 
				{
					// Button PRESSED and flag is true, send text through BT.
					if (arg13.getAction() == 0)
					{ 
						MyOutStream.write("31\r".getBytes());
					}
					
					// Button RELEASED and flag is true, send text through BT.
					if (arg13.getAction() == 1)
					{						
						MyOutStream.write("30\r".getBytes());
					};
				} 
				
				catch (IOException e) 
				{
					// TODO Auto-generated catch block
				}	
        	}
			return true;
			//else Toast.makeText(this, "Device does not support Bluetooth", Toast.LENGTH_LONG).show();
		}
	};
	
	// Waits for Valve4 button to be pressed.
	private OnTouchListener ValveButton4 = new OnTouchListener()
	{
		public boolean onTouch(View arg04, MotionEvent arg14) 
		{
			// TODO Auto-generated method stub
			if (mBluetoothAdapter != null) 
         	{
        	    // Device does not support Bluetooth
         		try 
         		{
					MyOutStream = mySocket.getOutputStream();
				} 
         		
         		catch (IOException e) 
         		{
					// TODO Auto-generated catch block
				}     
			
				try 
				{
					// Button PRESSED and flag is true, send text through BT.
					if (arg14.getAction() == 0)
					{ 
						MyOutStream.write("41\r".getBytes());
					}
					
					// Button RELEASED and flag is true, send text through BT.
					if (arg14.getAction() == 1)
					{						
						MyOutStream.write("40\r".getBytes());
					};
				} 
				
				catch (IOException e) 
				{
					// TODO Auto-generated catch block
				}	
        	}
			return true;
			//else Toast.makeText(this, "Device does not support Bluetooth", Toast.LENGTH_LONG).show();
		}
	};
	
	// Waits for Valve5 button to be pressed.
	private OnTouchListener ValveButton5 = new OnTouchListener()
	{
		public boolean onTouch(View arg05, MotionEvent arg15) 
		{
			// TODO Auto-generated method stub
			if (mBluetoothAdapter != null) 
         	{
        	    // Device does not support Bluetooth
         		try 
         		{
					MyOutStream = mySocket.getOutputStream();
				} 
         		
         		catch (IOException e) 
         		{
					// TODO Auto-generated catch block
				}     
			
				try 
				{
					// Button PRESSED and flag is true, send text through BT.
					if (arg15.getAction() == 0) 
					{ 
						MyOutStream.write("51\r".getBytes());
					}
					
					// Button RELEASED and flag is true, send text through BT.
					if (arg15.getAction() == 1)
					{						
						MyOutStream.write("50\r".getBytes());
					};
				} 
				
				catch (IOException e) 
				{
					// TODO Auto-generated catch block
				}	
        	}
			return true;
			//else Toast.makeText(this, "Device does not support Bluetooth", Toast.LENGTH_LONG).show();
		}
	};
	
	// Waits for Valve6 button to be pressed.
	private OnTouchListener ValveButton6 = new OnTouchListener()
	{
		public boolean onTouch(View arg06, MotionEvent arg16) 
		{
			// TODO Auto-generated method stub
			if (mBluetoothAdapter != null) 
         	{
        	    // Device does not support Bluetooth
         		try 
         		{
					MyOutStream = mySocket.getOutputStream();
				} 
         		
         		catch (IOException e) 
         		{
					// TODO Auto-generated catch block
				}     
			
				try 
				{
					// Button PRESSED and flag is true, send text through BT.
					if (arg16.getAction() == 0)
					{ 
						MyOutStream.write("61\r".getBytes());
					}
					
					// Button RELEASED and flag is true, send text through BT.
					if (arg16.getAction() == 1)
					{						
						MyOutStream.write("60\r".getBytes());
					};
				} 
				
				catch (IOException e) 
				{
					// TODO Auto-generated catch block
				}	
        	}
			return true;
			//else Toast.makeText(this, "Device does not support Bluetooth", Toast.LENGTH_LONG).show();
		}
	};
	
	@Override
	protected void onDestroy() 
	{
		// TODO Auto-generated method stub
		super.onDestroy();
		
		try
		{
			mySocket.close();
		} 
		 	
	 	catch (IOException e) 
		{
	 		// TODO Auto-generated catch block
		}
	    
		// Make sure we're not doing discovery anymore
	    if (mBluetoothAdapter != null) 
	    {
	    	mBluetoothAdapter.cancelDiscovery();
	    }
	}
}