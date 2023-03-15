package Renderer.entity;

import Renderer.point.MyPoint;
import Renderer.point.MyVector;
import java.awt.Graphics;


public interface iEntity
{
    void render(Graphics g);
    
    void translate(double x, double y, double z);
    
    void rotate(boolean CW, double xDegrees, double yDegrees, double zDegrees, MyVector lightVector);
    
    void rotateArm(boolean CW, int axis, MyPoint steps, MyVector lightVector, double zAngle);

    void setLighting(MyVector lightVector);
}
