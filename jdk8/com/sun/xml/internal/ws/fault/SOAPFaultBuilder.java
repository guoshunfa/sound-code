package com.sun.xml.internal.ws.fault;

import com.sun.istack.internal.NotNull;
import com.sun.istack.internal.Nullable;
import com.sun.xml.internal.ws.api.SOAPVersion;
import com.sun.xml.internal.ws.api.message.Message;
import com.sun.xml.internal.ws.api.model.ExceptionType;
import com.sun.xml.internal.ws.encoding.soap.SerializationException;
import com.sun.xml.internal.ws.message.FaultMessage;
import com.sun.xml.internal.ws.message.jaxb.JAXBMessage;
import com.sun.xml.internal.ws.model.CheckedExceptionImpl;
import com.sun.xml.internal.ws.spi.db.XMLBridge;
import com.sun.xml.internal.ws.util.DOMUtil;
import com.sun.xml.internal.ws.util.StringUtils;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.ReflectPermission;
import java.security.AccessControlContext;
import java.security.AccessController;
import java.security.CodeSource;
import java.security.Permissions;
import java.security.PrivilegedAction;
import java.security.ProtectionDomain;
import java.util.Iterator;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.attachment.AttachmentUnmarshaller;
import javax.xml.namespace.QName;
import javax.xml.soap.Detail;
import javax.xml.soap.DetailEntry;
import javax.xml.soap.SOAPFault;
import javax.xml.transform.Result;
import javax.xml.transform.dom.DOMResult;
import javax.xml.ws.ProtocolException;
import javax.xml.ws.WebServiceException;
import javax.xml.ws.soap.SOAPFaultException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

public abstract class SOAPFaultBuilder {
   private static final JAXBContext JAXB_CONTEXT;
   private static final Logger logger = Logger.getLogger(SOAPFaultBuilder.class.getName());
   public static final boolean captureStackTrace;
   static final String CAPTURE_STACK_TRACE_PROPERTY = SOAPFaultBuilder.class.getName() + ".captureStackTrace";

   abstract DetailType getDetail();

   abstract void setDetail(DetailType var1);

   @XmlTransient
   @Nullable
   public QName getFirstDetailEntryName() {
      DetailType dt = this.getDetail();
      if (dt != null) {
         Node entry = dt.getDetail(0);
         if (entry != null) {
            return new QName(entry.getNamespaceURI(), entry.getLocalName());
         }
      }

      return null;
   }

   abstract String getFaultString();

   public Throwable createException(Map<QName, CheckedExceptionImpl> exceptions) throws JAXBException {
      DetailType dt = this.getDetail();
      Node detail = null;
      if (dt != null) {
         detail = dt.getDetail(0);
      }

      if (detail != null && exceptions != null) {
         QName detailName = new QName(detail.getNamespaceURI(), detail.getLocalName());
         CheckedExceptionImpl ce = (CheckedExceptionImpl)exceptions.get(detailName);
         if (ce == null) {
            return this.attachServerException(this.getProtocolException());
         } else if (ce.getExceptionType().equals(ExceptionType.UserDefined)) {
            return this.attachServerException(this.createUserDefinedException(ce));
         } else {
            Class exceptionClass = ce.getExceptionClass();

            try {
               Constructor constructor = exceptionClass.getConstructor(String.class, (Class)ce.getDetailType().type);
               Exception exception = (Exception)constructor.newInstance(this.getFaultString(), this.getJAXBObject(detail, ce));
               return this.attachServerException(exception);
            } catch (Exception var9) {
               throw new WebServiceException(var9);
            }
         }
      } else {
         return this.attachServerException(this.getProtocolException());
      }
   }

   @NotNull
   public static Message createSOAPFaultMessage(@NotNull SOAPVersion soapVersion, @NotNull ProtocolException ex, @Nullable QName faultcode) {
      Object detail = getFaultDetail((CheckedExceptionImpl)null, ex);
      return soapVersion == SOAPVersion.SOAP_12 ? createSOAP12Fault(soapVersion, ex, detail, (CheckedExceptionImpl)null, faultcode) : createSOAP11Fault(soapVersion, ex, detail, (CheckedExceptionImpl)null, faultcode);
   }

   public static Message createSOAPFaultMessage(SOAPVersion soapVersion, CheckedExceptionImpl ceModel, Throwable ex) {
      Throwable t = ex instanceof InvocationTargetException ? ((InvocationTargetException)ex).getTargetException() : ex;
      return createSOAPFaultMessage(soapVersion, (CheckedExceptionImpl)ceModel, (Throwable)t, (QName)null);
   }

   public static Message createSOAPFaultMessage(SOAPVersion soapVersion, CheckedExceptionImpl ceModel, Throwable ex, QName faultCode) {
      Object detail = getFaultDetail(ceModel, ex);
      return soapVersion == SOAPVersion.SOAP_12 ? createSOAP12Fault(soapVersion, ex, detail, ceModel, faultCode) : createSOAP11Fault(soapVersion, ex, detail, ceModel, faultCode);
   }

   public static Message createSOAPFaultMessage(SOAPVersion soapVersion, String faultString, QName faultCode) {
      if (faultCode == null) {
         faultCode = getDefaultFaultCode(soapVersion);
      }

      return createSOAPFaultMessage(soapVersion, (String)faultString, (QName)faultCode, (Element)null);
   }

   public static Message createSOAPFaultMessage(SOAPVersion soapVersion, SOAPFault fault) {
      switch(soapVersion) {
      case SOAP_11:
         return JAXBMessage.create((JAXBContext)JAXB_CONTEXT, new SOAP11Fault(fault), soapVersion);
      case SOAP_12:
         return JAXBMessage.create((JAXBContext)JAXB_CONTEXT, new SOAP12Fault(fault), soapVersion);
      default:
         throw new AssertionError();
      }
   }

   private static Message createSOAPFaultMessage(SOAPVersion soapVersion, String faultString, QName faultCode, Element detail) {
      switch(soapVersion) {
      case SOAP_11:
         return JAXBMessage.create((JAXBContext)JAXB_CONTEXT, new SOAP11Fault(faultCode, faultString, (String)null, detail), soapVersion);
      case SOAP_12:
         return JAXBMessage.create((JAXBContext)JAXB_CONTEXT, new SOAP12Fault(faultCode, faultString, detail), soapVersion);
      default:
         throw new AssertionError();
      }
   }

   final void captureStackTrace(@Nullable Throwable t) {
      if (t != null) {
         if (captureStackTrace) {
            try {
               Document d = DOMUtil.createDom();
               ExceptionBean.marshal(t, d);
               DetailType detail = this.getDetail();
               if (detail == null) {
                  this.setDetail(detail = new DetailType());
               }

               detail.getDetails().add(d.getDocumentElement());
            } catch (JAXBException var4) {
               logger.log(Level.WARNING, (String)"Unable to capture the stack trace into XML", (Throwable)var4);
            }

         }
      }
   }

   private <T extends Throwable> T attachServerException(T t) {
      DetailType detail = this.getDetail();
      if (detail == null) {
         return t;
      } else {
         Iterator var3 = detail.getDetails().iterator();

         Element n;
         do {
            if (!var3.hasNext()) {
               return t;
            }

            n = (Element)var3.next();
         } while(!ExceptionBean.isStackTraceXml(n));

         try {
            t.initCause(ExceptionBean.unmarshal(n));
         } catch (JAXBException var6) {
            logger.log(Level.WARNING, (String)"Unable to read the capture stack trace in the fault", (Throwable)var6);
         }

         return t;
      }
   }

   protected abstract Throwable getProtocolException();

   private Object getJAXBObject(Node jaxbBean, CheckedExceptionImpl ce) throws JAXBException {
      XMLBridge bridge = ce.getBond();
      return bridge.unmarshal((Node)jaxbBean, (AttachmentUnmarshaller)null);
   }

   private Exception createUserDefinedException(CheckedExceptionImpl ce) {
      Class exceptionClass = ce.getExceptionClass();
      Class detailBean = ce.getDetailBean();

      try {
         Node detailNode = (Node)this.getDetail().getDetails().get(0);
         Object jaxbDetail = this.getJAXBObject(detailNode, ce);

         Constructor exConstructor;
         try {
            exConstructor = exceptionClass.getConstructor(String.class, detailBean);
            return (Exception)exConstructor.newInstance(this.getFaultString(), jaxbDetail);
         } catch (NoSuchMethodException var8) {
            exConstructor = exceptionClass.getConstructor(String.class);
            return (Exception)exConstructor.newInstance(this.getFaultString());
         }
      } catch (Exception var9) {
         throw new WebServiceException(var9);
      }
   }

   private static String getWriteMethod(Field f) {
      return "set" + StringUtils.capitalize(f.getName());
   }

   private static Object getFaultDetail(CheckedExceptionImpl ce, Throwable exception) {
      if (ce == null) {
         return null;
      } else if (ce.getExceptionType().equals(ExceptionType.UserDefined)) {
         return createDetailFromUserDefinedException(ce, exception);
      } else {
         try {
            Method m = exception.getClass().getMethod("getFaultInfo");
            return m.invoke(exception);
         } catch (Exception var3) {
            throw new SerializationException(var3);
         }
      }
   }

   private static Object createDetailFromUserDefinedException(CheckedExceptionImpl ce, Object exception) {
      Class detailBean = ce.getDetailBean();
      Field[] fields = detailBean.getDeclaredFields();

      try {
         Object detail = detailBean.newInstance();
         Field[] var5 = fields;
         int var6 = fields.length;

         for(int var7 = 0; var7 < var6; ++var7) {
            Field f = var5[var7];
            Method em = exception.getClass().getMethod(getReadMethod(f));

            try {
               Method sm = detailBean.getMethod(getWriteMethod(f), em.getReturnType());
               sm.invoke(detail, em.invoke(exception));
            } catch (NoSuchMethodException var12) {
               Field sf = detailBean.getField(f.getName());
               sf.set(detail, em.invoke(exception));
            }
         }

         return detail;
      } catch (Exception var13) {
         throw new SerializationException(var13);
      }
   }

   private static String getReadMethod(Field f) {
      return f.getType().isAssignableFrom(Boolean.TYPE) ? "is" + StringUtils.capitalize(f.getName()) : "get" + StringUtils.capitalize(f.getName());
   }

   private static Message createSOAP11Fault(SOAPVersion soapVersion, Throwable e, Object detail, CheckedExceptionImpl ce, QName faultCode) {
      SOAPFaultException soapFaultException = null;
      String faultString = null;
      String faultActor = null;
      Throwable cause = e.getCause();
      if (e instanceof SOAPFaultException) {
         soapFaultException = (SOAPFaultException)e;
      } else if (cause != null && cause instanceof SOAPFaultException) {
         soapFaultException = (SOAPFaultException)e.getCause();
      }

      if (soapFaultException != null) {
         QName soapFaultCode = soapFaultException.getFault().getFaultCodeAsQName();
         if (soapFaultCode != null) {
            faultCode = soapFaultCode;
         }

         faultString = soapFaultException.getFault().getFaultString();
         faultActor = soapFaultException.getFault().getFaultActor();
      }

      if (faultCode == null) {
         faultCode = getDefaultFaultCode(soapVersion);
      }

      if (faultString == null) {
         faultString = e.getMessage();
         if (faultString == null) {
            faultString = e.toString();
         }
      }

      Element detailNode = null;
      QName firstEntry = null;
      if (detail == null && soapFaultException != null) {
         detailNode = soapFaultException.getFault().getDetail();
         firstEntry = getFirstDetailEntryName((Detail)detailNode);
      } else if (ce != null) {
         try {
            DOMResult dr = new DOMResult();
            ce.getBond().marshal(detail, (Result)dr);
            detailNode = (Element)dr.getNode().getFirstChild();
            firstEntry = getFirstDetailEntryName((Element)detailNode);
         } catch (JAXBException var13) {
            faultString = e.getMessage();
            faultCode = getDefaultFaultCode(soapVersion);
         }
      }

      SOAP11Fault soap11Fault = new SOAP11Fault(faultCode, faultString, faultActor, (Element)detailNode);
      if (ce == null) {
         soap11Fault.captureStackTrace(e);
      }

      Message msg = JAXBMessage.create((JAXBContext)JAXB_CONTEXT, soap11Fault, soapVersion);
      return new FaultMessage(msg, firstEntry);
   }

   @Nullable
   private static QName getFirstDetailEntryName(@Nullable Detail detail) {
      if (detail != null) {
         Iterator<DetailEntry> it = detail.getDetailEntries();
         if (it.hasNext()) {
            DetailEntry entry = (DetailEntry)it.next();
            return getFirstDetailEntryName((Element)entry);
         }
      }

      return null;
   }

   @NotNull
   private static QName getFirstDetailEntryName(@NotNull Element entry) {
      return new QName(entry.getNamespaceURI(), entry.getLocalName());
   }

   private static Message createSOAP12Fault(SOAPVersion soapVersion, Throwable e, Object detail, CheckedExceptionImpl ce, QName faultCode) {
      SOAPFaultException soapFaultException = null;
      CodeType code = null;
      String faultString = null;
      String faultRole = null;
      String faultNode = null;
      Throwable cause = e.getCause();
      if (e instanceof SOAPFaultException) {
         soapFaultException = (SOAPFaultException)e;
      } else if (cause != null && cause instanceof SOAPFaultException) {
         soapFaultException = (SOAPFaultException)e.getCause();
      }

      if (soapFaultException != null) {
         SOAPFault fault = soapFaultException.getFault();
         QName soapFaultCode = fault.getFaultCodeAsQName();
         if (soapFaultCode != null) {
            faultCode = soapFaultCode;
            code = new CodeType(soapFaultCode);
            Iterator iter = fault.getFaultSubcodes();
            boolean first = true;
            SubcodeType subcode = null;

            while(iter.hasNext()) {
               QName value = (QName)iter.next();
               if (first) {
                  SubcodeType sct = new SubcodeType(value);
                  code.setSubcode(sct);
                  subcode = sct;
                  first = false;
               } else {
                  subcode = fillSubcodes(subcode, value);
               }
            }
         }

         faultString = soapFaultException.getFault().getFaultString();
         faultRole = soapFaultException.getFault().getFaultActor();
         faultNode = soapFaultException.getFault().getFaultNode();
      }

      if (faultCode == null) {
         faultCode = getDefaultFaultCode(soapVersion);
         code = new CodeType(faultCode);
      } else if (code == null) {
         code = new CodeType(faultCode);
      }

      if (faultString == null) {
         faultString = e.getMessage();
         if (faultString == null) {
            faultString = e.toString();
         }
      }

      ReasonType reason = new ReasonType(faultString);
      Element detailNode = null;
      QName firstEntry = null;
      if (detail == null && soapFaultException != null) {
         detailNode = soapFaultException.getFault().getDetail();
         firstEntry = getFirstDetailEntryName((Detail)detailNode);
      } else if (detail != null) {
         try {
            DOMResult dr = new DOMResult();
            ce.getBond().marshal(detail, (Result)dr);
            detailNode = (Element)dr.getNode().getFirstChild();
            firstEntry = getFirstDetailEntryName((Element)detailNode);
         } catch (JAXBException var18) {
            faultString = e.getMessage();
         }
      }

      SOAP12Fault soap12Fault = new SOAP12Fault(code, reason, faultNode, faultRole, (Element)detailNode);
      if (ce == null) {
         soap12Fault.captureStackTrace(e);
      }

      Message msg = JAXBMessage.create((JAXBContext)JAXB_CONTEXT, soap12Fault, soapVersion);
      return new FaultMessage(msg, firstEntry);
   }

   private static SubcodeType fillSubcodes(SubcodeType parent, QName value) {
      SubcodeType newCode = new SubcodeType(value);
      parent.setSubcode(newCode);
      return newCode;
   }

   private static QName getDefaultFaultCode(SOAPVersion soapVersion) {
      return soapVersion.faultCodeServer;
   }

   public static SOAPFaultBuilder create(Message msg) throws JAXBException {
      return (SOAPFaultBuilder)msg.readPayloadAsJAXB(JAXB_CONTEXT.createUnmarshaller());
   }

   private static JAXBContext createJAXBContext() {
      if (isJDKRuntime()) {
         Permissions permissions = new Permissions();
         permissions.add(new RuntimePermission("accessClassInPackage.com.sun.xml.internal.ws.fault"));
         permissions.add(new ReflectPermission("suppressAccessChecks"));
         return (JAXBContext)AccessController.doPrivileged(new PrivilegedAction<JAXBContext>() {
            public JAXBContext run() {
               try {
                  return JAXBContext.newInstance(SOAP11Fault.class, SOAP12Fault.class);
               } catch (JAXBException var2) {
                  throw new Error(var2);
               }
            }
         }, new AccessControlContext(new ProtectionDomain[]{new ProtectionDomain((CodeSource)null, permissions)}));
      } else {
         try {
            return JAXBContext.newInstance(SOAP11Fault.class, SOAP12Fault.class);
         } catch (JAXBException var1) {
            throw new Error(var1);
         }
      }
   }

   private static boolean isJDKRuntime() {
      return SOAPFaultBuilder.class.getName().contains("internal");
   }

   static {
      boolean tmpVal = false;

      try {
         tmpVal = Boolean.getBoolean(CAPTURE_STACK_TRACE_PROPERTY);
      } catch (SecurityException var2) {
      }

      captureStackTrace = tmpVal;
      JAXB_CONTEXT = createJAXBContext();
   }
}
