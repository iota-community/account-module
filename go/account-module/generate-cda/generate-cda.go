package main

import (
	"fmt"
	"github.com/iotaledger/iota.go/account/builder"
	"github.com/iotaledger/iota.go/account/deposit"
	"github.com/iotaledger/iota.go/account/store/badger"
	"github.com/iotaledger/iota.go/account/timesrc"
	"github.com/iotaledger/iota.go/api"
	"github.com/iotaledger/iota.go/trinary"
	"time"
)

// The seed that the account uses to generate CDAs and send bundles
var seed = trinary.Trytes("PUETTSEITFEVEWCWBTSIZM9NKRGJEIMXTULBACGFRQK9IMGICLBKW9TTEVSDQMGWKBXPVCBMMCXWMNPDX")

func main() {
	// Define the node to connect to
	apiSettings := api.HTTPClientSettings{URI: "https://nodes.devnet.iota.org:443"}

	iotaAPI, err := api.ComposeAPI(apiSettings)
	handleErr(err)

	// Define a database in which to store the seed state
	store, err := badger.NewBadgerStore("seed-state-database")
	handleErr(err)

	// Make sure the database closes when the code stops
	defer store.Close()

	// Use the Google NTP servers as a reliable source of time to check CDA timeouts
	timesource := timesrc.NewNTPTimeSource("time.google.com")

	account, err := builder.NewBuilder().
		// Connect to a node
		WithAPI(iotaAPI).
		// Create the database
		WithStore(store).
		WithSeed(seed).
		// Set the minimum weight magnitude for the Devnet
		WithMWM(9).
		WithTimeSource(timesource).
		// Load the default plugins that enhance the functionality of the account
		WithDefaultPlugins().
		Build()
	handleErr(err)

	handleErr(account.Start())

	// Make sure the account shuts down when the code stops
	defer account.Shutdown()

	// Get the current time
	now, err := timesource.Time()
	handleErr(err)

	// Define the same time tomorrow
	now = now.Add(time.Duration(24) * time.Hour)

	// Specify the conditions
	conditions := &deposit.Conditions{TimeoutAt: &now, MultiUse: true}

	// Generate the CDA
	cda, err := account.AllocateDepositAddress(conditions)
	handleErr(err)

	fmt.Println(cda.AsMagnetLink())

}

func handleErr(err error) {
	if err != nil {
		panic(err)
	}
}
