package garbagecollectors.com.unipool.models;

import java.util.ArrayList;

public class PairUp
{
    private String pairUpId;

    private String creatorId;   //person who created the tripEntry
    private String requesterId;

    private String expiryDate;

    private ArrayList<String> messages;

    public PairUp(String pairUpId, String creatorId, String requesterId, String expiryDate, ArrayList<String> messages)
    {
        this.pairUpId = pairUpId;
        this.creatorId = creatorId;
        this.requesterId = requesterId;
        this.expiryDate = expiryDate;
        this.messages = messages;
    }

    public PairUp() {}

    public String getPairUpId()
    {
        return pairUpId;
    }

    public String getCreatorId()
    {
        return creatorId;
    }

    public String getRequesterId()
    {
        return requesterId;
    }

    public ArrayList<String> getMessages()
    {
        return messages;
    }

    public String getExpiryDate()
    {
        return expiryDate;
    }

    public void setExpiryDate(String expiryDate)
    {
        this.expiryDate = expiryDate;
    }
}
