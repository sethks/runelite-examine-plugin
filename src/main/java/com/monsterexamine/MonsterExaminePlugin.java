package com.monsterexamine;

import com.google.inject.Provides;
import javax.inject.Inject;
import javax.swing.*;

import net.runelite.api.events.MenuEntryAdded;
import net.runelite.client.ui.ClientToolbar;
import net.runelite.client.ui.NavigationButton;
import net.runelite.client.util.ImageUtil;

import lombok.extern.slf4j.Slf4j;
import net.runelite.api.*;
import net.runelite.api.events.MenuOptionClicked;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Slf4j
@PluginDescriptor(
	name = "Monster Examine"
)
public class MonsterExaminePlugin extends Plugin
{
	@Inject
	private Client client;
	@Inject
	private MonsterExamineConfig config;
	@Inject
	private ClientToolbar clientToolbar;
	private MonsterExaminePanel panel;
	private NavigationButton navButton;

	@Provides
	MonsterExamineConfig provideConfig(ConfigManager configManager) {
		return configManager.getConfig(MonsterExamineConfig.class);
	}
	@Override
	protected void startUp() throws Exception
	{
		panel = injector.getInstance(MonsterExaminePanel.class);

		navButton = NavigationButton.builder()
				.tooltip("Monster Examine")
				.icon(ImageUtil.loadImageResource(getClass(), "/icon.png"))
				.priority(7)
				.panel(panel)
				.build();

		clientToolbar.addNavigation(navButton);
	}

	@Override
	protected void shutDown() throws Exception
	{
		clientToolbar.removeNavigation(navButton);
	}

	@Subscribe
	public void onMenuEntryAdded(MenuEntryAdded event)
	{
		if (event.getType() == MenuAction.EXAMINE_NPC.getId())
		{
			MenuEntry[] menuEntries = client.getMenuEntries();
			MenuEntry examineEntry = event.getMenuEntry();

			client.createMenuEntry(menuEntries.length)
					.setOption("Examine Stats")
					.setTarget(examineEntry.getTarget())
					.setType(MenuAction.of(MenuAction.RUNELITE.getId()))
					.setIdentifier(event.getIdentifier())
					.setParam0(event.getActionParam0())
					.setParam1(event.getActionParam1())
					.onClick(e -> {
						NPC npc = examineEntry.getNpc();
						if (npc != null)
						{
							String monsterName = npc.getName();
							examineMonster(monsterName);
						}
					});
		}
	}

	@Subscribe
	public void onMenuOptionClicked(MenuOptionClicked event)
	{
		if (event.getMenuOption().equals("Examine Stats"))
		{
			NPC npc = event.getMenuEntry().getNpc();
			if (npc != null)
			{
				String monsterName = npc.getName();
				examineMonster(monsterName);
			}
		}
	}

	public void examineMonster(String monsterName)
	{
		new Thread(() -> {
			getMonsterStats(monsterName);
		}).start();
	}

	private int extractStat(String monsterStats, String statName)
	{
		int index = monsterStats.indexOf(statName);
		if (index != -1)
		{
			int startIndex = index + statName.length();
			int endIndex = monsterStats.indexOf("\n", startIndex);
			if (endIndex == -1)
			{
				endIndex = monsterStats.length();
			}
			String statValue = monsterStats.substring(startIndex, endIndex).trim();
			statValue = statValue.replace(",", ""); // remove comma separators from stats in the thousands
			return Integer.parseInt(statValue);
		}
		return 0;
	}

	private void getMonsterStats(String monsterName)
	{
		new Thread(() -> {
			try
			{
				// Construct the URL for Lambda function
				String lambdaFunctionUrl = "https://6hohf76c6qaiobp73x6ylxpi5a0dlcfn.lambda-url.us-east-1.on.aws/";
				String encodedMonsterName = URLEncoder.encode(monsterName, StandardCharsets.UTF_8);
				URL url = new URL(lambdaFunctionUrl + "?name=" + encodedMonsterName);
				HttpURLConnection connection = (HttpURLConnection) url.openConnection();
				connection.setRequestMethod("GET");

				int responseCode = connection.getResponseCode();
				if (responseCode == HttpURLConnection.HTTP_OK)
				{
					BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
					StringBuilder response = new StringBuilder();
					String line;
					while ((line = reader.readLine()) != null)
					{
						response.append(line);
					}
					reader.close();

					// Parse the response and extract the monster stats
					String monsterStats = extractMonsterStatsFromResponse(response.toString());

					// Update the panel on the main thread
					SwingUtilities.invokeLater(() -> {
						//parse individual stats from monsterStats string
						int hitpoints = extractStat(monsterStats, "Hitpoints: ");
						int attack = extractStat(monsterStats, "Attack: ");
						int strength = extractStat(monsterStats, "Strength: ");
						int defence = extractStat(monsterStats, "Defence: ");
						int magic = extractStat(monsterStats, "Magic: ");
						int ranged = extractStat(monsterStats, "Ranged: ");
						int attackBonus = extractStat(monsterStats, "Attack Bonus: ");
						int strengthBonus = extractStat(monsterStats, "Strength Bonus: ");
						int magicAccuracy = extractStat(monsterStats, "Magic Accuracy: ");
						int magicStrength = extractStat(monsterStats, "Magic Strength: ");
						int rangedAccuracy = extractStat(monsterStats, "Ranged Accuracy: ");
						int rangedStrength = extractStat(monsterStats, "Ranged Strength: ");
						int stabDefence = extractStat(monsterStats, "Stab Defence: ");
						int slashDefence = extractStat(monsterStats, "Slash Defence: ");
						int crushDefence = extractStat(monsterStats, "Crush Defence: ");
						int magicDefence = extractStat(monsterStats, "Magic Defence: ");
						int rangedDefence = extractStat(monsterStats, "Ranged Defence: ");

						panel.updateMonsterInfo(monsterName, hitpoints, attack, strength, defence, magic, ranged,
								attackBonus, strengthBonus, magicAccuracy, magicStrength, rangedAccuracy, rangedStrength,
								stabDefence, slashDefence, crushDefence, magicDefence, rangedDefence);
					});
				}
				else
				{
					System.err.println("Failed to retrieve monster stats. Response code: " + responseCode);
				}
			}
			catch (IOException e)
			{
				System.err.println("Error occurred while retrieving monster stats: " + e.getMessage());
			}
		}).start();
	}

	private String extractMonsterStatsFromResponse(String response)
	{
		// Split the response string into individual stats
		String[] statArray = response.split("(?<=\\d)(?=[A-Z])");

		return String.join("\n", statArray);
	}
}
