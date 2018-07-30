package garbagecollectors.com.unipool.models;

public class TripEntry
{
    private String entry_id;

    private String user_id;

    private String name;

    private String time, date;

    private GenLocation source, destination;

    private String message;

    private String phone;

    private String email;

    private boolean notFromApp;

    public TripEntry(String name, String entry_id, String user_id, String time, String date,
                     GenLocation source, GenLocation destination, String message, String phone, String email, boolean notFromApp)
    {
        this.entry_id = entry_id;
        this.user_id = user_id;
        this.time = time;
        this.date = date;
        this.source = source;
        this.destination = destination;
        this.name = name;
        this.message = message;
        this.phone = phone;
        this.email = email;
        this.notFromApp = notFromApp;
    }

    public TripEntry(TripEntry tripEntry)   //copyConstructor
    {
        this.entry_id = tripEntry.getEntry_id();
        this.user_id = tripEntry.getUser_id();
        this.time = tripEntry.getTime();
        this.date = tripEntry.getDate();
        this.source = tripEntry.getSource();
        this.destination = tripEntry.getDestination();
        this.name = tripEntry.getName();
        this.phone = tripEntry.getPhone();
        this.notFromApp = tripEntry.isNotFromApp();
    }

    public TripEntry()
    {}

    public String getEntry_id()
    {
        return entry_id;
    }

    public void setEntry_id(String entry_id)
    {
        this.entry_id = entry_id;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public String getUser_id()
    {
        return user_id;
    }

    public void setUser_id(String user_id)
    {
        this.user_id = user_id;
    }

    public String getTime()
    {
        return time;
    }

    public void setTime(String time)
    {
        this.time = time;
    }

    public String getDate()
    {
        return date;
    }

    public GenLocation getSource()
    {
        return source;
    }

    public GenLocation getDestination()
    {
        return destination;
    }

    public String getMessage()
    {
        return message;
    }

    public void setMessage(String message)
    {
        this.message = message;
    }

    public String getPhone()
    {
        return phone;
    }

    public void setPhone(String phone)
    {
        this.phone = phone;
    }

    public boolean isNotFromApp()
    {
        return notFromApp;
    }

    public void setNotFromApp(boolean notFromApp)
    {
        this.notFromApp = notFromApp;
    }

    public String getEmail()
    {
        return email;
    }

    public void setEmail(String email)
    {
        this.email = email;
    }
}
