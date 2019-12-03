package com.iota;

import org.iota.jota.account.plugins.AccountPlugin;
import org.iota.jota.account.event.events.EventTransferConfirmed;
import org.iota.jota.account.event.events.EventPromotion;
import org.iota.jota.account.event.AccountEvent;

public class TestPlugin extends AccountPlugin {

	@Override
	public void load() throws Exception {
		// Load data that the plugin needs such as reading a file, generating memory intensive resources, etc..
	}

	@Override
	public boolean start() {
		// Start any processes that you want to run continuously

		// Return true if all went well, otherwise return false
		return true;
	}

	@Override
	public void shutdown() {
		// Stop any running processes here
	}

	@Override
	public String name() {
		return "AwesomeTestPlugin";
	}

	@AccountEvent
	public void confirmed(EventTransferConfirmed e) {
	    System.out.println("account: " + this.getAccount().getId());
	    System.out.println("confimed: " + e.getBundle().getBundleHash());
	}

	@AccountEvent
	public void promoted(EventPromotion e) {
	    System.out.println("account: " + this.getAccount().getId());
	    System.out.println("promoted: " + e.getPromotedBundle());
	}
}