package com.sun.org.apache.xerces.internal.impl.xpath;

import com.sun.org.apache.xerces.internal.util.SymbolTable;
import com.sun.org.apache.xerces.internal.util.XMLChar;
import com.sun.org.apache.xerces.internal.util.XMLSymbols;
import com.sun.org.apache.xerces.internal.xni.NamespaceContext;
import com.sun.org.apache.xerces.internal.xni.QName;
import java.io.PrintStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Vector;

public class XPath {
   private static final boolean DEBUG_ALL = false;
   private static final boolean DEBUG_XPATH_PARSE = false;
   private static final boolean DEBUG_ANY = false;
   protected String fExpression;
   protected SymbolTable fSymbolTable;
   protected XPath.LocationPath[] fLocationPaths;

   public XPath(String xpath, SymbolTable symbolTable, NamespaceContext context) throws XPathException {
      this.fExpression = xpath;
      this.fSymbolTable = symbolTable;
      this.parseExpression(context);
   }

   public XPath.LocationPath[] getLocationPaths() {
      XPath.LocationPath[] ret = new XPath.LocationPath[this.fLocationPaths.length];

      for(int i = 0; i < this.fLocationPaths.length; ++i) {
         ret[i] = (XPath.LocationPath)this.fLocationPaths[i].clone();
      }

      return ret;
   }

   public XPath.LocationPath getLocationPath() {
      return (XPath.LocationPath)this.fLocationPaths[0].clone();
   }

   public String toString() {
      StringBuffer buf = new StringBuffer();

      for(int i = 0; i < this.fLocationPaths.length; ++i) {
         if (i > 0) {
            buf.append("|");
         }

         buf.append(this.fLocationPaths[i].toString());
      }

      return buf.toString();
   }

   private static void check(boolean b) throws XPathException {
      if (!b) {
         throw new XPathException("c-general-xpath");
      }
   }

   private XPath.LocationPath buildLocationPath(Vector stepsVector) throws XPathException {
      int size = stepsVector.size();
      check(size != 0);
      XPath.Step[] steps = new XPath.Step[size];
      stepsVector.copyInto(steps);
      stepsVector.removeAllElements();
      return new XPath.LocationPath(steps);
   }

   private void parseExpression(NamespaceContext context) throws XPathException {
      XPath.Tokens xtokens = new XPath.Tokens(this.fSymbolTable);
      XPath.Scanner scanner = new XPath.Scanner(this.fSymbolTable) {
         protected void addToken(XPath.Tokens tokens, int token) throws XPathException {
            if (token != 6 && token != 35 && token != 11 && token != 21 && token != 4 && token != 9 && token != 10 && token != 22 && token != 23 && token != 36 && token != 8) {
               throw new XPathException("c-general-xpath");
            } else {
               super.addToken(tokens, token);
            }
         }
      };
      int length = this.fExpression.length();
      boolean success = scanner.scanExpr(this.fSymbolTable, xtokens, this.fExpression, 0, length);
      if (!success) {
         throw new XPathException("c-general-xpath");
      } else {
         Vector stepsVector = new Vector();
         Vector locationPathsVector = new Vector();
         boolean expectingStep = true;
         boolean expectingDoubleColon = false;

         while(xtokens.hasMore()) {
            int token = xtokens.nextToken();
            XPath.Step step;
            switch(token) {
            case 4:
               check(expectingStep);
               expectingStep = false;
               if (stepsVector.size() == 0) {
                  XPath.Axis axis = new XPath.Axis((short)3);
                  XPath.NodeTest nodeTest = new XPath.NodeTest((short)3);
                  XPath.Step step = new XPath.Step(axis, nodeTest);
                  stepsVector.addElement(step);
                  if (xtokens.hasMore() && xtokens.peekToken() == 22) {
                     xtokens.nextToken();
                     axis = new XPath.Axis((short)4);
                     nodeTest = new XPath.NodeTest((short)3);
                     step = new XPath.Step(axis, nodeTest);
                     stepsVector.addElement(step);
                     expectingStep = true;
                  }
               }
               break;
            case 5:
            case 7:
            case 12:
            case 13:
            case 14:
            case 15:
            case 16:
            case 17:
            case 18:
            case 19:
            case 20:
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
            default:
               throw new XPathException("c-general-xpath");
            case 6:
               check(expectingStep);
               step = new XPath.Step(new XPath.Axis((short)2), this.parseNodeTest(xtokens.nextToken(), xtokens, context));
               stepsVector.addElement(step);
               expectingStep = false;
               break;
            case 8:
               check(expectingStep);
               check(expectingDoubleColon);
               expectingDoubleColon = false;
               break;
            case 9:
            case 10:
            case 11:
               check(expectingStep);
               step = new XPath.Step(new XPath.Axis((short)1), this.parseNodeTest(token, xtokens, context));
               stepsVector.addElement(step);
               expectingStep = false;
               break;
            case 21:
               check(!expectingStep);
               expectingStep = true;
               break;
            case 22:
               throw new XPathException("c-general-xpath");
            case 23:
               check(!expectingStep);
               locationPathsVector.addElement(this.buildLocationPath(stepsVector));
               expectingStep = true;
               break;
            case 35:
               check(expectingStep);
               expectingDoubleColon = true;
               if (xtokens.nextToken() == 8) {
                  step = new XPath.Step(new XPath.Axis((short)2), this.parseNodeTest(xtokens.nextToken(), xtokens, context));
                  stepsVector.addElement(step);
                  expectingStep = false;
                  expectingDoubleColon = false;
               }
               break;
            case 36:
               check(expectingStep);
               expectingDoubleColon = true;
            }
         }

         check(!expectingStep);
         locationPathsVector.addElement(this.buildLocationPath(stepsVector));
         this.fLocationPaths = new XPath.LocationPath[locationPathsVector.size()];
         locationPathsVector.copyInto(this.fLocationPaths);
      }
   }

   private XPath.NodeTest parseNodeTest(int typeToken, XPath.Tokens xtokens, NamespaceContext context) throws XPathException {
      switch(typeToken) {
      case 9:
         return new XPath.NodeTest((short)2);
      case 10:
      case 11:
         String prefix = xtokens.nextTokenAsString();
         String uri = null;
         if (context != null && prefix != XMLSymbols.EMPTY_STRING) {
            uri = context.getURI(prefix);
         }

         if (prefix != XMLSymbols.EMPTY_STRING && context != null && uri == null) {
            throw new XPathException("c-general-xpath-ns");
         } else {
            if (typeToken == 10) {
               return new XPath.NodeTest(prefix, uri);
            }

            String localpart = xtokens.nextTokenAsString();
            String rawname = prefix != XMLSymbols.EMPTY_STRING ? this.fSymbolTable.addSymbol(prefix + ':' + localpart) : localpart;
            return new XPath.NodeTest(new QName(prefix, localpart, rawname, uri));
         }
      default:
         throw new XPathException("c-general-xpath");
      }
   }

   public static void main(String[] argv) throws Exception {
      for(int i = 0; i < argv.length; ++i) {
         String expression = argv[i];
         System.out.println("# XPath expression: \"" + expression + '"');

         try {
            SymbolTable symbolTable = new SymbolTable();
            XPath xpath = new XPath(expression, symbolTable, (NamespaceContext)null);
            System.out.println("expanded xpath: \"" + xpath.toString() + '"');
         } catch (XPathException var5) {
            System.out.println("error: " + var5.getMessage());
         }
      }

   }

   private static class Scanner {
      private static final byte CHARTYPE_INVALID = 0;
      private static final byte CHARTYPE_OTHER = 1;
      private static final byte CHARTYPE_WHITESPACE = 2;
      private static final byte CHARTYPE_EXCLAMATION = 3;
      private static final byte CHARTYPE_QUOTE = 4;
      private static final byte CHARTYPE_DOLLAR = 5;
      private static final byte CHARTYPE_OPEN_PAREN = 6;
      private static final byte CHARTYPE_CLOSE_PAREN = 7;
      private static final byte CHARTYPE_STAR = 8;
      private static final byte CHARTYPE_PLUS = 9;
      private static final byte CHARTYPE_COMMA = 10;
      private static final byte CHARTYPE_MINUS = 11;
      private static final byte CHARTYPE_PERIOD = 12;
      private static final byte CHARTYPE_SLASH = 13;
      private static final byte CHARTYPE_DIGIT = 14;
      private static final byte CHARTYPE_COLON = 15;
      private static final byte CHARTYPE_LESS = 16;
      private static final byte CHARTYPE_EQUAL = 17;
      private static final byte CHARTYPE_GREATER = 18;
      private static final byte CHARTYPE_ATSIGN = 19;
      private static final byte CHARTYPE_LETTER = 20;
      private static final byte CHARTYPE_OPEN_BRACKET = 21;
      private static final byte CHARTYPE_CLOSE_BRACKET = 22;
      private static final byte CHARTYPE_UNDERSCORE = 23;
      private static final byte CHARTYPE_UNION = 24;
      private static final byte CHARTYPE_NONASCII = 25;
      private static final byte[] fASCIICharMap = new byte[]{0, 0, 0, 0, 0, 0, 0, 0, 0, 2, 2, 0, 0, 2, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 2, 3, 4, 1, 5, 1, 1, 4, 6, 7, 8, 9, 10, 11, 12, 13, 14, 14, 14, 14, 14, 14, 14, 14, 14, 14, 15, 1, 16, 17, 18, 1, 19, 20, 20, 20, 20, 20, 20, 20, 20, 20, 20, 20, 20, 20, 20, 20, 20, 20, 20, 20, 20, 20, 20, 20, 20, 20, 20, 21, 1, 22, 1, 23, 1, 20, 20, 20, 20, 20, 20, 20, 20, 20, 20, 20, 20, 20, 20, 20, 20, 20, 20, 20, 20, 20, 20, 20, 20, 20, 20, 1, 24, 1, 1, 1};
      private SymbolTable fSymbolTable;
      private static final String fAndSymbol = "and".intern();
      private static final String fOrSymbol = "or".intern();
      private static final String fModSymbol = "mod".intern();
      private static final String fDivSymbol = "div".intern();
      private static final String fCommentSymbol = "comment".intern();
      private static final String fTextSymbol = "text".intern();
      private static final String fPISymbol = "processing-instruction".intern();
      private static final String fNodeSymbol = "node".intern();
      private static final String fAncestorSymbol = "ancestor".intern();
      private static final String fAncestorOrSelfSymbol = "ancestor-or-self".intern();
      private static final String fAttributeSymbol = "attribute".intern();
      private static final String fChildSymbol = "child".intern();
      private static final String fDescendantSymbol = "descendant".intern();
      private static final String fDescendantOrSelfSymbol = "descendant-or-self".intern();
      private static final String fFollowingSymbol = "following".intern();
      private static final String fFollowingSiblingSymbol = "following-sibling".intern();
      private static final String fNamespaceSymbol = "namespace".intern();
      private static final String fParentSymbol = "parent".intern();
      private static final String fPrecedingSymbol = "preceding".intern();
      private static final String fPrecedingSiblingSymbol = "preceding-sibling".intern();
      private static final String fSelfSymbol = "self".intern();

      public Scanner(SymbolTable symbolTable) {
         this.fSymbolTable = symbolTable;
      }

      public boolean scanExpr(SymbolTable symbolTable, XPath.Tokens tokens, String data, int currentOffset, int endOffset) throws XPathException {
         boolean starIsMultiplyOperator = false;

         while(currentOffset != endOffset) {
            char ch;
            for(ch = data.charAt(currentOffset); ch == ' ' || ch == '\n' || ch == '\t' || ch == '\r'; ch = data.charAt(currentOffset)) {
               ++currentOffset;
               if (currentOffset == endOffset) {
                  break;
               }
            }

            if (currentOffset == endOffset) {
               break;
            }

            byte chartype = ch >= 128 ? 25 : fASCIICharMap[ch];
            int nameOffset;
            String nameHandle;
            String prefixHandle;
            int ch;
            switch(chartype) {
            case 3:
               ++currentOffset;
               if (currentOffset == endOffset) {
                  return false;
               }

               ch = data.charAt(currentOffset);
               if (ch != '=') {
                  return false;
               }

               this.addToken(tokens, 27);
               starIsMultiplyOperator = false;
               ++currentOffset;
               if (currentOffset == endOffset) {
               }
               break;
            case 4:
               int qchar = ch;
               ++currentOffset;
               if (currentOffset == endOffset) {
                  return false;
               }

               ch = data.charAt(currentOffset);

               int litOffset;
               for(litOffset = currentOffset; ch != qchar; ch = data.charAt(currentOffset)) {
                  ++currentOffset;
                  if (currentOffset == endOffset) {
                     return false;
                  }
               }

               int litLength = currentOffset - litOffset;
               this.addToken(tokens, 46);
               starIsMultiplyOperator = true;
               tokens.addToken(symbolTable.addSymbol(data.substring(litOffset, litOffset + litLength)));
               ++currentOffset;
               if (currentOffset == endOffset) {
               }
               break;
            case 5:
               ++currentOffset;
               if (currentOffset == endOffset) {
                  return false;
               }

               nameOffset = currentOffset;
               currentOffset = this.scanNCName(data, endOffset, currentOffset);
               if (currentOffset == nameOffset) {
                  return false;
               }

               if (currentOffset < endOffset) {
                  ch = data.charAt(currentOffset);
               } else {
                  ch = -1;
               }

               nameHandle = symbolTable.addSymbol(data.substring(nameOffset, currentOffset));
               if (ch != 58) {
                  prefixHandle = XMLSymbols.EMPTY_STRING;
               } else {
                  prefixHandle = nameHandle;
                  ++currentOffset;
                  if (currentOffset == endOffset) {
                     return false;
                  }

                  nameOffset = currentOffset;
                  currentOffset = this.scanNCName(data, endOffset, currentOffset);
                  if (currentOffset == nameOffset) {
                     return false;
                  }

                  if (currentOffset < endOffset) {
                     data.charAt(currentOffset);
                  } else {
                     boolean var18 = true;
                  }

                  nameHandle = symbolTable.addSymbol(data.substring(nameOffset, currentOffset));
               }

               this.addToken(tokens, 48);
               starIsMultiplyOperator = true;
               tokens.addToken(prefixHandle);
               tokens.addToken(nameHandle);
               break;
            case 6:
               this.addToken(tokens, 0);
               starIsMultiplyOperator = false;
               ++currentOffset;
               if (currentOffset == endOffset) {
               }
               break;
            case 7:
               this.addToken(tokens, 1);
               starIsMultiplyOperator = true;
               ++currentOffset;
               if (currentOffset == endOffset) {
               }
               break;
            case 8:
               if (starIsMultiplyOperator) {
                  this.addToken(tokens, 20);
                  starIsMultiplyOperator = false;
               } else {
                  this.addToken(tokens, 9);
                  starIsMultiplyOperator = true;
               }

               ++currentOffset;
               if (currentOffset == endOffset) {
               }
               break;
            case 9:
               this.addToken(tokens, 24);
               starIsMultiplyOperator = false;
               ++currentOffset;
               if (currentOffset == endOffset) {
               }
               break;
            case 10:
               this.addToken(tokens, 7);
               starIsMultiplyOperator = false;
               ++currentOffset;
               if (currentOffset == endOffset) {
               }
               break;
            case 11:
               this.addToken(tokens, 25);
               starIsMultiplyOperator = false;
               ++currentOffset;
               if (currentOffset == endOffset) {
               }
               break;
            case 12:
               if (currentOffset + 1 == endOffset) {
                  this.addToken(tokens, 4);
                  starIsMultiplyOperator = true;
                  ++currentOffset;
               } else {
                  ch = data.charAt(currentOffset + 1);
                  if (ch == '.') {
                     this.addToken(tokens, 5);
                     starIsMultiplyOperator = true;
                     currentOffset += 2;
                  } else if (ch >= '0' && ch <= '9') {
                     this.addToken(tokens, 47);
                     starIsMultiplyOperator = true;
                     currentOffset = this.scanNumber(tokens, data, endOffset, currentOffset);
                  } else {
                     if (ch != '/') {
                        if (ch == '|') {
                           this.addToken(tokens, 4);
                           starIsMultiplyOperator = true;
                           ++currentOffset;
                           continue;
                        }

                        if (ch != ' ' && ch != '\n' && ch != '\t' && ch != '\r') {
                           throw new XPathException("c-general-xpath");
                        }

                        do {
                           ++currentOffset;
                           if (currentOffset == endOffset) {
                              break;
                           }

                           ch = data.charAt(currentOffset);
                        } while(ch == ' ' || ch == '\n' || ch == '\t' || ch == '\r');

                        if (currentOffset != endOffset && ch != '|' && ch != '/') {
                           throw new XPathException("c-general-xpath");
                        }

                        this.addToken(tokens, 4);
                        starIsMultiplyOperator = true;
                        continue;
                     }

                     this.addToken(tokens, 4);
                     starIsMultiplyOperator = true;
                     ++currentOffset;
                  }

                  if (currentOffset == endOffset) {
                  }
               }
               break;
            case 13:
               ++currentOffset;
               if (currentOffset == endOffset) {
                  this.addToken(tokens, 21);
                  starIsMultiplyOperator = false;
               } else {
                  ch = data.charAt(currentOffset);
                  if (ch == '/') {
                     this.addToken(tokens, 22);
                     starIsMultiplyOperator = false;
                     ++currentOffset;
                     if (currentOffset == endOffset) {
                     }
                  } else {
                     this.addToken(tokens, 21);
                     starIsMultiplyOperator = false;
                  }
               }
               break;
            case 14:
               this.addToken(tokens, 47);
               starIsMultiplyOperator = true;
               currentOffset = this.scanNumber(tokens, data, endOffset, currentOffset);
               break;
            case 15:
               ++currentOffset;
               if (currentOffset == endOffset) {
                  return false;
               }

               ch = data.charAt(currentOffset);
               if (ch != ':') {
                  return false;
               }

               this.addToken(tokens, 8);
               starIsMultiplyOperator = false;
               ++currentOffset;
               if (currentOffset == endOffset) {
               }
               break;
            case 16:
               ++currentOffset;
               if (currentOffset == endOffset) {
                  this.addToken(tokens, 28);
                  starIsMultiplyOperator = false;
               } else {
                  ch = data.charAt(currentOffset);
                  if (ch == '=') {
                     this.addToken(tokens, 29);
                     starIsMultiplyOperator = false;
                     ++currentOffset;
                     if (currentOffset == endOffset) {
                     }
                  } else {
                     this.addToken(tokens, 28);
                     starIsMultiplyOperator = false;
                  }
               }
               break;
            case 17:
               this.addToken(tokens, 26);
               starIsMultiplyOperator = false;
               ++currentOffset;
               if (currentOffset == endOffset) {
               }
               break;
            case 18:
               ++currentOffset;
               if (currentOffset == endOffset) {
                  this.addToken(tokens, 30);
                  starIsMultiplyOperator = false;
               } else {
                  ch = data.charAt(currentOffset);
                  if (ch == '=') {
                     this.addToken(tokens, 31);
                     starIsMultiplyOperator = false;
                     ++currentOffset;
                     if (currentOffset == endOffset) {
                     }
                  } else {
                     this.addToken(tokens, 30);
                     starIsMultiplyOperator = false;
                  }
               }
               break;
            case 19:
               this.addToken(tokens, 6);
               starIsMultiplyOperator = false;
               ++currentOffset;
               if (currentOffset == endOffset) {
               }
               break;
            case 20:
            case 23:
            case 25:
               nameOffset = currentOffset;
               currentOffset = this.scanNCName(data, endOffset, currentOffset);
               if (currentOffset == nameOffset) {
                  return false;
               }

               if (currentOffset < endOffset) {
                  ch = data.charAt(currentOffset);
               } else {
                  ch = -1;
               }

               nameHandle = symbolTable.addSymbol(data.substring(nameOffset, currentOffset));
               boolean isNameTestNCName = false;
               boolean isAxisName = false;
               prefixHandle = XMLSymbols.EMPTY_STRING;
               if (ch == 58) {
                  ++currentOffset;
                  if (currentOffset == endOffset) {
                     return false;
                  }

                  ch = data.charAt(currentOffset);
                  if (ch == 42) {
                     ++currentOffset;
                     if (currentOffset < endOffset) {
                        ch = data.charAt(currentOffset);
                     }

                     isNameTestNCName = true;
                  } else if (ch == 58) {
                     ++currentOffset;
                     if (currentOffset < endOffset) {
                        ch = data.charAt(currentOffset);
                     }

                     isAxisName = true;
                  } else {
                     prefixHandle = nameHandle;
                     nameOffset = currentOffset;
                     currentOffset = this.scanNCName(data, endOffset, currentOffset);
                     if (currentOffset == nameOffset) {
                        return false;
                     }

                     if (currentOffset < endOffset) {
                        ch = data.charAt(currentOffset);
                     } else {
                        ch = -1;
                     }

                     nameHandle = symbolTable.addSymbol(data.substring(nameOffset, currentOffset));
                  }
               }

               while(ch == 32 || ch == 10 || ch == 9 || ch == 13) {
                  ++currentOffset;
                  if (currentOffset == endOffset) {
                     break;
                  }

                  ch = data.charAt(currentOffset);
               }

               if (starIsMultiplyOperator) {
                  if (nameHandle == fAndSymbol) {
                     this.addToken(tokens, 16);
                     starIsMultiplyOperator = false;
                  } else if (nameHandle == fOrSymbol) {
                     this.addToken(tokens, 17);
                     starIsMultiplyOperator = false;
                  } else if (nameHandle == fModSymbol) {
                     this.addToken(tokens, 18);
                     starIsMultiplyOperator = false;
                  } else {
                     if (nameHandle != fDivSymbol) {
                        return false;
                     }

                     this.addToken(tokens, 19);
                     starIsMultiplyOperator = false;
                  }

                  if (isNameTestNCName) {
                     return false;
                  }

                  if (isAxisName) {
                     return false;
                  }
               } else if (ch == 40 && !isNameTestNCName && !isAxisName) {
                  if (nameHandle == fCommentSymbol) {
                     this.addToken(tokens, 12);
                  } else if (nameHandle == fTextSymbol) {
                     this.addToken(tokens, 13);
                  } else if (nameHandle == fPISymbol) {
                     this.addToken(tokens, 14);
                  } else if (nameHandle == fNodeSymbol) {
                     this.addToken(tokens, 15);
                  } else {
                     this.addToken(tokens, 32);
                     tokens.addToken(prefixHandle);
                     tokens.addToken(nameHandle);
                  }

                  this.addToken(tokens, 0);
                  starIsMultiplyOperator = false;
                  ++currentOffset;
                  if (currentOffset == endOffset) {
                  }
               } else if (isAxisName || ch == 58 && currentOffset + 1 < endOffset && data.charAt(currentOffset + 1) == ':') {
                  if (nameHandle == fAncestorSymbol) {
                     this.addToken(tokens, 33);
                  } else if (nameHandle == fAncestorOrSelfSymbol) {
                     this.addToken(tokens, 34);
                  } else if (nameHandle == fAttributeSymbol) {
                     this.addToken(tokens, 35);
                  } else if (nameHandle == fChildSymbol) {
                     this.addToken(tokens, 36);
                  } else if (nameHandle == fDescendantSymbol) {
                     this.addToken(tokens, 37);
                  } else if (nameHandle == fDescendantOrSelfSymbol) {
                     this.addToken(tokens, 38);
                  } else if (nameHandle == fFollowingSymbol) {
                     this.addToken(tokens, 39);
                  } else if (nameHandle == fFollowingSiblingSymbol) {
                     this.addToken(tokens, 40);
                  } else if (nameHandle == fNamespaceSymbol) {
                     this.addToken(tokens, 41);
                  } else if (nameHandle == fParentSymbol) {
                     this.addToken(tokens, 42);
                  } else if (nameHandle == fPrecedingSymbol) {
                     this.addToken(tokens, 43);
                  } else if (nameHandle == fPrecedingSiblingSymbol) {
                     this.addToken(tokens, 44);
                  } else {
                     if (nameHandle != fSelfSymbol) {
                        return false;
                     }

                     this.addToken(tokens, 45);
                  }

                  if (isNameTestNCName) {
                     return false;
                  }

                  this.addToken(tokens, 8);
                  starIsMultiplyOperator = false;
                  if (!isAxisName) {
                     ++currentOffset;
                     ++currentOffset;
                     if (currentOffset == endOffset) {
                     }
                  }
               } else if (isNameTestNCName) {
                  this.addToken(tokens, 10);
                  starIsMultiplyOperator = true;
                  tokens.addToken(nameHandle);
               } else {
                  this.addToken(tokens, 11);
                  starIsMultiplyOperator = true;
                  tokens.addToken(prefixHandle);
                  tokens.addToken(nameHandle);
               }
               break;
            case 21:
               this.addToken(tokens, 2);
               starIsMultiplyOperator = false;
               ++currentOffset;
               if (currentOffset == endOffset) {
               }
               break;
            case 22:
               this.addToken(tokens, 3);
               starIsMultiplyOperator = true;
               ++currentOffset;
               if (currentOffset == endOffset) {
               }
               break;
            case 24:
               this.addToken(tokens, 23);
               starIsMultiplyOperator = false;
               ++currentOffset;
               if (currentOffset == endOffset) {
               }
            }
         }

         return true;
      }

      int scanNCName(String data, int endOffset, int currentOffset) {
         int ch = data.charAt(currentOffset);
         byte chartype;
         if (ch >= 128) {
            if (!XMLChar.isNameStart(ch)) {
               return currentOffset;
            }
         } else {
            chartype = fASCIICharMap[ch];
            if (chartype != 20 && chartype != 23) {
               return currentOffset;
            }
         }

         while(true) {
            ++currentOffset;
            if (currentOffset >= endOffset) {
               break;
            }

            ch = data.charAt(currentOffset);
            if (ch >= 128) {
               if (!XMLChar.isName(ch)) {
                  break;
               }
            } else {
               chartype = fASCIICharMap[ch];
               if (chartype != 20 && chartype != 14 && chartype != 12 && chartype != 11 && chartype != 23) {
                  break;
               }
            }
         }

         return currentOffset;
      }

      private int scanNumber(XPath.Tokens tokens, String data, int endOffset, int currentOffset) {
         int ch = data.charAt(currentOffset);
         int whole = 0;

         int part;
         for(part = 0; ch >= '0' && ch <= '9'; ch = data.charAt(currentOffset)) {
            whole = whole * 10 + (ch - 48);
            ++currentOffset;
            if (currentOffset == endOffset) {
               break;
            }
         }

         if (ch == '.') {
            ++currentOffset;
            if (currentOffset < endOffset) {
               for(ch = data.charAt(currentOffset); ch >= '0' && ch <= '9'; ch = data.charAt(currentOffset)) {
                  part = part * 10 + (ch - 48);
                  ++currentOffset;
                  if (currentOffset == endOffset) {
                     break;
                  }
               }

               if (part != 0) {
                  throw new RuntimeException("find a solution!");
               }
            }
         }

         tokens.addToken(whole);
         tokens.addToken(part);
         return currentOffset;
      }

      protected void addToken(XPath.Tokens tokens, int token) throws XPathException {
         tokens.addToken(token);
      }
   }

   private static final class Tokens {
      static final boolean DUMP_TOKENS = false;
      public static final int EXPRTOKEN_OPEN_PAREN = 0;
      public static final int EXPRTOKEN_CLOSE_PAREN = 1;
      public static final int EXPRTOKEN_OPEN_BRACKET = 2;
      public static final int EXPRTOKEN_CLOSE_BRACKET = 3;
      public static final int EXPRTOKEN_PERIOD = 4;
      public static final int EXPRTOKEN_DOUBLE_PERIOD = 5;
      public static final int EXPRTOKEN_ATSIGN = 6;
      public static final int EXPRTOKEN_COMMA = 7;
      public static final int EXPRTOKEN_DOUBLE_COLON = 8;
      public static final int EXPRTOKEN_NAMETEST_ANY = 9;
      public static final int EXPRTOKEN_NAMETEST_NAMESPACE = 10;
      public static final int EXPRTOKEN_NAMETEST_QNAME = 11;
      public static final int EXPRTOKEN_NODETYPE_COMMENT = 12;
      public static final int EXPRTOKEN_NODETYPE_TEXT = 13;
      public static final int EXPRTOKEN_NODETYPE_PI = 14;
      public static final int EXPRTOKEN_NODETYPE_NODE = 15;
      public static final int EXPRTOKEN_OPERATOR_AND = 16;
      public static final int EXPRTOKEN_OPERATOR_OR = 17;
      public static final int EXPRTOKEN_OPERATOR_MOD = 18;
      public static final int EXPRTOKEN_OPERATOR_DIV = 19;
      public static final int EXPRTOKEN_OPERATOR_MULT = 20;
      public static final int EXPRTOKEN_OPERATOR_SLASH = 21;
      public static final int EXPRTOKEN_OPERATOR_DOUBLE_SLASH = 22;
      public static final int EXPRTOKEN_OPERATOR_UNION = 23;
      public static final int EXPRTOKEN_OPERATOR_PLUS = 24;
      public static final int EXPRTOKEN_OPERATOR_MINUS = 25;
      public static final int EXPRTOKEN_OPERATOR_EQUAL = 26;
      public static final int EXPRTOKEN_OPERATOR_NOT_EQUAL = 27;
      public static final int EXPRTOKEN_OPERATOR_LESS = 28;
      public static final int EXPRTOKEN_OPERATOR_LESS_EQUAL = 29;
      public static final int EXPRTOKEN_OPERATOR_GREATER = 30;
      public static final int EXPRTOKEN_OPERATOR_GREATER_EQUAL = 31;
      public static final int EXPRTOKEN_FUNCTION_NAME = 32;
      public static final int EXPRTOKEN_AXISNAME_ANCESTOR = 33;
      public static final int EXPRTOKEN_AXISNAME_ANCESTOR_OR_SELF = 34;
      public static final int EXPRTOKEN_AXISNAME_ATTRIBUTE = 35;
      public static final int EXPRTOKEN_AXISNAME_CHILD = 36;
      public static final int EXPRTOKEN_AXISNAME_DESCENDANT = 37;
      public static final int EXPRTOKEN_AXISNAME_DESCENDANT_OR_SELF = 38;
      public static final int EXPRTOKEN_AXISNAME_FOLLOWING = 39;
      public static final int EXPRTOKEN_AXISNAME_FOLLOWING_SIBLING = 40;
      public static final int EXPRTOKEN_AXISNAME_NAMESPACE = 41;
      public static final int EXPRTOKEN_AXISNAME_PARENT = 42;
      public static final int EXPRTOKEN_AXISNAME_PRECEDING = 43;
      public static final int EXPRTOKEN_AXISNAME_PRECEDING_SIBLING = 44;
      public static final int EXPRTOKEN_AXISNAME_SELF = 45;
      public static final int EXPRTOKEN_LITERAL = 46;
      public static final int EXPRTOKEN_NUMBER = 47;
      public static final int EXPRTOKEN_VARIABLE_REFERENCE = 48;
      private static final String[] fgTokenNames = new String[]{"EXPRTOKEN_OPEN_PAREN", "EXPRTOKEN_CLOSE_PAREN", "EXPRTOKEN_OPEN_BRACKET", "EXPRTOKEN_CLOSE_BRACKET", "EXPRTOKEN_PERIOD", "EXPRTOKEN_DOUBLE_PERIOD", "EXPRTOKEN_ATSIGN", "EXPRTOKEN_COMMA", "EXPRTOKEN_DOUBLE_COLON", "EXPRTOKEN_NAMETEST_ANY", "EXPRTOKEN_NAMETEST_NAMESPACE", "EXPRTOKEN_NAMETEST_QNAME", "EXPRTOKEN_NODETYPE_COMMENT", "EXPRTOKEN_NODETYPE_TEXT", "EXPRTOKEN_NODETYPE_PI", "EXPRTOKEN_NODETYPE_NODE", "EXPRTOKEN_OPERATOR_AND", "EXPRTOKEN_OPERATOR_OR", "EXPRTOKEN_OPERATOR_MOD", "EXPRTOKEN_OPERATOR_DIV", "EXPRTOKEN_OPERATOR_MULT", "EXPRTOKEN_OPERATOR_SLASH", "EXPRTOKEN_OPERATOR_DOUBLE_SLASH", "EXPRTOKEN_OPERATOR_UNION", "EXPRTOKEN_OPERATOR_PLUS", "EXPRTOKEN_OPERATOR_MINUS", "EXPRTOKEN_OPERATOR_EQUAL", "EXPRTOKEN_OPERATOR_NOT_EQUAL", "EXPRTOKEN_OPERATOR_LESS", "EXPRTOKEN_OPERATOR_LESS_EQUAL", "EXPRTOKEN_OPERATOR_GREATER", "EXPRTOKEN_OPERATOR_GREATER_EQUAL", "EXPRTOKEN_FUNCTION_NAME", "EXPRTOKEN_AXISNAME_ANCESTOR", "EXPRTOKEN_AXISNAME_ANCESTOR_OR_SELF", "EXPRTOKEN_AXISNAME_ATTRIBUTE", "EXPRTOKEN_AXISNAME_CHILD", "EXPRTOKEN_AXISNAME_DESCENDANT", "EXPRTOKEN_AXISNAME_DESCENDANT_OR_SELF", "EXPRTOKEN_AXISNAME_FOLLOWING", "EXPRTOKEN_AXISNAME_FOLLOWING_SIBLING", "EXPRTOKEN_AXISNAME_NAMESPACE", "EXPRTOKEN_AXISNAME_PARENT", "EXPRTOKEN_AXISNAME_PRECEDING", "EXPRTOKEN_AXISNAME_PRECEDING_SIBLING", "EXPRTOKEN_AXISNAME_SELF", "EXPRTOKEN_LITERAL", "EXPRTOKEN_NUMBER", "EXPRTOKEN_VARIABLE_REFERENCE"};
      private static final int INITIAL_TOKEN_COUNT = 256;
      private int[] fTokens = new int[256];
      private int fTokenCount = 0;
      private SymbolTable fSymbolTable;
      private Map<String, Integer> fSymbolMapping = new HashMap();
      private Map<Integer, String> fTokenNames = new HashMap();
      private int fCurrentTokenIndex;

      public Tokens(SymbolTable symbolTable) {
         this.fSymbolTable = symbolTable;
         String[] symbols = new String[]{"ancestor", "ancestor-or-self", "attribute", "child", "descendant", "descendant-or-self", "following", "following-sibling", "namespace", "parent", "preceding", "preceding-sibling", "self"};

         for(int i = 0; i < symbols.length; ++i) {
            this.fSymbolMapping.put(this.fSymbolTable.addSymbol(symbols[i]), i);
         }

         this.fTokenNames.put(0, "EXPRTOKEN_OPEN_PAREN");
         this.fTokenNames.put(1, "EXPRTOKEN_CLOSE_PAREN");
         this.fTokenNames.put(2, "EXPRTOKEN_OPEN_BRACKET");
         this.fTokenNames.put(3, "EXPRTOKEN_CLOSE_BRACKET");
         this.fTokenNames.put(4, "EXPRTOKEN_PERIOD");
         this.fTokenNames.put(5, "EXPRTOKEN_DOUBLE_PERIOD");
         this.fTokenNames.put(6, "EXPRTOKEN_ATSIGN");
         this.fTokenNames.put(7, "EXPRTOKEN_COMMA");
         this.fTokenNames.put(8, "EXPRTOKEN_DOUBLE_COLON");
         this.fTokenNames.put(9, "EXPRTOKEN_NAMETEST_ANY");
         this.fTokenNames.put(10, "EXPRTOKEN_NAMETEST_NAMESPACE");
         this.fTokenNames.put(11, "EXPRTOKEN_NAMETEST_QNAME");
         this.fTokenNames.put(12, "EXPRTOKEN_NODETYPE_COMMENT");
         this.fTokenNames.put(13, "EXPRTOKEN_NODETYPE_TEXT");
         this.fTokenNames.put(14, "EXPRTOKEN_NODETYPE_PI");
         this.fTokenNames.put(15, "EXPRTOKEN_NODETYPE_NODE");
         this.fTokenNames.put(16, "EXPRTOKEN_OPERATOR_AND");
         this.fTokenNames.put(17, "EXPRTOKEN_OPERATOR_OR");
         this.fTokenNames.put(18, "EXPRTOKEN_OPERATOR_MOD");
         this.fTokenNames.put(19, "EXPRTOKEN_OPERATOR_DIV");
         this.fTokenNames.put(20, "EXPRTOKEN_OPERATOR_MULT");
         this.fTokenNames.put(21, "EXPRTOKEN_OPERATOR_SLASH");
         this.fTokenNames.put(22, "EXPRTOKEN_OPERATOR_DOUBLE_SLASH");
         this.fTokenNames.put(23, "EXPRTOKEN_OPERATOR_UNION");
         this.fTokenNames.put(24, "EXPRTOKEN_OPERATOR_PLUS");
         this.fTokenNames.put(25, "EXPRTOKEN_OPERATOR_MINUS");
         this.fTokenNames.put(26, "EXPRTOKEN_OPERATOR_EQUAL");
         this.fTokenNames.put(27, "EXPRTOKEN_OPERATOR_NOT_EQUAL");
         this.fTokenNames.put(28, "EXPRTOKEN_OPERATOR_LESS");
         this.fTokenNames.put(29, "EXPRTOKEN_OPERATOR_LESS_EQUAL");
         this.fTokenNames.put(30, "EXPRTOKEN_OPERATOR_GREATER");
         this.fTokenNames.put(31, "EXPRTOKEN_OPERATOR_GREATER_EQUAL");
         this.fTokenNames.put(32, "EXPRTOKEN_FUNCTION_NAME");
         this.fTokenNames.put(33, "EXPRTOKEN_AXISNAME_ANCESTOR");
         this.fTokenNames.put(34, "EXPRTOKEN_AXISNAME_ANCESTOR_OR_SELF");
         this.fTokenNames.put(35, "EXPRTOKEN_AXISNAME_ATTRIBUTE");
         this.fTokenNames.put(36, "EXPRTOKEN_AXISNAME_CHILD");
         this.fTokenNames.put(37, "EXPRTOKEN_AXISNAME_DESCENDANT");
         this.fTokenNames.put(38, "EXPRTOKEN_AXISNAME_DESCENDANT_OR_SELF");
         this.fTokenNames.put(39, "EXPRTOKEN_AXISNAME_FOLLOWING");
         this.fTokenNames.put(40, "EXPRTOKEN_AXISNAME_FOLLOWING_SIBLING");
         this.fTokenNames.put(41, "EXPRTOKEN_AXISNAME_NAMESPACE");
         this.fTokenNames.put(42, "EXPRTOKEN_AXISNAME_PARENT");
         this.fTokenNames.put(43, "EXPRTOKEN_AXISNAME_PRECEDING");
         this.fTokenNames.put(44, "EXPRTOKEN_AXISNAME_PRECEDING_SIBLING");
         this.fTokenNames.put(45, "EXPRTOKEN_AXISNAME_SELF");
         this.fTokenNames.put(46, "EXPRTOKEN_LITERAL");
         this.fTokenNames.put(47, "EXPRTOKEN_NUMBER");
         this.fTokenNames.put(48, "EXPRTOKEN_VARIABLE_REFERENCE");
      }

      public String getTokenString(int token) {
         return (String)this.fTokenNames.get(token);
      }

      public void addToken(String tokenStr) {
         Integer tokenInt = null;
         Iterator var3 = this.fTokenNames.entrySet().iterator();

         while(var3.hasNext()) {
            Map.Entry<Integer, String> entry = (Map.Entry)var3.next();
            if (((String)entry.getValue()).equals(tokenStr)) {
               tokenInt = (Integer)entry.getKey();
            }
         }

         if (tokenInt == null) {
            tokenInt = this.fTokenNames.size();
            this.fTokenNames.put(tokenInt, tokenStr);
         }

         this.addToken(tokenInt);
      }

      public void addToken(int token) {
         try {
            this.fTokens[this.fTokenCount] = token;
         } catch (ArrayIndexOutOfBoundsException var4) {
            int[] oldList = this.fTokens;
            this.fTokens = new int[this.fTokenCount << 1];
            System.arraycopy(oldList, 0, this.fTokens, 0, this.fTokenCount);
            this.fTokens[this.fTokenCount] = token;
         }

         ++this.fTokenCount;
      }

      public void rewind() {
         this.fCurrentTokenIndex = 0;
      }

      public boolean hasMore() {
         return this.fCurrentTokenIndex < this.fTokenCount;
      }

      public int nextToken() throws XPathException {
         if (this.fCurrentTokenIndex == this.fTokenCount) {
            throw new XPathException("c-general-xpath");
         } else {
            return this.fTokens[this.fCurrentTokenIndex++];
         }
      }

      public int peekToken() throws XPathException {
         if (this.fCurrentTokenIndex == this.fTokenCount) {
            throw new XPathException("c-general-xpath");
         } else {
            return this.fTokens[this.fCurrentTokenIndex];
         }
      }

      public String nextTokenAsString() throws XPathException {
         String s = this.getTokenString(this.nextToken());
         if (s == null) {
            throw new XPathException("c-general-xpath");
         } else {
            return s;
         }
      }

      public void dumpTokens() {
         for(int i = 0; i < this.fTokenCount; ++i) {
            PrintStream var10000;
            StringBuilder var10001;
            switch(this.fTokens[i]) {
            case 0:
               System.out.print("<OPEN_PAREN/>");
               break;
            case 1:
               System.out.print("<CLOSE_PAREN/>");
               break;
            case 2:
               System.out.print("<OPEN_BRACKET/>");
               break;
            case 3:
               System.out.print("<CLOSE_BRACKET/>");
               break;
            case 4:
               System.out.print("<PERIOD/>");
               break;
            case 5:
               System.out.print("<DOUBLE_PERIOD/>");
               break;
            case 6:
               System.out.print("<ATSIGN/>");
               break;
            case 7:
               System.out.print("<COMMA/>");
               break;
            case 8:
               System.out.print("<DOUBLE_COLON/>");
               break;
            case 9:
               System.out.print("<NAMETEST_ANY/>");
               break;
            case 10:
               System.out.print("<NAMETEST_NAMESPACE");
               var10000 = System.out;
               var10001 = (new StringBuilder()).append(" prefix=\"");
               ++i;
               var10000.print(var10001.append(this.getTokenString(this.fTokens[i])).append("\"").toString());
               System.out.print("/>");
               break;
            case 11:
               System.out.print("<NAMETEST_QNAME");
               ++i;
               if (this.fTokens[i] != -1) {
                  System.out.print(" prefix=\"" + this.getTokenString(this.fTokens[i]) + "\"");
               }

               var10000 = System.out;
               var10001 = (new StringBuilder()).append(" localpart=\"");
               ++i;
               var10000.print(var10001.append(this.getTokenString(this.fTokens[i])).append("\"").toString());
               System.out.print("/>");
               break;
            case 12:
               System.out.print("<NODETYPE_COMMENT/>");
               break;
            case 13:
               System.out.print("<NODETYPE_TEXT/>");
               break;
            case 14:
               System.out.print("<NODETYPE_PI/>");
               break;
            case 15:
               System.out.print("<NODETYPE_NODE/>");
               break;
            case 16:
               System.out.print("<OPERATOR_AND/>");
               break;
            case 17:
               System.out.print("<OPERATOR_OR/>");
               break;
            case 18:
               System.out.print("<OPERATOR_MOD/>");
               break;
            case 19:
               System.out.print("<OPERATOR_DIV/>");
               break;
            case 20:
               System.out.print("<OPERATOR_MULT/>");
               break;
            case 21:
               System.out.print("<OPERATOR_SLASH/>");
               if (i + 1 < this.fTokenCount) {
                  System.out.println();
                  System.out.print("  ");
               }
               break;
            case 22:
               System.out.print("<OPERATOR_DOUBLE_SLASH/>");
               break;
            case 23:
               System.out.print("<OPERATOR_UNION/>");
               break;
            case 24:
               System.out.print("<OPERATOR_PLUS/>");
               break;
            case 25:
               System.out.print("<OPERATOR_MINUS/>");
               break;
            case 26:
               System.out.print("<OPERATOR_EQUAL/>");
               break;
            case 27:
               System.out.print("<OPERATOR_NOT_EQUAL/>");
               break;
            case 28:
               System.out.print("<OPERATOR_LESS/>");
               break;
            case 29:
               System.out.print("<OPERATOR_LESS_EQUAL/>");
               break;
            case 30:
               System.out.print("<OPERATOR_GREATER/>");
               break;
            case 31:
               System.out.print("<OPERATOR_GREATER_EQUAL/>");
               break;
            case 32:
               System.out.print("<FUNCTION_NAME");
               ++i;
               if (this.fTokens[i] != -1) {
                  System.out.print(" prefix=\"" + this.getTokenString(this.fTokens[i]) + "\"");
               }

               var10000 = System.out;
               var10001 = (new StringBuilder()).append(" localpart=\"");
               ++i;
               var10000.print(var10001.append(this.getTokenString(this.fTokens[i])).append("\"").toString());
               System.out.print("/>");
               break;
            case 33:
               System.out.print("<AXISNAME_ANCESTOR/>");
               break;
            case 34:
               System.out.print("<AXISNAME_ANCESTOR_OR_SELF/>");
               break;
            case 35:
               System.out.print("<AXISNAME_ATTRIBUTE/>");
               break;
            case 36:
               System.out.print("<AXISNAME_CHILD/>");
               break;
            case 37:
               System.out.print("<AXISNAME_DESCENDANT/>");
               break;
            case 38:
               System.out.print("<AXISNAME_DESCENDANT_OR_SELF/>");
               break;
            case 39:
               System.out.print("<AXISNAME_FOLLOWING/>");
               break;
            case 40:
               System.out.print("<AXISNAME_FOLLOWING_SIBLING/>");
               break;
            case 41:
               System.out.print("<AXISNAME_NAMESPACE/>");
               break;
            case 42:
               System.out.print("<AXISNAME_PARENT/>");
               break;
            case 43:
               System.out.print("<AXISNAME_PRECEDING/>");
               break;
            case 44:
               System.out.print("<AXISNAME_PRECEDING_SIBLING/>");
               break;
            case 45:
               System.out.print("<AXISNAME_SELF/>");
               break;
            case 46:
               System.out.print("<LITERAL");
               var10000 = System.out;
               var10001 = (new StringBuilder()).append(" value=\"");
               ++i;
               var10000.print(var10001.append(this.getTokenString(this.fTokens[i])).append("\"").toString());
               System.out.print("/>");
               break;
            case 47:
               System.out.print("<NUMBER");
               var10000 = System.out;
               var10001 = (new StringBuilder()).append(" whole=\"");
               ++i;
               var10000.print(var10001.append(this.getTokenString(this.fTokens[i])).append("\"").toString());
               var10000 = System.out;
               var10001 = (new StringBuilder()).append(" part=\"");
               ++i;
               var10000.print(var10001.append(this.getTokenString(this.fTokens[i])).append("\"").toString());
               System.out.print("/>");
               break;
            case 48:
               System.out.print("<VARIABLE_REFERENCE");
               ++i;
               if (this.fTokens[i] != -1) {
                  System.out.print(" prefix=\"" + this.getTokenString(this.fTokens[i]) + "\"");
               }

               var10000 = System.out;
               var10001 = (new StringBuilder()).append(" localpart=\"");
               ++i;
               var10000.print(var10001.append(this.getTokenString(this.fTokens[i])).append("\"").toString());
               System.out.print("/>");
               break;
            default:
               System.out.println("<???/>");
            }
         }

         System.out.println();
      }
   }

   public static class NodeTest implements Cloneable {
      public static final short QNAME = 1;
      public static final short WILDCARD = 2;
      public static final short NODE = 3;
      public static final short NAMESPACE = 4;
      public short type;
      public final QName name = new QName();

      public NodeTest(short type) {
         this.type = type;
      }

      public NodeTest(QName name) {
         this.type = 1;
         this.name.setValues(name);
      }

      public NodeTest(String prefix, String uri) {
         this.type = 4;
         this.name.setValues(prefix, (String)null, (String)null, uri);
      }

      public NodeTest(XPath.NodeTest nodeTest) {
         this.type = nodeTest.type;
         this.name.setValues(nodeTest.name);
      }

      public String toString() {
         switch(this.type) {
         case 1:
            if (this.name.prefix.length() != 0) {
               if (this.name.uri != null) {
                  return this.name.prefix + ':' + this.name.localpart;
               }

               return "{" + this.name.uri + '}' + this.name.prefix + ':' + this.name.localpart;
            }

            return this.name.localpart;
         case 2:
            return "*";
         case 3:
            return "node()";
         case 4:
            if (this.name.prefix.length() != 0) {
               if (this.name.uri != null) {
                  return this.name.prefix + ":*";
               }

               return "{" + this.name.uri + '}' + this.name.prefix + ":*";
            }

            return "???:*";
         default:
            return "???";
         }
      }

      public Object clone() {
         return new XPath.NodeTest(this);
      }
   }

   public static class Axis implements Cloneable {
      public static final short CHILD = 1;
      public static final short ATTRIBUTE = 2;
      public static final short SELF = 3;
      public static final short DESCENDANT = 4;
      public short type;

      public Axis(short type) {
         this.type = type;
      }

      protected Axis(XPath.Axis axis) {
         this.type = axis.type;
      }

      public String toString() {
         switch(this.type) {
         case 1:
            return "child";
         case 2:
            return "attribute";
         case 3:
            return "self";
         case 4:
            return "descendant";
         default:
            return "???";
         }
      }

      public Object clone() {
         return new XPath.Axis(this);
      }
   }

   public static class Step implements Cloneable {
      public XPath.Axis axis;
      public XPath.NodeTest nodeTest;

      public Step(XPath.Axis axis, XPath.NodeTest nodeTest) {
         this.axis = axis;
         this.nodeTest = nodeTest;
      }

      protected Step(XPath.Step step) {
         this.axis = (XPath.Axis)step.axis.clone();
         this.nodeTest = (XPath.NodeTest)step.nodeTest.clone();
      }

      public String toString() {
         if (this.axis.type == 3) {
            return ".";
         } else if (this.axis.type == 2) {
            return "@" + this.nodeTest.toString();
         } else if (this.axis.type == 1) {
            return this.nodeTest.toString();
         } else {
            return this.axis.type == 4 ? "//" : "??? (" + this.axis.type + ')';
         }
      }

      public Object clone() {
         return new XPath.Step(this);
      }
   }

   public static class LocationPath implements Cloneable {
      public XPath.Step[] steps;

      public LocationPath(XPath.Step[] steps) {
         this.steps = steps;
      }

      protected LocationPath(XPath.LocationPath path) {
         this.steps = new XPath.Step[path.steps.length];

         for(int i = 0; i < this.steps.length; ++i) {
            this.steps[i] = (XPath.Step)path.steps[i].clone();
         }

      }

      public String toString() {
         StringBuffer str = new StringBuffer();

         for(int i = 0; i < this.steps.length; ++i) {
            if (i > 0 && this.steps[i - 1].axis.type != 4 && this.steps[i].axis.type != 4) {
               str.append('/');
            }

            str.append(this.steps[i].toString());
         }

         return str.toString();
      }

      public Object clone() {
         return new XPath.LocationPath(this);
      }
   }
}
