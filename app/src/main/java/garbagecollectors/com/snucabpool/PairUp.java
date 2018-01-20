package garbagecollectors.com.snucabpool;

import java.util.ArrayList;

public class PairUp
{
    String pairUpId;

    String creatorId;   //person who created the tripEntry
    String requesterId;

    ArrayList<Message> messages;

    public PairUp(String pairUpId, String creatorId, String requesterId, ArrayList<Message> messages)
    {
        this.pairUpId = pairUpId;
        this.creatorId = creatorId;
        this.requesterId = requesterId;
        this.messages = messages;
    }

    public PairUp()
    {
    }

    public String getPairUpId()
    {
        return pairUpId;
    }

    public void setPairUpId(String pairUpId)
    {
        this.pairUpId = pairUpId;
    }

    public String getCreatorId()
    {
        return creatorId;
    }

    public void setCreatorId(String creatorId)
    {
        this.creatorId = creatorId;
    }

    public String getRequesterId()
    {
        return requesterId;
    }

    public void setRequesterId(String requesterId)
    {
        this.requesterId = requesterId;
    }

    public ArrayList<Message> getMessages()
    {
        return messages;
    }

    public void setMessages(ArrayList<Message> messages)
    {
        this.messages = messages;
    }

}
