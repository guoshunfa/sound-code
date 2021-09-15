package com.sun.xml.internal.bind.v2.runtime.property;

import com.sun.xml.internal.bind.api.AccessorException;
import com.sun.xml.internal.bind.v2.model.core.PropertyKind;
import com.sun.xml.internal.bind.v2.model.runtime.RuntimePropertyInfo;
import com.sun.xml.internal.bind.v2.runtime.XMLSerializer;
import com.sun.xml.internal.bind.v2.runtime.reflect.Accessor;
import java.io.IOException;
import javax.xml.stream.XMLStreamException;
import org.xml.sax.SAXException;

public interface Property<BeanT> extends StructureLoaderBuilder {
   void reset(BeanT var1) throws AccessorException;

   void serializeBody(BeanT var1, XMLSerializer var2, Object var3) throws SAXException, AccessorException, IOException, XMLStreamException;

   void serializeURIs(BeanT var1, XMLSerializer var2) throws SAXException, AccessorException;

   boolean hasSerializeURIAction();

   String getIdValue(BeanT var1) throws AccessorException, SAXException;

   PropertyKind getKind();

   Accessor getElementPropertyAccessor(String var1, String var2);

   void wrapUp();

   RuntimePropertyInfo getInfo();

   boolean isHiddenByOverride();

   void setHiddenByOverride(boolean var1);

   String getFieldName();
}
