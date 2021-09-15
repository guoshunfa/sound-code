package sun.nio.fs;

import java.io.IOException;
import java.nio.file.attribute.AclFileAttributeView;
import java.nio.file.attribute.FileAttributeView;
import java.nio.file.attribute.FileOwnerAttributeView;
import java.nio.file.attribute.PosixFileAttributeView;
import java.nio.file.attribute.UserPrincipal;
import java.util.HashMap;
import java.util.Map;

final class FileOwnerAttributeViewImpl implements FileOwnerAttributeView, DynamicFileAttributeView {
   private static final String OWNER_NAME = "owner";
   private final FileAttributeView view;
   private final boolean isPosixView;

   FileOwnerAttributeViewImpl(PosixFileAttributeView var1) {
      this.view = var1;
      this.isPosixView = true;
   }

   FileOwnerAttributeViewImpl(AclFileAttributeView var1) {
      this.view = var1;
      this.isPosixView = false;
   }

   public String name() {
      return "owner";
   }

   public void setAttribute(String var1, Object var2) throws IOException {
      if (var1.equals("owner")) {
         this.setOwner((UserPrincipal)var2);
      } else {
         throw new IllegalArgumentException("'" + this.name() + ":" + var1 + "' not recognized");
      }
   }

   public Map<String, Object> readAttributes(String[] var1) throws IOException {
      HashMap var2 = new HashMap();
      String[] var3 = var1;
      int var4 = var1.length;

      for(int var5 = 0; var5 < var4; ++var5) {
         String var6 = var3[var5];
         if (!var6.equals("*") && !var6.equals("owner")) {
            throw new IllegalArgumentException("'" + this.name() + ":" + var6 + "' not recognized");
         }

         var2.put("owner", this.getOwner());
      }

      return var2;
   }

   public UserPrincipal getOwner() throws IOException {
      return this.isPosixView ? ((PosixFileAttributeView)this.view).readAttributes().owner() : ((AclFileAttributeView)this.view).getOwner();
   }

   public void setOwner(UserPrincipal var1) throws IOException {
      if (this.isPosixView) {
         ((PosixFileAttributeView)this.view).setOwner(var1);
      } else {
         ((AclFileAttributeView)this.view).setOwner(var1);
      }

   }
}
