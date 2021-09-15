package com.sun.org.apache.xerces.internal.dom;

import com.sun.org.apache.xerces.internal.impl.xs.XSImplementationImpl;
import java.util.Vector;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.DOMImplementationList;

public class DOMXSImplementationSourceImpl extends DOMImplementationSourceImpl {
   public DOMImplementation getDOMImplementation(String features) {
      DOMImplementation impl = super.getDOMImplementation(features);
      if (impl != null) {
         return impl;
      } else {
         impl = PSVIDOMImplementationImpl.getDOMImplementation();
         if (this.testImpl(impl, features)) {
            return impl;
         } else {
            impl = XSImplementationImpl.getDOMImplementation();
            return this.testImpl(impl, features) ? impl : null;
         }
      }
   }

   public DOMImplementationList getDOMImplementationList(String features) {
      Vector implementations = new Vector();
      DOMImplementationList list = super.getDOMImplementationList(features);

      for(int i = 0; i < list.getLength(); ++i) {
         implementations.addElement(list.item(i));
      }

      DOMImplementation impl = PSVIDOMImplementationImpl.getDOMImplementation();
      if (this.testImpl(impl, features)) {
         implementations.addElement(impl);
      }

      impl = XSImplementationImpl.getDOMImplementation();
      if (this.testImpl(impl, features)) {
         implementations.addElement(impl);
      }

      return new DOMImplementationListImpl(implementations);
   }
}
