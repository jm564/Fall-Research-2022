package Socket;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.Socket;
import java.net.UnknownHostException;

public class WebSocket
{
    public static String connectionStatus = "Not Connected";
    public static String socketIPAddress = "192.168.4.1";
    
    Socket soc;
    
    private boolean isConnected()
    {
        // TCheck is socket is connected to server
        if(soc!=null)
        {
                if(!soc.isClosed())
                        return true;
        }
        return false;
    }
    
    public boolean sendMessage(String message)
    {
        
        System.out.println("message to be sent: " + message);
        if(message.contains("connect"))
        {
            System.out.println(socketIPAddress);
            if(isConnected()) return true;
            try
            {
                System.out.println("trying to connect");
                soc = new Socket(socketIPAddress, 1987);
                System.out.println("Connected");
            }
            catch (UnknownHostException e1) 
            {
		e1.printStackTrace();
		System.out.println("Can not connect");
            }
            catch(IOException e1)
            {
		e1.printStackTrace();
                System.out.println("Connection failed");
            }
            return true;
        }
        
        int aASCII=97;
        int command=0;
        for(int i = 0; i < 4; i++)
        {
            String axisMessage = "PAxis" + Integer.toString(i);
            if(message.contains(axisMessage))
            {
                command = aASCII + i;
                writeCommand(command);
                break;
            }
            
            axisMessage = "MAxis" + Integer.toString(i);
            if(message.contains(axisMessage))
            {
                command = aASCII + i + 4;
                writeCommand(command);
                break;
            }
        }
        
        return true;
    }
    
    private void writeCommand(int command) 
    {
        // If socket is null return
	if(soc==null) return;
	
        // if socket is connected send command
	if(soc.isConnected())
	{
            byte[] cmdByte=new byte[4];
            cmdByte[0]=(byte)command;
            byte[] temp;
            
            try 
            {
		temp = "!".getBytes("US-ASCII");
		cmdByte[3]=temp[0];
		System.out.println("Message sent");
            } 
            catch (UnsupportedEncodingException e) 
            {
		e.printStackTrace();
		System.out.println("Error 1");
            }
            try 
            {
		soc.getOutputStream().write(cmdByte);
		System.out.println("Message sent");
            } 
            catch (IOException e1)
            {
                CloseConnection();
                sendMessage("connect");
                System.out.println("Error 2");
            }
	}
    }
    
    public boolean sendMessage(String message, int steps)
    {
        
        System.out.println("message to be sent: " + message);
        if(message.contains("connect"))
        {
            System.out.println(socketIPAddress);
            if(isConnected()) return true;
            try
            {
                System.out.println("trying to connect");
                soc = new Socket(socketIPAddress, 1987);
                System.out.println("Connected");
            }
            catch (UnknownHostException e1) 
            {
		e1.printStackTrace();
		System.out.println("Can not connect");
            }
            catch(IOException e1)
            {
		e1.printStackTrace();
                System.out.println("Connection failed");
            }
            return true;
        }
        
        int aASCII=97;
        int command=0;
        for(int i = 0; i < 4; i++)
        {
            String axisMessage = "PAxis" + Integer.toString(i);
            if(message.contains(axisMessage))
            {
                command = aASCII + i;
                writeCommand(command, steps);
                break;
            }
            
            axisMessage = "MAxis" + Integer.toString(i);
            if(message.contains(axisMessage))
            {
                command = aASCII + i + 4;
                writeCommand(command, steps);
                break;
            }
        }
        
        return true;
    }
    
    private void writeCommand(int command, int steps) 
    {
        // If socket is null return
	if(soc==null) return;
	
        // if socket is connected send command
	if(soc.isConnected())
	{
            byte[] cmdByte=new byte[4];
            cmdByte[0]=(byte)command;
            cmdByte[1]=(byte)steps;

            byte[] temp;
            
            try 
            {
		temp = "!".getBytes("US-ASCII");
		cmdByte[3]=temp[0];
		System.out.println("Message sent");
            } 
            catch (UnsupportedEncodingException e) 
            {
		e.printStackTrace();
		System.out.println("Error 1");
            }
            try 
            {
		soc.getOutputStream().write(cmdByte);
		System.out.println("Message sent");
            } 
            catch (IOException e1)
            {
                CloseConnection();
                sendMessage("connect");
                System.out.println("Error 2");
            }
	}
    }
    
    public void CloseConnection()
    {
	if(soc!=null)
	{
            if(soc.isConnected())
            {
		try {
                    soc.close();
                    System.out.println("Socket closed");
		} 
                catch (IOException e1) 
                {
                    e1.printStackTrace();
                    System.out.println("Error closing socket");
		}
            }
        }
		
    }
    
}

