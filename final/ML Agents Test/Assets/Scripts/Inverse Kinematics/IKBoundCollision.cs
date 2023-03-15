using System.Collections;
using System.Collections.Generic;
using UnityEngine;

public class IKBoundCollision : MonoBehaviour
{
    public IKEnvironmentManager environmentManager;

    public bool IsHand = false;

    private void OnTriggerEnter(Collider other)
    {
        if (other.TryGetComponent<Bound>(out Bound bound) || (!IsHand && other.TryGetComponent<Goal>(out Goal goal)))
        {
            environmentManager.BoundEntered();
        }
    }
}
