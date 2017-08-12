package me.kenzierocks.hardvox.commands.args;

import static com.google.common.collect.ImmutableBiMap.toImmutableBiMap;

import java.util.Iterator;
import java.util.Locale;
import java.util.function.Function;
import java.util.stream.Stream;

import com.google.common.base.Joiner;
import com.google.common.collect.BiMap;
import com.google.common.collect.ImmutableBiMap;

public class EnumArg<E extends Enum<E>> extends BaseArg<E> {

    public static <E extends Enum<E>> EnumArg<E> create(Class<E> enumClass) {
        BiMap<E, String> arguments = getArguments(enumClass, e -> e.name().toLowerCase(Locale.ENGLISH));
        return new EnumArg<>(arguments, Joiner.on('|').join(arguments.values()));
    }

    public static <E extends Enum<E>> EnumArg<E> create(Class<E> enumClass, String name) {
        return create(enumClass, name, e -> e.name().toLowerCase(Locale.ENGLISH));
    }

    public static <E extends Enum<E>> EnumArg<E> create(Class<E> enumClass, String name, Function<E, String> argFunc) {
        BiMap<E, String> arguments = getArguments(enumClass, argFunc);
        return new EnumArg<>(arguments, name);
    }

    private static <E extends Enum<E>> BiMap<E, String> getArguments(Class<E> enumClass, Function<E, String> argFunc) {
        return Stream.of(enumClass.getEnumConstants())
                .collect(toImmutableBiMap(Function.identity(), argFunc));
    }

    private final BiMap<E, String> args;

    private EnumArg(BiMap<E, String> args, String name) {
        super(name);
        this.args = ImmutableBiMap.copyOf(args);
    }

    @Override
    public E convert(ArgumentContext context) {
        return args.inverse().get(context.text);
    }

    @Override
    public boolean validText(ArgumentContext context) {
        return args.values().stream().filter(s -> s.startsWith(context.text)).count() > 0;
    }

    @Override
    public Iterator<String> getCompletions(ArgumentContext context) {
        return args.values().stream().filter(s -> s.startsWith(context.text)).iterator();
    }

}
