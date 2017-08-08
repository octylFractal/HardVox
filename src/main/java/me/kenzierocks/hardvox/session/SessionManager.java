package me.kenzierocks.hardvox.session;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.Vec3d;

public class SessionManager {

    private static final SessionManager INSTANCE = new SessionManager();

    public static SessionManager getInstance() {
        return INSTANCE;
    }

    private static String cacheKey(ICommandSender sender) {
        if (sender.getCommandSenderEntity() != null) {
            // ez pz, use the UUID
            return sender.getCommandSenderEntity().getCachedUniqueIdString();
        }
        // panic! use the position like it's a block or something!
        // also prepend the simple class name so that we can still kinda cache
        // the server, rcon, etc.
        Vec3d vec = sender.getPositionVector();
        return sender.getClass().getSimpleName() + "[" + vec.x + "," + vec.y + "," + vec.z + "]";
    }

    private final Map<String, HVSession> sessionCache = new HashMap<>();

    public HVSession getSession(MinecraftServer server, ICommandSender owner) {
        return sessionCache.computeIfAbsent(cacheKey(owner), k -> new HVSession(server, owner));
    }

    public Iterator<HVSession> getAllSessions() {
        return sessionCache.values().iterator();
    }

    public void clearAllSessions() {
        sessionCache.clear();
    }

}
