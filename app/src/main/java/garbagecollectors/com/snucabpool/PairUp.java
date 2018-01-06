package garbagecollectors.com.snucabpool;

import java.util.ArrayList;

public class PairUp
{
    String creatorId;   //person who created the tripEntry
    String requesterId;

    ArrayList<Message> messages;

    ArrayList<String> tripEntriesPairedUpOver;

    public PairUp(String creatorId, String requesterId, ArrayList<Message> messages, ArrayList<String> tripEntriesPairedUpOver)
    {
        this.creatorId = creatorId;
        this.requesterId = requesterId;
        this.messages = messages;
        this.tripEntriesPairedUpOver = tripEntriesPairedUpOver;
    }

    public PairUp()
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

    public ArrayList<String> getTripEntriesPairedUpOver()
    {
        return tripEntriesPairedUpOver;
    }

    public void setTripEntriesPairedUpOver(ArrayList<String> tripEntriesPairedUpOver)
    {
        this.tripEntriesPairedUpOver = tripEntriesPairedUpOver;
    }
}
