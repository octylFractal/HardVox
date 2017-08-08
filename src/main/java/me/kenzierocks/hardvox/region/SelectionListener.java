package me.kenzierocks.hardvox.region;

import java.util.function.BiFunction;

import com.flowpowered.math.vector.Vector3i;

import me.kenzierocks.hardvox.HardVoxConfig;
import me.kenzierocks.hardvox.region.selector.RegionSelector;
import me.kenzierocks.hardvox.session.SessionManager;
import me.kenzierocks.hardvox.vector.VecBridge;
import net.minecraft.world.WorldServer;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.LeftClickBlock;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.RightClickBlock;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class SelectionListener {

    @SubscribeEvent
    public void onLeftClickBlock(LeftClickBlock event) {
        dueProcess(event, RegionSelector::selectPrimary);
    }

    @SubscribeEvent
    public void onRightClickBlock(RightClickBlock event) {
        dueProcess(event, RegionSelector::selectSecondary);
    }

    private void dueProcess(PlayerInteractEvent event, BiFunction<RegionSelector<?, ?>, Vector3i, String> selector) {
        if (event.getWorld().isRemote) {
            // don't process on client
            // perhaps we can cancel on client if we do config sync
            return;
        }

        if (event.getItemStack().getItem() == HardVoxConfig.getSelectionWand()) {
            // cancel event, fire selection
            event.setCanceled(true);

            WorldServer ws = (WorldServer) event.getWorld();
            SessionManager.getInstance()
                    .getSession(ws.getMinecraftServer(), event.getEntityPlayer())
                    .performRegionCommand(sel -> selector.apply(sel, VecBridge.toFlow(event.getPos())));
            // send an update to the player so the block re-appears?
            ws.getPlayerChunkMap().markBlockForUpdate(event.getPos());
        }
    }

}
