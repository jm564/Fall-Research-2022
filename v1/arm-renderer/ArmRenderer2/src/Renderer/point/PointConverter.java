package Renderer.point;

import Renderer.Display;
import java.awt.Point;

public class PointConverter
{
    private static double scale = 1;
    private static final double ZOOM_FACTOR = 1.2;

    public static void zoomIn()
    {
        scale *= ZOOM_FACTOR;
    }
    
    public static void zoomOut()
    {
        scale /= ZOOM_FACTOR;
    }
    
    public static Point convertPoint(MyPoint point3D)
    {
        double x3D = point3D.getAdjustedY()* scale;
        double y3D = point3D.getAdjustedZ() * scale;
        double depth = point3D.getAdjustedX() * scale;
        double[] newVal = scale(x3D, y3D, depth);
        
        int x2D = (int) (Display.DISPLAY_WIDTH / 2 + newVal[0]);
        int y2D = (int) (Display.DISPLAY_HEIGHT / 2 - newVal[1]);
        
        Point point2D = new Point(x2D, y2D);
        return point2D;
    }
    
    private static double[] scale(double x3D, double y3D, double depth)
    {
        double distance = Math.sqrt(x3D * x3D + y3D * y3D);
        double theta = Math.atan2(y3D, x3D);
        double depth2 = 15 - depth; // to take into account camer being 15 from origin
        double localScale = Math.abs(1400/(1400+depth2));
        distance *= localScale;
        double[] newVal = new double[2];
        newVal[0] = distance * Math.cos(theta);
        newVal[1] = distance * Math.sin(theta);
        return newVal;
    }
    
    public static void rotateAxisX(MyPoint point, boolean CW, double degrees)
    {
        double radius = Math.sqrt(point.y * point.y + point.z * point.z);
        double theta = Math.atan2(point.z, point.y);
        theta += 2*Math.PI/360 * degrees * (CW?-1:1);
        point.y = radius * Math.cos(theta);
        point.z = radius * Math.sin(theta);

    }
    
    public static void rotateAxisY(MyPoint point, boolean CW, double degrees)
    {
         double radius = Math.sqrt(point.y * point.y + point.z * point.z);
        double theta = Math.atan2(point.z, point.y);
        theta += 2*Math.PI/360 * degrees * (CW?-1:1);
        point.y = radius * Math.cos(theta);
        point.z = radius * Math.sin(theta);
    }
    
    public static void rotateAxisZ(MyPoint point, boolean CW, double degrees)
    {
        double radius = Math.sqrt(point.x * point.x + point.y * point.y);
        double theta = Math.atan2(point.y, point.x);
        theta += 2*Math.PI/360 * degrees * (CW?-1:1);
        point.x = radius * Math.cos(theta);
        point.y = radius * Math.sin(theta);

    }
    
    public static void rotateXAxisPoint(MyPoint objPoint, MyPoint rotatePoint, boolean CW, double degrees)
    {
       double temp = objPoint.y;
       double theta = degrees * Math.PI/180 * (CW?-1:1);
       objPoint.y = (((temp - rotatePoint.y) * Math.cos(theta)) - ((objPoint.z - rotatePoint.z) * Math.sin(theta))) + rotatePoint.y;
       objPoint.z = (((temp - rotatePoint.y) * Math.sin(theta)) + ((objPoint.z - rotatePoint.z) * Math.cos(theta))) + rotatePoint.z;
    }
    
//    public static void rotateXAxisPoint(MyPoint objPoint, MyPoint rotatePoint, boolean CW, double degrees)
//    {
//       double temp = objPoint.z;
//       double theta = degrees * Math.PI/180 * (CW?-1:1);
//       objPoint.z = (((temp - rotatePoint.z) * Math.cos(theta)) - ((objPoint.y - rotatePoint.y) * Math.sin(theta))) + rotatePoint.z;
//       objPoint.y = (((temp - rotatePoint.z) * Math.sin(theta)) + ((objPoint.y - rotatePoint.y) * Math.cos(theta))) + rotatePoint.y;
//    }
    
    
    public static void rotateYAxisPoint(MyPoint objPoint, MyPoint rotatePoint, boolean CW, double degrees)
    {
       double temp = objPoint.x;
       double theta = degrees * Math.PI/180 * (CW?-1:1);
       objPoint.x = (((temp - rotatePoint.x) * Math.cos(theta)) - ((objPoint.z - rotatePoint.z) * Math.sin(theta))) + rotatePoint.x;
       objPoint.z = (((temp - rotatePoint.x) * Math.sin(theta)) + ((objPoint.z - rotatePoint.z) * Math.cos(theta))) + rotatePoint.z;
    }
    
//    public static void rotateXYAxisPoint(MyPoint objPoint, MyPoint rotatePoint, boolean CW, double xDegrees, double yDegrees)
//    {
//       double tempX = objPoint.x;
//       double tempY = objPoint.y;
//
//       double theta = (xDegrees + yDegrees) * Math.PI/180 * (CW?-1:1);
//       objPoint.x = (((tempX - rotatePoint.x) * Math.cos(theta)) - ((objPoint.z - rotatePoint.z) * Math.sin(theta))) + rotatePoint.x;
//       objPoint.y = (((tempY - rotatePoint.y) * Math.cos(theta)) - ((objPoint.z - rotatePoint.z) * Math.sin(theta))) + rotatePoint.y;
//       objPoint.z = (((tempY - rotatePoint.x) * Math.sin(theta)) + ((objPoint.z - rotatePoint.z) * Math.cos(theta))) + rotatePoint.z;
//
//    }
    
//    public static void rotateYAxisPoint(MyPoint objPoint, MyPoint rotatePoint, boolean CW, double degrees)
//    {
//       double temp = objPoint.z;
//       double theta = degrees * Math.PI/180 * (CW?-1:1);
//       objPoint.z = (((temp - rotatePoint.z) * Math.cos(theta)) - ((objPoint.x - rotatePoint.x) * Math.sin(theta))) + rotatePoint.z;
//       objPoint.x = (((temp - rotatePoint.z) * Math.sin(theta)) + ((objPoint.x - rotatePoint.x) * Math.cos(theta))) + rotatePoint.x;
//    }
    
    public static void rotateZAxisPoint(MyPoint objPoint, MyPoint rotatePoint, boolean CW, double degrees)
    {
       double temp = objPoint.y;
       double theta = degrees * Math.PI/180 * (CW?-1:1);
       objPoint.y = (((temp - rotatePoint.y) * Math.cos(theta)) - ((objPoint.x - rotatePoint.x) * Math.sin(theta))) + rotatePoint.y;
       objPoint.x = (((temp - rotatePoint.y) * Math.sin(theta)) + ((objPoint.x - rotatePoint.x) * Math.cos(theta))) + rotatePoint.x;
    }

}
