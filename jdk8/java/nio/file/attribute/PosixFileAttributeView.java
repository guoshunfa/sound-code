package java.nio.file.attribute;

import java.io.IOException;
import java.util.Set;

public interface PosixFileAttributeView extends BasicFileAttributeView, FileOwnerAttributeView {
   String name();

   PosixFileAttributes readAttributes() throws IOException;

   void setPermissions(Set<PosixFilePermission> var1) throws IOException;

   void setGroup(GroupPrincipal var1) throws IOException;
}
