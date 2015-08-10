package coreNet;

import java.io.Serializable;

import coreEntity.UnityBaseModel;
import coreNet.NetHeader.TYPE;

public class NetDataUnity extends NetBase implements Serializable
{
	private UnityBaseModel model;
	
	public UnityBaseModel getModel() {
		return model;
	}

	public void setModel(UnityBaseModel model) {
		this.model = model;
	}

	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return model.toString();
	}

	
}
