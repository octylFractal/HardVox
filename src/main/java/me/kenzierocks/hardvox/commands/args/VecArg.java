package me.kenzierocks.hardvox.commands.args;

import java.util.Iterator;
import java.util.List;
import java.util.function.Function;

import com.flowpowered.math.vector.VectorNi;
import com.flowpowered.math.vector.Vectori;
import com.google.common.base.Splitter;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import com.google.common.collect.Iterators;

class VecArg<V extends Vectori> extends BaseArg<V> {

    private static final ImmutableList<String> COMMA = ImmutableList.of(",");
    private static final Splitter COMMA_SPLITTER = Splitter.on(',').limit(2);

    private final IntArg delegate = new IntArg("");
    private final int dimensions;
    private final Function<VectorNi, V> vectorConstructor;

    VecArg(String name, int dimensions, Function<VectorNi, V> vectorConstructor) {
        super(name);
        this.dimensions = dimensions;
        this.vectorConstructor = vectorConstructor;
    }

    @Override
    public V convert(ArgumentContext context) {
        VectorNi vec = new VectorNi(dimensions);
        Iterator<String> split = COMMA_SPLITTER.limit(dimensions).split(context.text).iterator();
        for (int i = 0; split.hasNext(); i++) {
            vec.set(i, delegate.convert(context.withText(split.next())));
        }
        return vectorConstructor.apply(vec);
    }

    @Override
    public boolean validText(ArgumentContext context) {
        return Iterables.all(COMMA_SPLITTER.limit(dimensions).split(context.text), number -> delegate.validText(context.withText(number)));
    }

    @Override
    public Iterator<String> getCompletions(ArgumentContext context) {
        String textSoFar = context.text;
        List<String> sections = COMMA_SPLITTER.splitToList(textSoFar);
        String lastSection = Iterables.getLast(sections);
        String prependText = String.join(",", sections.subList(0, sections.size() - 1));
        Iterator<String> integer = Iterators.transform(delegate.getCompletions(context.withText(lastSection)), prependText::concat);
        if (textSoFar.isEmpty() || textSoFar.endsWith(",") || textSoFar.codePoints().filter(cp -> cp == ',').count() >= (dimensions - 1)) {
            return integer;
        }
        return Iterators.concat(integer, Iterators.transform(COMMA.iterator(), textSoFar::concat));
    }

}
