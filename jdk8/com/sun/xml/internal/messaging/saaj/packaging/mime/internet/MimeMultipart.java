package com.sun.xml.internal.messaging.saaj.packaging.mime.internet;

import com.sun.xml.internal.messaging.saaj.packaging.mime.MessagingException;
import com.sun.xml.internal.messaging.saaj.packaging.mime.MultipartDataSource;
import com.sun.xml.internal.messaging.saaj.packaging.mime.util.ASCIIUtility;
import com.sun.xml.internal.messaging.saaj.packaging.mime.util.LineInputStream;
import com.sun.xml.internal.messaging.saaj.packaging.mime.util.OutputUtil;
import com.sun.xml.internal.messaging.saaj.util.ByteOutputStream;
import com.sun.xml.internal.messaging.saaj.util.FinalArrayList;
import com.sun.xml.internal.messaging.saaj.util.SAAJUtil;
import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import javax.activation.DataSource;

public class MimeMultipart {
   protected DataSource ds;
   protected boolean parsed;
   protected FinalArrayList parts;
   protected ContentType contentType;
   protected MimeBodyPart parent;
   protected static final boolean ignoreMissingEndBoundary = SAAJUtil.getSystemBoolean("saaj.mime.multipart.ignoremissingendboundary");

   public MimeMultipart() {
      this("mixed");
   }

   public MimeMultipart(String subtype) {
      this.ds = null;
      this.parsed = true;
      this.parts = new FinalArrayList();
      String boundary = UniqueValue.getUniqueBoundaryValue();
      this.contentType = new ContentType("multipart", subtype, (ParameterList)null);
      this.contentType.setParameter("boundary", boundary);
   }

   public MimeMultipart(DataSource ds, ContentType ct) throws MessagingException {
      this.ds = null;
      this.parsed = true;
      this.parts = new FinalArrayList();
      this.parsed = false;
      this.ds = ds;
      if (ct == null) {
         this.contentType = new ContentType(ds.getContentType());
      } else {
         this.contentType = ct;
      }

   }

   public void setSubType(String subtype) {
      this.contentType.setSubType(subtype);
   }

   public int getCount() throws MessagingException {
      this.parse();
      return this.parts == null ? 0 : this.parts.size();
   }

   public MimeBodyPart getBodyPart(int index) throws MessagingException {
      this.parse();
      if (this.parts == null) {
         throw new IndexOutOfBoundsException("No such BodyPart");
      } else {
         return (MimeBodyPart)this.parts.get(index);
      }
   }

   public MimeBodyPart getBodyPart(String CID) throws MessagingException {
      this.parse();
      int count = this.getCount();

      for(int i = 0; i < count; ++i) {
         MimeBodyPart part = this.getBodyPart(i);
         String s = part.getContentID();
         String sNoAngle = s != null ? s.replaceFirst("^<", "").replaceFirst(">$", "") : null;
         if (s != null && (s.equals(CID) || CID.equals(sNoAngle))) {
            return part;
         }
      }

      return null;
   }

   protected void updateHeaders() throws MessagingException {
      for(int i = 0; i < this.parts.size(); ++i) {
         ((MimeBodyPart)this.parts.get(i)).updateHeaders();
      }

   }

   public void writeTo(OutputStream os) throws IOException, MessagingException {
      this.parse();
      String boundary = "--" + this.contentType.getParameter("boundary");

      for(int i = 0; i < this.parts.size(); ++i) {
         OutputUtil.writeln(boundary, os);
         this.getBodyPart(i).writeTo(os);
         OutputUtil.writeln(os);
      }

      OutputUtil.writeAsAscii(boundary, os);
      OutputUtil.writeAsAscii("--", os);
      os.flush();
   }

   protected void parse() throws MessagingException {
      if (!this.parsed) {
         SharedInputStream sin = null;
         long start = 0L;
         long end = 0L;
         boolean foundClosingBoundary = false;

         Object in;
         try {
            in = this.ds.getInputStream();
            if (!(in instanceof ByteArrayInputStream) && !(in instanceof BufferedInputStream) && !(in instanceof SharedInputStream)) {
               in = new BufferedInputStream((InputStream)in);
            }
         } catch (Exception var22) {
            throw new MessagingException("No inputstream from datasource");
         }

         if (in instanceof SharedInputStream) {
            sin = (SharedInputStream)in;
         }

         String boundary = "--" + this.contentType.getParameter("boundary");
         byte[] bndbytes = ASCIIUtility.getBytes(boundary);
         int bl = bndbytes.length;

         try {
            LineInputStream lin = new LineInputStream((InputStream)in);

            String line;
            while((line = lin.readLine()) != null) {
               int i;
               for(i = line.length() - 1; i >= 0; --i) {
                  char c = line.charAt(i);
                  if (c != ' ' && c != '\t') {
                     break;
                  }
               }

               line = line.substring(0, i + 1);
               if (line.equals(boundary)) {
                  break;
               }
            }

            if (line == null) {
               throw new MessagingException("Missing start boundary");
            }

            MimeBodyPart part;
            for(boolean done = false; !done; this.addBodyPart(part)) {
               InternetHeaders headers = null;
               if (sin == null) {
                  headers = this.createInternetHeaders((InputStream)in);
               } else {
                  start = sin.getPosition();

                  while((line = lin.readLine()) != null && line.length() > 0) {
                  }

                  if (line == null) {
                     if (!ignoreMissingEndBoundary) {
                        throw new MessagingException("Missing End Boundary for Mime Package : EOF while skipping headers");
                     }
                     break;
                  }
               }

               if (!((InputStream)in).markSupported()) {
                  throw new MessagingException("Stream doesn't support mark");
               }

               ByteOutputStream buf = null;
               if (sin == null) {
                  buf = new ByteOutputStream();
               }

               boolean bol = true;
               int eol1 = -1;
               int eol2 = -1;

               label190:
               while(true) {
                  while(true) {
                     if (bol) {
                        ((InputStream)in).mark(bl + 4 + 1000);

                        int i;
                        for(i = 0; i < bl && ((InputStream)in).read() == bndbytes[i]; ++i) {
                        }

                        if (i == bl) {
                           int b2 = ((InputStream)in).read();
                           if (b2 == 45 && ((InputStream)in).read() == 45) {
                              done = true;
                              foundClosingBoundary = true;
                              break label190;
                           }

                           while(b2 == 32 || b2 == 9) {
                              b2 = ((InputStream)in).read();
                           }

                           if (b2 == 10) {
                              break label190;
                           }

                           if (b2 == 13) {
                              ((InputStream)in).mark(1);
                              if (((InputStream)in).read() != 10) {
                                 ((InputStream)in).reset();
                              }
                              break label190;
                           }
                        }

                        ((InputStream)in).reset();
                        if (buf != null && eol1 != -1) {
                           buf.write(eol1);
                           if (eol2 != -1) {
                              buf.write(eol2);
                           }

                           eol2 = -1;
                           eol1 = -1;
                        }
                     }

                     int b;
                     if ((b = ((InputStream)in).read()) < 0) {
                        done = true;
                        break label190;
                     }

                     if (b != 13 && b != 10) {
                        bol = false;
                        if (buf != null) {
                           buf.write(b);
                        }
                     } else {
                        bol = true;
                        if (sin != null) {
                           end = sin.getPosition() - 1L;
                        }

                        eol1 = b;
                        if (b == 13) {
                           ((InputStream)in).mark(1);
                           if ((b = ((InputStream)in).read()) == 10) {
                              eol2 = b;
                           } else {
                              ((InputStream)in).reset();
                           }
                        }
                     }
                  }
               }

               if (sin != null) {
                  part = this.createMimeBodyPart(sin.newStream(start, end));
               } else {
                  part = this.createMimeBodyPart(headers, buf.getBytes(), buf.getCount());
               }
            }
         } catch (IOException var23) {
            throw new MessagingException("IO Error", var23);
         }

         if (!ignoreMissingEndBoundary && !foundClosingBoundary && sin == null) {
            throw new MessagingException("Missing End Boundary for Mime Package : EOF while skipping headers");
         } else {
            this.parsed = true;
         }
      }
   }

   protected InternetHeaders createInternetHeaders(InputStream is) throws MessagingException {
      return new InternetHeaders(is);
   }

   protected MimeBodyPart createMimeBodyPart(InternetHeaders headers, byte[] content, int len) {
      return new MimeBodyPart(headers, content, len);
   }

   protected MimeBodyPart createMimeBodyPart(InputStream is) throws MessagingException {
      return new MimeBodyPart(is);
   }

   protected void setMultipartDataSource(MultipartDataSource mp) throws MessagingException {
      this.contentType = new ContentType(mp.getContentType());
      int count = mp.getCount();

      for(int i = 0; i < count; ++i) {
         this.addBodyPart(mp.getBodyPart(i));
      }

   }

   public ContentType getContentType() {
      return this.contentType;
   }

   public boolean removeBodyPart(MimeBodyPart part) throws MessagingException {
      if (this.parts == null) {
         throw new MessagingException("No such body part");
      } else {
         boolean ret = this.parts.remove(part);
         part.setParent((MimeMultipart)null);
         return ret;
      }
   }

   public void removeBodyPart(int index) {
      if (this.parts == null) {
         throw new IndexOutOfBoundsException("No such BodyPart");
      } else {
         MimeBodyPart part = (MimeBodyPart)this.parts.get(index);
         this.parts.remove(index);
         part.setParent((MimeMultipart)null);
      }
   }

   public synchronized void addBodyPart(MimeBodyPart part) {
      if (this.parts == null) {
         this.parts = new FinalArrayList();
      }

      this.parts.add(part);
      part.setParent(this);
   }

   public synchronized void addBodyPart(MimeBodyPart part, int index) {
      if (this.parts == null) {
         this.parts = new FinalArrayList();
      }

      this.parts.add(index, part);
      part.setParent(this);
   }

   MimeBodyPart getParent() {
      return this.parent;
   }

   void setParent(MimeBodyPart parent) {
      this.parent = parent;
   }
}
