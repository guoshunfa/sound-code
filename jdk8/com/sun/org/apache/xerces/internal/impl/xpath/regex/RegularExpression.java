package com.sun.org.apache.xerces.internal.impl.xpath.regex;

import com.sun.org.apache.xerces.internal.util.IntStack;
import java.io.Serializable;
import java.text.CharacterIterator;
import java.util.Locale;
import java.util.Stack;

public class RegularExpression implements Serializable {
   private static final long serialVersionUID = 6242499334195006401L;
   static final boolean DEBUG = false;
   String regex;
   int options;
   int nofparen;
   Token tokentree;
   boolean hasBackReferences;
   transient int minlength;
   transient Op operations;
   transient int numberOfClosures;
   transient RegularExpression.Context context;
   transient RangeToken firstChar;
   transient String fixedString;
   transient int fixedStringOptions;
   transient BMPattern fixedStringTable;
   transient boolean fixedStringOnly;
   static final int IGNORE_CASE = 2;
   static final int SINGLE_LINE = 4;
   static final int MULTIPLE_LINES = 8;
   static final int EXTENDED_COMMENT = 16;
   static final int USE_UNICODE_CATEGORY = 32;
   static final int UNICODE_WORD_BOUNDARY = 64;
   static final int PROHIBIT_HEAD_CHARACTER_OPTIMIZATION = 128;
   static final int PROHIBIT_FIXED_STRING_OPTIMIZATION = 256;
   static final int XMLSCHEMA_MODE = 512;
   static final int SPECIAL_COMMA = 1024;
   private static final int WT_IGNORE = 0;
   private static final int WT_LETTER = 1;
   private static final int WT_OTHER = 2;
   static final int LINE_FEED = 10;
   static final int CARRIAGE_RETURN = 13;
   static final int LINE_SEPARATOR = 8232;
   static final int PARAGRAPH_SEPARATOR = 8233;

   private synchronized void compile(Token tok) {
      if (this.operations == null) {
         this.numberOfClosures = 0;
         this.operations = this.compile(tok, (Op)null, false);
      }
   }

   private Op compile(Token tok, Op next, boolean reverse) {
      Object ret;
      switch(tok.type) {
      case 0:
         ret = Op.createChar(tok.getChar());
         ((Op)ret).next = next;
         break;
      case 1:
         ret = next;
         int i;
         if (!reverse) {
            for(i = tok.size() - 1; i >= 0; --i) {
               ret = this.compile(tok.getChild(i), (Op)ret, false);
            }

            return (Op)ret;
         } else {
            for(i = 0; i < tok.size(); ++i) {
               ret = this.compile(tok.getChild(i), (Op)ret, true);
            }

            return (Op)ret;
         }
      case 2:
         Op.UnionOp uni = Op.createUnion(tok.size());

         for(int i = 0; i < tok.size(); ++i) {
            uni.addElement(this.compile(tok.getChild(i), next, reverse));
         }

         ret = uni;
         break;
      case 3:
      case 9:
         Token child = tok.getChild(0);
         int min = tok.getMin();
         int max = tok.getMax();
         int i;
         if (min >= 0 && min == max) {
            ret = next;

            for(i = 0; i < min; ++i) {
               ret = this.compile(child, (Op)ret, reverse);
            }

            return (Op)ret;
         } else {
            if (min > 0 && max > 0) {
               max -= min;
            }

            if (max > 0) {
               ret = next;

               for(i = 0; i < max; ++i) {
                  Op.ChildOp q = Op.createQuestion(tok.type == 9);
                  q.next = next;
                  q.setChild(this.compile(child, (Op)ret, reverse));
                  ret = q;
               }
            } else {
               Op.ChildOp op;
               if (tok.type == 9) {
                  op = Op.createNonGreedyClosure();
               } else {
                  op = Op.createClosure(this.numberOfClosures++);
               }

               op.next = next;
               op.setChild(this.compile(child, op, reverse));
               ret = op;
            }

            if (min > 0) {
               for(i = 0; i < min; ++i) {
                  ret = this.compile(child, (Op)ret, reverse);
               }
            }
            break;
         }
      case 4:
      case 5:
         ret = Op.createRange(tok);
         ((Op)ret).next = next;
         break;
      case 6:
         if (tok.getParenNumber() == 0) {
            ret = this.compile(tok.getChild(0), next, reverse);
         } else {
            Op.CharOp next;
            if (reverse) {
               next = Op.createCapture(tok.getParenNumber(), next);
               next = this.compile(tok.getChild(0), next, reverse);
               ret = Op.createCapture(-tok.getParenNumber(), next);
            } else {
               next = Op.createCapture(-tok.getParenNumber(), next);
               next = this.compile(tok.getChild(0), next, reverse);
               ret = Op.createCapture(tok.getParenNumber(), next);
            }
         }
         break;
      case 7:
         ret = next;
         break;
      case 8:
         ret = Op.createAnchor(tok.getChar());
         ((Op)ret).next = next;
         break;
      case 10:
         ret = Op.createString(tok.getString());
         ((Op)ret).next = next;
         break;
      case 11:
         ret = Op.createDot();
         ((Op)ret).next = next;
         break;
      case 12:
         ret = Op.createBackReference(tok.getReferenceNumber());
         ((Op)ret).next = next;
         break;
      case 13:
      case 14:
      case 15:
      case 16:
      case 17:
      case 18:
      case 19:
      default:
         throw new RuntimeException("Unknown token type: " + tok.type);
      case 20:
         ret = Op.createLook(20, next, this.compile(tok.getChild(0), (Op)null, false));
         break;
      case 21:
         ret = Op.createLook(21, next, this.compile(tok.getChild(0), (Op)null, false));
         break;
      case 22:
         ret = Op.createLook(22, next, this.compile(tok.getChild(0), (Op)null, true));
         break;
      case 23:
         ret = Op.createLook(23, next, this.compile(tok.getChild(0), (Op)null, true));
         break;
      case 24:
         ret = Op.createIndependent(next, this.compile(tok.getChild(0), (Op)null, reverse));
         break;
      case 25:
         ret = Op.createModifier(next, this.compile(tok.getChild(0), (Op)null, reverse), ((Token.ModifierToken)tok).getOptions(), ((Token.ModifierToken)tok).getOptionsMask());
         break;
      case 26:
         Token.ConditionToken ctok = (Token.ConditionToken)tok;
         int ref = ctok.refNumber;
         Op condition = ctok.condition == null ? null : this.compile(ctok.condition, (Op)null, reverse);
         Op yes = this.compile(ctok.yes, next, reverse);
         Op no = ctok.no == null ? null : this.compile(ctok.no, next, reverse);
         ret = Op.createCondition(next, ref, condition, yes, no);
      }

      return (Op)ret;
   }

   public boolean matches(char[] target) {
      return this.matches((char[])target, 0, target.length, (Match)null);
   }

   public boolean matches(char[] target, int start, int end) {
      return this.matches(target, start, end, (Match)null);
   }

   public boolean matches(char[] target, Match match) {
      return this.matches((char[])target, 0, target.length, match);
   }

   public boolean matches(char[] target, int start, int end, Match match) {
      synchronized(this) {
         if (this.operations == null) {
            this.prepare();
         }

         if (this.context == null) {
            this.context = new RegularExpression.Context();
         }
      }

      RegularExpression.Context con = null;
      synchronized(this.context) {
         con = this.context.inuse ? new RegularExpression.Context() : this.context;
         con.reset(target, start, end, this.numberOfClosures);
      }

      if (match != null) {
         match.setNumberOfGroups(this.nofparen);
         match.setSource(target);
      } else if (this.hasBackReferences) {
         match = new Match();
         match.setNumberOfGroups(this.nofparen);
      }

      con.match = match;
      int limit;
      if (isSet(this.options, 512)) {
         limit = this.match(con, this.operations, con.start, 1, this.options);
         if (limit == con.limit) {
            if (con.match != null) {
               con.match.setBeginning(0, con.start);
               con.match.setEnd(0, limit);
            }

            con.setInUse(false);
            return true;
         } else {
            return false;
         }
      } else if (this.fixedStringOnly) {
         limit = this.fixedStringTable.matches(target, con.start, con.limit);
         if (limit >= 0) {
            if (con.match != null) {
               con.match.setBeginning(0, limit);
               con.match.setEnd(0, limit + this.fixedString.length());
            }

            con.setInUse(false);
            return true;
         } else {
            con.setInUse(false);
            return false;
         }
      } else {
         if (this.fixedString != null) {
            limit = this.fixedStringTable.matches(target, con.start, con.limit);
            if (limit < 0) {
               con.setInUse(false);
               return false;
            }
         }

         limit = con.limit - this.minlength;
         int matchEnd = -1;
         int matchStart;
         if (this.operations != null && this.operations.type == 7 && this.operations.getChild().type == 0) {
            if (isSet(this.options, 4)) {
               matchStart = con.start;
               matchEnd = this.match(con, this.operations, con.start, 1, this.options);
            } else {
               boolean previousIsEOL = true;

               for(matchStart = con.start; matchStart <= limit; ++matchStart) {
                  int ch = target[matchStart];
                  if (isEOLChar(ch)) {
                     previousIsEOL = true;
                  } else {
                     if (previousIsEOL && 0 <= (matchEnd = this.match(con, this.operations, matchStart, 1, this.options))) {
                        break;
                     }

                     previousIsEOL = false;
                  }
               }
            }
         } else if (this.firstChar != null) {
            RangeToken range = this.firstChar;

            for(matchStart = con.start; matchStart <= limit; ++matchStart) {
               int ch = target[matchStart];
               if (REUtil.isHighSurrogate(ch) && matchStart + 1 < con.limit) {
                  ch = REUtil.composeFromSurrogates(ch, target[matchStart + 1]);
               }

               if (range.match(ch) && 0 <= (matchEnd = this.match(con, this.operations, matchStart, 1, this.options))) {
                  break;
               }
            }
         } else {
            for(matchStart = con.start; matchStart <= limit && 0 > (matchEnd = this.match(con, this.operations, matchStart, 1, this.options)); ++matchStart) {
            }
         }

         if (matchEnd >= 0) {
            if (con.match != null) {
               con.match.setBeginning(0, matchStart);
               con.match.setEnd(0, matchEnd);
            }

            con.setInUse(false);
            return true;
         } else {
            con.setInUse(false);
            return false;
         }
      }
   }

   public boolean matches(String target) {
      return this.matches((String)target, 0, target.length(), (Match)null);
   }

   public boolean matches(String target, int start, int end) {
      return this.matches(target, start, end, (Match)null);
   }

   public boolean matches(String target, Match match) {
      return this.matches((String)target, 0, target.length(), match);
   }

   public boolean matches(String target, int start, int end, Match match) {
      synchronized(this) {
         if (this.operations == null) {
            this.prepare();
         }

         if (this.context == null) {
            this.context = new RegularExpression.Context();
         }
      }

      RegularExpression.Context con = null;
      synchronized(this.context) {
         con = this.context.inuse ? new RegularExpression.Context() : this.context;
         con.reset(target, start, end, this.numberOfClosures);
      }

      if (match != null) {
         match.setNumberOfGroups(this.nofparen);
         match.setSource(target);
      } else if (this.hasBackReferences) {
         match = new Match();
         match.setNumberOfGroups(this.nofparen);
      }

      con.match = match;
      int limit;
      if (isSet(this.options, 512)) {
         limit = this.match(con, this.operations, con.start, 1, this.options);
         if (limit == con.limit) {
            if (con.match != null) {
               con.match.setBeginning(0, con.start);
               con.match.setEnd(0, limit);
            }

            con.setInUse(false);
            return true;
         } else {
            return false;
         }
      } else if (this.fixedStringOnly) {
         limit = this.fixedStringTable.matches(target, con.start, con.limit);
         if (limit >= 0) {
            if (con.match != null) {
               con.match.setBeginning(0, limit);
               con.match.setEnd(0, limit + this.fixedString.length());
            }

            con.setInUse(false);
            return true;
         } else {
            con.setInUse(false);
            return false;
         }
      } else {
         if (this.fixedString != null) {
            limit = this.fixedStringTable.matches(target, con.start, con.limit);
            if (limit < 0) {
               con.setInUse(false);
               return false;
            }
         }

         limit = con.limit - this.minlength;
         int matchEnd = -1;
         int matchStart;
         if (this.operations != null && this.operations.type == 7 && this.operations.getChild().type == 0) {
            if (isSet(this.options, 4)) {
               matchStart = con.start;
               matchEnd = this.match(con, this.operations, con.start, 1, this.options);
            } else {
               boolean previousIsEOL = true;

               for(matchStart = con.start; matchStart <= limit; ++matchStart) {
                  int ch = target.charAt(matchStart);
                  if (isEOLChar(ch)) {
                     previousIsEOL = true;
                  } else {
                     if (previousIsEOL && 0 <= (matchEnd = this.match(con, this.operations, matchStart, 1, this.options))) {
                        break;
                     }

                     previousIsEOL = false;
                  }
               }
            }
         } else if (this.firstChar != null) {
            RangeToken range = this.firstChar;

            for(matchStart = con.start; matchStart <= limit; ++matchStart) {
               int ch = target.charAt(matchStart);
               if (REUtil.isHighSurrogate(ch) && matchStart + 1 < con.limit) {
                  ch = REUtil.composeFromSurrogates(ch, target.charAt(matchStart + 1));
               }

               if (range.match(ch) && 0 <= (matchEnd = this.match(con, this.operations, matchStart, 1, this.options))) {
                  break;
               }
            }
         } else {
            for(matchStart = con.start; matchStart <= limit && 0 > (matchEnd = this.match(con, this.operations, matchStart, 1, this.options)); ++matchStart) {
            }
         }

         if (matchEnd >= 0) {
            if (con.match != null) {
               con.match.setBeginning(0, matchStart);
               con.match.setEnd(0, matchEnd);
            }

            con.setInUse(false);
            return true;
         } else {
            con.setInUse(false);
            return false;
         }
      }
   }

   private int match(RegularExpression.Context con, Op op, int offset, int dx, int opts) {
      RegularExpression.ExpressionTarget target = con.target;
      Stack opStack = new Stack();
      IntStack dataStack = new IntStack();
      boolean isSetIgnoreCase = isSet(opts, 2);
      int retValue = true;
      boolean returned = false;

      while(true) {
         int saved;
         int retValue;
         int refno;
         if (op != null && offset <= con.limit && offset >= con.start) {
            retValue = -1;
            switch(op.type) {
            case 0:
               refno = dx > 0 ? offset : offset - 1;
               if (refno < con.limit && refno >= 0) {
                  if (isSet(opts, 4)) {
                     if (REUtil.isHighSurrogate(target.charAt(refno)) && refno + dx >= 0 && refno + dx < con.limit) {
                        refno += dx;
                     }
                  } else {
                     saved = target.charAt(refno);
                     if (REUtil.isHighSurrogate(saved) && refno + dx >= 0 && refno + dx < con.limit) {
                        refno += dx;
                        saved = REUtil.composeFromSurrogates(saved, target.charAt(refno));
                     }

                     if (isEOLChar(saved)) {
                        returned = true;
                        break;
                     }
                  }

                  offset = dx > 0 ? refno + 1 : refno;
                  op = op.next;
               } else {
                  returned = true;
               }
               break;
            case 1:
               refno = dx > 0 ? offset : offset - 1;
               if (refno < con.limit && refno >= 0 && this.matchChar(op.getData(), target.charAt(refno), isSetIgnoreCase)) {
                  offset += dx;
                  op = op.next;
               } else {
                  returned = true;
               }
               break;
            case 2:
            case 12:
            case 13:
            case 14:
            case 17:
            case 18:
            case 19:
            default:
               throw new RuntimeException("Unknown operation type: " + op.type);
            case 3:
            case 4:
               refno = dx > 0 ? offset : offset - 1;
               if (refno < con.limit && refno >= 0) {
                  saved = target.charAt(offset);
                  if (REUtil.isHighSurrogate(saved) && refno + dx < con.limit && refno + dx >= 0) {
                     refno += dx;
                     saved = REUtil.composeFromSurrogates(saved, target.charAt(refno));
                  }

                  RangeToken tok = op.getToken();
                  if (!tok.match(saved)) {
                     returned = true;
                  } else {
                     offset = dx > 0 ? refno + 1 : refno;
                     op = op.next;
                  }
               } else {
                  returned = true;
               }
               break;
            case 5:
               if (!this.matchAnchor(target, op, con, offset, opts)) {
                  returned = true;
               } else {
                  op = op.next;
               }
               break;
            case 6:
               String literal = op.getString();
               saved = literal.length();
               if (dx > 0) {
                  if (!target.regionMatches(isSetIgnoreCase, offset, con.limit, literal, saved)) {
                     returned = true;
                     break;
                  }

                  offset += saved;
               } else {
                  if (!target.regionMatches(isSetIgnoreCase, offset - saved, con.limit, literal, saved)) {
                     returned = true;
                     break;
                  }

                  offset -= saved;
               }

               op = op.next;
               break;
            case 7:
               refno = op.getData();
               if (con.closureContexts[refno].contains(offset)) {
                  returned = true;
                  break;
               } else {
                  con.closureContexts[refno].addOffset(offset);
               }
            case 9:
               opStack.push(op);
               dataStack.push(offset);
               op = op.getChild();
               break;
            case 8:
            case 10:
               opStack.push(op);
               dataStack.push(offset);
               op = op.next;
               break;
            case 11:
               if (op.size() == 0) {
                  returned = true;
               } else {
                  opStack.push(op);
                  dataStack.push(0);
                  dataStack.push(offset);
                  op = op.elementAt(0);
               }
               break;
            case 15:
               refno = op.getData();
               if (con.match != null) {
                  if (refno > 0) {
                     dataStack.push(con.match.getBeginning(refno));
                     con.match.setBeginning(refno, offset);
                  } else {
                     saved = -refno;
                     dataStack.push(con.match.getEnd(saved));
                     con.match.setEnd(saved, offset);
                  }

                  opStack.push(op);
                  dataStack.push(offset);
               }

               op = op.next;
               break;
            case 16:
               refno = op.getData();
               if (refno <= 0 || refno >= this.nofparen) {
                  throw new RuntimeException("Internal Error: Reference number must be more than zero: " + refno);
               }

               if (con.match.getBeginning(refno) >= 0 && con.match.getEnd(refno) >= 0) {
                  saved = con.match.getBeginning(refno);
                  int literallen = con.match.getEnd(refno) - saved;
                  if (dx > 0) {
                     if (!target.regionMatches(isSetIgnoreCase, offset, con.limit, saved, literallen)) {
                        returned = true;
                        break;
                     }

                     offset += literallen;
                  } else {
                     if (!target.regionMatches(isSetIgnoreCase, offset - literallen, con.limit, saved, literallen)) {
                        returned = true;
                        break;
                     }

                     offset -= literallen;
                  }

                  op = op.next;
               } else {
                  returned = true;
               }
               break;
            case 20:
            case 21:
            case 22:
            case 23:
               opStack.push(op);
               dataStack.push(dx);
               dataStack.push(offset);
               dx = op.type != 20 && op.type != 21 ? -1 : 1;
               op = op.getChild();
               break;
            case 24:
               opStack.push(op);
               dataStack.push(offset);
               op = op.getChild();
               break;
            case 25:
               refno = opts | op.getData();
               refno &= ~op.getData2();
               opStack.push(op);
               dataStack.push(opts);
               dataStack.push(offset);
               opts = refno;
               op = op.getChild();
               break;
            case 26:
               Op.ConditionOp cop = (Op.ConditionOp)op;
               if (cop.refNumber > 0) {
                  if (cop.refNumber >= this.nofparen) {
                     throw new RuntimeException("Internal Error: Reference number must be more than zero: " + cop.refNumber);
                  }

                  if (con.match.getBeginning(cop.refNumber) >= 0 && con.match.getEnd(cop.refNumber) >= 0) {
                     op = cop.yes;
                  } else if (cop.no != null) {
                     op = cop.no;
                  } else {
                     op = cop.next;
                  }
               } else {
                  opStack.push(op);
                  dataStack.push(offset);
                  op = cop.condition;
               }
            }
         } else {
            if (op != null) {
               retValue = -1;
            } else {
               retValue = isSet(opts, 512) && offset != con.limit ? -1 : offset;
            }

            returned = true;
         }

         while(returned) {
            if (opStack.isEmpty()) {
               return retValue;
            }

            op = (Op)opStack.pop();
            offset = dataStack.pop();
            switch(op.type) {
            case 7:
            case 9:
               if (retValue < 0) {
                  op = op.next;
                  returned = false;
               }
               break;
            case 8:
            case 10:
               if (retValue < 0) {
                  op = op.getChild();
                  returned = false;
               }
               break;
            case 11:
               refno = dataStack.pop();
               if (retValue < 0) {
                  ++refno;
                  if (refno < op.size()) {
                     opStack.push(op);
                     dataStack.push(refno);
                     dataStack.push(offset);
                     op = op.elementAt(refno);
                     returned = false;
                  } else {
                     retValue = -1;
                  }
               }
            case 12:
            case 13:
            case 14:
            case 16:
            case 17:
            case 18:
            case 19:
            default:
               break;
            case 15:
               refno = op.getData();
               saved = dataStack.pop();
               if (retValue < 0) {
                  if (refno > 0) {
                     con.match.setBeginning(refno, saved);
                  } else {
                     con.match.setEnd(-refno, saved);
                  }
               }
               break;
            case 20:
            case 22:
               dx = dataStack.pop();
               if (0 <= retValue) {
                  op = op.next;
                  returned = false;
               }

               retValue = -1;
               break;
            case 21:
            case 23:
               dx = dataStack.pop();
               if (0 > retValue) {
                  op = op.next;
                  returned = false;
               }

               retValue = -1;
               break;
            case 25:
               opts = dataStack.pop();
            case 24:
               if (retValue >= 0) {
                  offset = retValue;
                  op = op.next;
                  returned = false;
               }
               break;
            case 26:
               Op.ConditionOp cop = (Op.ConditionOp)op;
               if (0 <= retValue) {
                  op = cop.yes;
               } else if (cop.no != null) {
                  op = cop.no;
               } else {
                  op = cop.next;
               }

               returned = false;
            }
         }
      }
   }

   private boolean matchChar(int ch, int other, boolean ignoreCase) {
      return ignoreCase ? matchIgnoreCase(ch, other) : ch == other;
   }

   boolean matchAnchor(RegularExpression.ExpressionTarget target, Op op, RegularExpression.Context con, int offset, int opts) {
      boolean go = false;
      int after;
      switch(op.getData()) {
      case 36:
         if (isSet(opts, 8)) {
            if (offset == con.limit || offset < con.limit && isEOLChar(target.charAt(offset))) {
               break;
            }

            return false;
         } else {
            if (offset == con.limit || offset + 1 == con.limit && isEOLChar(target.charAt(offset)) || offset + 2 == con.limit && target.charAt(offset) == '\r' && target.charAt(offset + 1) == '\n') {
               break;
            }

            return false;
         }
      case 60:
         if (con.length != 0 && offset != con.limit) {
            if (getWordType(target, con.start, con.limit, offset, opts) == 1 && getPreviousWordType(target, con.start, con.limit, offset, opts) == 2) {
               break;
            }

            return false;
         }

         return false;
      case 62:
         if (con.length != 0 && offset != con.start) {
            if (getWordType(target, con.start, con.limit, offset, opts) != 2 || getPreviousWordType(target, con.start, con.limit, offset, opts) != 1) {
               return false;
            }
            break;
         }

         return false;
      case 64:
         if (offset == con.start || offset > con.start && isEOLChar(target.charAt(offset - 1))) {
            break;
         }

         return false;
      case 65:
         if (offset != con.start) {
            return false;
         }
         break;
      case 66:
         if (con.length == 0) {
            go = true;
         } else {
            after = getWordType(target, con.start, con.limit, offset, opts);
            go = after == 0 || after == getPreviousWordType(target, con.start, con.limit, offset, opts);
         }

         if (!go) {
            return false;
         }
         break;
      case 90:
         if (offset == con.limit || offset + 1 == con.limit && isEOLChar(target.charAt(offset)) || offset + 2 == con.limit && target.charAt(offset) == '\r' && target.charAt(offset + 1) == '\n') {
            break;
         }

         return false;
      case 94:
         if (isSet(opts, 8)) {
            if (offset == con.start || offset > con.start && offset < con.limit && isEOLChar(target.charAt(offset - 1))) {
               break;
            }

            return false;
         }

         if (offset != con.start) {
            return false;
         }
         break;
      case 98:
         if (con.length == 0) {
            return false;
         }

         after = getWordType(target, con.start, con.limit, offset, opts);
         if (after == 0) {
            return false;
         }

         int before = getPreviousWordType(target, con.start, con.limit, offset, opts);
         if (after == before) {
            return false;
         }
         break;
      case 122:
         if (offset != con.limit) {
            return false;
         }
      }

      return true;
   }

   private static final int getPreviousWordType(RegularExpression.ExpressionTarget target, int begin, int end, int offset, int opts) {
      --offset;

      int ret;
      for(ret = getWordType(target, begin, end, offset, opts); ret == 0; ret = getWordType(target, begin, end, offset, opts)) {
         --offset;
      }

      return ret;
   }

   private static final int getWordType(RegularExpression.ExpressionTarget target, int begin, int end, int offset, int opts) {
      return offset >= begin && offset < end ? getWordType0(target.charAt(offset), opts) : 2;
   }

   public boolean matches(CharacterIterator target) {
      return this.matches(target, (Match)null);
   }

   public boolean matches(CharacterIterator target, Match match) {
      int start = target.getBeginIndex();
      int end = target.getEndIndex();
      synchronized(this) {
         if (this.operations == null) {
            this.prepare();
         }

         if (this.context == null) {
            this.context = new RegularExpression.Context();
         }
      }

      RegularExpression.Context con = null;
      synchronized(this.context) {
         con = this.context.inuse ? new RegularExpression.Context() : this.context;
         con.reset(target, start, end, this.numberOfClosures);
      }

      if (match != null) {
         match.setNumberOfGroups(this.nofparen);
         match.setSource(target);
      } else if (this.hasBackReferences) {
         match = new Match();
         match.setNumberOfGroups(this.nofparen);
      }

      con.match = match;
      int limit;
      if (isSet(this.options, 512)) {
         limit = this.match(con, this.operations, con.start, 1, this.options);
         if (limit == con.limit) {
            if (con.match != null) {
               con.match.setBeginning(0, con.start);
               con.match.setEnd(0, limit);
            }

            con.setInUse(false);
            return true;
         } else {
            return false;
         }
      } else if (this.fixedStringOnly) {
         limit = this.fixedStringTable.matches(target, con.start, con.limit);
         if (limit >= 0) {
            if (con.match != null) {
               con.match.setBeginning(0, limit);
               con.match.setEnd(0, limit + this.fixedString.length());
            }

            con.setInUse(false);
            return true;
         } else {
            con.setInUse(false);
            return false;
         }
      } else {
         if (this.fixedString != null) {
            limit = this.fixedStringTable.matches(target, con.start, con.limit);
            if (limit < 0) {
               con.setInUse(false);
               return false;
            }
         }

         limit = con.limit - this.minlength;
         int matchEnd = -1;
         int matchStart;
         if (this.operations != null && this.operations.type == 7 && this.operations.getChild().type == 0) {
            if (isSet(this.options, 4)) {
               matchStart = con.start;
               matchEnd = this.match(con, this.operations, con.start, 1, this.options);
            } else {
               boolean previousIsEOL = true;

               for(matchStart = con.start; matchStart <= limit; ++matchStart) {
                  int ch = target.setIndex(matchStart);
                  if (isEOLChar(ch)) {
                     previousIsEOL = true;
                  } else {
                     if (previousIsEOL && 0 <= (matchEnd = this.match(con, this.operations, matchStart, 1, this.options))) {
                        break;
                     }

                     previousIsEOL = false;
                  }
               }
            }
         } else if (this.firstChar != null) {
            RangeToken range = this.firstChar;

            for(matchStart = con.start; matchStart <= limit; ++matchStart) {
               int ch = target.setIndex(matchStart);
               if (REUtil.isHighSurrogate(ch) && matchStart + 1 < con.limit) {
                  ch = REUtil.composeFromSurrogates(ch, target.setIndex(matchStart + 1));
               }

               if (range.match(ch) && 0 <= (matchEnd = this.match(con, this.operations, matchStart, 1, this.options))) {
                  break;
               }
            }
         } else {
            for(matchStart = con.start; matchStart <= limit && 0 > (matchEnd = this.match(con, this.operations, matchStart, 1, this.options)); ++matchStart) {
            }
         }

         if (matchEnd >= 0) {
            if (con.match != null) {
               con.match.setBeginning(0, matchStart);
               con.match.setEnd(0, matchEnd);
            }

            con.setInUse(false);
            return true;
         } else {
            con.setInUse(false);
            return false;
         }
      }
   }

   void prepare() {
      this.compile(this.tokentree);
      this.minlength = this.tokentree.getMinLength();
      this.firstChar = null;
      if (!isSet(this.options, 128) && !isSet(this.options, 512)) {
         RangeToken firstChar = Token.createRange();
         int fresult = this.tokentree.analyzeFirstCharacter(firstChar, this.options);
         if (fresult == 1) {
            firstChar.compactRanges();
            this.firstChar = firstChar;
         }
      }

      if (this.operations != null && (this.operations.type == 6 || this.operations.type == 1) && this.operations.next == null) {
         this.fixedStringOnly = true;
         if (this.operations.type == 6) {
            this.fixedString = this.operations.getString();
         } else if (this.operations.getData() >= 65536) {
            this.fixedString = REUtil.decomposeToSurrogates(this.operations.getData());
         } else {
            char[] ac = new char[]{(char)this.operations.getData()};
            this.fixedString = new String(ac);
         }

         this.fixedStringOptions = this.options;
         this.fixedStringTable = new BMPattern(this.fixedString, 256, isSet(this.fixedStringOptions, 2));
      } else if (!isSet(this.options, 256) && !isSet(this.options, 512)) {
         Token.FixedStringContainer container = new Token.FixedStringContainer();
         this.tokentree.findFixedString(container, this.options);
         this.fixedString = container.token == null ? null : container.token.getString();
         this.fixedStringOptions = container.options;
         if (this.fixedString != null && this.fixedString.length() < 2) {
            this.fixedString = null;
         }

         if (this.fixedString != null) {
            this.fixedStringTable = new BMPattern(this.fixedString, 256, isSet(this.fixedStringOptions, 2));
         }
      }

   }

   private static final boolean isSet(int options, int flag) {
      return (options & flag) == flag;
   }

   public RegularExpression(String regex) throws ParseException {
      this(regex, (String)null);
   }

   public RegularExpression(String regex, String options) throws ParseException {
      this.hasBackReferences = false;
      this.operations = null;
      this.context = null;
      this.firstChar = null;
      this.fixedString = null;
      this.fixedStringTable = null;
      this.fixedStringOnly = false;
      this.setPattern(regex, options);
   }

   public RegularExpression(String regex, String options, Locale locale) throws ParseException {
      this.hasBackReferences = false;
      this.operations = null;
      this.context = null;
      this.firstChar = null;
      this.fixedString = null;
      this.fixedStringTable = null;
      this.fixedStringOnly = false;
      this.setPattern(regex, options, locale);
   }

   RegularExpression(String regex, Token tok, int parens, boolean hasBackReferences, int options) {
      this.hasBackReferences = false;
      this.operations = null;
      this.context = null;
      this.firstChar = null;
      this.fixedString = null;
      this.fixedStringTable = null;
      this.fixedStringOnly = false;
      this.regex = regex;
      this.tokentree = tok;
      this.nofparen = parens;
      this.options = options;
      this.hasBackReferences = hasBackReferences;
   }

   public void setPattern(String newPattern) throws ParseException {
      this.setPattern(newPattern, Locale.getDefault());
   }

   public void setPattern(String newPattern, Locale locale) throws ParseException {
      this.setPattern(newPattern, this.options, locale);
   }

   private void setPattern(String newPattern, int options, Locale locale) throws ParseException {
      this.regex = newPattern;
      this.options = options;
      RegexParser rp = isSet(this.options, 512) ? new ParserForXMLSchema(locale) : new RegexParser(locale);
      this.tokentree = ((RegexParser)rp).parse(this.regex, this.options);
      this.nofparen = ((RegexParser)rp).parennumber;
      this.hasBackReferences = ((RegexParser)rp).hasBackReferences;
      this.operations = null;
      this.context = null;
   }

   public void setPattern(String newPattern, String options) throws ParseException {
      this.setPattern(newPattern, options, Locale.getDefault());
   }

   public void setPattern(String newPattern, String options, Locale locale) throws ParseException {
      this.setPattern(newPattern, REUtil.parseOptions(options), locale);
   }

   public String getPattern() {
      return this.regex;
   }

   public String toString() {
      return this.tokentree.toString(this.options);
   }

   public String getOptions() {
      return REUtil.createOptionString(this.options);
   }

   public boolean equals(Object obj) {
      if (obj == null) {
         return false;
      } else if (!(obj instanceof RegularExpression)) {
         return false;
      } else {
         RegularExpression r = (RegularExpression)obj;
         return this.regex.equals(r.regex) && this.options == r.options;
      }
   }

   boolean equals(String pattern, int options) {
      return this.regex.equals(pattern) && this.options == options;
   }

   public int hashCode() {
      return (this.regex + "/" + this.getOptions()).hashCode();
   }

   public int getNumberOfGroups() {
      return this.nofparen;
   }

   private static final int getWordType0(char ch, int opts) {
      if (!isSet(opts, 64)) {
         if (isSet(opts, 32)) {
            return Token.getRange("IsWord", true).match(ch) ? 1 : 2;
         } else {
            return isWordChar(ch) ? 1 : 2;
         }
      } else {
         switch(Character.getType(ch)) {
         case 1:
         case 2:
         case 3:
         case 4:
         case 5:
         case 8:
         case 9:
         case 10:
         case 11:
            return 1;
         case 6:
         case 7:
         case 16:
            return 0;
         case 12:
         case 13:
         case 14:
         default:
            return 2;
         case 15:
            switch(ch) {
            case '\t':
            case '\n':
            case '\u000b':
            case '\f':
            case '\r':
               return 2;
            default:
               return 0;
            }
         }
      }
   }

   private static final boolean isEOLChar(int ch) {
      return ch == 10 || ch == 13 || ch == 8232 || ch == 8233;
   }

   private static final boolean isWordChar(int ch) {
      if (ch == 95) {
         return true;
      } else if (ch < 48) {
         return false;
      } else if (ch > 122) {
         return false;
      } else if (ch <= 57) {
         return true;
      } else if (ch < 65) {
         return false;
      } else if (ch <= 90) {
         return true;
      } else {
         return ch >= 97;
      }
   }

   private static final boolean matchIgnoreCase(int chardata, int ch) {
      if (chardata == ch) {
         return true;
      } else if (chardata <= 65535 && ch <= 65535) {
         char uch1 = Character.toUpperCase((char)chardata);
         char uch2 = Character.toUpperCase((char)ch);
         if (uch1 == uch2) {
            return true;
         } else {
            return Character.toLowerCase(uch1) == Character.toLowerCase(uch2);
         }
      } else {
         return false;
      }
   }

   static final class Context {
      int start;
      int limit;
      int length;
      Match match;
      boolean inuse = false;
      RegularExpression.ClosureContext[] closureContexts;
      private RegularExpression.StringTarget stringTarget;
      private RegularExpression.CharArrayTarget charArrayTarget;
      private RegularExpression.CharacterIteratorTarget characterIteratorTarget;
      RegularExpression.ExpressionTarget target;

      private void resetCommon(int nofclosures) {
         this.length = this.limit - this.start;
         this.setInUse(true);
         this.match = null;
         if (this.closureContexts == null || this.closureContexts.length != nofclosures) {
            this.closureContexts = new RegularExpression.ClosureContext[nofclosures];
         }

         for(int i = 0; i < nofclosures; ++i) {
            if (this.closureContexts[i] == null) {
               this.closureContexts[i] = new RegularExpression.ClosureContext();
            } else {
               this.closureContexts[i].reset();
            }
         }

      }

      void reset(CharacterIterator target, int start, int limit, int nofclosures) {
         if (this.characterIteratorTarget == null) {
            this.characterIteratorTarget = new RegularExpression.CharacterIteratorTarget(target);
         } else {
            this.characterIteratorTarget.resetTarget(target);
         }

         this.target = this.characterIteratorTarget;
         this.start = start;
         this.limit = limit;
         this.resetCommon(nofclosures);
      }

      void reset(String target, int start, int limit, int nofclosures) {
         if (this.stringTarget == null) {
            this.stringTarget = new RegularExpression.StringTarget(target);
         } else {
            this.stringTarget.resetTarget(target);
         }

         this.target = this.stringTarget;
         this.start = start;
         this.limit = limit;
         this.resetCommon(nofclosures);
      }

      void reset(char[] target, int start, int limit, int nofclosures) {
         if (this.charArrayTarget == null) {
            this.charArrayTarget = new RegularExpression.CharArrayTarget(target);
         } else {
            this.charArrayTarget.resetTarget(target);
         }

         this.target = this.charArrayTarget;
         this.start = start;
         this.limit = limit;
         this.resetCommon(nofclosures);
      }

      synchronized void setInUse(boolean inUse) {
         this.inuse = inUse;
      }
   }

   static final class ClosureContext {
      int[] offsets = new int[4];
      int currentIndex = 0;

      boolean contains(int offset) {
         for(int i = 0; i < this.currentIndex; ++i) {
            if (this.offsets[i] == offset) {
               return true;
            }
         }

         return false;
      }

      void reset() {
         this.currentIndex = 0;
      }

      void addOffset(int offset) {
         if (this.currentIndex == this.offsets.length) {
            this.offsets = this.expandOffsets();
         }

         this.offsets[this.currentIndex++] = offset;
      }

      private int[] expandOffsets() {
         int len = this.offsets.length;
         int newLen = len << 1;
         int[] newOffsets = new int[newLen];
         System.arraycopy(this.offsets, 0, newOffsets, 0, this.currentIndex);
         return newOffsets;
      }
   }

   static final class CharacterIteratorTarget extends RegularExpression.ExpressionTarget {
      CharacterIterator target;

      CharacterIteratorTarget(CharacterIterator target) {
         this.target = target;
      }

      final void resetTarget(CharacterIterator target) {
         this.target = target;
      }

      final char charAt(int index) {
         return this.target.setIndex(index);
      }

      final boolean regionMatches(boolean ignoreCase, int offset, int limit, String part, int partlen) {
         if (offset >= 0 && limit - offset >= partlen) {
            return ignoreCase ? this.regionMatchesIgnoreCase(offset, limit, part, partlen) : this.regionMatches(offset, limit, part, partlen);
         } else {
            return false;
         }
      }

      private final boolean regionMatches(int offset, int limit, String part, int partlen) {
         int var5 = 0;

         do {
            if (partlen-- <= 0) {
               return true;
            }
         } while(this.target.setIndex(offset++) == part.charAt(var5++));

         return false;
      }

      private final boolean regionMatchesIgnoreCase(int offset, int limit, String part, int partlen) {
         int var5 = 0;

         while(partlen-- > 0) {
            char ch1 = this.target.setIndex(offset++);
            char ch2 = part.charAt(var5++);
            if (ch1 != ch2) {
               char uch1 = Character.toUpperCase(ch1);
               char uch2 = Character.toUpperCase(ch2);
               if (uch1 != uch2 && Character.toLowerCase(uch1) != Character.toLowerCase(uch2)) {
                  return false;
               }
            }
         }

         return true;
      }

      final boolean regionMatches(boolean ignoreCase, int offset, int limit, int offset2, int partlen) {
         if (offset >= 0 && limit - offset >= partlen) {
            return ignoreCase ? this.regionMatchesIgnoreCase(offset, limit, offset2, partlen) : this.regionMatches(offset, limit, offset2, partlen);
         } else {
            return false;
         }
      }

      private final boolean regionMatches(int offset, int limit, int offset2, int partlen) {
         int var5 = offset2;

         do {
            if (partlen-- <= 0) {
               return true;
            }
         } while(this.target.setIndex(offset++) == this.target.setIndex(var5++));

         return false;
      }

      private final boolean regionMatchesIgnoreCase(int offset, int limit, int offset2, int partlen) {
         int var5 = offset2;

         while(partlen-- > 0) {
            char ch1 = this.target.setIndex(offset++);
            char ch2 = this.target.setIndex(var5++);
            if (ch1 != ch2) {
               char uch1 = Character.toUpperCase(ch1);
               char uch2 = Character.toUpperCase(ch2);
               if (uch1 != uch2 && Character.toLowerCase(uch1) != Character.toLowerCase(uch2)) {
                  return false;
               }
            }
         }

         return true;
      }
   }

   static final class CharArrayTarget extends RegularExpression.ExpressionTarget {
      char[] target;

      CharArrayTarget(char[] target) {
         this.target = target;
      }

      final void resetTarget(char[] target) {
         this.target = target;
      }

      char charAt(int index) {
         return this.target[index];
      }

      final boolean regionMatches(boolean ignoreCase, int offset, int limit, String part, int partlen) {
         if (offset >= 0 && limit - offset >= partlen) {
            return ignoreCase ? this.regionMatchesIgnoreCase(offset, limit, part, partlen) : this.regionMatches(offset, limit, part, partlen);
         } else {
            return false;
         }
      }

      private final boolean regionMatches(int offset, int limit, String part, int partlen) {
         int var5 = 0;

         do {
            if (partlen-- <= 0) {
               return true;
            }
         } while(this.target[offset++] == part.charAt(var5++));

         return false;
      }

      private final boolean regionMatchesIgnoreCase(int offset, int limit, String part, int partlen) {
         int var5 = 0;

         while(partlen-- > 0) {
            char ch1 = this.target[offset++];
            char ch2 = part.charAt(var5++);
            if (ch1 != ch2) {
               char uch1 = Character.toUpperCase(ch1);
               char uch2 = Character.toUpperCase(ch2);
               if (uch1 != uch2 && Character.toLowerCase(uch1) != Character.toLowerCase(uch2)) {
                  return false;
               }
            }
         }

         return true;
      }

      final boolean regionMatches(boolean ignoreCase, int offset, int limit, int offset2, int partlen) {
         if (offset >= 0 && limit - offset >= partlen) {
            return ignoreCase ? this.regionMatchesIgnoreCase(offset, limit, offset2, partlen) : this.regionMatches(offset, limit, offset2, partlen);
         } else {
            return false;
         }
      }

      private final boolean regionMatches(int offset, int limit, int offset2, int partlen) {
         int var5 = offset2;

         do {
            if (partlen-- <= 0) {
               return true;
            }
         } while(this.target[offset++] == this.target[var5++]);

         return false;
      }

      private final boolean regionMatchesIgnoreCase(int offset, int limit, int offset2, int partlen) {
         int var5 = offset2;

         while(partlen-- > 0) {
            char ch1 = this.target[offset++];
            char ch2 = this.target[var5++];
            if (ch1 != ch2) {
               char uch1 = Character.toUpperCase(ch1);
               char uch2 = Character.toUpperCase(ch2);
               if (uch1 != uch2 && Character.toLowerCase(uch1) != Character.toLowerCase(uch2)) {
                  return false;
               }
            }
         }

         return true;
      }
   }

   static final class StringTarget extends RegularExpression.ExpressionTarget {
      private String target;

      StringTarget(String target) {
         this.target = target;
      }

      final void resetTarget(String target) {
         this.target = target;
      }

      final char charAt(int index) {
         return this.target.charAt(index);
      }

      final boolean regionMatches(boolean ignoreCase, int offset, int limit, String part, int partlen) {
         if (limit - offset < partlen) {
            return false;
         } else {
            return ignoreCase ? this.target.regionMatches(true, offset, part, 0, partlen) : this.target.regionMatches(offset, part, 0, partlen);
         }
      }

      final boolean regionMatches(boolean ignoreCase, int offset, int limit, int offset2, int partlen) {
         if (limit - offset < partlen) {
            return false;
         } else {
            return ignoreCase ? this.target.regionMatches(true, offset, this.target, offset2, partlen) : this.target.regionMatches(offset, this.target, offset2, partlen);
         }
      }
   }

   abstract static class ExpressionTarget {
      abstract char charAt(int var1);

      abstract boolean regionMatches(boolean var1, int var2, int var3, String var4, int var5);

      abstract boolean regionMatches(boolean var1, int var2, int var3, int var4, int var5);
   }
}
