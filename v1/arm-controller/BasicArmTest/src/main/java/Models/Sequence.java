/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Models;

import java.util.Queue;
import java.util.Stack;

/**
 *
 * @author j_maslak
 */
public class Sequence
{
    public Queue<Movement> Steps;
    public Stack<Movement> StepsCompleted;
    public String name;
    
    public Movement GetNext()
    {
        if(!Steps.isEmpty())
        {
            var removed = Steps.remove();
            StepsCompleted.add(removed);
            return removed;
        }
        return null;
    }
    
    public void AddMovement(Movement movement)
    {
        Steps.add(movement);
    }
    
    public Movement GetPrevious()
    {
        if(!StepsCompleted.isEmpty())
            return StepsCompleted.pop();
        return null;
    }
}
