package com.iota;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import org.iota.jota.IotaAPI;
import org.iota.jota.IotaAccount;
import org.iota.jota.account.AccountStore;
import org.iota.jota.account.ExportedAccountState;
import org.iota.jota.account.store.AccountFileStore;
import org.iota.jota.error.ArgumentException;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

class ExportAccount {
    public static void main(String[] args) throws ArgumentException, IOException {
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

        // Create a file to which to save the exported seed state
        BufferedWriter writer = new BufferedWriter(new FileWriter("exported-seed-state-database.json"));

        // Export the seed state
        ExportedAccountState state = store.exportAccount(account.getId());

        ObjectMapper mapper = new ObjectMapper();
        try {

            // Serialize the seed state as JSON
            String json = mapper.writeValueAsString(state);
            System.out.println("ResultingJSONstring = " + json);

            // Write the seed state to the JSON file
            writer.write(json);
            writer.close();
        } catch (JsonProcessingException e) {
            e.printStackTrace();

            // Close the database and stop any ongoing reattachments
            account.shutdown();
        }
        
        mapper = new ObjectMapper();
        // Ignore new fields
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        
        try {

            FileReader readState = new FileReader("exported-seed-state-database.json");

            state = mapper.readValue(readState, ExportedAccountState.class);

            store.importAccount(state);

            System.out.println("Seed state imported");
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        
        System.out.println(account);
        // Close the database and stop any ongoing reattachments
        account.shutdown();
    }
}
