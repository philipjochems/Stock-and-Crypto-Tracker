package csc.arizona.pbjava_asset_portfolio.Model;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class AssetCollection implements Serializable {
    public static Map<String, Asset> assets = new HashMap<>();
    private static DataStream dataStream = new DataStream();

    public AssetCollection(){};

    /**
     * Saves assetCollection using DataStream class
     */
    public static void saveAssetCollection(){
        try {
            dataStream.saveAssetCollection(assets);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * Loads assetCollection using DataStream class
     */
    public static void loadAssetCollection(){
        Map<String, Asset> load = null;
        try {
            load = dataStream.loadAssetCollection();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        assets = load;
    }


    public static Asset getAsset(String ticker){
        return assets.get(ticker);
    };

    public static List<Asset> getAllAssets(){
        return assets.values().stream().toList();
    }

    public static List<Asset> getStocks(){
        List<Asset> stocks = new ArrayList<>();

        for(String key : assets.keySet()){
            if(!assets.get(key).isCrypto()){
                stocks.add(assets.get(key));
            }
        }
        return stocks;
    }

    public static List<Asset> getCryptos(){
        List<Asset> cryptos = new ArrayList<>();
        for(String key : assets.keySet()){
            if(assets.get(key).isCrypto()){
                cryptos.add(assets.get(key));
            }
        }
        return cryptos;
    }

    public static void addAsset(Asset a){
        if(assets.containsKey(a.getTicker())){
            assets.replace(a.getTicker(), a);
        }else{
            assets.put(a.getTicker(), a);
        }
    }

    public static void addAssets(List<Asset> assetList){
        for(int i=0; i<assetList.size(); i++){
            if(assets.containsKey(assetList.get(i).getTicker())){
                assets.replace(assetList.get(i).getTicker(), assetList.get(i));
            }else{
                assets.put(assetList.get(i).getTicker(), assetList.get(i));
            }
        }
    }

    @Override
    public String toString(){
        String out = "";
        out += assets.toString();
        return out;
    }



}
