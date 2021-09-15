package com.sun.xml.internal.messaging.saaj.packaging.mime.internet;

import com.sun.xml.internal.messaging.saaj.packaging.mime.MessagingException;
import com.sun.xml.internal.messaging.saaj.soap.AttachmentPartImpl;
import com.sun.xml.internal.org.jvnet.mimepull.MIMEConfig;
import com.sun.xml.internal.org.jvnet.mimepull.MIMEMessage;
import com.sun.xml.internal.org.jvnet.mimepull.MIMEPart;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.List;
import javax.activation.DataSource;

public class MimePullMultipart extends MimeMultipart {
   private InputStream in = null;
   private String boundary = null;
   private MIMEMessage mm = null;
   private DataSource dataSource = null;
   private ContentType contType = null;
   private String startParam = null;
   private MIMEPart soapPart = null;

   public MimePullMultipart(DataSource ds, ContentType ct) throws MessagingException {
      this.parsed = false;
      if (ct == null) {
         this.contType = new ContentType(ds.getContentType());
      } else {
         this.contType = ct;
      }

      this.dataSource = ds;
      this.boundary = this.contType.getParameter("boundary");
   }

   public MIMEPart readAndReturnSOAPPart() throws MessagingException {
      if (this.soapPart != null) {
         throw new MessagingException("Inputstream from datasource was already consumed");
      } else {
         this.readSOAPPart();
         return this.soapPart;
      }
   }

   protected void readSOAPPart() throws MessagingException {
      try {
         if (this.soapPart == null) {
            this.in = this.dataSource.getInputStream();
            MIMEConfig config = new MIMEConfig();
            this.mm = new MIMEMessage(this.in, this.boundary, config);
            String st = this.contType.getParameter("start");
            if (this.startParam == null) {
               this.soapPart = this.mm.getPart(0);
            } else {
               if (st != null && st.length() > 2 && st.charAt(0) == '<' && st.charAt(st.length() - 1) == '>') {
                  st = st.substring(1, st.length() - 1);
               }

               this.startParam = st;
               this.soapPart = this.mm.getPart(this.startParam);
            }

         }
      } catch (IOException var3) {
         throw new MessagingException("No inputstream from datasource", var3);
      }
   }

   public void parseAll() throws MessagingException {
      if (!this.parsed) {
         if (this.soapPart == null) {
            this.readSOAPPart();
         }

         List<MIMEPart> prts = this.mm.getAttachments();
         Iterator var2 = prts.iterator();

         while(var2.hasNext()) {
            MIMEPart part = (MIMEPart)var2.next();
            if (part != this.soapPart) {
               new AttachmentPartImpl(part);
               this.addBodyPart(new MimeBodyPart(part));
            }
         }

         this.parsed = true;
      }
   }

   protected void parse() throws MessagingException {
      this.parseAll();
   }
}
