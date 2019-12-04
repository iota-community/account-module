package com.iota;

import org.iota.jota.IotaAPI;
import org.iota.jota.IotaAccount;
import org.iota.jota.account.AccountStore;
import org.iota.jota.account.plugins.Plugin;
import org.iota.jota.account.store.AccountFileStore;
import org.iota.jota.error.ArgumentException;
import java.io.File;
import com.iota.TestPlugin;

class CreatePluginAccount {
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
    
    // Create a new instance of your plugin
    Plugin myPlugin = new TestPlugin();

    // Create an account
    IotaAccount account = new IotaAccount.Builder(mySeed)
    .store(store)
    .plugin(myPlugin)
    .api(api)
    .build();

    // Start the account and any plugins
    account.start();

    // Close the database and stop any ongoing reattachments
    account.shutdown();
    }
}

