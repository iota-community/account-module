package main

import (
	"fmt"
	"github.com/iotaledger/iota.go/account/builder"
	"github.com/iotaledger/iota.go/account/store/badger"
	"github.com/iotaledger/iota.go/account/timesrc"
	"github.com/iotaledger/iota.go/api"
	"github.com/iotaledger/iota.go/trinary"
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
		// Connect to the database
		WithStore(store).
		// Load the seed
		WithSeed(seed).
		// Set the minimum weight magnitude for the Devnet (default is 14)
		WithMWM(9).
		// Use a reliable time source
		WithTimeSource(timesource).
		// Load the default plugins that enhance the functionality of the account
		WithDefaultPlugins().
		Build()
	handleErr(err)

	handleErr(account.Start())

	// Make sure the account shuts down when the code stops
	defer account.Shutdown()

	balance, err := account.AvailableBalance()
	handleErr(err)
	fmt.Println("Total available balance: ")
	fmt.Println(balance)
}

func handleErr(err error) {
	if err != nil {
		panic(err)
	}
}
