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
package cloud.commandframework.sponge.parser;

import cloud.commandframework.sponge.NodeSource;
import java.util.concurrent.CompletableFuture;
import net.minecraft.commands.arguments.CompoundTagArgument;
import net.minecraft.nbt.CompoundTag;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.incendo.cloud.brigadier.parser.WrappedBrigadierParser;
import org.incendo.cloud.context.CommandContext;
import org.incendo.cloud.context.CommandInput;
import org.incendo.cloud.parser.ArgumentParseResult;
import org.incendo.cloud.parser.ArgumentParser;
import org.incendo.cloud.parser.ParserDescriptor;
import org.incendo.cloud.suggestion.Suggestion;
import org.incendo.cloud.suggestion.SuggestionProvider;
import org.spongepowered.api.command.registrar.tree.CommandTreeNode;
import org.spongepowered.api.command.registrar.tree.CommandTreeNodeTypes;
import org.spongepowered.api.data.persistence.DataContainer;
import org.spongepowered.common.data.persistence.NBTTranslator;

/**
 * Argument for parsing {@link DataContainer DataContainers} from
 * <a href="https://minecraft.fandom.com/wiki/NBT_format">SNBT</a> strings.
 *
 * @param <C> command sender type
 */
public final class DataContainerParser<C> implements ArgumentParser.FutureArgumentParser<C, DataContainer>,
    NodeSource, SuggestionProvider<C> {

    /**
     * Creates a new {@link DataContainerParser}.
     *
     * @param <C> command sender type
     * @return new parser
     */
    public static <C> ParserDescriptor<C, DataContainer> dataContainerParser() {
        return ParserDescriptor.of(new DataContainerParser<>(), DataContainer.class);
    }

    private final ArgumentParser<C, DataContainer> mappedParser =
        new WrappedBrigadierParser<C, CompoundTag>(CompoundTagArgument.compoundTag())
            .flatMapSuccess((ctx, compoundTag) ->
                ArgumentParseResult.successFuture(NBTTranslator.INSTANCE.translate(compoundTag)));

    @Override
    public @NonNull CompletableFuture<ArgumentParseResult<@NonNull DataContainer>> parseFuture(
        final @NonNull CommandContext<@NonNull C> commandContext,
        final @NonNull CommandInput inputQueue
    ) {
        return this.mappedParser.parseFuture(commandContext, inputQueue);
    }

    @Override
    public @NonNull CompletableFuture<@NonNull Iterable<@NonNull Suggestion>> suggestionsFuture(
        final @NonNull CommandContext<C> context,
        final @NonNull CommandInput input
    ) {
        return this.mappedParser.suggestionProvider().suggestionsFuture(context, input);
    }

    @Override
    public CommandTreeNode.@NonNull Argument<? extends CommandTreeNode.Argument<?>> node() {
        return CommandTreeNodeTypes.NBT_COMPOUND_TAG.get().createNode();
    }

}
