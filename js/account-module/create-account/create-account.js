const { createAccount }  = require('@iota/account');
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
const timeSource = () => util.promisify(ntpClient.getNetworkTime)("time.google.com", 123);

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

// Get the accounts total available balance
account.getAvailableBalance()
.then(balance => {
    console.log(`Your balance is: ${balance}`);
})
.catch(error => {
    console.log(error);
    // Close the database and stop any ongoing reattachments
    account.stop();
});

