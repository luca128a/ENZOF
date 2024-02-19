#include <SoftwareSerial.h>
#include <TimerThree.h>
SoftwareSerial blue(10, 11); 
#define BUFFER_SIZE 10
int adcValue;
int bufferM[BUFFER_SIZE];
bool bufferFlag = false;
bool sendFlag = false;

void setup() {
  pinMode(A0, INPUT);
  Serial.begin(9600);
  blue.begin(9600);
  EICRA &= ~(1 << ISC01); 
  Timer3.initialize(1000); 
  Timer3.attachInterrupt(Muestreo);
}

void loop(){
  static unsigned int pos = 0;
  if (blue.available()) {
    char signal = blue.read();
    Serial.println(signal);
    if (signal == '1') 
    {
      sendFlag = true;
    } else if(signal != '1'){
      sendFlag = false;
    }
  }
    if(EICRA & (1 << ISC01)){
    bufferM[pos] = analogRead(A0);
    
    pos += 1;  
    if (pos == BUFFER_SIZE){
      bufferFlag = true;
      pos = 0;
    }
    EICRA &= ~(1 << ISC01);
  }
   if (sendFlag && bufferFlag) 
   {
    for (unsigned int i=0; i<BUFFER_SIZE; i++) 
    {
      byte highByte = bufferM[i] & 0xFF;
      byte lowByte = (bufferM[i] >> 8) & 0xFF;
      blue.write(highByte);
      blue.write(lowByte);
      Serial.println(bufferM[i]);
    }
    bufferFlag = false;
  }
 }
 
void Muestreo() {
  EICRA |= (1 << ISC01); 
}
