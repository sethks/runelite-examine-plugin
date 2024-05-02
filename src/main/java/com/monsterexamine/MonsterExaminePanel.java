package com.monsterexamine;

import lombok.Getter;
import net.runelite.client.ui.ColorScheme;
import net.runelite.client.ui.FontManager;
import net.runelite.client.ui.PluginPanel;
import javax.inject.Inject;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;

public class MonsterExaminePanel extends PluginPanel
{
    private static final int ICON_SIZE = 16;
    private final MonsterExaminePlugin plugin;
    private final JLabel monsterNameLabel;
    private final JPanel combatStatsPanel;
    private final JPanel aggressiveStatsPanel;
    private final JPanel defensiveStatsPanel;

    @Inject
    MonsterExaminePanel(MonsterExaminePlugin plugin)
    {
        this.plugin = plugin;
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBackground(ColorScheme.DARKER_GRAY_COLOR);
        setPreferredSize(new Dimension(200, 400));

        JPanel titlePanel = new JPanel();
        titlePanel.setBackground(ColorScheme.DARK_GRAY_COLOR);
        titlePanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        titlePanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));

        JLabel titleLabel = new JLabel("Monster Examine");
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setFont(titleLabel.getFont().deriveFont(Font.BOLD, 16));
        titlePanel.add(titleLabel);
        add(titlePanel);

        add(Box.createVerticalStrut(10));

        monsterNameLabel = new JLabel();
        monsterNameLabel.setFont(FontManager.getRunescapeFont());
        monsterNameLabel.setForeground(Color.WHITE);
        monsterNameLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        monsterNameLabel.setFont(FontManager.getRunescapeBoldFont());
        add(monsterNameLabel);

        add(Box.createVerticalStrut(20));

        combatStatsPanel = new JPanel();
        aggressiveStatsPanel = new JPanel();
        defensiveStatsPanel = new JPanel();

        addStatsCategory("Combat Stats", combatStatsPanel);
        addStatsCategory("Aggressive Stats", aggressiveStatsPanel);
        addStatsCategory("Defensive Stats", defensiveStatsPanel);
    }

    private void addStatsCategory(String categoryName, JPanel panel)
    {
        JPanel categoryPanel = new JPanel();
        categoryPanel.setLayout(new BoxLayout(categoryPanel, BoxLayout.Y_AXIS));
        categoryPanel.setBackground(ColorScheme.DARKER_GRAY_COLOR);
        categoryPanel.setBorder(new TitledBorder(categoryName));
        categoryPanel.setAlignmentX(Component.CENTER_ALIGNMENT);

        panel.setLayout(new FlowLayout(FlowLayout.CENTER));
        panel.setBackground(ColorScheme.DARKER_GRAY_COLOR);
        categoryPanel.add(panel);

        add(categoryPanel);
        add(Box.createVerticalStrut(10)); // Add vertical spacing between categories
    }

    private void addCombatStat(JPanel panel, ImageIcon icon, int value)
    {
        JPanel statPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        statPanel.setBackground(ColorScheme.DARKER_GRAY_COLOR);

        JLabel iconLabel = new JLabel(resizeIcon(icon, ICON_SIZE, ICON_SIZE));
        JLabel valueLabel = new JLabel(String.valueOf(value));
        valueLabel.setForeground(Color.WHITE);

        statPanel.add(iconLabel);
        statPanel.add(valueLabel);
        panel.add(statPanel);
    }

    private void addAggressiveStat(JPanel panel, ImageIcon icon, int value)
    {
        JPanel statPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        statPanel.setBackground(ColorScheme.DARKER_GRAY_COLOR);

        JLabel iconLabel = new JLabel(resizeIcon(icon, ICON_SIZE, ICON_SIZE));
        JLabel valueLabel = new JLabel("+" + value);
        valueLabel.setForeground(Color.WHITE);

        statPanel.add(iconLabel);
        statPanel.add(valueLabel);
        panel.add(statPanel);
    }


    private void addDefensiveStat(JPanel panel, ImageIcon icon, int value)
    {
        JPanel statPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        statPanel.setBackground(ColorScheme.DARKER_GRAY_COLOR);

        JLabel iconLabel = new JLabel(resizeIcon(icon, ICON_SIZE, ICON_SIZE));
        JLabel valueLabel = new JLabel(String.valueOf(value));
        valueLabel.setForeground(Color.WHITE);

        statPanel.add(iconLabel);
        statPanel.add(valueLabel);
        panel.add(statPanel);
    }


    private ImageIcon resizeIcon(ImageIcon icon, int width, int height)
    {
        Image image = icon.getImage().getScaledInstance(width, height, Image.SCALE_SMOOTH);
        return new ImageIcon(image);
    }


    public void updateMonsterInfo(String monsterName, int hitpoints, int attack, int strength, int defence, int magic, int ranged,
                                  int attackBonus, int strengthBonus, int magicAccuracy, int magicStrength, int rangedAccuracy, int rangedStrength,
                                  int stabDefence, int slashDefence, int crushDefence, int magicDefence, int rangedDefence)
    {
        monsterNameLabel.setText(monsterName);

        combatStatsPanel.removeAll();
        addCombatStat(combatStatsPanel, new ImageIcon(getClass().getResource("/icons/combat_stats/hitpoints_icon.png")), hitpoints);
        addCombatStat(combatStatsPanel, new ImageIcon(getClass().getResource("/icons/combat_stats/attack_icon.png")), attack);
        addCombatStat(combatStatsPanel, new ImageIcon(getClass().getResource("/icons/combat_stats/strength_icon.png")), strength);
        addCombatStat(combatStatsPanel, new ImageIcon(getClass().getResource("/icons/combat_stats/defence_icon.png")), defence);
        addCombatStat(combatStatsPanel, new ImageIcon(getClass().getResource("/icons/combat_stats/magic_icon.png")), magic);
        addCombatStat(combatStatsPanel, new ImageIcon(getClass().getResource("/icons/combat_stats/ranged_icon.png")), ranged);

        aggressiveStatsPanel.removeAll();
        addAggressiveStat(aggressiveStatsPanel, new ImageIcon(getClass().getResource("/icons/aggressive_stats/attack_bonus_icon.png")), attackBonus);
        addAggressiveStat(aggressiveStatsPanel, new ImageIcon(getClass().getResource("/icons/aggressive_stats/strength_bonus_icon.png")), strengthBonus);
        addAggressiveStat(aggressiveStatsPanel, new ImageIcon(getClass().getResource("/icons/aggressive_stats/magic_accuracy_icon.png")), magicAccuracy);
        addAggressiveStat(aggressiveStatsPanel, new ImageIcon(getClass().getResource("/icons/aggressive_stats/magic_strength_icon.png")), magicStrength);
        addAggressiveStat(aggressiveStatsPanel, new ImageIcon(getClass().getResource("/icons/aggressive_stats/ranged_accuracy_icon.png")), rangedAccuracy);
        addAggressiveStat(aggressiveStatsPanel, new ImageIcon(getClass().getResource("/icons/aggressive_stats/ranged_strength_icon.png")), rangedStrength);

        defensiveStatsPanel.removeAll();
        addDefensiveStat(defensiveStatsPanel, new ImageIcon(getClass().getResource("/icons/defensive_stats/stab_defence_icon.png")), stabDefence);
        addDefensiveStat(defensiveStatsPanel, new ImageIcon(getClass().getResource("/icons/defensive_stats/slash_defence_icon.png")), slashDefence);
        addDefensiveStat(defensiveStatsPanel, new ImageIcon(getClass().getResource("/icons/defensive_stats/crush_defence_icon.png")), crushDefence);
        addDefensiveStat(defensiveStatsPanel, new ImageIcon(getClass().getResource("/icons/defensive_stats/magic_defence_icon.png")), magicDefence);
        addDefensiveStat(defensiveStatsPanel, new ImageIcon(getClass().getResource("/icons/defensive_stats/ranged_defence_icon.png")), rangedDefence);

        revalidate();
        repaint();
    }
}
