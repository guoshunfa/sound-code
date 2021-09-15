package com.sun.xml.internal.org.jvnet.mimepull;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MIMEMessage {
   private static final Logger LOGGER = Logger.getLogger(MIMEMessage.class.getName());
   MIMEConfig config;
   private final InputStream in;
   private final List<MIMEPart> partsList;
   private final Map<String, MIMEPart> partsMap;
   private final Iterator<MIMEEvent> it;
   private boolean parsed;
   private MIMEPart currentPart;
   private int currentIndex;

   public MIMEMessage(InputStream in, String boundary) {
      this(in, boundary, new MIMEConfig());
   }

   public MIMEMessage(InputStream in, String boundary, MIMEConfig config) {
      this.in = in;
      this.config = config;
      MIMEParser parser = new MIMEParser(in, boundary, config);
      this.it = parser.iterator();
      this.partsList = new ArrayList();
      this.partsMap = new HashMap();
      if (config.isParseEagerly()) {
         this.parseAll();
      }

   }

   public List<MIMEPart> getAttachments() {
      if (!this.parsed) {
         this.parseAll();
      }

      return this.partsList;
   }

   public MIMEPart getPart(int index) {
      LOGGER.log(Level.FINE, (String)"index={0}", (Object)index);
      MIMEPart part = index < this.partsList.size() ? (MIMEPart)this.partsList.get(index) : null;
      if (this.parsed && part == null) {
         throw new MIMEParsingException("There is no " + index + " attachment part ");
      } else {
         if (part == null) {
            part = new MIMEPart(this);
            this.partsList.add(index, part);
         }

         LOGGER.log(Level.FINE, "Got attachment at index={0} attachment={1}", new Object[]{index, part});
         return part;
      }
   }

   public MIMEPart getPart(String contentId) {
      LOGGER.log(Level.FINE, (String)"Content-ID={0}", (Object)contentId);
      MIMEPart part = this.getDecodedCidPart(contentId);
      if (this.parsed && part == null) {
         throw new MIMEParsingException("There is no attachment part with Content-ID = " + contentId);
      } else {
         if (part == null) {
            part = new MIMEPart(this, contentId);
            this.partsMap.put(contentId, part);
         }

         LOGGER.log(Level.FINE, "Got attachment for Content-ID={0} attachment={1}", new Object[]{contentId, part});
         return part;
      }
   }

   private MIMEPart getDecodedCidPart(String cid) {
      MIMEPart part = (MIMEPart)this.partsMap.get(cid);
      if (part == null && cid.indexOf(37) != -1) {
         try {
            String tempCid = URLDecoder.decode(cid, "utf-8");
            part = (MIMEPart)this.partsMap.get(tempCid);
         } catch (UnsupportedEncodingException var4) {
         }
      }

      return part;
   }

   public final void parseAll() {
      while(this.makeProgress()) {
      }

   }

   public synchronized boolean makeProgress() {
      if (!this.it.hasNext()) {
         return false;
      } else {
         MIMEEvent event = (MIMEEvent)this.it.next();
         switch(event.getEventType()) {
         case START_MESSAGE:
            LOGGER.log(Level.FINE, (String)"MIMEEvent={0}", (Object)MIMEEvent.EVENT_TYPE.START_MESSAGE);
            break;
         case START_PART:
            LOGGER.log(Level.FINE, (String)"MIMEEvent={0}", (Object)MIMEEvent.EVENT_TYPE.START_PART);
            break;
         case HEADERS:
            LOGGER.log(Level.FINE, (String)"MIMEEvent={0}", (Object)MIMEEvent.EVENT_TYPE.HEADERS);
            MIMEEvent.Headers headers = (MIMEEvent.Headers)event;
            InternetHeaders ih = headers.getHeaders();
            List<String> cids = ih.getHeader("content-id");
            String cid = cids != null ? (String)cids.get(0) : this.currentIndex + "";
            if (cid.length() > 2 && cid.charAt(0) == '<') {
               cid = cid.substring(1, cid.length() - 1);
            }

            MIMEPart listPart = this.currentIndex < this.partsList.size() ? (MIMEPart)this.partsList.get(this.currentIndex) : null;
            MIMEPart mapPart = this.getDecodedCidPart(cid);
            if (listPart == null && mapPart == null) {
               this.currentPart = this.getPart(cid);
               this.partsList.add(this.currentIndex, this.currentPart);
            } else if (listPart == null) {
               this.currentPart = mapPart;
               this.partsList.add(this.currentIndex, mapPart);
            } else if (mapPart == null) {
               this.currentPart = listPart;
               this.currentPart.setContentId(cid);
               this.partsMap.put(cid, this.currentPart);
            } else if (listPart != mapPart) {
               throw new MIMEParsingException("Created two different attachments using Content-ID and index");
            }

            this.currentPart.setHeaders(ih);
            break;
         case CONTENT:
            LOGGER.log(Level.FINER, (String)"MIMEEvent={0}", (Object)MIMEEvent.EVENT_TYPE.CONTENT);
            MIMEEvent.Content content = (MIMEEvent.Content)event;
            ByteBuffer buf = content.getData();
            this.currentPart.addBody(buf);
            break;
         case END_PART:
            LOGGER.log(Level.FINE, (String)"MIMEEvent={0}", (Object)MIMEEvent.EVENT_TYPE.END_PART);
            this.currentPart.doneParsing();
            ++this.currentIndex;
            break;
         case END_MESSAGE:
            LOGGER.log(Level.FINE, (String)"MIMEEvent={0}", (Object)MIMEEvent.EVENT_TYPE.END_MESSAGE);
            this.parsed = true;

            try {
               this.in.close();
               break;
            } catch (IOException var11) {
               throw new MIMEParsingException(var11);
            }
         default:
            throw new MIMEParsingException("Unknown Parser state = " + event.getEventType());
         }

         return true;
      }
   }
}
