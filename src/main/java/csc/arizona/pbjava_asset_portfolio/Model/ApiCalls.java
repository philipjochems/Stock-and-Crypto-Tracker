package csc.arizona.pbjava_asset_portfolio.Model;

import java.util.ArrayList;
import java.util.List;

public class ApiCalls {
    // API CALL FRAGMENTS
    private String[] apiKeysAlphaVantage = new String[]{"T2LTRKI3ZX9KKBY6", "WMFMXQO55GFLNANT", "ZBQ27Z6LU0BG5EW9", "MN5FVI72NMHQA5MA", "224ZTRA07IIEE6NC"};
    private String[] apiKeysFMP = new String[]{"654754852c11c03c7be91519c55b82e2", "dd76dc990a4de725e3dccff26d2c36f7"};
    private int countAlphaVantage = 0;
    private int countFMP = 0;
    private String cryptoCMCApiCall = "https://pro-api.coinmarketcap.com/v1/cryptocurrency/listings/latest?CMC_PRO_API_KEY=ec689167-6c72-4b2f-888d-3f4e0955e8c3";
    private String allStockApiCall = "https://financialmodelingprep.com/api/v3/stock-screener?apikey=";

    public String getAllStockCall(){
        countFMP+=1;
        return allStockApiCall + apiKeysFMP[countFMP%2];
    }

    public String getAllCryptoCall(){
        return cryptoCMCApiCall;
    }
    public void getStockChartDataCall(String ticker){}
    public void getCryptoChartDataCall(String ticker){}
    public void getBTCTechnicalDataCall(){}

    public String getStockPriceApiCall(String ticker){
        if(countAlphaVantage == 25){
            return null;
        }
        String stockPrice = "https://www.alphavantage.co/query?"+"function=GLOBAL_QUOTE&symbol="+ticker+"&apikey="+apiKeysAlphaVantage[countAlphaVantage%5];
        System.out.println(stockPrice);
        countAlphaVantage++;
        return stockPrice;
    }

    public String getStockDailyApiCall(String ticker, boolean compact){
        if(compact){
            String stockDailyCompact = "https://www.alphavantage.co/query?"+"function=TIME_SERIES_DAILY&symbol="+ticker+"&apikey=";
            System.out.println(stockDailyCompact);
            return stockDailyCompact;
        } else {
            String stockDailyFull = "https://www.alphavantage.co/query?"+"function=TIME_SERIES_DAILY&symbol="+ticker+"&outputsize=full"+"&apikey=";
            System.out.println(stockDailyFull);
            return stockDailyFull;
        }
    }

    public String getCryptoPriceApiCall(String symbol){
        String cryptoQuote = "https://www.alphavantage.co/query?"+"function=CURRENCY_EXCHANGE_RATE&from_currency="+symbol+"&to_currency="+"USD"+"&apikey=";
        System.out.println(cryptoQuote);
        return cryptoQuote;
    }

    public String getCryptoDailyApiCall(String symbol){
        String cryptoDaily = "https://www.alphavantage.co/query?"+"function=DIGITAL_CURRENCY_DAILY&symbol="+symbol+"&market="+"USD"+"&apikey=";
        System.out.println(cryptoDaily);
        return cryptoDaily;
    }
}
