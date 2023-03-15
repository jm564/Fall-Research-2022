package Renderer.entity.builder;

import Renderer.entity.Entity;
import Renderer.entity.iEntity;
import Renderer.point.MyPoint;
import Renderer.shapes.MyPolygon;
import Renderer.shapes.Tetrahedron;
import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

public class BasicEntityBuilder
{
    public static iEntity createCube(double size, double centerX, double centerY, double centerZ)
    {
        MyPoint p1,p2,p3,p4,p5,p6,p7,p8;
        
        p1 = new MyPoint(centerX + size/2,centerY + -size/2,centerZ + -size/2);
        p2 = new MyPoint(centerX + size/2,centerY + size/2,centerZ + -size/2);
        p3 = new MyPoint(centerX + size/2,centerY + size/2,centerZ + size/2);
        p4 = new MyPoint(centerX + size/2,centerY + -size/2,centerZ + size/2);
        p5 = new MyPoint(centerX + -size/2,centerY + -size/2,centerZ + -size/2);
        p6 = new MyPoint(centerX + -size/2,centerY + size/2,centerZ + -size/2);
        p7 = new MyPoint(centerX + -size/2,centerY + size/2,centerZ + size/2);
        p8 = new MyPoint(centerX + -size/2,centerY + -size/2,centerZ + size/2);
        
        Tetrahedron tetra = new Tetrahedron(
        new MyPolygon(Color.RED, p1,p2,p3,p4),
        new MyPolygon(Color.BLUE, p5,p6,p7,p8),
        new MyPolygon(Color.ORANGE, p1,p2,p6,p5),
        new MyPolygon(Color.CYAN, p1,p5,p8,p4),
        new MyPolygon(Color.GREEN, p2,p6,p7,p3),
        new MyPolygon(Color.PINK, p4,p3,p7,p8)
        );
        
        
        List<Tetrahedron> tetras = new ArrayList<>();
            tetras.add(tetra);
            
        return new Entity(tetras);
    }
    
    public static iEntity createDiamond(Color color, double size, double centerX, double centerY, double centerZ)
    {
        int edges = 15;
        double inFactor = 0.8;
        MyPoint bottom = new MyPoint(centerX, centerY, centerZ - size/2);
        MyPoint[] outerPoints = new MyPoint[edges];
        MyPoint[] innerPoints = new MyPoint[edges];
        
        for(int i = 0; i < edges; i++)
        {
            double theta = 2 * Math.PI / edges * i;
            double xPos = -Math.sin(theta) * size / 2;
            double yPos = Math.cos(theta) * size / 2;
            double zPos = size/2;
            outerPoints[i] = new MyPoint(centerX + xPos, centerY + yPos, centerZ + zPos * inFactor);
            innerPoints[i] = new MyPoint(centerX + xPos * inFactor, centerY + yPos * inFactor, centerZ + zPos);

        }
        
        MyPolygon polygons[] = new MyPolygon[2 * edges + 1];

        for(int i = 0; i < edges; i++)
        {
            polygons[i] = new MyPolygon(outerPoints[i], bottom, outerPoints[(i+1)%edges]);
        }
        for(int i = 0; i < edges; i++)
        {
            polygons[i + edges] = new MyPolygon(outerPoints[i], outerPoints[(i+1)%edges], innerPoints[(i+1)%edges], innerPoints[i]);
        }
        polygons[edges * 2] = new MyPolygon(innerPoints);
        
        Tetrahedron tetra = new Tetrahedron(color, true,  polygons);
        
        List<Tetrahedron> tetras = new ArrayList<>();
        tetras.add(tetra);
            
        return new Entity(tetras);

    }
    
    public static iEntity createSphere(Color color, double size, int resolution, double centerX, double centerY, double centerZ)
    {
        List<Tetrahedron> tetras = new ArrayList<Tetrahedron>();
        List<MyPolygon> polygons = new ArrayList<MyPolygon>();
        
        MyPoint bottom = new MyPoint(centerX, centerY, centerZ - size/2);
        MyPoint top = new MyPoint(centerX, centerY, centerZ + size/2);

        MyPoint[][] points = new MyPoint[resolution - 1][resolution];
        for(int i = 1; i < resolution; i++)
        {
            double theta = Math.PI / resolution * i;
            double zPos = -Math.cos(theta) * size/2;
            double currentRadius = Math.abs(Math.sin(theta) * size/2);
            for(int j = 0; j < resolution; j++)
            {
                double alpha = 2 * Math.PI / resolution * j;
                double xPos = -Math.sin(alpha) * currentRadius;
                double yPos = Math.cos(alpha) * currentRadius;
                points[i - 1][j] = new MyPoint(centerX + xPos, centerY + yPos, centerZ + zPos);
            }
        }

        
        for(int i = 1; i <= resolution; i++)
        {
            for(int j = 0; j < resolution; j++)
            {
                if(i==1)
                {
                polygons.add(
                    new MyPolygon(color,
                        points[i-1][j],
                        points[i-1][(j+1)%resolution],
                        bottom));
                }
                else if(i == resolution)
                {
                polygons.add(
                    new MyPolygon(color,
                        points[i-2][(j+1)%resolution],
                        points[i-2][j],
                        top));    
                }
                else
                {
                polygons.add(
                    new MyPolygon(color,
                        points[i-1][j],
                        points[i-1][(j+1)%resolution],
                        points[i-2][(j+1)%resolution],
                        points[i-2][j]));    
                }
            }
        }
        
        MyPolygon[] polygonsArray = new MyPolygon[polygons.size()];
        polygonsArray = polygons.toArray(polygonsArray);
        
        Tetrahedron tetra = new Tetrahedron(color, false,  polygonsArray);
        tetras.add(tetra);
        return new Entity(tetras);        
    }
    
    public static Tetrahedron createArmSegment(double segmentLength, double centerX, double centerY, double centerZ)
    {
        MyPoint p1,p2,p3,p4,p5,p6,p7,p8;
        
        double size = 50;
        
        p1 = new MyPoint(centerX + size/2,centerY + -size/2,centerZ + -size);
        p2 = new MyPoint(centerX + size/2,centerY + size/2,centerZ + -size);
        p3 = new MyPoint(centerX + size/2,centerY + size/2,centerZ + size);
        p4 = new MyPoint(centerX + size/2,centerY + -size/2,centerZ + size);
        p5 = new MyPoint(centerX + -size/2,centerY + -size/2,centerZ + -size);
        p6 = new MyPoint(centerX + -size/2,centerY + size/2,centerZ + -size);
        p7 = new MyPoint(centerX + -size/2,centerY + size/2,centerZ + size);
        p8 = new MyPoint(centerX + -size/2,centerY + -size/2,centerZ + size);
        
        Tetrahedron tetra = new Tetrahedron(
        new MyPolygon(Color.RED, p1,p2,p3,p4),
        new MyPolygon(Color.BLUE, p5,p6,p7,p8),
        new MyPolygon(Color.ORANGE, p1,p2,p6,p5),
        new MyPolygon(Color.CYAN, p1,p5,p8,p4),
        new MyPolygon(Color.GREEN, p2,p6,p7,p3),
        new MyPolygon(Color.PINK, p4,p3,p7,p8)
        );
        
        
        return tetra;
    }
    
    public static Tetrahedron createArmBase(double centerX, double centerY, double centerZ)
    {
        int edges = 15;
        int size = 100;
        
        MyPoint[] upperPoints = new MyPoint[edges];
        MyPoint[] lowerPoints = new MyPoint[edges];
        
        for(int i = 0; i < edges; i++)
        {
            double theta = 2 * Math.PI / edges * i;
            double xPos = -Math.sin(theta) * size / 2;
            double yPos = Math.cos(theta) * size / 2;
            double zPos = size/4;
            upperPoints[i] = new MyPoint(centerX + xPos, centerY + yPos, centerZ + centerZ - zPos);
            lowerPoints[i] = new MyPoint(centerX + xPos, centerY + yPos, centerZ + zPos);

        }
        
        MyPolygon polygons[] = new MyPolygon[edges + 2];
        
        for(int i = 0; i < edges; i++)
        {
            polygons[i] = new MyPolygon(upperPoints[i], upperPoints[(i+1)%edges], lowerPoints[(i+1)%edges], lowerPoints[i]);
        }
        
        polygons[edges] = new MyPolygon(upperPoints);
        polygons[edges+1] = new MyPolygon(lowerPoints);
        
        Tetrahedron tetra = new Tetrahedron(Color.RED,  polygons);
            
        return tetra;
    }
    
    public static iEntity createArmClaw(double centerX, double centerY, double centerZ)
    {
        return null;
    }
}
