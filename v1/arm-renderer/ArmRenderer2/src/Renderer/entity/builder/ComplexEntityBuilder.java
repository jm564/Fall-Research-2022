package Renderer.entity.builder;

import Renderer.entity.Entity;
import Renderer.entity.iEntity;
import Renderer.point.MyPoint;
import Renderer.shapes.MyPolygon;
import Renderer.shapes.Tetrahedron;
import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

public class ComplexEntityBuilder
{
    public static iEntity createRubixCube(double size, double centerX, double centerY, double centerZ)
    {
        List<Tetrahedron> tetras = new ArrayList<>();
        
        double cubeSpacing = 10;
        
        for(int i = -1; i < 2; i++)
        {
            double cubeCenterX = i * (size+cubeSpacing) + centerX;           
            
            for(int j = -1; j < 2; j++)
            {
                double cubeCenterY = j * (size+cubeSpacing) + centerY;
                
                for(int k = -1; k < 2; k++)
                {
                    if(i == 0 && j == 0 && k == 0) continue;
                    double cubeCenterZ = k * (size+cubeSpacing) + centerZ;
                    MyPoint p1 = new MyPoint(cubeCenterX - size/2, cubeCenterY - size/2, cubeCenterZ - size/2);
                    MyPoint p2 = new MyPoint(cubeCenterX - size/2, cubeCenterY - size/2, cubeCenterZ + size/2);
                    MyPoint p3 = new MyPoint(cubeCenterX - size/2, cubeCenterY + size/2, cubeCenterZ - size/2);
                    MyPoint p4 = new MyPoint(cubeCenterX - size/2, cubeCenterY + size/2, cubeCenterZ + size/2);
                    MyPoint p5 = new MyPoint(cubeCenterX + size/2, cubeCenterY - size/2, cubeCenterZ - size/2);
                    MyPoint p6 = new MyPoint(cubeCenterX + size/2, cubeCenterY - size/2, cubeCenterZ + size/2);
                    MyPoint p7 = new MyPoint(cubeCenterX + size/2, cubeCenterY + size/2, cubeCenterZ - size/2);
                    MyPoint p8 = new MyPoint(cubeCenterX + size/2, cubeCenterY + size/2, cubeCenterZ + size/2);
                    
                    MyPolygon polyRed = new MyPolygon(Color.RED, p5,p6, p8, p7);
                    MyPolygon polyWhite = new MyPolygon(Color.WHITE, p2,p4, p8, p6);
                    MyPolygon polyBlue = new MyPolygon(Color.BLUE, p3, p7, p8, p4);
                    MyPolygon polyGreen = new MyPolygon(Color.GREEN, p1,p2, p6, p5);
                    MyPolygon polyOrange = new MyPolygon(Color.ORANGE, p1,p3, p4, p2);
                    MyPolygon polyYellow = new MyPolygon(Color.YELLOW, p1, p5, p7, p3);
                    
                    
                    Tetrahedron tetra = new Tetrahedron(polyRed, polyWhite, polyBlue, polyGreen, polyOrange, polyYellow);
                    tetras.add(tetra);
                }
            }
        }
        
        return new Entity(tetras);
    }
    
    public static iEntity createArm(double segmentLength, double centerX, double centerY, double centerZ)
    {
        List<Tetrahedron> tetras = new ArrayList();
        
        tetras.add(BasicEntityBuilder.createArmBase(0, 0, 0));
        tetras.add(BasicEntityBuilder.createArmSegment(100, 0, 0, 75));
        tetras.add(BasicEntityBuilder.createArmSegment(100, 0, 0, 175));
        tetras.add(BasicEntityBuilder.createArmSegment(100, 0, 0, 275));
        
        return new Entity(tetras);
    }

}
