using System.Collections;
using System.Collections.Generic;
using UnityEngine;
using TMPro;
using System;

public class TextUpdateHandler : MonoBehaviour
{
    [SerializeField] TMP_Text MLSolutions;
    [SerializeField] TMP_Text IKSolutions;
    [SerializeField] TMP_Text IKAverage;
    [SerializeField] TMP_Text MLAverage;
    [SerializeField] TMP_Text TimeElapsed;

    private float timeElapsed = 0f;
    private int MLSolutionCount = 0;
    private int AvgMLSolutionCount = 0;
    private int AvgIKSolutionCount = 0;
    private int IKSolutionCount = 0;

    void Update()
    {
        timeElapsed += Time.deltaTime;

        TimeSpan ts = TimeSpan.FromSeconds(timeElapsed);
        if(ts.Minutes != 0)
        {
            MLAverage.text = $"Average: {MLSolutionCount / ts.Minutes}";
            IKAverage.text = $"Average: {IKSolutionCount / ts.Minutes}";
        }

        if(ts.Seconds % 60.0f == 0)
        {
            Debug.Log($"IK AVG: {AvgIKSolutionCount} | {ts.Minutes}");
            Debug.Log($"ML AVG: {AvgMLSolutionCount} | {ts.Minutes}");
            AvgIKSolutionCount = 0;
            AvgMLSolutionCount = 0;
        }

        TimeElapsed.text = string.Format("Time Elapsed: {0,00}:{1,00}.{2,00}", ts.Minutes, ts.Seconds, ts.Milliseconds);
    }

    public void MLSolution()
    {
        MLSolutionCount ++;
        AvgMLSolutionCount ++;
        MLSolutions.text = $"Total Solutions Found: {MLSolutionCount}";
    }

    public void IKSolution()
    {
        IKSolutionCount++;
        AvgIKSolutionCount++;
        IKSolutions.text = $"Total Solutions Found: {IKSolutionCount}";
    }
}
