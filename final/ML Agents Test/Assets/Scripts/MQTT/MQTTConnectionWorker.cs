using System;
using System.Collections;
using System.Collections.Generic;
using MQTTnet;
using MQTTnet.Client;
using MQTTnet.Formatter;
using System.Text;
using System.Threading.Tasks;
using UnityEngine;
using System.Linq;
using System.Diagnostics;
using Debug = UnityEngine.Debug;

public class MQTTConnectionWorker
{
    #region Fields & Properties
    private IMqttClient? client;
    private StatusPublisher statusPublisher;
    #endregion

    #region ConnectionMethods

    public async Task<bool> ConnectToMQTTServer(string _host, string _port)
    {
        try
        {
            client = new MqttFactory().CreateMqttClient();

            var options = new MqttClientOptionsBuilder()
                .WithTcpServer(_host, int.Parse(_port))
                .WithProtocolVersion(MqttProtocolVersion.V311)
                .Build();

            var auth = await client.ConnectAsync(options);

            if (auth.ResultCode != MqttClientConnectResultCode.Success)
            {
                throw new Exception(auth.ResultCode.ToString());
            }
            else
            {
                Debug.Log("Connection Successful");
            }

            statusPublisher = new StatusPublisher();
            client!.ApplicationMessageReceivedAsync += MessageReceived;

            return true;
        }
        catch (Exception ex)
        {
            Debug.Log(ex.Message);
        }
        return false;
    }

    public async Task SubscribeToTopic(string topic)
    {
        try
        {
            var result = (await client.SubscribeAsync(
                            new MqttTopicFilterBuilder()
                            .WithTopic(topic)
                            .Build()
                        )).Items;

            switch (result.First().ResultCode)
            {
                case MqttClientSubscribeResultCode.GrantedQoS0:
                case MqttClientSubscribeResultCode.GrantedQoS1:
                case MqttClientSubscribeResultCode.GrantedQoS2:
                    Debug.Log("Subscribed to Topic: " + topic);
                    break;
                default:
                    throw new Exception(result.First().ResultCode.ToString());
            }

        }
        catch (Exception ex)
        {
            Debug.Log(ex.Message);
        }
    }

    public async Task PublishToTopic(string topic, string payload)
    {
        try
        {
            var message = new MqttApplicationMessage();
            message.Topic = topic;
            message.Payload = Encoding.Default.GetBytes(payload);
            client!.PublishAsync(message);
        }
        catch (Exception ex)
        {
            Debug.Log(ex.Message);
        }
    }

    public async Task PublishToTopic(string topic, int payload)
    {
        await PublishToTopic(topic, payload.ToString());
    }

    public Task MessageReceived(MqttApplicationMessageReceivedEventArgs args)
    {
        try
        {
            var payloadAsString = Encoding.Default.GetString(args.ApplicationMessage.Payload);
            Debug.Log(args.ApplicationMessage.Topic + ": " + payloadAsString);

            if(args.ApplicationMessage.Topic.Contains("Status"))
            {
                statusPublisher.ReadyStatusChanged(payloadAsString);
            }
            else if (args.ApplicationMessage.Topic.Contains("Send"))
            {
                statusPublisher.BallStatusChanged(payloadAsString);
            }
            else if (args.ApplicationMessage.Topic.Contains("Ready"))
            {
                statusPublisher.CVStatusChanged(payloadAsString);
            }
        }
        catch (Exception ex)
        {
            Debug.Log(ex);
        }
        return Task.CompletedTask;
    }
    #endregion
}
