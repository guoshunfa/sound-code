package com.sun.xml.internal.ws.wsdl.parser;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import javax.xml.ws.WebServiceException;

public class InaccessibleWSDLException extends WebServiceException {
   private final List<Throwable> errors;
   private static final long serialVersionUID = 1L;

   public InaccessibleWSDLException(List<Throwable> errors) {
      super(errors.size() + " counts of InaccessibleWSDLException.\n");

      assert !errors.isEmpty() : "there must be at least one error";

      this.errors = Collections.unmodifiableList(new ArrayList(errors));
   }

   public String toString() {
      StringBuilder sb = new StringBuilder(super.toString());
      sb.append('\n');
      Iterator var2 = this.errors.iterator();

      while(var2.hasNext()) {
         Throwable error = (Throwable)var2.next();
         sb.append(error.toString()).append('\n');
      }

      return sb.toString();
   }

   public List<Throwable> getErrors() {
      return this.errors;
   }

   public static class Builder implements ErrorHandler {
      private final List<Throwable> list = new ArrayList();

      public void error(Throwable e) {
         this.list.add(e);
      }

      public void check() throws InaccessibleWSDLException {
         if (!this.list.isEmpty()) {
            throw new InaccessibleWSDLException(this.list);
         }
      }
   }
}
