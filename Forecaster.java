import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import com.google.gson.*;
import com.google.gson.stream.JsonReader;
import javax.swing.*;

public class Forecaster extends JFrame implements ActionListener{

    private Container c;

    private JPanel header, controls, forecast;
    private JLabel headerLabel;
    private static JLabel[] dayDataLabel;
    private JTextField cityField, stateField;
    private JButton search;

    private String headerText = "Forecaster";

    private static final String APIKEY = "";
    private static final String FILENAME = "/cities.json";
    private static InputStream stream = Forecaster.class.getClass().getResourceAsStream(FILENAME);
    private static Day[] dayData;

    String city, state;

    public Forecaster(){

        super("Forecaster");

        header = new JPanel();
        headerLabel = new JLabel();
        headerLabel.setFont(new Font("Helvetica Neue", Font.PLAIN, 30));
        headerLabel.setText(headerText);
        headerLabel.setHorizontalAlignment(SwingConstants.CENTER);
        header.add(headerLabel);
        header.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        controls = new JPanel(new GridBagLayout());

        GridBagConstraints constraints = new GridBagConstraints();
        constraints.insets = new Insets(10, 10, 10, 10);

        constraints.gridx = 0;
        constraints.gridy = 0;

        cityField = new JTextField("City", 10);
        cityField.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                JTextField source = (JTextField)e.getComponent();
                if(source.getText().equals("City")){
                    source.setText("");
                }
            }
            @Override
            public void focusLost(FocusEvent e){
                JTextField source = (JTextField)e.getComponent();
                if(source.getText().equals("")){
                    source.setText("City");
                }
            }
        });
        controls.add(cityField, constraints);

        constraints.gridx = 1;
        constraints.gridy = 0;

        stateField = new JTextField("State", 10);
        stateField.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                JTextField source = (JTextField)e.getComponent();
                if(source.getText().equals("State")){
                    source.setText("");
                }
            }
            @Override
            public void focusLost(FocusEvent e){
                JTextField source = (JTextField)e.getComponent();
                if(source.getText().equals("")){
                    source.setText("State");
                }
            }
        });
        controls.add(stateField, constraints);

        constraints.gridx = 2;
        constraints.gridy = 0;

        search = new JButton("Search");
        search.setActionCommand("search");
        search.addActionListener(this);
        controls.add(search, constraints);

        controls.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createTitledBorder("Location"),
                BorderFactory.createEmptyBorder(5,5,5,5)));

        forecast = new JPanel(new GridLayout(6, 1));

        dayDataLabel = new JLabel[6];

        for(int i = 0; i < 6; i++) {
            dayDataLabel[i] = new JLabel("<html>" +
                    "Date: " + "<br>" +
                    "Summary: " + "<br>" +
                    "Low: " + "<br>" +
                    "High: " + "<br>" +
                    "Precipitation: " +
                    "</html>");
            dayDataLabel[i].setHorizontalAlignment(JLabel.LEFT);
            dayDataLabel[i].setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
            forecast.add(dayDataLabel[i]);
        }

        forecast.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createTitledBorder("Data"),
                BorderFactory.createEmptyBorder(5,5,5,5)));

        c = getContentPane();
        c.add(header, BorderLayout.NORTH);
        c.add(forecast, BorderLayout.CENTER);
        c.add(controls, BorderLayout.SOUTH);

        setBackground(Color.LIGHT_GRAY);
        setResizable(false);
        pack();
        search.requestFocusInWindow();
        setVisible(true);
    }

    public static City findCity(String city, String state){
        try{
            stream = Forecaster.class.getClass().getResourceAsStream(FILENAME);
            JsonReader reader = new JsonReader(new InputStreamReader(stream, "UTF-8"));
            Gson gson = new GsonBuilder().create();
            reader.beginArray();
            while (reader.hasNext()) {
                City temp = gson.fromJson(reader, City.class);
                if (temp.getCity().equals(city) && temp.getState().equals(state)) {
                    return temp;
                }
            }
            reader.close();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Day[] getForecast(City city){
        String urlString = "https://api.darksky.net/forecast/" + APIKEY + "/" + city.getLatitude() + "," + city.getLongitude();
        Day tempDays[];
        try {
            StringBuilder result = new StringBuilder();
            URL url = new URL(urlString);
            URLConnection connection = url.openConnection();
            BufferedReader read = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String line;
            while ((line = read.readLine()) != null) {
                result.append(line);
            }
            read.close();

            JsonElement root = new JsonParser().parse(result.toString());
            JsonElement dailyData = root.getAsJsonObject().get("daily").getAsJsonObject().get("data");

            Gson gson = new GsonBuilder().create();
            tempDays = gson.fromJson(dailyData, Day[].class);
            return tempDays;

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void updateDisplay(){
        for(int i = 0; i < 6; i++) {
            dayDataLabel[i].setText("<html>" +
                    "Date: " + dayData[i].getDate() + "<br>" +
                    "Summary: " + dayData[i].getSummary() + "<br>" +
                    "Low: " + dayData[i].getTemperatureLow() + "<br>" +
                    "High: " + dayData[i].getTemperatureHigh() + "<br>" +
                    "Precipitation: " + dayData[i].getPrecipType() +
                    "</html>");
        }
    }

    public static void main(String[] args){
        Forecaster F = new Forecaster();
        F.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                System.exit(0);
            }
        });
    }

    public void actionPerformed(ActionEvent e) {
        city = cityField.getText();
        state = stateField.getText();
        City curr = findCity(city, state);
        if(curr == null){
            JOptionPane.showMessageDialog( Forecaster.this,"Error: Unknown Location");
        }
        else{
            dayData = getForecast(curr);
            if(dayData == null){
                JOptionPane.showMessageDialog( Forecaster.this,"Error: Connection Failure");
            }
            else{
                updateDisplay();
            }
        }
    }
}
