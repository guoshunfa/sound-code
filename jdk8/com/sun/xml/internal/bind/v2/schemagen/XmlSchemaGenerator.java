package com.sun.xml.internal.bind.v2.schemagen;

import com.sun.istack.internal.NotNull;
import com.sun.istack.internal.Nullable;
import com.sun.xml.internal.bind.api.CompositeStructure;
import com.sun.xml.internal.bind.api.ErrorListener;
import com.sun.xml.internal.bind.v2.TODO;
import com.sun.xml.internal.bind.v2.model.core.Adapter;
import com.sun.xml.internal.bind.v2.model.core.ArrayInfo;
import com.sun.xml.internal.bind.v2.model.core.AttributePropertyInfo;
import com.sun.xml.internal.bind.v2.model.core.ClassInfo;
import com.sun.xml.internal.bind.v2.model.core.Element;
import com.sun.xml.internal.bind.v2.model.core.ElementInfo;
import com.sun.xml.internal.bind.v2.model.core.ElementPropertyInfo;
import com.sun.xml.internal.bind.v2.model.core.EnumConstant;
import com.sun.xml.internal.bind.v2.model.core.EnumLeafInfo;
import com.sun.xml.internal.bind.v2.model.core.MapPropertyInfo;
import com.sun.xml.internal.bind.v2.model.core.MaybeElement;
import com.sun.xml.internal.bind.v2.model.core.NonElement;
import com.sun.xml.internal.bind.v2.model.core.NonElementRef;
import com.sun.xml.internal.bind.v2.model.core.PropertyInfo;
import com.sun.xml.internal.bind.v2.model.core.ReferencePropertyInfo;
import com.sun.xml.internal.bind.v2.model.core.TypeInfo;
import com.sun.xml.internal.bind.v2.model.core.TypeInfoSet;
import com.sun.xml.internal.bind.v2.model.core.TypeRef;
import com.sun.xml.internal.bind.v2.model.core.ValuePropertyInfo;
import com.sun.xml.internal.bind.v2.model.core.WildcardMode;
import com.sun.xml.internal.bind.v2.model.impl.ClassInfoImpl;
import com.sun.xml.internal.bind.v2.model.nav.Navigator;
import com.sun.xml.internal.bind.v2.runtime.SwaRefAdapter;
import com.sun.xml.internal.bind.v2.schemagen.episode.Bindings;
import com.sun.xml.internal.bind.v2.schemagen.xmlschema.Any;
import com.sun.xml.internal.bind.v2.schemagen.xmlschema.AttrDecls;
import com.sun.xml.internal.bind.v2.schemagen.xmlschema.AttributeType;
import com.sun.xml.internal.bind.v2.schemagen.xmlschema.ComplexExtension;
import com.sun.xml.internal.bind.v2.schemagen.xmlschema.ComplexType;
import com.sun.xml.internal.bind.v2.schemagen.xmlschema.ComplexTypeHost;
import com.sun.xml.internal.bind.v2.schemagen.xmlschema.ContentModelContainer;
import com.sun.xml.internal.bind.v2.schemagen.xmlschema.ExplicitGroup;
import com.sun.xml.internal.bind.v2.schemagen.xmlschema.Import;
import com.sun.xml.internal.bind.v2.schemagen.xmlschema.LocalAttribute;
import com.sun.xml.internal.bind.v2.schemagen.xmlschema.LocalElement;
import com.sun.xml.internal.bind.v2.schemagen.xmlschema.Schema;
import com.sun.xml.internal.bind.v2.schemagen.xmlschema.SimpleExtension;
import com.sun.xml.internal.bind.v2.schemagen.xmlschema.SimpleRestrictionModel;
import com.sun.xml.internal.bind.v2.schemagen.xmlschema.SimpleType;
import com.sun.xml.internal.bind.v2.schemagen.xmlschema.SimpleTypeHost;
import com.sun.xml.internal.bind.v2.schemagen.xmlschema.TopLevelAttribute;
import com.sun.xml.internal.bind.v2.schemagen.xmlschema.TopLevelElement;
import com.sun.xml.internal.bind.v2.schemagen.xmlschema.TypeDefParticle;
import com.sun.xml.internal.bind.v2.schemagen.xmlschema.TypeHost;
import com.sun.xml.internal.bind.v2.util.CollisionCheckStack;
import com.sun.xml.internal.bind.v2.util.StackRecorder;
import com.sun.xml.internal.txw2.TXW;
import com.sun.xml.internal.txw2.TxwException;
import com.sun.xml.internal.txw2.TypedXmlWriter;
import com.sun.xml.internal.txw2.output.ResultFactory;
import com.sun.xml.internal.txw2.output.XmlSerializer;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.Writer;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.activation.MimeType;
import javax.xml.bind.SchemaOutputResolver;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.namespace.QName;
import javax.xml.transform.Result;
import javax.xml.transform.stream.StreamResult;
import org.xml.sax.Locator;
import org.xml.sax.SAXParseException;

public final class XmlSchemaGenerator<T, C, F, M> {
   private static final Logger logger = com.sun.xml.internal.bind.Util.getClassLogger();
   private final Map<String, XmlSchemaGenerator<T, C, F, M>.Namespace> namespaces;
   private ErrorListener errorListener;
   private Navigator<T, C, F, M> navigator;
   private final TypeInfoSet<T, C, F, M> types;
   private final NonElement<T, C> stringType;
   private final NonElement<T, C> anyType;
   private final CollisionCheckStack<ClassInfo<T, C>> collisionChecker;
   private static final Comparator<String> NAMESPACE_COMPARATOR = new Comparator<String>() {
      public int compare(String lhs, String rhs) {
         return -lhs.compareTo(rhs);
      }
   };
   private static final String newline = "\n";

   public XmlSchemaGenerator(Navigator<T, C, F, M> navigator, TypeInfoSet<T, C, F, M> types) {
      this.namespaces = new TreeMap(NAMESPACE_COMPARATOR);
      this.collisionChecker = new CollisionCheckStack();
      this.navigator = navigator;
      this.types = types;
      this.stringType = types.getTypeInfo(navigator.ref(String.class));
      this.anyType = types.getAnyTypeInfo();
      Iterator var3 = types.beans().values().iterator();

      while(var3.hasNext()) {
         ClassInfo<T, C> ci = (ClassInfo)var3.next();
         this.add(ci);
      }

      var3 = types.getElementMappings((Object)null).values().iterator();

      while(var3.hasNext()) {
         ElementInfo<T, C> ei1 = (ElementInfo)var3.next();
         this.add(ei1);
      }

      var3 = types.enums().values().iterator();

      while(var3.hasNext()) {
         EnumLeafInfo<T, C> ei = (EnumLeafInfo)var3.next();
         this.add(ei);
      }

      var3 = types.arrays().values().iterator();

      while(var3.hasNext()) {
         ArrayInfo<T, C> a = (ArrayInfo)var3.next();
         this.add(a);
      }

   }

   private XmlSchemaGenerator<T, C, F, M>.Namespace getNamespace(String uri) {
      XmlSchemaGenerator<T, C, F, M>.Namespace n = (XmlSchemaGenerator.Namespace)this.namespaces.get(uri);
      if (n == null) {
         this.namespaces.put(uri, n = new XmlSchemaGenerator.Namespace(uri));
      }

      return n;
   }

   public void add(ClassInfo<T, C> clazz) {
      assert clazz != null;

      String nsUri = null;
      if (clazz.getClazz() != this.navigator.asDecl(CompositeStructure.class)) {
         if (clazz.isElement()) {
            nsUri = clazz.getElementName().getNamespaceURI();
            XmlSchemaGenerator<T, C, F, M>.Namespace ns = this.getNamespace(nsUri);
            ns.classes.add(clazz);
            ns.addDependencyTo(clazz.getTypeName());
            this.add(clazz.getElementName(), false, clazz);
         }

         QName tn = clazz.getTypeName();
         if (tn != null) {
            nsUri = tn.getNamespaceURI();
         } else if (nsUri == null) {
            return;
         }

         XmlSchemaGenerator<T, C, F, M>.Namespace n = this.getNamespace(nsUri);
         n.classes.add(clazz);
         Iterator var5 = clazz.getProperties().iterator();

         while(var5.hasNext()) {
            PropertyInfo<T, C> p = (PropertyInfo)var5.next();
            n.processForeignNamespaces(p, 1);
            if (p instanceof AttributePropertyInfo) {
               AttributePropertyInfo<T, C> ap = (AttributePropertyInfo)p;
               String aUri = ap.getXmlName().getNamespaceURI();
               if (aUri.length() > 0) {
                  this.getNamespace(aUri).addGlobalAttribute(ap);
                  n.addDependencyTo(ap.getXmlName());
               }
            }

            if (p instanceof ElementPropertyInfo) {
               ElementPropertyInfo<T, C> ep = (ElementPropertyInfo)p;
               Iterator var15 = ep.getTypes().iterator();

               while(var15.hasNext()) {
                  TypeRef<T, C> tref = (TypeRef)var15.next();
                  String eUri = tref.getTagName().getNamespaceURI();
                  if (eUri.length() > 0 && !eUri.equals(n.uri)) {
                     this.getNamespace(eUri).addGlobalElement(tref);
                     n.addDependencyTo(tref.getTagName());
                  }
               }
            }

            if (this.generateSwaRefAdapter(p)) {
               n.useSwaRef = true;
            }

            MimeType mimeType = p.getExpectedMimeType();
            if (mimeType != null) {
               n.useMimeNs = true;
            }
         }

         ClassInfo<T, C> bc = clazz.getBaseClass();
         if (bc != null) {
            this.add(bc);
            n.addDependencyTo(bc.getTypeName());
         }

      }
   }

   public void add(ElementInfo<T, C> elem) {
      assert elem != null;

      boolean nillable = false;
      QName name = elem.getElementName();
      XmlSchemaGenerator<T, C, F, M>.Namespace n = this.getNamespace(name.getNamespaceURI());
      ElementInfo ei;
      if (elem.getScope() != null) {
         ei = this.types.getElementInfo(elem.getScope().getClazz(), name);
      } else {
         ei = this.types.getElementInfo((Object)null, name);
      }

      XmlElement xmlElem = (XmlElement)ei.getProperty().readAnnotation(XmlElement.class);
      if (xmlElem == null) {
         nillable = false;
      } else {
         nillable = xmlElem.nillable();
      }

      n.elementDecls.put((Comparable)name.getLocalPart(), n.new ElementWithType(nillable, elem.getContentType()));
      n.processForeignNamespaces(elem.getProperty(), 1);
   }

   public void add(EnumLeafInfo<T, C> envm) {
      assert envm != null;

      String nsUri = null;
      if (envm.isElement()) {
         nsUri = envm.getElementName().getNamespaceURI();
         XmlSchemaGenerator<T, C, F, M>.Namespace ns = this.getNamespace(nsUri);
         ns.enums.add(envm);
         ns.addDependencyTo(envm.getTypeName());
         this.add(envm.getElementName(), false, envm);
      }

      QName typeName = envm.getTypeName();
      if (typeName != null) {
         nsUri = typeName.getNamespaceURI();
      } else if (nsUri == null) {
         return;
      }

      XmlSchemaGenerator<T, C, F, M>.Namespace n = this.getNamespace(nsUri);
      n.enums.add(envm);
      n.addDependencyTo(envm.getBaseType().getTypeName());
   }

   public void add(ArrayInfo<T, C> a) {
      assert a != null;

      String namespaceURI = a.getTypeName().getNamespaceURI();
      XmlSchemaGenerator<T, C, F, M>.Namespace n = this.getNamespace(namespaceURI);
      n.arrays.add(a);
      n.addDependencyTo(a.getItemType().getTypeName());
   }

   public void add(QName tagName, boolean isNillable, NonElement<T, C> type) {
      if (type == null || type.getType() != this.navigator.ref(CompositeStructure.class)) {
         XmlSchemaGenerator<T, C, F, M>.Namespace n = this.getNamespace(tagName.getNamespaceURI());
         n.elementDecls.put((Comparable)tagName.getLocalPart(), n.new ElementWithType(isNillable, type));
         if (type != null) {
            n.addDependencyTo(type.getTypeName());
         }

      }
   }

   public void writeEpisodeFile(XmlSerializer out) {
      Bindings root = (Bindings)TXW.create(Bindings.class, out);
      if (this.namespaces.containsKey("")) {
         root._namespace("http://java.sun.com/xml/ns/jaxb", "jaxb");
      }

      root.version("2.1");
      Iterator var3 = this.namespaces.entrySet().iterator();

      while(var3.hasNext()) {
         Map.Entry<String, XmlSchemaGenerator<T, C, F, M>.Namespace> e = (Map.Entry)var3.next();
         Bindings group = root.bindings();
         String tns = (String)e.getKey();
         String prefix;
         if (!tns.equals("")) {
            group._namespace(tns, "tns");
            prefix = "tns:";
         } else {
            prefix = "";
         }

         group.scd("x-schema::" + (tns.equals("") ? "" : "tns"));
         group.schemaBindings().map(false);
         Iterator var8 = ((XmlSchemaGenerator.Namespace)e.getValue()).classes.iterator();

         Bindings child;
         while(var8.hasNext()) {
            ClassInfo<T, C> ci = (ClassInfo)var8.next();
            if (ci.getTypeName() != null) {
               if (ci.getTypeName().getNamespaceURI().equals(tns)) {
                  child = group.bindings();
                  child.scd('~' + prefix + ci.getTypeName().getLocalPart());
                  child.klass().ref(ci.getName());
               }

               if (ci.isElement() && ci.getElementName().getNamespaceURI().equals(tns)) {
                  child = group.bindings();
                  child.scd(prefix + ci.getElementName().getLocalPart());
                  child.klass().ref(ci.getName());
               }
            }
         }

         var8 = ((XmlSchemaGenerator.Namespace)e.getValue()).enums.iterator();

         while(var8.hasNext()) {
            EnumLeafInfo<T, C> en = (EnumLeafInfo)var8.next();
            if (en.getTypeName() != null) {
               child = group.bindings();
               child.scd('~' + prefix + en.getTypeName().getLocalPart());
               child.klass().ref(this.navigator.getClassName(en.getClazz()));
            }
         }

         group.commit(true);
      }

      root.commit();
   }

   public void write(SchemaOutputResolver resolver, ErrorListener errorListener) throws IOException {
      if (resolver == null) {
         throw new IllegalArgumentException();
      } else {
         if (logger.isLoggable(Level.FINE)) {
            logger.log(Level.FINE, (String)("Writing XML Schema for " + this.toString()), (Throwable)(new StackRecorder()));
         }

         SchemaOutputResolver resolver = new FoolProofResolver(resolver);
         this.errorListener = errorListener;
         Map<String, String> schemaLocations = this.types.getSchemaLocations();
         Map<XmlSchemaGenerator<T, C, F, M>.Namespace, Result> out = new HashMap();
         Map<XmlSchemaGenerator<T, C, F, M>.Namespace, String> systemIds = new HashMap();
         this.namespaces.remove("http://www.w3.org/2001/XMLSchema");

         Iterator var6;
         XmlSchemaGenerator.Namespace n;
         for(var6 = this.namespaces.values().iterator(); var6.hasNext(); n.resetWritten()) {
            n = (XmlSchemaGenerator.Namespace)var6.next();
            String schemaLocation = (String)schemaLocations.get(n.uri);
            if (schemaLocation != null) {
               systemIds.put(n, schemaLocation);
            } else {
               Result output = resolver.createOutput(n.uri, "schema" + (out.size() + 1) + ".xsd");
               if (output != null) {
                  out.put(n, output);
                  systemIds.put(n, output.getSystemId());
               }
            }
         }

         var6 = out.entrySet().iterator();

         while(var6.hasNext()) {
            Map.Entry<XmlSchemaGenerator<T, C, F, M>.Namespace, Result> e = (Map.Entry)var6.next();
            Result result = (Result)e.getValue();
            ((XmlSchemaGenerator.Namespace)e.getKey()).writeTo(result, systemIds);
            if (result instanceof StreamResult) {
               OutputStream outputStream = ((StreamResult)result).getOutputStream();
               if (outputStream != null) {
                  outputStream.close();
               } else {
                  Writer writer = ((StreamResult)result).getWriter();
                  if (writer != null) {
                     writer.close();
                  }
               }
            }
         }

      }
   }

   private boolean generateSwaRefAdapter(NonElementRef<T, C> typeRef) {
      return this.generateSwaRefAdapter(typeRef.getSource());
   }

   private boolean generateSwaRefAdapter(PropertyInfo<T, C> prop) {
      Adapter<T, C> adapter = prop.getAdapter();
      if (adapter == null) {
         return false;
      } else {
         Object o = this.navigator.asDecl(SwaRefAdapter.class);
         return o == null ? false : o.equals(adapter.adapterType);
      }
   }

   public String toString() {
      StringBuilder buf = new StringBuilder();

      XmlSchemaGenerator.Namespace ns;
      for(Iterator var2 = this.namespaces.values().iterator(); var2.hasNext(); buf.append(ns.uri).append('=').append((Object)ns)) {
         ns = (XmlSchemaGenerator.Namespace)var2.next();
         if (buf.length() > 0) {
            buf.append(',');
         }
      }

      return super.toString() + '[' + buf + ']';
   }

   private static String getProcessContentsModeName(WildcardMode wc) {
      switch(wc) {
      case LAX:
      case SKIP:
         return wc.name().toLowerCase();
      case STRICT:
         return null;
      default:
         throw new IllegalStateException();
      }
   }

   protected static String relativize(String uri, String baseUri) {
      try {
         assert uri != null;

         if (baseUri == null) {
            return uri;
         } else {
            URI theUri = new URI(Util.escapeURI(uri));
            URI theBaseUri = new URI(Util.escapeURI(baseUri));
            if (!theUri.isOpaque() && !theBaseUri.isOpaque()) {
               if (Util.equalsIgnoreCase(theUri.getScheme(), theBaseUri.getScheme()) && Util.equal(theUri.getAuthority(), theBaseUri.getAuthority())) {
                  String uriPath = theUri.getPath();
                  String basePath = theBaseUri.getPath();
                  if (!basePath.endsWith("/")) {
                     basePath = Util.normalizeUriPath(basePath);
                  }

                  if (uriPath.equals(basePath)) {
                     return ".";
                  } else {
                     String relPath = calculateRelativePath(uriPath, basePath, fixNull(theUri.getScheme()).equals("file"));
                     if (relPath == null) {
                        return uri;
                     } else {
                        StringBuilder relUri = new StringBuilder();
                        relUri.append(relPath);
                        if (theUri.getQuery() != null) {
                           relUri.append('?').append(theUri.getQuery());
                        }

                        if (theUri.getFragment() != null) {
                           relUri.append('#').append(theUri.getFragment());
                        }

                        return relUri.toString();
                     }
                  }
               } else {
                  return uri;
               }
            } else {
               return uri;
            }
         }
      } catch (URISyntaxException var8) {
         throw new InternalError("Error escaping one of these uris:\n\t" + uri + "\n\t" + baseUri);
      }
   }

   private static String fixNull(String s) {
      return s == null ? "" : s;
   }

   private static String calculateRelativePath(String uri, String base, boolean fileUrl) {
      boolean onWindows = File.pathSeparatorChar == ';';
      if (base == null) {
         return null;
      } else {
         return (!fileUrl || !onWindows || !startsWithIgnoreCase(uri, base)) && !uri.startsWith(base) ? "../" + calculateRelativePath(uri, Util.getParentUriPath(base), fileUrl) : uri.substring(base.length());
      }
   }

   private static boolean startsWithIgnoreCase(String s, String t) {
      return s.toUpperCase().startsWith(t.toUpperCase());
   }

   private class Namespace {
      @NotNull
      final String uri;
      private final Set<XmlSchemaGenerator<T, C, F, M>.Namespace> depends = new LinkedHashSet();
      private boolean selfReference;
      private final Set<ClassInfo<T, C>> classes = new LinkedHashSet();
      private final Set<EnumLeafInfo<T, C>> enums = new LinkedHashSet();
      private final Set<ArrayInfo<T, C>> arrays = new LinkedHashSet();
      private final MultiMap<String, AttributePropertyInfo<T, C>> attributeDecls = new MultiMap((Object)null);
      private final MultiMap<String, XmlSchemaGenerator<T, C, F, M>.Namespace.ElementDeclaration> elementDecls;
      private Form attributeFormDefault;
      private Form elementFormDefault;
      private boolean useSwaRef;
      private boolean useMimeNs;
      private final Set<ClassInfo> written;

      public Namespace(String uri) {
         this.elementDecls = new MultiMap(new XmlSchemaGenerator.Namespace.ElementWithType(true, XmlSchemaGenerator.this.anyType));
         this.written = new HashSet();
         this.uri = uri;

         assert !XmlSchemaGenerator.this.namespaces.containsKey(uri);

         XmlSchemaGenerator.this.namespaces.put(uri, this);
      }

      void resetWritten() {
         this.written.clear();
      }

      private void processForeignNamespaces(PropertyInfo<T, C> p, int processingDepth) {
         Iterator var3 = p.ref().iterator();

         while(var3.hasNext()) {
            TypeInfo<T, C> t = (TypeInfo)var3.next();
            if (t instanceof ClassInfo && processingDepth > 0) {
               List<PropertyInfo> l = ((ClassInfo)t).getProperties();
               Iterator var6 = l.iterator();

               while(var6.hasNext()) {
                  PropertyInfo subp = (PropertyInfo)var6.next();
                  --processingDepth;
                  this.processForeignNamespaces(subp, processingDepth);
               }
            }

            if (t instanceof Element) {
               this.addDependencyTo(((Element)t).getElementName());
            }

            if (t instanceof NonElement) {
               this.addDependencyTo(((NonElement)t).getTypeName());
            }
         }

      }

      private void addDependencyTo(@Nullable QName qname) {
         if (qname != null) {
            String nsUri = qname.getNamespaceURI();
            if (!nsUri.equals("http://www.w3.org/2001/XMLSchema")) {
               if (nsUri.equals(this.uri)) {
                  this.selfReference = true;
               } else {
                  this.depends.add(XmlSchemaGenerator.this.getNamespace(nsUri));
               }
            }
         }
      }

      private void writeTo(Result result, Map<XmlSchemaGenerator<T, C, F, M>.Namespace, String> systemIds) throws IOException {
         try {
            Schema schema = (Schema)TXW.create(Schema.class, ResultFactory.createSerializer(result));
            Map<String, String> xmlNs = XmlSchemaGenerator.this.types.getXmlNs(this.uri);
            Iterator var5 = xmlNs.entrySet().iterator();

            Map.Entry ex;
            while(var5.hasNext()) {
               ex = (Map.Entry)var5.next();
               schema._namespace((String)ex.getValue(), (String)ex.getKey());
            }

            if (this.useSwaRef) {
               schema._namespace("http://ws-i.org/profiles/basic/1.1/xsd", "swaRef");
            }

            if (this.useMimeNs) {
               schema._namespace("http://www.w3.org/2005/05/xmlmime", "xmime");
            }

            this.attributeFormDefault = Form.get(XmlSchemaGenerator.this.types.getAttributeFormDefault(this.uri));
            this.attributeFormDefault.declare("attributeFormDefault", schema);
            this.elementFormDefault = Form.get(XmlSchemaGenerator.this.types.getElementFormDefault(this.uri));
            this.elementFormDefault.declare("elementFormDefault", schema);
            if (!xmlNs.containsValue("http://www.w3.org/2001/XMLSchema") && !xmlNs.containsKey("xs")) {
               schema._namespace("http://www.w3.org/2001/XMLSchema", "xs");
            }

            schema.version("1.0");
            if (this.uri.length() != 0) {
               schema.targetNamespace(this.uri);
            }

            var5 = this.depends.iterator();

            XmlSchemaGenerator.Namespace n;
            while(var5.hasNext()) {
               n = (XmlSchemaGenerator.Namespace)var5.next();
               schema._namespace(n.uri);
            }

            if (this.selfReference && this.uri.length() != 0) {
               schema._namespace(this.uri, "tns");
            }

            schema._pcdata("\n");

            for(var5 = this.depends.iterator(); var5.hasNext(); schema._pcdata("\n")) {
               n = (XmlSchemaGenerator.Namespace)var5.next();
               Import imp = schema._import();
               if (n.uri.length() != 0) {
                  imp.namespace(n.uri);
               }

               String refSystemId = (String)systemIds.get(n);
               if (refSystemId != null && !refSystemId.equals("")) {
                  imp.schemaLocation(XmlSchemaGenerator.relativize(refSystemId, result.getSystemId()));
               }
            }

            if (this.useSwaRef) {
               schema._import().namespace("http://ws-i.org/profiles/basic/1.1/xsd").schemaLocation("http://ws-i.org/profiles/basic/1.1/swaref.xsd");
            }

            if (this.useMimeNs) {
               schema._import().namespace("http://www.w3.org/2005/05/xmlmime").schemaLocation("http://www.w3.org/2005/05/xmlmime");
            }

            var5 = this.elementDecls.entrySet().iterator();

            while(var5.hasNext()) {
               ex = (Map.Entry)var5.next();
               ((XmlSchemaGenerator.Namespace.ElementDeclaration)ex.getValue()).writeTo((String)ex.getKey(), schema);
               schema._pcdata("\n");
            }

            var5 = this.classes.iterator();

            while(var5.hasNext()) {
               ClassInfo<T, C> c = (ClassInfo)var5.next();
               if (c.getTypeName() != null) {
                  if (this.uri.equals(c.getTypeName().getNamespaceURI())) {
                     this.writeClass(c, schema);
                  }

                  schema._pcdata("\n");
               }
            }

            var5 = this.enums.iterator();

            while(var5.hasNext()) {
               EnumLeafInfo<T, C> e = (EnumLeafInfo)var5.next();
               if (e.getTypeName() != null) {
                  if (this.uri.equals(e.getTypeName().getNamespaceURI())) {
                     this.writeEnum(e, schema);
                  }

                  schema._pcdata("\n");
               }
            }

            var5 = this.arrays.iterator();

            while(var5.hasNext()) {
               ArrayInfo<T, C> ax = (ArrayInfo)var5.next();
               this.writeArray(ax, schema);
               schema._pcdata("\n");
            }

            for(var5 = this.attributeDecls.entrySet().iterator(); var5.hasNext(); schema._pcdata("\n")) {
               ex = (Map.Entry)var5.next();
               TopLevelAttribute a = schema.attribute();
               a.name((String)ex.getKey());
               if (ex.getValue() == null) {
                  this.writeTypeRef(a, (NonElement)XmlSchemaGenerator.this.stringType, "type");
               } else {
                  this.writeAttributeTypeRef((AttributePropertyInfo)ex.getValue(), a);
               }
            }

            schema.commit();
         } catch (TxwException var9) {
            XmlSchemaGenerator.logger.log(Level.INFO, (String)var9.getMessage(), (Throwable)var9);
            throw new IOException(var9.getMessage());
         }
      }

      private void writeTypeRef(TypeHost th, NonElementRef<T, C> typeRef, String refAttName) {
         switch(typeRef.getSource().id()) {
         case ID:
            th._attribute(refAttName, new QName("http://www.w3.org/2001/XMLSchema", "ID"));
            return;
         case IDREF:
            th._attribute(refAttName, new QName("http://www.w3.org/2001/XMLSchema", "IDREF"));
            return;
         case NONE:
            MimeType mimeType = typeRef.getSource().getExpectedMimeType();
            if (mimeType != null) {
               th._attribute(new QName("http://www.w3.org/2005/05/xmlmime", "expectedContentTypes", "xmime"), mimeType.toString());
            }

            if (XmlSchemaGenerator.this.generateSwaRefAdapter(typeRef)) {
               th._attribute(refAttName, new QName("http://ws-i.org/profiles/basic/1.1/xsd", "swaRef", "ref"));
               return;
            } else {
               if (typeRef.getSource().getSchemaType() != null) {
                  th._attribute(refAttName, typeRef.getSource().getSchemaType());
                  return;
               }

               this.writeTypeRef(th, typeRef.getTarget(), refAttName);
               return;
            }
         default:
            throw new IllegalStateException();
         }
      }

      private void writeTypeRef(TypeHost th, NonElement<T, C> type, String refAttName) {
         Element e = null;
         if (type instanceof MaybeElement) {
            MaybeElement me = (MaybeElement)type;
            boolean isElement = me.isElement();
            if (isElement) {
               e = me.asElement();
            }
         }

         if (type instanceof Element) {
            e = (Element)type;
         }

         if (type.getTypeName() == null) {
            if (e != null && e.getElementName() != null) {
               th.block();
               if (type instanceof ClassInfo) {
                  this.writeClass((ClassInfo)type, th);
               } else {
                  this.writeEnum((EnumLeafInfo)type, (SimpleTypeHost)th);
               }
            } else {
               th.block();
               if (type instanceof ClassInfo) {
                  if (XmlSchemaGenerator.this.collisionChecker.push((ClassInfo)type)) {
                     XmlSchemaGenerator.this.errorListener.warning(new SAXParseException(Messages.ANONYMOUS_TYPE_CYCLE.format(XmlSchemaGenerator.this.collisionChecker.getCycleString()), (Locator)null));
                  } else {
                     this.writeClass((ClassInfo)type, th);
                  }

                  XmlSchemaGenerator.this.collisionChecker.pop();
               } else {
                  this.writeEnum((EnumLeafInfo)type, (SimpleTypeHost)th);
               }
            }
         } else {
            th._attribute(refAttName, type.getTypeName());
         }

      }

      private void writeArray(ArrayInfo<T, C> a, Schema schema) {
         ComplexType ct = schema.complexType().name(a.getTypeName().getLocalPart());
         ct._final("#all");
         LocalElement le = ct.sequence().element().name("item");
         le.type(a.getItemType().getTypeName());
         le.minOccurs(0).maxOccurs("unbounded");
         le.nillable(true);
         ct.commit();
      }

      private void writeEnum(EnumLeafInfo<T, C> e, SimpleTypeHost th) {
         SimpleType st = th.simpleType();
         this.writeName(e, st);
         SimpleRestrictionModel base = st.restriction();
         this.writeTypeRef(base, (NonElement)e.getBaseType(), "base");
         Iterator var5 = e.getConstants().iterator();

         while(var5.hasNext()) {
            EnumConstant c = (EnumConstant)var5.next();
            base.enumeration().value(c.getLexicalValue());
         }

         st.commit();
      }

      private void writeClass(ClassInfo<T, C> c, TypeHost parent) {
         if (!this.written.contains(c)) {
            this.written.add(c);
            ComplexType ct;
            if (this.containsValueProp(c)) {
               if (c.getProperties().size() == 1) {
                  ValuePropertyInfo<T, C> vp = (ValuePropertyInfo)c.getProperties().get(0);
                  SimpleType st = ((SimpleTypeHost)parent).simpleType();
                  this.writeName(c, st);
                  if (vp.isCollection()) {
                     this.writeTypeRef(st.list(), (NonElement)vp.getTarget(), "itemType");
                  } else {
                     this.writeTypeRef(st.restriction(), (NonElement)vp.getTarget(), "base");
                  }

               } else {
                  ct = ((ComplexTypeHost)parent).complexType();
                  this.writeName(c, ct);
                  if (c.isFinal()) {
                     ct._final("extension restriction");
                  }

                  SimpleExtension se = ct.simpleContent().extension();
                  se.block();
                  Iterator var14 = c.getProperties().iterator();

                  while(var14.hasNext()) {
                     PropertyInfo<T, C> p = (PropertyInfo)var14.next();
                     switch(p.kind()) {
                     case ATTRIBUTE:
                        this.handleAttributeProp((AttributePropertyInfo)p, se);
                        break;
                     case VALUE:
                        TODO.checkSpec("what if vp.isCollection() == true?");
                        ValuePropertyInfo vpx = (ValuePropertyInfo)p;
                        se.base(vpx.getTarget().getTypeName());
                        break;
                     case ELEMENT:
                     case REFERENCE:
                     default:
                        assert false;

                        throw new IllegalStateException();
                     }
                  }

                  se.commit();
                  TODO.schemaGenerator("figure out what to do if bc != null");
                  TODO.checkSpec("handle sec 8.9.5.2, bullet #4");
               }
            } else {
               ct = ((ComplexTypeHost)parent).complexType();
               this.writeName(c, ct);
               if (c.isFinal()) {
                  ct._final("extension restriction");
               }

               if (c.isAbstract()) {
                  ct._abstract(true);
               }

               AttrDecls contentModel = ct;
               TypeDefParticle contentModelOwner = ct;
               ClassInfo<T, C> bc = c.getBaseClass();
               if (bc != null) {
                  if (bc.hasValueProperty()) {
                     SimpleExtension sex = ct.simpleContent().extension();
                     contentModel = sex;
                     contentModelOwner = null;
                     sex.base(bc.getTypeName());
                  } else {
                     ComplexExtension ce = ct.complexContent().extension();
                     contentModel = ce;
                     contentModelOwner = ce;
                     ce.base(bc.getTypeName());
                  }
               }

               if (contentModelOwner != null) {
                  ArrayList<Tree> children = new ArrayList();
                  Iterator var8 = c.getProperties().iterator();

                  while(var8.hasNext()) {
                     PropertyInfo<T, C> px = (PropertyInfo)var8.next();
                     if (px instanceof ReferencePropertyInfo && ((ReferencePropertyInfo)px).isMixed()) {
                        ct.mixed(true);
                     }

                     Tree t = this.buildPropertyContentModel(px);
                     if (t != null) {
                        children.add(t);
                     }
                  }

                  Tree top = Tree.makeGroup(c.isOrdered() ? GroupKind.SEQUENCE : GroupKind.ALL, children);
                  top.write((TypeDefParticle)contentModelOwner);
               }

               Iterator var18 = c.getProperties().iterator();

               while(var18.hasNext()) {
                  PropertyInfo<T, C> pxx = (PropertyInfo)var18.next();
                  if (pxx instanceof AttributePropertyInfo) {
                     this.handleAttributeProp((AttributePropertyInfo)pxx, (AttrDecls)contentModel);
                  }
               }

               if (c.hasAttributeWildcard()) {
                  ((AttrDecls)contentModel).anyAttribute().namespace("##other").processContents("skip");
               }

               ct.commit();
            }
         }
      }

      private void writeName(NonElement<T, C> c, TypedXmlWriter xw) {
         QName tn = c.getTypeName();
         if (tn != null) {
            xw._attribute((String)"name", tn.getLocalPart());
         }

      }

      private boolean containsValueProp(ClassInfo<T, C> c) {
         Iterator var2 = c.getProperties().iterator();

         PropertyInfo p;
         do {
            if (!var2.hasNext()) {
               return false;
            }

            p = (PropertyInfo)var2.next();
         } while(!(p instanceof ValuePropertyInfo));

         return true;
      }

      private Tree buildPropertyContentModel(PropertyInfo<T, C> p) {
         switch(p.kind()) {
         case ATTRIBUTE:
            return null;
         case VALUE:
            assert false;

            throw new IllegalStateException();
         case ELEMENT:
            return this.handleElementProp((ElementPropertyInfo)p);
         case REFERENCE:
            return this.handleReferenceProp((ReferencePropertyInfo)p);
         case MAP:
            return this.handleMapProp((MapPropertyInfo)p);
         default:
            assert false;

            throw new IllegalStateException();
         }
      }

      private Tree handleElementProp(final ElementPropertyInfo<T, C> ep) {
         if (ep.isValueList()) {
            return new Tree.Term() {
               protected void write(ContentModelContainer parent, boolean isOptional, boolean repeated) {
                  TypeRef<T, C> t = (TypeRef)ep.getTypes().get(0);
                  LocalElement e = parent.element();
                  e.block();
                  QName tn = t.getTagName();
                  e.name(tn.getLocalPart());
                  com.sun.xml.internal.bind.v2.schemagen.xmlschema.List lst = e.simpleType().list();
                  Namespace.this.writeTypeRef(lst, (NonElementRef)t, "itemType");
                  Namespace.this.elementFormDefault.writeForm(e, tn);
                  this.writeOccurs(e, isOptional || !ep.isRequired(), repeated);
               }
            };
         } else {
            ArrayList<Tree> children = new ArrayList();
            Iterator var3 = ep.getTypes().iterator();

            while(var3.hasNext()) {
               final TypeRef<T, C> t = (TypeRef)var3.next();
               children.add(new Tree.Term() {
                  protected void write(ContentModelContainer parent, boolean isOptional, boolean repeated) {
                     LocalElement e = parent.element();
                     QName tn = t.getTagName();
                     PropertyInfo propInfo = t.getSource();
                     TypeInfo parentInfo = propInfo == null ? null : propInfo.parent();
                     if (Namespace.this.canBeDirectElementRef(t, tn, parentInfo)) {
                        if (!t.getTarget().isSimpleType() && t.getTarget() instanceof ClassInfo && XmlSchemaGenerator.this.collisionChecker.findDuplicate((ClassInfo)t.getTarget())) {
                           e.ref(new QName(Namespace.this.uri, tn.getLocalPart()));
                        } else {
                           QName elemName = null;
                           if (t.getTarget() instanceof Element) {
                              Element te = (Element)t.getTarget();
                              elemName = te.getElementName();
                           }

                           Collection<TypeInfo> refs = propInfo.ref();
                           TypeInfo ti;
                           if (refs != null && !refs.isEmpty() && elemName != null && ((ti = (TypeInfo)refs.iterator().next()) == null || ti instanceof ClassInfoImpl)) {
                              ClassInfoImpl cImpl = (ClassInfoImpl)ti;
                              if (cImpl != null && cImpl.getElementName() != null) {
                                 e.ref(new QName(cImpl.getElementName().getNamespaceURI(), tn.getLocalPart()));
                              } else {
                                 e.ref(new QName("", tn.getLocalPart()));
                              }
                           } else {
                              e.ref(tn);
                           }
                        }
                     } else {
                        e.name(tn.getLocalPart());
                        Namespace.this.writeTypeRef(e, (NonElementRef)t, "type");
                        Namespace.this.elementFormDefault.writeForm(e, tn);
                     }

                     if (t.isNillable()) {
                        e.nillable(true);
                     }

                     if (t.getDefaultValue() != null) {
                        e._default(t.getDefaultValue());
                     }

                     this.writeOccurs(e, isOptional, repeated);
                  }
               });
            }

            final Tree choice = Tree.makeGroup(GroupKind.CHOICE, children).makeOptional(!ep.isRequired()).makeRepeated(ep.isCollection());
            final QName ename = ep.getXmlName();
            return (Tree)(ename != null ? new Tree.Term() {
               protected void write(ContentModelContainer parent, boolean isOptional, boolean repeated) {
                  LocalElement e = parent.element();
                  if (ename.getNamespaceURI().length() > 0 && !ename.getNamespaceURI().equals(Namespace.this.uri)) {
                     e.ref(new QName(ename.getNamespaceURI(), ename.getLocalPart()));
                  } else {
                     e.name(ename.getLocalPart());
                     Namespace.this.elementFormDefault.writeForm(e, ename);
                     if (ep.isCollectionNillable()) {
                        e.nillable(true);
                     }

                     this.writeOccurs(e, !ep.isCollectionRequired(), repeated);
                     ComplexType p = e.complexType();
                     choice.write(p);
                  }
               }
            } : choice);
         }
      }

      private boolean canBeDirectElementRef(TypeRef<T, C> t, QName tn, TypeInfo parentInfo) {
         Element te = null;
         ClassInfo ci = null;
         QName targetTagName = null;
         if (!t.isNillable() && t.getDefaultValue() == null) {
            if (t.getTarget() instanceof Element) {
               te = (Element)t.getTarget();
               targetTagName = te.getElementName();
               if (te instanceof ClassInfo) {
                  ci = (ClassInfo)te;
               }
            }

            String nsUri = tn.getNamespaceURI();
            if (!nsUri.equals(this.uri) && nsUri.length() > 0 && (!(parentInfo instanceof ClassInfo) || ((ClassInfo)parentInfo).getTypeName() != null)) {
               return true;
            } else if (ci != null && targetTagName != null && te.getScope() == null && targetTagName.getNamespaceURI() == null && targetTagName.equals(tn)) {
               return true;
            } else if (te == null) {
               return false;
            } else {
               return targetTagName != null && targetTagName.equals(tn);
            }
         } else {
            return false;
         }
      }

      private void handleAttributeProp(AttributePropertyInfo<T, C> ap, AttrDecls attr) {
         LocalAttribute localAttribute = attr.attribute();
         String attrURI = ap.getXmlName().getNamespaceURI();
         if (attrURI.equals("")) {
            localAttribute.name(ap.getXmlName().getLocalPart());
            this.writeAttributeTypeRef(ap, localAttribute);
            this.attributeFormDefault.writeForm(localAttribute, ap.getXmlName());
         } else {
            localAttribute.ref(ap.getXmlName());
         }

         if (ap.isRequired()) {
            localAttribute.use("required");
         }

      }

      private void writeAttributeTypeRef(AttributePropertyInfo<T, C> ap, AttributeType a) {
         if (ap.isCollection()) {
            this.writeTypeRef(a.simpleType().list(), (NonElementRef)ap, "itemType");
         } else {
            this.writeTypeRef(a, (NonElementRef)ap, "type");
         }

      }

      private Tree handleReferenceProp(final ReferencePropertyInfo<T, C> rp) {
         ArrayList<Tree> children = new ArrayList();
         Iterator var3 = rp.getElements().iterator();

         while(var3.hasNext()) {
            final Element<T, C> e = (Element)var3.next();
            children.add(new Tree.Term() {
               protected void write(ContentModelContainer parent, boolean isOptional, boolean repeated) {
                  LocalElement eref = parent.element();
                  boolean local = false;
                  QName en = e.getElementName();
                  if (e.getScope() != null) {
                     boolean qualified = en.getNamespaceURI().equals(Namespace.this.uri);
                     boolean unqualified = en.getNamespaceURI().equals("");
                     if (qualified || unqualified) {
                        if (unqualified) {
                           if (Namespace.this.elementFormDefault.isEffectivelyQualified) {
                              eref.form("unqualified");
                           }
                        } else if (!Namespace.this.elementFormDefault.isEffectivelyQualified) {
                           eref.form("qualified");
                        }

                        local = true;
                        eref.name(en.getLocalPart());
                        if (e instanceof ClassInfo) {
                           Namespace.this.writeTypeRef(eref, (NonElement)((ClassInfo)e), "type");
                        } else {
                           Namespace.this.writeTypeRef(eref, (NonElement)((ElementInfo)e).getContentType(), "type");
                        }
                     }
                  }

                  if (!local) {
                     eref.ref(en);
                  }

                  this.writeOccurs(eref, isOptional, repeated);
               }
            });
         }

         final WildcardMode wc = rp.getWildcard();
         if (wc != null) {
            children.add(new Tree.Term() {
               protected void write(ContentModelContainer parent, boolean isOptional, boolean repeated) {
                  Any any = parent.any();
                  String pcmode = XmlSchemaGenerator.getProcessContentsModeName(wc);
                  if (pcmode != null) {
                     any.processContents(pcmode);
                  }

                  any.namespace("##other");
                  this.writeOccurs(any, isOptional, repeated);
               }
            });
         }

         final Tree choice = Tree.makeGroup(GroupKind.CHOICE, children).makeRepeated(rp.isCollection()).makeOptional(!rp.isRequired());
         final QName ename = rp.getXmlName();
         return (Tree)(ename != null ? new Tree.Term() {
            protected void write(ContentModelContainer parent, boolean isOptional, boolean repeated) {
               LocalElement e = parent.element().name(ename.getLocalPart());
               Namespace.this.elementFormDefault.writeForm(e, ename);
               if (rp.isCollectionNillable()) {
                  e.nillable(true);
               }

               this.writeOccurs(e, true, repeated);
               ComplexType p = e.complexType();
               choice.write(p);
            }
         } : choice);
      }

      private Tree handleMapProp(final MapPropertyInfo<T, C> mp) {
         return new Tree.Term() {
            protected void write(ContentModelContainer parent, boolean isOptional, boolean repeated) {
               QName ename = mp.getXmlName();
               LocalElement e = parent.element();
               Namespace.this.elementFormDefault.writeForm(e, ename);
               if (mp.isCollectionNillable()) {
                  e.nillable(true);
               }

               e = e.name(ename.getLocalPart());
               this.writeOccurs(e, isOptional, repeated);
               ComplexType p = e.complexType();
               e = p.sequence().element();
               e.name("entry").minOccurs(0).maxOccurs("unbounded");
               ExplicitGroup seq = e.complexType().sequence();
               Namespace.this.writeKeyOrValue(seq, "key", mp.getKeyType());
               Namespace.this.writeKeyOrValue(seq, "value", mp.getValueType());
            }
         };
      }

      private void writeKeyOrValue(ExplicitGroup seq, String tagName, NonElement<T, C> typeRef) {
         LocalElement key = seq.element().name(tagName);
         key.minOccurs(0);
         this.writeTypeRef(key, (NonElement)typeRef, "type");
      }

      public void addGlobalAttribute(AttributePropertyInfo<T, C> ap) {
         this.attributeDecls.put((Comparable)ap.getXmlName().getLocalPart(), ap);
         this.addDependencyTo(ap.getTarget().getTypeName());
      }

      public void addGlobalElement(TypeRef<T, C> tref) {
         this.elementDecls.put((Comparable)tref.getTagName().getLocalPart(), new XmlSchemaGenerator.Namespace.ElementWithType(false, tref.getTarget()));
         this.addDependencyTo(tref.getTarget().getTypeName());
      }

      public String toString() {
         StringBuilder buf = new StringBuilder();
         buf.append("[classes=").append((Object)this.classes);
         buf.append(",elementDecls=").append((Object)this.elementDecls);
         buf.append(",enums=").append((Object)this.enums);
         buf.append("]");
         return super.toString();
      }

      class ElementWithType extends XmlSchemaGenerator<T, C, F, M>.Namespace.ElementDeclaration {
         private final boolean nillable;
         private final NonElement<T, C> type;

         public ElementWithType(boolean nillable, NonElement<T, C> type) {
            super();
            this.type = type;
            this.nillable = nillable;
         }

         public void writeTo(String localName, Schema schema) {
            TopLevelElement e = schema.element().name(localName);
            if (this.nillable) {
               e.nillable(true);
            }

            if (this.type != null) {
               Namespace.this.writeTypeRef(e, (NonElement)this.type, "type");
            } else {
               e.complexType();
            }

            e.commit();
         }

         public boolean equals(Object o) {
            if (this == o) {
               return true;
            } else if (o != null && this.getClass() == o.getClass()) {
               XmlSchemaGenerator<T, C, F, M>.Namespace.ElementWithType that = (XmlSchemaGenerator.Namespace.ElementWithType)o;
               return this.type.equals(that.type);
            } else {
               return false;
            }
         }

         public int hashCode() {
            return this.type.hashCode();
         }
      }

      abstract class ElementDeclaration {
         public abstract boolean equals(Object var1);

         public abstract int hashCode();

         public abstract void writeTo(String var1, Schema var2);
      }
   }
}
