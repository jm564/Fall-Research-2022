package Renderer.output;

import Renderer.Display;
import com.fazecast.jSerialComm.*;
//import java.io.BufferedReader;
import java.io.IOException;
//import java.io.InputStreamReader;


        

public class SerialESP32
{
    public static String portName;
    public SerialPort sp;
//    private BufferedReader input;

    
    public void SerialESP32(String port)
    {
        System.out.println("Connecting to ESP32 on Port: " + port);
        SerialESP32.portName = port;
        this.sp = SerialPort.getCommPort(portName);
        sp.setComPortParameters(115200, 8, 1, 0);
        sp.setComPortTimeouts(SerialPort.TIMEOUT_WRITE_BLOCKING, 0, 0);
        
        if (sp.openPort())
        {
            System.out.println("Port " + sp.getDescriptivePortName() + " is open");
        } 
        else 
        {
            System.out.println("Failed to open port" + sp.getDescriptivePortName());
            return;
        }
        System.out.println();
    }     
    
        
    public void setPort(String port)
    {
        SerialESP32.portName = port;
    }
    
    public void printAllPorts()
    {
       SerialPort[] ports = SerialPort.getCommPorts();
       System.out.println("Available ports:");
       for(SerialPort port : ports)
       {
           String name = port.getDescriptivePortName();
           String path = port.getSystemPortPath();
           System.out.println(name + " " + path);
       }
       System.out.println();
    }
    
    public void writeToPort(Integer axis, Integer steps, Integer direction) throws IOException, InterruptedException
    {
        System.out.println("axis: " + Integer.toString(axis) + "|| steps: " + Integer.toString(steps) + "|| dir: " + Integer.toString(direction));
        if(!Display.debugNOUSB)
        {
            byte[] toArduino = new byte[]{axis.byteValue(), steps.byteValue(), direction.byteValue()};
            this.sp.getOutputStream().write(toArduino);
//            this.sp.getOutputStream().flush();
            Thread.sleep(100);
        }
    }
    
    public void closePort()
    {
        if(this.sp.closePort())
        {
            System.out.println("Port: " + this.sp.getDescriptivePortName() + " has been closed");
        }
        else
        {
            System.out.println("Port: " + this.sp.getDescriptivePortName() + " failed to clsoe");
        }
        System.out.println();
    }
   
//    public void read(String data)
//    {
//
//        int serialRead = Integer.parseInt(data);
//
//        if(serialRead == 255)
//        {
//            
//        }
//    }
}
