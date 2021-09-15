package java.rmi.activation;

import java.io.Serializable;
import java.rmi.MarshalledObject;

public final class ActivationDesc implements Serializable {
   private ActivationGroupID groupID;
   private String className;
   private String location;
   private MarshalledObject<?> data;
   private boolean restart;
   private static final long serialVersionUID = 7455834104417690957L;

   public ActivationDesc(String var1, String var2, MarshalledObject<?> var3) throws ActivationException {
      this(ActivationGroup.internalCurrentGroupID(), var1, var2, var3, false);
   }

   public ActivationDesc(String var1, String var2, MarshalledObject<?> var3, boolean var4) throws ActivationException {
      this(ActivationGroup.internalCurrentGroupID(), var1, var2, var3, var4);
   }

   public ActivationDesc(ActivationGroupID var1, String var2, String var3, MarshalledObject<?> var4) {
      this(var1, var2, var3, var4, false);
   }

   public ActivationDesc(ActivationGroupID var1, String var2, String var3, MarshalledObject<?> var4, boolean var5) {
      if (var1 == null) {
         throw new IllegalArgumentException("groupID can't be null");
      } else {
         this.groupID = var1;
         this.className = var2;
         this.location = var3;
         this.data = var4;
         this.restart = var5;
      }
   }

   public ActivationGroupID getGroupID() {
      return this.groupID;
   }

   public String getClassName() {
      return this.className;
   }

   public String getLocation() {
      return this.location;
   }

   public MarshalledObject<?> getData() {
      return this.data;
   }

   public boolean getRestartMode() {
      return this.restart;
   }

   public boolean equals(Object var1) {
      if (!(var1 instanceof ActivationDesc)) {
         return false;
      } else {
         boolean var10000;
         label53: {
            ActivationDesc var2 = (ActivationDesc)var1;
            if (this.groupID == null) {
               if (var2.groupID != null) {
                  break label53;
               }
            } else if (!this.groupID.equals(var2.groupID)) {
               break label53;
            }

            if (this.className == null) {
               if (var2.className != null) {
                  break label53;
               }
            } else if (!this.className.equals(var2.className)) {
               break label53;
            }

            if (this.location == null) {
               if (var2.location != null) {
                  break label53;
               }
            } else if (!this.location.equals(var2.location)) {
               break label53;
            }

            if (this.data == null) {
               if (var2.data != null) {
                  break label53;
               }
            } else if (!this.data.equals(var2.data)) {
               break label53;
            }

            if (this.restart == var2.restart) {
               var10000 = true;
               return var10000;
            }
         }

         var10000 = false;
         return var10000;
      }
   }

   public int hashCode() {
      return (this.location == null ? 0 : this.location.hashCode() << 24) ^ (this.groupID == null ? 0 : this.groupID.hashCode() << 16) ^ (this.className == null ? 0 : this.className.hashCode() << 9) ^ (this.data == null ? 0 : this.data.hashCode() << 1) ^ (this.restart ? 1 : 0);
   }
}
