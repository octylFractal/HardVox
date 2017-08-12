package me.kenzierocks.hardvox;

import me.kenzierocks.hardvox.net.SelectionMessageHandler;
import me.kenzierocks.hardvox.session.SessionManager;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;
import net.minecraftforge.fml.common.gameevent.TickEvent.ServerTickEvent;

public class HVSharedProxy {

    public SelectionMessageHandler createSelectionMessageHandler() {
        return new SelectionMessageHandler();
    }

    @SubscribeEvent
    public void onTick(ServerTickEvent event) {
        if (event.phase == Phase.START) {
            SessionManager.getInstance().getAllSessions().forEachRemaining(sess -> {
                sess.taskManager.runTasks();
            });
        }
    }

}
