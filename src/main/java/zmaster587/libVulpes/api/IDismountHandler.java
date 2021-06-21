package zmaster587.libVulpes.api;

import net.minecraft.entity.Entity;

public interface IDismountHandler {
	/**
	 * Called when a player entity attempts to dismount from this entity
	 * @param entity
	 */
	void handleDismount(Entity entity);
}
