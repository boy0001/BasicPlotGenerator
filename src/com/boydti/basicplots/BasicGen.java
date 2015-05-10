package com.boydti.basicplots;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.bukkit.World;
import org.bukkit.block.Biome;

import com.intellectualcrafters.plot.object.PlotGenerator;
import com.intellectualcrafters.plot.object.PlotManager;
import com.intellectualcrafters.plot.object.PlotPopulator;
import com.intellectualcrafters.plot.object.PlotWorld;
import com.intellectualcrafters.plot.object.PseudoRandom;
import com.intellectualcrafters.plot.object.RegionWrapper;

public class BasicGen extends PlotGenerator {

    private short[][] result;
    private static PlotManager manager = new BasicPlotManager();

    public final static short HEIGHT = 64;
    public final static short ROAD_BLOCK = 172; // Road block (hardened clay)
    public final static short WALL_BLOCK = 7; // Plot wall (between plot and road = bedrock)
    public final static short BORDER_BLOCK = 44; // Plot border (half slab)
    public final static short BORDER_CLAIMED_BLOCK = 126; // Plot border (half slab)
    public final static short MAIN_BLOCK = 1; // Plot main filling (stone)
    public final static short FLOOR_BLOCK = 2; // Plot top floor (grass)
    public final static short BOTTOM_BLOCK = 7; // Bottom bedrock

    public BasicGen(final String worldName) {
        super(worldName);
    }

    @Override
    public short[][] generateExtBlockSections(final World world, final Random r, final int cx, final int cz, final BiomeGrid biomes) {
        /*
         *  This would normally not be overrided unless we aren't using the extra features PlotGenerator offers
         *   - We will provide our own plot clearing
         *   - All the chunks will be the same (that's how basic this generator is)
         */
        for (byte x = 0; x < 16; x++) {
            for (byte z = 0; z < 16; z++) {
                biomes.setBiome(x, z, Biome.FOREST);
            }
        }
        // And since we are using a pre-made chunk, we just return that. Easy!
        return this.result;
    }

    @Override
    public void generateChunk(final World world, final RegionWrapper region, final PseudoRandom random, final int X, final int Z, final BiomeGrid grid) {
        /*
         * Usually we would use this (instead of generateExtBlockSections)
         * as then we wouldn't need to provide our own plot clearing
         *
         * However for this simple example we are not
         */
    }

    @Override
    public PlotWorld getNewPlotWorld(final String world) {
        return new BasicPlotWorld(world);
    }

    @Override
    public PlotManager getPlotManager() {
        return manager;
    }

    @Override
    public List<PlotPopulator> getPopulators(final String world) {
        // We aren't doing any population, as this is going to be a really basic generator
        return new ArrayList<PlotPopulator>();
    }

    public void setBlock(final short[][] result, final int x, final int y, final int z, final short blkid) {
        if (result[y >> 4] == null) {
            result[y >> 4] = new short[4096];
        }
        result[y >> 4][((y & 0xF) << 8) | (z << 4) | x] = blkid;
    }

    @Override
    public void init(final PlotWorld world) {
        this.result = new short[16][];
        boolean roadx;
        boolean roadz;
        boolean wallx;
        boolean wallz;
        for (int x = 0; x < 16; x++) {
            roadx = (x == 0) || (x == 15);
            wallx = (x == 1) || (x == 14);
            for (int z = 0; z < 16; z++) {
                roadz = (z == 0) || (z == 15);
                wallz = (z == 1) || (z == 14);

                // Setting the road block
                if (roadx || roadz) {
                    for (int y = 1; y <= HEIGHT; y++) {
                        setBlock(this.result, x, y, z, ROAD_BLOCK);
                    }
                }

                // Setting the wall and border block
                else if (wallx || wallz) {
                    for (int y = 1; y <= HEIGHT; y++) {
                        setBlock(this.result, x, y, z, WALL_BLOCK);
                    }
                    setBlock(this.result, x, HEIGHT + 1, z, BORDER_BLOCK);
                }

                // Setting the main block
                else {
                    for (int y = 1; y < HEIGHT; y++) {
                        setBlock(this.result, x, y, z, MAIN_BLOCK);
                    }
                    setBlock(this.result, x, HEIGHT, z, FLOOR_BLOCK);
                }

                // Adding the bottom layer 0 blocks
                setBlock(this.result, x, 0, z, BOTTOM_BLOCK);
            }
        }
    }

}
