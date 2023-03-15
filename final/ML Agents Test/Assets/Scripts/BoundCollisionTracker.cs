using System;
using System.Collections;
using System.Collections.Generic;
using Unity.MLAgents;
using UnityEngine;

public class BoundCollisionTracker : MonoBehaviour
{
    public MoveToGoalAgent MainAgent;
    public bool IsHand = false;

    private void OnTriggerEnter(Collider other)
    {
        if (other.TryGetComponent<Bound>(out Bound bound) || (!IsHand && other.TryGetComponent<Goal>(out Goal goal)))
        {
            MainAgent.BoundEntered();
        }
    }
}
