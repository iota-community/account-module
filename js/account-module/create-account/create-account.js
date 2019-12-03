const { createAccount }  = require('@iota/account');
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

account.getAvailableBalance()
.then(balance => {
    console.log(`Your balance is: ${balance}`);
})
.catch(error => {
    console.log(error);
    // Close the database and stop any ongoing reattachments
    account.stop();
});
