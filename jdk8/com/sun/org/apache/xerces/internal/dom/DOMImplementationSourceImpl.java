package com.sun.org.apache.xerces.internal.dom;

import java.util.StringTokenizer;
import java.util.Vector;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.DOMImplementationList;
import org.w3c.dom.DOMImplementationSource;

public class DOMImplementationSourceImpl implements DOMImplementationSource {
   public DOMImplementation getDOMImplementation(String features) {
      DOMImplementation impl = CoreDOMImplementationImpl.getDOMImplementation();
      if (this.testImpl(impl, features)) {
         return impl;
      } else {
         impl = DOMImplementationImpl.getDOMImplementation();
         return this.testImpl(impl, features) ? impl : null;
      }
   }

   public DOMImplementationList getDOMImplementationList(String features) {
      DOMImplementation impl = CoreDOMImplementationImpl.getDOMImplementation();
      Vector implementations = new Vector();
      if (this.testImpl(impl, features)) {
         implementations.addElement(impl);
      }

      impl = DOMImplementationImpl.getDOMImplementation();
      if (this.testImpl(impl, features)) {
         implementations.addElement(impl);
      }

      return new DOMImplementationListImpl(implementations);
   }

   boolean testImpl(DOMImplementation impl, String features) {
      StringTokenizer st = new StringTokenizer(features);
      String feature = null;
      String version = null;
      if (st.hasMoreTokens()) {
         feature = st.nextToken();
      }

      while(feature != null) {
         boolean isVersion = false;
         if (st.hasMoreTokens()) {
            version = st.nextToken();
            char c = version.charAt(0);
            switch(c) {
            case '0':
            case '1':
            case '2':
            case '3':
            case '4':
            case '5':
            case '6':
            case '7':
            case '8':
            case '9':
               isVersion = true;
            }
         } else {
            version = null;
         }

         if (isVersion) {
            if (!impl.hasFeature(feature, version)) {
               return false;
            }

            if (st.hasMoreTokens()) {
               feature = st.nextToken();
            } else {
               feature = null;
            }
         } else {
            if (!impl.hasFeature(feature, (String)null)) {
               return false;
            }

            feature = version;
         }
      }

      return true;
   }
}
