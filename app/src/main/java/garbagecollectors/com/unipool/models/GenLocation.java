package garbagecollectors.com.unipool.models;

public class GenLocation
{
    private String name;
    private String address;

    private Double latitude;
    private Double longitude;

    public GenLocation(String name, String address, Double latitude, Double longitude)
    {
        this.name = name;
        this.address = address;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public GenLocation()
    {}

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public Double getLatitude()
    {
        return latitude;
    }

    public Double getLongitude()
    {
        return longitude;
    }

}
