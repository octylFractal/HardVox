package me.kenzierocks.hardvox.vector;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import me.kenzierocks.hardvox.vector.VMShared.Vec;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.world.ChunkDataEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

/**
 * Stores data that is tied to a chunk. The data is written to disk when its
 * corresponding chunk is. Data will not be loaded from disk if not found in
 * memory, it will only be loaded if its corresponding chunk is.
 */
public class ChunkDataStore<D> implements AutoCloseable {

    private static String fileName(Vec vec) {
        return vec.x + "," + vec.z;
    }

    private final CDSCodec<D> codec;
    private final Path tmpDir;
    private final Map<Vec, D> memory = new HashMap<>();

    public ChunkDataStore(CDSCodec<D> codec) {
        this.codec = codec;
        try {
            tmpDir = Files.createTempDirectory("hardvox-chunkdatastore");
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
        MinecraftForge.EVENT_BUS.register(new CDSListeners());
    }

    private final class CDSListeners {

        @SubscribeEvent
        public void onChunkLoad(ChunkDataEvent.Load event) {
            preload(event.getChunk().x, event.getChunk().z);
        }

        @SubscribeEvent
        public void onChunkSave(ChunkDataEvent.Save event) {
            Vec v = new Vec(event.getChunk().x, 0, event.getChunk().z);
            D d = memory.get(v);
            if (d != null) {
                Path p = tmpDir.resolve(fileName(v));
                try (DataOutputStream stream = new DataOutputStream(new BufferedOutputStream(Files.newOutputStream(p)))) {
                    codec.save(stream, d);
                    memory.remove(v);
                } catch (IOException e) {
                    throw new UncheckedIOException(e);
                }
            }
        }

    }

    public void preload(int x, int z) {
        Vec v = new Vec(x, 0, z);
        if (memory.containsKey(v)) {
            return;
        }
        Path p = tmpDir.resolve(fileName(v));
        if (Files.exists(p)) {
            try (DataInputStream stream = new DataInputStream(new BufferedInputStream(Files.newInputStream(p)))) {
                D d = codec.read(stream);
                memory.put(v, d);
            } catch (IOException e) {
                throw new UncheckedIOException(e);
            }
        }
    }

    public Optional<D> get(int x, int z) {
        return Optional.ofNullable(memory.get(new Vec(x, 0, z)));
    }

    public void put(int x, int z, D d) {
        memory.put(new Vec(x, 0, z), d);
    }

    public void delete(int x, int z) {
        Vec vec = new Vec(x, 0, z);
        memory.remove(vec);
        try {
            Files.deleteIfExists(tmpDir.resolve(fileName(vec)));
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    @Override
    public void close() {
        memory.clear();
        try {
            Files.walkFileTree(tmpDir, new SimpleFileVisitor<Path>() {

                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                    Files.deleteIfExists(file);
                    return super.visitFile(file, attrs);
                }

                @Override
                public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
                    Files.deleteIfExists(dir);
                    return super.postVisitDirectory(dir, exc);
                }
            });
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

}
