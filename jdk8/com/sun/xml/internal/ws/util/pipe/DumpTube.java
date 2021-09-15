package com.sun.xml.internal.ws.util.pipe;

import com.sun.xml.internal.ws.api.message.Packet;
import com.sun.xml.internal.ws.api.pipe.NextAction;
import com.sun.xml.internal.ws.api.pipe.Tube;
import com.sun.xml.internal.ws.api.pipe.TubeCloner;
import com.sun.xml.internal.ws.api.pipe.helper.AbstractFilterTubeImpl;
import com.sun.xml.internal.ws.api.pipe.helper.AbstractTubeImpl;
import java.io.OutputStream;
import java.io.PrintStream;
import java.lang.reflect.Constructor;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

public class DumpTube extends AbstractFilterTubeImpl {
   private final String name;
   private final PrintStream out;
   private final XMLOutputFactory staxOut;
   private static boolean warnStaxUtils;

   public DumpTube(String name, PrintStream out, Tube next) {
      super(next);
      this.name = name;
      this.out = out;
      this.staxOut = XMLOutputFactory.newInstance();
   }

   protected DumpTube(DumpTube that, TubeCloner cloner) {
      super(that, cloner);
      this.name = that.name;
      this.out = that.out;
      this.staxOut = that.staxOut;
   }

   public NextAction processRequest(Packet request) {
      this.dump("request", request);
      return super.processRequest(request);
   }

   public NextAction processResponse(Packet response) {
      this.dump("response", response);
      return super.processResponse(response);
   }

   protected void dump(String header, Packet packet) {
      this.out.println("====[" + this.name + ":" + header + "]====");
      if (packet.getMessage() == null) {
         this.out.println("(none)");
      } else {
         try {
            XMLStreamWriter writer = this.staxOut.createXMLStreamWriter((OutputStream)(new PrintStream(this.out) {
               public void close() {
               }
            }));
            writer = this.createIndenter(writer);
            packet.getMessage().copy().writeTo(writer);
            writer.close();
         } catch (XMLStreamException var4) {
            var4.printStackTrace(this.out);
         }
      }

      this.out.println("============");
   }

   private XMLStreamWriter createIndenter(XMLStreamWriter writer) {
      try {
         Class clazz = this.getClass().getClassLoader().loadClass("javanet.staxutils.IndentingXMLStreamWriter");
         Constructor c = clazz.getConstructor(XMLStreamWriter.class);
         writer = (XMLStreamWriter)c.newInstance(writer);
      } catch (Exception var4) {
         if (!warnStaxUtils) {
            warnStaxUtils = true;
            this.out.println("WARNING: put stax-utils.jar to the classpath to indent the dump output");
         }
      }

      return writer;
   }

   public AbstractTubeImpl copy(TubeCloner cloner) {
      return new DumpTube(this, cloner);
   }
}
