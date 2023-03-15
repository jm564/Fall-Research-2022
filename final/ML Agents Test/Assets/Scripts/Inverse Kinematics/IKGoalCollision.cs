using System.Collections;
using System.Collections.Generic;
using UnityEngine;

public class IKGoalCollision : MonoBehaviour
{
    public IKEnvironmentManager environmentManager;

    private void OnTriggerEnter(Collider other)
    {
        if (other.TryGetComponent<Goal>(out Goal goal))
        {
            environmentManager.GoalEntered();
        }
    }
}
