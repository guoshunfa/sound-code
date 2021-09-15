package javax.swing.text.html;

import java.io.IOException;
import java.io.Reader;

class CSSParser {
   private static final int IDENTIFIER = 1;
   private static final int BRACKET_OPEN = 2;
   private static final int BRACKET_CLOSE = 3;
   private static final int BRACE_OPEN = 4;
   private static final int BRACE_CLOSE = 5;
   private static final int PAREN_OPEN = 6;
   private static final int PAREN_CLOSE = 7;
   private static final int END = -1;
   private static final char[] charMapping = new char[]{'\u0000', '\u0000', '[', ']', '{', '}', '(', ')', '\u0000'};
   private boolean didPushChar;
   private int pushedChar;
   private StringBuffer unitBuffer = new StringBuffer();
   private int[] unitStack = new int[2];
   private int stackCount;
   private Reader reader;
   private boolean encounteredRuleSet;
   private CSSParser.CSSParserCallback callback;
   private char[] tokenBuffer = new char[80];
   private int tokenBufferLength;
   private boolean readWS;

   void parse(Reader var1, CSSParser.CSSParserCallback var2, boolean var3) throws IOException {
      this.callback = var2;
      this.stackCount = this.tokenBufferLength = 0;
      this.reader = var1;
      this.encounteredRuleSet = false;

      try {
         if (var3) {
            this.parseDeclarationBlock();
         } else {
            while(true) {
               if (this.getNextStatement()) {
                  continue;
               }
            }
         }
      } finally {
         var2 = null;
         var1 = null;
      }

   }

   private boolean getNextStatement() throws IOException {
      this.unitBuffer.setLength(0);
      int var1 = this.nextToken('\u0000');
      switch(var1) {
      case -1:
         return false;
      case 0:
      default:
         return true;
      case 1:
         if (this.tokenBufferLength > 0) {
            if (this.tokenBuffer[0] == '@') {
               this.parseAtRule();
            } else {
               this.encounteredRuleSet = true;
               this.parseRuleSet();
            }
         }

         return true;
      case 2:
      case 4:
      case 6:
         this.parseTillClosed(var1);
         return true;
      case 3:
      case 5:
      case 7:
         throw new RuntimeException("Unexpected top level block close");
      }
   }

   private void parseAtRule() throws IOException {
      boolean var1 = false;
      boolean var2 = this.tokenBufferLength == 7 && this.tokenBuffer[0] == '@' && this.tokenBuffer[1] == 'i' && this.tokenBuffer[2] == 'm' && this.tokenBuffer[3] == 'p' && this.tokenBuffer[4] == 'o' && this.tokenBuffer[5] == 'r' && this.tokenBuffer[6] == 't';
      this.unitBuffer.setLength(0);

      while(!var1) {
         int var3 = this.nextToken(';');
         switch(var3) {
         case -1:
            var1 = true;
         case 0:
         default:
            break;
         case 1:
            if (this.tokenBufferLength > 0 && this.tokenBuffer[this.tokenBufferLength - 1] == ';') {
               --this.tokenBufferLength;
               var1 = true;
            }

            if (this.tokenBufferLength > 0) {
               if (this.unitBuffer.length() > 0 && this.readWS) {
                  this.unitBuffer.append(' ');
               }

               this.unitBuffer.append((char[])this.tokenBuffer, 0, this.tokenBufferLength);
            }
            break;
         case 2:
         case 6:
            this.unitBuffer.append(charMapping[var3]);
            this.parseTillClosed(var3);
            break;
         case 3:
         case 5:
         case 7:
            throw new RuntimeException("Unexpected close in @ rule");
         case 4:
            if (this.unitBuffer.length() > 0 && this.readWS) {
               this.unitBuffer.append(' ');
            }

            this.unitBuffer.append(charMapping[var3]);
            this.parseTillClosed(var3);
            var1 = true;
            int var4 = this.readWS();
            if (var4 != -1 && var4 != 59) {
               this.pushChar(var4);
            }
         }
      }

      if (var2 && !this.encounteredRuleSet) {
         this.callback.handleImport(this.unitBuffer.toString());
      }

   }

   private void parseRuleSet() throws IOException {
      if (this.parseSelectors()) {
         this.callback.startRule();
         this.parseDeclarationBlock();
         this.callback.endRule();
      }

   }

   private boolean parseSelectors() throws IOException {
      if (this.tokenBufferLength > 0) {
         this.callback.handleSelector(new String(this.tokenBuffer, 0, this.tokenBufferLength));
      }

      this.unitBuffer.setLength(0);

      while(true) {
         int var1;
         while((var1 = this.nextToken('\u0000')) != 1) {
            switch(var1) {
            case -1:
               return false;
            case 0:
            case 1:
            default:
               break;
            case 2:
            case 6:
               this.parseTillClosed(var1);
               this.unitBuffer.setLength(0);
               break;
            case 3:
            case 5:
            case 7:
               throw new RuntimeException("Unexpected block close in selector");
            case 4:
               return true;
            }
         }

         if (this.tokenBufferLength > 0) {
            this.callback.handleSelector(new String(this.tokenBuffer, 0, this.tokenBufferLength));
         }
      }
   }

   private void parseDeclarationBlock() throws IOException {
      while(true) {
         int var1 = this.parseDeclaration();
         switch(var1) {
         case -1:
         case 5:
            return;
         case 0:
         case 1:
         case 2:
         case 4:
         case 6:
         default:
            break;
         case 3:
         case 7:
            throw new RuntimeException("Unexpected close in declaration block");
         }
      }
   }

   private int parseDeclaration() throws IOException {
      int var1;
      if ((var1 = this.parseIdentifiers(':', false)) != 1) {
         return var1;
      } else {
         for(int var2 = this.unitBuffer.length() - 1; var2 >= 0; --var2) {
            this.unitBuffer.setCharAt(var2, Character.toLowerCase(this.unitBuffer.charAt(var2)));
         }

         this.callback.handleProperty(this.unitBuffer.toString());
         var1 = this.parseIdentifiers(';', true);
         this.callback.handleValue(this.unitBuffer.toString());
         return var1;
      }
   }

   private int parseIdentifiers(char var1, boolean var2) throws IOException {
      this.unitBuffer.setLength(0);

      while(true) {
         int var3 = this.nextToken(var1);
         switch(var3) {
         case -1:
         case 3:
         case 5:
         case 7:
            return var3;
         case 0:
         default:
            break;
         case 1:
            if (this.tokenBufferLength > 0) {
               if (this.tokenBuffer[this.tokenBufferLength - 1] == var1) {
                  if (--this.tokenBufferLength > 0) {
                     if (this.readWS && this.unitBuffer.length() > 0) {
                        this.unitBuffer.append(' ');
                     }

                     this.unitBuffer.append((char[])this.tokenBuffer, 0, this.tokenBufferLength);
                  }

                  return 1;
               }

               if (this.readWS && this.unitBuffer.length() > 0) {
                  this.unitBuffer.append(' ');
               }

               this.unitBuffer.append((char[])this.tokenBuffer, 0, this.tokenBufferLength);
            }
            break;
         case 2:
         case 4:
         case 6:
            int var4 = this.unitBuffer.length();
            if (var2) {
               this.unitBuffer.append(charMapping[var3]);
            }

            this.parseTillClosed(var3);
            if (!var2) {
               this.unitBuffer.setLength(var4);
            }
         }
      }
   }

   private void parseTillClosed(int var1) throws IOException {
      boolean var3 = false;
      this.startBlock(var1);

      while(!var3) {
         int var2 = this.nextToken('\u0000');
         switch(var2) {
         case -1:
            throw new RuntimeException("Unclosed block");
         case 0:
         default:
            break;
         case 1:
            if (this.unitBuffer.length() > 0 && this.readWS) {
               this.unitBuffer.append(' ');
            }

            if (this.tokenBufferLength > 0) {
               this.unitBuffer.append((char[])this.tokenBuffer, 0, this.tokenBufferLength);
            }
            break;
         case 2:
         case 4:
         case 6:
            if (this.unitBuffer.length() > 0 && this.readWS) {
               this.unitBuffer.append(' ');
            }

            this.unitBuffer.append(charMapping[var2]);
            this.startBlock(var2);
            break;
         case 3:
         case 5:
         case 7:
            if (this.unitBuffer.length() > 0 && this.readWS) {
               this.unitBuffer.append(' ');
            }

            this.unitBuffer.append(charMapping[var2]);
            this.endBlock(var2);
            if (!this.inBlock()) {
               var3 = true;
            }
         }
      }

   }

   private int nextToken(char var1) throws IOException {
      this.readWS = false;
      int var2 = this.readWS();
      switch(var2) {
      case -1:
         return -1;
      case 34:
         this.readTill('"');
         if (this.tokenBufferLength > 0) {
            --this.tokenBufferLength;
         }

         return 1;
      case 39:
         this.readTill('\'');
         if (this.tokenBufferLength > 0) {
            --this.tokenBufferLength;
         }

         return 1;
      case 40:
         return 6;
      case 41:
         return 7;
      case 91:
         return 2;
      case 93:
         return 3;
      case 123:
         return 4;
      case 125:
         return 5;
      default:
         this.pushChar(var2);
         this.getIdentifier(var1);
         return 1;
      }
   }

   private boolean getIdentifier(char var1) throws IOException {
      boolean var2 = false;
      boolean var3 = false;
      int var4 = 0;
      int var5 = 0;
      char var7 = var1;
      int var9 = 0;
      this.tokenBufferLength = 0;

      while(!var3) {
         int var6 = this.readChar();
         byte var8;
         switch(var6) {
         case -1:
            var3 = true;
            var8 = 0;
            break;
         case 0:
         case 1:
         case 2:
         case 3:
         case 4:
         case 5:
         case 6:
         case 7:
         case 8:
         case 11:
         case 12:
         case 14:
         case 15:
         case 16:
         case 17:
         case 18:
         case 19:
         case 20:
         case 21:
         case 22:
         case 23:
         case 24:
         case 25:
         case 26:
         case 27:
         case 28:
         case 29:
         case 30:
         case 31:
         case 33:
         case 35:
         case 36:
         case 37:
         case 38:
         case 42:
         case 43:
         case 44:
         case 45:
         case 46:
         case 58:
         case 59:
         case 60:
         case 61:
         case 62:
         case 63:
         case 64:
         case 71:
         case 72:
         case 73:
         case 74:
         case 75:
         case 76:
         case 77:
         case 78:
         case 79:
         case 80:
         case 81:
         case 82:
         case 83:
         case 84:
         case 85:
         case 86:
         case 87:
         case 88:
         case 89:
         case 90:
         case 94:
         case 95:
         case 96:
         case 103:
         case 104:
         case 105:
         case 106:
         case 107:
         case 108:
         case 109:
         case 110:
         case 111:
         case 112:
         case 113:
         case 114:
         case 115:
         case 116:
         case 117:
         case 118:
         case 119:
         case 120:
         case 121:
         case 122:
         case 124:
         default:
            var8 = 0;
            break;
         case 9:
         case 10:
         case 13:
         case 32:
         case 34:
         case 39:
         case 40:
         case 41:
         case 91:
         case 93:
         case 123:
         case 125:
            var8 = 3;
            break;
         case 47:
            var8 = 4;
            break;
         case 48:
         case 49:
         case 50:
         case 51:
         case 52:
         case 53:
         case 54:
         case 55:
         case 56:
         case 57:
            var8 = 2;
            var9 = var6 - 48;
            break;
         case 65:
         case 66:
         case 67:
         case 68:
         case 69:
         case 70:
            var8 = 2;
            var9 = var6 - 65 + 10;
            break;
         case 92:
            var8 = 1;
            break;
         case 97:
         case 98:
         case 99:
         case 100:
         case 101:
         case 102:
            var8 = 2;
            var9 = var6 - 97 + 10;
         }

         if (var2) {
            if (var8 == 2) {
               var5 = var5 * 16 + var9;
               ++var4;
               if (var4 == 4) {
                  var2 = false;
                  this.append((char)var5);
               }
            } else {
               var2 = false;
               if (var4 > 0) {
                  this.append((char)var5);
                  this.pushChar(var6);
               } else if (!var3) {
                  this.append((char)var6);
               }
            }
         } else if (!var3) {
            if (var8 == 1) {
               var2 = true;
               var4 = 0;
               var5 = 0;
            } else if (var8 == 3) {
               var3 = true;
               this.pushChar(var6);
            } else if (var8 == 4) {
               var6 = this.readChar();
               if (var6 == 42) {
                  var3 = true;
                  this.readComment();
                  this.readWS = true;
               } else {
                  this.append('/');
                  if (var6 == -1) {
                     var3 = true;
                  } else {
                     this.pushChar(var6);
                  }
               }
            } else {
               this.append((char)var6);
               if (var6 == var7) {
                  var3 = true;
               }
            }
         }
      }

      return this.tokenBufferLength > 0;
   }

   private void readTill(char var1) throws IOException {
      boolean var2 = false;
      int var3 = 0;
      int var4 = 0;
      boolean var6 = false;
      char var7 = var1;
      int var9 = 0;
      this.tokenBufferLength = 0;

      while(!var6) {
         int var5 = this.readChar();
         byte var8;
         switch(var5) {
         case -1:
            throw new RuntimeException("Unclosed " + var1);
         case 0:
         case 1:
         case 2:
         case 3:
         case 4:
         case 5:
         case 6:
         case 7:
         case 8:
         case 9:
         case 10:
         case 11:
         case 12:
         case 13:
         case 14:
         case 15:
         case 16:
         case 17:
         case 18:
         case 19:
         case 20:
         case 21:
         case 22:
         case 23:
         case 24:
         case 25:
         case 26:
         case 27:
         case 28:
         case 29:
         case 30:
         case 31:
         case 32:
         case 33:
         case 34:
         case 35:
         case 36:
         case 37:
         case 38:
         case 39:
         case 40:
         case 41:
         case 42:
         case 43:
         case 44:
         case 45:
         case 46:
         case 47:
         case 58:
         case 59:
         case 60:
         case 61:
         case 62:
         case 63:
         case 64:
         case 71:
         case 72:
         case 73:
         case 74:
         case 75:
         case 76:
         case 77:
         case 78:
         case 79:
         case 80:
         case 81:
         case 82:
         case 83:
         case 84:
         case 85:
         case 86:
         case 87:
         case 88:
         case 89:
         case 90:
         case 91:
         case 93:
         case 94:
         case 95:
         case 96:
         default:
            var8 = 0;
            break;
         case 48:
         case 49:
         case 50:
         case 51:
         case 52:
         case 53:
         case 54:
         case 55:
         case 56:
         case 57:
            var8 = 2;
            var9 = var5 - 48;
            break;
         case 65:
         case 66:
         case 67:
         case 68:
         case 69:
         case 70:
            var8 = 2;
            var9 = var5 - 65 + 10;
            break;
         case 92:
            var8 = 1;
            break;
         case 97:
         case 98:
         case 99:
         case 100:
         case 101:
         case 102:
            var8 = 2;
            var9 = var5 - 97 + 10;
         }

         if (var2) {
            if (var8 == 2) {
               var4 = var4 * 16 + var9;
               ++var3;
               if (var3 == 4) {
                  var2 = false;
                  this.append((char)var4);
               }
            } else if (var3 > 0) {
               this.append((char)var4);
               if (var8 == 1) {
                  var2 = true;
                  var3 = 0;
                  var4 = 0;
               } else {
                  if (var5 == var7) {
                     var6 = true;
                  }

                  this.append((char)var5);
                  var2 = false;
               }
            } else {
               this.append((char)var5);
               var2 = false;
            }
         } else if (var8 == 1) {
            var2 = true;
            var3 = 0;
            var4 = 0;
         } else {
            if (var5 == var7) {
               var6 = true;
            }

            this.append((char)var5);
         }
      }

   }

   private void append(char var1) {
      if (this.tokenBufferLength == this.tokenBuffer.length) {
         char[] var2 = new char[this.tokenBuffer.length * 2];
         System.arraycopy(this.tokenBuffer, 0, var2, 0, this.tokenBuffer.length);
         this.tokenBuffer = var2;
      }

      this.tokenBuffer[this.tokenBufferLength++] = var1;
   }

   private void readComment() throws IOException {
      while(true) {
         int var1 = this.readChar();
         switch(var1) {
         case -1:
            throw new RuntimeException("Unclosed comment");
         case 42:
            var1 = this.readChar();
            if (var1 == 47) {
               return;
            }

            if (var1 == -1) {
               throw new RuntimeException("Unclosed comment");
            }

            this.pushChar(var1);
         }
      }
   }

   private void startBlock(int var1) {
      if (this.stackCount == this.unitStack.length) {
         int[] var2 = new int[this.stackCount * 2];
         System.arraycopy(this.unitStack, 0, var2, 0, this.stackCount);
         this.unitStack = var2;
      }

      this.unitStack[this.stackCount++] = var1;
   }

   private void endBlock(int var1) {
      byte var2;
      switch(var1) {
      case 3:
         var2 = 2;
         break;
      case 4:
      case 6:
      default:
         var2 = -1;
         break;
      case 5:
         var2 = 4;
         break;
      case 7:
         var2 = 6;
      }

      if (this.stackCount > 0 && this.unitStack[this.stackCount - 1] == var2) {
         --this.stackCount;
      } else {
         throw new RuntimeException("Unmatched block");
      }
   }

   private boolean inBlock() {
      return this.stackCount > 0;
   }

   private int readWS() throws IOException {
      int var1;
      while((var1 = this.readChar()) != -1 && Character.isWhitespace((char)var1)) {
         this.readWS = true;
      }

      return var1;
   }

   private int readChar() throws IOException {
      if (this.didPushChar) {
         this.didPushChar = false;
         return this.pushedChar;
      } else {
         return this.reader.read();
      }
   }

   private void pushChar(int var1) {
      if (this.didPushChar) {
         throw new RuntimeException("Can not handle look ahead of more than one character");
      } else {
         this.didPushChar = true;
         this.pushedChar = var1;
      }
   }

   interface CSSParserCallback {
      void handleImport(String var1);

      void handleSelector(String var1);

      void startRule();

      void handleProperty(String var1);

      void handleValue(String var1);

      void endRule();
   }
}
