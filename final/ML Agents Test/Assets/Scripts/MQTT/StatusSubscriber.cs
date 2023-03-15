using System.Collections;
using System.Collections.Generic;
using UnityEngine;

public class StatusSubscriber : MonoBehaviour
{
    [SerializeField] Material ReadyMaterial;
    [SerializeField] Material WorkingMaterial;
    [SerializeField] MeshRenderer StatusIndicator;

    void Start()
    {
        //StatusPublisher.Publish += UpdateESP32StatusColor;
    }

    void UpdateESP32StatusColor()
    {
        if(StatusPublisher.ReadyStatus.Contains("Ready"))
        {
            StatusIndicator.material = ReadyMaterial;
        }
        else if(StatusPublisher.ReadyStatus.Contains("Working"))
        {
            StatusIndicator.material = WorkingMaterial;
        }
    }
}
