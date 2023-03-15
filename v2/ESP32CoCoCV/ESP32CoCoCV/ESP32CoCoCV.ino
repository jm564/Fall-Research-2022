#include <Arduino.h>
#include <WiFi.h>
#include "esp_http_server.h"
#include "esp_timer.h"
#include "esp_camera.h"
#include "img_converters.h"

//define camera pins
#define PWDN_GPIO_NUM     32
#define RESET_GPIO_NUM    -1
#define XCLK_GPIO_NUM      0
#define SIOD_GPIO_NUM     26
#define SIOC_GPIO_NUM     27

#define Y9_GPIO_NUM       35
#define Y8_GPIO_NUM       34
#define Y7_GPIO_NUM       39
#define Y6_GPIO_NUM       36
#define Y5_GPIO_NUM       21
#define Y4_GPIO_NUM       19
#define Y3_GPIO_NUM       18
#define Y2_GPIO_NUM        5
#define VSYNC_GPIO_NUM    25
#define HREF_GPIO_NUM     23
#define PCLK_GPIO_NUM     22
  
// HTML Page
const char index_html[] PROGMEM = R"rawliteral(
<!DOCTYPE html><html lang="en"><head><title>Multiple object detection using pre trained model in TensorFlow.js</title><meta charset="utf-8"><meta http-equiv="X-UA-Compatible" content="IE=edge"><meta name="viewport" content="width=device-width, initial-scale=1"><style>img{display:block}.camView{position:relative;float:left;width:calc(100% - 20px);margin:10px;cursor:pointer}.camView p{position:absolute;padding:5px;background-color:rgba(255, 111, 0, 0.85);color:#FFF;border:1px dashed rgba(255,255,255,0.7);z-index:2;font-size:12px}.highlighter{background:rgba(0, 255, 0, 0.25);border:1px dashed #fff;z-index:1;position:absolute}</style></head><body><h1>ESP32 TensorFlow.js - Object Detection</h1><p>Wait for the model to load before clicking the button to enable the webcam - at which point it will become visible to use.</p> <section id="camSection" ><div id="liveView" class="camView" > <button id="esp32camButton">Start ESP32 Webcam</button> <img id="esp32cam_video" width="640" height="480" crossorigin=' '/></div> </section> <script src="https://cdn.jsdelivr.net/npm/@tensorflow/tfjs" type="text/javascript"></script> <script src="https://cdn.jsdelivr.net/npm/@tensorflow-models/coco-ssd"></script> <script>const video=document.getElementById('esp32cam_video');const liveView=document.getElementById('liveView');const camSection=document.getElementById('camSection');const enableWebcamButton=document.getElementById('esp32camButton');esp32camButton.addEventListener('click',enableCam);var model=undefined;esp32camButton.disabled=true;cocoSsd.load().then(function(loadedModel){model=loadedModel;esp32camButton.disabled=false;});function enableCam(event){video.src='http://'+window.location.hostname+":81";if(!model){console.log("Failed to get model");return;} predictWebcam();} var children=[];function predictWebcam(){video.crossorigin=' ';model.detect(video).then(function(predictions){for(let i=0;i<children.length;i++){liveView.removeChild(children[i]);} children.splice(0);for(let n=0;n<predictions.length;n++){console.log(predictions[n].class+' '+predictions[n].score);if(predictions[n].score>0.55){const p=document.createElement('p');p.innerText=predictions[n].class+' - with ' +Math.round(parseFloat(predictions[n].score)*100) +'% confidence.';p.style='margin-left: '+predictions[n].bbox[0]+'px; margin-top: ' +(predictions[n].bbox[1]-10)+'px; width: ' +(predictions[n].bbox[2]-10)+'px; top: 0; left: 0;';const highlighter=document.createElement('div');highlighter.setAttribute('class','highlighter');highlighter.style='left: '+predictions[n].bbox[0]+'px; top: ' +predictions[n].bbox[1]+'px; width: ' +predictions[n].bbox[2]+'px; height: ' +predictions[n].bbox[3]+'px;';liveView.appendChild(highlighter);liveView.appendChild(p);children.push(highlighter);children.push(p);}} window.requestAnimationFrame(predictWebcam);});}</script> </body></html>
 )rawliteral";    
#define PART_BOUNDARY "123456789000000000000987654321"

static const char* _STREAM_CONTENT_TYPE = "multipart/x-mixed-replace;boundary=" PART_BOUNDARY;
static const char* _STREAM_BOUNDARY = "\r\n--" PART_BOUNDARY "\r\n";
static const char* _STREAM_PART = "Content-Type: image/jpeg\r\nContent-Length: %u\r\n\r\n";
httpd_handle_t camera_httpd = NULL;
httpd_handle_t stream_httpd = NULL;

const char* ssid = "Two Girls, One Router";
const char* password =  "AlexShitsTheBed42069";

static esp_err_t page_handler(httpd_req_t *req) {
    httpd_resp_set_type(req, "text/html");
    httpd_resp_set_hdr(req, "Access-Control-Allow-Origin", "*");
    httpd_resp_send(req, index_html, sizeof(index_html));
}

static esp_err_t stream_handler(httpd_req_t *req){
    camera_fb_t * fb = NULL;
    esp_err_t res = ESP_OK;
    size_t _jpg_buf_len = 0;
    uint8_t * _jpg_buf = NULL;
    char * part_buf[64];

    res = httpd_resp_set_type(req, _STREAM_CONTENT_TYPE);
    if(res != ESP_OK){
        return res;
    }
    httpd_resp_set_hdr(req, "Access-Control-Allow-Origin", "*");
    while(true){
        fb = esp_camera_fb_get();
        if (!fb) {
            Serial.println("Camera capture failed");
            res = ESP_FAIL;
        } else {
            
                if(fb->format != PIXFORMAT_JPEG){
                    bool jpeg_converted = frame2jpg(fb, 80, &_jpg_buf, &_jpg_buf_len);
                    esp_camera_fb_return(fb);
                    fb = NULL;
                    if(!jpeg_converted){
                        Serial.println("JPEG compression failed");
                        res = ESP_FAIL;
                    }
                } else {
                    _jpg_buf_len = fb->len;
                    _jpg_buf = fb->buf;
                }
             }
        if(res == ESP_OK){
            res = httpd_resp_send_chunk(req, _STREAM_BOUNDARY, strlen(_STREAM_BOUNDARY));
        }
        if(res == ESP_OK){
            size_t hlen = snprintf((char *)part_buf, 64, _STREAM_PART, _jpg_buf_len);
            res = httpd_resp_send_chunk(req, (const char *)part_buf, hlen);
        }
        if(res == ESP_OK){
            res = httpd_resp_send_chunk(req, (const char *)_jpg_buf, _jpg_buf_len);
        }
        if(fb){
            esp_camera_fb_return(fb);
            fb = NULL;
            _jpg_buf = NULL;
        } else if(_jpg_buf){
            free(_jpg_buf);
            _jpg_buf = NULL;
        }
        if(res != ESP_OK){
            break;
        }
    }
    return res;
}

void startCameraServer(){
    httpd_config_t config = HTTPD_DEFAULT_CONFIG();
    httpd_uri_t index_uri = {
        .uri       = "/",
        .method    = HTTP_GET,
        .handler   = stream_handler,
        .user_ctx  = NULL
    };
    httpd_uri_t page_uri = {
        .uri       = "/ts",
        .method    = HTTP_GET,
        .handler   = page_handler,
        .user_ctx  = NULL
    };
 

    Serial.printf("Starting web server on port: '%d'\n", config.server_port);
    if (httpd_start(&camera_httpd, &config) == ESP_OK) {
      httpd_register_uri_handler(camera_httpd, &page_uri);
    }
    // start stream using another webserver
    config.server_port += 1;
    config.ctrl_port += 1;
    Serial.printf("Starting stream server on port: '%d'\n", config.server_port);
    if (httpd_start(&stream_httpd, &config) == ESP_OK) {
        httpd_register_uri_handler(stream_httpd, &index_uri);
    }
}

void setup() {
  Serial.begin(9600);
  Serial.setDebugOutput(true);
  Serial.println("started");
  camera_config_t config;
  config.ledc_channel = LEDC_CHANNEL_0;
  config.ledc_timer = LEDC_TIMER_0;
  config.pin_d0 = Y2_GPIO_NUM;
  config.pin_d1 = Y3_GPIO_NUM;
  config.pin_d2 = Y4_GPIO_NUM;
  config.pin_d3 = Y5_GPIO_NUM;
  config.pin_d4 = Y6_GPIO_NUM;
  config.pin_d5 = Y7_GPIO_NUM;
  config.pin_d6 = Y8_GPIO_NUM;
  config.pin_d7 = Y9_GPIO_NUM;
  config.pin_xclk = XCLK_GPIO_NUM;
  config.pin_pclk = PCLK_GPIO_NUM;
  config.pin_vsync = VSYNC_GPIO_NUM;
  config.pin_href = HREF_GPIO_NUM;
  config.pin_sscb_sda = SIOD_GPIO_NUM;
  config.pin_sscb_scl = SIOC_GPIO_NUM;
  config.pin_pwdn = PWDN_GPIO_NUM;
  config.pin_reset = RESET_GPIO_NUM;
  config.xclk_freq_hz = 20000000;
  config.pixel_format = PIXFORMAT_JPEG;
  
  // if PSRAM IC present, init with UXGA resolution and higher JPEG quality
  //                      for larger pre-allocated frame buffer.
  if(psramFound()){
    config.frame_size = FRAMESIZE_UXGA;
    config.jpeg_quality = 10;
    config.fb_count = 2;
  } else {
    config.frame_size = FRAMESIZE_UXGA;
    config.jpeg_quality = 12;
    config.fb_count = 1;
  }

  // camera init
  esp_err_t err = esp_camera_init(&config);
  if (err != ESP_OK) {
    Serial.printf("Camera init failed with error 0x%x", err);
    return;
  }
  
  sensor_t * s = esp_camera_sensor_get();
  // initial sensors are flipped vertically and colors are a bit saturated
  if (s->id.PID == OV3660_PID) {
    s->set_vflip(s, 1); // flip it back
    s->set_brightness(s, 1); // up the brightness just a bit
    s->set_saturation(s, 0); // lower the saturation
  }
  // drop down frame size for higher initial frame rate
  s->set_framesize(s, FRAMESIZE_QVGA);

  WiFi.begin(ssid, password);
  while (WiFi.status() != WL_CONNECTED) {
    delay(500);
    Serial.print(".");
  }
  Serial.println("");
  Serial.println("WiFi connected");
  startCameraServer();
  Serial.print("Camera Ready! Use 'http://");
  Serial.print(WiFi.localIP());
  Serial.println("' to connect");
}
void loop() {
  // put your main code here, to run repeatedly:
  delay(10);
}
