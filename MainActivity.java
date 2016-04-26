package com.hariharan.arduinousb;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.felhr.usbserial.UsbSerialDevice;
import com.felhr.usbserial.UsbSerialInterface;

import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;




public class MainActivity extends Activity {
    public final String ACTION_USB_PERMISSION = "com.hariharan.arduinousb.USB_PERMISSION";

    //Initializing global variables for Spinners, Seekbars, Buttons
    private Spinner ColorSpinner, BlinkrateSpinner;
    private Button buttonSend;
    private SeekBar BrightnessBar;
    private Button startButton;
    private TextView textView;
    public String BlinkHex, ColorHex, SliderHex, HexFinal;
    public String TimeHex = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(new Date());

    UsbManager usbManager;
    UsbDevice device;
    UsbSerialDevice serialPort;
    UsbDeviceConnection connection;



    // setting up serial connection
    UsbSerialInterface.UsbReadCallback mCallback = new UsbSerialInterface.UsbReadCallback() { //Defining a Callback which triggers whenever data is read.
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

    // setting up serial connection p2
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
            } else if (intent.getAction().equals(UsbManager.ACTION_USB_DEVICE_ATTACHED)) {
                onClickStart(startButton);
            } else if (intent.getAction().equals(UsbManager.ACTION_USB_DEVICE_DETACHED)) {
                //onClickStop(stopButton);

            }
        }

        ;
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        usbManager = (UsbManager) getSystemService(this.USB_SERVICE);
        startButton = (Button) findViewById(R.id.buttonStart);
        buttonSend = (Button) findViewById(R.id.buttonSend);

        addItemsOnBlinkrateSpinner();
        addItemsOnColorSpinner();
        addListenerOnButton();
        addListenerOnSpinnerItemSelection();

        BrightnessBar = (SeekBar) findViewById(R.id.BrightnessBar);
        textView = (TextView) findViewById(R.id.textView1);

        //Intent intent = new Intent(this, LoginActivity.class);
        //startActivity(intent);

        setUiEnabled(false);
        IntentFilter filter = new IntentFilter();
        filter.addAction(ACTION_USB_PERMISSION);
        filter.addAction(UsbManager.ACTION_USB_DEVICE_ATTACHED);
        filter.addAction(UsbManager.ACTION_USB_DEVICE_DETACHED);
        registerReceiver(broadcastReceiver, filter);

        // Initialize the textview with '0' for brightness bar
        textView.setText(BrightnessBar.getProgress() + "/" + BrightnessBar.getMax());

        // tracks slider value
        BrightnessBar.setOnSeekBarChangeListener(
                new SeekBar.OnSeekBarChangeListener() {
                    int progress = 0;
                    //String SliderHex;

                    @Override
                    public void onProgressChanged(SeekBar seekBar, int progresValue, boolean fromUser) {
                        progress = progresValue;
                    }

                    @Override
                    public void onStartTrackingTouch(SeekBar seekBar) {
                        // Do something here, if you want to do anything at the start of
                        // touching the seekbar
                    }

                    @Override
                    public void onStopTrackingTouch(SeekBar seekBar) {
                        // Display the value in textview
                        textView.setText(progress + "/" + seekBar.getMax() + "\n" + "\n");

                        //add hex values here Integer.toHexString(num);
                        SliderHex =  Integer.toHexString(num); //String.valueOf(progress);
                    }
                });
    }

    public void setUiEnabled(boolean bool) {
        startButton.setEnabled(!bool);
        buttonSend.setEnabled(bool);
        //stopButton.setEnabled(bool);
        //textView.setEnabled(bool);

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

    // add items into spinner dynamically
    public void addItemsOnColorSpinner() {

        ColorSpinner = (Spinner) findViewById(R.id.ColorSpinner);
        List<String> list = new ArrayList<String>();
        list.add("White");
        list.add("Red");
        list.add("Blue");
        list.add("Green");
        list.add("Yellow");
        list.add("Purple");
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, list);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        ColorSpinner.setAdapter(dataAdapter);
    }

    // add items into spinner dynamically
    public void addItemsOnBlinkrateSpinner() {

        BlinkrateSpinner = (Spinner) findViewById(R.id.BlinkrateSpinner);
        List<String> list = new ArrayList<String>();
        list.add("No Blink");
        list.add("1/2 Second");
        list.add("1/4 Second");
        list.add("1 Second");
        list.add("2 Second");
        list.add("4 Second");
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, list);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        BlinkrateSpinner.setAdapter(dataAdapter);
    }

    public void addListenerOnSpinnerItemSelection() {
        ColorSpinner = (Spinner) findViewById(R.id.ColorSpinner);
        ColorSpinner.setOnItemSelectedListener(new CustomOnItemSelectedListener());
        BlinkrateSpinner = (Spinner) findViewById(R.id.BlinkrateSpinner);
        BlinkrateSpinner.setOnItemSelectedListener(new CustomOnItemSelectedListener2());
    }

    // get the selected dropdown list value
    public void addListenerOnButton() {

        ColorSpinner = (Spinner) findViewById(R.id.ColorSpinner);
        BlinkrateSpinner = (Spinner) findViewById(R.id.BlinkrateSpinner);
        BrightnessBar = (SeekBar) findViewById(R.id.BrightnessBar);
        buttonSend = (Button) findViewById(R.id.buttonSend);

        buttonSend.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                Toast.makeText(MainActivity.this, "Sending Instructions : " + "\nColor chosen : " +
                        String.valueOf(ColorSpinner.getSelectedItem()) + "\nBlink rate chosen : " +
                        String.valueOf(BlinkrateSpinner.getSelectedItem()) + "\nBrightness chosen : " +
                        String.valueOf(BrightnessBar.getProgress()), Toast.LENGTH_SHORT).show();
            }

        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void onClickSend(View view) {

        HexFinal = TimeHex + ColorHex + BlinkHex + SliderHex;
        tvAppend(textView, "\nData Sent : " + HexFinal + "\n");
    }



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

}
