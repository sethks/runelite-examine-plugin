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
    private final MonsterExaminePlugin plugin;
    private final JLabel monsterNameLabel;
    private final JTextArea monsterStatsTextArea;


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

        monsterStatsTextArea = new JTextArea();
        monsterStatsTextArea.setFont(FontManager.getRunescapeFont());
        monsterStatsTextArea.setForeground(Color.WHITE);
        monsterStatsTextArea.setBackground(ColorScheme.DARKER_GRAY_COLOR);
        monsterStatsTextArea.setEditable(false);
        monsterStatsTextArea.setLineWrap(true);
        monsterStatsTextArea.setWrapStyleWord(true);
        JScrollPane scrollPane = new JScrollPane(monsterStatsTextArea);
        add(scrollPane, BorderLayout.CENTER);
    }

    public void updateMonsterInfo(String monsterName, String monsterStats)
    {
        monsterNameLabel.setText(monsterName);
        monsterStatsTextArea.setText(monsterStats);
    }
}