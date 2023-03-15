//-- Libraries Included --------------------------------------------------------------
  #include <WiFi.h>
  #include <Arduino.h>
  #include <Stepper.h>
  #include <ESP32Servo.h>

// Network Name and Password
  char*       net_ssid = "1221-217";              // WIFI NAME
  char*       net_pass = "jwk773nynwv5";          // PASSWORD
  
  #define     MAXSC     6           // MAXIMUM NUMBER OF CLIENTS
  
  WiFiServer  daServer(1987);      
  WiFiClient  daClient[MAXSC];  



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

const int stepsPerRevolution = 200;
Stepper stepper_A(stepsPerRevolution, A1, A2, A3, A4);//index 1
Stepper stepper_B(stepsPerRevolution, B1, B2, B3, B4);//index 2
Stepper stepper_C(stepsPerRevolution, C1, C2, C3, C4);// index 3
//to do Stepper stepper_D(stepsPerRevolution, D1, D3, D2, D4);//figure out how to use wrist joint //index 4
const int stepperSpeed = 10;


Servo servo_E;//index 5
int servo_E_pos = 0;

Servo servo_F;//index 6
int servo_F_pos = 0;
//====================================================================================


void setup()
  {
    // Setting Serial Port
    Serial.begin(115200);           // Computer Communication

    // Setting Wifi Access Point
    SetWifi("ctrlESPwithJava", "");
    stepper_A.setSpeed(stepperSpeed);
    stepper_B.setSpeed(stepperSpeed + 60);
    stepper_C.setSpeed(stepperSpeed);

   }


//====================================================================================


void loop()
  {
    // Checking For Available Clients
    AvailableClients();
    // Checking For Available Client Messages
    AvailableMessage();
  }

//====================================================================================
  
  void SetWifi(char* Name, char* Password)
  {
    // Stop Any Previous WIFI
    WiFi.disconnect();

    // Setting The Wifi Mode
    WiFi.mode(WIFI_AP_STA);
    Serial.println("WIFI Mode : AccessPoint Station");
    
    // Setting The AccessPoint Name & Password
    net_ssid      = Name;
    net_pass  = Password;
    
    // Starting The Access Point
    WiFi.softAP(net_ssid, net_pass);
    Serial.println("WIFI << " + String(net_ssid) + " >> has Started");
    
    // Wait For Few Seconds
    delay(2000);
    
    // Getting Server IP
    IPAddress IP = WiFi.softAPIP();
    
    // Printing The Server IP Address
    Serial.print("Server IP : ");
    Serial.println(IP);

    // Starting Server
    daServer.begin();
    daServer.setNoDelay(true);
    Serial.println("Server Started, you can connect from the JAVA client");
  }

//====================================================================================

  void AvailableClients()
  {   
    if (daServer.hasClient())
    {
      
     
      for(uint8_t i = 0; i < MAXSC; i++)
      {
        //find free/disconnected spot
        if (!daClient[i] || !daClient[i].connected())
        {
          // Checks If Previously The Client Is Taken
          if(daClient[i])
          {
            daClient[i].stop();
          }

          // Checks If Clients Connected To The Server
          if(daClient[i] = daServer.available())
          {
            Serial.println("New Client: " + String(i));
          }

          // Continue Scanning
          continue;
        }
      }
      
      //no free/disconnected spot so reject
      WiFiClient daClient = daServer.available();
      daClient.stop();
    }
  }

//====================================================================================
bool canRecieveNew = true;
int newMovement = 0;

int ActiveAxes[4];

void AvailableMessage()
{
  //check clients for data
  for(uint8_t i = 0; i < MAXSC; i++)
  {
    if (daClient[i] && daClient[i].connected() && daClient[i].available())
    {
      while(daClient[i].available())
      {
        while(canRecieveNew)
        {
          updateActiveAxes(daClient[i].readStringUntil('!'));
          updateAxesPositions();
        }
      }
    }
  }
}

void updateActiveAxes(String Message)
{
  for(int i=0;i<4;i++)
  {
    int cmd=97+i;
    if((int)Message[0]==cmd)
    {
      Serial.println(Message);
      if(ActiveAxes[i] != 0)
      {
        ActiveAxes[i] = 0;
      }
      else
      {
        ActiveAxes[i] = 1;
      }
    }
    
    cmd=97+i+4;
    if((int)Message[0]==cmd)
    {
      Serial.println(Message);
      if(ActiveAxes[i] != 0)
      {
        ActiveAxes[i] = 0;
      }
      else
      {
        ActiveAxes[i] = -1;
      }
    }
  }
}

void updateAxesPositions()
{
  int steps = 1;
  canRecieveNew = false;
  
  if(ActiveAxes[0] == 1)
  {
      Serial.print("shoulder rotation: positive x ");
      Serial.println(steps);
      stepper_A.step(steps);
  }
  else if(ActiveAxes[0] == -1)
  {
      Serial.print("shoulder rotation: negative x ");
      Serial.println(steps);
      stepper_A.step(-steps);
  }
        
  if(ActiveAxes[1] == 1)
  {
      Serial.print("shoulder movement: positive x ");
      Serial.println(steps);
      stepper_B.step(steps);
  }
  else if(ActiveAxes[1] == -1)
  {
      Serial.print("shoulder movement: negative x ");
      Serial.println(steps);
      stepper_B.step(-steps);
  }      
  
  if(ActiveAxes[2] == 1)
  {
      Serial.print("elbow movement: positive x ");
      Serial.println(steps);
      stepper_C.step(steps);
  }
  else if(ActiveAxes[2] == -1)
  {
      Serial.print("elbow movement: negative x ");
      Serial.println(steps);
      stepper_C.step(-steps);
  }
  canRecieveNew = true;
}
