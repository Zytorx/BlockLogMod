package net.zytorx.minecraft.blocklog.commands;

import net.minecraftforge.common.UsernameCache;
import net.zytorx.minecraft.blocklog.BlockLog;
import net.zytorx.minecraft.blocklog.cache.model.InteractionUtils;
import net.zytorx.minecraft.blocklog.cache.model.blocks.BlockInteraction;
import net.zytorx.minecraft.blocklog.cache.model.common.Interaction;
import net.zytorx.minecraft.blocklog.cache.model.common.OldNewTuple;
import net.zytorx.minecraft.blocklog.commands.filter.Filter;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Stream;

public class CommandsCache {

    private static final SimpleDateFormat format = new SimpleDateFormat("dd.MM.yy-hh:mm:ss");
    private static HashMap<Filter, Map<Integer, List<String>>> blockLogCache = new HashMap<>();
    private static HashMap<Filter, Integer> blockLogCounter = new HashMap<>();

    public static List<String> loadBlockLogCache(Filter filter, int page) {
        if (!blockLogCache.containsKey(filter)) {
            var toAdd = new ArrayList<String>();
            var interactions = BlockLog.CACHE != null ? BlockLog.CACHE.getInteractions() : Stream.<Interaction>empty();
            var filtered = filter.filter(interactions);
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
            blockLogCache.put(filter, pageMap);
            blockLogCounter.put(filter, 0);
        }
        var paged = blockLogCache.get(filter);

        return paged.get(paged.containsKey(page) ? page : 0);
    }

    private static void interactionToString(Interaction toConvert, List<String> strings) {
        var time = format.format(new Date(toConvert.getTime()));
        var entity = loadName(toConvert.getEntityID(), toConvert.getEntityName());
        var id = toConvert.getId();
        var template = "§bid§f: §3" + id + " §f: §b" + time + "§f: §3" + entity + " {action} §b(§a{x}§b, §a{y}§b, §a{z}§b)";
        if (toConvert instanceof BlockInteraction singleBlock) {
            strings.add(convert(template, singleBlock.getBlock(), singleBlock.getX(), singleBlock.getY(), singleBlock.getZ()));
        }
    }

    private static String convert(String template, OldNewTuple tuple, int x, int y, int z) {
        var oldState = InteractionUtils.readBlockState(tuple.getOldState());
        var newState = InteractionUtils.readBlockState(tuple.getNewState());
        if (newState.isAir()) {
            template = template.replace("{action}", "§cbroke §3{old}");
        } else if (oldState.isAir()) {
            template = template.replace("{action}", "§cplaced §3{new}");
        } else {
            template = template.replace("{action}", "§creplaced §3{old} §cwith §3{new}");
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
        var id1 = i1.getId();
        var id2 = i2.getId();
        return id1.length() == id2.length() ? -id1.compareTo(id2) : id2.length() - id1.length();
    }

    static void clearCache() {
        blockLogCounter = new HashMap<>();
        blockLogCache = new HashMap<>();
    }

    public static void deleteOverhead() {
        var toRemove = new ArrayList<Filter>();
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
