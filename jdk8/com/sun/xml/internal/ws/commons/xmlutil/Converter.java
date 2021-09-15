package com.sun.xml.internal.ws.commons.xmlutil;

import com.sun.istack.internal.NotNull;
import com.sun.istack.internal.logging.Logger;
import com.sun.xml.internal.ws.api.message.Message;
import com.sun.xml.internal.ws.api.message.Messages;
import com.sun.xml.internal.ws.api.message.Packet;
import com.sun.xml.internal.ws.util.xml.XmlUtil;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.reflect.Constructor;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Level;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;

public final class Converter {
   public static final String UTF_8 = "UTF-8";
   private static final Logger LOGGER = Logger.getLogger(Converter.class);
   private static final ContextClassloaderLocal<XMLOutputFactory> xmlOutputFactory = new ContextClassloaderLocal<XMLOutputFactory>() {
      protected XMLOutputFactory initialValue() throws Exception {
         return XMLOutputFactory.newInstance();
      }
   };
   private static final AtomicBoolean logMissingStaxUtilsWarning = new AtomicBoolean(false);

   private Converter() {
   }

   public static String toString(Throwable throwable) {
      if (throwable == null) {
         return "[ No exception ]";
      } else {
         StringWriter stringOut = new StringWriter();
         throwable.printStackTrace(new PrintWriter(stringOut));
         return stringOut.toString();
      }
   }

   public static String toString(Packet packet) {
      if (packet == null) {
         return "[ Null packet ]";
      } else {
         return packet.getMessage() == null ? "[ Empty packet ]" : toString(packet.getMessage());
      }
   }

   public static String toStringNoIndent(Packet packet) {
      if (packet == null) {
         return "[ Null packet ]";
      } else {
         return packet.getMessage() == null ? "[ Empty packet ]" : toStringNoIndent(packet.getMessage());
      }
   }

   public static String toString(Message message) {
      return toString(message, true);
   }

   public static String toStringNoIndent(Message message) {
      return toString(message, false);
   }

   private static String toString(Message message, boolean createIndenter) {
      if (message == null) {
         return "[ Null message ]";
      } else {
         StringWriter stringOut = null;

         String var4;
         try {
            stringOut = new StringWriter();
            XMLStreamWriter writer = null;

            try {
               writer = ((XMLOutputFactory)xmlOutputFactory.get()).createXMLStreamWriter((Writer)stringOut);
               if (createIndenter) {
                  writer = createIndenter(writer);
               }

               message.copy().writeTo(writer);
            } catch (Exception var26) {
               LOGGER.log(Level.WARNING, "Unexpected exception occured while dumping message", (Throwable)var26);
            } finally {
               if (writer != null) {
                  try {
                     writer.close();
                  } catch (XMLStreamException var25) {
                     LOGGER.fine("Unexpected exception occured while closing XMLStreamWriter", var25);
                  }
               }

            }

            var4 = stringOut.toString();
         } finally {
            if (stringOut != null) {
               try {
                  stringOut.close();
               } catch (IOException var24) {
                  LOGGER.finest("An exception occured when trying to close StringWriter", (Throwable)var24);
               }
            }

         }

         return var4;
      }
   }

   public static byte[] toBytes(Message message, String encoding) throws XMLStreamException {
      ByteArrayOutputStream baos = new ByteArrayOutputStream();

      byte[] var24;
      try {
         if (message != null) {
            XMLStreamWriter xsw = ((XMLOutputFactory)xmlOutputFactory.get()).createXMLStreamWriter(baos, encoding);

            try {
               message.writeTo(xsw);
            } finally {
               try {
                  xsw.close();
               } catch (XMLStreamException var21) {
                  LOGGER.warning("Unexpected exception occured while closing XMLStreamWriter", (Throwable)var21);
               }

            }
         }

         var24 = baos.toByteArray();
      } finally {
         try {
            baos.close();
         } catch (IOException var20) {
            LOGGER.warning("Unexpected exception occured while closing ByteArrayOutputStream", (Throwable)var20);
         }

      }

      return var24;
   }

   public static Message toMessage(@NotNull InputStream dataStream, String encoding) throws XMLStreamException {
      XMLStreamReader xsr = XmlUtil.newXMLInputFactory(true).createXMLStreamReader(dataStream, encoding);
      return Messages.create(xsr);
   }

   public static String messageDataToString(byte[] data, String encoding) {
      try {
         return toString(toMessage(new ByteArrayInputStream(data), encoding));
      } catch (XMLStreamException var3) {
         LOGGER.warning("Unexpected exception occured while converting message data to string", (Throwable)var3);
         return "[ Message Data Conversion Failed ]";
      }
   }

   private static XMLStreamWriter createIndenter(XMLStreamWriter writer) {
      try {
         Class<?> clazz = Converter.class.getClassLoader().loadClass("javanet.staxutils.IndentingXMLStreamWriter");
         Constructor<?> c = clazz.getConstructor(XMLStreamWriter.class);
         writer = (XMLStreamWriter)XMLStreamWriter.class.cast(c.newInstance(writer));
      } catch (Exception var3) {
         if (logMissingStaxUtilsWarning.compareAndSet(false, true)) {
            LOGGER.log(Level.WARNING, "Put stax-utils.jar to the classpath to indent the dump output", (Throwable)var3);
         }
      }

      return writer;
   }
}
