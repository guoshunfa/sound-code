package java.rmi.activation;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.rmi.MarshalledObject;
import java.util.Arrays;
import java.util.Properties;

public final class ActivationGroupDesc implements Serializable {
   private String className;
   private String location;
   private MarshalledObject<?> data;
   private ActivationGroupDesc.CommandEnvironment env;
   private Properties props;
   private static final long serialVersionUID = -4936225423168276595L;

   public ActivationGroupDesc(Properties var1, ActivationGroupDesc.CommandEnvironment var2) {
      this((String)null, (String)null, (MarshalledObject)null, var1, var2);
   }

   public ActivationGroupDesc(String var1, String var2, MarshalledObject<?> var3, Properties var4, ActivationGroupDesc.CommandEnvironment var5) {
      this.props = var4;
      this.env = var5;
      this.data = var3;
      this.location = var2;
      this.className = var1;
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

   public Properties getPropertyOverrides() {
      return this.props != null ? (Properties)this.props.clone() : null;
   }

   public ActivationGroupDesc.CommandEnvironment getCommandEnvironment() {
      return this.env;
   }

   public boolean equals(Object var1) {
      if (!(var1 instanceof ActivationGroupDesc)) {
         return false;
      } else {
         boolean var10000;
         label63: {
            label57: {
               ActivationGroupDesc var2 = (ActivationGroupDesc)var1;
               if (this.className == null) {
                  if (var2.className != null) {
                     break label57;
                  }
               } else if (!this.className.equals(var2.className)) {
                  break label57;
               }

               if (this.location == null) {
                  if (var2.location != null) {
                     break label57;
                  }
               } else if (!this.location.equals(var2.location)) {
                  break label57;
               }

               if (this.data == null) {
                  if (var2.data != null) {
                     break label57;
                  }
               } else if (!this.data.equals(var2.data)) {
                  break label57;
               }

               if (this.env == null) {
                  if (var2.env != null) {
                     break label57;
                  }
               } else if (!this.env.equals(var2.env)) {
                  break label57;
               }

               if (this.props == null) {
                  if (var2.props == null) {
                     break label63;
                  }
               } else if (this.props.equals(var2.props)) {
                  break label63;
               }
            }

            var10000 = false;
            return var10000;
         }

         var10000 = true;
         return var10000;
      }
   }

   public int hashCode() {
      return (this.location == null ? 0 : this.location.hashCode() << 24) ^ (this.env == null ? 0 : this.env.hashCode() << 16) ^ (this.className == null ? 0 : this.className.hashCode() << 8) ^ (this.data == null ? 0 : this.data.hashCode());
   }

   public static class CommandEnvironment implements Serializable {
      private static final long serialVersionUID = 6165754737887770191L;
      private String command;
      private String[] options;

      public CommandEnvironment(String var1, String[] var2) {
         this.command = var1;
         if (var2 == null) {
            this.options = new String[0];
         } else {
            this.options = new String[var2.length];
            System.arraycopy(var2, 0, this.options, 0, var2.length);
         }

      }

      public String getCommandPath() {
         return this.command;
      }

      public String[] getCommandOptions() {
         return (String[])this.options.clone();
      }

      public boolean equals(Object var1) {
         if (!(var1 instanceof ActivationGroupDesc.CommandEnvironment)) {
            return false;
         } else {
            boolean var10000;
            label21: {
               ActivationGroupDesc.CommandEnvironment var2 = (ActivationGroupDesc.CommandEnvironment)var1;
               if (this.command == null) {
                  if (var2.command != null) {
                     break label21;
                  }
               } else if (!this.command.equals(var2.command)) {
                  break label21;
               }

               if (Arrays.equals((Object[])this.options, (Object[])var2.options)) {
                  var10000 = true;
                  return var10000;
               }
            }

            var10000 = false;
            return var10000;
         }
      }

      public int hashCode() {
         return this.command == null ? 0 : this.command.hashCode();
      }

      private void readObject(ObjectInputStream var1) throws IOException, ClassNotFoundException {
         var1.defaultReadObject();
         if (this.options == null) {
            this.options = new String[0];
         }

      }
   }
}
