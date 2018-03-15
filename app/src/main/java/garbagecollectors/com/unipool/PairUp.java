package garbagecollectors.com.unipool;

import java.util.ArrayList;

public class PairUp
{
    private String pairUpId;

    private String creatorId;   //person who created the tripEntry
    private String requesterId;

    private ArrayList<String> messages;

    public PairUp(String pairUpId, String creatorId, String requesterId, ArrayList<String> messages)
    {
        this.pairUpId = pairUpId;
        this.creatorId = creatorId;
        this.requesterId = requesterId;
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

}
