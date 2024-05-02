package com.monsterexamine;

import net.runelite.client.RuneLite;
import net.runelite.client.externalplugins.ExternalPluginManager;

public class MonsterExaminePluginTest
{
	public static void main(String[] args) throws Exception
	{
		ExternalPluginManager.loadBuiltin(MonsterExaminePlugin.class);
		RuneLite.main(args);
	}
}