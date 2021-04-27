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
package cloud.commandframework.fabric.argument;

import cloud.commandframework.ArgumentDescription;
import cloud.commandframework.arguments.CommandArgument;
import cloud.commandframework.brigadier.argument.WrappedBrigadierParser;
import cloud.commandframework.context.CommandContext;
import net.minecraft.command.argument.ParticleArgumentType;
import net.minecraft.particle.ParticleEffect;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.util.List;
import java.util.function.BiFunction;

/**
 * An argument for any {@link net.minecraft.particle.ParticleEffect}.
 *
 * @param <C> the sender type
 * @since 1.5.0
 */
public final class ParticleEffectArgument<C> extends CommandArgument<C, ParticleEffect> {

    ParticleEffectArgument(
            final boolean required,
            final @NonNull String name,
            final @NonNull String defaultValue,
            final @Nullable BiFunction<CommandContext<C>, String, List<String>> suggestionsProvider,
            final @NonNull ArgumentDescription defaultDescription
    ) {
        super(
                required,
                name,
                new WrappedBrigadierParser<>(ParticleArgumentType.particle()),
                defaultValue,
                ParticleEffect.class,
                suggestionsProvider,
                defaultDescription
        );
    }

    /**
     * Create a new builder.
     *
     * @param name Name of the argument
     * @param <C>  Command sender type
     * @return Created builder
     * @since 1.5.0
     */
    public static <C> ParticleEffectArgument.@NonNull Builder<C> newBuilder(final @NonNull String name) {
        return new ParticleEffectArgument.Builder<>(name);
    }

    /**
     * Create a new required command argument.
     *
     * @param name Component name
     * @param <C>  Command sender type
     * @return Created argument
     * @since 1.5.0
     */
    public static <C> @NonNull ParticleEffectArgument<C> of(final @NonNull String name) {
        return ParticleEffectArgument.<C>newBuilder(name).asRequired().build();
    }

    /**
     * Create a new optional command argument.
     *
     * @param name Component name
     * @param <C>  Command sender type
     * @return Created argument
     * @since 1.5.0
     */
    public static <C> @NonNull ParticleEffectArgument<C> optional(final @NonNull String name) {
        return ParticleEffectArgument.<C>newBuilder(name).asOptional().build();
    }

    /**
     * Create a new optional command argument with a default value.
     *
     * @param name         Argument name
     * @param defaultValue Default particle effect value
     * @param <C>          Command sender type
     * @return Created argument
     * @since 1.5.0
     */
    public static <C> @NonNull ParticleEffectArgument<C> optional(
            final @NonNull String name,
            final @NonNull ParticleEffect defaultValue
    ) {
        return ParticleEffectArgument.<C>newBuilder(name).asOptionalWithDefault(defaultValue.asString()).build();
    }


    /**
     * Builder for {@link ParticleEffectArgument}.
     *
     * @param <C> sender type
     * @since 1.5.0
     */
    public static final class Builder<C> extends TypedBuilder<C, ParticleEffect, Builder<C>> {

        Builder(final @NonNull String name) {
            super(ParticleEffect.class, name);
        }

        /**
         * Build a new particle effect argument.
         *
         * @return Constructed argument
         * @since 1.5.0
         */
        @Override
        public @NonNull ParticleEffectArgument<C> build() {
            return new ParticleEffectArgument<>(
                    this.isRequired(),
                    this.getName(),
                    this.getDefaultValue(),
                    this.getSuggestionsProvider(),
                    this.getDefaultDescription()
            );
        }

    }

}
