using System.Collections;
using System.Collections.Generic;
using UnityEngine;

public class GoalGenerator : MonoBehaviour
{
    [SerializeField] public Object GoalObject;

    [SerializeField] public float ArmRadius = 10f;
    [SerializeField] public float MinGoalHeight = 1f;
    [SerializeField] public float GoalExclusionRadius = 3f;
    [SerializeField] public bool TestGoalGeneration = false;
    [SerializeField] public bool ResetGoals = false;
    [SerializeField] public int MaxGoalsToInstantiate = 100;

    private int goalObjectsAdded = 0;
    private List<Object> createdGoals;

    public void Start()
    {
        createdGoals = new List<Object>();
    }


    public static Vector3 GetRandomGoalPosition(float armDiameter, float goalExclusionRadius)
    {
        var XYGoalPosition = Vector3.zero;

        while (Vector3.Distance(XYGoalPosition, Vector3.zero) < goalExclusionRadius)
        {
            XYGoalPosition.x = Random.Range(-1 * armDiameter, armDiameter);
            float yRange = Mathf.Sqrt((-1 * XYGoalPosition.x) + Mathf.Pow(armDiameter, 2));
            XYGoalPosition.y = Random.Range(-1 * armDiameter, armDiameter);
        }

        float zRange = Mathf.Sqrt((-1 * XYGoalPosition.x) + (-1 * XYGoalPosition.y) + Mathf.Pow(armDiameter, 2));
        float randomGoalZ = Random.Range(1, armDiameter);

        return new Vector3(XYGoalPosition.x, randomGoalZ, XYGoalPosition.y);
    }

    public static Vector3 GetRandomGoalPositionInSphere(float armRadius, float minGoalHeight, float goalExclusionRadius)
    {
        Vector3 newPosition = Vector3.zero;
        while (newPosition.y < minGoalHeight || Vector3.Distance(new Vector3(0, newPosition.y, 0), newPosition) < goalExclusionRadius) 
        {
            float radiusInsideRange = Random.Range(goalExclusionRadius, armRadius);
            float randomPhi = Mathf.Deg2Rad * Random.Range(0, 90);
            float randomTheta = Mathf.Deg2Rad * Random.Range(0, 360);


            newPosition.x = radiusInsideRange * Mathf.Cos(randomTheta) * Mathf.Sin(randomPhi);
            newPosition.y = radiusInsideRange * Mathf.Cos(randomPhi);
            newPosition.z = radiusInsideRange * Mathf.Sin(randomTheta) * Mathf.Sin(randomPhi);
        }

        return newPosition;
    }

    public void Update()
    {
        if (TestGoalGeneration && goalObjectsAdded < MaxGoalsToInstantiate)
        {
            createdGoals.Add(GameObject.Instantiate(GoalObject, GetRandomGoalPositionInSphere(ArmRadius, MinGoalHeight, GoalExclusionRadius), Quaternion.identity));
            goalObjectsAdded++;
        }

        if(ResetGoals)
        {
            foreach(GameObject obj in createdGoals)
            {
                GameObject.Destroy(obj);
            }

            createdGoals = new List<Object>();
            ResetGoals = false;
            goalObjectsAdded = 0;
        }
    }
}
