//
// MIT License
//
// Copyright (c) 2021 Alexander Söderberg & Contributors
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
package cloud.commandframework.sponge.argument;

import cloud.commandframework.ArgumentDescription;
import cloud.commandframework.arguments.CommandArgument;
import cloud.commandframework.arguments.parser.ArgumentParseResult;
import cloud.commandframework.arguments.parser.ArgumentParser;
import cloud.commandframework.brigadier.argument.WrappedBrigadierParser;
import cloud.commandframework.context.CommandContext;
import cloud.commandframework.sponge.NodeSupplyingArgumentParser;
import cloud.commandframework.sponge.SpongeCommandContextKeys;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.arguments.coordinates.ColumnPosArgument;
import net.minecraft.commands.arguments.coordinates.Coordinates;
import net.minecraft.core.BlockPos;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.spongepowered.api.command.registrar.tree.ClientCompletionKeys;
import org.spongepowered.api.command.registrar.tree.CommandTreeNode;
import org.spongepowered.math.vector.Vector2i;

import java.util.List;
import java.util.Queue;
import java.util.function.BiFunction;

public final class Vector2iArgument<C> extends CommandArgument<C, Vector2i> {

    private Vector2iArgument(
            final boolean required,
            final @NonNull String name,
            final @NonNull String defaultValue,
            final @Nullable BiFunction<CommandContext<C>, String, List<String>> suggestionsProvider,
            final @NonNull ArgumentDescription defaultDescription
    ) {
        super(
                required,
                name,
                new Parser<>(),
                defaultValue,
                Vector2i.class,
                suggestionsProvider,
                defaultDescription
        );
    }

    /**
     * Create a new optional {@link Vector2iArgument}.
     *
     * @param name argument name
     * @param <C>  sender type
     * @return a new {@link Vector2iArgument}
     */
    public static <C> @NonNull Vector2iArgument<C> optional(final @NonNull String name) {
        return Vector2iArgument.<C>builder(name).asOptional().build();
    }

    /**
     * Create a new required {@link Vector2iArgument}.
     *
     * @param name argument name
     * @param <C>  sender type
     * @return a new {@link Vector2iArgument}
     */
    public static <C> @NonNull Vector2iArgument<C> of(final @NonNull String name) {
        return Vector2iArgument.<C>builder(name).build();
    }

    /**
     * Create a new {@link Builder}.
     *
     * @param name argument name
     * @param <C>  sender type
     * @return a new {@link Builder}
     */
    public static <C> @NonNull Builder<C> builder(final @NonNull String name) {
        return new Builder<>(name);
    }

    public static final class Parser<C> implements NodeSupplyingArgumentParser<C, Vector2i> {

        private final ArgumentParser<C, Vector2i> mappedParser =
                new WrappedBrigadierParser<C, Coordinates>(ColumnPosArgument.columnPos())
                        .map((ctx, coordinates) -> {
                            final BlockPos pos = coordinates.getBlockPos(
                                    (CommandSourceStack) ctx.get(SpongeCommandContextKeys.COMMAND_CAUSE_KEY)
                            );
                            return ArgumentParseResult.success(new Vector2i(pos.getX(), pos.getZ()));
                        });

        @Override
        public @NonNull ArgumentParseResult<@NonNull Vector2i> parse(
                @NonNull final CommandContext<@NonNull C> commandContext,
                @NonNull final Queue<@NonNull String> inputQueue
        ) {
            return this.mappedParser.parse(commandContext, inputQueue);
        }

        @Override
        public CommandTreeNode.@NonNull Argument<? extends CommandTreeNode.Argument<?>> node() {
            return ClientCompletionKeys.COLUMN_POS.get().createNode();
        }

    }

    public static final class Builder<C> extends TypedBuilder<C, Vector2i, Builder<C>> {

        Builder(final @NonNull String name) {
            super(Vector2i.class, name);
        }

        @Override
        public @NonNull Vector2iArgument<C> build() {
            return new Vector2iArgument<>(
                    this.isRequired(),
                    this.getName(),
                    this.getDefaultValue(),
                    this.getSuggestionsProvider(),
                    this.getDefaultDescription()
            );
        }

    }

}
