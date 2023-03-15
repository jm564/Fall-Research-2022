using System.Collections;
using System.Collections.Generic;
using UnityEngine;
using Unity.MLAgents;
using Unity.MLAgents.Sensors;
using Unity.MLAgents.Actuators;
using Unity.VisualScripting;

public class MoveToGoalAgent : Agent
{
    [SerializeField] public Transform goalTransform;
    [SerializeField] private Transform InClawTarget;

    [SerializeField] private EnvironmentManager Manager;
    [SerializeField] private PhysicalArmManager armManager;

    [SerializeField] private PickStockPlatformGoalGenerator PickPlatform;
    [SerializeField] private PickStockPlatformGoalGenerator StockPlatform;

    [SerializeField] private Transform BaseTransform;
    [SerializeField] private Transform BicepTransform;
    [SerializeField] private Transform ForearmTransform;
    [SerializeField] private Transform WristTransform;
    [SerializeField] private Transform WristRotationTransform;
    [SerializeField] private Transform ClawLeftTransform;
    [SerializeField] private Transform ClawRightTransform;

    [SerializeField] private MeshRenderer PlatformMeshRenderer;
    [SerializeField] private Transform PlatformTransform;
    [SerializeField] private Material WinMaterial;
    [SerializeField] private Material PickMaterial;
    [SerializeField] private Material LoseMaterial;

    [SerializeField] private bool Inference = false;
    [SerializeField] private bool TrainForSphereRegion = false;

    [SerializeField] private TextUpdateHandler uiHandler;

    public float MovementSpeed = 90f;
    public float armRadius = 20f;
    public float StartSmallGoalRewards = 10f;
    public float goalExclusionRadius = 5f;
    public float goalPrecisionTolerance = 1f;
    public float minGoalHeight = 1f;

    public Vector3 goalPosition = Vector3.zero;
    public bool goalPositionUpdated = false;

    private float closestDistToGoal = 10f;
    private float[] angles;

    private bool PickGoalHit = false;

    private CustomDecisionRequester decisionRequester;

    void Awake()
    {
        decisionRequester = gameObject.GetComponent<CustomDecisionRequester>();

        if (decisionRequester == null)
            Debug.Log("Failed to find custom decision requester for agent.");
    }

    public override void OnEpisodeBegin()
    {
        if (!Inference)
        {
            ResetGoalPosition();
            SetDefaultArmPositions();
            RotateArmPositionsRandom();
            PickGoalHit = false;
        }
        else if(Inference)
        {
            ResetGoalPosition();
            SetDefaultArmPositions();
            PickGoalHit = false;
        }

        angles = new float[] { 0, 0, 0, 0, 0 };
    }

    public void ResetGoalPosition()
    {
        closestDistToGoal = StartSmallGoalRewards;

        if(TrainForSphereRegion)
            goalTransform.localPosition = GoalGenerator.GetRandomGoalPositionInSphere(armRadius, minGoalHeight, goalExclusionRadius);
        else
            goalTransform.localPosition = PickPlatform.GetRandomGoalPositionOnPlatform();
    }

    public void SetStockGoalPosition()
    {
        closestDistToGoal = StartSmallGoalRewards;
        goalTransform.localPosition = StockPlatform.GetRandomGoalPositionOnPlatform();
    }

    private void SetDefaultArmPositions()
    {
        BaseTransform.rotation = Quaternion.Euler(0, 0, 0);
        BicepTransform.rotation = Quaternion.Euler(-90, 0, 0);
        ForearmTransform.rotation = Quaternion.Euler(-90, 0, 0);
        WristTransform.rotation = Quaternion.Euler(-90, 0, 0);
        WristRotationTransform.rotation = Quaternion.identity;
    }

    private void RotateArmPositionsRandom()
    {
        BaseTransform.Rotate(0, 0, Random.Range(-150, 150f));
        BicepTransform.Rotate(0, Random.Range(-50f, 50f), 0);
        ForearmTransform.Rotate(0, Random.Range(-50f, 50f), 0);
        WristRotationTransform.Rotate(0, Random.Range(-50f, 50f), 0);
        WristTransform.Rotate(0, Random.Range(-150, 150f), 0);
    }

    private Vector3 SetRandomSmallSegmentAngles(bool isRotational, int axisToRotate)
    {
        Vector3 euler = new Vector3();
        float range = isRotational ? Random.Range(-50f, 50f) : Random.Range(-150, 150f);

        switch (axisToRotate)
        {
            case 0:
                euler.x = range;
                break;
            case 1:
                euler.y = range;
                break;
            case 2:
                euler.z = range;
                break;
        }
        return euler;
    }

    public override void CollectObservations(VectorSensor sensor) //space size of 10 //axis rotation & hand position
    {
        sensor.AddObservation(BaseTransform.localRotation.eulerAngles.y / 360);
        sensor.AddObservation(BicepTransform.localRotation.eulerAngles.x / 360);
        sensor.AddObservation(ForearmTransform.localRotation.eulerAngles.x / 360);
        sensor.AddObservation(WristTransform.localRotation.eulerAngles.x / 360);
        sensor.AddObservation(WristRotationTransform.localRotation.eulerAngles.x / 360);
        sensor.AddObservation( GetPosRelativeToBasePlatform(InClawTarget.position) );
        sensor.AddObservation( GetPosRelativeToBasePlatform(goalTransform.position) );
        sensor.AddObservation( GetDistanceToGoal() );
        sensor.AddObservation(angles);
    }

    private Vector3 GetPosRelativeToBasePlatform(Vector3 v)
    {
        Vector3 platform = PlatformTransform.position;
        return new Vector3(v.x - platform.x, v.y - platform.y, v.z - platform.z);
    }

    private float GetDistanceToGoal()
    {
        Vector3 handPos = GetPosRelativeToBasePlatform(InClawTarget.position);
        Vector3 goalPos = GetPosRelativeToBasePlatform(goalTransform.position);

        return Vector3.Distance(handPos, goalPos);
    }

    public override void OnActionReceived(ActionBuffers actions)
    {
        this.angles = actions.ContinuousActions.ToArrayPooled();
        float moveBase = Mathf.Clamp(actions.ContinuousActions[0], -1f, 1f) * MovementSpeed;
        float moveBicep = Mathf.Clamp(actions.ContinuousActions[1], -1f, 1f) * MovementSpeed;
        float moveForearm = Mathf.Clamp(actions.ContinuousActions[2], -1f, 1f) * MovementSpeed;
        float rotateWrist = Mathf.Clamp(actions.ContinuousActions[3], -1f, 1f) * MovementSpeed;
        float moveWrist = Mathf.Clamp(actions.ContinuousActions[4], -1f, 1f)* MovementSpeed;
        int clawSetting = 0;// actions.DiscreteActions[0];

        if(Inference)
        {
            armManager.InterpretArmMovement(actions, MovementSpeed);
        }

        BaseTransform.Rotate(0, moveBase * MovementSpeed, 0);
        BicepTransform.Rotate(0, moveBicep * MovementSpeed, 0);
        ForearmTransform.Rotate(0, moveForearm * MovementSpeed, 0);
        WristRotationTransform.Rotate(0, rotateWrist * MovementSpeed, 0);
        WristTransform.Rotate(0, moveWrist * MovementSpeed, 0);

        if(clawSetting == 1 && ClawLeftTransform.localRotation.x == 0)
        {
            ClawLeftTransform.Rotate(60, 0, 0);
            ClawRightTransform.Rotate(60, 0, 0);
        }

        if(clawSetting == 2 && ClawLeftTransform.localRotation.x == 0.5)
        {
            ClawLeftTransform.Rotate(-60, 0, 0);
            ClawRightTransform.Rotate(-60, 0, 0);
        }

        SetActionReward();
    }

    public void SetActionReward()
    {
        var distToGoal = GetDistanceToGoal();

        if(distToGoal <= closestDistToGoal || distToGoal <= goalPrecisionTolerance)
        {
            SetReward(0.1f);
            closestDistToGoal = distToGoal;
        }
        else if(distToGoal > closestDistToGoal || distToGoal > goalPrecisionTolerance)
        {
            SetReward(-0.1f);
        }
    }

    public override void Heuristic(in ActionBuffers actionsOut)
    {
        ActionSegment<float> continuousActions = actionsOut.ContinuousActions;
        ActionSegment<int> discreteActions = actionsOut.DiscreteActions;

        if (Input.GetKey(KeyCode.Alpha1))
        {
            continuousActions[0] = 1f;
        }
        if (Input.GetKey(KeyCode.Alpha2))
        {
            continuousActions[0] = -1f;
        }
        if (Input.GetKey(KeyCode.Alpha4))
        {
            continuousActions[1] = 1f;
        }
        if (Input.GetKey(KeyCode.Alpha5))
        {
            continuousActions[1] = -1f;
        }
        if (Input.GetKey(KeyCode.Alpha7))
        {
            continuousActions[2] = 1f;
        }
        if (Input.GetKey(KeyCode.Alpha8))
        {
            continuousActions[2] = -1f;
        }
        if (Input.GetKey(KeyCode.RightArrow))
        {
            continuousActions[3] = 1f;
        }
        if (Input.GetKey(KeyCode.LeftArrow))
        {
            continuousActions[3] = -1f;
        }
        if (Input.GetKey(KeyCode.UpArrow))
        {
            continuousActions[4] = 1f;
        }
        if (Input.GetKey(KeyCode.DownArrow))
        {
            continuousActions[4] = -1f;
        }
        if (Input.GetKey(KeyCode.LeftBracket))
        {
            discreteActions[0] = 1;
        }
        if (Input.GetKey(KeyCode.RightBracket))
        {
            discreteActions[0] = 2;
        }
    }

    public void GoalEntered()
    {
        if (!Inference && TrainForSphereRegion)
        {
            SetReward(+1f);
            Debug.Log("Episode Won");
            PlatformMeshRenderer.material = WinMaterial;
            EndEpisode();
        }
        else if(!Inference && !TrainForSphereRegion)
        {
            SetReward(+0.5f);
            Debug.Log(PickGoalHit ? "Stock " : "Pick " + "Goal Entered");
            SetStockGoalPosition();

            if(PickGoalHit)
            {
                Debug.Log("Episode Won");
                PlatformMeshRenderer.material = WinMaterial;

                if(decisionRequester != null)
                    decisionRequester.ResetBallFoundState();

                EndEpisode();
            }
            else
            {
                PickGoalHit = true;
                PlatformMeshRenderer.material = PickMaterial;
            }
        }
        else if(Inference && !TrainForSphereRegion)
        {
            //Debug.Log(PickGoalHit ? "Stock " : "Pick " + "Goal Entered");
            SetStockGoalPosition();
            PlatformMeshRenderer.material = PickMaterial;

            if (PickGoalHit)
            {
                PlatformMeshRenderer.material = WinMaterial;

                if (decisionRequester != null)
                    decisionRequester.ResetBallFoundState();

                EndEpisode();
            }

            uiHandler.MLSolution();
            PickGoalHit = true;
        }
    }

    public void BoundEntered()
    {
        if (!Inference)
        {
            SetReward(-1f);
        }

        if(decisionRequester != null)
            decisionRequester.ResetBallFoundState();

        PlatformMeshRenderer.material = LoseMaterial;
        EndEpisode();
    }
}
