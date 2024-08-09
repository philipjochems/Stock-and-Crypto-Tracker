package csc.arizona.pbjava_asset_portfolio.Model;

import com.victorlaerte.asynctask.AsyncTask;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.*;
import java.math.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;

public class AssetPortfolioModel implements Serializable {
    private ApiCalls apiCalls;
    private Asset asset;
    private String[] tickers2 = new String[]{
            "AAPL","MSFT","GOOG","AMZN","FB",
            "TSLA", "TSM","NVDA","V","BABA",
            "SQ","SOFI","PLTR","FUV","COIN",
            "ABNB","GME","AMC","ARKG","SPY",
            "DIS","LMND","DNA","TWTR","SHOP"
    };
    private String[] tickers = new String[]{"AAPL","MSFT","GOOG","AMZN","TSLA"};
    private BigDecimal billion = BigDecimal.valueOf(1000000000);

    public AssetPortfolioModel(){
        apiCalls = new ApiCalls();
    }

    public String[] getTickers(){
        return tickers;
    }

    /**
     * gets list of all stocks(non-crypto) from a local file if the application
     * has been run before. Otherwise, this method uses APIcalls to load the Hashmap
     * in AssetCollection
     *
     * @return List of stocks
     */
    public List<Asset> getAllStock(){
        if(AssetCollection.getStocks().isEmpty()){
            File assetColFile = new File("assetCol.txt");
            if (assetColFile.exists()){
                System.out.println("fetching from local file");
                return fetchAllStockFromLocalFile();
            }else{
                return fetchAllStockFromAPI();
            }
        }else{
            return AssetCollection.getStocks();
        }
    }

    /**
     * Loads AssetCollection from a local file and return List<Asset> from the loaded Object
     * @return list of Asset
     */
    public List<Asset> fetchAllStockFromLocalFile(){
        AssetCollection.loadAssetCollection();
        return AssetCollection.getStocks();
    }

    public List<Asset> getAllCrypto(){
        if(AssetCollection.getCryptos().isEmpty()){
            return fetchAllCryptoFromAPI();
        }else{
            return AssetCollection.getCryptos();
        }
    }

    /**
     * Loads AssetCollection from a local file and return List<Asset> from the loaded Object
     * @return list of Asset
     */
    public List<Asset> fetchAllCryptoFromLocalFile(){
        AssetCollection.loadAssetCollection();
        return AssetCollection.getStocks();
    }

    public List<Asset> fetchAllStockFromAPI(){

        List<Asset> stocks = new ArrayList<>();
        List<JSONObject> jsonObjects = getJSONArrayfromAPI(apiCalls.getAllStockCall());
        System.out.println(apiCalls.getAllStockCall());
        for(int i=0;i<jsonObjects.size();i++){
            JSONObject curr =jsonObjects.get(i);
            //(curr.get("price"))
            //curr.get("volume")
            //BigDecimal volume =(double) curr.get("volume");
            asset = new Asset(
                    curr.get("symbol").toString(),
                    Math.floor(curr.getDouble("price")*100)/100,
                    BigDecimal.valueOf(Math.floor(curr.getBigDecimal("marketCap").divide(billion).doubleValue()*100)/100),
                    curr.getInt("volume")*1.0,
                    false,
                    false);

            AssetCollection.addAsset(asset);
            stocks.add(asset);
        }
        for (Account account : AccountCollection.accounts) {
            account.portfolio.updateHistory();
        }
        //saves AssetCollection
        AssetCollection.saveAssetCollection();
        return stocks;
    }
    public List<Asset> fetchAllCryptoFromAPI(){
        List<Asset> cryptos = new ArrayList<>();
        JSONObject jsonObject = getJSONfromAPI(apiCalls.getAllCryptoCall());
        System.out.println(apiCalls.getAllCryptoCall());
        if(jsonObject == null){
            return null;
        }
        try {
            JSONArray a = jsonObject.getJSONArray("data");
            for(int i=0; i< a.length(); i++){
                JSONObject o =  (JSONObject)a.get(i);
                String ticker = o.getString("symbol");
                double price =o.getJSONObject("quote").getJSONObject("USD").getDouble("price");
                if(price < 0.01){
                    BigDecimal bd = new BigDecimal(price);
                    bd = bd.round(new MathContext(3));
                    price = bd.doubleValue();
                }else{
                    price = Math.floor(o.getJSONObject("quote").getJSONObject("USD").getDouble("price")*100)/100;
                }
                asset = new Asset(
                        ticker,
                        price,
                        BigDecimal.valueOf(Math.floor(o.getJSONObject("quote").getJSONObject("USD").getBigDecimal("market_cap").divide(billion).doubleValue()*100)/100),
                        Math.floor(o.getJSONObject("quote").getJSONObject("USD").getDouble("percent_change_24h")*100)/100,
                        true,
                        false);
                AssetCollection.addAsset(asset);
                cryptos.add(asset);
            }
        } catch (Exception e){
            e.printStackTrace();
        }
        for (Account account : AccountCollection.accounts) {
            account.portfolio.updateHistory();
        }
        AssetCollection.saveAssetCollection();
        return cryptos;
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
            in.close();
            return new JSONObject(json);
        }catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }


    private List<JSONObject> getJSONArrayfromAPI(String apiCall) {
        try{
            List<JSONObject> jsonObjects = new ArrayList<JSONObject>();
            URL url = new URL(apiCall);
            BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()));
            String line;
            String json = "";
            while ((line = in.readLine()) != null) {

                if(line.contains("[")){
                    line=line.substring(2);

                }
                if(line.contains("},")) {
                    json+="}";

                    jsonObjects.add(new JSONObject(json));
                    json ="{";
                }else if(!line.contains("]")){
                    json += line;
                }else{
                    json+="}";
                    jsonObjects.add(new JSONObject(json));
                }

            }
            in.close();

            return jsonObjects;
        }catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public Double getStockPrice(String ticker){
        asset = null;
//        new AssetPriceAsyncTask().execute(apiCalls.getStockPriceApiCall(ticker), "stock", ticker);

        //todo: Loop through multiple api keys to maximize calls.
//        JSONObject jsonObject = null;
//        while(jsonObject == null){
//
//        }

        JSONObject jsonObject = getJSONfromAPI(apiCalls.getStockPriceApiCall(ticker));
        if(jsonObject == null){
            return null;
        }

        try {
            asset = new Asset(
                    ticker,
                    jsonObject.getJSONObject("Global Quote").getDouble("05. price"),
                    jsonObject.getJSONObject("Global Quote").getBigDecimal("09. change"),
                    jsonObject.getJSONObject("Global Quote").getDouble("10. change percent"),
                    false,
                    false);
            AssetCollection.addAsset(asset);
            return asset.price;
        } catch (Exception e){
            System.out.println();
        }
        return null;
    }

    public Double getCryptoPrice(String ticker){
        asset = null;

        JSONObject jsonObject = getJSONfromAPI(apiCalls.getCryptoPriceApiCall(ticker));
        //todo: Loop through multiple api keys to maximize calls.
        if(jsonObject == null){
            return null;
        }
        try {
            asset = new Asset(
                    ticker,
                    jsonObject.getJSONObject("Realtime Currency Exchange Rate").getDouble("5. Exchange Rate"),
                    BigDecimal.valueOf(0.0),
                    0.0,
                    true,
                    false);
            AssetCollection.addAsset(asset);
            return asset.price;
        } catch (Exception e){
            System.out.println();
        }
        return null;
    }

    public void getStockHistory(String ticker){
        try {
            String apiCall = apiCalls.getStockDailyApiCall(ticker,false);
            URL url = new URL(apiCall);
            BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()));
            String line;
            String json = "";
            while ((line = in.readLine()) != null) {
                json += line;
            }
            in.close();
            System.out.println(json);
            if (json.equals("")) {
                return;
            }

            JSONObject jsonObject = new JSONObject(json);
            Iterator<String> keys = jsonObject.getJSONObject("Time Series (Daily)").keys();

            List<String> stockPrices = new ArrayList<>();
            List<String> stockDates = new ArrayList<>();
            for (Iterator<String> it = keys; it.hasNext(); ) {
                String key = it.next();
                String price = jsonObject.getJSONObject("Time Series (Daily)").getJSONObject(key).getString("4. close");
                stockDates.add(key);
                stockPrices.add(price);
            }
            int index = 0;
            for (Integer i = stockPrices.size() - 1; i >= 0; i--) {
                String year = stockDates.get(index).substring(0, 4);
                String month = stockDates.get(index).substring(5, 7);
                String day = stockDates.get(index).substring(8, 10);
                index += 1;
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    private class AssetPriceAsyncTask extends AsyncTask<Object, Void, Double> {
        @Override
        public void onPreExecute() {}

        @Override
        public Double doInBackground(Object[] objects) {
            try{
                String apiCall = (String) objects[0];
                boolean crypto = objects[1].equals("crypto");
                String ticker = (String) objects[2];
                URL url = new URL(apiCall);
                BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()));
                String line;
                String json = "";
                while ((line = in.readLine()) != null){
//                    System.out.println("JSON Line " + line);
                    json += line;
                }
                in.close();
                JSONObject jsonObject = new JSONObject(json);
                if(crypto){
                    //todo: Loop through multiple api keys to maximize calls.
                    try {
                        asset = new Asset(
                                ticker,
                                jsonObject.getJSONObject("Realtime Currency Exchange Rate").getDouble("5. Exchange Rate"),
                                BigDecimal.valueOf(0.0),
                                0.0,
                                true,
                                false);
                        AssetCollection.addAsset(asset);
                        return asset.price;
                    } catch (Exception e){
                        System.out.println();
                    }
                }else {
                    //todo: Loop through multiple api keys to maximize calls.
                    try {
                        asset = new Asset(
                                ticker,
                                jsonObject.getJSONObject("Global Quote").getDouble("05. price"),
                                jsonObject.getJSONObject("Global Quote").getBigDecimal("09. change"),
                                jsonObject.getJSONObject("Global Quote").getDouble("10. change percent"),
                                false,
                                false);
                        AssetCollection.addAsset(asset);
                        return asset.price;
                    } catch (Exception e){
                        System.out.println();
                    }
                }
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        public void onPostExecute(Double s) {}

        @Override
        public void progressCallback(Void... voids) {}
    }
    private class StockPricesAsyncTask extends AsyncTask<Object, Void, List<String>> {

        @Override
        public void onPreExecute() {

        }

        @Override
        public List<String> doInBackground(Object object[]) {
            try {
                String apiCall = (String) object[0];
                URL url = new URL(apiCall);
                BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()));
                String line;
                String json = "";
                while ((line = in.readLine()) != null) {
                    json += line;
                }
                in.close();
                System.out.println(json);
                if (json.equals("")) {
                    return null;
                }

                JSONObject jsonObject = new JSONObject(json);
                Iterator<String> keys = jsonObject.getJSONObject("Time Series (Daily)").keys();

                List<String> stockPrices = new ArrayList<>();
                List<String> stockDates = new ArrayList<>();
                for (Iterator<String> it = keys; it.hasNext(); ) {
                    String key = it.next();
                    String price = jsonObject.getJSONObject("Time Series (Daily)").getJSONObject(key).getString("4. close");
                    stockDates.add(key);
                    stockPrices.add(price);
                }
                int index = 0;
                for (Integer i = stockPrices.size() - 1; i >= 0; i--) {
                    String year = stockDates.get(index).substring(0, 4);
                    String month = stockDates.get(index).substring(5, 7);
                    String day = stockDates.get(index).substring(8, 10);
                    index += 1;
                }
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        public void onPostExecute(List<String> strings) {

        }

        @Override
        public void progressCallback(Void... voids) {

        }

    }
}
