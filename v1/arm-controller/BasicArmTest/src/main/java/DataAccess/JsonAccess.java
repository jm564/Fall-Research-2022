/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package DataAccess;

import Models.Sequence;

/**
 *
 * @author j_maslak
 */
public class JsonAccess
{
    public String FileName;
    public final String FileExtension = ".json";
    
    public JsonAccess(String fileName)
    {
        this.FileName = fileName;
    }
    
    public Sequence[] LoadGCode()
    {
        return null;
    }
    
    public boolean WriteGCode(Sequence[] Sequence)
    {
        boolean isSuccesful = false;
        
        return isSuccesful;
    }
}
