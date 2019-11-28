const { createAccount }  = require('@iota/account');
const CDA = require('@iota/cda');
const TransactionConverter  = require('@iota/transaction-converter');
const ntpClient = require('ntp-client');

const seed = 'PUEOTSEITFEVEWCWBTSIZM9NKRGJEIMXTULBACGFRQK9IMGICLBKW9TTEVSDQMGWKBXPVCBMMCXWMNPDX';

// The node to connect to
const provider = 'https://nodes.devnet.iota.org:443';

// How far back in the Tangle to start the tip selection
const depth = 3;

// The minimum weight magnitude is 9 on the Devnet
const minWeightMagnitude = 9;

// How long to wait before the next attachment round
const delay = 1000 * 30;

// The depth at which transactions are no longer promotable
// Those transactions are automatically re-attached
const maxDepth = 6;

const timeSource = ntpClient.getNetworkTime("time.google.com");

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

// Start the plugins
account.start();

// Define the CDA to send the payment to
const magnetLink = "iota://BWNYWGULIIAVRYOOFWZTSDFXFPRCFF9YEHGVBOORLGCPCJSKTHU9OKESUGZGWZXZZDLESFPPTGEHVKTTXG9BQLSIGP/?timeout_at=5174418337&multi_use=1&expected_amount=0";

const cda = CDA.parseCDAMagnet(
    magnetLink
);

let isActive = true;

// Get the current time to use to compare to the CDA's timeout
ntpClient.getNetworkTime("time.google.com", 123, function(err, date) {
    if(err) {
        console.error(err);
        return;
    } else if (!(CDA.isAlive(date, cda))) {
        isActive = false
    }
});

// Send the payment only if the CDA is active
if (isActive) {
    account.sendToCDA({
        ...cda,
        value: 1000
    })
    .then((trytes) => {
        // Get the tail transaction and convert it to an object
        let bundle = TransactionConverter.asTransactionObject(trytes[trytes.length - 1]);
        let bundleHash = bundle.bundle;
        let address = bundle.address
        let value = bundle.value;
        console.log(`Sent ${value} IOTA tokens to ${address} in bundle:  ${bundleHash}`);
    })
    .catch(error => {
        console.log(error);
        // Close the database and stop any ongoing reattachments
        account.stop();
    });

} else {
    console.log('CDA is expired. Use an active CDA.');
    // Close the database and stop any ongoing reattachments
    account.stop();
    return;
}