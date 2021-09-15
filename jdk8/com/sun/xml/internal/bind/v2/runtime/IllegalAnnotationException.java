package com.sun.xml.internal.bind.v2.runtime;

import com.sun.xml.internal.bind.v2.model.annotation.Locatable;
import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import javax.xml.bind.JAXBException;

public class IllegalAnnotationException extends JAXBException {
   private final List<List<Location>> pos;
   private static final long serialVersionUID = 1L;

   public IllegalAnnotationException(String message, Locatable src) {
      super(message);
      this.pos = this.build(src);
   }

   public IllegalAnnotationException(String message, Annotation src) {
      this(message, cast(src));
   }

   public IllegalAnnotationException(String message, Locatable src1, Locatable src2) {
      super(message);
      this.pos = this.build(src1, src2);
   }

   public IllegalAnnotationException(String message, Annotation src1, Annotation src2) {
      this(message, cast(src1), cast(src2));
   }

   public IllegalAnnotationException(String message, Annotation src1, Locatable src2) {
      this(message, cast(src1), src2);
   }

   public IllegalAnnotationException(String message, Throwable cause, Locatable src) {
      super(message, cause);
      this.pos = this.build(src);
   }

   private static Locatable cast(Annotation a) {
      return a instanceof Locatable ? (Locatable)a : null;
   }

   private List<List<Location>> build(Locatable... srcs) {
      List<List<Location>> r = new ArrayList();
      Locatable[] var3 = srcs;
      int var4 = srcs.length;

      for(int var5 = 0; var5 < var4; ++var5) {
         Locatable l = var3[var5];
         if (l != null) {
            List<Location> ll = this.convert(l);
            if (ll != null && !ll.isEmpty()) {
               r.add(ll);
            }
         }
      }

      return Collections.unmodifiableList(r);
   }

   private List<Location> convert(Locatable src) {
      if (src == null) {
         return null;
      } else {
         ArrayList r;
         for(r = new ArrayList(); src != null; src = src.getUpstream()) {
            r.add(src.getLocation());
         }

         return Collections.unmodifiableList(r);
      }
   }

   public List<List<Location>> getSourcePos() {
      return this.pos;
   }

   public String toString() {
      StringBuilder sb = new StringBuilder(this.getMessage());
      Iterator var2 = this.pos.iterator();

      while(var2.hasNext()) {
         List<Location> locs = (List)var2.next();
         sb.append("\n\tthis problem is related to the following location:");
         Iterator var4 = locs.iterator();

         while(var4.hasNext()) {
            Location loc = (Location)var4.next();
            sb.append("\n\t\tat ").append(loc.toString());
         }
      }

      return sb.toString();
   }
}
