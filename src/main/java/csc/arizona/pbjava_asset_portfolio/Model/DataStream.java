package csc.arizona.pbjava_asset_portfolio.Model;

import java.io.*;
import java.util.ArrayList;
import java.util.Map;

public class DataStream {
    public DataStream(){}

    /***
     * Save function that saves an Account
     * @param ac
     * @throws IOException
     * @throws ClassNotFoundException
     */
    public void saveAccount(Account ac) throws IOException, ClassNotFoundException{
        FileOutputStream fileOutputStream
                = new FileOutputStream("yourfile.txt");
        ObjectOutputStream objectOutputStream
                = new ObjectOutputStream(fileOutputStream);
        objectOutputStream.writeObject(ac);
        objectOutputStream.flush();
        objectOutputStream.close();


        System.out.println("Saved");

        System.out.println("p1:"+ac.getUsername());
        System.out.println("p1:"+ac.getHoldings());
    }

    /***
     * Save function that saves an AccountCollection
     * @param ac
     * @throws IOException
     * @throws ClassNotFoundException
     */
    public void saveCollection(ArrayList<Account> ac) throws IOException, ClassNotFoundException{
        FileOutputStream fileOutputStream
                = new FileOutputStream("acCollection.txt");
        ObjectOutputStream objectOutputStream
                = new ObjectOutputStream(fileOutputStream);
        objectOutputStream.writeObject(ac);
        objectOutputStream.flush();
        objectOutputStream.close();
        System.out.println("Saved accountCollection\n\n");
    }

    /***
     * Load function that load an Account
     * @return
     * @throws IOException
     * @throws ClassNotFoundException
     */
    public ArrayList<Account> loadCollection() throws IOException, ClassNotFoundException{
        FileInputStream fileInputStream
                = new FileInputStream("acCollection.txt");
        ObjectInputStream objectInputStream
                = new ObjectInputStream(fileInputStream);
        ArrayList<Account> acCol = (ArrayList<Account>) objectInputStream.readObject();
        objectInputStream.close();
        System.out.println("Loaded account collection:"+acCol);
        return acCol;
    }

    /***
     * Load function that load an Account
     * @return
     * @throws IOException
     * @throws ClassNotFoundException
     */
    public Account loadAccount() throws IOException, ClassNotFoundException{
        FileInputStream fileInputStream
                = new FileInputStream("yourfile.txt");
        ObjectInputStream objectInputStream
                = new ObjectInputStream(fileInputStream);
        Account p2 = (Account) objectInputStream.readObject();
        objectInputStream.close();

        System.out.println("p2:"+p2.getUsername());

        System.out.println("p2:"+p2.getHoldings());
        return p2;
    }

    /***
     * Save function that saves an AssetCollection
     * @param ac
     * @throws IOException
     * @throws ClassNotFoundException
     */
    public void saveAssetCollection(Map<String, Asset> ac) throws IOException, ClassNotFoundException{
        FileOutputStream fileOutputStream
                = new FileOutputStream("assetCol.txt");
        ObjectOutputStream objectOutputStream
                = new ObjectOutputStream(fileOutputStream);
        objectOutputStream.writeObject(ac);
        objectOutputStream.flush();
        objectOutputStream.close();
        System.out.println("Saved assetColection");
    }

    /***
     * Load function that load an AssetCollection
     * @return
     * @throws IOException
     * @throws ClassNotFoundException
     */
    public Map<String, Asset> loadAssetCollection() throws IOException, ClassNotFoundException{
        FileInputStream fileInputStream
                = new FileInputStream("assetCol.txt");
        ObjectInputStream objectInputStream
                = new ObjectInputStream(fileInputStream);
        Map<String, Asset> p2 = (Map<String, Asset>) objectInputStream.readObject();
        objectInputStream.close();
        return p2;
    }
}
