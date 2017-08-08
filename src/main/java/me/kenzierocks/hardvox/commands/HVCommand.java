package me.kenzierocks.hardvox.commands;

import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

import com.google.common.base.Strings;
import com.google.common.collect.ImmutableList;

import me.kenzierocks.hardvox.Texts;
import me.kenzierocks.hardvox.commands.args.CommandArgSet;
import me.kenzierocks.hardvox.commands.args.CommandParser;
import me.kenzierocks.hardvox.commands.args.ParserContext;
import me.kenzierocks.hardvox.session.HVSession;
import me.kenzierocks.hardvox.session.SessionManager;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;

public abstract class HVCommand extends CommandBase {

    private static String usageFromParser(CommandParser parser) {
        return parser.getArguments().stream()
                .map(arg -> {
                    boolean opt = parser.isArgumentOptional(arg);
                    return (opt ? "[" : "<") + arg.getName() + (opt ? "]" : ">");
                }).collect(Collectors.joining(" "));
    }

    private final String name;
    private final CommandParser parser;

    public HVCommand(String name, CommandParser parser) {
        // as is tradition
        this.name = "/" + name;
        this.parser = parser;
    }

    @Override
    public int getRequiredPermissionLevel() {
        // for command blocks.
        return 2;
    }

    @Override
    public String getName() {
        return this.name;
    }

    public String getSlashName() {
        return "/" + getName();
    }

    @Override
    public String getUsage(ICommandSender sender) {
        String args = describeArguments(sender);
        return getSlashName() + (args.isEmpty() ? "" : " " + args);
    }

    protected String describeArguments(ICommandSender sender) {
        return usageFromParser(parser);
    }

    @Override
    public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, BlockPos targetPos) {
        Iterator<String> completions = this.parser.getCompletions(new ParserContext(server, sender, targetPos, args));
        return ImmutableList.copyOf(completions);
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        try {
            CommandArgSet argSet = this.parser.parse(getUsage(sender), new ParserContext(server, sender, null, args));
            if (argSet == null) {
                throw new CommandException("Something is wrong!");
            }
            execute(SessionManager.getInstance().getSession(server, sender), argSet);
        } catch (IllegalArgumentException e) {
            throw new CommandException(e.getMessage());
        } catch (WrongUsageException | UncheckedWUE e) {
            // subsystems can throw UWUE
            // (or WUE with an empty string) to have it filled
            if (Strings.isNullOrEmpty(e.getMessage()) || (e instanceof UncheckedWUE)) {
                if (e instanceof UncheckedWUE) {
                    sender.sendMessage(Texts.hardVoxError(((UncheckedWUE) e).getExtraErrorText()));
                }
                WrongUsageException copy = new WrongUsageException(getUsage(sender));
                copy.setStackTrace(e.getStackTrace());
                throw copy;
            }
            throw e;
        }
    }

    protected abstract void execute(HVSession session, CommandArgSet args) throws CommandException;

}
