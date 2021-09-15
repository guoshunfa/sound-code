package javax.smartcardio;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.security.Permission;

public class CardPermission extends Permission {
   private static final long serialVersionUID = 7146787880530705613L;
   private static final int A_CONNECT = 1;
   private static final int A_EXCLUSIVE = 2;
   private static final int A_GET_BASIC_CHANNEL = 4;
   private static final int A_OPEN_LOGICAL_CHANNEL = 8;
   private static final int A_RESET = 16;
   private static final int A_TRANSMIT_CONTROL = 32;
   private static final int A_ALL = 63;
   private static final int[] ARRAY_MASKS = new int[]{63, 1, 2, 4, 8, 16, 32};
   private static final String S_CONNECT = "connect";
   private static final String S_EXCLUSIVE = "exclusive";
   private static final String S_GET_BASIC_CHANNEL = "getBasicChannel";
   private static final String S_OPEN_LOGICAL_CHANNEL = "openLogicalChannel";
   private static final String S_RESET = "reset";
   private static final String S_TRANSMIT_CONTROL = "transmitControl";
   private static final String S_ALL = "*";
   private static final String[] ARRAY_STRINGS = new String[]{"*", "connect", "exclusive", "getBasicChannel", "openLogicalChannel", "reset", "transmitControl"};
   private transient int mask;
   private volatile String actions;

   public CardPermission(String var1, String var2) {
      super(var1);
      if (var1 == null) {
         throw new NullPointerException();
      } else {
         this.mask = getMask(var2);
      }
   }

   private static int getMask(String var0) {
      if (var0 != null && var0.length() != 0) {
         int var1;
         for(var1 = 0; var1 < ARRAY_STRINGS.length; ++var1) {
            if (var0 == ARRAY_STRINGS[var1]) {
               return ARRAY_MASKS[var1];
            }
         }

         if (var0.endsWith(",")) {
            throw new IllegalArgumentException("Invalid actions: '" + var0 + "'");
         } else {
            var1 = 0;
            String[] var2 = var0.split(",");
            String[] var3 = var2;
            int var4 = var2.length;

            label38:
            for(int var5 = 0; var5 < var4; ++var5) {
               String var6 = var3[var5];

               for(int var7 = 0; var7 < ARRAY_STRINGS.length; ++var7) {
                  if (ARRAY_STRINGS[var7].equalsIgnoreCase(var6)) {
                     var1 |= ARRAY_MASKS[var7];
                     continue label38;
                  }
               }

               throw new IllegalArgumentException("Invalid action: '" + var6 + "'");
            }

            return var1;
         }
      } else {
         throw new IllegalArgumentException("actions must not be empty");
      }
   }

   private static String getActions(int var0) {
      if (var0 == 63) {
         return "*";
      } else {
         boolean var1 = true;
         StringBuilder var2 = new StringBuilder();

         for(int var3 = 0; var3 < ARRAY_MASKS.length; ++var3) {
            int var4 = ARRAY_MASKS[var3];
            if ((var0 & var4) == var4) {
               if (!var1) {
                  var2.append(",");
               } else {
                  var1 = false;
               }

               var2.append(ARRAY_STRINGS[var3]);
            }
         }

         return var2.toString();
      }
   }

   public String getActions() {
      if (this.actions == null) {
         this.actions = getActions(this.mask);
      }

      return this.actions;
   }

   public boolean implies(Permission var1) {
      if (!(var1 instanceof CardPermission)) {
         return false;
      } else {
         CardPermission var2 = (CardPermission)var1;
         if ((this.mask & var2.mask) != var2.mask) {
            return false;
         } else {
            String var3 = this.getName();
            if (var3.equals("*")) {
               return true;
            } else {
               return var3.equals(var2.getName());
            }
         }
      }
   }

   public boolean equals(Object var1) {
      if (this == var1) {
         return true;
      } else if (!(var1 instanceof CardPermission)) {
         return false;
      } else {
         CardPermission var2 = (CardPermission)var1;
         return this.getName().equals(var2.getName()) && this.mask == var2.mask;
      }
   }

   public int hashCode() {
      return this.getName().hashCode() + 31 * this.mask;
   }

   private void writeObject(ObjectOutputStream var1) throws IOException {
      if (this.actions == null) {
         this.getActions();
      }

      var1.defaultWriteObject();
   }

   private void readObject(ObjectInputStream var1) throws IOException, ClassNotFoundException {
      var1.defaultReadObject();
      this.mask = getMask(this.actions);
   }
}
