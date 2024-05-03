package com.monsterexamine;

import com.google.inject.Provides;
import javax.inject.Inject;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

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
import org.jsoup.Jsoup;

import java.awt.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

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
		//retrieve stats
		String monsterStats = getMonsterStats(monsterName);

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

		// update panel with individual stats
		SwingUtilities.invokeLater(() -> panel.updateMonsterInfo(monsterName, hitpoints, attack, strength, defence, magic, ranged,
				attackBonus, strengthBonus, magicAccuracy, magicStrength, rangedAccuracy, rangedStrength,
				stabDefence, slashDefence, crushDefence, magicDefence, rangedDefence));
	}

	//
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

	private String getMonsterStats(String monsterName)
	{
		try {
			// the wiki URL for the monster
			String wikiUrl = "https://oldschool.runescape.wiki/w/" + monsterName.replace(" ", "_");

			// HTTP request to retrieve the HTML content
			URL url = new URL(wikiUrl);
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();
			connection.setRequestMethod("GET");

			// response content
			BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
			StringBuilder html = new StringBuilder();
			String line;
			while ((line = reader.readLine()) != null)
			{
				html.append(line);
			}
			reader.close();

			// Parse the HTML content using jsoup... all NPCs have the infobox-monster table so look for that.
			org.jsoup.nodes.Document document = Jsoup.parse(html.toString());
			org.jsoup.nodes.Element infoboxElement = document.selectFirst("table.infobox-monster");

			if (infoboxElement != null)
			{
				// check if the "data-attr-param" attribute exists in the HTML. If not we will have to use default params
				// Some monsters (typically older mobs from when the game was first released) don't have the attr-params tag.
				boolean hasDataAttrParam = infoboxElement.select("td[data-attr-param]").size() > 0;

				// Combat stats
				String hitpoints = hasDataAttrParam ? infoboxElement.selectFirst("td[data-attr-param=hitpoints]").text() : infoboxElement.select("td.infobox-nested").get(0).text();
				String attack = hasDataAttrParam ? infoboxElement.selectFirst("td[data-attr-param=att]").text() : infoboxElement.select("td.infobox-nested").get(1).text();
				String strength = hasDataAttrParam ? infoboxElement.selectFirst("td[data-attr-param=str]").text() : infoboxElement.select("td.infobox-nested").get(2).text();
				String defence = hasDataAttrParam ? infoboxElement.selectFirst("td[data-attr-param=def]").text() : infoboxElement.select("td.infobox-nested").get(3).text();
				String magic = hasDataAttrParam ? infoboxElement.selectFirst("td[data-attr-param=mage]").text() : infoboxElement.select("td.infobox-nested").get(4).text();
				String ranged = hasDataAttrParam ? infoboxElement.selectFirst("td[data-attr-param=range]").text() : infoboxElement.select("td.infobox-nested").get(5).text();

				// Aggressive stats
				String attackBonus = hasDataAttrParam ? infoboxElement.selectFirst("td[data-attr-param=attbns]").text() : infoboxElement.select("td.infobox-nested").get(6).text();
				String strengthBonus = hasDataAttrParam ? infoboxElement.selectFirst("td[data-attr-param=strbns]").text() : infoboxElement.select("td.infobox-nested").get(7).text();
				String magicAccuracy = hasDataAttrParam ? infoboxElement.selectFirst("td[data-attr-param=amagic]").text() : infoboxElement.select("td.infobox-nested").get(8).text();
				String magicStrength = hasDataAttrParam ? infoboxElement.selectFirst("td[data-attr-param=mbns]").text() : infoboxElement.select("td.infobox-nested").get(9).text();
				String rangedAccuracy = hasDataAttrParam ? infoboxElement.selectFirst("td[data-attr-param=arange]").text() : infoboxElement.select("td.infobox-nested").get(10).text();
				String rangedStrength = hasDataAttrParam ? infoboxElement.selectFirst("td[data-attr-param=rngbns]").text() : infoboxElement.select("td.infobox-nested").get(11).text();

				// Defensive stats
				String stabDefence = hasDataAttrParam ? infoboxElement.selectFirst("td[data-attr-param=dstab]").text() : infoboxElement.select("td.infobox-nested").get(12).text();
				String slashDefence = hasDataAttrParam ? infoboxElement.selectFirst("td[data-attr-param=dslash]").text() : infoboxElement.select("td.infobox-nested").get(13).text();
				String crushDefence = hasDataAttrParam ? infoboxElement.selectFirst("td[data-attr-param=dcrush]").text() : infoboxElement.select("td.infobox-nested").get(14).text();
				String magicDefence = hasDataAttrParam ? infoboxElement.selectFirst("td[data-attr-param=dmagic]").text() : infoboxElement.select("td.infobox-nested").get(15).text();
				String rangedDefence = hasDataAttrParam ? infoboxElement.selectFirst("td[data-attr-param=drange]").text() : infoboxElement.select("td.infobox-nested").get(16).text();

				// Combine the extracted stats into a formatted string
				return "Combat Stats:\n" +
						"Hitpoints: " + hitpoints + "\n" +
						"Attack: " + attack + "\n" +
						"Strength: " + strength + "\n" +
						"Defence: " + defence + "\n" +
						"Magic: " + magic + "\n" +
						"Ranged: " + ranged + "\n\n" +
						"Aggressive Stats:\n" +
						"Attack Bonus: " + attackBonus + "\n" +
						"Strength Bonus: " + strengthBonus + "\n" +
						"Magic Accuracy: " + magicAccuracy + "\n" +
						"Magic Strength: " + magicStrength + "\n" +
						"Ranged Accuracy: " + rangedAccuracy + "\n" +
						"Ranged Strength: " + rangedStrength + "\n\n" +
						"Defensive Stats:\n" +
						"Stab Defence: " + stabDefence + "\n" +
						"Slash Defence: " + slashDefence + "\n" +
						"Crush Defence: " + crushDefence + "\n" +
						"Magic Defence: " + magicDefence + "\n" +
						"Ranged Defence: " + rangedDefence;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

		return "Failed to retrieve monster stats";
	}
}
