package jdk.management.resource.internal;

import java.io.FileDescriptor;
import java.util.Objects;
import jdk.Exported;
import jdk.management.resource.ResourceAccuracy;
import jdk.management.resource.ResourceId;
import sun.misc.JavaIOFileDescriptorAccess;
import sun.misc.SharedSecrets;

@Exported(false)
public class ResourceIdImpl implements ResourceId {
   private static final JavaIOFileDescriptorAccess FD_ACCESS = SharedSecrets.getJavaIOFileDescriptorAccess();
   private final Object target;
   private final ResourceAccuracy accuracy;
   private final boolean forceUpdate;

   public static ResourceIdImpl of(Object var0) {
      return var0 == null ? null : new ResourceIdImpl(var0, (ResourceAccuracy)null, false);
   }

   public static ResourceIdImpl of(FileDescriptor var0) {
      long var1 = -1L;
      if (var0 != null) {
         var1 = (long)FD_ACCESS.get(var0);
         if (var1 == -1L) {
            try {
               var1 = FD_ACCESS.getHandle(var0);
            } catch (UnsupportedOperationException var4) {
            }
         }
      }

      return var1 == -1L ? null : of((Object)((int)var1));
   }

   public static ResourceIdImpl of(Object var0, ResourceAccuracy var1) {
      return var0 == null ? null : new ResourceIdImpl(var0, var1, false);
   }

   public static ResourceIdImpl of(Object var0, ResourceAccuracy var1, boolean var2) {
      return var0 == null ? null : new ResourceIdImpl(var0, var1, var2);
   }

   protected ResourceIdImpl(Object var1, ResourceAccuracy var2, boolean var3) {
      this.target = var1;
      this.accuracy = var2;
      this.forceUpdate = var3;
   }

   public String getName() {
      return Objects.toString(this.target, (String)null);
   }

   public ResourceAccuracy getAccuracy() {
      return this.accuracy;
   }

   public boolean isForcedUpdate() {
      return this.forceUpdate;
   }

   public String toString() {
      StringBuilder var1 = new StringBuilder();
      var1.append(this.getName());
      ResourceAccuracy var2 = this.getAccuracy();
      if (var2 != null) {
         var1.append(", accuracy: ");
         var1.append((Object)var2);
      }

      return var1.toString();
   }
}
