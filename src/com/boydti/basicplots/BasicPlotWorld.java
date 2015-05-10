package com.boydti.basicplots;

import org.bukkit.configuration.ConfigurationSection;

import com.intellectualcrafters.plot.config.ConfigurationNode;
import com.intellectualcrafters.plot.generator.GridPlotWorld;

public class BasicPlotWorld extends GridPlotWorld {

    public BasicPlotWorld(final String worldname) {
        super(worldname);
    }

    @Override
    public ConfigurationNode[] getSettingNodes() {
        return new ConfigurationNode[0];
    }

    @Override
    public void loadConfiguration(final ConfigurationSection arg0) {
        // Nothing is configurable :P
    }

}
