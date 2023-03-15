using System.Collections;
using System.Collections.Generic;
using UnityEngine;

public class PickStockPlatformGoalGenerator : MonoBehaviour
{
    [SerializeField] public bool TestGoalGeneration = false;
    [SerializeField] public bool ResetGoals = false;

    [SerializeField] public int MaxGoalsToInstantiate = 0;
    [SerializeField] public GameObject GoalObject;

    private int goalObjectsAdded;
    private List<GameObject> createdGoals;

    private bool CanGenerateGoals => TestGoalGeneration && goalObjectsAdded < MaxGoalsToInstantiate;

    void Start()
    {
        this.createdGoals = new List<GameObject>();
    }

    private void Update()
    {
        if (CanGenerateGoals)
            GenerateGoals();

        if (ResetGoals)
            ResetGoalGenerationCount();
    }

    private void GenerateGoals()
    {
        createdGoals.Add(GameObject.Instantiate(GoalObject, GetRandomGoalPositionOnPlatform(), Quaternion.identity));
        goalObjectsAdded++;
    }

    private void ResetGoalGenerationCount()
    {
        foreach (GameObject obj in createdGoals)
        {
            GameObject.Destroy(obj);
        }

        createdGoals = new List<GameObject>();
        ResetGoals = false;
        goalObjectsAdded = 0;
    }

    public Vector3 GetRandomGoalPositionOnPlatform()
    {
        Vector3 currentPosition = this.gameObject.transform.localPosition;
        Vector3 platformScale = this.gameObject.transform.localScale;
        Vector3 newPosition = Vector3.zero;

        newPosition.x = Random.Range(currentPosition.x - (platformScale.x / 2), currentPosition.x + (platformScale.x / 2));
        newPosition.y = currentPosition.y + 2;
        newPosition.z = Random.Range(currentPosition.z - (platformScale.z / 2), currentPosition.z + (platformScale.z / 2));


        return newPosition;
    }
}
