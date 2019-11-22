# Account module sample code in Go

This repository contains the sample code that we use on the [IOTA documentation portal](https://docs.iota.org) to help you get started with the Go account module.

## Prerequisites

To get started you need [Git](https://git-scm.com/book/en/v2/Getting-Started-Installing-Git) installed on your device.

If you don't have a Go development environment or if this is your first time using the Go client library, complete our [getting started guide]().

## Getting started

To start playing with these examples run the following commands:

```bash
git clone https://github.com/JakeSCahill/getting-started-go-accounts.git
cd getting-started-go-accounts
go mod download
go run create-account/create-account.go
```
You should see the balance of your new account.

You'll also have a database file that keeps track of your seed state.
