using System;
using System.Collections.Generic;
using UnityEngine;

public class EnvironmentManager : MonoBehaviour
{
    public GameObject Environment;
    public int EnvironmentsPerRow = 3;
    public int EnvironomentsPerColumn = 3;
    public int TimesGoalIsReachedBeforeUpdate = 5;
    public int TimesGoalIsMissedBeforeUpdate = 50;
    public float SpaceBetweenEnvironments = 50;

    public float armDiameter = 20f;
    public float goalExclusionRadius = 5f;
    public float minGoalHeight = 1f;

    private List<GameObject> createdEnvironments;
    private int countGoalReached = 0;
    private int countGoalNotReached = 0;
    public bool CreateEnvironments = true;

    public void Start()
    {
        if (CreateEnvironments)
        { 
            createdEnvironments = new();
            createdEnvironments.Add(Environment);
            for (int i = 0; i < EnvironomentsPerColumn; i++)
            {
                for (int j = 0; j < EnvironmentsPerRow; j++)
                {
                    if (i == 0 && j == 0) continue;

                    GameObject newEnv = GameObject.Instantiate(Environment);
                    newEnv.transform.Translate(j * SpaceBetweenEnvironments, 0, i * SpaceBetweenEnvironments);
                    createdEnvironments.Add(newEnv);
                }
            }
        }
    }

    public void GoalReached()
    {
        countGoalReached ++;

        if(countGoalReached == TimesGoalIsReachedBeforeUpdate)
        {
            UpdateAllGoals();
            countGoalReached = 0;
            countGoalNotReached = 0;
        }
    }

    public void GoalNotReached()
    {
        countGoalNotReached ++;

        if(countGoalNotReached == TimesGoalIsMissedBeforeUpdate)
        {
            UpdateAllGoals();
            countGoalNotReached = 0;
        }
    }

    private void UpdateAllGoals()
    {
        var newPosition = GoalGenerator.GetRandomGoalPositionInSphere(armDiameter, minGoalHeight, goalExclusionRadius);
        foreach (var env in createdEnvironments)
        {
            var childAgent = env.GetComponentInChildren<MoveToGoalAgent>();
            childAgent.goalPosition = newPosition;
            childAgent.goalPositionUpdated = true;
        }
    }
}

