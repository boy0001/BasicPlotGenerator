package com.boydti.basicplots;

import org.bukkit.generator.ChunkGenerator;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin {
    @Override
    public ChunkGenerator getDefaultWorldGenerator(final String worldName, final String id) {
        return new BasicGen(worldName);
    }
}
