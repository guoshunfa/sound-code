package com.sun.xml.internal.org.jvnet.mimepull;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;

class MIMEParser implements Iterable<MIMEEvent> {
   private static final Logger LOGGER = Logger.getLogger(MIMEParser.class.getName());
   private static final String HEADER_ENCODING = "ISO8859-1";
   private static final int NO_LWSP = 1000;
   private MIMEParser.STATE state;
   private final InputStream in;
   private final byte[] bndbytes;
   private final int bl;
   private final MIMEConfig config;
   private final int[] bcs;
   private final int[] gss;
   private boolean parsed;
   private boolean done;
   private boolean eof;
   private final int capacity;
   private byte[] buf;
   private int len;
   private boolean bol;

   MIMEParser(InputStream in, String boundary, MIMEConfig config) {
      this.state = MIMEParser.STATE.START_MESSAGE;
      this.bcs = new int[128];
      this.done = false;
      this.in = in;
      this.bndbytes = getBytes("--" + boundary);
      this.bl = this.bndbytes.length;
      this.config = config;
      this.gss = new int[this.bl];
      this.compileBoundaryPattern();
      this.capacity = config.chunkSize + 2 + this.bl + 4 + 1000;
      this.createBuf(this.capacity);
   }

   public Iterator<MIMEEvent> iterator() {
      return new MIMEParser.MIMEEventIterator();
   }

   private InternetHeaders readHeaders() {
      if (!this.eof) {
         this.fillBuf();
      }

      return new InternetHeaders(new MIMEParser.LineInputStream());
   }

   private ByteBuffer readBody() {
      if (!this.eof) {
         this.fillBuf();
      }

      int start = this.match(this.buf, 0, this.len);
      int chunkLen;
      if (start == -1) {
         assert this.eof || this.len >= this.config.chunkSize;

         chunkLen = this.eof ? this.len : this.config.chunkSize;
         if (this.eof) {
            this.done = true;
            throw new MIMEParsingException("Reached EOF, but there is no closing MIME boundary.");
         } else {
            return this.adjustBuf(chunkLen, this.len - chunkLen);
         }
      } else {
         chunkLen = start;
         if (!this.bol || start != 0) {
            if (start <= 0 || this.buf[start - 1] != 10 && this.buf[start - 1] != 13) {
               return this.adjustBuf(start + 1, this.len - start - 1);
            }

            chunkLen = start - 1;
            if (this.buf[start - 1] == 10 && start > 1 && this.buf[start - 2] == 13) {
               --chunkLen;
            }
         }

         if (start + this.bl + 1 < this.len && this.buf[start + this.bl] == 45 && this.buf[start + this.bl + 1] == 45) {
            this.state = MIMEParser.STATE.END_PART;
            this.done = true;
            return this.adjustBuf(chunkLen, 0);
         } else {
            int lwsp = 0;

            for(int i = start + this.bl; i < this.len && (this.buf[i] == 32 || this.buf[i] == 9); ++i) {
               ++lwsp;
            }

            if (start + this.bl + lwsp < this.len && this.buf[start + this.bl + lwsp] == 10) {
               this.state = MIMEParser.STATE.END_PART;
               return this.adjustBuf(chunkLen, this.len - start - this.bl - lwsp - 1);
            } else if (start + this.bl + lwsp + 1 < this.len && this.buf[start + this.bl + lwsp] == 13 && this.buf[start + this.bl + lwsp + 1] == 10) {
               this.state = MIMEParser.STATE.END_PART;
               return this.adjustBuf(chunkLen, this.len - start - this.bl - lwsp - 2);
            } else if (start + this.bl + lwsp + 1 < this.len) {
               return this.adjustBuf(chunkLen + 1, this.len - chunkLen - 1);
            } else if (this.eof) {
               this.done = true;
               throw new MIMEParsingException("Reached EOF, but there is no closing MIME boundary.");
            } else {
               return this.adjustBuf(chunkLen, this.len - chunkLen);
            }
         }
      }
   }

   private ByteBuffer adjustBuf(int chunkSize, int remaining) {
      assert this.buf != null;

      assert chunkSize >= 0;

      assert remaining >= 0;

      byte[] temp = this.buf;
      this.createBuf(remaining);
      System.arraycopy(temp, this.len - remaining, this.buf, 0, remaining);
      this.len = remaining;
      return ByteBuffer.wrap(temp, 0, chunkSize);
   }

   private void createBuf(int min) {
      this.buf = new byte[min < this.capacity ? this.capacity : min];
   }

   private void skipPreamble() {
      while(true) {
         if (!this.eof) {
            this.fillBuf();
         }

         int start = this.match(this.buf, 0, this.len);
         if (start == -1) {
            if (this.eof) {
               throw new MIMEParsingException("Missing start boundary");
            }

            this.adjustBuf(this.len - this.bl + 1, this.bl - 1);
         } else if (start > this.config.chunkSize) {
            this.adjustBuf(start, this.len - start);
         } else {
            int lwsp = 0;

            for(int i = start + this.bl; i < this.len && (this.buf[i] == 32 || this.buf[i] == 9); ++i) {
               ++lwsp;
            }

            label69: {
               if (start + this.bl + lwsp < this.len && (this.buf[start + this.bl + lwsp] == 10 || this.buf[start + this.bl + lwsp] == 13)) {
                  if (this.buf[start + this.bl + lwsp] == 10) {
                     this.adjustBuf(start + this.bl + lwsp + 1, this.len - start - this.bl - lwsp - 1);
                     break label69;
                  }

                  if (start + this.bl + lwsp + 1 < this.len && this.buf[start + this.bl + lwsp + 1] == 10) {
                     this.adjustBuf(start + this.bl + lwsp + 2, this.len - start - this.bl - lwsp - 2);
                     break label69;
                  }
               }

               this.adjustBuf(start + 1, this.len - start - 1);
               continue;
            }

            if (LOGGER.isLoggable(Level.FINE)) {
               LOGGER.log(Level.FINE, (String)"Skipped the preamble. buffer len={0}", (Object)this.len);
            }

            return;
         }
      }
   }

   private static byte[] getBytes(String s) {
      char[] chars = s.toCharArray();
      int size = chars.length;
      byte[] bytes = new byte[size];

      for(int i = 0; i < size; bytes[i] = (byte)chars[i++]) {
      }

      return bytes;
   }

   private void compileBoundaryPattern() {
      int i;
      for(i = 0; i < this.bndbytes.length; ++i) {
         this.bcs[this.bndbytes[i] & 127] = i + 1;
      }

      label30:
      for(i = this.bndbytes.length; i > 0; --i) {
         int j;
         for(j = this.bndbytes.length - 1; j >= i; --j) {
            if (this.bndbytes[j] != this.bndbytes[j - i]) {
               continue label30;
            }

            this.gss[j - 1] = i;
         }

         while(j > 0) {
            --j;
            this.gss[j] = i;
         }
      }

      this.gss[this.bndbytes.length - 1] = 1;
   }

   private int match(byte[] mybuf, int off, int len) {
      int j;
      byte ch;
      label23:
      for(int last = len - this.bndbytes.length; off <= last; off += Math.max(j + 1 - this.bcs[ch & 127], this.gss[j])) {
         for(j = this.bndbytes.length - 1; j >= 0; --j) {
            ch = mybuf[off + j];
            if (ch != this.bndbytes[j]) {
               continue label23;
            }
         }

         return off;
      }

      return -1;
   }

   private void fillBuf() {
      if (LOGGER.isLoggable(Level.FINER)) {
         LOGGER.log(Level.FINER, (String)"Before fillBuf() buffer len={0}", (Object)this.len);
      }

      assert !this.eof;

      while(this.len < this.buf.length) {
         int read;
         try {
            read = this.in.read(this.buf, this.len, this.buf.length - this.len);
         } catch (IOException var4) {
            throw new MIMEParsingException(var4);
         }

         if (read == -1) {
            this.eof = true;

            try {
               if (LOGGER.isLoggable(Level.FINE)) {
                  LOGGER.fine("Closing the input stream.");
               }

               this.in.close();
               break;
            } catch (IOException var3) {
               throw new MIMEParsingException(var3);
            }
         }

         this.len += read;
      }

      if (LOGGER.isLoggable(Level.FINER)) {
         LOGGER.log(Level.FINER, (String)"After fillBuf() buffer len={0}", (Object)this.len);
      }

   }

   private void doubleBuf() {
      byte[] temp = new byte[2 * this.len];
      System.arraycopy(this.buf, 0, temp, 0, this.len);
      this.buf = temp;
      if (!this.eof) {
         this.fillBuf();
      }

   }

   class LineInputStream {
      private int offset;

      public String readLine() throws IOException {
         int hdrLen = 0;

         byte lwsp;
         for(lwsp = 0; this.offset + hdrLen < MIMEParser.this.len; ++hdrLen) {
            if (MIMEParser.this.buf[this.offset + hdrLen] == 10) {
               lwsp = 1;
               break;
            }

            if (this.offset + hdrLen + 1 == MIMEParser.this.len) {
               MIMEParser.this.doubleBuf();
            }

            if (this.offset + hdrLen + 1 >= MIMEParser.this.len) {
               assert MIMEParser.this.eof;

               return null;
            }

            if (MIMEParser.this.buf[this.offset + hdrLen] == 13 && MIMEParser.this.buf[this.offset + hdrLen + 1] == 10) {
               lwsp = 2;
               break;
            }
         }

         if (hdrLen == 0) {
            MIMEParser.this.adjustBuf(this.offset + lwsp, MIMEParser.this.len - this.offset - lwsp);
            return null;
         } else {
            String hdr = new String(MIMEParser.this.buf, this.offset, hdrLen, "ISO8859-1");
            this.offset += hdrLen + lwsp;
            return hdr;
         }
      }
   }

   class MIMEEventIterator implements Iterator<MIMEEvent> {
      public boolean hasNext() {
         return !MIMEParser.this.parsed;
      }

      public MIMEEvent next() {
         switch(MIMEParser.this.state) {
         case START_MESSAGE:
            if (MIMEParser.LOGGER.isLoggable(Level.FINER)) {
               MIMEParser.LOGGER.log(Level.FINER, (String)"MIMEParser state={0}", (Object)MIMEParser.STATE.START_MESSAGE);
            }

            MIMEParser.this.state = MIMEParser.STATE.SKIP_PREAMBLE;
            return MIMEEvent.START_MESSAGE;
         case SKIP_PREAMBLE:
            if (MIMEParser.LOGGER.isLoggable(Level.FINER)) {
               MIMEParser.LOGGER.log(Level.FINER, (String)"MIMEParser state={0}", (Object)MIMEParser.STATE.SKIP_PREAMBLE);
            }

            MIMEParser.this.skipPreamble();
         case START_PART:
            if (MIMEParser.LOGGER.isLoggable(Level.FINER)) {
               MIMEParser.LOGGER.log(Level.FINER, (String)"MIMEParser state={0}", (Object)MIMEParser.STATE.START_PART);
            }

            MIMEParser.this.state = MIMEParser.STATE.HEADERS;
            return MIMEEvent.START_PART;
         case HEADERS:
            if (MIMEParser.LOGGER.isLoggable(Level.FINER)) {
               MIMEParser.LOGGER.log(Level.FINER, (String)"MIMEParser state={0}", (Object)MIMEParser.STATE.HEADERS);
            }

            InternetHeaders ih = MIMEParser.this.readHeaders();
            MIMEParser.this.state = MIMEParser.STATE.BODY;
            MIMEParser.this.bol = true;
            return new MIMEEvent.Headers(ih);
         case BODY:
            if (MIMEParser.LOGGER.isLoggable(Level.FINER)) {
               MIMEParser.LOGGER.log(Level.FINER, (String)"MIMEParser state={0}", (Object)MIMEParser.STATE.BODY);
            }

            ByteBuffer buf = MIMEParser.this.readBody();
            MIMEParser.this.bol = false;
            return new MIMEEvent.Content(buf);
         case END_PART:
            if (MIMEParser.LOGGER.isLoggable(Level.FINER)) {
               MIMEParser.LOGGER.log(Level.FINER, (String)"MIMEParser state={0}", (Object)MIMEParser.STATE.END_PART);
            }

            if (MIMEParser.this.done) {
               MIMEParser.this.state = MIMEParser.STATE.END_MESSAGE;
            } else {
               MIMEParser.this.state = MIMEParser.STATE.START_PART;
            }

            return MIMEEvent.END_PART;
         case END_MESSAGE:
            if (MIMEParser.LOGGER.isLoggable(Level.FINER)) {
               MIMEParser.LOGGER.log(Level.FINER, (String)"MIMEParser state={0}", (Object)MIMEParser.STATE.END_MESSAGE);
            }

            MIMEParser.this.parsed = true;
            return MIMEEvent.END_MESSAGE;
         default:
            throw new MIMEParsingException("Unknown Parser state = " + MIMEParser.this.state);
         }
      }

      public void remove() {
         throw new UnsupportedOperationException();
      }
   }

   private static enum STATE {
      START_MESSAGE,
      SKIP_PREAMBLE,
      START_PART,
      HEADERS,
      BODY,
      END_PART,
      END_MESSAGE;
   }
}
