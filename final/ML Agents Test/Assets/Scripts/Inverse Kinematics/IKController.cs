using System.Collections;
using System.Collections.Generic;
using System.Numerics;
using UnityEngine;
using Quaternion = UnityEngine.Quaternion;
using Vector3 = UnityEngine.Vector3;

public class IKController : MonoBehaviour
{
    [SerializeField] private Transform _targetTransform;
    [SerializeField] private Transform InClawTarget;
    [SerializeField] private Transform PlatformTransform;
    [SerializeField] private Transform Base;
    [SerializeField] private IKEnvironmentManager iKEnvironmentManager;
    [SerializeField] private float SamplingDistance = 15f;
    [SerializeField] private float LearningRate = 100f;
    [SerializeField] private float DistanceThreshold = 0.1f;

    public ArmJoint[] Joints;
    public float[] Angles;

    public void Reset()
    {
        float[] angles = new float[Joints.Length];

        for (int i = 0; i < Joints.Length; i++)
        {
            if (Joints[i]._rotationAxis == 'x')
            {
                angles[i] = Joints[i].transform.localRotation.eulerAngles.x;
            }
            else if (Joints[i]._rotationAxis == 'y')
            {
                angles[i] = Joints[i].transform.localRotation.eulerAngles.y;
            }
            else if (Joints[i]._rotationAxis == 'z')
            {
                angles[i] = Joints[i].transform.localRotation.eulerAngles.z;
            }
        }
        Angles = angles;
    }

    void Update()
    {
        InverseKinematics(_targetTransform.position, Angles);
        AdjustBasePosition();
    }

    public Vector3 ForwardKinematics(float[] angles)
    {
        Vector3 prevPoint = Joints[0].transform.position;
        Quaternion rotation = Quaternion.identity;
        for (int i = 1; i < Joints.Length; i++)
        {
            // Rotates around a new axis
            rotation *= Quaternion.AngleAxis(angles[i - 1], Joints[i - 1].RotationAxis);
            Vector3 nextPoint = prevPoint + rotation * Joints[i].StartOffset;

            prevPoint = nextPoint;
        }
        return prevPoint;
    }

    public float DistanceFromTarget(Vector3 target, float[] angles)
    {
        Vector3 point = ForwardKinematics(angles);
        return Vector3.Distance(point, target);
    }

    public float PartialGradient(Vector3 target, float[] angles, int i)
    {
        // Saves the angle,
        // it will be restored later
        float angle = angles[i];

        // Gradient : [F(x+SamplingDistance) - F(x)] / h
        float f_x = DistanceFromTarget(target, angles);

        angles[i] += SamplingDistance;
        float f_x_plus_d = DistanceFromTarget(target, angles);

        float gradient = (f_x_plus_d - f_x) / SamplingDistance;

        // Restores
        angles[i] = angle;

        return gradient;
    }

    private void AdjustBasePosition()
    {
        var normTargetPosition = _targetTransform.position;
        normTargetPosition.x = Base.transform.position.x;

        var posToLook = normTargetPosition - Base.transform.position;
        Base.transform.right = posToLook;
    }

    public void InverseKinematics(Vector3 target, float[] angles)
    {
        var distance = DistanceFromTarget(target, angles);
        if (distance < DistanceThreshold)
        {
            iKEnvironmentManager.GoalEntered();
            return;
        }

        for (int i = Joints.Length - 1; i >= 0; i--)
        {
            // Gradient descent
            // Update : Solution -= LearningRate * Gradient
            float gradient = PartialGradient(target, angles, i);
            angles[i] -= LearningRate * gradient;

            //Joint angle constraints, base is unconstrained so skip it
            if(i != 0)
                angles[i] = Mathf.Clamp(angles[i], Joints[i].MinAngle, Joints[i].MaxAngle);

            // Early termination for simpler convergence
            var newDistance = DistanceFromTarget(target, angles);
            if ( distance < DistanceThreshold)
            {
                iKEnvironmentManager.GoalEntered();
                return;
            }

            switch (Joints[i]._rotationAxis)
            {
                case 'x':
                    Joints[i].transform.localEulerAngles = new Vector3(angles[i], 0, 0);
                    break;
                case 'y':
                    Joints[i].transform.localEulerAngles = new Vector3(0, angles[i], 0);
                    break;
                case 'z':
                    Joints[i].transform.localEulerAngles = new Vector3(0, 0, angles[i]);
                    break;
            }
        }
    }
}
