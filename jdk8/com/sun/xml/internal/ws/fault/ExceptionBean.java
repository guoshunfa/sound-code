package com.sun.xml.internal.ws.fault;

import com.sun.xml.internal.bind.marshaller.NamespacePrefixMapper;
import com.sun.xml.internal.ws.developer.ServerSideException;
import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.PropertyException;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

@XmlRootElement(
   namespace = "http://jax-ws.dev.java.net/",
   name = "exception"
)
final class ExceptionBean {
   @XmlAttribute(
      name = "class"
   )
   public String className;
   @XmlElement
   public String message;
   @XmlElementWrapper(
      namespace = "http://jax-ws.dev.java.net/",
      name = "stackTrace"
   )
   @XmlElement(
      namespace = "http://jax-ws.dev.java.net/",
      name = "frame"
   )
   public List<ExceptionBean.StackFrame> stackTrace = new ArrayList();
   @XmlElement(
      namespace = "http://jax-ws.dev.java.net/",
      name = "cause"
   )
   public ExceptionBean cause;
   @XmlAttribute
   public String note;
   private static final JAXBContext JAXB_CONTEXT;
   static final String NS = "http://jax-ws.dev.java.net/";
   static final String LOCAL_NAME = "exception";
   private static final NamespacePrefixMapper nsp;

   public static void marshal(Throwable t, Node parent) throws JAXBException {
      Marshaller m = JAXB_CONTEXT.createMarshaller();

      try {
         m.setProperty("com.sun.xml.internal.bind.namespacePrefixMapper", nsp);
      } catch (PropertyException var4) {
      }

      m.marshal(new ExceptionBean(t), (Node)parent);
   }

   public static ServerSideException unmarshal(Node xml) throws JAXBException {
      ExceptionBean e = (ExceptionBean)JAXB_CONTEXT.createUnmarshaller().unmarshal(xml);
      return e.toException();
   }

   ExceptionBean() {
      this.note = "To disable this feature, set " + SOAPFaultBuilder.CAPTURE_STACK_TRACE_PROPERTY + " system property to false";
   }

   private ExceptionBean(Throwable t) {
      this.note = "To disable this feature, set " + SOAPFaultBuilder.CAPTURE_STACK_TRACE_PROPERTY + " system property to false";
      this.className = t.getClass().getName();
      this.message = t.getMessage();
      StackTraceElement[] var2 = t.getStackTrace();
      int var3 = var2.length;

      for(int var4 = 0; var4 < var3; ++var4) {
         StackTraceElement f = var2[var4];
         this.stackTrace.add(new ExceptionBean.StackFrame(f));
      }

      Throwable cause = t.getCause();
      if (t != cause && cause != null) {
         this.cause = new ExceptionBean(cause);
      }

   }

   private ServerSideException toException() {
      ServerSideException e = new ServerSideException(this.className, this.message);
      if (this.stackTrace != null) {
         StackTraceElement[] ste = new StackTraceElement[this.stackTrace.size()];

         for(int i = 0; i < this.stackTrace.size(); ++i) {
            ste[i] = ((ExceptionBean.StackFrame)this.stackTrace.get(i)).toStackTraceElement();
         }

         e.setStackTrace(ste);
      }

      if (this.cause != null) {
         e.initCause(this.cause.toException());
      }

      return e;
   }

   public static boolean isStackTraceXml(Element n) {
      return "exception".equals(n.getLocalName()) && "http://jax-ws.dev.java.net/".equals(n.getNamespaceURI());
   }

   static {
      try {
         JAXB_CONTEXT = JAXBContext.newInstance(ExceptionBean.class);
      } catch (JAXBException var1) {
         throw new Error(var1);
      }

      nsp = new NamespacePrefixMapper() {
         public String getPreferredPrefix(String namespaceUri, String suggestion, boolean requirePrefix) {
            return "http://jax-ws.dev.java.net/".equals(namespaceUri) ? "" : suggestion;
         }
      };
   }

   static final class StackFrame {
      @XmlAttribute(
         name = "class"
      )
      public String declaringClass;
      @XmlAttribute(
         name = "method"
      )
      public String methodName;
      @XmlAttribute(
         name = "file"
      )
      public String fileName;
      @XmlAttribute(
         name = "line"
      )
      public String lineNumber;

      StackFrame() {
      }

      public StackFrame(StackTraceElement ste) {
         this.declaringClass = ste.getClassName();
         this.methodName = ste.getMethodName();
         this.fileName = ste.getFileName();
         this.lineNumber = this.box(ste.getLineNumber());
      }

      private String box(int i) {
         if (i >= 0) {
            return String.valueOf(i);
         } else {
            return i == -2 ? "native" : "unknown";
         }
      }

      private int unbox(String v) {
         try {
            return Integer.parseInt(v);
         } catch (NumberFormatException var3) {
            return "native".equals(v) ? -2 : -1;
         }
      }

      private StackTraceElement toStackTraceElement() {
         return new StackTraceElement(this.declaringClass, this.methodName, this.fileName, this.unbox(this.lineNumber));
      }
   }
}
