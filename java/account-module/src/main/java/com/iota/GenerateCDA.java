package com.iota;

import org.iota.jota.IotaAPI;
import org.iota.jota.IotaAccount;
import org.iota.jota.account.AccountStore;
import org.iota.jota.account.deposits.ConditionalDepositAddress;
import org.iota.jota.account.deposits.methods.DepositFactory;
import org.iota.jota.account.deposits.methods.MagnetMethod;
import org.iota.jota.account.errors.AccountError;
import org.iota.jota.account.store.AccountFileStore;
import org.iota.jota.error.ArgumentException;
import java.io.File;
import java.util.Date;
import java.util.concurrent.ExecutionException;

class GenerateCDA {
    public static void main(String[] args)
            throws ArgumentException, AccountError, ExecutionException, InterruptedException {

    // Connect to a node
    IotaAPI api = new IotaAPI.Builder()
        .protocol("https")
        .host("nodes.devnet.thetangle.org")
        .port(443)
        .build();

    String mySeed = "PUEOTSEITFEVEWCWBTSIZM9NKRGJEIMXTULBACGFRQK9IMGICLBKW9TTEVSDQMGWKBXPVCBMMCXWMNPDX";
    File file = new File("seed-state-database.json");
    AccountStore store = new AccountFileStore(file);

    IotaAccount account = new IotaAccount.Builder(mySeed)
    .store(store)
    .api(api)
    .build();

    account.start();

    // Define the same time tomorrow
    Date timeoutAt = new Date(System.currentTimeMillis() + 24000 * 60 * 60);

	// Generate the CDA
	ConditionalDepositAddress cda = account.newDepositAddress(timeoutAt, false,0).get();

    String magnet = (String) DepositFactory.get().build(cda, MagnetMethod.class);
    
    System.out.println(magnet);
    
    account.shutdown();
    }
}

