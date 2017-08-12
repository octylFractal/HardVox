package me.kenzierocks.hardvox.commands.region;

import me.kenzierocks.hardvox.HardVoxConfig;
import me.kenzierocks.hardvox.Texts;
import me.kenzierocks.hardvox.commands.HVCommand;
import me.kenzierocks.hardvox.commands.args.CommandArgSet;
import me.kenzierocks.hardvox.commands.args.CommandParser;
import me.kenzierocks.hardvox.session.HVSession;
import net.minecraft.command.CommandException;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

public class SelectionWandCommand extends HVCommand {

    public SelectionWandCommand() {
        super("wand", CommandParser.init());
    }

    @Override
    protected void execute(HVSession session, CommandArgSet args) throws CommandException {
        Entity e = session.owner.getCommandSenderEntity();
        if (e == null) {
            throw new CommandException(getSlashName() + " must be performed by a player.");
        }

        if (e instanceof EntityPlayer) {
            if (((EntityPlayer) e).addItemStackToInventory(new ItemStack(HardVoxConfig.getSelectionWand()))) {
                session.sendMessage(Texts.hardVoxMessage("Added ONE (1) selection wand."));
            } else {
                throw new CommandException("It looks like your inventory is full.");
            }
            return;
        }
        session.sendMessage(Texts.hardVoxError("Hey! You're not a player! Have 128 wands."));
        // suprise! you get so many wands!
        for (int i = 0; i < 128; i++) {
            e.dropItem(HardVoxConfig.getSelectionWand(), 1);
        }
    }

}
