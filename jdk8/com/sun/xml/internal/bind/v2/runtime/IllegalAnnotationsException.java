package com.sun.xml.internal.bind.v2.runtime;

import com.sun.xml.internal.bind.v2.model.core.ErrorHandler;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import javax.xml.bind.JAXBException;

public class IllegalAnnotationsException extends JAXBException {
   private final List<IllegalAnnotationException> errors;
   private static final long serialVersionUID = 1L;

   public IllegalAnnotationsException(List<IllegalAnnotationException> errors) {
      super(errors.size() + " counts of IllegalAnnotationExceptions");

      assert !errors.isEmpty() : "there must be at least one error";

      this.errors = Collections.unmodifiableList(new ArrayList(errors));
   }

   public String toString() {
      StringBuilder sb = new StringBuilder(super.toString());
      sb.append('\n');
      Iterator var2 = this.errors.iterator();

      while(var2.hasNext()) {
         IllegalAnnotationException error = (IllegalAnnotationException)var2.next();
         sb.append(error.toString()).append('\n');
      }

      return sb.toString();
   }

   public List<IllegalAnnotationException> getErrors() {
      return this.errors;
   }

   public static class Builder implements ErrorHandler {
      private final List<IllegalAnnotationException> list = new ArrayList();

      public void error(IllegalAnnotationException e) {
         this.list.add(e);
      }

      public void check() throws IllegalAnnotationsException {
         if (!this.list.isEmpty()) {
            throw new IllegalAnnotationsException(this.list);
         }
      }
   }
}
