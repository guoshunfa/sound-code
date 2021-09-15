package java.awt.font;

import java.awt.geom.AffineTransform;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.ObjectStreamException;
import java.io.Serializable;

public final class TransformAttribute implements Serializable {
   private AffineTransform transform;
   public static final TransformAttribute IDENTITY = new TransformAttribute((AffineTransform)null);
   static final long serialVersionUID = 3356247357827709530L;

   public TransformAttribute(AffineTransform var1) {
      if (var1 != null && !var1.isIdentity()) {
         this.transform = new AffineTransform(var1);
      }

   }

   public AffineTransform getTransform() {
      AffineTransform var1 = this.transform;
      return var1 == null ? new AffineTransform() : new AffineTransform(var1);
   }

   public boolean isIdentity() {
      return this.transform == null;
   }

   private void writeObject(ObjectOutputStream var1) throws ClassNotFoundException, IOException {
      if (this.transform == null) {
         this.transform = new AffineTransform();
      }

      var1.defaultWriteObject();
   }

   private Object readResolve() throws ObjectStreamException {
      return this.transform != null && !this.transform.isIdentity() ? this : IDENTITY;
   }

   public int hashCode() {
      return this.transform == null ? 0 : this.transform.hashCode();
   }

   public boolean equals(Object var1) {
      if (var1 != null) {
         try {
            TransformAttribute var2 = (TransformAttribute)var1;
            if (this.transform == null) {
               return var2.transform == null;
            }

            return this.transform.equals(var2.transform);
         } catch (ClassCastException var3) {
         }
      }

      return false;
   }
}
