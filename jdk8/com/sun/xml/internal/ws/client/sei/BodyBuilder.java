package com.sun.xml.internal.ws.client.sei;

import com.sun.xml.internal.ws.api.SOAPVersion;
import com.sun.xml.internal.ws.api.message.Message;
import com.sun.xml.internal.ws.api.message.Messages;
import com.sun.xml.internal.ws.message.jaxb.JAXBMessage;
import com.sun.xml.internal.ws.model.ParameterImpl;
import com.sun.xml.internal.ws.model.WrapperParameter;
import com.sun.xml.internal.ws.spi.db.BindingContext;
import com.sun.xml.internal.ws.spi.db.DatabindingException;
import com.sun.xml.internal.ws.spi.db.PropertyAccessor;
import com.sun.xml.internal.ws.spi.db.WrapperComposite;
import com.sun.xml.internal.ws.spi.db.XMLBridge;
import java.util.List;
import javax.xml.bind.JAXBException;
import javax.xml.namespace.QName;
import javax.xml.ws.WebServiceException;

abstract class BodyBuilder {
   static final BodyBuilder EMPTY_SOAP11;
   static final BodyBuilder EMPTY_SOAP12;

   abstract Message createMessage(Object[] var1);

   static {
      EMPTY_SOAP11 = new BodyBuilder.Empty(SOAPVersion.SOAP_11);
      EMPTY_SOAP12 = new BodyBuilder.Empty(SOAPVersion.SOAP_12);
   }

   static final class RpcLit extends BodyBuilder.Wrapped {
      RpcLit(WrapperParameter wp, SOAPVersion soapVersion, ValueGetterFactory getter) {
         super(wp, soapVersion, getter);

         assert wp.getTypeInfo().type == WrapperComposite.class;

         this.parameterBridges = new XMLBridge[this.children.size()];

         for(int i = 0; i < this.parameterBridges.length; ++i) {
            this.parameterBridges[i] = ((ParameterImpl)this.children.get(i)).getXMLBridge();
         }

      }

      Object build(Object[] methodArgs) {
         return this.buildWrapperComposite(methodArgs);
      }
   }

   static final class DocLit extends BodyBuilder.Wrapped {
      private final PropertyAccessor[] accessors;
      private final Class wrapper;
      private BindingContext bindingContext;
      private boolean dynamicWrapper;

      DocLit(WrapperParameter wp, SOAPVersion soapVersion, ValueGetterFactory getter) {
         super(wp, soapVersion, getter);
         this.bindingContext = wp.getOwner().getBindingContext();
         this.wrapper = (Class)wp.getXMLBridge().getTypeInfo().type;
         this.dynamicWrapper = WrapperComposite.class.equals(this.wrapper);
         this.parameterBridges = new XMLBridge[this.children.size()];
         this.accessors = new PropertyAccessor[this.children.size()];

         for(int i = 0; i < this.accessors.length; ++i) {
            ParameterImpl p = (ParameterImpl)this.children.get(i);
            QName name = p.getName();
            if (this.dynamicWrapper) {
               this.parameterBridges[i] = ((ParameterImpl)this.children.get(i)).getInlinedRepeatedElementBridge();
               if (this.parameterBridges[i] == null) {
                  this.parameterBridges[i] = ((ParameterImpl)this.children.get(i)).getXMLBridge();
               }
            } else {
               try {
                  this.accessors[i] = p.getOwner().getBindingContext().getElementPropertyAccessor(this.wrapper, name.getNamespaceURI(), name.getLocalPart());
               } catch (JAXBException var8) {
                  throw new WebServiceException(this.wrapper + " do not have a property of the name " + name, var8);
               }
            }
         }

      }

      Object build(Object[] methodArgs) {
         if (this.dynamicWrapper) {
            return this.buildWrapperComposite(methodArgs);
         } else {
            try {
               Object bean = this.bindingContext.newWrapperInstace(this.wrapper);

               for(int i = this.indices.length - 1; i >= 0; --i) {
                  this.accessors[i].set(bean, this.getters[i].get(methodArgs[this.indices[i]]));
               }

               return bean;
            } catch (InstantiationException var4) {
               Error x = new InstantiationError(var4.getMessage());
               x.initCause(var4);
               throw x;
            } catch (IllegalAccessException var5) {
               Error x = new IllegalAccessError(var5.getMessage());
               x.initCause(var5);
               throw x;
            } catch (DatabindingException var6) {
               throw new WebServiceException(var6);
            }
         }
      }
   }

   abstract static class Wrapped extends BodyBuilder.JAXB {
      protected final int[] indices;
      protected final ValueGetter[] getters;
      protected XMLBridge[] parameterBridges;
      protected List<ParameterImpl> children;

      protected Wrapped(WrapperParameter wp, SOAPVersion soapVersion, ValueGetterFactory getter) {
         super(wp.getXMLBridge(), soapVersion);
         this.children = wp.getWrapperChildren();
         this.indices = new int[this.children.size()];
         this.getters = new ValueGetter[this.children.size()];

         for(int i = 0; i < this.indices.length; ++i) {
            ParameterImpl p = (ParameterImpl)this.children.get(i);
            this.indices[i] = p.getIndex();
            this.getters[i] = getter.get(p);
         }

      }

      protected WrapperComposite buildWrapperComposite(Object[] methodArgs) {
         WrapperComposite cs = new WrapperComposite();
         cs.bridges = this.parameterBridges;
         cs.values = new Object[this.parameterBridges.length];

         for(int i = this.indices.length - 1; i >= 0; --i) {
            Object arg = this.getters[i].get(methodArgs[this.indices[i]]);
            if (arg == null) {
               throw new WebServiceException("Method Parameter: " + ((ParameterImpl)this.children.get(i)).getName() + " cannot be null. This is BP 1.1 R2211 violation.");
            }

            cs.values[i] = arg;
         }

         return cs;
      }
   }

   static final class Bare extends BodyBuilder.JAXB {
      private final int methodPos;
      private final ValueGetter getter;

      Bare(ParameterImpl p, SOAPVersion soapVersion, ValueGetter getter) {
         super(p.getXMLBridge(), soapVersion);
         this.methodPos = p.getIndex();
         this.getter = getter;
      }

      Object build(Object[] methodArgs) {
         return this.getter.get(methodArgs[this.methodPos]);
      }
   }

   private abstract static class JAXB extends BodyBuilder {
      private final XMLBridge bridge;
      private final SOAPVersion soapVersion;

      protected JAXB(XMLBridge bridge, SOAPVersion soapVersion) {
         assert bridge != null;

         this.bridge = bridge;
         this.soapVersion = soapVersion;
      }

      final Message createMessage(Object[] methodArgs) {
         return JAXBMessage.create(this.bridge, this.build(methodArgs), this.soapVersion);
      }

      abstract Object build(Object[] var1);
   }

   private static final class Empty extends BodyBuilder {
      private final SOAPVersion soapVersion;

      public Empty(SOAPVersion soapVersion) {
         this.soapVersion = soapVersion;
      }

      Message createMessage(Object[] methodArgs) {
         return Messages.createEmpty(this.soapVersion);
      }
   }
}
