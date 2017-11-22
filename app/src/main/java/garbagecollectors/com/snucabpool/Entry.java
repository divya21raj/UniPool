package garbagecollectors.com.snucabpool;

public class Entry
{
    private String entryID;                                                                         //Data type could be changed to long

    private String userId;

    private String sourceLocation, destinationLocation;

    private String time;

    private HashMap<Long, Float> map = new HashMap<>();                                             //HashMap contains entry_id(Long value) and lambda(Float value)

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
