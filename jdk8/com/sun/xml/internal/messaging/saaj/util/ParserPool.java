package com.sun.xml.internal.messaging.saaj.util;

import com.sun.org.apache.xerces.internal.jaxp.SAXParserFactoryImpl;
import com.sun.org.apache.xerces.internal.util.SymbolTable;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import org.xml.sax.SAXException;
import org.xml.sax.SAXNotRecognizedException;
import org.xml.sax.SAXNotSupportedException;

public class ParserPool {
   private final BlockingQueue queue;
   private SAXParserFactory factory;
   private int capacity;

   public ParserPool(int capacity) {
      this.capacity = capacity;
      this.queue = new ArrayBlockingQueue(capacity);
      this.factory = new SAXParserFactoryImpl();
      this.factory.setNamespaceAware(true);

      for(int i = 0; i < capacity; ++i) {
         try {
            this.queue.put(this.factory.newSAXParser());
         } catch (InterruptedException var4) {
            Thread.currentThread().interrupt();
            throw new RuntimeException(var4);
         } catch (ParserConfigurationException var5) {
            throw new RuntimeException(var5);
         } catch (SAXException var6) {
            throw new RuntimeException(var6);
         }
      }

   }

   public SAXParser get() throws ParserConfigurationException, SAXException {
      try {
         return (SAXParser)this.queue.take();
      } catch (InterruptedException var2) {
         throw new SAXException(var2);
      }
   }

   public void put(SAXParser parser) {
      this.queue.offer(parser);
   }

   public void returnParser(SAXParser saxParser) {
      saxParser.reset();
      this.resetSaxParser(saxParser);
      this.put(saxParser);
   }

   private void resetSaxParser(SAXParser parser) {
      try {
         SymbolTable table = new SymbolTable();
         parser.setProperty("http://apache.org/xml/properties/internal/symbol-table", table);
      } catch (SAXNotRecognizedException var3) {
      } catch (SAXNotSupportedException var4) {
      }

   }
}
