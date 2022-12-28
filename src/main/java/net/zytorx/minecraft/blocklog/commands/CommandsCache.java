package net.zytorx.minecraft.blocklog.commands;

import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.UsernameCache;
import net.zytorx.minecraft.blocklog.cache.model.InteractionUtils;
import net.zytorx.minecraft.blocklog.cache.model.blocks.MultiBlockInteraction;
import net.zytorx.minecraft.blocklog.cache.model.blocks.SingleBlockInteraction;
import net.zytorx.minecraft.blocklog.cache.model.common.Interaction;
import net.zytorx.minecraft.blocklog.cache.model.common.OldNewTuple;
import net.zytorx.minecraft.blocklog.logging.Logger;

import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

public class CommandsCache {
    private static final HashMap<UUID, Map<Integer, List<String>>> blockLogCache = new HashMap<>();
    private static final HashMap<UUID, Integer> blockLogCounter = new HashMap<>();

    public static List<String> loadBlockLogCache(UUID executer, Player filter, int page) {
        if (!blockLogCache.containsKey(executer)) {
            var toAdd = new ArrayList<String>();
            Logger.getAllInteractions()
                    .filter(interaction -> filter == null || interaction.getEntity().equals(filter.getUUID()))
                    .sorted(CommandsCache::compare).forEach(interaction -> interactionToString(interaction, toAdd));
            var pageMap = new HashMap<Integer, List<String>>();
            var pageLines = 10;
            for (var i = 0; i < toAdd.size(); i += pageLines) {
                pageMap.put(i / pageLines, toAdd.subList(i, Math.min(i + pageLines, toAdd.size())));
            }
            blockLogCache.put(executer, pageMap);

        }
        var paged = blockLogCache.get(executer);

        return paged.get(paged.containsKey(page) ? page : 0);
    }

    private static void interactionToString(Interaction toConvert, List<String> strings) {
        var time = new Date(toConvert.getTime()).toString();
        var entity = UsernameCache.containsUUID(toConvert.getEntity()) ? UsernameCache.getLastKnownUsername(toConvert.getEntity()) : toConvert.getEntity().toString();
        var template = time + ": " + entity + " {action} at x: {x} y: {y} z: {z}";
        if (toConvert instanceof SingleBlockInteraction singleBlock) {
            strings.add(convert(template, singleBlock.getBlock(), singleBlock.getX(), singleBlock.getY(), singleBlock.getZ()));
        }

        if (toConvert instanceof MultiBlockInteraction multiBlock) {
            var interactions = multiBlock.getInteractions();
            for (var pos : interactions.keySet()) {
                strings.add(convert(template, interactions.get(pos), pos.getX(), pos.getY(), pos.getZ()));
            }
        }
    }

    private static String convert(String template, OldNewTuple tuple, int x, int y, int z) {
        var oldState = InteractionUtils.readBlockState(tuple.getOldState());
        var newState = InteractionUtils.readBlockState(tuple.getNewState());
        if (newState.isAir()) {
            template = template.replace("{action}", "broke {old}");
        } else if (oldState.isAir()) {
            template = template.replace("{action}", "placed {new}");
        } else {
            template = template.replace("{action}", "replaced {old} with {new}");
        }
        var oldItem = oldState.getBlock().asItem();
        var newItem = newState.getBlock().asItem();
        return template.replace("{old}", oldItem.getName(oldItem.getDefaultInstance()).getString())
                .replace("{new}", newItem.getName(newItem.getDefaultInstance()).getString())
                .replace("{x}", x + "")
                .replace("{y}", y + "")
                .replace("{z}", z + "");
    }

    private static int compare(Interaction i1, Interaction i2) {
        var temp = i1.getTime() - i2.getTime();
        if (temp > 0) {
            return 1;
        }
        if (temp < 0) {
            return -1;
        }
        return 0;
    }

    public static void deleteOverhead() {
        var toRemove = new ArrayList<UUID>();
        for (var key : blockLogCounter.keySet()) {
            var isOverhead = new AtomicBoolean(false);
            blockLogCounter.computeIfPresent(key, (u, i) -> {
                if (i + 1 == 20) {
                    isOverhead.set(true);
                }
                return i + 1;
            });
            if (isOverhead.get()) {
                toRemove.add(key);
            }
        }
        for (var removing : toRemove) {
            blockLogCache.remove(removing);
            blockLogCounter.remove(removing);
        }
    }

}
