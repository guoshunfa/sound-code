package javax.sql.rowset.serial;

import java.io.Serializable;
import java.net.MalformedURLException;
import java.net.URL;

public class SerialDatalink implements Serializable, Cloneable {
   private URL url;
   private int baseType;
   private String baseTypeName;
   static final long serialVersionUID = 2826907821828733626L;

   public SerialDatalink(URL var1) throws SerialException {
      if (var1 == null) {
         throw new SerialException("Cannot serialize empty URL instance");
      } else {
         this.url = var1;
      }
   }

   public URL getDatalink() throws SerialException {
      URL var1 = null;

      try {
         var1 = new URL(this.url.toString());
         return var1;
      } catch (MalformedURLException var3) {
         throw new SerialException("MalformedURLException: " + var3.getMessage());
      }
   }

   public boolean equals(Object var1) {
      if (this == var1) {
         return true;
      } else if (var1 instanceof SerialDatalink) {
         SerialDatalink var2 = (SerialDatalink)var1;
         return this.url.equals(var2.url);
      } else {
         return false;
      }
   }

   public int hashCode() {
      return 31 + this.url.hashCode();
   }

   public Object clone() {
      try {
         SerialDatalink var1 = (SerialDatalink)super.clone();
         return var1;
      } catch (CloneNotSupportedException var2) {
         throw new InternalError();
      }
   }
}
