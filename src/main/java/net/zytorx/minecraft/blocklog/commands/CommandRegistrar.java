package net.zytorx.minecraft.blocklog.commands;

import net.minecraftforge.event.RegisterCommandsEvent;

public class CommandRegistrar {
    private CommandRegistrar() {
    }

    public static void register(RegisterCommandsEvent event) {
        new BlockLogCommand(event.getDispatcher());
    }
}
