package com.sun.org.apache.regexp.internal;

import java.io.Serializable;
import java.util.Vector;

public class RE implements Serializable {
   public static final int MATCH_NORMAL = 0;
   public static final int MATCH_CASEINDEPENDENT = 1;
   public static final int MATCH_MULTILINE = 2;
   public static final int MATCH_SINGLELINE = 4;
   static final char OP_END = 'E';
   static final char OP_BOL = '^';
   static final char OP_EOL = '$';
   static final char OP_ANY = '.';
   static final char OP_ANYOF = '[';
   static final char OP_BRANCH = '|';
   static final char OP_ATOM = 'A';
   static final char OP_STAR = '*';
   static final char OP_PLUS = '+';
   static final char OP_MAYBE = '?';
   static final char OP_ESCAPE = '\\';
   static final char OP_OPEN = '(';
   static final char OP_OPEN_CLUSTER = '<';
   static final char OP_CLOSE = ')';
   static final char OP_CLOSE_CLUSTER = '>';
   static final char OP_BACKREF = '#';
   static final char OP_GOTO = 'G';
   static final char OP_NOTHING = 'N';
   static final char OP_RELUCTANTSTAR = '8';
   static final char OP_RELUCTANTPLUS = '=';
   static final char OP_RELUCTANTMAYBE = '/';
   static final char OP_POSIXCLASS = 'P';
   static final char E_ALNUM = 'w';
   static final char E_NALNUM = 'W';
   static final char E_BOUND = 'b';
   static final char E_NBOUND = 'B';
   static final char E_SPACE = 's';
   static final char E_NSPACE = 'S';
   static final char E_DIGIT = 'd';
   static final char E_NDIGIT = 'D';
   static final char POSIX_CLASS_ALNUM = 'w';
   static final char POSIX_CLASS_ALPHA = 'a';
   static final char POSIX_CLASS_BLANK = 'b';
   static final char POSIX_CLASS_CNTRL = 'c';
   static final char POSIX_CLASS_DIGIT = 'd';
   static final char POSIX_CLASS_GRAPH = 'g';
   static final char POSIX_CLASS_LOWER = 'l';
   static final char POSIX_CLASS_PRINT = 'p';
   static final char POSIX_CLASS_PUNCT = '!';
   static final char POSIX_CLASS_SPACE = 's';
   static final char POSIX_CLASS_UPPER = 'u';
   static final char POSIX_CLASS_XDIGIT = 'x';
   static final char POSIX_CLASS_JSTART = 'j';
   static final char POSIX_CLASS_JPART = 'k';
   static final int maxNode = 65536;
   static final int MAX_PAREN = 16;
   static final int offsetOpcode = 0;
   static final int offsetOpdata = 1;
   static final int offsetNext = 2;
   static final int nodeSize = 3;
   REProgram program;
   transient CharacterIterator search;
   int matchFlags;
   int maxParen;
   transient int parenCount;
   transient int start0;
   transient int end0;
   transient int start1;
   transient int end1;
   transient int start2;
   transient int end2;
   transient int[] startn;
   transient int[] endn;
   transient int[] startBackref;
   transient int[] endBackref;
   public static final int REPLACE_ALL = 0;
   public static final int REPLACE_FIRSTONLY = 1;
   public static final int REPLACE_BACKREFERENCES = 2;

   public RE(String pattern) throws RESyntaxException {
      this((String)pattern, 0);
   }

   public RE(String pattern, int matchFlags) throws RESyntaxException {
      this((new RECompiler()).compile(pattern));
      this.setMatchFlags(matchFlags);
   }

   public RE(REProgram program, int matchFlags) {
      this.maxParen = 16;
      this.setProgram(program);
      this.setMatchFlags(matchFlags);
   }

   public RE(REProgram program) {
      this((REProgram)program, 0);
   }

   public RE() {
      this((REProgram)((REProgram)null), 0);
   }

   public static String simplePatternToFullRegularExpression(String pattern) {
      StringBuffer buf = new StringBuffer();

      for(int i = 0; i < pattern.length(); ++i) {
         char c = pattern.charAt(i);
         switch(c) {
         case '$':
         case '(':
         case ')':
         case '+':
         case '.':
         case '?':
         case '[':
         case '\\':
         case ']':
         case '^':
         case '{':
         case '|':
         case '}':
            buf.append('\\');
         default:
            buf.append(c);
            break;
         case '*':
            buf.append(".*");
         }
      }

      return buf.toString();
   }

   public void setMatchFlags(int matchFlags) {
      this.matchFlags = matchFlags;
   }

   public int getMatchFlags() {
      return this.matchFlags;
   }

   public void setProgram(REProgram program) {
      this.program = program;
      if (program != null && program.maxParens != -1) {
         this.maxParen = program.maxParens;
      } else {
         this.maxParen = 16;
      }

   }

   public REProgram getProgram() {
      return this.program;
   }

   public int getParenCount() {
      return this.parenCount;
   }

   public String getParen(int which) {
      int start;
      return which < this.parenCount && (start = this.getParenStart(which)) >= 0 ? this.search.substring(start, this.getParenEnd(which)) : null;
   }

   public final int getParenStart(int which) {
      if (which < this.parenCount) {
         switch(which) {
         case 0:
            return this.start0;
         case 1:
            return this.start1;
         case 2:
            return this.start2;
         default:
            if (this.startn == null) {
               this.allocParens();
            }

            return this.startn[which];
         }
      } else {
         return -1;
      }
   }

   public final int getParenEnd(int which) {
      if (which < this.parenCount) {
         switch(which) {
         case 0:
            return this.end0;
         case 1:
            return this.end1;
         case 2:
            return this.end2;
         default:
            if (this.endn == null) {
               this.allocParens();
            }

            return this.endn[which];
         }
      } else {
         return -1;
      }
   }

   public final int getParenLength(int which) {
      return which < this.parenCount ? this.getParenEnd(which) - this.getParenStart(which) : -1;
   }

   protected final void setParenStart(int which, int i) {
      if (which < this.parenCount) {
         switch(which) {
         case 0:
            this.start0 = i;
            break;
         case 1:
            this.start1 = i;
            break;
         case 2:
            this.start2 = i;
            break;
         default:
            if (this.startn == null) {
               this.allocParens();
            }

            this.startn[which] = i;
         }
      }

   }

   protected final void setParenEnd(int which, int i) {
      if (which < this.parenCount) {
         switch(which) {
         case 0:
            this.end0 = i;
            break;
         case 1:
            this.end1 = i;
            break;
         case 2:
            this.end2 = i;
            break;
         default:
            if (this.endn == null) {
               this.allocParens();
            }

            this.endn[which] = i;
         }
      }

   }

   protected void internalError(String s) throws Error {
      throw new Error("RE internal error: " + s);
   }

   private final void allocParens() {
      this.startn = new int[this.maxParen];
      this.endn = new int[this.maxParen];

      for(int i = 0; i < this.maxParen; ++i) {
         this.startn[i] = -1;
         this.endn[i] = -1;
      }

   }

   protected int matchNodes(int firstNode, int lastNode, int idxStart) {
      int idx = idxStart;
      char[] instruction = this.program.instruction;
      int node = firstNode;

      while(node < lastNode) {
         int next;
         int opcode = instruction[node + 0];
         next = node + (short)instruction[node + 2];
         int opdata = instruction[node + 1];
         int idxNew;
         int l;
         int i;
         char c;
         int startAtom;
         int s;
         label380:
         switch(opcode) {
         case '#':
            s = this.startBackref[opdata];
            startAtom = this.endBackref[opdata];
            if (s != -1 && startAtom != -1) {
               if (s != startAtom) {
                  l = startAtom - s;
                  if (this.search.isEnd(idx + l - 1)) {
                     return -1;
                  }

                  boolean caseFold = (this.matchFlags & 1) != 0;

                  for(int i = 0; i < l; ++i) {
                     if (this.compareChars(this.search.charAt(idx++), this.search.charAt(s + i), caseFold) != 0) {
                        return -1;
                     }
                  }
               }
               break;
            }

            return -1;
         case '$':
            if (!this.search.isEnd(0) && !this.search.isEnd(idx)) {
               if ((this.matchFlags & 2) != 2) {
                  return -1;
               }

               if (!this.isNewline(idx)) {
                  return -1;
               }
            }
            break;
         case '(':
            if ((this.program.flags & 1) != 0) {
               this.startBackref[opdata] = idx;
            }

            if ((idxNew = this.matchNodes(next, 65536, idx)) != -1) {
               if (opdata + 1 > this.parenCount) {
                  this.parenCount = opdata + 1;
               }

               if (this.getParenStart(opdata) == -1) {
                  this.setParenStart(opdata, idx);
               }
            }

            return idxNew;
         case ')':
            if ((this.program.flags & 1) != 0) {
               this.endBackref[opdata] = idx;
            }

            if ((idxNew = this.matchNodes(next, 65536, idx)) != -1) {
               if (opdata + 1 > this.parenCount) {
                  this.parenCount = opdata + 1;
               }

               if (this.getParenEnd(opdata) == -1) {
                  this.setParenEnd(opdata, idx);
               }
            }

            return idxNew;
         case '.':
            if ((this.matchFlags & 4) == 4) {
               if (this.search.isEnd(idx)) {
                  return -1;
               }
            } else if (this.search.isEnd(idx) || this.isNewline(idx)) {
               return -1;
            }

            ++idx;
            break;
         case '/':
            s = 0;

            do {
               if ((idxNew = this.matchNodes(next, 65536, idx)) != -1) {
                  return idxNew;
               }
            } while(s++ == 0 && (idx = this.matchNodes(node + 3, next, idx)) != -1);

            return -1;
         case '8':
            while((idxNew = this.matchNodes(next, 65536, idx)) == -1) {
               if ((idx = this.matchNodes(node + 3, next, idx)) == -1) {
                  return -1;
               }
            }

            return idxNew;
         case '<':
         case '>':
            return this.matchNodes(next, 65536, idx);
         case '=':
            do {
               if ((idx = this.matchNodes(node + 3, next, idx)) == -1) {
                  return -1;
               }
            } while((idxNew = this.matchNodes(next, 65536, idx)) == -1);

            return idxNew;
         case 'A':
            if (this.search.isEnd(idx)) {
               return -1;
            }

            c = opdata;
            startAtom = node + 3;
            if (this.search.isEnd(opdata + idx - 1)) {
               return -1;
            }

            boolean caseFold = (this.matchFlags & 1) != 0;
            i = 0;

            while(true) {
               if (i >= c) {
                  break label380;
               }

               if (this.compareChars(this.search.charAt(idx++), instruction[startAtom + i], caseFold) != 0) {
                  return -1;
               }

               ++i;
            }
         case 'E':
            this.setParenEnd(0, idx);
            return idx;
         case 'G':
         case 'N':
            break;
         case 'P':
            if (this.search.isEnd(idx)) {
               return -1;
            }

            label323:
            switch(opdata) {
            case '!':
               s = Character.getType(this.search.charAt(idx));
               switch(s) {
               case 20:
               case 21:
               case 22:
               case 23:
               case 24:
                  break label323;
               default:
                  return -1;
               }
            case 'a':
               if (!Character.isLetter(this.search.charAt(idx))) {
                  return -1;
               }
               break;
            case 'b':
               if (!Character.isSpaceChar(this.search.charAt(idx))) {
                  return -1;
               }
               break;
            case 'c':
               if (Character.getType(this.search.charAt(idx)) != 15) {
                  return -1;
               }
               break;
            case 'd':
               if (!Character.isDigit(this.search.charAt(idx))) {
                  return -1;
               }
               break;
            case 'g':
               switch(Character.getType(this.search.charAt(idx))) {
               case 25:
               case 26:
               case 27:
               case 28:
                  break label323;
               default:
                  return -1;
               }
            case 'j':
               if (!Character.isJavaIdentifierStart(this.search.charAt(idx))) {
                  return -1;
               }
               break;
            case 'k':
               if (!Character.isJavaIdentifierPart(this.search.charAt(idx))) {
                  return -1;
               }
               break;
            case 'l':
               if (Character.getType(this.search.charAt(idx)) != 2) {
                  return -1;
               }
               break;
            case 'p':
               if (Character.getType(this.search.charAt(idx)) == 15) {
                  return -1;
               }
               break;
            case 's':
               if (!Character.isWhitespace(this.search.charAt(idx))) {
                  return -1;
               }
               break;
            case 'u':
               if (Character.getType(this.search.charAt(idx)) != 1) {
                  return -1;
               }
               break;
            case 'w':
               if (!Character.isLetterOrDigit(this.search.charAt(idx))) {
                  return -1;
               }
               break;
            case 'x':
               boolean isXDigit = this.search.charAt(idx) >= '0' && this.search.charAt(idx) <= '9' || this.search.charAt(idx) >= 'a' && this.search.charAt(idx) <= 'f' || this.search.charAt(idx) >= 'A' && this.search.charAt(idx) <= 'F';
               if (!isXDigit) {
                  return -1;
               }
               break;
            default:
               this.internalError("Bad posix class");
            }

            ++idx;
            break;
         case '[':
            if (this.search.isEnd(idx)) {
               return -1;
            }

            c = this.search.charAt(idx);
            boolean caseFold = (this.matchFlags & 1) != 0;
            l = node + 3;
            i = l + opdata * 2;
            boolean match = false;

            char s;
            char e;
            for(int i = l; !match && i < i; match = this.compareChars(c, s, caseFold) >= 0 && this.compareChars(c, e, caseFold) <= 0) {
               s = instruction[i++];
               e = instruction[i++];
            }

            if (!match) {
               return -1;
            }

            ++idx;
            break;
         case '\\':
            switch(opdata) {
            case 'B':
            case 'b':
               c = idx == 0 ? 10 : this.search.charAt(idx - 1);
               char cNext = this.search.isEnd(idx) ? 10 : this.search.charAt(idx);
               if (Character.isLetterOrDigit(c) == Character.isLetterOrDigit(cNext) == (opdata == 'b')) {
                  return -1;
               }
               break label380;
            case 'D':
            case 'S':
            case 'W':
            case 'd':
            case 's':
            case 'w':
               if (this.search.isEnd(idx)) {
                  return -1;
               }

               c = this.search.charAt(idx);
               switch(opdata) {
               case 'D':
               case 'd':
                  if (Character.isDigit(c) != (opdata == 'd')) {
                     return -1;
                  }
                  break;
               case 'S':
               case 's':
                  if (Character.isWhitespace(c) != (opdata == 's')) {
                     return -1;
                  }
                  break;
               case 'W':
               case 'w':
                  if ((Character.isLetterOrDigit(c) || c == '_') != (opdata == 'w')) {
                     return -1;
                  }
               }

               ++idx;
               break label380;
            default:
               this.internalError("Unrecognized escape '" + opdata + "'");
               break label380;
            }
         case '^':
            if (idx == 0) {
               break;
            }

            if ((this.matchFlags & 2) != 2) {
               return -1;
            }

            if (idx > 0 && this.isNewline(idx - 1)) {
               break;
            }

            return -1;
         case '|':
            if (instruction[next + 0] == '|') {
               short nextBranch;
               do {
                  if ((idxNew = this.matchNodes(node + 3, 65536, idx)) != -1) {
                     return idxNew;
                  }

                  nextBranch = (short)instruction[node + 2];
                  node += nextBranch;
               } while(nextBranch != 0 && instruction[node + 0] == '|');

               return -1;
            }

            node += 3;
            continue;
         default:
            this.internalError("Invalid opcode '" + opcode + "'");
         }

         node = next;
      }

      this.internalError("Corrupt program");
      return -1;
   }

   protected boolean matchAt(int i) {
      this.start0 = -1;
      this.end0 = -1;
      this.start1 = -1;
      this.end1 = -1;
      this.start2 = -1;
      this.end2 = -1;
      this.startn = null;
      this.endn = null;
      this.parenCount = 1;
      this.setParenStart(0, i);
      if ((this.program.flags & 1) != 0) {
         this.startBackref = new int[this.maxParen];
         this.endBackref = new int[this.maxParen];
      }

      int idx;
      if ((idx = this.matchNodes(0, 65536, i)) != -1) {
         this.setParenEnd(0, idx);
         return true;
      } else {
         this.parenCount = 0;
         return false;
      }
   }

   public boolean match(String search, int i) {
      return this.match((CharacterIterator)(new StringCharacterIterator(search)), i);
   }

   public boolean match(CharacterIterator search, int i) {
      if (this.program == null) {
         this.internalError("No RE program to run!");
      }

      this.search = search;
      if (this.program.prefix == null) {
         while(!search.isEnd(i - 1)) {
            if (this.matchAt(i)) {
               return true;
            }

            ++i;
         }

         return false;
      } else {
         boolean caseIndependent = (this.matchFlags & 1) != 0;

         for(char[] prefix = this.program.prefix; !search.isEnd(i + prefix.length - 1); ++i) {
            int j = i;
            int k = 0;

            boolean match;
            do {
               match = this.compareChars(search.charAt(j++), prefix[k++], caseIndependent) == 0;
            } while(match && k < prefix.length);

            if (k == prefix.length && this.matchAt(i)) {
               return true;
            }
         }

         return false;
      }
   }

   public boolean match(String search) {
      return this.match((String)search, 0);
   }

   public String[] split(String s) {
      Vector v = new Vector();
      int pos = 0;

      int newpos;
      for(int len = s.length(); pos < len && this.match(s, pos); pos = newpos) {
         int start = this.getParenStart(0);
         newpos = this.getParenEnd(0);
         if (newpos == pos) {
            v.addElement(s.substring(pos, start + 1));
            ++newpos;
         } else {
            v.addElement(s.substring(pos, start));
         }
      }

      String remainder = s.substring(pos);
      if (remainder.length() != 0) {
         v.addElement(remainder);
      }

      String[] ret = new String[v.size()];
      v.copyInto(ret);
      return ret;
   }

   public String subst(String substituteIn, String substitution) {
      return this.subst(substituteIn, substitution, 0);
   }

   public String subst(String substituteIn, String substitution, int flags) {
      StringBuffer ret = new StringBuffer();
      int pos = 0;
      int len = substituteIn.length();

      while(pos < len && this.match(substituteIn, pos)) {
         ret.append(substituteIn.substring(pos, this.getParenStart(0)));
         int lCurrentPosition;
         if ((flags & 2) != 0) {
            lCurrentPosition = 0;
            int lLastPosition = -2;
            int lLength = substitution.length();

            for(boolean bAddedPrefix = false; (lCurrentPosition = substitution.indexOf("$", lCurrentPosition)) >= 0; ++lCurrentPosition) {
               if ((lCurrentPosition == 0 || substitution.charAt(lCurrentPosition - 1) != '\\') && lCurrentPosition + 1 < lLength) {
                  char c = substitution.charAt(lCurrentPosition + 1);
                  if (c >= '0' && c <= '9') {
                     if (!bAddedPrefix) {
                        ret.append(substitution.substring(0, lCurrentPosition));
                        bAddedPrefix = true;
                     } else {
                        ret.append(substitution.substring(lLastPosition + 2, lCurrentPosition));
                     }

                     ret.append(this.getParen(c - 48));
                     lLastPosition = lCurrentPosition;
                  }
               }
            }

            ret.append(substitution.substring(lLastPosition + 2, lLength));
         } else {
            ret.append(substitution);
         }

         lCurrentPosition = this.getParenEnd(0);
         if (lCurrentPosition == pos) {
            ++lCurrentPosition;
         }

         pos = lCurrentPosition;
         if ((flags & 1) != 0) {
            break;
         }
      }

      if (pos < len) {
         ret.append(substituteIn.substring(pos));
      }

      return ret.toString();
   }

   public String[] grep(Object[] search) {
      Vector v = new Vector();

      for(int i = 0; i < search.length; ++i) {
         String s = search[i].toString();
         if (this.match(s)) {
            v.addElement(s);
         }
      }

      String[] ret = new String[v.size()];
      v.copyInto(ret);
      return ret;
   }

   private boolean isNewline(int i) {
      char nextChar = this.search.charAt(i);
      return nextChar == '\n' || nextChar == '\r' || nextChar == 133 || nextChar == 8232 || nextChar == 8233;
   }

   private int compareChars(char c1, char c2, boolean caseIndependent) {
      if (caseIndependent) {
         c1 = Character.toLowerCase(c1);
         c2 = Character.toLowerCase(c2);
      }

      return c1 - c2;
   }
}
