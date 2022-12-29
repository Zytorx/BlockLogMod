package net.zytorx.minecraft.blocklog.logging.filter;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import net.minecraft.commands.CommandSourceStack;
import net.zytorx.minecraft.blocklog.commands.Utils;

import java.util.Collection;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class FilterArgumentType implements ArgumentType<Filter> {

    private static final Collection<String> EXAMPLES = List.of("e:Notch r:80", "e:Mob.Creeper", "r:10 x:69 y:42 z:0");

    @Override
    public Filter parse(StringReader reader) throws CommandSyntaxException {
        var args = reader.getRemaining().split(" ");
        var x = Integer.MIN_VALUE;
        var y = Integer.MIN_VALUE;
        var z = Integer.MIN_VALUE;
        var radius = 0;
        var entity = "";
        UUID player = null;
        for (var arg : args) {
            if (arg.length() < 3) {
                break;
            }
            var filterMap = arg.split(":");
            if (filterMap.length != 2 || filterMap[0].length() != 1) {
                break;
            }
            var valid = switch (filterMap[0].toLowerCase()) {
                case "x" -> {
                    if (x == Integer.MIN_VALUE) {
                        try {
                            x = Integer.parseInt(filterMap[1]);
                            yield true;
                        } catch (Exception ignored) {
                        }
                    }
                    yield false;
                }
                case "y" -> {
                    if (y == Integer.MIN_VALUE) {
                        try {
                            y = Integer.parseInt(filterMap[1]);
                            yield true;
                        } catch (Exception ignored) {
                        }
                    }
                    yield false;
                }
                case "z" -> {
                    if (z == Integer.MIN_VALUE) {
                        try {
                            z = Integer.parseInt(filterMap[1]);
                            yield true;
                        } catch (Exception ignored) {
                        }
                    }
                    yield false;
                }
                case "r" -> {
                    if (radius == 0) {
                        try {
                            radius = Integer.parseUnsignedInt(filterMap[1]);
                            yield true;
                        } catch (Exception ignored) {
                        }
                    }
                    yield false;
                }
                case "e" -> {
                    if (entity.isEmpty() && player == null) {
                        if (filterMap[1].startsWith("Mob.")) {
                            entity = filterMap[1];
                            yield true;
                        }
                        var id = Utils.getUuid(filterMap[1]);
                        if (id == null) {
                            entity = filterMap[1];
                        } else {
                            player = id;
                        }
                        yield true;
                    }
                    yield false;
                }
                default -> false;
            };
            if (valid) {
                reader.setCursor(reader.getCursor() + arg.length() + 1);
            } else {
                break;
            }
        }
        return new Filter(x, y, z, radius, entity, player);
    }

    @Override
    public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
        var s = context.toString();
        return null;
    }

    @Override
    public Collection<String> getExamples() {
        return EXAMPLES;
    }

    public static Filter getFilter(CommandContext<CommandSourceStack> context, String name) {
        var filter = context.getArgument(name, Filter.class);
        var entity = context.getSource().getEntity();
        if (entity != null) {
            filter.setPos(entity.getOnPos());
        }
        return filter;
    }
}
