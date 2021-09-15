package java.nio.file.attribute;

import java.io.IOException;

public interface DosFileAttributeView extends BasicFileAttributeView {
   String name();

   DosFileAttributes readAttributes() throws IOException;

   void setReadOnly(boolean var1) throws IOException;

   void setHidden(boolean var1) throws IOException;

   void setSystem(boolean var1) throws IOException;

   void setArchive(boolean var1) throws IOException;
}
