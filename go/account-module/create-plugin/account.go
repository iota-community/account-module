package main

import (
	"fmt"
	"github.com/iotaledger/iota.go/account/builder"
	"github.com/iotaledger/iota.go/account/event"
	"github.com/iotaledger/iota.go/account/store/badger"
	"github.com/iotaledger/iota.go/account/timesrc"
	"github.com/iotaledger/iota.go/api"
	"github.com/iotaledger/iota.go/trinary"
)

// You should never hard-code a seed
var seed = trinary.Trytes("PUETTSEITFEVEWCWBTSIZM9NKRGJEIMXTULBACGFRQK9IMGICLBKW9TTEVSDQMGWKBXPVCBMMCXWMNPDX")

func main() {
	// Define the node to connect to
	apiSettings := api.HTTPClientSettings{URI: "https://nodes.devnet.iota.org:443"}

	iotaAPI, err := api.ComposeAPI(apiSettings)
	handleErr(err)

	store, err := badger.NewBadgerStore("seed-state-database")
	handleErr(err)

	// Make sure the database closes when the code stops
	defer store.Close()

	timesource := timesrc.NewNTPTimeSource("time.google.com")

	nodeInfo, err := iotaAPI.GetNodeInfo()
	handleErr(err)
	fmt.Println(nodeInfo.AppName)

	em := event.NewEventMachine()

	account, err := builder.NewBuilder().
		// Connect to a node
		WithAPI(iotaAPI).
		// Create the database
		WithStore(store).
		WithSeed(seed).
		// Set the minimum weight magnitude for the Devnet
		WithMWM(9).
		WithTimeSource(timesource).
		WithEvents(em).
		// Load the default plugins that enhance the functionality of the account
		WithDefaultPlugins().
		Build(NewLogPlugin(em))
	handleErr(err)

	handleErr(account.Start())

	// Make sure the account shuts down when the code stops
	defer account.Shutdown()
}

func handleErr(err error) {
	if err != nil {
		panic(err)
	}
}
