package com.sun.org.apache.xerces.internal.impl.xs;

import com.sun.org.apache.xerces.internal.dom.CoreDOMImplementationImpl;
import com.sun.org.apache.xerces.internal.dom.DOMMessageFormatter;
import com.sun.org.apache.xerces.internal.impl.xs.util.StringListImpl;
import com.sun.org.apache.xerces.internal.xs.StringList;
import com.sun.org.apache.xerces.internal.xs.XSException;
import com.sun.org.apache.xerces.internal.xs.XSImplementation;
import com.sun.org.apache.xerces.internal.xs.XSLoader;
import org.w3c.dom.DOMImplementation;

public class XSImplementationImpl extends CoreDOMImplementationImpl implements XSImplementation {
   static XSImplementationImpl singleton = new XSImplementationImpl();

   public static DOMImplementation getDOMImplementation() {
      return singleton;
   }

   public boolean hasFeature(String feature, String version) {
      return feature.equalsIgnoreCase("XS-Loader") && (version == null || version.equals("1.0")) || super.hasFeature(feature, version);
   }

   public XSLoader createXSLoader(StringList versions) throws XSException {
      XSLoader loader = new XSLoaderImpl();
      if (versions == null) {
         return loader;
      } else {
         for(int i = 0; i < versions.getLength(); ++i) {
            if (!versions.item(i).equals("1.0")) {
               String msg = DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "FEATURE_NOT_SUPPORTED", new Object[]{versions.item(i)});
               throw new XSException((short)1, msg);
            }
         }

         return loader;
      }
   }

   public StringList getRecognizedVersions() {
      StringListImpl list = new StringListImpl(new String[]{"1.0"}, 1);
      return list;
   }
}
