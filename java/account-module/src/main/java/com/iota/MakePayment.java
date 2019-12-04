package com.iota;

import org.iota.jota.IotaAPI;
import org.iota.jota.IotaAccount;
import org.iota.jota.account.AccountStore;
import org.iota.jota.account.deposits.ConditionalDepositAddress;
import org.iota.jota.account.deposits.methods.DepositFactory;
import org.iota.jota.account.deposits.methods.MagnetMethod;
import org.iota.jota.account.store.AccountFileStore;
import org.iota.jota.error.ArgumentException;
import org.iota.jota.model.Bundle;
import java.io.File;
import java.util.Optional;
import java.util.concurrent.ExecutionException;

class MakePayment {
    public static void main(String[] args) throws ArgumentException, InterruptedException, ExecutionException {

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

    // Define the CDA to send the payment to
    String magnet = "iota://LQHETLMIBUEGAHMHMIQFBOMSSF9BF9FHOFWAFGDLND9CMBDXOPLBDPMXHBEGZSFFDYVHSXEXCBYVDXKQYXOWVOCRYB/?timeout_at=1575452386589&multi_use=true&expected_amount=0";

    ConditionalDepositAddress cda = DepositFactory.get().parse(magnet, MagnetMethod.class);

    // Send the bundle
    Bundle bundle = account.send(
        cda.getDepositAddress().getHashCheckSum(), 
        cda.getRequest().getExpectedAmount(), 
        Optional.of("Thanks for the pizza"),
        Optional.of("ACCOUNTMODULETEST")).get();

    System.out.printf("Sent deposit to %s in the bundle with the following tail transaction hash %s\n",
    bundle.getTransactions().get(bundle.getLength() - 1).getAddress(), bundle.getTransactions().get(bundle.getLength() - 1).getHash());
    
    // Close the database and stop any ongoing reattachments
    account.shutdown();
    }
}

