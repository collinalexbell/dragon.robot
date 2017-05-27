#include <Servo.h>
#include <SoftwareSerial.h>
#include "choreo.h"


SoftwareSerial BTSerial (5, 6);

void setup(){
  Serial.begin(9600);
  SERIAL.begin(9600);
  choreo_setup();
  serialFlush();
  delay(2000);
  SERIAL.println("OKK");
  Serial.println("OTAY");
}

void loop(){
  choreo_loop();
}

