# Sample code for the account module in Java

This repository contains the sample code that we use on the [IOTA documentation portal](https://docs.iota.org/docs/client-libraries/0.1/account-module/js/get-started) to help you get started with the Java account module.

## Prerequisites

To get started you need [Git](https://git-scm.com/book/en/v2/Getting-Started-Installing-Git) installed on your device.

You also need a Java development environment that uses the [Maven](https://maven.apache.org/download.cgi) build tool. If this is your first time using the Java client library, complete our [getting started guide](https://docs.iota.org/docs/client-libraries/0.1/getting-started/java-quickstart), and follow the instructions for installing the library with Maven.

## Getting started

To start playing with these examples run the following commands:

--------------------
### Linux and macOS
```bash
git clone https://github.com/JakeSCahill/iota-samples.git
cd iota-samples/java/account-module
mvn clean install
mvn exec:java -Dexec.mainClass="com.iota.CreateAccount"
```
---
### Windows
```bash
git clone https://github.com/JakeSCahill/iota-samples.git
cd iota-samples/java/account-module
mvn clean install
mvn exec:java -D"exec.mainClass"="com.iota.CreateAccount"
```
--------------------

You should see the balance of your new account.

You'll also have a JSON file that keeps track of your seed state.