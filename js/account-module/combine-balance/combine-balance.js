const { createAccount }  = require('@iota/account');
const TransactionConverter  = require('@iota/transaction-converter');
const ntpClient = require('ntp-client');

const seed = 'PUEOTSEITFEVEWCWBTSIZM9NKRGJEIMXTULBACGFRQK9IMGICLBKW9TTEVSDQMGWKBXPVCBMMCXWMNPDX';

// The node to connect to
const provider = 'https://nodes.devnet.iota.org:443';

// How far back in the Tangle to start the tip selection
const depth = 3;

// The minimum weight magnitude is 9 on the Devnet
const minWeightMagnitude = 9;

// How long to wait between each reattachment round
const delay = 1000 * 30;

// The depth at which transactions are no longer promotable
// and should be reattached
const maxDepth = 6;

// Create a new account
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
            account.generateCDA({
                timeoutAt: Date.now() + 24 * 60 * 60 * 1000,
                expectedAmount: balance
            }).then(cda => {
                account.sendToCDA({
                ...cda,
                value: balance
            })
            .then(trytes => {
                
                // Get the tail transaction and convert it to an object
                let bundle = TransactionConverter.asTransactionObject(trytes[trytes.length - 1]);
                let bundleHash = bundle.bundle;
                let address = bundle.address
                let value = bundle.value;
                console.log(`Sent ${value} IOTA tokens to ${address} in bundle:  ${bundleHash}`);
            })
        })
    }).catch(error => {
    console.log(error);
    // Close the database and stop any ongoing reattachments
    account.stop();
});

