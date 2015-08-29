package coreMessageManager;

import java.util.ArrayList;
import java.util.List;

public class ListRegistration extends ArrayList<RegistrationObject>
{
	
	public IPumpMessage getObject(int id)
	{
		for(RegistrationObject obj : this)
		{
			if(obj.id == id)
				return obj.object;
		}
		
		return null;
	}
	
	public IPumpMessage getObject(Class idClass)
	{
		for(RegistrationObject obj : this)
		{
			if(obj.idClass == idClass)
				return obj.object;
		}
		
		return null;
	}

	
}
