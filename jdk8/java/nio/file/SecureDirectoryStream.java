package java.nio.file;

import java.io.IOException;
import java.nio.channels.SeekableByteChannel;
import java.nio.file.attribute.FileAttribute;
import java.nio.file.attribute.FileAttributeView;
import java.util.Set;

public interface SecureDirectoryStream<T> extends DirectoryStream<T> {
   SecureDirectoryStream<T> newDirectoryStream(T var1, LinkOption... var2) throws IOException;

   SeekableByteChannel newByteChannel(T var1, Set<? extends OpenOption> var2, FileAttribute<?>... var3) throws IOException;

   void deleteFile(T var1) throws IOException;

   void deleteDirectory(T var1) throws IOException;

   void move(T var1, SecureDirectoryStream<T> var2, T var3) throws IOException;

   <V extends FileAttributeView> V getFileAttributeView(Class<V> var1);

   <V extends FileAttributeView> V getFileAttributeView(T var1, Class<V> var2, LinkOption... var3);
}
