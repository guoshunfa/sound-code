package javax.swing.text.rtf;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;

abstract class RTFParser extends AbstractFilter {
   public int level = 0;
   private int state = 0;
   private StringBuffer currentCharacters = new StringBuffer();
   private String pendingKeyword = null;
   private int pendingCharacter;
   private long binaryBytesLeft;
   ByteArrayOutputStream binaryBuf;
   private boolean[] savedSpecials;
   protected PrintStream warnings;
   private final int S_text = 0;
   private final int S_backslashed = 1;
   private final int S_token = 2;
   private final int S_parameter = 3;
   private final int S_aftertick = 4;
   private final int S_aftertickc = 5;
   private final int S_inblob = 6;
   static final boolean[] rtfSpecialsTable;

   public abstract boolean handleKeyword(String var1);

   public abstract boolean handleKeyword(String var1, int var2);

   public abstract void handleText(String var1);

   public void handleText(char var1) {
      this.handleText(String.valueOf(var1));
   }

   public abstract void handleBinaryBlob(byte[] var1);

   public abstract void begingroup();

   public abstract void endgroup();

   public RTFParser() {
      this.specialsTable = rtfSpecialsTable;
   }

   public void writeSpecial(int var1) throws IOException {
      this.write((char)var1);
   }

   protected void warning(String var1) {
      if (this.warnings != null) {
         this.warnings.println(var1);
      }

   }

   public void write(String var1) throws IOException {
      if (this.state != 0) {
         int var2 = 0;

         int var3;
         for(var3 = var1.length(); var2 < var3 && this.state != 0; ++var2) {
            this.write(var1.charAt(var2));
         }

         if (var2 >= var3) {
            return;
         }

         var1 = var1.substring(var2);
      }

      if (this.currentCharacters.length() > 0) {
         this.currentCharacters.append(var1);
      } else {
         this.handleText(var1);
      }

   }

   public void write(char var1) throws IOException {
      boolean var2;
      switch(this.state) {
      case 0:
         if (var1 != '\n' && var1 != '\r') {
            if (var1 == '{') {
               if (this.currentCharacters.length() > 0) {
                  this.handleText(this.currentCharacters.toString());
                  this.currentCharacters = new StringBuffer();
               }

               ++this.level;
               this.begingroup();
            } else if (var1 == '}') {
               if (this.currentCharacters.length() > 0) {
                  this.handleText(this.currentCharacters.toString());
                  this.currentCharacters = new StringBuffer();
               }

               if (this.level == 0) {
                  throw new IOException("Too many close-groups in RTF text");
               }

               this.endgroup();
               --this.level;
            } else if (var1 == '\\') {
               if (this.currentCharacters.length() > 0) {
                  this.handleText(this.currentCharacters.toString());
                  this.currentCharacters = new StringBuffer();
               }

               this.state = 1;
            } else {
               this.currentCharacters.append(var1);
            }
         }
         break;
      case 1:
         if (var1 == '\'') {
            this.state = 4;
            break;
         } else if (!Character.isLetter(var1)) {
            char[] var6 = new char[]{var1};
            if (!this.handleKeyword(new String(var6))) {
               this.warning("Unknown keyword: " + var6 + " (" + (byte)var1 + ")");
            }

            this.state = 0;
            this.pendingKeyword = null;
            break;
         } else {
            this.state = 2;
         }
      case 2:
         if (Character.isLetter(var1)) {
            this.currentCharacters.append(var1);
         } else {
            this.pendingKeyword = this.currentCharacters.toString();
            this.currentCharacters = new StringBuffer();
            if (!Character.isDigit(var1) && var1 != '-') {
               var2 = this.handleKeyword(this.pendingKeyword);
               if (!var2) {
                  this.warning("Unknown keyword: " + this.pendingKeyword);
               }

               this.pendingKeyword = null;
               this.state = 0;
               if (!Character.isWhitespace(var1)) {
                  this.write(var1);
               }
            } else {
               this.state = 3;
               this.currentCharacters.append(var1);
            }
         }
         break;
      case 3:
         if (Character.isDigit(var1)) {
            this.currentCharacters.append(var1);
         } else if (this.pendingKeyword.equals("bin")) {
            long var3 = Long.parseLong(this.currentCharacters.toString());
            this.pendingKeyword = null;
            this.state = 6;
            this.binaryBytesLeft = var3;
            if (this.binaryBytesLeft > 2147483647L) {
               this.binaryBuf = new ByteArrayOutputStream(Integer.MAX_VALUE);
            } else {
               this.binaryBuf = new ByteArrayOutputStream((int)this.binaryBytesLeft);
            }

            this.savedSpecials = this.specialsTable;
            this.specialsTable = allSpecialsTable;
         } else {
            int var5 = Integer.parseInt(this.currentCharacters.toString());
            var2 = this.handleKeyword(this.pendingKeyword, var5);
            if (!var2) {
               this.warning("Unknown keyword: " + this.pendingKeyword + " (param " + this.currentCharacters + ")");
            }

            this.pendingKeyword = null;
            this.currentCharacters = new StringBuffer();
            this.state = 0;
            if (!Character.isWhitespace(var1)) {
               this.write(var1);
            }
         }
         break;
      case 4:
         if (Character.digit((char)var1, 16) == -1) {
            this.state = 0;
         } else {
            this.pendingCharacter = Character.digit((char)var1, 16);
            this.state = 5;
         }
         break;
      case 5:
         this.state = 0;
         if (Character.digit((char)var1, 16) != -1) {
            this.pendingCharacter = this.pendingCharacter * 16 + Character.digit((char)var1, 16);
            var1 = this.translationTable[this.pendingCharacter];
            if (var1 != 0) {
               this.handleText(var1);
            }
         }
         break;
      case 6:
         this.binaryBuf.write(var1);
         --this.binaryBytesLeft;
         if (this.binaryBytesLeft == 0L) {
            this.state = 0;
            this.specialsTable = this.savedSpecials;
            this.savedSpecials = null;
            this.handleBinaryBlob(this.binaryBuf.toByteArray());
            this.binaryBuf = null;
         }
      }

   }

   public void flush() throws IOException {
      super.flush();
      if (this.state == 0 && this.currentCharacters.length() > 0) {
         this.handleText(this.currentCharacters.toString());
         this.currentCharacters = new StringBuffer();
      }

   }

   public void close() throws IOException {
      this.flush();
      if (this.state != 0 || this.level > 0) {
         this.warning("Truncated RTF file.");

         while(this.level > 0) {
            this.endgroup();
            --this.level;
         }
      }

      super.close();
   }

   static {
      rtfSpecialsTable = (boolean[])noSpecialsTable.clone();
      rtfSpecialsTable[10] = true;
      rtfSpecialsTable[13] = true;
      rtfSpecialsTable[123] = true;
      rtfSpecialsTable[125] = true;
      rtfSpecialsTable[92] = true;
   }
}
