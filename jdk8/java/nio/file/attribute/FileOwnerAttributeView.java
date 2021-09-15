package java.nio.file.attribute;

import java.io.IOException;

public interface FileOwnerAttributeView extends FileAttributeView {
   String name();

   UserPrincipal getOwner() throws IOException;

   void setOwner(UserPrincipal var1) throws IOException;
}
