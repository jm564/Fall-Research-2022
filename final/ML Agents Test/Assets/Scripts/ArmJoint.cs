using System.Collections;
using System.Collections.Generic;
using UnityEngine;

public class ArmJoint : MonoBehaviour
{
    public Vector3 RotationAxis => Pivot.position;
    public Vector3 StartOffset;
    public Transform Pivot;
    private Transform _transform;
    public char _rotationAxis;

    public float MinAngle;
    public float MaxAngle;

    private void Awake()
    {
        _transform = this.transform;
        StartOffset = _transform.position;
    }
}
