package garbagecollectors.com.snucabpool;

import java.util.ArrayList;

public class PairUps
{
    String creatorId;   //person who created the tripEntry
    String requesterId;

    ArrayList<Message> messages;

    ArrayList<String> tripEntriesPairedUpOver;

    public PairUps(String creatorId, String requesterId, ArrayList<Message> messages)
    {
        this.creatorId = creatorId;
        this.requesterId = requesterId;
        this.messages = messages;
    }

    public PairUps()
    {
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
