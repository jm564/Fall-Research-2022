package Renderer.shapes;

import Renderer.point.MyPoint;
import Renderer.point.MyVector;
import Renderer.point.PointConverter;
import java.awt.Color;
import java.awt.Graphics;
import java.util.List;
import java.awt.Point;
import java.awt.Polygon;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;



public class MyPolygon
{
    private static final double AMBIENT_LIGHT = 0.05;
    
    private MyPoint[] points;
    private Color baseColor;
    private Color lightingColor;
    private boolean visible;
    
    public MyPolygon(Color color, MyPoint... points)
    {
        this.baseColor = this.lightingColor = color;
        this.points = new MyPoint[points.length];
        for(int i = 0; i < points.length; i++)
        {
            MyPoint p = points[i];
            this.points[i] = new MyPoint(p.x, p.y, p.z); 
        }
        this.updateVisibility();
    }
    
    public MyPolygon( MyPoint... points)
    {
        this.baseColor = this.lightingColor = Color.WHITE;
        this.points = new MyPoint[points.length];
        for(int i = 0; i < points.length; i++)
        {
            MyPoint p = points[i];
            this.points[i] = new MyPoint(p.x, p.y, p.z); 
        }
        this.updateVisibility();
    }
    
    public void render(Graphics g)
    {
        if(!this.visible) return;
        Polygon poly = new Polygon();
        for(int i = 0; i < this.points.length; i++)
        {
            Point p = PointConverter.convertPoint(this.points[i]);
            poly.addPoint(p.x, p.y);
        }
        
        
        g.setColor(this.lightingColor);
        g.fillPolygon(poly);
    }
    
    public void rotate(boolean CW, double xDegrees, double yDegrees, double zDegrees, MyVector lightVector)
    {
        for(MyPoint p : points)
        {
            PointConverter.rotateAxisX(p, CW, xDegrees);
            PointConverter.rotateAxisY(p, CW, yDegrees);
            PointConverter.rotateAxisZ(p, CW, zDegrees);
        }
        this.updateVisibility();
        this.updateLightingRatio(lightVector);
    }
    
    public void rotateAxis(List<MyPoint> centers, int centerIndex, boolean CW, double xDegrees, double yDegrees, double zDegrees, MyVector lightVector)
    {
        for(MyPoint p : points)
        {
            PointConverter.rotateXAxisPoint(p, centers.get(centerIndex), CW, xDegrees);
            PointConverter.rotateYAxisPoint(p, centers.get(centerIndex), CW, yDegrees);
            PointConverter.rotateZAxisPoint(p, centers.get(centerIndex), CW, zDegrees);
        }

//        for(int i = centerIndex; i < centers.size(); i++)
//        {
//            PointConverter.rotateXAxisPoint(centers.get(i), centers.get(centerIndex), CW, xDegrees);
//        }
        
        this.updateVisibility();
        this.updateLightingRatio(lightVector);
    }
        
    public void translate(double x, double y, double z)
    {
        for(MyPoint p : points)
        {
            p.xOffset += x;
            p.yOffset += y;
            p.zOffset += z;
        }
        this.updateVisibility();

    }
    
    public double getAverageX()
    {
        double sum = 0;
        for(MyPoint p : this.points)
        {
            sum += p.x + p.xOffset;
        }
        
        return sum / this.points.length;
    }
    
    private MyPoint getAveragePoint()
    {
        double x = 0;
        double y = 0;
        double z = 0;
        for(MyPoint p : this.points)
        {
            x += p.x + p.xOffset;
            y += p.y + p.yOffset;
            z += p.z + p.zOffset;
        }
        
        x /= this.points.length;
        y /= this.points.length;
        z /= this.points.length;
        
        return new MyPoint(x,y,z);
    }
    
    public void setColor(Color color)
    {
        this.baseColor = color;
    }
    
    public static MyPolygon[] sortPolygons(MyPolygon[] polygons)
    {
//        List<MyPolygon> polygonList = new ArrayList<>();
//        
//        polygonList.addAll(Arrays.asList(polygons));
//    
//    Collections.sort(polygonList, (MyPolygon p1, MyPolygon p2) -> p2.getAverageX() - p1.getAverageX() < 0? 1 : -1);
//    
//    for(int i = 0; i < polygons.length; i++)
//    {
//        polygons[i] = polygonList.get(i);
//    }
//    
//    return polygons;
        
        
        List<MyPolygon> polygonList = new ArrayList<>();
        
        polygonList.addAll(Arrays.asList(polygons));
        
        Collections.sort(polygonList, (MyPolygon p1, MyPolygon p2) ->
        {
            MyPoint p1Average = p1.getAveragePoint();
            MyPoint p2Average = p2.getAveragePoint();
            double p1Dist = MyPoint.dist(p1Average, MyPoint.origin);
            double p2Dist = MyPoint.dist(p2Average, MyPoint.origin);            
            double diff = p1Dist - p2Dist;
            if(diff == 0)
            {
                return 0;
            }
            
            return diff < 0? 1: -1;
        });
        
        for(int i = 0; i < polygons.length; i++)
        {
            polygons[i] = polygonList.get(i);
        }
        return polygons;
    }
    
    public void updateLightingRatio(MyVector lightVector)
    {
        if(this.points.length < 3)
        {
            return;
        }
        
        MyVector v1 = new MyVector(points[0], points[1]);
        MyVector v2 = new MyVector(points[1], points[2]);
        MyVector normalVector = MyVector.normalize(MyVector.cross(v2,v1));
        
        double dot = MyVector.dotProduct(normalVector, lightVector);
        double sign = dot < 0? -1 : 1;
        dot = sign * dot * dot;
        dot = (dot + 1) / 2 * (1 - AMBIENT_LIGHT);
        
        double lightRatio = Math.min(1, Math.max(0, AMBIENT_LIGHT + dot));
        this.updateLightingColor(lightRatio);
    }
    
    private void updateLightingColor(double lightRatio)
    {
        int red = (int) (this.baseColor.getRed() * lightRatio);
        int green = (int) (this.baseColor.getGreen() * lightRatio);
        int blue = (int) (this.baseColor.getBlue() * lightRatio);
        this.lightingColor = new Color(red,green,blue);
    }
    
    private void updateVisibility()
    {
        this.visible = getAverageX() < 0;
    }
    
    public boolean isVisible()
    {
        return this.visible;
    }
}