//Nick Turner
//LED Control 
//3-25-2016
//Ver:0.2




// Set the initial state of the LED cathodes.  
int ledState = LOW;
int ledState2 = LOW;
int ledState3 = LOW;

// Initialize the LED cathodes to specific PWM pins on the Lilypad.
int ledR = 3;
int ledG = 5;
int ledB = 6;
int ledW = 9;

int brightness = 0;    // how bright the LED is
int color = 0;  //the hex command converted to binary
int fadeAmount = 5;    // how many points to fade the LED by
unsigned long previousMillis = 0;        // will store last time LED was updated
unsigned long previousMillis2 = 0;  
unsigned long previousMillis3 = 0;


//The following are commands received from the smartphone, transmitted commands,and stored commands.
long incomingCommand = 0xFFFFFFFF;
long outgoingCommand = 0xFFFFFFFF;
long oldCommand = 0xFFFFFFFF;
int colorCommand = 0xF;
int brightCommand = 0x0A;
int blinkCommand = 0xF;


//Initialize the color variables.
int colorR = 0;
int colorG = 0;
int colorB = 0;
int colorW = 0;

void setup() {
  pinMode(ledW, OUTPUT);
  pinMode(ledR, OUTPUT);
  pinMode(ledG, OUTPUT);
  pinMode(ledB, OUTPUT);   

}

void loop() {
  // put your main code here, to run repeatedly:

//Receive: Receives command from smartphone

//Checknew: Check to see if command is new

//StoreCommand: Store command if it is new

//ParseCommand: Decodes the new command



//-----------------------------------------------------------------------------------------------
//ExecuteCommand: Execute the command


 
//Blink: This function will set the blink rate of the LED according the instruction received by the smartphone

//Brightness: This function converts the hex brightness command to an int between 0 and 255.
char *ptr;
String str1 = String(brightCommand);
const char *s = str1.c_str();
brightness = strtol(s,&ptr,10);

//Color: Changes the color of the LED according to the instructions received. Also sets the desired brighness.
if(colorCommand == 0x1)
{
  analogWrite(ledW, brightness);
}
else if (colorCommand == 0x2)
{
  analogWrite(ledB, brightness);
}
else if (colorCommand == 0x4)
{
  analogWrite(ledG, brightness);
}
else if (colorCommand == 0x8)
{
  analogWrite(ledR, brightness);
}
else if (colorCommand == 0xA)
{
  analogWrite(ledR, brightness);
  analogWrite(ledB, brightness);
}
else if (colorCommand == 0xC)
{
  analogWrite(ledR, brightness);
  analogWrite(ledG, brightness);
}
else if (colorCommand == 0xF)
{
  analogWrite(ledR, brightness);
  analogWrite(ledG, brightness);
  analogWrite(ledB, brightness);
  analogWrite(ledW, brightness);
}

//Serial.println(brightness);
//delay(1000);

//-----------------------------------------------------------------------------------------------------------------

//Transmit: This will package the new instruction and transmit it to other devices in range.

//Sleep? Sleeps for a specified amount of time in between commands.


}
