package com.iota;

import org.iota.jota.IotaAPI;
import org.iota.jota.IotaAccount;
import org.iota.jota.account.AccountStore;
import org.iota.jota.account.store.AccountFileStore;
import org.iota.jota.error.ArgumentException;
import static java.lang.Math.toIntExact;
import java.io.File;

class CreateAccount {
public static void main(String[] args) throws ArgumentException {

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

    long balance = account.availableBalance();

    System.out.print("Your balance is: " + toIntExact(balance));

    account.shutdown();
    }
}

