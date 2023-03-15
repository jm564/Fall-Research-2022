using System;
using System.Collections;
using System.Collections.Generic;
using Newtonsoft.Json;
using Unity.MLAgents;
using UnityEngine;
using UnityEngine.Serialization;

[RequireComponent(typeof(Agent))]
public class CustomDecisionRequester : MonoBehaviour
{
    [Range(1, 20)]
    [Tooltip("The frequency with which the agent requests a decision. A DecisionPeriod " +
        "of 5 means that the Agent will request a decision every 5 Academy steps.")]
    public int DecisionPeriod = 5;

    public int BallRequestPeriod = 5;


    [Tooltip("Indicates whether or not the agent will take an action during the Academy " +
        "steps where it does not request a decision. Has no effect when DecisionPeriod " +
        "is set to 1.")]
    public bool TakeActionsBetweenDecisions = true;

    public bool PullNewBallPosition = false;

    public float BallXMult = 1f;
    public float BallYMult = 1f;
    public float BallZMult = 1f;


    [SerializeField] public GameObject ball;
    [SerializeField] public ConnectionManager connectionManager;

    private bool ArmReadyState = true;
    private bool BallFoundState = false;
    private bool NewBallPositionAvailable = false;
    private float[] newBallCoords;

    [NonSerialized]
    Agent m_Agent;

    public Agent Agent
    {
        get => m_Agent;
    }

    internal void Awake()
    {
        m_Agent = gameObject.GetComponent<Agent>();
        Debug.Assert(m_Agent != null, "Agent component was not found on this gameObject and is required.");
        Academy.Instance.AgentPreStep += MakeRequests;
        StatusPublisher.ReadyPublish += ArmReadyChanged;
        StatusPublisher.BallPublish += BallFound;
    }

    void OnDestroy()
    {
        if (Academy.IsInitialized)
        {
            Academy.Instance.AgentPreStep -= MakeRequests;
        }
    }

    public struct DecisionRequestContext
    {
        public int AcademyStepCount;
    }

    void BallFound()
    {
        // If we already have an active ball don't pull in a new ball
        if (BallFoundState)
            return;

        try
        {
            Debug.Log(StatusPublisher.BallStatus);

            var ballCoords = JsonConvert.DeserializeObject<float[]>(StatusPublisher.BallStatus);

            if(ballCoords != null)
            {
                newBallCoords = ballCoords;
                NewBallPositionAvailable = true;
            }

            BallFoundState = true;
        }
        catch(Exception ex)
        {
            Debug.Log(ex);
            Debug.Log("Failed to deserialize ball position from MQTT publisher.");
        }
    }

    //Ball found state is reset by Arm Agent OnGoalEntered()
    public void ResetBallFoundState()
    {
        BallFoundState = false;
    }    

    void ArmReadyChanged()
    {
        ArmReadyState = StatusPublisher.ReadyStatus.Contains("Ready");
    }

    void MakeRequests(int academyStepCount)
    {
        var context = new DecisionRequestContext
        {
            AcademyStepCount = academyStepCount
        };

        if(ShouldGetBall(context))
        {
            connectionManager.GetBall();
        }

        if(PullNewBallPosition)
        {
            BallFoundState = false;
        }

        if(NewBallPositionAvailable && newBallCoords != null)
        {
            //workaround to allow goal position to be updated on main thread
            var newBallPosition = new Vector3()
            {
                x = newBallCoords[0] * BallXMult,
                y = ball.transform.localPosition.y * BallYMult,
                z = newBallCoords[1] * BallZMult
            };

            ball.transform.localPosition = newBallPosition;
            NewBallPositionAvailable = false;
        }

        if (ShouldRequestDecision(context))
        {
            m_Agent?.RequestDecision();
        }

        if (ShouldRequestAction(context))
        {
            m_Agent?.RequestAction();
        }
    }

    protected virtual bool ShouldRequestDecision(DecisionRequestContext context)
    {
        return context.AcademyStepCount % DecisionPeriod == 0
            && ArmReadyState
            && BallFoundState;
    }

    protected virtual bool ShouldRequestAction(DecisionRequestContext context)
    {
        return TakeActionsBetweenDecisions
            && ArmReadyState
            && BallFoundState;
    }

    protected virtual bool ShouldGetBall(DecisionRequestContext context)
    {
        return context.AcademyStepCount % BallRequestPeriod == 0
            && !BallFoundState;
    }
}

