package zmaster587.libVulpes.inventory.modules;

public interface IDataSync {
	/**
	 * Called when an update is received from the server
	 * @param id assigned id of the data module
	 * @param value value recieved from the server
	 */
	void setData(int id, int value);
	
	/**
	 * 
	 * @param id id of the module sync
	 * @return the data associated with a moduleSync with the passed id
	 */
	int getData(int id);
}
