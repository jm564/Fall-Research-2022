using System.Collections;
using System.Collections.Generic;
using UnityEngine;

public class ArmController : MonoBehaviour
{
    public Transform ArmBase;
    public Transform Bicep;
    public Transform Forearm;
    public Transform Wrist;
    public Transform ClawLeft;
    public Transform ClawRight;
    public Transform WristRotationParent;

    public float SpeedConstant = 10;

    void Update()
    {
        if (Input.GetKeyDown(KeyCode.Alpha1))
        {
            ArmBase.Rotate(0, 0, SpeedConstant);
        }
        if (Input.GetKeyDown(KeyCode.Alpha2))
        {
            ArmBase.Rotate(0, 0, -SpeedConstant);
        }
        if (Input.GetKeyDown(KeyCode.Alpha4))
        {
            Bicep.Rotate(0, SpeedConstant, 0);
        }
        if (Input.GetKeyDown(KeyCode.Alpha5))
        {
            Bicep.Rotate(0, -SpeedConstant, 0);
        }
        if (Input.GetKeyDown(KeyCode.Alpha7))
        {
            Forearm.Rotate(0, SpeedConstant, 0);
        }
        if (Input.GetKeyDown(KeyCode.Alpha8))
        {
            Forearm.Rotate(0, -SpeedConstant, 0);
        }
        if (Input.GetKeyDown(KeyCode.RightArrow))
        {
            Wrist.Rotate(0, SpeedConstant, 0);
        }
        if (Input.GetKeyDown(KeyCode.LeftArrow))
        {
            Wrist.Rotate(0, -SpeedConstant, 0);
        }
        if (Input.GetKeyDown(KeyCode.UpArrow))
        {
            WristRotationParent.Rotate(0, 0, SpeedConstant);
        }
        if (Input.GetKeyDown(KeyCode.DownArrow))
        {
            WristRotationParent.Rotate(0, 0, -SpeedConstant);
        }
        if (Input.GetKeyDown(KeyCode.LeftBracket))
        {
            ClawLeft.Rotate(SpeedConstant, 0, 0);
            ClawRight.Rotate(SpeedConstant, 0, 0);
        }
        if (Input.GetKeyDown(KeyCode.RightBracket))
        {
            ClawLeft.Rotate(-SpeedConstant, 0, 0);
            ClawRight.Rotate(-SpeedConstant, 0, 0);
        }
    }
}
