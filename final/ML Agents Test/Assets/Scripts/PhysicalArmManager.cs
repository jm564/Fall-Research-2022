using System.Collections;
using System.Collections.Generic;
using UnityEngine;

public class PhysicalArmManager : MonoBehaviour
{
    [SerializeField] public string ASpeed;
    [SerializeField] public string BSpeed;
    [SerializeField] public string CSpeed;
    [SerializeField] public ConnectionManager connectionManager;
    [SerializeField] public bool ResetArm;
    [SerializeField] public bool SetSpeed;

    private float timeCounter = 0;

    public void Update()
    {
        timeCounter += Time.deltaTime;

        if (timeCounter >= 6)
        {
            //Debug.Log(moveBicepValue);
            timeCounter = 0;
        }

        if(ResetArm)
        {
            ZeroArm();
        }
        else if(SetSpeed)
        {

        }
    }

    private float moveBicepValue = 0f;

    public void InterpretArmMovement(Unity.MLAgents.Actuators.ActionBuffers actions, float MovementSpeed)
    {
        float moveBase = ( Mathf.Clamp(actions.ContinuousActions[0], -1f, 1f) * MovementSpeed);
        float moveBicep = (Mathf.Clamp(actions.ContinuousActions[1], -1f, 1f) * MovementSpeed) * ( 325 / 90);
        float moveForearm = (Mathf.Clamp(actions.ContinuousActions[2], -1f, 1f) * MovementSpeed) * ( 225 / 90);
        float rotateWrist = (Mathf.Clamp(actions.ContinuousActions[3], -1f, 1f) * MovementSpeed);
        float moveWrist = (Mathf.Clamp(actions.ContinuousActions[4], -1f, 1f) * MovementSpeed);

        moveBicepValue += moveBicep;

        if (moveBicep != 0f) connectionManager.QueueMotion(new Action { JointID = "A", value = (int) moveBicep });
        if (moveForearm != 0f) connectionManager.QueueMotion(new Action { JointID = "B", value = (int) moveForearm });
        if (rotateWrist != 0f) connectionManager.QueueMotion(new Action { JointID = "F", value = (int) rotateWrist });
        if (moveWrist != 0f) connectionManager.QueueMotion(new Action { JointID = "F", value = (int) moveWrist });
        if (moveBase != 0f) connectionManager.QueueMotion(new Action { JointID = "F", value = (int) moveBase });
    }

    public void ZeroArm()
    {
        connectionManager.ZeroArm();
        ResetArm = false;
    }
}
