const { createAccount }  = require('@iota/account');
const TransactionConverter  = require('@iota/transaction-converter');

// The seed that the account uses to generate CDAs and send bundles
const seed = 'PUEOTSEITFEVEWCWBTSIZM9NKRGJEIMXTULBACGFRQK9IMGICLBKW9TTEVSDQMGWKBXPVCBMMCXWMNPDX';

// The node to connect to
const provider = 'https://nodes.devnet.iota.org:443';

// How far back in the Tangle to start the tip selection
const depth = 3;

// The minimum weight magnitude for the Devnet
const minWeightMagnitude = 9;

// How long to wait between each reattachment round
const delay = 1000 * 30;

// The depth at which transactions are no longer promotable
// and are automatically reattached
const maxDepth = 6;

// Create an account
const account = createAccount({
    seed,
    provider,
    depth,
    minWeightMagnitude,
    delay,
    maxDepth,
    timeSource
});

account.getAvailableBalance()
    .then(balance => {
            // Generate a new CDA that expects the total available balance of the account
            account.generateCDA({
                timeoutAt: Date.now() + 24 * 60 * 60 * 1000,
                expectedAmount: balance
            }).then(cda => {
                // Send the bundle
                account.sendToCDA({
                ...cda,
                value: balance
            })
            .then(trytes => {
                
                // Get the tail transaction and convert it to an object
                let bundle = TransactionConverter.asTransactionObject(trytes[trytes.length - 1]);
                let tailTransaction = bundle.hash;
                let address = bundle.address
                let value = bundle.value;
                console.log(`Sent ${value} IOTA tokens to ${address} in
                the bundle with the following tail transaction hash:  ${tailTransaction}`);
            })
        })
    }).catch(error => {
    console.log(error);
    // Close the database and stop any ongoing reattachments
    account.stop();
});

