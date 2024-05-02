package com.monsterexamine;

import lombok.Getter;
import net.runelite.client.ui.ColorScheme;
import net.runelite.client.ui.FontManager;
import net.runelite.client.ui.PluginPanel;
import javax.inject.Inject;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
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
        setBorder(new EmptyBorder(10, 10, 10, 10));
        setLayout(new BorderLayout());
        setBackground(ColorScheme.DARKER_GRAY_COLOR);

        JPanel titlePanel = new JPanel();
        titlePanel.setLayout(new BoxLayout(titlePanel, BoxLayout.X_AXIS));
        titlePanel.setBorder(new EmptyBorder(0, 0, 10, 0));

        JLabel titleLabel = new JLabel("Monster Examine");
        titleLabel.setFont(FontManager.getRunescapeBoldFont());
        titleLabel.setForeground(Color.WHITE);
        titlePanel.add(titleLabel);

        add(titlePanel, BorderLayout.NORTH);

        monsterNameLabel = new JLabel();
        monsterNameLabel.setFont(FontManager.getRunescapeFont());
        monsterNameLabel.setForeground(Color.WHITE);
        monsterNameLabel.setHorizontalAlignment(SwingConstants.CENTER);
        add(monsterNameLabel, BorderLayout.NORTH);

        JPanel statsPanel = new JPanel();
        statsPanel.setLayout(new GridLayout(3, 1));
        statsPanel.setBackground(ColorScheme.DARKER_GRAY_COLOR);

        combatStatsPanel = createStatsPanel("Combat Stats");
        aggressiveStatsPanel = createStatsPanel("Aggressive Stats");
        defensiveStatsPanel = createStatsPanel("Defensive Stats");

        statsPanel.add(combatStatsPanel);
        statsPanel.add(aggressiveStatsPanel);
        statsPanel.add(defensiveStatsPanel);

        add(statsPanel, BorderLayout.CENTER);
    }

    private JPanel createStatsPanel(String title)
    {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(ColorScheme.DARKER_GRAY_COLOR);

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(FontManager.getRunescapeFont());
        titleLabel.setForeground(Color.WHITE);
        panel.add(titleLabel);

        return panel;
    }

    private void addCombatStat(JPanel panel, ImageIcon icon, int value)
    {
        JLabel iconLabel = new JLabel(resizeIcon(icon, ICON_SIZE, ICON_SIZE));
        JLabel valueLabel = new JLabel(String.valueOf(value));
        valueLabel.setForeground(Color.WHITE);

        panel.add(iconLabel);
        panel.add(valueLabel);
    }

    private void addAggressiveStat(JPanel panel, ImageIcon icon, int value)
    {
        JLabel iconLabel = new JLabel(resizeIcon(icon, ICON_SIZE, ICON_SIZE));
        JLabel valueLabel = new JLabel("+" + value);
        valueLabel.setForeground(Color.WHITE);

        panel.add(iconLabel);
        panel.add(valueLabel);
    }

    private void addDefensiveStat(JPanel panel, ImageIcon icon, int value)
    {
        JLabel iconLabel = new JLabel(resizeIcon(icon, ICON_SIZE, ICON_SIZE));
        JLabel valueLabel = new JLabel(String.valueOf(value));
        valueLabel.setForeground(Color.WHITE);

        panel.add(iconLabel);
        panel.add(valueLabel);
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
        addCombatStat(combatStatsPanel, new ImageIcon(getClass().getResource("/icons/hitpoints_icon.png")), hitpoints);
        addCombatStat(combatStatsPanel, new ImageIcon(getClass().getResource("/icons/attack_icon.png")), attack);
        addCombatStat(combatStatsPanel, new ImageIcon(getClass().getResource("/icons/strength_icon.png")), strength);
        addCombatStat(combatStatsPanel, new ImageIcon(getClass().getResource("/icons/defence_icon.png")), defence);
        addCombatStat(combatStatsPanel, new ImageIcon(getClass().getResource("/icons/magic_icon.png")), magic);
        addCombatStat(combatStatsPanel, new ImageIcon(getClass().getResource("/icons/ranged_icon.png")), ranged);

        aggressiveStatsPanel.removeAll();
        addAggressiveStat(aggressiveStatsPanel, new ImageIcon(getClass().getResource("/icons/attack_bonus_icon.png")), attackBonus);
        addAggressiveStat(aggressiveStatsPanel, new ImageIcon(getClass().getResource("/icons/strength_bonus_icon.png")), strengthBonus);
        addAggressiveStat(aggressiveStatsPanel, new ImageIcon(getClass().getResource("/icons/magic_accuracy_icon.png")), magicAccuracy);
        addAggressiveStat(aggressiveStatsPanel, new ImageIcon(getClass().getResource("/icons/magic_strength_icon.png")), magicStrength);
        addAggressiveStat(aggressiveStatsPanel, new ImageIcon(getClass().getResource("/icons/ranged_accuracy_icon.png")), rangedAccuracy);
        addAggressiveStat(aggressiveStatsPanel, new ImageIcon(getClass().getResource("/icons/ranged_strength_icon.png")), rangedStrength);

        defensiveStatsPanel.removeAll();
        addDefensiveStat(defensiveStatsPanel, new ImageIcon(getClass().getResource("/icons/stab_defence_icon.png")), stabDefence);
        addDefensiveStat(defensiveStatsPanel, new ImageIcon(getClass().getResource("/icons/slash_defence_icon.png")), slashDefence);
        addDefensiveStat(defensiveStatsPanel, new ImageIcon(getClass().getResource("/icons/crush_defence_icon.png")), crushDefence);
        addDefensiveStat(defensiveStatsPanel, new ImageIcon(getClass().getResource("/icons/magic_defence_icon.png")), magicDefence);
        addDefensiveStat(defensiveStatsPanel, new ImageIcon(getClass().getResource("/icons/ranged_defence_icon.png")), rangedDefence);

        revalidate();
        repaint();
    }
}
