package com.hariharan.arduinousb;


import android.view.View;
import android.widget.AdapterView;


//blinkrate
public class CustomOnItemSelectedListener2 implements AdapterView.OnItemSelectedListener {

    public String BlinkHex;

    public void onItemSelected(AdapterView<?> parent, View view, int pos, String id) {
        //add hex values here for blinkrate
        switch (pos){
            case 0:
                //no blink
                BlinkHex = "1";
                break;
            case 1:
                //1/2 second
                BlinkHex = "2";
                break;
            case 2:
                //1/4 second
                BlinkHex = "3";
                break;
            case 3:
                //1 second
                BlinkHex = "4";
                break;
            case 4:
                //2 second
                BlinkHex = "5";
                break;
            case 5:
                //4 second;
                BlinkHex = "6";
                break;
        }
        //Toast.makeText(parent.getContext(), "Blink rate : " + parent.getItemAtPosition(pos).toString(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

    }

    @Override
    public void onNothingSelected(AdapterView<?> arg0) {
        // TODO Auto-generated method stub

    }

}
