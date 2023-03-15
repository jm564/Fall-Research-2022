using System.Collections;
using System.Collections.Generic;
using System.Linq;
using System.Threading.Tasks;
using UnityEngine;
using static StatusPublisher;
using static UnityEditor.Progress;

public class ConnectionManager : MonoBehaviour
{
    [SerializeField] public string host = "192.168.4.100";
    [SerializeField] public string port = "1883";
    [SerializeField] public int BicepMax = 325;
    [SerializeField] public int ForearmMax = 225;


    [SerializeField] public MeshRenderer connectionObject;
    [SerializeField] private Material ConnectedMaterial;
    [SerializeField] private Material FailedMaterial;

    private List<Action> motions;
    private MQTTConnectionWorker mqttConnection;
    private bool ArmIsReady;
    private bool CVIsReady;

    public async void Start()
    {
        this.mqttConnection = new MQTTConnectionWorker();
        var result = await this.mqttConnection.ConnectToMQTTServer(host, port);
        connectionObject.material = result ? ConnectedMaterial : FailedMaterial;

        await mqttConnection.SubscribeToTopic("esp32/Status");
        await mqttConnection.SubscribeToTopic("BallCV/Send");
        await mqttConnection.SubscribeToTopic("BallCV/Ready");

        motions = new List<Action>();

        StatusPublisher.ReadyPublish += SetArmStatusRecord;
        StatusPublisher.CVPublish += SetCVStatus;
    }

    public void Update()
    {
        PerformNextMotion();
    }

    void SetArmStatusRecord()
    {
        this.ArmIsReady = StatusPublisher.ReadyStatus.Contains("Ready");
    }

    void SetCVStatus()
    {
        this.CVIsReady = StatusPublisher.CVStatus.Contains("Ready");
    }

    public void QueueMotion(Action action)
    {
        if(motions != null && action != null)
        {
            var sameAxisMotion = motions.FirstOrDefault(x => x.JointID == action.JointID);

            if (sameAxisMotion != null)
            {
                sameAxisMotion.value += action.value;
                return;
            }
            else
            {
                motions.Add(action);
            }
        }
    }

    public async void GetBall()
    {
        if(CVIsReady)
        {
            Debug.Log("Sending get ball to CV.");
            var request = $"BallCV/Get";
            await this.mqttConnection.PublishToTopic(request, "Get");
        }
    }

    public async void PerformNextMotion()
    {
        if (ArmIsReady)
        {
            this.ArmIsReady = false;
            var nextMotion = motions.FirstOrDefault();
            motions.Remove(nextMotion);

            if (nextMotion != null)
            {
                if (!string.IsNullOrWhiteSpace(nextMotion.JointID)
                    && nextMotion.value != 0)
                {
                    var request = $"esp32/{nextMotion.JointID}/step";
                    await this.mqttConnection.PublishToTopic(request, nextMotion.value);
                }
            }
        }
    }

    public async void ZeroArm()
    {
        Debug.Log("Arm being zeroed");
        await this.mqttConnection.PublishToTopic("esp32/ZeroArm", "A");
    }
}
