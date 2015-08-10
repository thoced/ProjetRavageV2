package coreNet;

import java.io.Serializable;


public class NetBase implements Serializable
{
	public static enum TYPE {HELLO,CREATE,UPDATE,DELETE};
	// type de message
	protected TYPE typeMessage;
	
	public TYPE getTypeMessage() {
		return typeMessage;
	}
	public void setTypeMessage(TYPE typeMessage) {
		this.typeMessage = typeMessage;
	} 
	
	
}
