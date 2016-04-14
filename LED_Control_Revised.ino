
// MACROS
#define BLINK           0x0000000F
#define COLOR           0x000000F0
#define BRIGHTNESS      0x0000FF00
#define TIMESTAMP       0xFFFF0000
#define COLORSHIFT      4
#define BRIGHTNESSSHIFT 8
#define TIMESTAMPSHIFT  16

#define QUARTER_S 63583
#define HALF_S    61630
#define ONE_S     57723
#define TWO_S     49911
#define FOUR_S    34286


#define WHITE  {0, 0, 0, 1}
#define BLUE   {0, 0, 1, 0}
#define GREEN  {0, 1, 0, 0}
#define RED    {1, 0, 0, 0}
#define PURPLE {1, 0, 1, 0}
#define YELLOW {1, 1, 0, 0}
#define OFF    {0, 0, 0, 0}

// LED COLOR-PIN ASSOCIATION
int ledG = 2;
int ledB = 3;
int ledR = 4;
int ledW = 5;

// ALGORITHM VARIABLES
int LED_status = LOW; // LED on/off state, active HIGH.
const int color_settings[7][4] = {OFF, GREEN, BLUE, RED, WHITE, PURPLE, YELLOW}; // Complete LED configs for each color.
unsigned long oldCommand = 0x00000000; // Command input from control device.
unsigned long newCommand = 0x00000000; // Assumed to be 8 bytes hex value.
int brightness = 0;                    // how bright the LED is.
int color = 0;                         // the hex command converted to binary.
int blink_rate = 0;                    // Blink rate.
unsigned long previousMillis = 0;      // will store last time LED was updated.
int timer_preload = 57723;             // Default set to 1 second period. To calculate value for a desired delay, just use
                                       // timer_preload = 65536 - 15625x, where x is the desired time in seconds.

// SETUP
void setup() {
  pinMode(ledR, OUTPUT);
  pinMode(ledG, OUTPUT);
  pinMode(ledB, OUTPUT);
  pinMode(ledW, OUTPUT);

  // initialize timer1 
  noInterrupts();           // disable all interrupts
  TCCR1A = 0;
  TCCR1B = 0;

  TCNT1 = 57723;            // preload timer 65536-16MHz/256/2Hz
  TCCR1B |= (1 << CS12);    // 1024 prescaler 
  TCCR1B |= (1 << CS10);    // ^
  TIMSK1 |= (1 << TOIE1);   // enable timer overflow interrupt
  interrupts();             // enable all interrupts

  Serial.begin(9600);
  Serial1.begin(9600);
}

// INTERRUPT SERVICE ROUTINE
ISR(TIMER1_OVF_vect)        // interrupt service routine for maintaining blink rate.
{
  TCNT1 = timer_preload;            // preload timer
  // Blink LEDs needed for specific color.
  for (int i = 0; i < 4; i++)
  {
    if (color_settings[color][i])
    {
      if (LED_status)
      {
        analogWrite(ledR, 0);
        LED_status = !LED_status;
      } else {
        analogWrite(ledR, brightness);
        LED_status = !LED_status;
      }
    }
  }
}

// FUNCTIONS
void recv_serial() // send data only when you receive data:
{
  if (Serial1.available() > 0) {
          // read the incoming byte:
          newCommand = Serial1.read();
  
          // say what you got:
          Serial1.print("I received: ");
          Serial1.println(newCommand, HEX);
  }
  return;
}

void set_led(int color, int brightness, int blink_rate)
{
  // Get serial input if available.
  recv_serial();

  // If received command is newer, deconstruct into individual instructions.
  if (newCommand > oldCommand and newCommand != -1)
  {
    oldCommand = newCommand;
    blink_rate = oldCommand & BLINK;
    color = (oldCommand >> COLORSHIFT) & COLOR;
    brightness = (oldCommand >> BRIGHTNESSSHIFT) & BRIGHTNESS;
    Serial.println("New command: Parsed");
  } else {
    Serial1.println("Old Command: Discarding...");
  }

  
}

// MAIN LOOP
void loop() {
  // put your main code here, to run repeatedly:

}
