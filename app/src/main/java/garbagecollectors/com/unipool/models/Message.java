package garbagecollectors.com.unipool.models;

public class Message
{
    private String messageId;
    private String pairUpId;
    private String message;
    private String senderId;
    private String receiverId;
    private Long createdAtTime;

    public Message(String messageId, String pairUpId, String message, String senderId, String receiverId, Long createdAtTime)
    {
        this.messageId = messageId;
        this.pairUpId = pairUpId;
        this.message = message;
        this.senderId = senderId;
	    this.receiverId = receiverId;
	    this.createdAtTime = createdAtTime;
    }

    public Message()
    { }

    public String getMessage()
    {
        return message;
    }

    public String getSenderId()
    {
        return senderId;
    }

    public String getMessageId()
    {
        return messageId;
    }

    public void setMessageId(String messageId)
    {
        this.messageId = messageId;
    }

    public String getPairUpId()
    {
        return pairUpId;
    }

    public String getReceiverId()
    {
        return receiverId;
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
