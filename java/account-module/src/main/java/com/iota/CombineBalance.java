package com.iota;

import org.iota.jota.IotaAPI;
import org.iota.jota.IotaAccount;
import org.iota.jota.account.AccountStore;
import org.iota.jota.account.deposits.ConditionalDepositAddress;
import org.iota.jota.account.errors.AccountError;
import org.iota.jota.account.store.AccountFileStore;
import org.iota.jota.error.ArgumentException;
import org.iota.jota.model.Bundle;

import java.io.File;
import java.util.Date;
import java.util.Optional;
import java.util.concurrent.ExecutionException;

class CombineBalance {
    public static void main(String[] args)
            throws ArgumentException, AccountError, ExecutionException, InterruptedException {

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
    
    // Create an account
    IotaAccount account = new IotaAccount.Builder(mySeed)
    .store(store)
    .api(api)
    .build();

    // Start the account and any plugins
    account.start();

    // Define the same time tomorrow
    Date timeoutAt = new Date(System.currentTimeMillis() + 24000 * 60 * 60);

	// Generate the CDA that expects the total balance of your account
    ConditionalDepositAddress cda = account.newDepositAddress(timeoutAt, true, account.availableBalance()).get();

    // Send the bundle
    Bundle bundle = account.send(
        cda.getDepositAddress().getHashCheckSum(), 
        cda.getRequest().getExpectedAmount(), 
        Optional.of("Sweep of all addresses"),
        Optional.of("IOTA9SWEEP")).get();

    System.out.printf("Sent deposit to %s in the bundle with the following tail transaction hash %s\n",
    bundle.getTransactions().get(bundle.getLength() - 1).getAddress(), bundle.getTransactions().get(bundle.getLength() - 1).getHash());
    
    account.shutdown();
    }
}