import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Day {

    private long time;
    private double temperatureLow, temperatureHigh;
    private String summary, precipType;

    public Day(int time, double temperatureLow, double temperatureHigh, String summary, String precipType) {
        this.time = time;
        this.temperatureLow = temperatureLow;
        this.temperatureHigh = temperatureHigh;
        this.summary = summary;
        this.precipType = precipType;
    }

    public String getDate() {
        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy", Locale.US);
        return sdf.format(new Date(time*1000));
    }

    public double getTemperatureLow() {
        return temperatureLow;
    }

    public double getTemperatureHigh() {
        return temperatureHigh;
    }

    public String getSummary() {
        return summary;
    }

    public String getPrecipType() {
        if(precipType == null){
            return "none";
        }
        else{
            return precipType;
        }
    }
}
