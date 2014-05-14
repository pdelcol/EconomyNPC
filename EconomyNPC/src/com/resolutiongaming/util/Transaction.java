package com.resolutiongaming.util;

public class Transaction {
	public String senderName, receiverName;
	public int numTokens, transactionID;
	public boolean inProgress, cancelled;
	public Transaction(String senderName, String receiverName, int numTokens, boolean inProgress,int transactionID)
	{
		this.senderName = senderName;
		this.receiverName = receiverName;
		this.numTokens = numTokens;
		this.inProgress = inProgress;
		this.transactionID = transactionID;
	}
}
