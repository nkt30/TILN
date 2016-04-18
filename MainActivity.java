package com.example.david.tilnappv3;


import java.io.File;
import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbManager;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import java.sql.Timestamp;
import java.util.Date;
import java.lang.*;

//import com.felhr.usbserial.UsbSerialDevice;
//import com.felhr.usbserial.UsbSerialInterface;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

// Main Activity with the Serial Connection Code
public class MainActivity extends Activity{

    //This is the serial connection code
    public final String ACTION_USB_PERMISSION = "com.hariharan.arduinousb.USB_PERMISSION";
    Button sendButton;
    TextView textView;
    EditText editText;
    UsbManager usbManager;
    UsbDevice device;
    UsbSerialDevice serialPort;
    UsbDeviceConnection connection;

    //Initializing the Timestamp variable to get time
    long Timestamp = System.currentTimeMillis();

    UsbSerialInterface.UsbReadCallback mCallback = new UsbSerialInterface.UsbReadCallback() {
        //Defining a Callback which triggers whenever data is read.
        @Override
        public void onReceivedData(byte[] arg0) {
            String data = null;
            try {
                data = new String(arg0, "UTF-8");
                data.concat("/n");
                tvAppend(textView, data);
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }


        }
    };
    private final BroadcastReceiver broadcastReceiver = new BroadcastReceiver() { //Broadcast Receiver to automatically start and stop the Serial connection.
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(ACTION_USB_PERMISSION)) {
                boolean granted = intent.getExtras().getBoolean(UsbManager.EXTRA_PERMISSION_GRANTED);
                if (granted) {
                    connection = usbManager.openDevice(device);
                    serialPort = UsbSerialDevice.createUsbSerialDevice(device, connection);
                    if (serialPort != null) {
                        if (serialPort.open()) { //Set Serial Connection Parameters.
                            setUiEnabled(true);
                            serialPort.setBaudRate(9600);
                            serialPort.setDataBits(UsbSerialInterface.DATA_BITS_8);
                            serialPort.setStopBits(UsbSerialInterface.STOP_BITS_1);
                            serialPort.setParity(UsbSerialInterface.PARITY_NONE);
                            serialPort.setFlowControl(UsbSerialInterface.FLOW_CONTROL_OFF);
                            serialPort.read(mCallback);
                            tvAppend(textView,"Serial Connection Opened!\n");

                        } else {
                            Log.d("SERIAL", "PORT NOT OPEN");
                        }
                    } else {
                        Log.d("SERIAL", "PORT IS NULL");
                    }
                } else {
                    Log.d("SERIAL", "PERM NOT GRANTED");
                }
            } //else if (intent.getAction().equals(UsbManager.ACTION_USB_DEVICE_ATTACHED)) {
                //onClickStart(startButton);}
            //else if (intent.getAction().equals(UsbManager.ACTION_USB_DEVICE_DETACHED)) {
                //onClickStop(stopButton);}
        }

        ;
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        usbManager = (UsbManager) getSystemService(this.USB_SERVICE);
        //startButton = (Button) findViewById(R.id.buttonStart);
        sendButton = (Button) findViewById(R.id.buttonSend);
        //clearButton = (Button) findViewById(R.id.buttonClear);
        //stopButton = (Button) findViewById(R.id.buttonStop);
        //editText = (EditText) findViewById(R.id.editText);
        textView = (TextView) findViewById(R.id.textView);
        setUiEnabled(false);
        IntentFilter filter = new IntentFilter();
        filter.addAction(ACTION_USB_PERMISSION);
        filter.addAction(UsbManager.ACTION_USB_DEVICE_ATTACHED);
        filter.addAction(UsbManager.ACTION_USB_DEVICE_DETACHED);
        registerReceiver(broadcastReceiver, filter);


    }

    public void setUiEnabled(boolean bool) {
        //startButton.setEnabled(!bool);
        sendButton.setEnabled(bool);
        //stopButton.setEnabled(bool);
        textView.setEnabled(bool);

    }

    public void onClickStart(View view) {

        HashMap<String, UsbDevice> usbDevices = usbManager.getDeviceList();
        if (!usbDevices.isEmpty()) {
            boolean keep = true;
            for (Map.Entry<String, UsbDevice> entry : usbDevices.entrySet()) {
                device = entry.getValue();
                int deviceVID = device.getVendorId();
                if (deviceVID == 0x2341)//Arduino Vendor ID
                {
                    PendingIntent pi = PendingIntent.getBroadcast(this, 0, new Intent(ACTION_USB_PERMISSION), 0);
                    usbManager.requestPermission(device, pi);
                    keep = false;
                } else {
                    connection = null;
                    device = null;
                }

                if (!keep)
                    break;
            }
        }


    }

    public void onClickSend(View view) {
        String string = editText.getText().toString();
        serialPort.write(string.getBytes());
        tvAppend(textView, "\nData Sent : " + string + "\n");

    }

    /**public void onClickStop(View view) {
        setUiEnabled(false);
        serialPort.close();
        tvAppend(textView,"\nSerial Connection Closed! \n");

    }*/

    /**public void onClickClear(View view) {
        textView.setText(" ");
    }*/

    private void tvAppend(TextView tv, CharSequence text) {
        final TextView ftv = tv;
        final CharSequence ftext = text;

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ftv.append(ftext);
            }
        });
    }

    //my take on how it appends:
    private void HexAppend(TextView tv, CharSequence text) {
        final TextView ftv = tv;
        //final CharSequence ftext = text;
        final color =  SpinnerActivity;
        final blinkrate = SpinnerActivity;
        final brightness = SeekBar;
        final timestamp = Timestamp;


        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ftv.append(timestamp, brightness, color, blinkrate);
            }
        });
    }
}

public class SpinnerActivity extends Activity{
    // Initializing the Spinner variables for ColorSpinner and BlinkrateSpinner
    Spinner ColorSpinner;
    Spinner BlinkrateSpinner;

    //Assigning to each Spinner/Dropdown the necessary choices
    String[] Colors = { "White", "Red", "Blue", "Green", "Purple", "Yellow"};
    String[] BlinkSpeeds = { "1/2 Second", "1/4 Second", "1 Minute", "2 Minutes", "No Blink"};


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //creating the 2 array adapters separately
        ArrayAdapter<String> adapter1 = new ArrayAdapter<String>(SpinnerActivity.this, android.R.layout.simple_spinner_item, Colors);
        ArrayAdapter<String> adapter2 = new ArrayAdapter<String>(SpinnerActivity.this, android.R.layout.simple_spinner_item, BlinkSpeeds);

        //Assigning the adapters to the corresponding Spinners
        ColorSpinner = (Spinner) findViewById(R.id.ColorSpinner);
        ColorSpinner.setAdapter(adapter1);
        ColorSpinner.setOnItemSelectedListener(this);

        BlinkrateSpinner = (Spinner) findViewById(R.id.BlinkrateSpinner);
        BlinkrateSpinner.setAdapter(adapter2);
        BlinkrateSpinner.setOnItemSelectedListener(this);
    }

    //When item in the Spinner is selected
    //Assign hex decimals
    public void onItemSelectedListener(AdapterView<?>long position, long arg1, long arg2) {
        switch (position.getId()) {
            case R.id.ColorSpinner:
                if (position = 0){
                    long arg1 = "0x1";
                    break;}

                else if(position = 1){
                    long arg1 = "0x2";
                    break;}

                else if (position = 2) {
                    long arg1 = "0x4";
                    break;}

                else if (position = 3) {
                    long arg1 = "0x8";
                    break;}

                else if (position = 4) {
                    long arg1 = "0xA";
                    break;}

                else if (position = 5) {
                    long arg1 = "0xC";
                    break;}


            case R.id.BlinkrateSpinner:
                if (position = 0){
                    //1/2sec
                    long arg2 = "0x1";
                    break;}

                else if(position = 1){
                    //1/4sec
                    long arg2 = "0x2";
                    break;}

                else if (position = 2) {
                    //1min
                    long arg2 = "0x3";
                    break;}

                else if (position = 3) {
                    //2min
                    long arg2 = "0x4";
                    break;}

                else if (position = 4) {
                    //no Blink
                    long arg2 = "0x5";
                    break;}
        }
    }

    public void onNothingSelected(AdapterView<?> position) {
        //do nothing
    }
}

public class SeekbarActivity extends Activity{

    private SeekBar seekBar;
    private TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initializeVariables();

        // Initialize the textview with '0'.
        textView.setText("Brightness: " + seekBar.getProgress() + "/" + seekBar.getMax());
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            int progress = 0;

            @Override
            public void onProgressChanged(SeekBar seekBar, int progresValue, boolean fromUser) {
                progress = progresValue;
                Toast.makeText(getApplicationContext(), "Changing seekbar's progress", Toast.LENGTH_SHORT).show();
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                Toast.makeText(getApplicationContext(), "Started tracking seekbar", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                textView.setText("Brightness: " + progress + "/" + seekBar.getMax());
                Toast.makeText(getApplicationContext(), "Stopped tracking seekbar", Toast.LENGTH_SHORT).show();
            }
        });
    }

            // A private method to help us initialize our variables.
    private void initializeVariables() {
        seekBar = (SeekBar) findViewById(R.id.BrightnessBar);
        textView = (TextView) findViewById(R.id.Feedback);
    }

}