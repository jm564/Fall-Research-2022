using System.Collections;
using System.Collections.Generic;
using UnityEngine;

public class GoalCollisionTracker : MonoBehaviour
{
    public MoveToGoalAgent MainAgent;

    private void OnTriggerEnter(Collider other)
    {
        if (other.TryGetComponent<Goal>(out Goal goal))
        {
            MainAgent.GoalEntered();
        }
    }
}
