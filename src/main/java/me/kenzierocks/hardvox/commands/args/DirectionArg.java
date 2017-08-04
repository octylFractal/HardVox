package me.kenzierocks.hardvox.commands.args;

import java.util.Iterator;
import java.util.stream.Stream;

import net.minecraft.util.EnumFacing;

class DirectionArg extends BaseArg<EnumFacing> {

    DirectionArg(String name) {
        super(name);
    }

    @Override
    public EnumFacing convert(ArgumentContext context) {
        return EnumFacing.byName(context.text);
    }

    @Override
    public boolean validText(ArgumentContext context) {
        return getNameStream().anyMatch(s -> s.startsWith(context.text));
    }

    @Override
    public Iterator<String> getCompletions(ArgumentContext context) {
        return getNameStream().filter(s -> s.startsWith(context.text)).iterator();
    }

    private Stream<String> getNameStream() {
        return Stream.of(EnumFacing.values()).map(ef -> ef.getName2());
    }

}
