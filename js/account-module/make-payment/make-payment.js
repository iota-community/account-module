const { createAccount }  = require('@iota/account');
const CDA = require('@iota/cda');
const TransactionConverter  = require('@iota/transaction-converter');
const ntpClient = require('ntp-client');
const util = require('util');

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

// Use the Google NTP servers as a reliable source of time to check CDA timeouts
const timeSource = () => util.promisify(ntpClient.getNetworkTime)("time.google.com");

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

// Send the bundle only if the CDA is active
if (isActive) {
    account.sendToCDA({
        ...cda,
        value: 1
    })
    .then((trytes) => {
        // Get the tail transaction and convert it to an object
        let bundle = TransactionConverter.asTransactionObject(trytes[trytes.length - 1]);
        let tailTransaction = bundle.hash;
        let address = bundle.address
        let value = bundle.value;
        console.log(`Sent ${value} IOTA tokens to ${address} in
        the bundle with the following tail transaction hash:  ${tailTransaction}`);
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
