package zmaster587.libVulpes.inventory.modules;

public interface IProgressBar {
	
	/**
	 * @param id id of the progress bar 
	 * @return progress from 0 to 1
	 */
	float getNormallizedProgress(int id);
	
	/**
	 * Called on the client to sync information with the server
	 * @param id id of the progress bar to update
	 * @param progress progress data received from the server
	 */
	void setProgress(int id, int progress);
	
	/**
	 * @param id id of the progress bar
	 * @return amount much progress
	 */
	int getProgress(int id);
	
	/**
	 * Gets the total progress, usually compared with getProgress to determine percent complete
	 * @param id id of the progress bar
	 * @return maximum amount of progress for this object
	 */
	int getTotalProgress(int id);
	
	/**
	 * Called on the client to sync total progress with the server
	 * @param id id of the progress bar to sync
	 * @param progress progress received from the server
	 */
	void setTotalProgress(int id, int progress);
}
