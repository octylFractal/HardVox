package me.kenzierocks.hardvox.commands.args;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.function.IntPredicate;
import java.util.function.Supplier;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.common.base.Splitter;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

import me.kenzierocks.hardvox.block.BlockData;
import me.kenzierocks.hardvox.commands.UncheckedWUE;
import me.kenzierocks.hardvox.vector.ProviderVectorMap;
import me.kenzierocks.hardvox.vector.VectorMap;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;

class BlockPatternArg extends BaseArg<VectorMap<BlockData>> {

    private static final IntPredicate BLOCK_ID_CPS = c -> {
        boolean lower = 'a' <= c && c <= 'z';
        boolean upper = 'A' <= c && c <= 'Z';
        boolean number = '0' <= c && c <= '9';
        boolean other = c == ':' || c == '_';
        return lower || upper || number || other;
    };

    // DIGIT+ ('.' DIGIT+)? '%' PATTERN
    private static final Pattern RANDOM_PATTERN = Pattern.compile("(\\d+(?:\\.\\d+)?)%(.+?)", Pattern.CASE_INSENSITIVE);

    BlockPatternArg(String name) {
        super(name);
    }

    @Override
    public VectorMap<BlockData> convert(ArgumentContext context) {
        String text = context.text;
        IBlockState stateConstant = getConstantPatternResult(text);
        if (stateConstant != null) {
            return ProviderVectorMap.single(BlockData.get(stateConstant));
        }
        // try to parse pattern
        Supplier<IBlockState> state = parsePattern(text);
        if (state == null) {
            throw new UncheckedWUE("Block not found: " + text);
        }
        return ProviderVectorMap.from(() -> BlockData.get(state.get()));
    }

    private Supplier<IBlockState> parsePattern(String text) {
        // split the pattern by commas
        Iterator<String> parts = Splitter.on(',').split(text).iterator();
        Set<IBlockState> blocks = new HashSet<>();
        ImmutableMap.Builder<IBlockState, Double> chanceMap = ImmutableMap.builder();
        double totalChance = 0;
        while (parts.hasNext()) {
            String next = parts.next();
            Matcher m = RANDOM_PATTERN.matcher(next);
            if (!m.matches()) {
                return null;
            }
            double chance = Double.parseDouble(m.group(1));
            totalChance += chance;
            if (chance > 100) {
                throw new UncheckedWUE("Random chance must be less than or equal to 100.");
            }
            if (chance == 0) {
                throw new UncheckedWUE("Random chance must be greater than zero.");
            }
            if (totalChance > 100) {
                throw new UncheckedWUE("Total random chance must add up to 100 or less.");
            }
            IBlockState state = getConstantPatternResult(m.group(2));
            if (state == null) {
                throw new UncheckedWUE("Block not found: " + m.group(2));
            }
            if (blocks.contains(state)) {
                throw new UncheckedWUE("Duplicate block entries found.");
            }
            chanceMap.put(state, chance);
            blocks.add(state);
        }
        if (totalChance == 0) {
            throw new UncheckedWUE("Must provide at least one pattern!");
        }
        return new RandomPattern(chanceMap.build(), totalChance);
    }

    private IBlockState getConstantPatternResult(String text) {
        if (text.codePoints().allMatch(c -> '0' <= c && c <= '9')) {
            // blockstate number
            IBlockState stateConstant = Block.getStateById(Integer.parseInt(text));
            // don't accidentally include air!
            if (stateConstant.getBlock() != Blocks.AIR || text.equals("0")) {
                return stateConstant;
            } else {
                // no use doing the other branch here
                return null;
            }
        }
        if (text.codePoints().allMatch(BLOCK_ID_CPS)) {
            // string block ID?
            Block block = Block.getBlockFromName(text);
            if (block != null) {
                return block.getDefaultState();
            }
        }
        return null;
    }

    @Override
    public boolean validText(ArgumentContext context) {
        // assume it's valid till we convert
        return true;
    }

    @Override
    public Iterator<String> getCompletions(ArgumentContext context) {
        // TODO completion for this hell
        return ImmutableList.<String> of().iterator();
    }

}
