package garbagecollectors.com.snucabpool;

public class Message
{
    private String messageId;
    private String message;
    private String senderId;
    private Long createdAtTime;

    public Message(String messageId, String message, String senderId, Long createdAtTime)
    {
        this.messageId = messageId;
        this.message = message;
        this.senderId = senderId;
        this.createdAtTime = createdAtTime;
    }

    public Message()
    { }

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

    public String getMessageId()
    {
        return messageId;
    }

    public void setMessageId(String messageId)
    {
        this.messageId = messageId;
    }
}
