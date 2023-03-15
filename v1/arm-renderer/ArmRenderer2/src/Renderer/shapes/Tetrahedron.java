
package Renderer.shapes;

import Renderer.point.MyPoint;
import Renderer.point.MyVector;
import java.awt.Color;
import java.awt.Graphics;
import java.util.List;

public class Tetrahedron
{
    private MyPolygon[] polygons; 
    private Color color; 
    
    public Tetrahedron(Color color, MyPolygon... polygons)
    {
        this.color = color; 
        this.polygons = polygons;
        this.setPolygonColor();
    }
    
    public Tetrahedron(Color color, boolean colorDecay, MyPolygon... polygons)
    {
        this.color = color; 
        this.polygons = polygons;
        
        if(colorDecay)
        {
            this.setDecayingPolygonColor();

        }
        else
        {
            this.setPolygonColor();
        }
        this.sortPolygons();
    }
    
    public Tetrahedron( MyPolygon... polygons)
    {
        this.color = Color.WHITE; 
        this.polygons = polygons;
        this.sortPolygons();

    }
    
    public void render(Graphics g)
    {
        for(MyPolygon poly : this.polygons)
        {
            poly.render(g);
        }
    }
    
    public void rotate(boolean CW, double xDegrees, double yDegrees, double zDegrees, MyVector lightVector)
    {
        for(MyPolygon p : polygons)
        {
            p.rotate(CW, xDegrees, yDegrees, zDegrees, lightVector);
        }
        this.sortPolygons();
    }
    
    public void rotateAxis(List<MyPoint> centers, int centerIndex, boolean CW, double xDegrees, double yDegrees, double zDegrees, MyVector lightVector)
    {
        for(MyPolygon p : polygons)
        {
            p.rotateAxis(centers, centerIndex,CW, xDegrees, yDegrees, zDegrees, lightVector);
        }
        this.sortPolygons();
    }
    
    public void translate(double x, double y, double z)
    {
        for(MyPolygon p : polygons)
        {
            p.translate(x,y,z);
        }
        this.sortPolygons();        
    }
    
    public void setLighting(MyVector lightVector)
    {
        for(MyPolygon p : polygons)
        {
            p.updateLightingRatio(lightVector);
        }
    }
    
    public MyPolygon[] getPolygons()
    {
        return this.polygons;
    }
    
    private void sortPolygons()
    {
        MyPolygon.sortPolygons(this.polygons);
    }
    
    private void setPolygonColor()
    {
        for(MyPolygon poly : this.polygons)
        {
            poly.setColor(this.color);  
        }
    }
    
    private void setDecayingPolygonColor()
    {
        double decayFactor = .98;
        for(MyPolygon poly : this.polygons)
        {
            poly.setColor(this.color);
            int r = (int) (this.color.getRed() * decayFactor);
            int g = (int) (this.color.getGreen() * decayFactor);
            int b = (int) (this.color.getBlue() * decayFactor);
            this.color = new Color(r, g, b);
        }
    }
}
