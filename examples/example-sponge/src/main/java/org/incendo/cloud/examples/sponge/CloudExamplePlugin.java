//
// MIT License
//
// Copyright (c) 2024 Incendo
//
// Permission is hereby granted, free of charge, to any person obtaining a copy
// of this software and associated documentation files (the "Software"), to deal
// in the Software without restriction, including without limitation the rights
// to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
// copies of the Software, and to permit persons to whom the Software is
// furnished to do so, subject to the following conditions:
//
// The above copyright notice and this permission notice shall be included in all
// copies or substantial portions of the Software.
//
// THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
// IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
// FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
// AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
// LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
// OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
// SOFTWARE.
//
package org.incendo.cloud.examples.sponge;

import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.TypeLiteral;
import io.leangen.geantyref.TypeToken;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.incendo.cloud.Command;
import org.incendo.cloud.component.DefaultValue;
import org.incendo.cloud.context.CommandContext;
import org.incendo.cloud.description.Description;
import org.incendo.cloud.execution.ExecutionCoordinator;
import org.incendo.cloud.minecraft.extras.MinecraftExceptionHandler;
import org.incendo.cloud.parser.ArgumentParseResult;
import org.incendo.cloud.parser.standard.StringParser;
import org.incendo.cloud.permission.PredicatePermission;
import org.incendo.cloud.sponge.CloudInjectionModule;
import org.incendo.cloud.sponge.SpongeCommandManager;
import org.incendo.cloud.sponge.data.BlockInput;
import org.incendo.cloud.sponge.data.BlockPredicate;
import org.incendo.cloud.sponge.data.ItemStackPredicate;
import org.incendo.cloud.sponge.data.MultipleEntitySelector;
import org.incendo.cloud.sponge.data.ProtoItemStack;
import org.incendo.cloud.sponge.data.SinglePlayerSelector;
import org.incendo.cloud.sponge.exception.ComponentMessageRuntimeException;
import org.spongepowered.api.ResourceKey;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandCause;
import org.spongepowered.api.command.parameter.managed.operator.Operator;
import org.spongepowered.api.command.parameter.managed.operator.Operators;
import org.spongepowered.api.data.Keys;
import org.spongepowered.api.data.persistence.DataContainer;
import org.spongepowered.api.data.type.ProfessionType;
import org.spongepowered.api.data.type.VillagerType;
import org.spongepowered.api.effect.sound.SoundType;
import org.spongepowered.api.entity.EntityType;
import org.spongepowered.api.entity.EntityTypes;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.entity.living.player.User;
import org.spongepowered.api.entity.living.trader.Villager;
import org.spongepowered.api.item.enchantment.Enchantment;
import org.spongepowered.api.item.enchantment.EnchantmentType;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.item.inventory.Slot;
import org.spongepowered.api.item.inventory.entity.Hotbar;
import org.spongepowered.api.item.inventory.transaction.InventoryTransactionResult;
import org.spongepowered.api.registry.RegistryHolder;
import org.spongepowered.api.registry.RegistryTypes;
import org.spongepowered.api.world.DefaultWorldKeys;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.biome.Biome;
import org.spongepowered.api.world.schematic.PaletteTypes;
import org.spongepowered.api.world.server.ServerLocation;
import org.spongepowered.api.world.server.ServerWorld;
import org.spongepowered.math.vector.Vector3d;
import org.spongepowered.math.vector.Vector3i;
import org.spongepowered.plugin.builtin.jvm.Plugin;

import static net.kyori.adventure.text.Component.newline;
import static net.kyori.adventure.text.Component.space;
import static net.kyori.adventure.text.Component.text;
import static net.kyori.adventure.text.format.NamedTextColor.AQUA;
import static net.kyori.adventure.text.format.NamedTextColor.BLUE;
import static net.kyori.adventure.text.format.NamedTextColor.GRAY;
import static net.kyori.adventure.text.format.NamedTextColor.GREEN;
import static net.kyori.adventure.text.format.NamedTextColor.LIGHT_PURPLE;
import static net.kyori.adventure.text.format.NamedTextColor.RED;
import static net.kyori.adventure.text.format.TextColor.color;
import static org.incendo.cloud.parser.standard.DoubleParser.doubleParser;
import static org.incendo.cloud.parser.standard.IntegerParser.integerParser;
import static org.incendo.cloud.parser.standard.StringParser.greedyStringParser;
import static org.incendo.cloud.parser.standard.StringParser.stringParser;
import static org.incendo.cloud.sponge.parser.BlockInputParser.blockInputParser;
import static org.incendo.cloud.sponge.parser.BlockPredicateParser.blockPredicateParser;
import static org.incendo.cloud.sponge.parser.DataContainerParser.dataContainerParser;
import static org.incendo.cloud.sponge.parser.ItemStackPredicateParser.itemStackPredicateParser;
import static org.incendo.cloud.sponge.parser.MultipleEntitySelectorParser.multipleEntitySelectorParser;
import static org.incendo.cloud.sponge.parser.NamedTextColorParser.namedTextColorParser;
import static org.incendo.cloud.sponge.parser.OperatorParser.operatorParser;
import static org.incendo.cloud.sponge.parser.ProtoItemStackParser.protoItemStackParser;
import static org.incendo.cloud.sponge.parser.RegistryEntryParser.registryEntryParser;
import static org.incendo.cloud.sponge.parser.SinglePlayerSelectorParser.singlePlayerSelectorParser;
import static org.incendo.cloud.sponge.parser.UserParser.userParser;
import static org.incendo.cloud.sponge.parser.Vector3dParser.vector3dParser;
import static org.incendo.cloud.sponge.parser.Vector3iParser.vector3iParser;
import static org.incendo.cloud.sponge.parser.WorldParser.worldParser;

@Plugin("cloud-example-sponge")
public final class CloudExamplePlugin {

    private static final Component COMMAND_PREFIX = text()
        .color(color(0x333333))
        .content("[")
        .append(text("Cloud-Sponge", color(0xF7CF0D)))
        .append(text(']'))
        .build();

    private final SpongeCommandManager<CommandCause> commandManager;

    /**
     * Create example plugin instance
     *
     * @param injector injector
     */
    @Inject
    public CloudExamplePlugin(final @NonNull Injector injector) {
        // Create child injector with cloud module
        final Injector childInjector = injector.createChildInjector(
            CloudInjectionModule.createNative(ExecutionCoordinator.simpleCoordinator())
        );

        // Get command manager instance
        this.commandManager = childInjector.getInstance(Key.get(new TypeLiteral<>() {}));

        // Register minecraft-extras exception handlers
        MinecraftExceptionHandler.create(CommandCause::audience)
            .defaultHandlers()
            .decorator(message -> Component.text().append(COMMAND_PREFIX, space(), message).build())
            .registerTo(this.commandManager);

        this.registerCommands();
    }

    private void registerCommands() {
        this.commandManager.command(this.commandManager.commandBuilder("cloud_test1")
            .permission("cloud.test1")
            .handler(ctx -> ctx.sender().audience().sendMessage(text("success"))));
        this.commandManager.command(this.commandManager.commandBuilder("cloud_test2")
            .literal("test")
            .literal("test1")
            .handler(ctx -> ctx.sender().audience().sendMessage(text("success"))));
        final Command.Builder<CommandCause> cloudTest3 = this.commandManager.commandBuilder("cloud_test3");
        final Command.Builder<CommandCause> test = cloudTest3.literal("test");
        this.commandManager.command(test.required("string_arg", stringParser())
            .literal("test2")
            .handler(ctx -> ctx.sender().audience().sendMessage(text("success"))));
        this.commandManager.command(test.literal("literal_arg")
            .handler(ctx -> ctx.sender().audience().sendMessage(text("success"))));
        this.commandManager.command(cloudTest3.literal("another_test")
            .handler(ctx -> ctx.sender().audience().sendMessage(text("success"))));
        final Command.Builder<CommandCause> cloud = this.commandManager.commandBuilder("cloud");
        this.commandManager.command(cloud.literal("string_test")
            .required("single", stringParser(StringParser.StringMode.SINGLE))
            .required("quoted", stringParser(StringParser.StringMode.QUOTED))
            .required("greedy", stringParser(StringParser.StringMode.GREEDY))
            .handler(ctx -> ctx.sender().audience().sendMessage(text("success"))));
        this.commandManager.command(cloud.literal("int_test")
            .required("any", integerParser())
            .required("gt0", integerParser(1))
            .required("lt100", integerParser(Integer.MIN_VALUE, 99))
            .required("5to20", integerParser(5, 20))
            .handler(ctx -> ctx.sender().audience().sendMessage(text("success"))));
        this.commandManager.command(cloud.literal("enchantment_type_test")
            .required("enchantment_type", registryEntryParser(EnchantmentType.class, RegistryTypes.ENCHANTMENT_TYPE))
            .optional("level", integerParser(), DefaultValue.constant(1))
            .handler(ctx -> {
                final Object subject = ctx.sender().subject();
                if (!(subject instanceof Player)) {
                    ctx.sender().audience().sendMessage(text("This command is for players only!", RED));
                    return;
                }
                final Player player = (Player) subject;
                final Hotbar hotbar = player.inventory().hotbar();
                final int index = hotbar.selectedSlotIndex();
                final Slot slot = hotbar.slot(index).get();
                final InventoryTransactionResult.Poll result = slot.poll();
                if (result.type() != InventoryTransactionResult.Type.SUCCESS) {
                    player.sendMessage(text("You must hold an item to enchant!", RED));
                    return;
                }
                final ItemStack modified = ItemStack.builder()
                    .fromItemStack(result.polledItem().createStack())
                    .add(Keys.APPLIED_ENCHANTMENTS, List.of(
                        Enchantment.of(
                            ctx.<EnchantmentType>get("enchantment_type"),
                            ctx.<Integer>get("level")
                        )
                    ))
                    .build();
                slot.set(modified);
            }));
        this.commandManager.command(cloud.literal("color_test")
            .required("color", namedTextColorParser())
            .required("message", greedyStringParser())
            .handler(ctx -> {
                ctx.sender().audience().sendMessage(
                    text(ctx.get("message"), ctx.<NamedTextColor>get("color"))
                );
            }));
        this.commandManager.command(cloud.literal("operator_test")
            .required("first", integerParser())
            .required("operator", operatorParser())
            .required("second", integerParser())
            .handler(ctx -> {
                final int first = ctx.get("first");
                final int second = ctx.get("second");
                final Operator operator = ctx.get("operator");
                if (!(operator instanceof Operator.Simple)) {
                    ctx.sender().audience().sendMessage(
                        text("That type of operator is not applicable here!", RED)
                    );
                    return;
                }
                ctx.sender().audience().sendMessage(text()
                    .color(AQUA)
                    .append(text(first))
                    .append(space())
                    .append(text(operator.asString(), BLUE))
                    .append(space())
                    .append(text(second))
                    .append(space())
                    .append(text('→', BLUE))
                    .append(space())
                    .append(text(((Operator.Simple) operator).apply(first, second)))
                );
            }));
        this.commandManager.command(cloud.literal("modifylevel")
            .required("operator", operatorParser())
            .required("value", doubleParser())
            .handler(ctx -> {
                final Object subject = ctx.sender().subject();
                if (!(subject instanceof Player)) { // todo: a solution to this
                    ctx.sender().audience().sendMessage(text("This command is for players only!", RED));
                    return;
                }
                final Player player = (Player) subject;
                final Operator operator = ctx.get("operator");
                final double value = ctx.get("value");
                if (operator == Operators.ASSIGN.get()) {
                    player.offer(Keys.EXPERIENCE, (int) value);
                    return;
                }
                if (!(operator instanceof Operator.Simple)) {
                    ctx.sender().audience().sendMessage(
                        text("That type of operator is not applicable here!", RED)
                    );
                    return;
                }
                final int currentXp = player.get(Keys.EXPERIENCE).get();
                player.offer(Keys.EXPERIENCE, (int) ((Operator.Simple) operator).apply(currentXp, value));
            }));
        this.commandManager.command(cloud.literal("selectplayer")
            .required("player", singlePlayerSelectorParser())
            .handler(ctx -> {
                final Player player = ctx.<SinglePlayerSelector>get("player").getSingle();
                ctx.sender().audience().sendMessage(Component.text().append(
                    text("Display name of selected player: ", GRAY),
                    player.displayName().get()
                ).build());
            }));
        this.commandManager.command(cloud.literal("world_test")
            .required("world", worldParser())
            .handler(ctx -> {
                ctx.sender().audience().sendMessage(text(ctx.<ServerWorld>get("world").key().asString()));
            }));
        this.commandManager.command(cloud.literal("test_item")
            .required("item", protoItemStackParser())
            .literal("is")
            .required("predicate", itemStackPredicateParser())
            .handler(ctx -> {
                final ItemStack item = ctx.<ProtoItemStack>get("item").createItemStack(1, true);
                final ItemStackPredicate predicate = ctx.get("predicate");
                final Component message = text(builder -> {
                    builder.append(item.get(Keys.DISPLAY_NAME).orElse(item.type().asComponent()))
                        .append(space());
                    if (predicate.test(item)) {
                        builder.append(text("passes!", GREEN));
                        return;
                    }
                    builder.append(text("does not pass!", RED));
                });
                ctx.sender().audience().sendMessage(message);
            }));
        this.commandManager.command(cloud.literal("test_entity_type")
            .required("type", registryEntryParser(new TypeToken<>() {}, RegistryTypes.ENTITY_TYPE))
            .handler(ctx -> ctx.sender().audience().sendMessage(ctx.<EntityType<?>>get("type"))));
        final Function<CommandContext<CommandCause>, RegistryHolder> holderFunction = ctx -> ctx.sender()
            .location()
            .map(Location::world)
            .orElseGet(() -> Sponge.server().worldManager().world(DefaultWorldKeys.DEFAULT).orElseThrow());
        this.commandManager.command(cloud.literal("test_biomes")
            .required("biome", registryEntryParser(Biome.class, RegistryTypes.BIOME, holderFunction))
            .handler(ctx -> {
                final ResourceKey biomeKey = holderFunction.apply(ctx)
                    .registry(RegistryTypes.BIOME)
                    .findValueKey(ctx.get("biome"))
                    .orElseThrow(IllegalStateException::new);
                ctx.sender().audience().sendMessage(text(biomeKey.asString()));
            }));
        this.commandManager.command(cloud.literal("test_sounds")
            .required("type", registryEntryParser(SoundType.class, RegistryTypes.SOUND_TYPE))
            .handler(ctx -> {
                ctx.sender().audience().sendMessage(text(ctx.<SoundType>get("type").key().asString()));
            }));
        this.commandManager.command(cloud.literal("summon_villager")
            .required("type", registryEntryParser(VillagerType.class, RegistryTypes.VILLAGER_TYPE))
            .required("profession", registryEntryParser(ProfessionType.class, RegistryTypes.PROFESSION_TYPE))
            .handler(ctx -> {
                final ServerLocation loc = ctx.sender().location().orElse(null);
                if (loc == null) {
                    ctx.sender().audience().sendMessage(text("No location!"));
                    return;
                }
                final ServerWorld world = loc.world();
                final Villager villager = world.createEntity(EntityTypes.VILLAGER, loc.position());
                villager.offer(Keys.VILLAGER_TYPE, ctx.get("type"));
                villager.offer(Keys.PROFESSION_TYPE, ctx.get("profession"));
                if (world.spawnEntity(villager)) {
                    ctx.sender().audience().sendMessage(text()
                        .append(text("Spawned entity!", GREEN))
                        .append(space())
                        .append(villager.displayName().get())
                        .hoverEvent(villager));
                } else {
                    ctx.sender().audience().sendMessage(text("failed to spawn :("));
                }
            }));
        this.commandManager.command(cloud.literal("vec3d")
            .required("vec3d", vector3dParser())
            .handler(ctx -> {
                ctx.sender().audience().sendMessage(text(ctx.<Vector3d>get("vec3d").toString()));
            }));
        this.commandManager.command(cloud.literal("selectentities")
            .required("selector", multipleEntitySelectorParser())
            .handler(ctx -> {
                final MultipleEntitySelector selector = ctx.get("selector");
                ctx.sender().audience().sendMessage(Component.text().append(
                    text("Using selector: ", BLUE),
                    text(selector.inputString()),
                    newline(),
                    text("Selected: ", LIGHT_PURPLE),
                    selector.get().stream()
                        .map(e -> e.displayName().get())
                        .collect(Component.toComponent(text(", ", GRAY)))
                ).build());
            }));

        this.commandManager.command(cloud.literal("user")
            .required("user", userParser())
            .handler(ctx -> {
                ctx.sender().audience().sendMessage(text(ctx.<User>get("user").toString()));
            }));
        this.commandManager.command(cloud.literal("data")
            .required("data", dataContainerParser())
            .handler(ctx -> {
                ctx.sender().audience().sendMessage(text(ctx.<DataContainer>get("data").toString()));
            }));
        this.commandManager.command(cloud.literal("setblock")
            .permission("cloud.setblock")
            .required("position", vector3iParser())
            .required("block", blockInputParser())
            .handler(ctx -> {
                final Vector3i position = ctx.get("position");
                final BlockInput input = ctx.get("block");
                final Optional<ServerLocation> location = ctx.sender().location();
                if (location.isPresent()) {
                    final ServerWorld world = location.get().world();
                    input.place(world.location(position));
                    ctx.sender().audience().sendMessage(text("set block!"));
                } else {
                    ctx.sender().audience().sendMessage(text("no location!"));
                }
            }));
        this.commandManager.command(cloud.literal("blockinput")
            .required("block", blockInputParser())
            .handler(ctx -> {
                final BlockInput input = ctx.get("block");
                ctx.sender().audience().sendMessage(text(
                    PaletteTypes.BLOCK_STATE_PALETTE.get().stringifier()
                        .apply(RegistryTypes.BLOCK_TYPE.get(), input.blockState())
                ));
            }));
        this.commandManager.command(this.commandManager.commandBuilder("gib")
            .permission("cloud.gib")
            .requiredArgumentPair(
                "itemstack",
                TypeToken.get(ItemStack.class),
                "item", protoItemStackParser(),
                "amount", integerParser(),
                (sender, proto, amount) -> {
                    try {
                        return ArgumentParseResult.successFuture(
                            proto.createItemStack(amount, true)
                        );
                    } catch (final ComponentMessageRuntimeException e) {
                        return ArgumentParseResult.failureFuture(e);
                    }
                },
                Description.of("The ItemStack to give")
            )
            .handler(ctx -> ((Player) ctx.sender().subject()).inventory().offer(ctx.<ItemStack>get("itemstack"))));
        this.commandManager.command(cloud.literal("replace")
            .permission(PredicatePermission.of(cause -> {
                // works but error message is ugly
                // todo: cause.cause().root() returns DedicatedServer during permission checks?
                return cause.subject() instanceof Player;
            }))
            .required("predicate", blockPredicateParser())
            .required("radius", integerParser())
            .required("replacement", blockInputParser())
            .handler(ctx -> {
                final BlockPredicate predicate = ctx.get("predicate");
                final int radius = ctx.get("radius");
                final BlockInput replacement = ctx.get("replacement");

                // its a player so get is fine
                final ServerLocation loc = ctx.sender().location().get();
                final ServerWorld world = loc.world();
                final Vector3d vec = loc.position();

                for (double x = vec.x() - radius; x < vec.x() + radius; x++) {
                    for (double y = vec.y() - radius; y < vec.y() + radius; y++) {
                        for (double z = vec.z() - radius; z < vec.z() + radius; z++) {
                            final ServerLocation location = world.location(x, y, z);
                            if (predicate.test(location)) {
                                location.setBlock(replacement.blockState());
                            }
                        }
                    }
                }
            }));
    }

}
