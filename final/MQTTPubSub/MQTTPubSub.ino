#include <WiFi.h>
#include <PubSubClient.h>
#include <Stepper.h>
#include <ESP32Servo.h>

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

//servo D pin
#define servo_D_pin 32

//servo E
#define servo_E_pin 35

//servo hand pin
#define servo_hand_pin 35

struct Joint
{
  char name;
  Stepper * stepper;
  int max;
  int position;
  int speed;
};

struct ServoJoint
{
  char name;
  Servo * servo;
  int max;
  int position;
};

const char* ssid = "Two Girls, One Router";
const char* password =  "AlexShitsTheBed42069";
const char* mqttServer = "192.168.4.100";
const int mqttPort = 1883;
const int stepsPerRevolution = 200;
const int stepperSpeed = 60;

WiFiClient espClient;
PubSubClient client(espClient);
Stepper stepper_A(stepsPerRevolution, A1, A2, A3, A4);//index 1
Stepper stepper_B(stepsPerRevolution, B1, B2, B3, B4);//index 2
Stepper stepper_C(stepsPerRevolution, C1, C2, C3, C4);// index 3

Joint A = { 'A', &stepper_A, 325, 0, stepperSpeed }; 
Joint B = { 'B', &stepper_B, 225, 0, stepperSpeed }; 
Joint C = { 'C', &stepper_C, 1000, 0, stepperSpeed }; 

Servo servo_D;//index 5
Servo servo_E;//index 6
Servo servo_hand;

ServoJoint D = { 'A', &servo_D, 90, 0 }; 
ServoJoint E = { 'B', &servo_E, 90, 0 }; 
ServoJoint HAND = { 'C', &servo_hand, 50, 0 }; 

void setup()
{
  ESP32PWM::allocateTimer(0);
  ESP32PWM::allocateTimer(1);
  ESP32PWM::allocateTimer(2);
  ESP32PWM::allocateTimer(3);
  
  Serial.begin(115200);
  WiFi.begin(ssid, password);
  
  stepper_A.setSpeed(stepperSpeed);
  stepper_B.setSpeed(stepperSpeed);
  stepper_C.setSpeed(stepperSpeed);

  servo_D.setPeriodHertz(50);   
  servo_D.attach(servo_D_pin, 500, 2400);

  servo_E.setPeriodHertz(50);    
  servo_E.attach(servo_E_pin, 500, 2400);

  servo_hand.setPeriodHertz(50);    
  servo_hand.attach(servo_hand_pin, 500, 2400);
  
  ZeroArmInit();
    
  while (WiFi.status() != WL_CONNECTED) 
  {
    delay(500);
    Serial.println("Connecting to WiFi..");
  }
  
  Serial.println("Connected to the WiFi network");
  client.setServer(mqttServer, mqttPort);
  client.setCallback(callback);
  while (!client.connected()) 
  {
    Serial.println("Connecting to MQTT...");
    delay(50);
    if (client.connect("ESP32Client")) 
    {
      Serial.println("connected");
      client.publish("esp32/Status", "Ready");
      subscribeToTopics();
    } 
    else 
    {
      Serial.print("failed with state ");
      Serial.println(client.state());
      delay(2000);
    }
  }
}

void subscribeToTopics()
{
  client.subscribe("esp32/A/step");
  client.subscribe("esp32/B/step");
  client.subscribe("esp32/C/step");
  client.subscribe("esp32/D/step");
  client.subscribe("esp32/E/step");
  client.subscribe("esp32/HAND/step");

  client.subscribe("esp32/A/speed");
  client.subscribe("esp32/B/speed");
  client.subscribe("esp32/C/speed");
}

void callback(char* topic, byte* message, unsigned int length) 
{
  Serial.print("Message arrived on topic: ");
  Serial.print(topic);
  Serial.print(". Message: ");  
  String Message;
  
  for (int i = 0; i < length; i++) 
  {
    Serial.print((char)message[i]);
    Message += (char)message[i];
  }
  Serial.println();
  
  if (String(topic) == "esp32/A/step") 
  {
    SetStepperPosition(&A, Message.toInt());
  }
  else if (String(topic) == "esp32/B/step") 
  {
    SetStepperPosition(&B, Message.toInt());
  }
  else if (String(topic) == "esp32/C/step") 
  {
    SetStepperPosition(&C, Message.toInt());
  }
  else if (String(topic) == "esp32/D/step") 
  {
    StepServoPosition(&D, Message.toInt());
  }
  else if (String(topic) == "esp32/E/step") 
  {
    StepServoPosition(&E, Message.toInt());
  }
  else if (String(topic) == "esp32/E/step") 
  {
    StepServoPosition(&HAND, Message.toInt());
  }
  else if (String(topic) == "esp32/HAND") 
  {
    int handPosition;
    if(Message == "OPEN")
    {
      handPosition = 100;
    }
    else if(Message == "CLOSE")
    {
      handPosition = 0;
    }
    SetServoPosition(&HAND, handPosition);
  }
  else if (String(topic) == "esp32/A/speed") 
  {
    SetStepperSpeed(&A, Message.toInt());
  }
  else if (String(topic) == "esp32/B/speed") 
  {
    SetStepperSpeed(&B, Message.toInt());
  }
  else if (String(topic) == "esp32/C/speed") 
  {
    //SetStepperSpeed(&C, Message.toInt());
  }
  else if(String(topic) =="esp32/ZeroArm")
  {
    ZeroArm();
  }
  else if(String(topic) =="esp32/Rest")
  {
    RestArm();
  }
}
 
void loop() {
  client.loop();
}

void SetStepperPosition(Joint * joint, int steps)
{
  int newPosition = joint->position + steps;
  if(abs(newPosition) >= joint->max)
  {
      steps = (joint->max - abs(joint->position)) * (steps < 0 ? -1 : 1);
  }
  
  client.publish("esp32/Status", "Working");
  Serial.print(joint->name);
  Serial.print(" rotation: ");
  Serial.println(steps);
  joint->stepper->step(steps);
  delay(500);
  joint->position += steps;

  client.publish("esp32/Status", "Ready");
}

void StepServoPosition(ServoJoint * joint, int steps)
{
  int newPosition = joint->position + steps;
  if(abs(newPosition) >= joint->max)
  {
      newPosition = (joint->max) * (steps < 0 ? -1 : 1);
  }
  
  client.publish("esp32/Status", "Working");
  Serial.print(joint->name);
  Serial.print(" rotation: ");
  Serial.println(newPosition);
  joint->servo->write(newPosition);
  delay(500);
  joint->position = newPosition;

  client.publish("esp32/Status", "Ready");
}

void SetServoPosition(ServoJoint * joint, int position)
{
  client.publish("esp32/Status", "Working");
  Serial.print(joint->name);
  Serial.print(" new position: ");
  Serial.println(position);
  joint->servo->write(position);
  delay(500);
  joint->position = position;

  client.publish("esp32/Status", "Ready");
}

void SetStepperSpeed(Joint * joint, int speed)
{
  client.publish("esp32/Status", "Working");

  Serial.print(" Updating speed for");
  Serial.print(joint->name);
  Serial.print(":");
  Serial.println(speed);
  joint->stepper->setSpeed(speed);
  joint->speed = speed;

  client.publish("esp32/Status", "Ready");
}

//predefined centers as middle and base oriented facing "tray"
void ZeroArmInit()
{
  SetStepperPosition(&A, -(A.max));
  A.position = 0;
  SetStepperPosition(&B, -(B.max));
  B.position = 0;
}

void ZeroArm()
{
  SetStepperPosition(&A, -(A.position));
  SetStepperPosition(&B, -(B.position));
  SetStepperPosition(&C, -(C.position));
}

void RestArm()
{
  
}
