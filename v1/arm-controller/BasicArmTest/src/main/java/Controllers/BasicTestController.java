package Controllers;

import Socket.WebSocket;
import java.util.logging.Level;
import java.util.logging.Logger;

public class BasicTestController
{
    static WebSocket esp32Controller;
    public final int TestDuration = 2000;
    
    public static void main(String[] args)
    {
        BasicTestController testController = new BasicTestController();
        
        testController.Start();
        
        try
        {
            testController.Run();
        } 
        catch (InterruptedException ex)
        {
            Logger.getLogger(BasicTestController.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        testController.Stop();
    }
    
    public BasicTestController()
    {
        esp32Controller = new WebSocket();
    }
    
    public void Start()
    {
        esp32Controller.sendMessage("connect");
        System.out.println("connected");
    }
    
    public void Run() throws InterruptedException
    {
        MoveAxisSteps(50, 0);
        MoveAxisSteps(-50, 0);
    }
    
    public void Stop()
    {
        esp32Controller.CloseConnection();
        System.out.println("connection closed");
    }
    
    public void MoveAxisSteps(int steps, int axis)
    {
        if(steps > 63 || steps < -63)
            return;
        String message = steps > 0? "PAxis" : "MAxis";
        message += axis;
        int adjSteps = 63 + steps;
                
        esp32Controller.sendMessage(message, adjSteps);
    }
    
    public void MoveAxis(int duration, int direction, int axis) throws InterruptedException
    {
        String message = direction == 1? "PAxis" : "MAxis";
        message += axis;
        
        esp32Controller.sendMessage(message);
        Thread.sleep(duration);
        esp32Controller.sendMessage(message);
    }
    
    public void WideRangeMotionTest() throws InterruptedException
    {
        MoveAxis(3000, 1, 0);
        MoveAxis(3000, 0, 0);
        MoveAxis(3000, 1, 1);
        MoveAxis(3000, 0, 1);
        MoveAxis(3000, 1, 2);
        MoveAxis(3000, 0, 2);
    }
            
    public void TestElbow() throws InterruptedException
    {
        esp32Controller.sendMessage("PAxis2");
        Thread.sleep(TestDuration);
        esp32Controller.sendMessage("PAxis2");
        
        Thread.sleep(TestDuration);
        
        esp32Controller.sendMessage("MAxis2");
        Thread.sleep(TestDuration);
        esp32Controller.sendMessage("MAxis2");
    }
    
    public void TestShoulderVertical() throws InterruptedException
    {
        esp32Controller.sendMessage("PAxis1");
        Thread.sleep(TestDuration);
        esp32Controller.sendMessage("PAxis1");
        
        Thread.sleep(TestDuration);
        
        esp32Controller.sendMessage("MAxis1");
        Thread.sleep(TestDuration);
        esp32Controller.sendMessage("MAxis1");
    }
    
    public void TestShoulderRotation() throws InterruptedException
    {
        esp32Controller.sendMessage("PAxis0");
        Thread.sleep(TestDuration);
        esp32Controller.sendMessage("PAxis0");
        
        Thread.sleep(TestDuration);
        
        esp32Controller.sendMessage("MAxis0");
        Thread.sleep(TestDuration);
        esp32Controller.sendMessage("MAxis0");
    }
}
