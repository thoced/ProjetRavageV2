package coreNet;

import coreEntity.UnityBaseModel;

public class NetDataUnity extends NetBase 
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
