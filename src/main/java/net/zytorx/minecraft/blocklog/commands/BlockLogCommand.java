package net.zytorx.minecraft.blocklog.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.server.level.ServerPlayer;

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
                .then(literal("reload").executes(ignored -> {
                    CommandsCache.clearCache();
                    return 0;
                })));
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

    private static String getEntityOrDefault(CommandContext<CommandSourceStack> context) {
        try {
            return EntityArgument.getPlayer(context, "player").getStringUUID();
        } catch (Exception e1) {
            try {
                return StringArgumentType.getString(context, "entity");
            } catch (Exception e2) {
                return null;
            }
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
