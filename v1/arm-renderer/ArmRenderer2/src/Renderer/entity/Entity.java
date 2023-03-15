package Renderer.entity;

import Renderer.point.MyPoint;
import Renderer.point.MyVector;
import Renderer.point.PointConverter;
import Renderer.shapes.MyPolygon;
import Renderer.shapes.Tetrahedron;
import java.awt.Graphics;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Entity implements iEntity
{
    private List<Tetrahedron> tetrahedrons;
    private MyPolygon[] polygons;
    
    private double rotationHeight = 0;
    private List<MyPoint> rotationCenters = new ArrayList<>();
        
    public Entity(List<Tetrahedron> tetrahedrons)
    {
        this.tetrahedrons = tetrahedrons;
        List<MyPolygon> tempList = new ArrayList<>();
        for(Tetrahedron tetra : this.tetrahedrons)
        {
            tempList.addAll(Arrays.asList(tetra.getPolygons()));
        }
        this.polygons = new MyPolygon[tempList.size()];
        this.polygons = tempList.toArray(this.polygons);
        this.sortPolygons();
        
         //height 1
        rotationHeight = 0;
        rotationCenters.add(new MyPoint(0,0,rotationHeight));
        
        //height 2
        rotationHeight = 25;
        rotationCenters.add(new MyPoint(0,0,rotationHeight));

        //height 3
        rotationHeight = 125;
        rotationCenters.add(new MyPoint(0,0,rotationHeight));

        //height 4
        rotationHeight = 225;
        rotationCenters.add(new MyPoint(0,0,rotationHeight));
    }
            
    @Override
    public void render(Graphics g)
    {
        for(MyPolygon polygon : this.polygons)
        {
            polygon.render(g);
        }
    }

    @Override
    public void rotate(boolean CW, double xDegrees, double yDegrees, double zDegrees, MyVector lightVector)
    {
        for(Tetrahedron tetra : this.tetrahedrons)
        {
            tetra.rotate(CW, xDegrees, yDegrees, zDegrees, lightVector);
        }
        this.sortPolygons();
    }
    
    @Override
    public void setLighting(MyVector lightVector)
    {
        for(Tetrahedron tetra : this.tetrahedrons)
        {
            tetra.setLighting(lightVector);
        }
    }
    
    private void sortPolygons()
    {
        MyPolygon.sortPolygons(this.polygons);
    }

    @Override
    public void translate(double x, double y, double z)
    {
        for(Tetrahedron tetra : this.tetrahedrons)
        {
            tetra.translate(x,y,z);
        }
        this.sortPolygons();        
    }
        
    private MyPoint multiplierPoint = new MyPoint(1, 0, 0);
    
    @Override
    public void rotateArm(boolean CW, int axis, MyPoint stepsI, MyVector lightVector, double zAngle)
    {
//        double rotationMultiplierX = (0.5 * Math.cos((zAngle/90)/0.32))+0.5;
//        double rotationMultiplierY = (-0.5 * Math.cos((zAngle/90)/0.32))+0.5;
//        MyPoint steps = new MyPoint(0,0,stepsI.z);
//        
//        steps.x = (stepsI.x) * rotationMultiplierX;
//        steps.y = (stepsI.x) * rotationMultiplierY;
//
//        
//       System.out.printf("%f %f\n", rotationMultiplierX, rotationMultiplierY);
//       System.out.printf("%f %f\n", steps.x, steps.y);
//
//       System.out.printf("%f\n", zAngle);
       
        int centerIndex = 0;
                 
        MyPoint steps = new MyPoint(stepsI.x,stepsI.y,stepsI.z);

        stepsI = new MyPoint(stepsI.x * multiplierPoint.x, stepsI.y * multiplierPoint.y, stepsI.z * multiplierPoint.z);
        
        System.out.println("x: " + stepsI.x + " y: " + stepsI.y);
         switch(axis)
        {
            case 1:
                this.tetrahedrons.get(0).rotate(CW, steps.x, steps.y, steps.z, lightVector);
                this.tetrahedrons.get(1).rotate(CW, steps.x, steps.y, steps.z, lightVector);
                this.tetrahedrons.get(2).rotate(CW, steps.x, steps.y, steps.z, lightVector);
                this.tetrahedrons.get(3).rotate(CW, steps.x, steps.y, steps.z, lightVector);
                
                PointConverter.rotateZAxisPoint(rotationCenters.get(1), rotationCenters.get(centerIndex), CW, steps.z);
                PointConverter.rotateZAxisPoint(rotationCenters.get(2), rotationCenters.get(centerIndex), CW, steps.z);
                PointConverter.rotateZAxisPoint(rotationCenters.get(3), rotationCenters.get(centerIndex), CW, steps.z);
                PointConverter.rotateZAxisPoint(multiplierPoint, rotationCenters.get(centerIndex), CW, steps.z);
                break;
            case 2:
                rotationHeight = 25;
                centerIndex = 1;
                
                this.tetrahedrons.get(1).rotateAxis(rotationCenters, 1, CW, stepsI.x, stepsI.y, stepsI.z, lightVector);
                this.tetrahedrons.get(2).rotateAxis(rotationCenters, 1, CW, stepsI.x, stepsI.y, stepsI.z, lightVector);
                this.tetrahedrons.get(3).rotateAxis(rotationCenters, 1, CW, stepsI.x, stepsI.y, stepsI.z, lightVector);
                
                PointConverter.rotateXAxisPoint(rotationCenters.get(2), rotationCenters.get(centerIndex), CW, stepsI.x);
                PointConverter.rotateXAxisPoint(rotationCenters.get(3), rotationCenters.get(centerIndex), CW, stepsI.x);
                PointConverter.rotateYAxisPoint(rotationCenters.get(2), rotationCenters.get(centerIndex), CW, stepsI.y);
                PointConverter.rotateYAxisPoint(rotationCenters.get(3), rotationCenters.get(centerIndex), CW, stepsI.y);
                break;
            case 3:
                rotationHeight = 125;
                centerIndex = 2;

                this.tetrahedrons.get(2).rotateAxis(rotationCenters, 2, CW, stepsI.x, stepsI.y, stepsI.z, lightVector);
                this.tetrahedrons.get(3).rotateAxis(rotationCenters, 2, CW, stepsI.x, stepsI.y, stepsI.z, lightVector);
                
                PointConverter.rotateXAxisPoint(rotationCenters.get(3), rotationCenters.get(centerIndex), CW, stepsI.x);
                PointConverter.rotateYAxisPoint(rotationCenters.get(3), rotationCenters.get(centerIndex), CW, stepsI.y);
                //PointConverter.rotateXYAxisPoint(rotationCenters.get(3), rotationCenters.get(centerIndex), CW, stepsI.x, stepsI.y);
                break;
            case 4:
                rotationHeight = 225;
                this.tetrahedrons.get(3).rotateAxis(rotationCenters, 3, CW, stepsI.x, stepsI.y, 0, lightVector);
                break;
                
        }
        this.sortPolygons();
    }
}