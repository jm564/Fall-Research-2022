import cv2 as cv
import numpy as np
from urllib.request import urlopen
from paho.mqtt import client as mqtt_client
import requests
import json

dist = lambda x1,y1,x2,y2: (x1-x2)**2*(y1-y2)**2
broker = '192.168.4.100'
port = 1883
getTopic = "BallCV/Get"
sendTopic = "BallCV/Send"
readyTopic = "BallCV/Ready"
url = "http://192.168.4.39/capture"
prevCircle = None


def connect_mqtt():
    def on_connect(client, userdata, flags, rc):
        if rc == 0:
            print("Connected to MQTT Broker")
            publish(client, readyTopic, "Ready")
        else:
            print("Failed to connect, return code %d\n", rc)
    client = mqtt_client.Client()
    client.on_connect = on_connect
    client.connect(broker, port)
    return client

def publish(client, topic, msg):
    result = client.publish(topic, msg)
    status = result[0]
    if status == 0:
        print(f"Sent `{msg}` to topic `{topic}`")
    else:
        print(f"Failed to send message to topic {topic}")

def subscribe(client: mqtt_client):
    def on_message(client, userdata, msg):
        print(f"Received `{msg.payload.decode()}` from `{msg.topic}` topic")
        if msg.topic == getTopic and prevCircle is not None:
            publish(client, sendTopic, f"[{prevCircle[0]},{prevCircle[1]}]")
    client.subscribe(getTopic)
    client.on_message = on_message

def set_resolution(url: str, index: int=1, verbose: bool=False):
    try:
        if verbose:
            resolutions = "10: UXGA(1600x1200)\n9: SXGA(1280x1024)\n8: XGA(1024x768)\n7: SVGA(800x600)\n6: VGA(640x480)\n5: CIF(400x296)\n4: QVGA(320x240)\n3: HQVGA(240x176)\n0: QQVGA(160x120)"
            print("available resolutions\n{}".format(resolutions))

        if index in [10, 9, 8, 7, 6, 5, 4, 3, 0]:
            requests.get(url + "/control?var=framesize&val={}".format(index))
        else:
            print("Wrong index")
    except:
        print("SET_RESOLUTION: something went wrong")

client = connect_mqtt()
client.loop_start()
subscribe(client)
set_resolution(url, index=7)

while True:
    img_resp = urlopen(url)
    imgnp = np.asarray(bytearray(img_resp.read()), dtype="uint8")
    frame = cv.imdecode(imgnp, -1)
    if not frame.any(): break

    greyFrame = cv.cvtColor(frame, cv.COLOR_BGR2GRAY)
    blurFrame = cv.GaussianBlur(greyFrame, (17,17), 0)

    circles = cv.HoughCircles(blurFrame, cv.HOUGH_GRADIENT, 1.2, 100, param1=100, param2=30, minRadius=10, maxRadius=50)

    if circles is not None:
        circles = np.uint16(np.around(circles))
        chosen = None
        for i in circles[0, :]:
            if chosen is None: chosen = i
            if prevCircle is not None:
                if dist(chosen[0],chosen[1],prevCircle[0],prevCircle[1]) <= dist(i[0],i[1],prevCircle[0],prevCircle[1]):
                    chosen = i
        cv.circle(frame, (chosen[0], chosen[1]), 1, (0,100,100), 3)
        cv.circle(frame, (chosen[0], chosen[1]), chosen[2], (255, 0, 255), 3)
        prevCircle = chosen

    cv.imshow("circles", frame)
    if cv.waitKey(1) & 0xFF == ord('q'): break

cv.destroyAllWindows()
