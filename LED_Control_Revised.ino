
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
#define BLUE   {0, 1, 0, 0}
#define GREEN  {1, 0, 0, 0}
#define RED    {0, 0, 1, 0}
#define PURPLE {0, 1, 1, 0}
#define YELLOW {1, 0, 1, 0}
#define OFF    {0, 0, 0, 0}

#define LED_G  0
#define LED_B  1
#define LED_R  2
#define LED_W  3

// LED COLOR-PIN ASSOCIATION ARRAY
int led_pins[4] = {2, 3, 4, 5};
//              = {G, B, R, W}

// ALGORITHM VARIABLES
int LED_status[4] = {LOW, LOW, LOW, LOW}; // LED on/off state, active HIGH.
const int color_settings[7][4] = {OFF, GREEN, BLUE, RED, WHITE, PURPLE, YELLOW}; // Complete LED configs for each color.
unsigned long oldCommand = 0x00000000; // Command input from control device.
unsigned long newCommand = 0x00000000; // Assumed to be 4 byte hex value.
static int brightness = 0;                    // how bright the LED is.
int color = 0;                         // the hex command converted to binary.
int blink_rate = 0;                    // Blink rate.
unsigned long previousMillis = 0;      // will store last time LED was updated.
unsigned int timer_preload = 57723;             // Default set to 1 second period. To calculate value for a desired delay, just use
                                       // timer_preload = 65536 - 15625x, where x is the desired time in seconds.
/*
INSTRUCTION NOTES: 4-Byte hex value (0x00000000). Must be received as 
a string representation of a hex value (eg. "0x00012411", half second 
period of the color green at 164 brightness). 

Two most significant bytes are timestamp: 0x0000----
Second byte is brightness:                0x----00--
Top 4 bits of last byte is color:         0x------0-
bottom 4 bits of last byte is blink-rate: 0x-------0

Timestamp: Time in milliseconds that the instruction was sent. Used as
           a reference to determine if this is a new command. 

Brightness: 8 bit resolution (0-256).

Color:  0 = OFF
        1 = GREEN
        2 = BLUE
        3 = RED
        4 = WHITE
        5 = PURPLE
        6 = YELLOW

Blink Rate:  0 = Quarter second period.
             1 = half second period.
             2 = One second period.
             3 = Two second period.
             4 = Four second period.
*/

// SETUP
void setup() {
  pinMode(led_pins[LED_G], OUTPUT);
  pinMode(led_pins[LED_B], OUTPUT);
  pinMode(led_pins[LED_R], OUTPUT);
  pinMode(led_pins[LED_W], OUTPUT);

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
      if (LED_status[i])
      {
        analogWrite(led_pins[i], 0);
        LED_status[i] = !LED_status[i];
      } else {
        analogWrite(led_pins[i], brightness);
        LED_status[i] = !LED_status[i];
      }
    }
  }
}

// FUNCTIONS
int recv_serial() // Returns 1 if data was received, returns 0 otherwise.
{
  if (Serial1.available() > 0){
    newCommand = 0;
    char buff[8];
    int i = 0;
    
    while (Serial1.available() > 0){
      buff[i] = Serial1.read(); // read the incoming byte:
      i++;
      delay(5);
    }
    newCommand = strtoul(buff, NULL, 16);
    
    // say what you got:
    Serial1.print("I received: ");
    Serial1.println(newCommand, HEX);
    return 1;
  } else return 0;
}

int set_led() // Returns 1 if command is new, returns 0 otherwise.
{
  // If received command is newer, deconstruct into individual instructions.
  if (newCommand > oldCommand)
  {
    oldCommand = newCommand;
    blink_rate = oldCommand & BLINK;
    color = (oldCommand & COLOR) >> COLORSHIFT;
    brightness = (oldCommand & BRIGHTNESS) >> BRIGHTNESSSHIFT;
    Serial1.println("New command: Parsed");
    return 1;
  } else {
    Serial1.println("Old Command: Discarding...");
    return 0;
  }
}

void set_interrupt()
{
  switch (blink_rate) { // Set interrupt timer to result in a period of...
    case 0:
      timer_preload = QUARTER_S;
      break;
    case 1:
      timer_preload = HALF_S;
      break;
    case 2:
      timer_preload = ONE_S;
      break;
    case 3:
      timer_preload = TWO_S;
      break;
    case 4:
      timer_preload = FOUR_S;
      break;
    default: 
      break;
  }
}

// MAIN LOOP
void loop() {
  
  // Get serial input if available.
  if (recv_serial())
  {
    if (set_led())
    {
      noInterrupts();  // disable all interrupts
      set_interrupt();
      analogWrite(led_pins[LED_G], 0);
      analogWrite(led_pins[LED_B], 0);
      analogWrite(led_pins[LED_B], 0);
      analogWrite(led_pins[LED_W], 0);
      for (int i = 0; i < 4; i++) {LED_status[i] = 0;}
      TIMER1_OVF_vect;
      interrupts();    // enable all interrupts
    }
  }

  ///////////////
  delay(20);   // This Timer does not need to be included once integrated in 6LoWPAN code.
  ///////////////
}
