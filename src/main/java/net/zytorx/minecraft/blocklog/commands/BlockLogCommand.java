package net.zytorx.minecraft.blocklog.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;

import static net.minecraft.commands.Commands.argument;
import static net.minecraft.commands.Commands.literal;


public class BlockLogCommand {


    public BlockLogCommand(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(literal("blocklog")
                .requires(r -> r.hasPermission(4) && r.getEntity() instanceof ServerPlayer)
                .then(literal("list").executes(BlockLogCommand::list)
                        .then(argument("player", EntityArgument.player()).executes(BlockLogCommand::list)
                                .then(argument("page", IntegerArgumentType.integer(1)).executes(BlockLogCommand::list)))
                        .then(argument("page", IntegerArgumentType.integer(1)).executes(BlockLogCommand::list))));
    }

    private static int list(CommandContext<CommandSourceStack> context) {
        var player = getPlayerOrDefault(context, "player");
        var page = getIntOrDefault(context, "page");
        var source = context.getSource();
        var executer = source.getEntity().getUUID();

        var log = CommandsCache.loadBlockLogCache(executer, player, page);
        for (var text : log) {
            source.sendSuccess(new TextComponent(text), false);
        }

        return log.size();
    }

    private static Player getPlayerOrDefault(CommandContext<CommandSourceStack> context, String name) {
        try {
            return EntityArgument.getPlayer(context, name);
        } catch (Exception e) {
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
