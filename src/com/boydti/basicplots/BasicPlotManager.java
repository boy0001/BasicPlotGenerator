package com.boydti.basicplots;

import java.util.ArrayList;

import com.intellectualcrafters.plot.PS;
import com.intellectualcrafters.plot.generator.GridPlotManager;
import com.intellectualcrafters.plot.object.ChunkLoc;
import com.intellectualcrafters.plot.object.Location;
import com.intellectualcrafters.plot.object.Plot;
import com.intellectualcrafters.plot.object.PlotBlock;
import com.intellectualcrafters.plot.object.PlotId;
import com.intellectualcrafters.plot.object.PlotWorld;
import com.intellectualcrafters.plot.object.PseudoRandom;
import com.intellectualcrafters.plot.util.BlockManager;
import com.intellectualcrafters.plot.util.ChunkManager;
import com.intellectualcrafters.plot.util.MainUtil;
import com.intellectualcrafters.plot.util.SetBlockQueue;
import com.intellectualcrafters.plot.util.TaskManager;

public class BasicPlotManager extends GridPlotManager {

    /*
     * This is a modified version of the PlotSquared ClassicPlotManager class
     */

    @Override
    public boolean clearPlot(final PlotWorld plotworld, final Plot plot, final boolean isDelete, final Runnable whenDone) {
        ChunkManager.manager.regenerateChunk(plotworld.worldname, new ChunkLoc(plot.id.x, plot.id.y));
        TaskManager.runTask(whenDone);
        return true;
    }

    @Override
    public boolean setComponent(final PlotWorld plotworld, final PlotId plotid, final String component, final PlotBlock[] blocks) {
        if (blocks.length == 0) {
            return false;
        }
        switch (component) {
            case "floor": {
                setFloor(plotworld, plotid, blocks[0]);
                return true;
            }
            case "wall": {
                setWallFilling(plotworld, plotid, blocks[0]);
                return true;
            }
            case "border": {
                setWall(plotworld, plotid, blocks[0]);
                return true;
            }
        }
        return false;
    }

    public boolean setFloor(final PlotWorld plotworld, final PlotId plotid, final PlotBlock block) {
        final Location pos1 = MainUtil.getPlotBottomLoc(plotworld.worldname, plotid).add(1, 0, 1);
        final Location pos2 = MainUtil.getPlotTopLoc(plotworld.worldname, plotid).add(1, 0, 1);
        pos1.setY(BasicGen.HEIGHT);
        pos2.setY(BasicGen.HEIGHT + 1);
        MainUtil.setSimpleCuboidAsync(plotworld.worldname, pos1, pos2, block);
        return true;
    }

    public boolean setWallFilling(final PlotWorld plotworld, final PlotId plotid, final PlotBlock block) {
        final Location bottom = MainUtil.getPlotBottomLoc(plotworld.worldname, plotid);
        final Location top = MainUtil.getPlotTopLoc(plotworld.worldname, plotid).add(1, 0, 1);
        int x, z;
        z = bottom.getZ();
        final int length1 = top.getX() - bottom.getX();
        final int length2 = top.getZ() - bottom.getZ();
        final int size = ((length1 * 2) + (length2 * 2)) * (BasicGen.HEIGHT);
        final int[] xl = new int[size];
        final int[] yl = new int[size];
        final int[] zl = new int[size];
        final PlotBlock[] bl = new PlotBlock[size];
        int i = 0;
        new PseudoRandom();
        for (x = bottom.getX(); x <= (top.getX() - 1); x++) {
            for (int y = 1; y <= BasicGen.HEIGHT; y++) {
                xl[i] = x;
                zl[i] = z;
                yl[i] = y;
                bl[i] = block;
                i++;
            }
        }
        x = top.getX();
        for (z = bottom.getZ(); z <= (top.getZ() - 1); z++) {
            for (int y = 1; y <= BasicGen.HEIGHT; y++) {
                xl[i] = x;
                zl[i] = z;
                yl[i] = y;
                bl[i] = block;
                i++;
            }
        }
        z = top.getZ();
        for (x = top.getX(); x >= (bottom.getX() + 1); x--) {
            for (int y = 1; y <= BasicGen.HEIGHT; y++) {
                xl[i] = x;
                zl[i] = z;
                yl[i] = y;
                bl[i] = block;
                i++;
            }
        }
        x = bottom.getX();
        for (z = top.getZ(); z >= (bottom.getZ() + 1); z--) {
            for (int y = 1; y <= BasicGen.HEIGHT; y++) {
                xl[i] = x;
                zl[i] = z;
                yl[i] = y;
                bl[i] = block;
                i++;
            }
        }
        BlockManager.setBlocks(plotworld.worldname, xl, yl, zl, bl);
        return true;
    }

    public boolean setWall(final PlotWorld plotworld, final PlotId plotid, final PlotBlock block) {
        final Location bottom = MainUtil.getPlotBottomLoc(plotworld.worldname, plotid);
        final Location top = MainUtil.getPlotTopLoc(plotworld.worldname, plotid).add(1, 0, 1);
        int x, z;
        z = bottom.getZ();
        new PseudoRandom();
        final int y = BasicGen.HEIGHT + 1;
        for (x = bottom.getX(); x <= (top.getX() - 1); x++) {
            SetBlockQueue.setBlock(plotworld.worldname, x, y, z, BasicGen.BORDER_CLAIMED_BLOCK);
        }
        x = top.getX();
        for (z = bottom.getZ(); z <= (top.getZ() - 1); z++) {
            SetBlockQueue.setBlock(plotworld.worldname, x, y, z, BasicGen.BORDER_CLAIMED_BLOCK);
        }
        z = top.getZ();
        for (x = top.getX(); x >= (bottom.getX() + 1); x--) {
            SetBlockQueue.setBlock(plotworld.worldname, x, y, z, BasicGen.BORDER_CLAIMED_BLOCK);
        }
        x = bottom.getX();
        for (z = top.getZ(); z >= (bottom.getZ() + 1); z--) {
            SetBlockQueue.setBlock(plotworld.worldname, x, y, z, BasicGen.BORDER_CLAIMED_BLOCK);
        }
        return true;
    }

    /**
     * PLOT MERGING
     */
    @Override
    public boolean createRoadEast(final PlotWorld plotworld, final Plot plot) {
        final Location pos1 = getPlotBottomLocAbs(plotworld, plot.id);
        final Location pos2 = getPlotTopLocAbs(plotworld, plot.id);
        final int sx = pos2.getX() + 1;
        final int ex = (sx + 4) - 1;
        final int sz = pos1.getZ() - 1;
        final int ez = pos2.getZ() + 2;
        MainUtil.setSimpleCuboidAsync(plotworld.worldname, new Location(plotworld.worldname, sx, Math.min(BasicGen.HEIGHT, BasicGen.HEIGHT) + 1, sz), new Location(plotworld.worldname, ex + 1, 257, ez + 1), new PlotBlock((short) 0, (byte) 0));
        MainUtil.setSimpleCuboidAsync(plotworld.worldname, new Location(plotworld.worldname, sx, 1, sz + 1), new Location(plotworld.worldname, ex + 1, BasicGen.HEIGHT, ez), new PlotBlock((short) 7, (byte) 0));
        MainUtil.setSimpleCuboidAsync(plotworld.worldname, new Location(plotworld.worldname, sx, 1, sz + 1), new Location(plotworld.worldname, sx + 1, BasicGen.HEIGHT + 1, ez), new PlotBlock(BasicGen.WALL_BLOCK, (byte) 0));
        MainUtil.setSimpleCuboidAsync(plotworld.worldname, new Location(plotworld.worldname, sx, BasicGen.HEIGHT + 1, sz + 1), new Location(plotworld.worldname, sx + 1, BasicGen.HEIGHT + 2, ez), new PlotBlock(BasicGen.BORDER_CLAIMED_BLOCK, (byte) 0));
        MainUtil.setSimpleCuboidAsync(plotworld.worldname, new Location(plotworld.worldname, ex, 1, sz + 1), new Location(plotworld.worldname, ex + 1, BasicGen.HEIGHT + 1, ez), new PlotBlock(BasicGen.WALL_BLOCK, (byte) 0));
        MainUtil.setSimpleCuboidAsync(plotworld.worldname, new Location(plotworld.worldname, ex, BasicGen.HEIGHT + 1, sz + 1), new Location(plotworld.worldname, ex + 1, BasicGen.HEIGHT + 2, ez), new PlotBlock(BasicGen.BORDER_CLAIMED_BLOCK, (byte) 0));
        MainUtil.setSimpleCuboidAsync(plotworld.worldname, new Location(plotworld.worldname, sx + 1, 1, sz + 1), new Location(plotworld.worldname, ex, BasicGen.HEIGHT + 1, ez), new PlotBlock(BasicGen.ROAD_BLOCK, (byte) 0));
        return true;
    }

    @Override
    public boolean createRoadSouth(final PlotWorld plotworld, final Plot plot) {
        final Location pos1 = getPlotBottomLocAbs(plotworld, plot.id);
        final Location pos2 = getPlotTopLocAbs(plotworld, plot.id);
        final int sz = pos2.getZ() + 1;
        final int ez = (sz + 4) - 1;
        final int sx = pos1.getX() - 1;
        final int ex = pos2.getX() + 2;
        MainUtil.setSimpleCuboidAsync(plotworld.worldname, new Location(plotworld.worldname, sx, Math.min(BasicGen.HEIGHT, BasicGen.HEIGHT) + 1, sz), new Location(plotworld.worldname, ex + 1, 257, ez + 1), new PlotBlock((short) 0, (byte) 0));
        MainUtil.setSimpleCuboidAsync(plotworld.worldname, new Location(plotworld.worldname, sx + 1, 0, sz), new Location(plotworld.worldname, ex, 1, ez + 1), new PlotBlock((short) 7, (byte) 0));
        MainUtil.setSimpleCuboidAsync(plotworld.worldname, new Location(plotworld.worldname, sx + 1, 1, sz), new Location(plotworld.worldname, ex, BasicGen.HEIGHT + 1, sz + 1), new PlotBlock(BasicGen.WALL_BLOCK, (byte) 0));
        MainUtil.setSimpleCuboidAsync(plotworld.worldname, new Location(plotworld.worldname, sx + 1, BasicGen.HEIGHT + 1, sz), new Location(plotworld.worldname, ex, BasicGen.HEIGHT + 2, sz + 1), new PlotBlock(BasicGen.BORDER_CLAIMED_BLOCK, (byte) 0));
        MainUtil.setSimpleCuboidAsync(plotworld.worldname, new Location(plotworld.worldname, sx + 1, 1, ez), new Location(plotworld.worldname, ex, BasicGen.HEIGHT + 1, ez + 1), new PlotBlock(BasicGen.WALL_BLOCK, (byte) 0));
        MainUtil.setSimpleCuboidAsync(plotworld.worldname, new Location(plotworld.worldname, sx + 1, BasicGen.HEIGHT + 1, ez), new Location(plotworld.worldname, ex, BasicGen.HEIGHT + 2, ez + 1), new PlotBlock(BasicGen.BORDER_CLAIMED_BLOCK, (byte) 0));
        MainUtil.setSimpleCuboidAsync(plotworld.worldname, new Location(plotworld.worldname, sx + 1, 1, sz + 1), new Location(plotworld.worldname, ex, BasicGen.HEIGHT + 1, ez), new PlotBlock(BasicGen.ROAD_BLOCK, (byte) 0));
        return true;
    }

    @Override
    public boolean createRoadSouthEast(final PlotWorld plotworld, final Plot plot) {
        final Location pos2 = getPlotTopLocAbs(plotworld, plot.id);
        final int sx = pos2.getX() + 1;
        final int ex = (sx + 4) - 1;
        final int sz = pos2.getZ() + 1;
        final int ez = (sz + 4) - 1;
        MainUtil.setSimpleCuboidAsync(plotworld.worldname, new Location(plotworld.worldname, sx, BasicGen.HEIGHT + 1, sz + 1), new Location(plotworld.worldname, ex + 1, 257, ez), new PlotBlock((short) 0, (byte) 0));
        MainUtil.setSimpleCuboidAsync(plotworld.worldname, new Location(plotworld.worldname, sx + 1, 0, sz + 1), new Location(plotworld.worldname, ex, 1, ez), new PlotBlock((short) 7, (byte) 0));
        MainUtil.setSimpleCuboidAsync(plotworld.worldname, new Location(plotworld.worldname, sx + 1, 1, sz + 1), new Location(plotworld.worldname, ex, BasicGen.HEIGHT + 1, ez), new PlotBlock(BasicGen.ROAD_BLOCK, (byte) 0));
        return true;
    }

    @Override
    public boolean removeRoadEast(final PlotWorld plotworld, final Plot plot) {
        final Location pos1 = getPlotBottomLocAbs(plotworld, plot.id);
        final Location pos2 = getPlotTopLocAbs(plotworld, plot.id);
        final int sx = pos2.getX() + 1;
        final int ex = (sx + 4) - 1;
        final int sz = pos1.getZ();
        final int ez = pos2.getZ() + 1;
        MainUtil.setSimpleCuboidAsync(plotworld.worldname, new Location(plotworld.worldname, sx, Math.min(BasicGen.HEIGHT, BasicGen.HEIGHT) + 1, sz), new Location(plotworld.worldname, ex + 1, 257, ez + 1), new PlotBlock((short) 0, (byte) 0));
        MainUtil.setSimpleCuboidAsync(plotworld.worldname, new Location(plotworld.worldname, sx, 1, sz + 1), new Location(plotworld.worldname, ex + 1, BasicGen.HEIGHT, ez), new PlotBlock(BasicGen.MAIN_BLOCK, (byte) 0));
        MainUtil.setSimpleCuboidAsync(plotworld.worldname, new Location(plotworld.worldname, sx, BasicGen.HEIGHT, sz + 1), new Location(plotworld.worldname, ex + 1, BasicGen.HEIGHT + 1, ez), new PlotBlock(BasicGen.FLOOR_BLOCK, (byte) 0));
        return true;
    }

    @Override
    public boolean removeRoadSouth(final PlotWorld plotworld, final Plot plot) {
        final Location pos1 = getPlotBottomLocAbs(plotworld, plot.id);
        final Location pos2 = getPlotTopLocAbs(plotworld, plot.id);
        final int sz = pos2.getZ() + 1;
        final int ez = (sz + 4) - 1;
        final int sx = pos1.getX();
        final int ex = pos2.getX() + 1;
        MainUtil.setSimpleCuboidAsync(plotworld.worldname, new Location(plotworld.worldname, sx, Math.min(BasicGen.HEIGHT, BasicGen.HEIGHT) + 1, sz), new Location(plotworld.worldname, ex + 1, 257, ez + 1), new PlotBlock((short) 0, (byte) 0));
        MainUtil.setSimpleCuboidAsync(plotworld.worldname, new Location(plotworld.worldname, sx + 1, 1, sz), new Location(plotworld.worldname, ex, BasicGen.HEIGHT, ez + 1), new PlotBlock(BasicGen.MAIN_BLOCK, (byte) 0));
        MainUtil.setSimpleCuboidAsync(plotworld.worldname, new Location(plotworld.worldname, sx + 1, BasicGen.HEIGHT, sz), new Location(plotworld.worldname, ex, BasicGen.HEIGHT + 1, ez + 1), new PlotBlock(BasicGen.FLOOR_BLOCK, (byte) 0));
        return true;
    }

    @Override
    public boolean removeRoadSouthEast(final PlotWorld plotworld, final Plot plot) {
        final BasicPlotWorld dpw = (BasicPlotWorld) plotworld;
        final Location loc = getPlotTopLocAbs(dpw, plot.id);
        final int sx = loc.getX() + 1;
        final int ex = (sx + 4) - 1;
        final int sz = loc.getZ() + 1;
        final int ez = (sz + 4) - 1;
        MainUtil.setSimpleCuboidAsync(plotworld.worldname, new Location(plotworld.worldname, sx, BasicGen.HEIGHT + 1, sz), new Location(plotworld.worldname, ex + 1, 257, ez + 1), new PlotBlock((short) 0, (byte) 0));
        MainUtil.setSimpleCuboidAsync(plotworld.worldname, new Location(plotworld.worldname, sx, 1, sz), new Location(plotworld.worldname, ex + 1, BasicGen.HEIGHT, ez + 1), new PlotBlock(BasicGen.MAIN_BLOCK, (byte) 0));
        MainUtil.setSimpleCuboidAsync(plotworld.worldname, new Location(plotworld.worldname, sx, BasicGen.HEIGHT, sz), new Location(plotworld.worldname, ex + 1, BasicGen.HEIGHT + 1, ez + 1), new PlotBlock(BasicGen.FLOOR_BLOCK, (byte) 0));
        return true;
    }

    /**
     * Finishing off plot merging by adding in the walls surrounding the plot (OPTIONAL)(UNFINISHED)
     */
    @Override
    public boolean finishPlotMerge(final PlotWorld plotworld, final ArrayList<PlotId> plotIds) {
        final PlotId pos1 = plotIds.get(0);
        final PlotBlock block = new PlotBlock(BasicGen.BORDER_CLAIMED_BLOCK, (byte) 0);
        final PlotBlock unclaim = new PlotBlock(BasicGen.BORDER_BLOCK, (byte) 0);
        if (!block.equals(unclaim)) {
            setWall(plotworld, pos1, block);
        }
        return true;
    }

    @Override
    public boolean finishPlotUnlink(final PlotWorld plotworld, final ArrayList<PlotId> plotIds) {
        final PlotBlock block = new PlotBlock(BasicGen.BORDER_CLAIMED_BLOCK, (byte) 0);
        final PlotBlock unclaim = new PlotBlock(BasicGen.BORDER_BLOCK, (byte) 0);
        for (final PlotId id : plotIds) {
            if ((block.id != 0) || !block.equals(unclaim)) {
                setWall(plotworld, id, block);
            }
        }
        return true;
    }

    @Override
    public boolean startPlotMerge(final PlotWorld plotworld, final ArrayList<PlotId> plotIds) {
        return true;
    }

    @Override
    public boolean startPlotUnlink(final PlotWorld plotworld, final ArrayList<PlotId> plotIds) {
        return true;
    }

    @Override
    public boolean claimPlot(final PlotWorld plotworld, final Plot plot) {
        final PlotBlock claim = new PlotBlock(BasicGen.BORDER_CLAIMED_BLOCK, (byte) 0);
        final PlotBlock unclaim = new PlotBlock(BasicGen.BORDER_BLOCK, (byte) 0);
        if ((claim.id != 0) || !claim.equals(unclaim)) {
            setWall(plotworld, plot.id, claim);
        }
        return true;
    }

    @Override
    public String[] getPlotComponents(final PlotWorld plotworld, final PlotId plotid) {
        return new String[] { "floor", "wall", "border" };
    }

    /**
     * Remove sign for a plot
     */
    @Override
    public Location getSignLoc(final PlotWorld plotworld, final Plot plot) {
        final Location bot = MainUtil.getPlotBottomLoc(plotworld.worldname, plot.id);
        return new com.intellectualcrafters.plot.object.Location(plotworld.worldname, bot.getX(), BasicGen.HEIGHT + 1, bot.getZ() - 1);
    }

    @Override
    public Location getPlotBottomLocAbs(final PlotWorld pw, final PlotId id) {
        return new Location(pw.worldname, (id.x << 4) + 1, 0, (id.y << 4) + 1);
    }

    @Override
    public PlotId getPlotId(final PlotWorld pw, final int x, final int y, final int z) {
        final int X = x >> 4;
        final int Z = z >> 4;
        int xx = x % 16;
        int zz = z % 16;
        if (xx < 0) {
            xx += 16;
        }
        if (zz < 0) {
            zz += 16;
        }

        final boolean northSouth = (zz <= 2) || (zz > 13);
        final boolean eastWest = (xx <= 2) || (xx > 13);

        if (northSouth && eastWest) {
            // This means you are in the intersection
            final Location loc = new Location(pw.worldname, x + 5, 0, z + 5);
            final PlotId id = MainUtil.getPlotAbs(loc);
            final Plot plot = PS.get().getPlots(pw.worldname).get(id);
            if (plot == null) {
                return null;
            }
            if ((plot.getMerged(0) && plot.getMerged(3))) {
                return MainUtil.getBottomPlot(plot).id;
            }
            return null;
        }
        if (northSouth) {
            // You are on a road running West to East (yeah, I named the var poorly)
            final Location loc = new Location(pw.worldname, x, 0, z + 5);
            final PlotId id = MainUtil.getPlotAbs(loc);
            final Plot plot = PS.get().getPlots(pw.worldname).get(id);
            if (plot == null) {
                return null;
            }
            if (plot.getMerged(0)) {
                return MainUtil.getBottomPlot(plot).id;
            }
            return null;
        }
        if (eastWest) {
            // This is the road separating an Eastern and Western plot
            final Location loc = new Location(pw.worldname, x + 5, 0, z);
            final PlotId id = MainUtil.getPlotAbs(loc);
            final Plot plot = PS.get().getPlots(pw.worldname).get(id);
            if (plot == null) {
                return null;
            }
            if (plot.getMerged(3)) {
                return MainUtil.getBottomPlot(plot).id;
            }
            return null;
        }
        final PlotId id = new PlotId(X, Z);
        final Plot plot = PS.get().getPlots(pw.worldname).get(id);
        if (plot == null) {
            return id;
        }
        return MainUtil.getBottomPlot(plot).id;
    }

    @Override
    public PlotId getPlotIdAbs(final PlotWorld pw, final int x, final int y, final int z) {
        final int X = x >> 4;
        final int Z = z >> 4;
            int xx = x % 16;
            int zz = z % 16;
            if (xx < 0) {
                xx += 16;
            }
            if (zz < 0) {
                zz += 16;
            }
            if ((xx > 13) || (zz > 13) || (xx < 3) || (zz < 3)) {
                return null;
            }
            return new PlotId(X, Z);
    }

    @Override
    public Location getPlotTopLocAbs(final PlotWorld pw, final PlotId id) {
        return new Location(pw.worldname, (id.x << 4) + 13, 0, (id.y << 4) + 13);
    }
}
