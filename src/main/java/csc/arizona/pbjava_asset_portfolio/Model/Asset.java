package csc.arizona.pbjava_asset_portfolio.Model;

import java.io.*;

import javafx.scene.chart.XYChart;
import org.json.JSONArray;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Asset Class
 * Contains info on stocks and cryptocurrencies including ticker, current price, and history of prices.
 * Uses API calls to get the prices and history of practically all stocks and cryptos
 */

public class Asset implements Serializable{
    public String ticker;
    public String info;
    public Double price;
    public BigDecimal marketCap;
    public Double percentChange;
    public boolean crypto;
    public boolean custom;
    public List<DataPoint> priceHistory;
    private boolean needsUpdate;

    public Asset(String ticker, Double price, BigDecimal marketCap, Double percentChange, boolean crypto, boolean custom){
        this.ticker = ticker;
        this.price = price;
        this.marketCap = marketCap;
        this.percentChange = percentChange;
        this.crypto = crypto;
        this.custom = custom;
    }


    public String getTicker(){return ticker;}
    public Double getPrice(){return price;}
    public BigDecimal getMarketCap(){return marketCap;}
    public Double getPercentChange(){return percentChange;}
    public Boolean isCrypto(){return crypto;}


    public List<DataPoint> getPriceHistory(int days) {
        System.out.println(priceHistory.get(priceHistory.size()-1).price + " " + priceHistory.get(priceHistory.size()-1).date);
        List<DataPoint> temp = new ArrayList<DataPoint>();
        for(int i=priceHistory.size()-days; i<priceHistory.size()-1;i++){
            temp.add(priceHistory.get(i));
        }
        return temp;
    }
    // ASSET CHART FUNCTIONS
    public List<DataPoint> getPriceHistory() {
        if(priceHistory == null) {
            if(crypto){
                try{
                    if(needsUpdate){
                        throw new IOException();
                    }
                    priceHistory = parseCryptoDailyCSV();
                }catch(IOException | ParseException e){
                    priceHistory = fetchCryptoPriceDailyHistory();
                }
            }else {
                priceHistory = fetchStockPriceHistory();
                Collections.reverse(priceHistory);
            }
        }
        return priceHistory;
    }
    public List<DataPoint> updatePriceHistory() {
        // don't update if not enough time passed?
        if(crypto){
            priceHistory = fetchCryptoPriceDailyHistory();
        }else {
            priceHistory = fetchStockPriceHistory();
        }
        return priceHistory;
    }
    private List<DataPoint> parseCryptoDailyCSV() throws IOException, ParseException {
        System.out.print("PARSING CSV -> ");
        List<DataPoint> dataPoints = new ArrayList<>();
        File file = new File("csv/currency_daily_"+ticker.toUpperCase()+"_USD.txt");
        FileReader fileReader = new FileReader(file);
        BufferedReader reader = new BufferedReader(fileReader);
        List<String> lines = reader.lines().toList();
        if(lines.get(1).contains("\"Error Message\"")){
            file.delete();
            needsUpdate = true;
            System.out.println("FILE DELETED NEEDS UPDATE  -> DONE");
            return null;
        }
        reader.close();
        for(int i=lines.size()-2;i>0; i--){
            lines.get(i).strip();
            String[] s=lines.get(i).split(",");

            String timestamp = s[0];
            String open = s[1];
            String high = s[2];
            String low = s[3];
            String close = s[4];
            String volume = s[9];
            String mktCap = s[10];

            DataPoint myDataPoint = new DataPoint();
            myDataPoint.date = new SimpleDateFormat("yyyy-MM-dd").parse(timestamp);
            myDataPoint.price =Double.valueOf(close);
            dataPoints.add(myDataPoint);
        }
        System.out.println("DONE");
        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
        System.out.println("LAST MODIFIED : " + sdf.format(file.lastModified()));

        return dataPoints;
    }
    private List<DataPoint> fetchStockPriceHistory() {

        // api call to get data points for the price history formatted into a list
        //Single stock parsing returned as JSON OBJECT
        JSONObject myObj=getJSONfromAPI("https://financialmodelingprep.com/api/v3/historical-price-full/"+ticker+"?timeseries=180&apikey=dd76dc990a4de725e3dccff26d2c36f7");
        JSONArray a;
        try {
            a = myObj.getJSONArray("historical");
        } catch (Exception E) {
            return null;
        }
        List<DataPoint> dataPoints = new ArrayList<DataPoint>();
        for(int i=0;i<a.length();i++){
            JSONObject o =  (JSONObject)a.get(i);
            String ticker = o.getString("date");
            DataPoint myDataPoint = new DataPoint();
            Date myDate = new Date();

            for (Iterator<String> it = o.keys(); it.hasNext(); ) {
                String s = it.next();

                    if(s.contains("date")){
                        try {
                            myDataPoint.date = new SimpleDateFormat("yyyy-MM-dd").parse(o.get(s).toString()); //o.get(s);
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                    }

                    if(s.equals("close")){

                        myDataPoint.price =Double.valueOf(o.get(s).toString());
                    }




                    //myDataPoint.price =Double.valueOf(o.get(s).toString());
            }
            dataPoints.add(myDataPoint);
        }

        return dataPoints;
    }
    private List<DataPoint> fetchCryptoPriceDailyHistory() {
        // api call to get data points for the price history formatted into a list
        //Single stock parsing returned as JSON OBJECT
        List<DataPoint> dataPoints = new ArrayList<>();
        InputStream input = null;
        try {
            System.out.print("WRITING FILE -> ");
            input = new URL("https://www.alphavantage.co/query?function=DIGITAL_CURRENCY_DAILY&symbol="+ticker+"&market=USD&apikey=T2LTRKI3ZX9KKBY6&datatype=csv").openStream();
            Reader reader = new InputStreamReader(input, "UTF-8");
            File file = new File("csv/currency_daily_"+ticker.toUpperCase()+"_USD.txt");
            FileWriter fileWriter = new FileWriter(file);
            int charVal;
            int ct=0;
            String line = "";
            ArrayList<String> lines = new ArrayList();
            while ((charVal = reader.read()) != -1) {
                fileWriter.append((char) charVal);
                line+=(char) charVal;
                if((char) charVal == '\n'){
                    lines.add(line);
                    line="";
                    ct+=1;
                }
            }

            if(lines.get(1).contains("\"Error Message\"")){
                file.delete();
                System.out.print("API FAILED, FILE DELETED -> ");
            }else{
                needsUpdate = false;
                try {
                    dataPoints = parseCryptoDailyCSV();
                } catch (ParseException e) {
                    System.out.print("Could Not Parse File -> ");
                }
            }
            fileWriter.close();
            System.out.println("DONE");
        } catch (IOException e) {
            e.printStackTrace();
        }

        return dataPoints;
    }
    private JSONObject getJSONfromAPI(String apiCall) {
        try{
            URL url = new URL(apiCall);
            BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()));
            String line;
            String json = "";

            while ((line = in.readLine()) != null) {
//                System.out.println("JSON Line " + line);
                json += line;

            }

                json =json.substring(0,json.length()-3)+"]}";
                System.out.println(json);

            in.close();
            return new JSONObject(json);
        }catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public String toString(){return ticker+","+price+","+marketCap+","+percentChange;}

    @Override
    public boolean equals(Object o){
        // If the object is compared with itself then return true
        if (o == this) {
            return true;
        }

        // Check if o is an instance of Asset or not
        if (!(o instanceof Asset)) {
            return false;
        }

        // typecast o to Asset so that we can compare data members
        Asset a = (Asset) o;

        // Compare the data members and return accordingly
        return a.ticker.equals(ticker)
                && a.crypto==crypto;
    }
}
