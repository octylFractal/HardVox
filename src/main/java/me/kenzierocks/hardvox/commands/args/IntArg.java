package me.kenzierocks.hardvox.commands.args;

import java.util.Iterator;
import java.util.function.IntPredicate;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterators;

class IntArg extends BaseArg<Integer> {

    private static final ImmutableList<String> BASE_2_DIGITS = ImmutableList.of("0", "1");
    private static final ImmutableList<String> BASE_8_DIGITS = ImmutableList.of(
            "0", "1", "2", "3", "4", "5", "6", "7");
    private static final ImmutableList<String> BASE_10_DIGITS = ImmutableList.of(
            "0", "1", "2", "3", "4", "5", "6", "7", "8", "9");
    private static final ImmutableList<String> BASE_16_DIGITS = new ImmutableList.Builder<String>()
            .addAll(BASE_10_DIGITS)
            .add("a", "b", "c", "d", "e", "f")
            .build();
    private static final ImmutableList<String> SIGN_COMPLETIONS = ImmutableList.of("+", "-");
    private static final ImmutableList<String> BASE_PREFIX_COMPLETIONS = ImmutableList.of("0b", "0o", "0x");
    private static final ImmutableList<String> ZERO_BASE_COMPLETIONS = ImmutableList.of("b", "o", "x");

    private static final IntPredicate BASE_2 = cp -> cp == '0' || cp == '1';
    private static final IntPredicate BASE_8 = cp -> '0' <= cp && cp <= '7';
    private static final IntPredicate BASE_10 = cp -> '0' <= cp && cp <= '9';
    private static final IntPredicate BASE_16 = BASE_10.or(
            cp -> ('a' <= cp && cp <= 'f') || 'A' <= cp && cp <= 'F');

    IntArg(String name) {
        super(name);
    }

    @Override
    public Integer convert(ArgumentContext context) {
        String t = context.text;
        boolean negative = t.startsWith("-");
        if (t.startsWith("+") || negative) {
            t = t.substring(1);
        }
        String p = t.length() > 2 ? t.substring(0, 2) : "";
        String n = p.isEmpty() ? "" : (negative ? "-" : "") + t.substring(2);
        switch (p) {
            case "0b":
                return Integer.parseInt(n, 2);
            case "0o":
                return Integer.parseInt(n, 8);
            case "0x":
                return Integer.parseInt(n, 16);
            default:
                return Integer.parseInt(context.text, 10);
        }
    }

    @Override
    public boolean validText(ArgumentContext context) {
        String t = context.text;
        if (t.startsWith("+") || t.startsWith("-")) {
            t = t.substring(1);
        }
        String p = t.length() > 2 ? t.substring(0, 2) : "";
        String n = p.isEmpty() ? "" : t.substring(2);
        switch (p) {
            case "0b":
                return n.codePoints().allMatch(BASE_2);
            case "0o":
                return n.codePoints().allMatch(BASE_8);
            case "0x":
                return n.codePoints().allMatch(BASE_16);
            default:
                if (t.startsWith("0")) {
                    return false;
                }
                return t.codePoints().skip(1).allMatch(BASE_10);
        }
    }

    @Override
    public Iterator<String> getCompletions(ArgumentContext context) {
        String t = context.text;
        if (t.startsWith("+") || t.startsWith("-")) {
            t = t.substring(1);
        }
        String p = t.length() > 2 ? t.substring(0, 2) : "";
        Iterator<String> base;
        switch (p) {
            case "0b":
                base = BASE_2_DIGITS.iterator();
                break;
            case "0o":
                base = BASE_8_DIGITS.iterator();
                break;
            case "0x":
                base = BASE_16_DIGITS.iterator();
                break;
            default:
                base = BASE_10_DIGITS.iterator();
        }
        if (t.isEmpty()) {
            // insert prefix completions if empty besides signs
            base = Iterators.concat(BASE_PREFIX_COMPLETIONS.iterator(), base);
            if (context.text.isEmpty()) {
                // insert sign completions if totally empty
                base = Iterators.concat(SIGN_COMPLETIONS.iterator(), base);
            }
        } else if (t.equals("0")) {
            // set zero prefix completions -- there's no base 10 in this mode
            base = ZERO_BASE_COMPLETIONS.iterator();
        }
        return Iterators.transform(base, context.text::concat);
    }

}
