package csc.arizona.pbjava_asset_portfolio.Model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * AccountPortfolio holds details of a user's portfolio.
 *  - list of holdings
 *      - asset
 *      - quantity
 *      - cost per share
 *      - initial value
 *      - current value
 *      - percent of portfolio
 *      - current profit
 *      - current percent change
 *  - list of assets following
 *  - overall value
 *  - daily percent change
 *  - assets percentage of portfolio
 */
public class AccountPortfolio implements Serializable {
    public List<Holding> holdings;
    public List<Asset> following;
    public double currPortfolioValue;
    public double initialPortfolioValue;
    public List<Double> portfolioValueHistory;
    public double portfolioDailyChange;
    public double portfolioTotalChange;
    public Map<String,Double> assetDistribution;
    public int holdingCount;

    public AccountPortfolio(){
        holdings = new ArrayList<>();
        following = new ArrayList<>();
        currPortfolioValue = 0.0;
        initialPortfolioValue = 0.0;
        portfolioValueHistory = new ArrayList<Double>();
        portfolioValueHistory.add(1000.0);
        portfolioValueHistory.add(2000.0);
        portfolioDailyChange = 0.0;
        portfolioTotalChange = 0.0;
        assetDistribution = new HashMap<>();
        holdingCount = 0;
    }

    public boolean addHolding(Asset a, double quantity, double totalCost){
        for(int i=0; i<holdings.size(); i++){
            if(holdings.get(i).asset.equals(a)){
                return false;
            }
        }
        holdings.add(new Holding(a, quantity, totalCost));
        updatePortfolio();
        return true;
    }

    public boolean updateHolding(Asset a, double quantity, double unitCost){
        for(int i=0; i<holdings.size(); i++){
            if(holdings.get(i).asset.equals(a)){
                holdings.get(i).update(quantity, unitCost);
                updatePortfolio();
                return true;
            }
        }
        return false;
    }

    public boolean removeHolding(Asset a){
        for(int i=0; i<holdings.size(); i++){
            if(holdings.get(i).asset.equals(a)){
                holdings.remove(i);
                updatePortfolio();
                return true;
            }
        }
        return false;
    }

    public List<Holding> getHoldings(){return holdings;}

    public boolean addFollowing(Asset a){
        if(following.contains(a)){
            return false;
        }else{
            following.add(a);
            return true;
        }
    }

    public boolean removeFollowing(Asset a){
        for(int i=0; i<following.size(); i++){
            if(following.get(i).equals(a)){
                following.remove(i);
                return true;
            }
        }
        return false;
    }

    public List<Asset> getFollowing(){return following;}

    public void updateHistory() {
        if (!portfolioValueHistory.isEmpty() && portfolioValueHistory.get(portfolioValueHistory.size() - 1) != currPortfolioValue){
            portfolioValueHistory.add(currPortfolioValue);
        }
        if(portfolioValueHistory.size()>1 ){
            double curr = portfolioValueHistory.get(portfolioValueHistory.size()-1);
            double prev = portfolioValueHistory.get(portfolioValueHistory.size()-2);
            System.out.println(curr+" - "+prev);
            portfolioDailyChange = curr/prev;
        }else{
            portfolioDailyChange = 0.0;
        }
    }

    private void updatePortfolio(){
        double currV = 0.0;
        double initialV = 0.0;
        int ct = 0;
        for(Holding h : holdings){
            h.percentOfPortfolio = h.holdingValue/currPortfolioValue; //Update Percentages
            assetDistribution.replace(h.asset.ticker, h.percentOfPortfolio); //Update Asset Distribution
            currV += h.holdingValue;
            initialV += h.initialValue;
            ct +=1;
        }
        holdingCount = ct;
        currPortfolioValue = currV;
        initialPortfolioValue = initialV;
        portfolioTotalChange = currV/initialV;
    }

    public class Holding implements Serializable{
        public Asset asset;
        public double quantity;
        public double unitCost;
        public double initialValue;
        public double holdingValue;
        public double percentOfPortfolio;
        public double profit;
        public double profitPercent;

        public Holding(Asset a, double q, double tc){
            asset = a;
            quantity = q;
            initialValue = tc;
            unitCost = initialValue/quantity;
            holdingValue = asset.getPrice()*quantity;
            currPortfolioValue+=holdingValue;
            initialPortfolioValue+=initialValue;
            percentOfPortfolio = holdingValue/currPortfolioValue;
            profit = holdingValue-initialValue;
            profitPercent = profit/initialValue;
            //TODO: update all holdings portfolio percentage
        }

        public void update(double q, double tc){
            quantity = q;
            initialValue = tc;
            unitCost = initialValue/quantity;
            currPortfolioValue-=holdingValue;
            holdingValue = asset.getPrice()*quantity;
            currPortfolioValue+=holdingValue;
            percentOfPortfolio = holdingValue/currPortfolioValue;
            profit = holdingValue-initialValue;
            profitPercent = profit/initialValue;
        }

        @Override
        public String toString(){
            String out = "";
            out += asset.toString()+"  q: "+quantity+"  unitcost"+unitCost;
            return out;
        }
    }
}
