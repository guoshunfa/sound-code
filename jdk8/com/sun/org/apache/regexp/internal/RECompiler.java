package com.sun.org.apache.regexp.internal;

import java.util.Hashtable;

public class RECompiler {
   char[] instruction = new char[128];
   int lenInstruction = 0;
   String pattern;
   int len;
   int idx;
   int parens;
   static final int NODE_NORMAL = 0;
   static final int NODE_NULLABLE = 1;
   static final int NODE_TOPLEVEL = 2;
   static final int ESC_MASK = 1048560;
   static final int ESC_BACKREF = 1048575;
   static final int ESC_COMPLEX = 1048574;
   static final int ESC_CLASS = 1048573;
   int maxBrackets = 10;
   static final int bracketUnbounded = -1;
   int brackets = 0;
   int[] bracketStart = null;
   int[] bracketEnd = null;
   int[] bracketMin = null;
   int[] bracketOpt = null;
   static Hashtable hashPOSIX = new Hashtable();

   void ensure(int n) {
      int curlen = this.instruction.length;
      if (this.lenInstruction + n >= curlen) {
         while(true) {
            if (this.lenInstruction + n < curlen) {
               char[] newInstruction = new char[curlen];
               System.arraycopy(this.instruction, 0, newInstruction, 0, this.lenInstruction);
               this.instruction = newInstruction;
               break;
            }

            curlen *= 2;
         }
      }

   }

   void emit(char c) {
      this.ensure(1);
      this.instruction[this.lenInstruction++] = c;
   }

   void nodeInsert(char opcode, int opdata, int insertAt) {
      this.ensure(3);
      System.arraycopy(this.instruction, insertAt, this.instruction, insertAt + 3, this.lenInstruction - insertAt);
      this.instruction[insertAt + 0] = opcode;
      this.instruction[insertAt + 1] = (char)opdata;
      this.instruction[insertAt + 2] = 0;
      this.lenInstruction += 3;
   }

   void setNextOfEnd(int node, int pointTo) {
      for(char next = this.instruction[node + 2]; next != 0 && node < this.lenInstruction; next = this.instruction[node + 2]) {
         if (node == pointTo) {
            pointTo = this.lenInstruction;
         }

         node += next;
      }

      if (node < this.lenInstruction) {
         this.instruction[node + 2] = (char)((short)(pointTo - node));
      }

   }

   int node(char opcode, int opdata) {
      this.ensure(3);
      this.instruction[this.lenInstruction + 0] = opcode;
      this.instruction[this.lenInstruction + 1] = (char)opdata;
      this.instruction[this.lenInstruction + 2] = 0;
      this.lenInstruction += 3;
      return this.lenInstruction - 3;
   }

   void internalError() throws Error {
      throw new Error("Internal error!");
   }

   void syntaxError(String s) throws RESyntaxException {
      throw new RESyntaxException(s);
   }

   void allocBrackets() {
      if (this.bracketStart == null) {
         this.bracketStart = new int[this.maxBrackets];
         this.bracketEnd = new int[this.maxBrackets];
         this.bracketMin = new int[this.maxBrackets];
         this.bracketOpt = new int[this.maxBrackets];

         for(int i = 0; i < this.maxBrackets; ++i) {
            this.bracketStart[i] = this.bracketEnd[i] = this.bracketMin[i] = this.bracketOpt[i] = -1;
         }
      }

   }

   synchronized void reallocBrackets() {
      if (this.bracketStart == null) {
         this.allocBrackets();
      }

      int new_size = this.maxBrackets * 2;
      int[] new_bS = new int[new_size];
      int[] new_bE = new int[new_size];
      int[] new_bM = new int[new_size];
      int[] new_bO = new int[new_size];

      for(int i = this.brackets; i < new_size; ++i) {
         new_bS[i] = new_bE[i] = new_bM[i] = new_bO[i] = -1;
      }

      System.arraycopy(this.bracketStart, 0, new_bS, 0, this.brackets);
      System.arraycopy(this.bracketEnd, 0, new_bE, 0, this.brackets);
      System.arraycopy(this.bracketMin, 0, new_bM, 0, this.brackets);
      System.arraycopy(this.bracketOpt, 0, new_bO, 0, this.brackets);
      this.bracketStart = new_bS;
      this.bracketEnd = new_bE;
      this.bracketMin = new_bM;
      this.bracketOpt = new_bO;
      this.maxBrackets = new_size;
   }

   void bracket() throws RESyntaxException {
      if (this.idx >= this.len || this.pattern.charAt(this.idx++) != '{') {
         this.internalError();
      }

      if (this.idx >= this.len || !Character.isDigit(this.pattern.charAt(this.idx))) {
         this.syntaxError("Expected digit");
      }

      StringBuffer number = new StringBuffer();

      while(this.idx < this.len && Character.isDigit(this.pattern.charAt(this.idx))) {
         number.append(this.pattern.charAt(this.idx++));
      }

      try {
         this.bracketMin[this.brackets] = Integer.parseInt(number.toString());
      } catch (NumberFormatException var4) {
         this.syntaxError("Expected valid number");
      }

      if (this.idx >= this.len) {
         this.syntaxError("Expected comma or right bracket");
      }

      if (this.pattern.charAt(this.idx) == '}') {
         ++this.idx;
         this.bracketOpt[this.brackets] = 0;
      } else {
         if (this.idx >= this.len || this.pattern.charAt(this.idx++) != ',') {
            this.syntaxError("Expected comma");
         }

         if (this.idx >= this.len) {
            this.syntaxError("Expected comma or right bracket");
         }

         if (this.pattern.charAt(this.idx) == '}') {
            ++this.idx;
            this.bracketOpt[this.brackets] = -1;
         } else {
            if (this.idx >= this.len || !Character.isDigit(this.pattern.charAt(this.idx))) {
               this.syntaxError("Expected digit");
            }

            number.setLength(0);

            while(this.idx < this.len && Character.isDigit(this.pattern.charAt(this.idx))) {
               number.append(this.pattern.charAt(this.idx++));
            }

            try {
               this.bracketOpt[this.brackets] = Integer.parseInt(number.toString()) - this.bracketMin[this.brackets];
            } catch (NumberFormatException var3) {
               this.syntaxError("Expected valid number");
            }

            if (this.bracketOpt[this.brackets] < 0) {
               this.syntaxError("Bad range");
            }

            if (this.idx >= this.len || this.pattern.charAt(this.idx++) != '}') {
               this.syntaxError("Missing close brace");
            }

         }
      }
   }

   int escape() throws RESyntaxException {
      if (this.pattern.charAt(this.idx) != '\\') {
         this.internalError();
      }

      if (this.idx + 1 == this.len) {
         this.syntaxError("Escape terminates string");
      }

      this.idx += 2;
      char escapeChar = this.pattern.charAt(this.idx - 1);
      int hexDigits;
      switch(escapeChar) {
      case '0':
      case '1':
      case '2':
      case '3':
      case '4':
      case '5':
      case '6':
      case '7':
      case '8':
      case '9':
         if ((this.idx >= this.len || !Character.isDigit(this.pattern.charAt(this.idx))) && escapeChar != '0') {
            return 1048575;
         } else {
            hexDigits = escapeChar - 48;
            if (this.idx < this.len && Character.isDigit(this.pattern.charAt(this.idx))) {
               hexDigits = (hexDigits << 3) + (this.pattern.charAt(this.idx++) - 48);
               if (this.idx < this.len && Character.isDigit(this.pattern.charAt(this.idx))) {
                  hexDigits = (hexDigits << 3) + (this.pattern.charAt(this.idx++) - 48);
               }
            }

            return hexDigits;
         }
      case ':':
      case ';':
      case '<':
      case '=':
      case '>':
      case '?':
      case '@':
      case 'A':
      case 'C':
      case 'E':
      case 'F':
      case 'G':
      case 'H':
      case 'I':
      case 'J':
      case 'K':
      case 'L':
      case 'M':
      case 'N':
      case 'O':
      case 'P':
      case 'Q':
      case 'R':
      case 'T':
      case 'U':
      case 'V':
      case 'X':
      case 'Y':
      case 'Z':
      case '[':
      case '\\':
      case ']':
      case '^':
      case '_':
      case '`':
      case 'a':
      case 'c':
      case 'e':
      case 'g':
      case 'h':
      case 'i':
      case 'j':
      case 'k':
      case 'l':
      case 'm':
      case 'o':
      case 'p':
      case 'q':
      case 'v':
      default:
         return escapeChar;
      case 'B':
      case 'b':
         return 1048574;
      case 'D':
      case 'S':
      case 'W':
      case 'd':
      case 's':
      case 'w':
         return 1048573;
      case 'f':
         return 12;
      case 'n':
         return 10;
      case 'r':
         return 13;
      case 't':
         return 9;
      case 'u':
      case 'x':
         hexDigits = escapeChar == 'u' ? 4 : 2;

         int val;
         for(val = 0; this.idx < this.len && hexDigits-- > 0; ++this.idx) {
            char c = this.pattern.charAt(this.idx);
            if (c >= '0' && c <= '9') {
               val = (val << 4) + c - 48;
            } else {
               c = Character.toLowerCase(c);
               if (c >= 'a' && c <= 'f') {
                  val = (val << 4) + (c - 97) + 10;
               } else {
                  this.syntaxError("Expected " + hexDigits + " hexadecimal digits after \\" + escapeChar);
               }
            }
         }

         return val;
      }
   }

   int characterClass() throws RESyntaxException {
      if (this.pattern.charAt(this.idx) != '[') {
         this.internalError();
      }

      if (this.idx + 1 >= this.len || this.pattern.charAt(++this.idx) == ']') {
         this.syntaxError("Empty or unterminated class");
      }

      int ret;
      if (this.idx < this.len && this.pattern.charAt(this.idx) == ':') {
         ++this.idx;

         for(ret = this.idx; this.idx < this.len && this.pattern.charAt(this.idx) >= 'a' && this.pattern.charAt(this.idx) <= 'z'; ++this.idx) {
         }

         if (this.idx + 1 < this.len && this.pattern.charAt(this.idx) == ':' && this.pattern.charAt(this.idx + 1) == ']') {
            String charClass = this.pattern.substring(ret, this.idx);
            Character i = (Character)hashPOSIX.get(charClass);
            if (i != null) {
               this.idx += 2;
               return this.node('P', i);
            }

            this.syntaxError("Invalid POSIX character class '" + charClass + "'");
         }

         this.syntaxError("Invalid POSIX character class syntax");
      }

      ret = this.node('[', 0);
      char CHAR_INVALID = '\uffff';
      char last = CHAR_INVALID;
      char simpleChar = false;
      boolean include = true;
      boolean definingRange = false;
      int idxFirst = this.idx;
      char rangeStart = 0;
      RECompiler.RERange range = new RECompiler.RERange();

      int i;
      while(this.idx < this.len && this.pattern.charAt(this.idx) != ']') {
         char simpleChar;
         label106:
         switch(this.pattern.charAt(this.idx)) {
         case '-':
            if (definingRange) {
               this.syntaxError("Bad class range");
            }

            definingRange = true;
            rangeStart = last == CHAR_INVALID ? 0 : last;
            if (this.idx + 1 >= this.len || this.pattern.charAt(++this.idx) != ']') {
               continue;
            }

            simpleChar = '\uffff';
            break;
         case '\\':
            switch(i = this.escape()) {
            case 1048574:
            case 1048575:
               this.syntaxError("Bad character class");
            case 1048573:
               if (definingRange) {
                  this.syntaxError("Bad character class");
               }

               switch(this.pattern.charAt(this.idx - 1)) {
               case 'D':
               case 'S':
               case 'W':
                  this.syntaxError("Bad character class");
               case 's':
                  range.include('\t', include);
                  range.include('\r', include);
                  range.include('\f', include);
                  range.include('\n', include);
                  range.include('\b', include);
                  range.include(' ', include);
                  break;
               case 'w':
                  range.include(97, 122, include);
                  range.include(65, 90, include);
                  range.include('_', include);
               case 'd':
                  range.include(48, 57, include);
               }

               last = CHAR_INVALID;
               continue;
            default:
               simpleChar = (char)i;
               break label106;
            }
         case '^':
            include = !include;
            if (this.idx == idxFirst) {
               range.include(0, 65535, true);
            }

            ++this.idx;
            continue;
         default:
            simpleChar = this.pattern.charAt(this.idx++);
         }

         if (definingRange) {
            if (rangeStart >= simpleChar) {
               this.syntaxError("Bad character class");
            }

            range.include(rangeStart, simpleChar, include);
            last = CHAR_INVALID;
            definingRange = false;
         } else {
            if (this.idx >= this.len || this.pattern.charAt(this.idx) != '-') {
               range.include(simpleChar, include);
            }

            last = simpleChar;
         }
      }

      if (this.idx == this.len) {
         this.syntaxError("Unterminated character class");
      }

      ++this.idx;
      this.instruction[ret + 1] = (char)range.num;

      for(i = 0; i < range.num; ++i) {
         this.emit((char)range.minRange[i]);
         this.emit((char)range.maxRange[i]);
      }

      return ret;
   }

   int atom() throws RESyntaxException {
      int ret = this.node('A', 0);
      int lenAtom = 0;

      label46:
      while(this.idx < this.len) {
         int c;
         if (this.idx + 1 < this.len) {
            char c = this.pattern.charAt(this.idx + 1);
            if (this.pattern.charAt(this.idx) == '\\') {
               c = this.idx;
               this.escape();
               if (this.idx < this.len) {
                  c = this.pattern.charAt(this.idx);
               }

               this.idx = c;
            }

            switch(c) {
            case '*':
            case '+':
            case '?':
            case '{':
               if (lenAtom != 0) {
                  break label46;
               }
            }
         }

         switch(this.pattern.charAt(this.idx)) {
         case '$':
         case '(':
         case ')':
         case '.':
         case '[':
         case ']':
         case '^':
         case '|':
            break label46;
         case '*':
         case '+':
         case '?':
         case '{':
            if (lenAtom == 0) {
               this.syntaxError("Missing operand to closure");
            }
            break label46;
         case '\\':
            int idxBeforeEscape = this.idx;
            c = this.escape();
            if ((c & 1048560) == 1048560) {
               this.idx = idxBeforeEscape;
               break label46;
            }

            this.emit((char)c);
            ++lenAtom;
            break;
         default:
            this.emit(this.pattern.charAt(this.idx++));
            ++lenAtom;
         }
      }

      if (lenAtom == 0) {
         this.internalError();
      }

      this.instruction[ret + 1] = (char)lenAtom;
      return ret;
   }

   int terminal(int[] flags) throws RESyntaxException {
      switch(this.pattern.charAt(this.idx)) {
      case '$':
      case '.':
      case '^':
         return this.node(this.pattern.charAt(this.idx++), 0);
      case '(':
         return this.expr(flags);
      case ')':
         this.syntaxError("Unexpected close paren");
      case '|':
         this.internalError();
      case ']':
         this.syntaxError("Mismatched class");
      case '\u0000':
         this.syntaxError("Unexpected end of input");
      case '*':
      case '+':
      case '?':
      case '{':
         this.syntaxError("Missing operand to closure");
      case '\\':
         int idxBeforeEscape = this.idx;
         switch(this.escape()) {
         case 1048573:
         case 1048574:
            flags[0] &= -2;
            return this.node('\\', this.pattern.charAt(this.idx - 1));
         case 1048575:
            char backreference = (char)(this.pattern.charAt(this.idx - 1) - 48);
            if (this.parens <= backreference) {
               this.syntaxError("Bad backreference");
            }

            flags[0] |= 1;
            return this.node('#', backreference);
         default:
            this.idx = idxBeforeEscape;
            flags[0] &= -2;
         }
      default:
         flags[0] &= -2;
         return this.atom();
      case '[':
         return this.characterClass();
      }
   }

   int closure(int[] flags) throws RESyntaxException {
      int idxBeforeTerminal = this.idx;
      int[] terminalFlags = new int[]{0};
      int ret = this.terminal(terminalFlags);
      flags[0] |= terminalFlags[0];
      if (this.idx >= this.len) {
         return ret;
      } else {
         boolean greedy = true;
         char closureType = this.pattern.charAt(this.idx);
         switch(closureType) {
         case '*':
         case '?':
            flags[0] |= 1;
         case '+':
            ++this.idx;
         case '{':
            int opcode = this.instruction[ret + 0];
            if (opcode == '^' || opcode == '$') {
               this.syntaxError("Bad closure operand");
            }

            if ((terminalFlags[0] & 1) != 0) {
               this.syntaxError("Closure operand can't be nullable");
            }
         default:
            if (this.idx < this.len && this.pattern.charAt(this.idx) == '?') {
               ++this.idx;
               greedy = false;
            }

            if (greedy) {
               int nothing;
               switch(closureType) {
               case '+':
                  nothing = this.node('|', 0);
                  this.setNextOfEnd(ret, nothing);
                  this.setNextOfEnd(this.node('G', 0), ret);
                  this.setNextOfEnd(nothing, this.node('|', 0));
                  this.setNextOfEnd(ret, this.node('N', 0));
                  break;
               case '{':
                  boolean found = false;
                  this.allocBrackets();

                  int i;
                  for(i = 0; i < this.brackets; ++i) {
                     if (this.bracketStart[i] == this.idx) {
                        found = true;
                        break;
                     }
                  }

                  if (!found) {
                     if (this.brackets >= this.maxBrackets) {
                        this.reallocBrackets();
                     }

                     this.bracketStart[this.brackets] = this.idx;
                     this.bracket();
                     this.bracketEnd[this.brackets] = this.idx;
                     i = this.brackets++;
                  }

                  if (this.bracketMin[i]-- > 0) {
                     if (this.bracketMin[i] <= 0 && this.bracketOpt[i] == 0) {
                        this.idx = this.bracketEnd[i];
                     } else {
                        for(int j = 0; j < this.brackets; ++j) {
                           if (j != i && this.bracketStart[j] < this.idx && this.bracketStart[j] >= idxBeforeTerminal) {
                              --this.brackets;
                              this.bracketStart[j] = this.bracketStart[this.brackets];
                              this.bracketEnd[j] = this.bracketEnd[this.brackets];
                              this.bracketMin[j] = this.bracketMin[this.brackets];
                              this.bracketOpt[j] = this.bracketOpt[this.brackets];
                           }
                        }

                        this.idx = idxBeforeTerminal;
                     }
                     break;
                  } else if (this.bracketOpt[i] == -1) {
                     closureType = '*';
                     this.bracketOpt[i] = 0;
                     this.idx = this.bracketEnd[i];
                  } else if (this.bracketOpt[i]-- <= 0) {
                     this.lenInstruction = ret;
                     this.node('N', 0);
                     this.idx = this.bracketEnd[i];
                     break;
                  } else {
                     if (this.bracketOpt[i] > 0) {
                        this.idx = idxBeforeTerminal;
                     } else {
                        this.idx = this.bracketEnd[i];
                     }

                     closureType = '?';
                  }
               case '*':
               case '?':
                  if (greedy) {
                     if (closureType == '?') {
                        this.nodeInsert('|', 0, ret);
                        this.setNextOfEnd(ret, this.node('|', 0));
                        nothing = this.node('N', 0);
                        this.setNextOfEnd(ret, nothing);
                        this.setNextOfEnd(ret + 3, nothing);
                     }

                     if (closureType == '*') {
                        this.nodeInsert('|', 0, ret);
                        this.setNextOfEnd(ret + 3, this.node('|', 0));
                        this.setNextOfEnd(ret + 3, this.node('G', 0));
                        this.setNextOfEnd(ret + 3, ret);
                        this.setNextOfEnd(ret, this.node('|', 0));
                        this.setNextOfEnd(ret, this.node('N', 0));
                     }
                  }
               }
            } else {
               this.setNextOfEnd(ret, this.node('E', 0));
               switch(closureType) {
               case '*':
                  this.nodeInsert('8', 0, ret);
                  break;
               case '+':
                  this.nodeInsert('=', 0, ret);
                  break;
               case '?':
                  this.nodeInsert('/', 0, ret);
               }

               this.setNextOfEnd(ret, this.lenInstruction);
            }

            return ret;
         }
      }
   }

   int branch(int[] flags) throws RESyntaxException {
      int ret = this.node('|', 0);
      int chain = -1;
      int[] closureFlags = new int[1];

      int node;
      boolean nullable;
      for(nullable = true; this.idx < this.len && this.pattern.charAt(this.idx) != '|' && this.pattern.charAt(this.idx) != ')'; chain = node) {
         closureFlags[0] = 0;
         node = this.closure(closureFlags);
         if (closureFlags[0] == 0) {
            nullable = false;
         }

         if (chain != -1) {
            this.setNextOfEnd(chain, node);
         }
      }

      if (chain == -1) {
         this.node('N', 0);
      }

      if (nullable) {
         flags[0] |= 1;
      }

      return ret;
   }

   int expr(int[] flags) throws RESyntaxException {
      int paren = -1;
      int ret = -1;
      int closeParens = this.parens;
      if ((flags[0] & 2) == 0 && this.pattern.charAt(this.idx) == '(') {
         if (this.idx + 2 < this.len && this.pattern.charAt(this.idx + 1) == '?' && this.pattern.charAt(this.idx + 2) == ':') {
            paren = 2;
            this.idx += 3;
            ret = this.node('<', 0);
         } else {
            paren = 1;
            ++this.idx;
            ret = this.node('(', this.parens++);
         }
      }

      flags[0] &= -3;
      int branch = this.branch(flags);
      if (ret == -1) {
         ret = branch;
      } else {
         this.setNextOfEnd(ret, branch);
      }

      while(this.idx < this.len && this.pattern.charAt(this.idx) == '|') {
         ++this.idx;
         branch = this.branch(flags);
         this.setNextOfEnd(ret, branch);
      }

      int end;
      if (paren > 0) {
         if (this.idx < this.len && this.pattern.charAt(this.idx) == ')') {
            ++this.idx;
         } else {
            this.syntaxError("Missing close paren");
         }

         if (paren == 1) {
            end = this.node(')', closeParens);
         } else {
            end = this.node('>', 0);
         }
      } else {
         end = this.node('E', 0);
      }

      this.setNextOfEnd(ret, end);
      int currentNode = ret;

      for(char nextNodeOffset = this.instruction[ret + 2]; nextNodeOffset != 0 && currentNode < this.lenInstruction; currentNode += nextNodeOffset) {
         if (this.instruction[currentNode + 0] == '|') {
            this.setNextOfEnd(currentNode + 3, end);
         }

         nextNodeOffset = this.instruction[currentNode + 2];
      }

      return ret;
   }

   public REProgram compile(String pattern) throws RESyntaxException {
      this.pattern = pattern;
      this.len = pattern.length();
      this.idx = 0;
      this.lenInstruction = 0;
      this.parens = 1;
      this.brackets = 0;
      int[] flags = new int[]{2};
      this.expr(flags);
      if (this.idx != this.len) {
         if (pattern.charAt(this.idx) == ')') {
            this.syntaxError("Unmatched close paren");
         }

         this.syntaxError("Unexpected input remains");
      }

      char[] ins = new char[this.lenInstruction];
      System.arraycopy(this.instruction, 0, ins, 0, this.lenInstruction);
      return new REProgram(this.parens, ins);
   }

   static {
      hashPOSIX.put("alnum", new Character('w'));
      hashPOSIX.put("alpha", new Character('a'));
      hashPOSIX.put("blank", new Character('b'));
      hashPOSIX.put("cntrl", new Character('c'));
      hashPOSIX.put("digit", new Character('d'));
      hashPOSIX.put("graph", new Character('g'));
      hashPOSIX.put("lower", new Character('l'));
      hashPOSIX.put("print", new Character('p'));
      hashPOSIX.put("punct", new Character('!'));
      hashPOSIX.put("space", new Character('s'));
      hashPOSIX.put("upper", new Character('u'));
      hashPOSIX.put("xdigit", new Character('x'));
      hashPOSIX.put("javastart", new Character('j'));
      hashPOSIX.put("javapart", new Character('k'));
   }

   class RERange {
      int size = 16;
      int[] minRange;
      int[] maxRange;
      int num;

      RERange() {
         this.minRange = new int[this.size];
         this.maxRange = new int[this.size];
         this.num = 0;
      }

      void delete(int index) {
         if (this.num != 0 && index < this.num) {
            while(true) {
               ++index;
               if (index >= this.num) {
                  --this.num;
                  return;
               }

               if (index - 1 >= 0) {
                  this.minRange[index - 1] = this.minRange[index];
                  this.maxRange[index - 1] = this.maxRange[index];
               }
            }
         }
      }

      void merge(int min, int max) {
         for(int i = 0; i < this.num; ++i) {
            if (min >= this.minRange[i] && max <= this.maxRange[i]) {
               return;
            }

            if (min <= this.minRange[i] && max >= this.maxRange[i]) {
               this.delete(i);
               this.merge(min, max);
               return;
            }

            if (min >= this.minRange[i] && min <= this.maxRange[i]) {
               this.delete(i);
               min = this.minRange[i];
               this.merge(min, max);
               return;
            }

            if (max >= this.minRange[i] && max <= this.maxRange[i]) {
               this.delete(i);
               max = this.maxRange[i];
               this.merge(min, max);
               return;
            }
         }

         if (this.num >= this.size) {
            this.size *= 2;
            int[] newMin = new int[this.size];
            int[] newMax = new int[this.size];
            System.arraycopy(this.minRange, 0, newMin, 0, this.num);
            System.arraycopy(this.maxRange, 0, newMax, 0, this.num);
            this.minRange = newMin;
            this.maxRange = newMax;
         }

         this.minRange[this.num] = min;
         this.maxRange[this.num] = max;
         ++this.num;
      }

      void remove(int min, int max) {
         for(int i = 0; i < this.num; ++i) {
            if (this.minRange[i] >= min && this.maxRange[i] <= max) {
               this.delete(i);
               --i;
               return;
            }

            if (min >= this.minRange[i] && max <= this.maxRange[i]) {
               int minr = this.minRange[i];
               int maxr = this.maxRange[i];
               this.delete(i);
               if (minr < min) {
                  this.merge(minr, min - 1);
               }

               if (max < maxr) {
                  this.merge(max + 1, maxr);
               }

               return;
            }

            if (this.minRange[i] >= min && this.minRange[i] <= max) {
               this.minRange[i] = max + 1;
               return;
            }

            if (this.maxRange[i] >= min && this.maxRange[i] <= max) {
               this.maxRange[i] = min - 1;
               return;
            }
         }

      }

      void include(int min, int max, boolean include) {
         if (include) {
            this.merge(min, max);
         } else {
            this.remove(min, max);
         }

      }

      void include(char minmax, boolean include) {
         this.include(minmax, minmax, include);
      }
   }
}
