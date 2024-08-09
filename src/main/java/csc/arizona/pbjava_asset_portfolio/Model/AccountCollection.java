package csc.arizona.pbjava_asset_portfolio.Model;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Author: Peter Bedrick (peterbedrick@email.arizona.edu)
 * Class: CSC436
 *
 * AccountCollection Class
 * Purpose: Keeps track of all accounts in system, includes a list of accounts
 * and the currently logged in account.
 */
public class AccountCollection implements Serializable {

    static ArrayList<Account> accounts = loadAccountCollection();
    public static Account loggedIn;

    public static ArrayList<Account> getCollection(){
        return accounts;
    }

    /**
     * loads the Account Collection.
     * @return Account Collection.
     */
    public static ArrayList<Account> loadAccountCollection(){
        DataStream data = new DataStream();

        ArrayList<Account> load = null;
        try {
            load = data.loadCollection();
        } catch (Exception e) {
            e.printStackTrace();
        }
        if(load == null){
            return new ArrayList<>();
        }
        return load;
    }


    /**
     * Adds an account to the system (list), doesn't add it if the username already exists.
     * @param username : username of the new account
     * @param password : password of the new account
     * @param admin : admin status of the new account
     * @return : newly created Account object
     */
    public static Account AddAccount(String username, String password, boolean admin) {
        Account curr = GetAccount(username);
        if(curr == null) {
            curr = new Account(username, password, admin);
            accounts.add(curr);
            System.out.println(accounts);
            // return newly created account
            loggedIn = curr;
            return curr;
        }
        // username already exists
        return null;
    }

    /**
     * Retrieves the account with the specified username.
     * @param username : username of account to find
     * @return : Account object with username parameter if found
     */
    public static Account GetAccount(String username) {
        if(accounts == null){
            return null;
        }
        for(Account acct : accounts) {
            if(acct.getUsername().equals(username)) {
                // account found
                return acct;
            }
        }
        // no account found
        return null;
    }

    /**
     * Attempts to log in to the system with a username and password.
     * @param username : username for attempted login
     * @param password : password for attempted login
     * @return : true if login successful
     */
    public static boolean Login(String username, String password) {
        Account curr = GetAccount(username);
        if(curr == null) {
            // account not found
            return false;
        }
        if(curr.getPassword().equals(password)) {
            // successful login
            loggedIn = curr;
            return true;
        }
        // password incorrect
        return false;
    }

    public static void Logout() {
        DataStream data = new DataStream();
        try {
            data.saveCollection(accounts);
        } catch (Exception e) {
            e.printStackTrace();
        }
        loggedIn = null;
    }

    @Override
    public String toString(){
        String out = "{ ";
        for (Account a : accounts){
            out += a.toString() + " ,";
        }
        return out+" }";
    }


}