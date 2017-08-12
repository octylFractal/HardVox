package me.kenzierocks.hardvox.commands.session;

import me.kenzierocks.hardvox.Texts;
import me.kenzierocks.hardvox.commands.HVCommand;
import me.kenzierocks.hardvox.commands.args.AccessAction;
import me.kenzierocks.hardvox.commands.args.CommandArgSet;
import me.kenzierocks.hardvox.commands.args.CommandArgument;
import me.kenzierocks.hardvox.commands.args.CommandArguments;
import me.kenzierocks.hardvox.commands.args.CommandParser;
import me.kenzierocks.hardvox.commands.args.EBool;
import me.kenzierocks.hardvox.commands.args.EnumArg;
import me.kenzierocks.hardvox.session.HVSession;
import me.kenzierocks.hardvox.session.SessionFlag;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;

public class SessionFlagCommand extends HVCommand {

    private static final CommandArgument<AccessAction> ACCESS_ACTION = CommandArguments.accessAction();
    private static final CommandArgument<SessionFlag> SESSION_FLAG = EnumArg.create(SessionFlag.class, "flag", s -> s.id);;
    private static final CommandArgument<EBool> ENABLED = CommandArguments.onOff("enabled");

    public SessionFlagCommand() {
        // TODO we could use sub-parsers here...
        super("flag", CommandParser.init(ACCESS_ACTION, SESSION_FLAG, ENABLED)
                .markOptionalStartingWith(ENABLED));
    }

    @Override
    public String getUsage(ICommandSender sender) {
        final String GET = "get <" + SESSION_FLAG.getName() + ">";
        final String SET = "set <" + SESSION_FLAG.getName() + "> <" + ENABLED.getName() + ">";
        return getSlashName() + " <" + GET + "|" + SET + ">";
    }

    @Override
    protected void execute(HVSession session, CommandArgSet args) throws CommandException {
        SessionFlag flag = args.value(SESSION_FLAG);
        boolean set;
        String msg;
        switch (args.value(ACCESS_ACTION)) {
            case GET:
                set = session.flags.contains(flag);
                msg = flagText(flag) + " is " + (set ? "ON" : "OFF") + ".";
                break;
            case SET:
                set = args.value(ENABLED).toBoolean();
                boolean changed;
                if (set) {
                    changed = session.flags.add(flag);
                } else {
                    changed = session.flags.remove(flag);
                }
                if (changed) {
                    msg = flagText(flag) + " is now " + (set ? "ON" : "OFF") + ".";
                } else {
                    msg = flagText(flag) + " was not changed. It is " + (set ? "ON" : "OFF") + ".";
                }
                break;
            default:
                throw new AssertionError("unexpected action " + args.value(ACCESS_ACTION));
        }
        session.sendMessage(Texts.hardVoxMessage(msg));
    }

    private String flagText(SessionFlag flag) {
        return "Flag '" + flag.id + "'";
    }

}
