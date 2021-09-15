package com.sun.corba.se.impl.ior.iiop;

import com.sun.corba.se.spi.ior.TaggedComponentBase;
import com.sun.corba.se.spi.ior.iiop.JavaCodebaseComponent;
import org.omg.CORBA_2_3.portable.OutputStream;

public class JavaCodebaseComponentImpl extends TaggedComponentBase implements JavaCodebaseComponent {
   private String URLs;

   public boolean equals(Object var1) {
      if (var1 == null) {
         return false;
      } else if (!(var1 instanceof JavaCodebaseComponentImpl)) {
         return false;
      } else {
         JavaCodebaseComponentImpl var2 = (JavaCodebaseComponentImpl)var1;
         return this.URLs.equals(var2.getURLs());
      }
   }

   public int hashCode() {
      return this.URLs.hashCode();
   }

   public String toString() {
      return "JavaCodebaseComponentImpl[URLs=" + this.URLs + "]";
   }

   public String getURLs() {
      return this.URLs;
   }

   public JavaCodebaseComponentImpl(String var1) {
      this.URLs = var1;
   }

   public void writeContents(OutputStream var1) {
      var1.write_string(this.URLs);
   }

   public int getId() {
      return 25;
   }
}
