#include <Arduino.h>
#include <WiFi.h>
#include <AsyncTCP.h>
#include <ESPAsyncWebServer.h>
#include "SPIFFS.h"
#include <Stepper.h>
#include <ESP32Servo.h>

#define ONBOARD_LED  2


//stepper A pins
#define A1 16
#define A2 17
#define A3 5
#define A4 18

//stepper B1 pins
#define B1 15
#define B2 2
#define B3 0
#define B4 4

////stepper B2 pins
//#define B5 19
//#define B6 21
//#define B7 22
//#define B8 23

//stepper C pins
#define C1 25
#define C2 26
#define C3 27
#define C4 14

//stepper D pins
#define D1 27
#define D2 28
#define D3 29
#define D4 30

//servo E pin
#define servo_E_pin 12

//servo F pin
#define servo_F_pin 13



bool WIFIENABLED = true;
bool SERIALCONTROLENABLED = false;


const int stepsPerRevolution = 200;
Stepper stepper_A(stepsPerRevolution, A1, A2, A3, A4);//index 1
Stepper stepper_B1(stepsPerRevolution, B1, B2, B3, B4);//index 2
//Stepper stepper_B2(stepsPerRevolution, B5, B6, B7, B8);//index 2
Stepper stepper_C(stepsPerRevolution, C1, C2, C3, C4);// index 3
//to do Stepper stepper_D(stepsPerRevolution, D1, D3, D2, D4);//figure out how to use wrist joint //index 4
const int stepperSpeed = 60;
//const int servoSpeed = 60;

Servo servo_E;//index 5
int servo_E_pos = 0;

Servo servo_F;//index 6
int servo_F_pos = 0;


String message = "";

const char* ssid     = "1221-217";
const char* password = "jwk773nynwv5";

//const char* ssid = "ChristianSmells";
//const char* password = "HairyCourtney202";

//open port 80
AsyncWebServer server(80);

AsyncWebSocket ws("/ws");

String A_Direction ="STOP";//shoulder rotation
String B_Direction ="STOP";//shoulder joint
String C_Direction ="STOP";//elbow joint
String D_Direction ="STOP";//wrist rotation
String E_Direction ="STOP";//wrist joint
String F_Direction ="STOP";//claw movement

String axisIndex;
String steps;
String direction = "STOP";

bool newRequest = false;

// Initialize SPIFFS
void initSPIFFS() {
  if (!SPIFFS.begin(true)) {
    Serial.println("An error has occurred while mounting SPIFFS");
  }
  else{
    Serial.println("SPIFFS mounted successfully");
  }
}

// Initialize WiFi
void initWiFi() 
{
  WiFi.mode(WIFI_STA);
  WiFi.begin(ssid, password);
  Serial.print("Connecting to WiFi ..");
  
  while (WiFi.status() != WL_CONNECTED) 
  {
    delay(500);
    Serial.print('.');
  }
  
  Serial.println("IP address: ");
  Serial.println(WiFi.localIP());
}

void notifyClients(String state) 
{
  ws.textAll(state);
}


void handleWebSocketMessage(void *arg, uint8_t *data, size_t len) 
{
  AwsFrameInfo *info = (AwsFrameInfo*)arg;
  if (info->final && info->index == 0 && info->len == len && info->opcode == WS_TEXT) {
    data[len] = 0;
    message = (char*)data;
    Serial.println(message);

    axisIndex = message.substring(0, message.indexOf("&"));
    steps = message.substring(message.indexOf("&")+1, message.lastIndexOf("&"));
    direction = message.substring(message.lastIndexOf("&")+1, message.length());
    Serial.print("axis index:");
    Serial.println(axisIndex);
    Serial.print("steps:");
    Serial.println(steps);
    Serial.print("direction:");
    Serial.println(direction);
    notifyClients(direction);
    newRequest = true;
  }
}

void onEvent(AsyncWebSocket *server, AsyncWebSocketClient *client, AwsEventType type, void *arg, uint8_t *data, size_t len) {
  switch (type) {
    case WS_EVT_CONNECT:
      Serial.printf("WebSocket client #%u connected from %s\n", client->id(), client->remoteIP().toString().c_str());
      //Notify client of motor current state when it first connects
      notifyClients(direction);
      break;
    case WS_EVT_DISCONNECT:
      Serial.printf("WebSocket client #%u disconnected\n", client->id());
      break;
    case WS_EVT_DATA:
        handleWebSocketMessage(arg, data, len);
        break;
    case WS_EVT_PONG:
    case WS_EVT_ERROR:
     break;
  }
}

void initWebSocket() {
  ws.onEvent(onEvent);
  server.addHandler(&ws);
}

void setAxisPosition(int axisIndex, int steps)
{
  switch (axisIndex)
  {
    case 1://shoulder rotation
        Serial.print("shoulder rotation: ");
        Serial.println(steps);
        stepper_A.step(steps);
        delay(500);
        break;
        
    case 2://shoulder movement
        Serial.print("shoulder movement: ");
        Serial.println(steps);
        stepper_B1.step(steps);
        delay(500);
        break;
        
    case 3://elbow movement
        Serial.print("elbow movement: ");
        Serial.println(steps);
        stepper_C.step(steps);
        delay(500);
        break;

    case 4://wrist movement
        Serial.print("wrist rotation: ");
        Serial.println(steps);
        if(steps > 0 && steps < 180)
        {
          for (servo_E_pos = 0; servo_E_pos < 180; servo_E_pos += 1)
           {
             servo_E.write(servo_E_pos);
             delay(15);     
           }
        }
        else if(steps < 0 && steps > -180)
        {
          for (servo_E_pos = 180; servo_E_pos > 0; servo_E_pos -= 1)
           {
             servo_E.write(servo_E_pos);
             delay(15);     
           }
        }
        else
        {
          Serial.print("invalid axis e position. Servo E: ");
          Serial.println(steps);
        }
        break;

    case 5://actuate claw
        Serial.print("wrist movement: ");
        Serial.println(steps);
        if(steps > 0 && steps < 180)
        {
          for (servo_F_pos = 0; servo_F_pos < 180; servo_F_pos += 1)
           {
             servo_F.write(servo_F_pos);
             delay(15);     
           }
        }
        else if(steps < 0 && steps > -180)
        {
          for (servo_F_pos = 180; servo_F_pos > 0; servo_F_pos -= 1)
           {
             servo_F.write(servo_F_pos);
             delay(15);     
           }
        }
        else
        {
          Serial.print("invalid axis F position. Servo F: ");
          Serial.println(steps);
        }
        break;

        
     case 6://wrist rotation
        Serial.print("actuate claw: ");
        Serial.println(steps);
        //stepper_D.step(steps);
        delay(500);
        break;
  }
}
void setup() {
  // Serial port for debugging purposes

  Serial.begin(115200);

  if(WIFIENABLED)
  {
    initWiFi();
    initWebSocket();
    initSPIFFS();

    // Web Server Root URL
    server.on("/", HTTP_GET, [](AsyncWebServerRequest *request)
    {
      request->send(SPIFFS, "/index.html", "text/html");
    });
  
    server.serveStatic("/", SPIFFS, "/");

    server.begin();
  }

  
  stepper_A.setSpeed(stepperSpeed);
  stepper_B1.setSpeed(stepperSpeed);
//  stepper_B2.setSpeed(stepperSpeed);
  stepper_C.setSpeed(stepperSpeed);
  //stepper_D.setSpeed(stepperSpeed);
  servo_E.attach(servo_E_pin);
  servo_E.attach(servo_F_pin);
  pinMode(ONBOARD_LED,OUTPUT);
}

void loop() {
  if(WIFIENABLED)
  {
    if (newRequest)
    {
      if (direction == "CW")
      {
        setAxisPosition(axisIndex.toInt(), steps.toInt());
     }
      else 
      {
        setAxisPosition(axisIndex.toInt(), -steps.toInt());
      }
      newRequest = false;
      notifyClients("stop");
    }
    ws.cleanupClients();
  }

  if(SERIALCONTROLENABLED)
  {

    // Loop and wait for a command we recognise
    String cmd = "";

    int inbyte;

     // wait for a command to be typed.
     while(true)
     {
        // what for a char to be available
        while (Serial.available() == 0)
        ;


        inbyte = Serial.read();
        if (inbyte == '\n')
          break;

        if (inbyte != 0)
          cmd += (char(inbyte));
     } 


    cmd.trim();
    cmd.toLowerCase();
    if (cmd.startsWith("1"))
    {
      Serial.println("Feeling groovy.");
      digitalWrite(ONBOARD_LED,HIGH);
    }
    if (cmd.startsWith("2"))
    {
      Serial.println("Feeling groovy.");
      digitalWrite(ONBOARD_LED,LOW);
    }
}



//    char buff[24];
//    
//    if (Serial.readBytes(buff, 3) == 3) 
//    {
//      char input1 = buff[0];
//      char input2 = buff[1];
//      char input3 = buff[2];
//
//      String buffer1 = "buff 1:";
//      String buffer2 = "buff 2:";
//      String buffer3 = "buff 3:";
//
//      Serial.print(buffer1);
//      Serial.println(input1);
//      if(input1 == '1')
//      {
//        Serial.println("yes input 1 is working");
//      }
//
//      Serial.print(buffer2);
//      Serial.println(input2);
//      
//      Serial.print(buffer3);
//      Serial.println(input3);
//    }
//  }
}
