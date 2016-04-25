package com.hariharan.arduinousb;

import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Toast;


//color
public class CustomOnItemSelectedListener implements OnItemSelectedListener {

    public String ColorHex;

    public void onItemSelected(AdapterView<?> parent, View view, int pos, String id) {
        //add hex values for color here
        switch (pos){
            case 0:
                //white
                ColorHex = "0x1";
                break;
            case 1:
                //red
                ColorHex= "0x2";
                break;
            case 2:
                //green
                ColorHex= "0x3";
                break;
            case 3:
                //blue
                ColorHex= "0x4";
                break;
            case 4:
                //yellow
                ColorHex= "0x5";
                break;
            case 5:
                //purple;
                ColorHex= "0x6";
                break;
        }
        //Toast.makeText(parent.getContext(), "Color : " + parent.getItemAtPosition(pos).toString(), Toast.LENGTH_SHORT).show();


    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

    }

    @Override
    public void onNothingSelected(AdapterView<?> arg0) {
        // TODO Auto-generated method stub

    }

}