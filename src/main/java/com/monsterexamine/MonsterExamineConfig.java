package com.monsterexamine;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;

@ConfigGroup("Monster Examine")
public interface MonsterExamineConfig extends Config
{
	@ConfigItem(
		keyName = "Monster Examine",
		name = "Monster Examine",
		description = "Show Monster's Weaknesses"
	)
	default boolean monsterExamine()
	{
		return false;
	}
}
