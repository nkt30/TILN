//Nick Turner
//LED Control 
//3-21-2016
//Ver:0.1

void setup() {

// Set the initial state of the LED cathodes.  
int ledState = LOW;
int ledState2 = LOW;
int ledState3 = LOW;

// Initialize the LED cathodes to specific PWM pins on the Lilypad.
int ledW = 3;
int ledR = 5;
int ledG = 6;
int ledB = 9;


int brightness = 0;    // how bright the LED is
int fadeAmount = 5;    // how many points to fade the LED by
unsigned long previousMillis = 0;        // will store last time LED was updated
unsigned long previousMillis2 = 0;  
unsigned long previousMillis3 = 0;


//The following are commands received from the smartphone, transmitted commands,and stored commands.
int incomingCommand = 0xFFFF;
int outgoingCommand = 0xFFFF;
int oldCommand = 0xFFFF;

}

void loop() {
  // put your main code here, to run repeatedly:

//Receive: Receives command from smartphone

//Checknew: Check to see if command is new

//StoreCommand: Store command if it is new

//ParseCommand: Decodes the new command

//ExecuteCommand: Execute the command

//Blink: This function will set the blink rate of the LED according the instruction received by the smartphone

//Brightness: This function will set the brightness of the LED according to the instruction received by the smartphone

//Color: Changes the color of the LED according to the instructions received.

//Commands?

//Transmit: This will package the new instruction and transmit it to other devices in range.

//Sleep? Sleeps for a specified amount of time in between commands.




}
