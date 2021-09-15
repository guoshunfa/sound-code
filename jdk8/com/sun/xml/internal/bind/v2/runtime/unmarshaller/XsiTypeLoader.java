package com.sun.xml.internal.bind.v2.runtime.unmarshaller;

import com.sun.istack.internal.Nullable;
import com.sun.xml.internal.bind.DatatypeConverterImpl;
import com.sun.xml.internal.bind.v2.runtime.JAXBContextImpl;
import com.sun.xml.internal.bind.v2.runtime.JaxBeanInfo;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import javax.xml.namespace.QName;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

public class XsiTypeLoader extends Loader {
   private final JaxBeanInfo defaultBeanInfo;
   static final QName XsiTypeQNAME = new QName("http://www.w3.org/2001/XMLSchema-instance", "type");

   public XsiTypeLoader(JaxBeanInfo defaultBeanInfo) {
      super(true);
      this.defaultBeanInfo = defaultBeanInfo;
   }

   public void startElement(UnmarshallingContext.State state, TagName ea) throws SAXException {
      JaxBeanInfo beanInfo = parseXsiType(state, ea, this.defaultBeanInfo);
      if (beanInfo == null) {
         beanInfo = this.defaultBeanInfo;
      }

      Loader loader = beanInfo.getLoader((JAXBContextImpl)null, false);
      state.setLoader(loader);
      loader.startElement(state, ea);
   }

   static JaxBeanInfo parseXsiType(UnmarshallingContext.State state, TagName ea, @Nullable JaxBeanInfo defaultBeanInfo) throws SAXException {
      UnmarshallingContext context = state.getContext();
      JaxBeanInfo beanInfo = null;
      Attributes atts = ea.atts;
      int idx = atts.getIndex("http://www.w3.org/2001/XMLSchema-instance", "type");
      if (idx >= 0) {
         String value = atts.getValue(idx);
         QName type = DatatypeConverterImpl._parseQName(value, context);
         if (type == null) {
            reportError(Messages.NOT_A_QNAME.format(value), true);
         } else {
            if (defaultBeanInfo != null && defaultBeanInfo.getTypeNames().contains(type)) {
               return defaultBeanInfo;
            }

            beanInfo = context.getJAXBContext().getGlobalType(type);
            if (beanInfo == null && context.parent.hasEventHandler() && context.shouldErrorBeReported()) {
               String nearest = context.getJAXBContext().getNearestTypeName(type);
               if (nearest != null) {
                  reportError(Messages.UNRECOGNIZED_TYPE_NAME_MAYBE.format(type, nearest), true);
               } else {
                  reportError(Messages.UNRECOGNIZED_TYPE_NAME.format(type), true);
               }
            }
         }
      }

      return beanInfo;
   }

   public Collection<QName> getExpectedAttributes() {
      Collection<QName> expAttrs = new HashSet();
      expAttrs.addAll(super.getExpectedAttributes());
      expAttrs.add(XsiTypeQNAME);
      return Collections.unmodifiableCollection(expAttrs);
   }
}
