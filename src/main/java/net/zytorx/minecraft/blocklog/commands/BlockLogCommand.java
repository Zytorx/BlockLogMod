package net.zytorx.minecraft.blocklog.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.server.level.ServerPlayer;
import net.zytorx.minecraft.blocklog.BlockLog;
import net.zytorx.minecraft.blocklog.cache.model.InteractionUtils;
import net.zytorx.minecraft.blocklog.cache.model.blocks.BlockInteraction;

import static net.minecraft.commands.Commands.argument;
import static net.minecraft.commands.Commands.literal;


public class BlockLogCommand {


    public BlockLogCommand(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(literal("blocklog")
                .requires(r -> r.hasPermission(4) && r.getEntity() instanceof ServerPlayer)
                .then(literal("list").executes(BlockLogCommand::list)
                        .then(argument("page", IntegerArgumentType.integer(0)).executes(BlockLogCommand::list))
                        .then(argument("entity", StringArgumentType.word()).executes(BlockLogCommand::list)
                                .then(argument("page", IntegerArgumentType.integer(0)).executes(BlockLogCommand::list))))
                .then(literal("reload").executes(context -> {
                    CommandsCache.clearCache();
                    context.getSource().sendSuccess(new TextComponent("Sucessfully reloaded"), false);
                    return 0;
                }))
                .then(literal("revert").then(argument("id", StringArgumentType.word()).executes(BlockLogCommand::revert))));
    }


    private static int list(CommandContext<CommandSourceStack> context) {
        var entity = getEntityOrDefault(context);
        var page = getIntOrDefault(context, "page");
        var source = context.getSource();

        var log = CommandsCache.loadBlockLogCache(entity, page);
        for (var text : log) {
            source.sendSuccess(new TextComponent(text), false);
        }

        return log.size();
    }

    private static int revert(CommandContext<CommandSourceStack> context) {
        var id = StringArgumentType.getString(context, "id");
        if (BlockLog.CACHE == null) {
            context.getSource().sendSuccess(new TextComponent("Couldnt find the given id"), false);
            return 0;
        }

        var toRevert = BlockLog.CACHE.removeInteraction(id);
        if (toRevert == null) {
            context.getSource().sendSuccess(new TextComponent("Couldnt find the given id"), false);
            return 0;
        }
        if (toRevert instanceof BlockInteraction block) {
            context.getSource().sendSuccess(new TextComponent("Successfully reverted block"), false);
            return revertBlock(context, block);
        }
        return 0;
    }

    private static int revertBlock(CommandContext<CommandSourceStack> context, BlockInteraction interaction) {

        var dim = InteractionUtils.getDimensionKey(interaction);
        var server = context.getSource().getServer();
        var level = server.getLevel(dim);
        if (level == null) {
            return 0;
        }
        var pos = new BlockPos(interaction.getX(), interaction.getY(), interaction.getZ());
        var state = InteractionUtils.readBlockState(interaction.getBlock().getOldState());
        level.setBlockAndUpdate(pos, state);

        return 0;
    }

    private static String getEntityOrDefault(CommandContext<CommandSourceStack> context) {
        try {
            return StringArgumentType.getString(context, "entity");
        } catch (Exception e2) {
            return null;
        }
    }

    private static int getIntOrDefault(CommandContext<CommandSourceStack> context, String name) {
        try {
            return IntegerArgumentType.getInteger(context, name);
        } catch (Exception e) {
            return 0;
        }
    }
}
