using System.Collections;
using System.Collections.Generic;
using Unity.MLAgents;
using UnityEngine;

public class IKEnvironmentManager : MonoBehaviour
{
    [SerializeField] public Transform goalTransform;
    [SerializeField] private Transform InClawTarget;
    [SerializeField] private Transform BaseTransform;
    [SerializeField] private Transform BicepTransform;
    [SerializeField] private Transform ForearmTransform;
    [SerializeField] private Transform WristTransform;
    [SerializeField] private Transform WristRotationTransform;
    [SerializeField] private Transform ClawLeftTransform;
    [SerializeField] private Transform ClawRightTransform;
    [SerializeField] private PickStockPlatformGoalGenerator PickPlatform;
    [SerializeField] private PickStockPlatformGoalGenerator StockPlatform;
    [SerializeField] private IKController ikController;
    [SerializeField] private TextUpdateHandler uiHandler;

    [SerializeField] private MeshRenderer PlatformMeshRenderer;
    [SerializeField] private Material WinMaterial;
    [SerializeField] private Material PickMaterial;
    [SerializeField] private Material LoseMaterial;

    private bool PickGoalHit = false;

    void Start()
    {
        ResetEnvironment();
    }

    private void ResetEnvironment()
    {
        SetDefaultArmPositions();
        SetPickGoalLocation();
        ikController.Reset();
        PickGoalHit = false;
    }

    private void SetDefaultArmPositions()
    {
        BaseTransform.rotation = Quaternion.Euler(0, 0, 0);
        BicepTransform.rotation = Quaternion.Euler(0, 0, 0);
        ForearmTransform.rotation = Quaternion.Euler(0, 0, 0);
        WristTransform.rotation = Quaternion.Euler(0, 0, 0);
        WristRotationTransform.rotation = Quaternion.identity;
    }

    public void SetPickGoalLocation()
    {
        goalTransform.localPosition = PickPlatform.GetRandomGoalPositionOnPlatform();
    }

    public void SetStockGoalPosition()
    {
        goalTransform.localPosition = StockPlatform.GetRandomGoalPositionOnPlatform();
    }

    public void GoalEntered()
    {
        //Debug.Log(PickGoalHit ? "Stock " : "Pick " + "Goal Entered IK");
        uiHandler.IKSolution();

        if (PickGoalHit)
        {
            //Debug.Log("Episode Won IK");
            PlatformMeshRenderer.material = WinMaterial;
            ResetEnvironment();
        }
        else
        {
            SetStockGoalPosition();
            PickGoalHit = true;
            PlatformMeshRenderer.material = PickMaterial;
        }
    }

    public void BoundEntered()
    {
        PlatformMeshRenderer.material = LoseMaterial;
        ResetEnvironment();
    }
}
