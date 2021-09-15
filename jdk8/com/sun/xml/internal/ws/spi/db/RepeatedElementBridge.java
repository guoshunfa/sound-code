package com.sun.xml.internal.ws.spi.db;

import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Array;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;
import javax.xml.bind.JAXBException;
import javax.xml.bind.attachment.AttachmentMarshaller;
import javax.xml.bind.attachment.AttachmentUnmarshaller;
import javax.xml.namespace.NamespaceContext;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import org.w3c.dom.Node;
import org.xml.sax.ContentHandler;

public class RepeatedElementBridge<T> implements XMLBridge<T> {
   XMLBridge<T> delegate;
   RepeatedElementBridge.CollectionHandler collectionHandler;
   static final RepeatedElementBridge.CollectionHandler ListHandler = new RepeatedElementBridge.BaseCollectionHandler(List.class) {
      public Object convert(List list) {
         return list;
      }
   };
   static final RepeatedElementBridge.CollectionHandler HashSetHandler = new RepeatedElementBridge.BaseCollectionHandler(HashSet.class) {
      public Object convert(List list) {
         return new HashSet(list);
      }
   };

   public RepeatedElementBridge(TypeInfo typeInfo, XMLBridge xb) {
      this.delegate = xb;
      this.collectionHandler = create(typeInfo);
   }

   public RepeatedElementBridge.CollectionHandler collectionHandler() {
      return this.collectionHandler;
   }

   public BindingContext context() {
      return this.delegate.context();
   }

   public void marshal(T object, XMLStreamWriter output, AttachmentMarshaller am) throws JAXBException {
      this.delegate.marshal(object, output, am);
   }

   public void marshal(T object, OutputStream output, NamespaceContext nsContext, AttachmentMarshaller am) throws JAXBException {
      this.delegate.marshal(object, output, nsContext, am);
   }

   public void marshal(T object, Node output) throws JAXBException {
      this.delegate.marshal(object, output);
   }

   public void marshal(T object, ContentHandler contentHandler, AttachmentMarshaller am) throws JAXBException {
      this.delegate.marshal(object, contentHandler, am);
   }

   public void marshal(T object, Result result) throws JAXBException {
      this.delegate.marshal(object, result);
   }

   public T unmarshal(XMLStreamReader in, AttachmentUnmarshaller au) throws JAXBException {
      return this.delegate.unmarshal(in, au);
   }

   public T unmarshal(Source in, AttachmentUnmarshaller au) throws JAXBException {
      return this.delegate.unmarshal(in, au);
   }

   public T unmarshal(InputStream in) throws JAXBException {
      return this.delegate.unmarshal(in);
   }

   public T unmarshal(Node n, AttachmentUnmarshaller au) throws JAXBException {
      return this.delegate.unmarshal(n, au);
   }

   public TypeInfo getTypeInfo() {
      return this.delegate.getTypeInfo();
   }

   public boolean supportOutputStream() {
      return this.delegate.supportOutputStream();
   }

   public static RepeatedElementBridge.CollectionHandler create(TypeInfo ti) {
      Class javaClass = (Class)ti.type;
      if (javaClass.isArray()) {
         return new RepeatedElementBridge.ArrayHandler((Class)ti.getItemType().type);
      } else if (!List.class.equals(javaClass) && !Collection.class.equals(javaClass)) {
         return (RepeatedElementBridge.CollectionHandler)(!Set.class.equals(javaClass) && !HashSet.class.equals(javaClass) ? new RepeatedElementBridge.BaseCollectionHandler(javaClass) : HashSetHandler);
      } else {
         return ListHandler;
      }
   }

   static class ArrayHandler implements RepeatedElementBridge.CollectionHandler {
      Class componentClass;

      public ArrayHandler(Class component) {
         this.componentClass = component;
      }

      public int getSize(Object c) {
         return Array.getLength(c);
      }

      public Object convert(List list) {
         Object array = Array.newInstance(this.componentClass, list.size());

         for(int i = 0; i < list.size(); ++i) {
            Array.set(array, i, list.get(i));
         }

         return array;
      }

      public Iterator iterator(final Object c) {
         return new Iterator() {
            int index = 0;

            public boolean hasNext() {
               if (c != null && Array.getLength(c) != 0) {
                  return this.index != Array.getLength(c);
               } else {
                  return false;
               }
            }

            public Object next() throws NoSuchElementException {
               Object retVal = null;

               try {
                  retVal = Array.get(c, this.index++);
                  return retVal;
               } catch (ArrayIndexOutOfBoundsException var3) {
                  throw new NoSuchElementException();
               }
            }

            public void remove() {
            }
         };
      }
   }

   static class BaseCollectionHandler implements RepeatedElementBridge.CollectionHandler {
      Class type;

      BaseCollectionHandler(Class c) {
         this.type = c;
      }

      public int getSize(Object c) {
         return ((Collection)c).size();
      }

      public Object convert(List list) {
         try {
            Object o = this.type.newInstance();
            ((Collection)o).addAll(list);
            return o;
         } catch (Exception var3) {
            var3.printStackTrace();
            return list;
         }
      }

      public Iterator iterator(Object c) {
         return ((Collection)c).iterator();
      }
   }

   public interface CollectionHandler {
      int getSize(Object var1);

      Iterator iterator(Object var1);

      Object convert(List var1);
   }
}
