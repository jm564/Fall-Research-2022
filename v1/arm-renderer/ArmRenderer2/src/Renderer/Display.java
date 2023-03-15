package Renderer;

import Renderer.Configuration.Configuration;
import Renderer.entity.EntityManager;
import Renderer.input.UserInput;
import Renderer.output.SerialESP32;
import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.image.BufferStrategy;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFrame;

public class Display extends Canvas implements Runnable
{

    private Thread thread;     
    private JFrame frame;
    private static final String TITLE = Configuration.DISPLAY_TITLE;
    public static final int DISPLAY_WIDTH = Configuration.DISPLAY_WIDTH;
    public static final int DISPLAY_HEIGHT = Configuration.DISPLAY_WIDTH;
    private static boolean running = false;
    private static final int UPDATES_PER_SECOND = Configuration.MAX_FPS;    
    private EntityManager entityManager;
    private SerialESP32 esp32 = new SerialESP32();
    
    //***********************
    public static final boolean debugNOUSB = Configuration.SERIAL_DISABLED; //CHANGE TO FALSE IF USB CONNECTION IS BEING USED
    //***********************
    
    
    private UserInput userInput;
    
    
    public Display()
    {
        this.frame = new JFrame();
        Dimension size = new Dimension(DISPLAY_WIDTH, DISPLAY_HEIGHT);
        this.setPreferredSize(size);
        
        this.userInput = new UserInput();
        this.entityManager = new EntityManager();
        
        this.addMouseListener(this.userInput.mouse);
        this.addMouseMotionListener(this.userInput.mouse);
        this.addMouseWheelListener(this.userInput.mouse);
        this.addKeyListener(this.userInput.keyboard);
    }
    
    public static void main(String[] args)
    {
        Display display = new Display();
        display.frame.setTitle(TITLE);
        display.frame.add(display);
        display.frame.pack();
        display.frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        display.frame.setLocationRelativeTo(null);
        display.frame.setResizable(false);
        display.frame.setVisible(true);
        
        display.start();
        
    }
    
    public synchronized void start()
    {
        running = true;
        this.thread = new Thread(this, "Display");
        this.thread.start();
    }
    
    public synchronized void stop() throws InterruptedException
    {
        running = false;
        this.thread.join();
        this.esp32.closePort();
    }
    
    @Override
    public void run()
    {
        long lastTime = System.nanoTime();
        long currTime = System.currentTimeMillis();
        final double nanoSec = 1000000000.0 / UPDATES_PER_SECOND; 
        double deltaTime = 0.0;
        int frames = 0;
        
        if(!this.debugNOUSB)
        {
            this.esp32.printAllPorts();
            this.esp32.SerialESP32("/dev/cu.SLAB_USBtoUART");//TODO SET ACTUAL PORT
        }   
        
        this.entityManager.init(this.userInput, this.esp32);
        
        while(running)
        {
            long now = System.nanoTime();
            deltaTime += (now - lastTime) / nanoSec;
            lastTime = now;
            
            while(deltaTime >= 1)
            {
                try
                {
                    update();
                } catch (IOException | InterruptedException ex)
                {
                    Logger.getLogger(Display.class.getName()).log(Level.SEVERE, null, ex);
                }
                deltaTime --;
                render();
                frames ++;
            }
            
            if(System.currentTimeMillis() - currTime > 1000)
            {
                currTime += 1000;
                this.frame.setTitle(TITLE + " | " + frames + " fps");
                frames = 0;
            }
            
        }
        
        try
        {
            stop();
        } catch (InterruptedException ex)
        {
            Logger.getLogger(Display.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
        
    private void render()
    {
        BufferStrategy buffS = this.getBufferStrategy();
        if(buffS == null)
        {
            this.createBufferStrategy(3);
            return;
        }
        
        Graphics g = buffS.getDrawGraphics();
        g.setColor(Color.BLACK);
        g.fillRect(0, 0, DISPLAY_WIDTH * 2, DISPLAY_HEIGHT * 2);
        
        this.entityManager.render(g);
        
        g.dispose();
        buffS.show();
    }
    
    private void update() throws IOException, InterruptedException
    {
        this.entityManager.update();
    }
}
