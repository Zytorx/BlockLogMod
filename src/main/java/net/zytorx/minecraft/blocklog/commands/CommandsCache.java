package net.zytorx.minecraft.blocklog.commands;

import net.minecraftforge.common.UsernameCache;
import net.zytorx.minecraft.blocklog.cache.model.InteractionUtils;
import net.zytorx.minecraft.blocklog.cache.model.blocks.BlockInteraction;
import net.zytorx.minecraft.blocklog.cache.model.common.Interaction;
import net.zytorx.minecraft.blocklog.cache.model.common.OldNewTuple;
import net.zytorx.minecraft.blocklog.logging.Logger;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

public class CommandsCache {

    private static final SimpleDateFormat format = new SimpleDateFormat("dd.MM.yy-hh:mm:ss");
    private static HashMap<String, Map<Integer, List<String>>> blockLogCache = new HashMap<>();
    private static HashMap<String, Integer> blockLogCounter = new HashMap<>();

    public static List<String> loadBlockLogCache(String filter, int page) {
        var loadedFilter = loadName(null, filter);
        if (!blockLogCache.containsKey(loadedFilter)) {
            var toAdd = new ArrayList<String>();
            var interactions = Logger.getAllInteractions();
            var filtered = interactions.filter(interaction -> loadedFilter == null || interaction.getEntityID().toString().equals(loadedFilter) || interaction.getEntityName().equals(loadedFilter));
            var sorted = filtered.sorted(CommandsCache::compare);
            sorted.forEach(interaction -> interactionToString(interaction, toAdd));
            var pageMap = new HashMap<Integer, List<String>>();
            var pageLines = 10;
            for (var i = 0; i < toAdd.size(); i += pageLines) {
                pageMap.put(i / pageLines, toAdd.subList(i, Math.min(i + pageLines, toAdd.size())));
            }
            if (pageMap.isEmpty()) {
                return List.of("No entries found");
            }
            blockLogCache.put(loadedFilter, pageMap);
            blockLogCounter.put(loadedFilter, 0);
        }
        var paged = blockLogCache.get(loadedFilter);

        return paged.get(paged.containsKey(page) ? page : 0);
    }

    private static void interactionToString(Interaction toConvert, List<String> strings) {
        var time = format.format(new Date(toConvert.getTime()));
        var entity = loadName(toConvert.getEntityID(), toConvert.getEntityName());
        var template = time + ": " + entity + " {action} at ({x}, {y}, {z})";
        if (toConvert instanceof BlockInteraction singleBlock) {
            strings.add(convert(template, singleBlock.getBlock(), singleBlock.getX(), singleBlock.getY(), singleBlock.getZ()));
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

    private static String loadName(UUID id, String name) {
        if (id == null || !UsernameCache.containsUUID(id)) {
            return name;
        }
        return UsernameCache.getLastKnownUsername(id);
    }

    private static int compare(Interaction i1, Interaction i2) {
        var temp = i1.getTime() - i2.getTime();
        if (temp < 0) {
            return 1;
        }
        if (temp > 0) {
            return -1;
        }
        return 0;
    }

    static void clearCache() {
        blockLogCounter = new HashMap<>();
        blockLogCache = new HashMap<>();
    }

    public static void deleteOverhead() {
        var toRemove = new ArrayList<String>();
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
