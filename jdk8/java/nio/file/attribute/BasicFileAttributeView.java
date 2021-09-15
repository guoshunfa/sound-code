package java.nio.file.attribute;

import java.io.IOException;

public interface BasicFileAttributeView extends FileAttributeView {
   String name();

   BasicFileAttributes readAttributes() throws IOException;

   void setTimes(FileTime var1, FileTime var2, FileTime var3) throws IOException;
}
