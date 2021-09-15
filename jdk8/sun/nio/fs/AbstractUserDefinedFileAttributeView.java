package sun.nio.fs;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.file.attribute.UserDefinedFileAttributeView;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

abstract class AbstractUserDefinedFileAttributeView implements UserDefinedFileAttributeView, DynamicFileAttributeView {
   protected AbstractUserDefinedFileAttributeView() {
   }

   protected void checkAccess(String var1, boolean var2, boolean var3) {
      assert var2 || var3;

      SecurityManager var4 = System.getSecurityManager();
      if (var4 != null) {
         if (var2) {
            var4.checkRead(var1);
         }

         if (var3) {
            var4.checkWrite(var1);
         }

         var4.checkPermission(new RuntimePermission("accessUserDefinedAttributes"));
      }

   }

   public final String name() {
      return "user";
   }

   public final void setAttribute(String var1, Object var2) throws IOException {
      ByteBuffer var3;
      if (var2 instanceof byte[]) {
         var3 = ByteBuffer.wrap((byte[])((byte[])var2));
      } else {
         var3 = (ByteBuffer)var2;
      }

      this.write(var1, var3);
   }

   public final Map<String, Object> readAttributes(String[] var1) throws IOException {
      Object var2 = new ArrayList();
      String[] var3 = var1;
      int var4 = var1.length;

      for(int var5 = 0; var5 < var4; ++var5) {
         String var6 = var3[var5];
         if (var6.equals("*")) {
            var2 = this.list();
            break;
         }

         if (var6.length() == 0) {
            throw new IllegalArgumentException();
         }

         ((List)var2).add(var6);
      }

      HashMap var10 = new HashMap();
      Iterator var11 = ((List)var2).iterator();

      while(var11.hasNext()) {
         String var12 = (String)var11.next();
         int var13 = this.size(var12);
         byte[] var7 = new byte[var13];
         int var8 = this.read(var12, ByteBuffer.wrap(var7));
         byte[] var9 = var8 == var13 ? var7 : Arrays.copyOf(var7, var8);
         var10.put(var12, var9);
      }

      return var10;
   }
}
