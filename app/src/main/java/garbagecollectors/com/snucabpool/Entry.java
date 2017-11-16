package garbagecollectors.com.snucabpool;

public class Entry
{
    private String entryID;

    private String userId;

    private String sourceLocation, destinationLocation;

    private String time;

    public Entry(String entryID, String userId, String sourceLocation, String destinationLocation, String time)
    {
        this.entryID = entryID;
        this.userId = userId;
        this.sourceLocation = sourceLocation;
        this.destinationLocation = destinationLocation;
        this.time = time;
    }

    public Entry()
    { }

    public String getEntryID()
    {
        return entryID;
    }

    public String getUserId()
    {
        return userId;
    }

    public String getSourceLocation()
    {
        return sourceLocation;
    }

    public String getDestinationLocation()
    {
        return destinationLocation;
    }

    public String getTime()
    {
        return time;
    }
}
