package com.hariharan.arduinousb;

import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Toast;


//color
public class CustomOnItemSelectedListener implements OnItemSelectedListener {

    public String ColorHex;

    public void onItemSelected(AdapterView<?> parent, View view, int pos, String id) {
        //add hex values for color here <---- These values don't need the 0x in front of them you only need it in front of the final value
        switch (pos){
            case 0:
                //white
                ColorHex = "1";
                break;
            case 1:
                //red
                ColorHex= "2";
                break;
            case 2:
                //green
                ColorHex= "3";
                break;
            case 3:
                //blue
                ColorHex= "4";
                break;
            case 4:
                //yellow
                ColorHex= "5";
                break;
            case 5:
                //purple;
                ColorHex= "6";
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
