using System.Collections;
using System.Collections.Generic;
using UnityEngine;

public class GoalController : MonoBehaviour
{
    public Transform Goal;
    public float SpeedConstant = 10;

    void Update()
    {
        if (Input.GetKeyDown(KeyCode.Alpha4))
        {
            Goal.Translate(new Vector3(SpeedConstant, 0, 0));
        }
        if (Input.GetKeyDown(KeyCode.Alpha6))
        {
            Goal.Translate(new Vector3(-SpeedConstant, 0, 0));
        }
        if (Input.GetKeyDown(KeyCode.Alpha8))
        {
            Goal.Translate(new Vector3(0, 0, SpeedConstant));
        }
        if (Input.GetKeyDown(KeyCode.Alpha2))
        {
            Goal.Translate(new Vector3(0, 0, -SpeedConstant));
        }
        if (Input.GetKeyDown(KeyCode.Plus))
        {
            Goal.Translate(new Vector3(0, SpeedConstant, 0));
        }
        if (Input.GetKeyDown(KeyCode.Minus))
        {
            Goal.Translate(new Vector3(0, -SpeedConstant, 0));
        }
    }
}
