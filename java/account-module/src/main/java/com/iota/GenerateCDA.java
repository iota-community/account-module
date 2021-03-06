package com.iota;

import java.io.File;
import java.util.Date;
import java.util.concurrent.ExecutionException;

import org.iota.jota.IotaAPI;
import org.iota.jota.IotaAccount;
import org.iota.jota.account.AccountStore;
import org.iota.jota.account.deposits.ConditionalDepositAddress;
import org.iota.jota.account.deposits.methods.DepositFactory;
import org.iota.jota.account.deposits.methods.MagnetMethod;
import org.iota.jota.account.errors.AccountError;
import org.iota.jota.account.store.AccountFileStore;
import org.iota.jota.error.ArgumentException;

class GenerateCDA {
    public static void main(String[] args)
            throws ArgumentException, AccountError, ExecutionException, InterruptedException {

        // Connect to a node
        IotaAPI api = new IotaAPI.Builder()
            .protocol("https")
            .host("nodes.devnet.thetangle.org")
            .port(443)
            .build();
    
        // The seed that the account uses to generate CDAs and send bundles
        String mySeed = "PUETTSEITFEVEWCTBTSIZM9NKRGJEIMXTULBACGFRQK9IMGICLBKW9TTEVSDQMGWKBXPVCBMMCXWMNPDX";
        
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
    
        // Define the same time tomorrow
        Date timeoutAt = new Date(System.currentTimeMillis() + 24000 * 60 * 60);
    
    	// Generate the CDA
    	ConditionalDepositAddress cda = account.newDepositAddress(timeoutAt, true, 0).get();
    
        String magnet = (String) DepositFactory.get().build(cda, MagnetMethod.class);
        
        System.out.println(magnet);
        
        // Close the database and stop any ongoing reattachments
        account.shutdown();
    }
}

