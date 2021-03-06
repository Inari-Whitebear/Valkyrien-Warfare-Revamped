package valkyrienwarfare.capability;

public interface IAirshipCounterCapability {
	
	/**
	 * Returns the player's current airship count
	 *
	 * @return
	 */
	int getAirshipCount();
	
	/**
	 * Sets the player's airship count
	 */
	void setAirshipCount(int value);
	
	/**
	 * Returns the player's total airship count ever created
	 *
	 * @return
	 */
	int getAirshipCountEver();
	
	/**
	 * Sets the player's total airship ever created count
	 */
	void setAirshipCountEver(int value);
	
	/**
	 * Adds one to the player's airship count
	 */
	void onCreate();
	
	/**
	 * Removes one from the player's airship count
	 */
	void onLose();
}
