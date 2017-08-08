package me.kenzierocks.hardvox.commands.args;

import java.util.Map;
import java.util.Random;
import java.util.function.Supplier;

import com.google.common.collect.ImmutableMap;

import net.minecraft.block.state.IBlockState;

class RandomPattern implements Supplier<IBlockState> {

    private static final Random RANDOM = new Random();

    private final Map<IBlockState, Double> chanceMap;
    private final double totalChance;

    RandomPattern(Map<IBlockState, Double> chanceMap, double totalChance) {
        this.chanceMap = ImmutableMap.copyOf(chanceMap);
        this.totalChance = totalChance;
    }

    @Override
    public IBlockState get() {
        double r = RANDOM.nextDouble();
        // accum builds to total chance as we go further down the list.
        double accum = 0;

        for (Map.Entry<IBlockState, Double> chance : chanceMap.entrySet()) {
            if (r <= ((accum + chance.getValue()) / totalChance)) {
                return chance.getKey();
            }

            accum += chance.getValue();
        }

        // last ditch effort, return the first one.
        return chanceMap.keySet().iterator().next();
    }

}
