public class City {

    private String state, city;
    private double latitude, longitude;

    public City(String city, double latitude, double longitude, String state){
        this.city = city;
        this.latitude = latitude;
        this.longitude = longitude;
        this.state = state;
    }

    public String getState() {
        return state;
    }

    public String getCity() {
        return city;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }
}