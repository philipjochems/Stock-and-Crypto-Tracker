package csc.arizona.pbjava_asset_portfolio.Model;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;

/**
 * Author: Peter Bedrick (peterbedrick@email.arizona.edu)
 * Class: CSC436
 *
 * Account Class
 * Purpose: Keeps track of an account, includes username, password and admin status
 */
public class Account implements Serializable {

    public String username;
    public String password;
    public boolean admin;
    public AccountPortfolio portfolio;

    /**
     * Constructor for class, sets parameters to fields
     * @param username : username for account
     * @param password : password for account
     * @param admin : boolean if account is an admin
     */
    public Account(String username, String password, boolean admin) {
        this.username = username;
        this.password = password;
        this.admin = admin;
        portfolio = new AccountPortfolio();
    }

    /**
     * Getter for the account's username
     * @return : username String
     */
    public String getUsername() {
        return username;
    }

    /**
     * Getter for the account's password
     * @return : password String
     */
    public String getPassword() {
        return password;
    }

    /**
     * Getter for the account's admin status
     * @return : admin boolean
     */
    public boolean isAdmin() {
        return admin;
    }

    /**
     * Getter for asset holdings
     * @return : list of holdings
     */
    public List<AccountPortfolio.Holding> getHoldings() {
        return portfolio.getHoldings();
    }

    /**
     * Getter for assets user follows
     * @return : list of assets
     */
    public List<Asset> getFollowing() {
        return portfolio.getFollowing();
    }

    /**
     * Adds an asset to the portfolio holdings
     * @param a : Asset to be added
     * @param quantity : number of shares
     * @param totalCost : total cost of shares
     * @return : true if added, false if already exists
     */
    public boolean addHolding(Asset a, double quantity, double totalCost) {
        boolean addRes = portfolio.addHolding(a, quantity, totalCost);
        if(!addRes){
            return updateHolding(a,quantity,totalCost);
        }
        return true;
    }

    /**
     * Adds an asset to the portfolio following
     * @param a : Asset to be added
     * @return : true if added, false if already exists
     */
    public boolean addFollowing(Asset a) {
        return portfolio.addFollowing(a);
    }

    /**
     * Removes an asset from the portfolio holdings
     * @param a : Asset to remove
     * @return : true if found and removed, false otherwise
     */
    public boolean removeHolding(Asset a) {
        return portfolio.removeHolding(a);
    }

    /**
     * Removes an asset from the portfolio following
     * @param a : Asset to remove
     * @return : true if found and removed, false otherwise
     */
    public boolean removeFollowing(Asset a) {
        return portfolio.removeFollowing(a);
    }

    /**
     * Changes amount of shares of a stock in the followed map
     * @param a : Asset
     * @param quantity : new quantity of asset
     * @param totalCost : new total cost of asset
     * @return : true if successfully changed, false if not
     */
    private boolean updateHolding(Asset a, double quantity, double totalCost) {
        return portfolio.updateHolding(a, quantity, totalCost);
    }

    @Override
    public String toString(){
        String out = "(Name: "+this.getUsername()+", Holdings: "+this.getHoldings()+")";
        return out;
    }

}