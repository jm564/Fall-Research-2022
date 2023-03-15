using UnityEngine;

public class StatusPublisher
{
    public delegate void ReadyPublishStatusChanged ();
    public static event ReadyPublishStatusChanged ReadyPublish;
    public static string ReadyStatus;

    public delegate void BallPublishStatusChanged();
    public static event BallPublishStatusChanged BallPublish;
    public static string BallStatus;

    public delegate void CVPublishStatusChanged();
    public static event CVPublishStatusChanged CVPublish;
    public static string CVStatus;

    public void ReadyStatusChanged(string status)
    {
        if (ReadyPublish != null)
        {
            ReadyStatus = status;
            ReadyPublish();
        }
    }

    public void BallStatusChanged(string status)
    {
        if (BallPublish != null)
        {
            BallStatus = status;
            BallPublish();
        }
    }

    public void CVStatusChanged(string status)
    {
        if (CVPublish != null)
        {
            CVStatus = status;
            CVPublish();
        }
    }
}
