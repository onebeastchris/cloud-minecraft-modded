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
package cloud.commandframework.fabric.argument;

import cloud.commandframework.CommandComponent;
import cloud.commandframework.arguments.parser.ParserDescriptor;
import cloud.commandframework.brigadier.argument.WrappedBrigadierParser;
import net.minecraft.nbt.Tag;
import org.apiguardian.api.API;
import org.checkerframework.checker.nullness.qual.NonNull;

/**
 * An argument for the string representation of an NBT {@link Tag}.
 *
 * @param <C> the sender type
 * @since 2.0.0
 */
public final class NbtTagParser<C> extends WrappedBrigadierParser<C, Tag> {

    /**
     * Creates a new nbt tag parser.
     *
     * @param <C> command sender type
     * @return the created parser
     * @since 2.0.0
     */
    @API(status = API.Status.STABLE, since = "2.0.0")
    public static <C> @NonNull ParserDescriptor<C, Tag> nbtTagParser() {
        return ParserDescriptor.of(new NbtTagParser<>(), Tag.class);
    }

    /**
     * Returns a {@link CommandComponent.Builder} using {@link #nbtTagParser()} as the parser.
     *
     * @param <C> the command sender type
     * @return the component builder
     * @since 2.0.0
     */
    @API(status = API.Status.STABLE, since = "2.0.0")
    public static <C> CommandComponent.@NonNull Builder<C, Tag> nbtTagComponent() {
        return CommandComponent.<C, Tag>builder().parser(nbtTagParser());
    }

    NbtTagParser() {
        super(net.minecraft.commands.arguments.NbtTagArgument.nbtTag());
    }
}
