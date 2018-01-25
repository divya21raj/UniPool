package garbagecollectors.com.snucabpool;

public class Message
{
    String message;
    String senderId;
    Long createdAtTime;

    public Message(String message, String senderId, Long createdAtTime)
    {
        this.message = message;
        this.senderId = senderId;
        this.createdAtTime = createdAtTime;
    }

    public Message()
    {
    }

    public String getMessage()
    {
        return message;
    }

    public void setMessage(String message)
    {
        this.message = message;
    }

    public String getSenderId()
    {
        return senderId;
    }

    public void setSenderId(String senderId)
    {
        this.senderId = senderId;
    }

    public Long getCreatedAtTime()
    {
        return createdAtTime;
    }

    public void setCreatedAtTime(Long createdAtTime)
    {
        this.createdAtTime = createdAtTime;
    }
}
