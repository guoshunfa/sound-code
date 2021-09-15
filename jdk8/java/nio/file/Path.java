package java.nio.file;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.Iterator;

public interface Path extends Comparable<Path>, Iterable<Path>, Watchable {
   FileSystem getFileSystem();

   boolean isAbsolute();

   Path getRoot();

   Path getFileName();

   Path getParent();

   int getNameCount();

   Path getName(int var1);

   Path subpath(int var1, int var2);

   boolean startsWith(Path var1);

   boolean startsWith(String var1);

   boolean endsWith(Path var1);

   boolean endsWith(String var1);

   Path normalize();

   Path resolve(Path var1);

   Path resolve(String var1);

   Path resolveSibling(Path var1);

   Path resolveSibling(String var1);

   Path relativize(Path var1);

   URI toUri();

   Path toAbsolutePath();

   Path toRealPath(LinkOption... var1) throws IOException;

   File toFile();

   WatchKey register(WatchService var1, WatchEvent.Kind<?>[] var2, WatchEvent.Modifier... var3) throws IOException;

   WatchKey register(WatchService var1, WatchEvent.Kind<?>... var2) throws IOException;

   Iterator<Path> iterator();

   int compareTo(Path var1);

   boolean equals(Object var1);

   int hashCode();

   String toString();
}
