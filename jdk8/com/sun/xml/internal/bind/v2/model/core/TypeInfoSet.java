package com.sun.xml.internal.bind.v2.model.core;

import com.sun.xml.internal.bind.v2.model.nav.Navigator;
import java.util.Map;
import javax.xml.bind.JAXBException;
import javax.xml.bind.annotation.XmlNsForm;
import javax.xml.namespace.QName;
import javax.xml.transform.Result;

public interface TypeInfoSet<T, C, F, M> {
   Navigator<T, C, F, M> getNavigator();

   NonElement<T, C> getTypeInfo(T var1);

   NonElement<T, C> getAnyTypeInfo();

   NonElement<T, C> getClassInfo(C var1);

   Map<? extends T, ? extends ArrayInfo<T, C>> arrays();

   Map<C, ? extends ClassInfo<T, C>> beans();

   Map<T, ? extends BuiltinLeafInfo<T, C>> builtins();

   Map<C, ? extends EnumLeafInfo<T, C>> enums();

   ElementInfo<T, C> getElementInfo(C var1, QName var2);

   NonElement<T, C> getTypeInfo(Ref<T, C> var1);

   Map<QName, ? extends ElementInfo<T, C>> getElementMappings(C var1);

   Iterable<? extends ElementInfo<T, C>> getAllElements();

   Map<String, String> getXmlNs(String var1);

   Map<String, String> getSchemaLocations();

   XmlNsForm getElementFormDefault(String var1);

   XmlNsForm getAttributeFormDefault(String var1);

   void dump(Result var1) throws JAXBException;
}
