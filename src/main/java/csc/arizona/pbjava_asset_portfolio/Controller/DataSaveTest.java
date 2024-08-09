package csc.arizona.pbjava_asset_portfolio.Controller;
import csc.arizona.pbjava_asset_portfolio.Model.*;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Author: Jasur Jiasuer (jasur@email.arizona.edu)
 * Class: CSC436
 *
 * Tests DataSaver, this class will eventually be deleted.
 */
public class DataSaveTest {

    public static void main(String[] args) throws IOException, ClassNotFoundException {
        TestAssetCollection();
    }

    private static void TestAssetCollection() throws IOException, ClassNotFoundException {
        Map<String, Asset> ac = new HashMap<String, Asset>();
        Asset a = new Asset("Tic tic", 111.0 , new BigDecimal(999), 0.03, true, false);
        Asset b = new Asset("Tic tac", 321.0 , new BigDecimal(999), 0.33, false, false);
        ac.put(a.getTicker(), a);
        ac.put(b.getTicker(), b);
        System.out.println(ac);

        DataStream data = new DataStream();
        data.saveAssetCollection(ac);

        Map<String, Asset> load = data.loadAssetCollection();

        System.out.println(load.toString());

    }

    private static void TestAccount() throws IOException, ClassNotFoundException {
        ArrayList<Account> accounts = AccountCollection.getCollection();
        Account j = AccountCollection.GetAccount("Jasur");
        Account t = AccountCollection.GetAccount("Tristan");
        j.addHolding(new Asset("ticker", 1.5, new BigDecimal("63745"), 0.05, true, false),5,50);
        t.addHolding(new Asset("ticker", 100.2, new BigDecimal("77"), 0.05, false,false),10,900);
        System.out.println(AccountCollection.GetAccount("Jasur"));
        System.out.println(AccountCollection.GetAccount("Tristan"));
        System.out.println(accounts);
        DataStream data = new DataStream();
        data.saveCollection(accounts);

        ArrayList<Account> load = AccountCollection.loadAccountCollection();
        Account j2 = load.get(0);
        Account t2 = load.get(1);
        System.out.println(j2);
        System.out.println(t2);
    }
}