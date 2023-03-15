package Renderer.entity;

import Renderer.Configuration.Configuration;
import Renderer.input.ClickType;
import Renderer.input.Keyboard;
import Renderer.input.Mouse;
import Renderer.input.UserInput;
import Renderer.point.MyVector;
import Renderer.point.PointConverter;
import Renderer.world.Camera;
import java.awt.Graphics;
import java.util.ArrayList;
import java.util.List;
import Renderer.entity.builder.ComplexEntityBuilder;
import Renderer.output.SerialESP32;
import Renderer.output.WebSocketESP32;
import Renderer.point.MyPoint;
import java.io.IOException;

public class EntityManager
{
    private List<iEntity> entities;
    private int initialX, initialY;
    private final double SENSITIVITY = Configuration.SENSITIVITY;
    private final double MOVEMENTSPEED = Configuration.MOVEMENT_SPEED;
    private MyVector lightVector = MyVector.normalize(new MyVector(1,1,1));
    private Mouse mouse;
    private Keyboard keyboard;
    private Camera camera;
    private SerialESP32 esp32 = new SerialESP32();
    
    private final boolean WIFIENABLED = Configuration.WIFI_ENABLED;
    
    private double axis1Pos = 0;
    private double axis2Pos = 0;
    private double axis3Pos = 0;
    private double axis4Pos = 0;
    private double axis5Pos = 0;
    private double axis6Pos = 0;

    WebSocketESP32 socket = new WebSocketESP32();
    
    public EntityManager()
    {
        this.entities = new ArrayList<>();
    }
    
    public void init(UserInput userInput, SerialESP32 esp32)
    {
        this.esp32 = esp32;//TODO SET ACTUAL PORT
        this.mouse = userInput.mouse;
        this.keyboard = userInput.keyboard;
        this.camera = new Camera(0,0,0);
        //this.entities.add(ComplexEntityBuilder.createRubixCube(100,0,0,0));
        //this.entities.add(BasicEntityBuilder.createSphere(Color.RED, 100,10, 0,0,0));
        this.entities.add(ComplexEntityBuilder.createArm(0,0,0,0));
        this.setLighting();
        
        if(this.WIFIENABLED)
        {
            this.socket.sendMessage("connect");
        }
    }
    
    public void update() throws IOException, InterruptedException
    {
        int x = this.mouse.getX();
        int y = this.mouse.getY();
        
//        double test = 11.0;
//        System.out.println("test int is 11.0 " + Integer.parseInt(Integer.toString((int)test)));
        
        
        if(this.mouse.getButton() == ClickType.LeftClick)
        {
            int xDif = x - initialX;
            int yDif = y - initialY;
            
            this.rotate(true, 0, -yDif/this.SENSITIVITY, -xDif/this.SENSITIVITY);
        } 
        else if(this.mouse.getButton() == ClickType.RightClick)
        {
            int xDif = x - initialX;
            
            this.rotate(true, -xDif/this.SENSITIVITY, 0, 0);
        } 
        
        if(this.mouse.isScrollingUp())
        {
            PointConverter.zoomIn();
        }
        else if(this.mouse.isScrollingDown())
        {
            PointConverter.zoomOut();
        }
        
        if(this.keyboard.getLeft())
        {
            this.camera.translate(0,-MOVEMENTSPEED,0);
            for(iEntity entity : this.entities)
            {
                entity.translate(0,MOVEMENTSPEED,0);
            }
        }
        
        if(this.keyboard.getRight())
        {
            this.camera.translate(0,MOVEMENTSPEED,0);
            for(iEntity entity : this.entities)
            {
                entity.translate(0,-MOVEMENTSPEED,0);
            }
        }
        
//        if(this.keyboard.getUp())
//        {
//            this.camera.translate(0,0,MOVEMENTSPEED);
//            for(iEntity entity : this.entities)
//            {
//                entity.translate(0,0,-MOVEMENTSPEED);
//            }
//        }
//        
//        if(this.keyboard.getDown())
//        {
//            this.camera.translate(0,0,-MOVEMENTSPEED);
//            for(iEntity entity : this.entities)
//            {
//                entity.translate(0,0,MOVEMENTSPEED);
//            }
//        }
        
        if(this.keyboard.getForward())
        {
            this.camera.translate(MOVEMENTSPEED,0,0);
            for(iEntity entity : this.entities)
            {
                entity.translate(-MOVEMENTSPEED,0,0);
            }
        }
        
        if(this.keyboard.getBackward())
        {
            this.camera.translate(-MOVEMENTSPEED,0,0);
            for(iEntity entity : this.entities)
            {
                entity.translate(MOVEMENTSPEED,0,0);
            }
        }
        
        if(this.keyboard.getAxis1Forward())
        {
            double increment = 0;
            if(axis1Pos < 180)
            {
                increment = SENSITIVITY;
                this.axis1Pos += SENSITIVITY;
                MyPoint point = new MyPoint(0,0,increment);
                this.rotateArm(1, point, true, axis1Pos);
//                this.esp32.writeToPort(1, Integer.parseInt(Integer.toString((int)SENSITIVITY)), 0);
                if(this.WIFIENABLED)
                {
                    socket.sendMessage("PAxis0");
                    Thread.sleep(100);

                }
            }
        }
        if(this.keyboard.getAxis1Backward())
        {
            double increment = 0;
            if(axis1Pos > -180)
            {
                increment = -SENSITIVITY;
                this.axis1Pos -= SENSITIVITY;
                MyPoint point = new MyPoint(0,0,increment);
                this.rotateArm(1, point, true, axis1Pos);
//                this.esp32.writeToPort(1, Integer.parseInt(Integer.toString((int)SENSITIVITY)), 1);
                if(this.WIFIENABLED)
                {
                    socket.sendMessage("MAxis0");
                    Thread.sleep(100);
                }
            }
        }
        
        if(this.keyboard.getAxis2Forward())
        {
            double increment = 0;
            if(axis2Pos < 90)
            {
                increment = SENSITIVITY;
                this.axis2Pos += SENSITIVITY;
                MyPoint point = new MyPoint(increment,increment,0);
                this.rotateArm(2, point, true, axis1Pos);
//                this.esp32.writeToPort(2, Integer.parseInt(Integer.toString((int)SENSITIVITY)), 0);
                if(this.WIFIENABLED)
                {
                    socket.sendMessage("PAxis1");
                }
            }
        }
        if(this.keyboard.getAxis2Backward())
        {
            double increment = 0;
            if(axis2Pos > -90)
            {
                increment = -SENSITIVITY;
                this.axis2Pos -= SENSITIVITY;
//                this.esp32.writeToPort(2, Integer.parseInt(Integer.toString((int)SENSITIVITY)), 1);

                MyPoint point = new MyPoint(increment,increment,0);
                this.rotateArm(2, point, true, axis1Pos);
                if(this.WIFIENABLED)
                {
                    socket.sendMessage("MAxis1");
                }
            }
        }
        
        if(this.keyboard.getAxis3Forward())
        {
            double increment = 0;
            if(axis3Pos < 90)
            {
                increment = SENSITIVITY;
                this.axis3Pos += SENSITIVITY;
                MyPoint point = new MyPoint(increment,increment,0);
                this.rotateArm(3, point, true, axis1Pos);
//                this.esp32.writeToPort(3, Integer.parseInt(Integer.toString((int)SENSITIVITY)), 0);
                if(this.WIFIENABLED)
                {
                    socket.sendMessage("PAxis2");
                }
            }
        }
        if(this.keyboard.getAxis3Backward())
        {
            double increment = 0;
            if(axis3Pos > -90)
            {
                increment = -SENSITIVITY;
                this.axis3Pos -= SENSITIVITY;
//                this.esp32.writeToPort(3, Integer.parseInt(Integer.toString((int)SENSITIVITY)), 1);
                MyPoint point = new MyPoint(increment,increment,0);
                this.rotateArm(3, point, true, axis1Pos);
                if(this.WIFIENABLED)
                {
                    socket.sendMessage("MAxis2");
                }
            }
        }
        
        if(this.keyboard.getAxis4Forward())
        {
            double increment = 0;
            if(axis4Pos < 90)
            {
                increment = SENSITIVITY;
                this.axis4Pos += SENSITIVITY;
                MyPoint point = new MyPoint(increment,increment,0);
                this.rotateArm(4, point, true, axis1Pos);
//                this.esp32.writeToPort(4, Integer.parseInt(Integer.toString((int)SENSITIVITY)), 0);
                if(this.WIFIENABLED)
                {
                    socket.sendMessage("PAxis3");
                }
            }

        }
        if(this.keyboard.getAxis4Backward())
        {
            double increment = 0;
            if(axis4Pos > -90)
            {
                increment = -SENSITIVITY;
                this.axis4Pos -= SENSITIVITY;
//                this.esp32.writeToPort(4, Integer.parseInt(Integer.toString((int)SENSITIVITY)), 1);
                MyPoint point = new MyPoint(increment,increment,0);
                this.rotateArm(4, point, true, axis1Pos);
                if(this.WIFIENABLED)
                {
                    socket.sendMessage("MAxis3");
                }
            }
        }
        this.mouse.resetScroll();
        this.keyboard.update();
        initialX = x;
        initialY = y;
        
//        char[] returnData = this.esp32.read();
//        System.out.println("this data was returned 1" + returnData[1]);
//        System.out.println("this data was returned 2" + returnData[2]);
//        System.out.println("this data was returned 3" + returnData[3]);

    }
    
    public void render(Graphics g)
    {
        for(iEntity entity : this.entities)
        {
            entity.render(g);
        }
    }
    
    private void rotate(boolean CW, double x, double y, double z)
    {
        for(iEntity entity : this.entities)
        {
            entity.rotate(CW, x, y, z, this.lightVector);
        }
    }
    
    private void setLighting()
    {
        for(iEntity entity : this.entities)
        {
            entity.setLighting(this.lightVector);
        }
    }
    
    private void rotateArm(int axis, MyPoint steps, boolean CW, double zAngle)
    {
        this.entities.get(0).rotateArm(CW, axis, steps, this.lightVector, zAngle);
    }
}
