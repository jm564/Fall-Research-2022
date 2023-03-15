package Renderer.input;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class Keyboard implements KeyListener
{
    private boolean[] keys = new boolean[66568];
    private boolean left,right,up,down,forward,backward,axis1Forward,axis1Backward,axis2Forward,axis2Backward,axis3Forward,axis3Backward,axis4Forward,axis4Backward;
    public void update()
    {
        this.left = this.keys[KeyEvent.VK_LEFT] || this.keys[KeyEvent.VK_A];
        this.right = this.keys[KeyEvent.VK_RIGHT] || this.keys[KeyEvent.VK_D];
        this.forward = this.keys[KeyEvent.VK_UP] || this.keys[KeyEvent.VK_W];
        this.backward = this.keys[KeyEvent.VK_DOWN] || this.keys[KeyEvent.VK_S];
        this.up = this.keys[KeyEvent.VK_SPACE];
        this.down = this.keys[KeyEvent.VK_SHIFT];
        
        this.axis1Forward = this.keys[KeyEvent.VK_U];
        this.axis1Backward = this.keys[KeyEvent.VK_J];
        this.axis2Forward = this.keys[KeyEvent.VK_I];
        this.axis2Backward = this.keys[KeyEvent.VK_K];
        this.axis3Forward = this.keys[KeyEvent.VK_O];
        this.axis3Backward = this.keys[KeyEvent.VK_L];
        this.axis4Forward = this.keys[KeyEvent.VK_P];
        this.axis4Backward = this.keys[KeyEvent.VK_SEMICOLON];
    }
    
    public boolean getAxis1Forward()
    {
        return this.axis1Forward;
    }
    
    public boolean getAxis1Backward()
    {
        return this.axis1Backward;
    }
    
    public boolean getAxis2Forward()
    {
        return this.axis2Forward;
    }
    
    public boolean getAxis2Backward()
    {
        return this.axis2Backward;
    }
    
    public boolean getAxis3Forward()
    {
        return this.axis3Forward;
    }
    
    public boolean getAxis3Backward()
    {
        return this.axis3Backward;
    }
    
    public boolean getAxis4Forward()
    {
        return this.axis4Forward;
    }
    
    public boolean getAxis4Backward()
    {
        return this.axis4Backward;
    }
    
    public boolean getUp()
    {
        return this.up;
    }
    
    public boolean getDown()
    {
        return this.down;
    }
    
    public boolean getLeft()
    {
        return this.left;
    }
    
    public boolean getRight()
    {
        return this.right;
    }
    
    public boolean getForward()
    {
        return this.forward;
    }
    
    public boolean getBackward()
    {
        return this.backward;
    }
    
    @Override
    public void keyPressed(KeyEvent event)
    {
        keys[event.getKeyCode()] = true;
    }

    @Override
    public void keyReleased(KeyEvent event)
    {
        keys[event.getKeyCode()] = false;

    }

    @Override
    public void keyTyped(KeyEvent e)
    {
    }
    
}
