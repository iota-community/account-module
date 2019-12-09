package com.iota;

import static java.lang.Math.toIntExact;

import java.io.File;

import org.iota.jota.IotaAPI;
import org.iota.jota.IotaAccount;
import org.iota.jota.account.AccountStore;
import org.iota.jota.account.store.AccountFileStore;
import org.iota.jota.error.ArgumentException;

class CreateAccount {
    public static void main(String[] args) throws ArgumentException {

        // Connect to a node
        IotaAPI api = new IotaAPI.Builder()
            .protocol("https")
            .host("nodes.devnet.thetangle.org")
            .port(443)
            .build();
    
        // The seed that the account uses to generate CDAs and send bundles
        String mySeed = "PUEOTSEITFEVEWCWBTSIZM9NKRGJEIMXTULBACGFRQK9IMGICLBKW9TTEVSDQMGWKBXPVCBMMCXWMNPDX";
        
        // Create a file to store the seed state
        File file = new File("seed-state-database.json");
        AccountStore store = new AccountFileStore(file);
        
        // Create an account, using your seed
        IotaAccount account = new IotaAccount.Builder(mySeed)
            // Connect to a node
            .api(api)
            // Connect to the database
            .store(store)
            // Set the minimum weight magnitude for the Devnet (default is 14)
            .mwm(9)
            // Set a security level for CDAs (default is 3)
            .securityLevel(2)
            .build();
    
        // Start the account and any plugins
        account.start();
    
        long balance = account.availableBalance();
    
        System.out.printf("Your balance is: %d\n", toIntExact(balance));
    
        // Close the database and stop any ongoing reattachments
        account.shutdown();
    }
}

