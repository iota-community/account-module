package main

import (
	"fmt"
	"github.com/iotaledger/iota.go/account"
	"github.com/iotaledger/iota.go/account/event"
	"github.com/iotaledger/iota.go/account/event/listener"
)

// NewLogPlugin ...
func NewLogPlugin(em event.EventMachine) account.Plugin {
	fmt.Printf("Starting event logger")
	return &logplugin{em: em, exit: make(chan struct{})}
}

type logplugin struct {
	em   event.EventMachine
	acc  account.Account
	exit chan struct{}
}

func (l *logplugin) Name() string {
	return "logger"
}

func (l *logplugin) Start(acc account.Account) error {
	l.acc = acc
	l.log()
	return nil
}

func (l *logplugin) Shutdown() error {
	l.exit <- struct{}{}
	return nil
}

func (l *logplugin) log() {
	lis := listener.NewChannelEventListener(l.em).All()

	go func() {
		defer lis.Close()
	exit:
		for {
			select {
			case ev := <-lis.Promoted:
				fmt.Printf("(event) promoted %s with %s\n", ev.BundleHash[:10], ev.PromotionTailTxHash)
			case ev := <-lis.Reattached:
				fmt.Printf("(event) reattached %s with %s\n", ev.BundleHash[:10], ev.ReattachmentTailTxHash)
			case ev := <-lis.SentTransfer:
				tail := ev[0]
				fmt.Printf("(event) sent %s with tail %s\n", tail.Bundle[:10], tail.Hash)
			case ev := <-lis.TransferConfirmed:
				tail := ev[0]
				fmt.Printf("(event) transfer confirmed %s with tail %s\n", tail.Bundle[:10], tail.Hash)
			case ev := <-lis.ReceivingDeposit:
				tail := ev[0]
				fmt.Printf("(event) receiving deposit %s with tail %s\n", tail.Bundle[:10], tail.Hash)
			case ev := <-lis.ReceivedDeposit:
				tail := ev[0]
				fmt.Printf("(event) received deposit %s with tail %s\n", tail.Bundle[:10], tail.Hash)
			case ev := <-lis.ReceivedMessage:
				tail := ev[0]
				fmt.Printf("(event) received msg %s with tail %s\n", tail.Bundle[:10], tail.Hash)
			case balanceCheck := <-lis.ExecutingInputSelection:
				fmt.Printf("(event) executing input selection (balance check: %v) \n", balanceCheck)
			case <-lis.PreparingTransfers:
				fmt.Printf("(event) preparing transfers\n")
			case <-lis.GettingTransactionsToApprove:
				fmt.Printf("(event) getting transactions to approve\n")
			case <-lis.AttachingToTangle:
				fmt.Printf("(event) executing proof of work\n")
			case err := <-lis.InternalError:
				fmt.Printf("received internal error: %s\n", err.Error())
			case <-l.exit:
				break exit
			}
		}
	}()
}
