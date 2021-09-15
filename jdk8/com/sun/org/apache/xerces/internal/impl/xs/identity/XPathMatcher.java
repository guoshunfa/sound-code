package com.sun.org.apache.xerces.internal.impl.xs.identity;

import com.sun.org.apache.xerces.internal.impl.xpath.XPath;
import com.sun.org.apache.xerces.internal.util.IntStack;
import com.sun.org.apache.xerces.internal.xni.QName;
import com.sun.org.apache.xerces.internal.xni.XMLAttributes;
import com.sun.org.apache.xerces.internal.xs.AttributePSVI;
import com.sun.org.apache.xerces.internal.xs.ShortList;
import com.sun.org.apache.xerces.internal.xs.XSTypeDefinition;

public class XPathMatcher {
   protected static final boolean DEBUG_ALL = false;
   protected static final boolean DEBUG_METHODS = false;
   protected static final boolean DEBUG_METHODS2 = false;
   protected static final boolean DEBUG_METHODS3 = false;
   protected static final boolean DEBUG_MATCH = false;
   protected static final boolean DEBUG_STACK = false;
   protected static final boolean DEBUG_ANY = false;
   protected static final int MATCHED = 1;
   protected static final int MATCHED_ATTRIBUTE = 3;
   protected static final int MATCHED_DESCENDANT = 5;
   protected static final int MATCHED_DESCENDANT_PREVIOUS = 13;
   private XPath.LocationPath[] fLocationPaths;
   private int[] fMatched;
   protected Object fMatchedString;
   private IntStack[] fStepIndexes;
   private int[] fCurrentStep;
   private int[] fNoMatchDepth;
   final QName fQName = new QName();

   public XPathMatcher(XPath xpath) {
      this.fLocationPaths = xpath.getLocationPaths();
      this.fStepIndexes = new IntStack[this.fLocationPaths.length];

      for(int i = 0; i < this.fStepIndexes.length; ++i) {
         this.fStepIndexes[i] = new IntStack();
      }

      this.fCurrentStep = new int[this.fLocationPaths.length];
      this.fNoMatchDepth = new int[this.fLocationPaths.length];
      this.fMatched = new int[this.fLocationPaths.length];
   }

   public boolean isMatched() {
      for(int i = 0; i < this.fLocationPaths.length; ++i) {
         if ((this.fMatched[i] & 1) == 1 && (this.fMatched[i] & 13) != 13 && (this.fNoMatchDepth[i] == 0 || (this.fMatched[i] & 5) == 5)) {
            return true;
         }
      }

      return false;
   }

   protected void handleContent(XSTypeDefinition type, boolean nillable, Object value, short valueType, ShortList itemValueType) {
   }

   protected void matched(Object actualValue, short valueType, ShortList itemValueType, boolean isNil) {
   }

   public void startDocumentFragment() {
      this.fMatchedString = null;

      for(int i = 0; i < this.fLocationPaths.length; ++i) {
         this.fStepIndexes[i].clear();
         this.fCurrentStep[i] = 0;
         this.fNoMatchDepth[i] = 0;
         this.fMatched[i] = 0;
      }

   }

   public void startElement(QName element, XMLAttributes attributes) {
      for(int i = 0; i < this.fLocationPaths.length; ++i) {
         int startStep = this.fCurrentStep[i];
         this.fStepIndexes[i].push(startStep);
         int var10002;
         if ((this.fMatched[i] & 5) != 1 && this.fNoMatchDepth[i] <= 0) {
            if ((this.fMatched[i] & 5) == 5) {
               this.fMatched[i] = 13;
            }

            XPath.Step[] steps;
            for(steps = this.fLocationPaths[i].steps; this.fCurrentStep[i] < steps.length && steps[this.fCurrentStep[i]].axis.type == 3; var10002 = this.fCurrentStep[i]++) {
            }

            if (this.fCurrentStep[i] == steps.length) {
               this.fMatched[i] = 1;
            } else {
               int descendantStep;
               for(descendantStep = this.fCurrentStep[i]; this.fCurrentStep[i] < steps.length && steps[this.fCurrentStep[i]].axis.type == 4; var10002 = this.fCurrentStep[i]++) {
               }

               boolean sawDescendant = this.fCurrentStep[i] > descendantStep;
               if (this.fCurrentStep[i] == steps.length) {
                  var10002 = this.fNoMatchDepth[i]++;
               } else {
                  XPath.NodeTest nodeTest;
                  if ((this.fCurrentStep[i] == startStep || this.fCurrentStep[i] > descendantStep) && steps[this.fCurrentStep[i]].axis.type == 1) {
                     XPath.Step step = steps[this.fCurrentStep[i]];
                     nodeTest = step.nodeTest;
                     if (nodeTest.type == 1 && !nodeTest.name.equals(element)) {
                        if (this.fCurrentStep[i] > descendantStep) {
                           this.fCurrentStep[i] = descendantStep;
                        } else {
                           var10002 = this.fNoMatchDepth[i]++;
                        }
                        continue;
                     }

                     var10002 = this.fCurrentStep[i]++;
                  }

                  if (this.fCurrentStep[i] == steps.length) {
                     if (sawDescendant) {
                        this.fCurrentStep[i] = descendantStep;
                        this.fMatched[i] = 5;
                     } else {
                        this.fMatched[i] = 1;
                     }
                  } else if (this.fCurrentStep[i] < steps.length && steps[this.fCurrentStep[i]].axis.type == 2) {
                     int attrCount = attributes.getLength();
                     if (attrCount > 0) {
                        nodeTest = steps[this.fCurrentStep[i]].nodeTest;

                        for(int aIndex = 0; aIndex < attrCount; ++aIndex) {
                           attributes.getName(aIndex, this.fQName);
                           if (nodeTest.type != 1 || nodeTest.name.equals(this.fQName)) {
                              var10002 = this.fCurrentStep[i]++;
                              if (this.fCurrentStep[i] == steps.length) {
                                 this.fMatched[i] = 3;

                                 int j;
                                 for(j = 0; j < i && (this.fMatched[j] & 1) != 1; ++j) {
                                 }

                                 if (j == i) {
                                    AttributePSVI attrPSVI = (AttributePSVI)attributes.getAugmentations(aIndex).getItem("ATTRIBUTE_PSVI");
                                    this.fMatchedString = attrPSVI.getActualNormalizedValue();
                                    this.matched(this.fMatchedString, attrPSVI.getActualNormalizedValueType(), attrPSVI.getItemValueTypes(), false);
                                 }
                              }
                              break;
                           }
                        }
                     }

                     if ((this.fMatched[i] & 1) != 1) {
                        if (this.fCurrentStep[i] > descendantStep) {
                           this.fCurrentStep[i] = descendantStep;
                        } else {
                           var10002 = this.fNoMatchDepth[i]++;
                        }
                     }
                  }
               }
            }
         } else {
            var10002 = this.fNoMatchDepth[i]++;
         }
      }

   }

   public void endElement(QName element, XSTypeDefinition type, boolean nillable, Object value, short valueType, ShortList itemValueType) {
      for(int i = 0; i < this.fLocationPaths.length; ++i) {
         this.fCurrentStep[i] = this.fStepIndexes[i].pop();
         if (this.fNoMatchDepth[i] > 0) {
            int var10002 = this.fNoMatchDepth[i]--;
         } else {
            int j;
            for(j = 0; j < i && (this.fMatched[j] & 1) != 1; ++j) {
            }

            if (j >= i && this.fMatched[j] != 0 && (this.fMatched[j] & 3) != 3) {
               this.handleContent(type, nillable, value, valueType, itemValueType);
               this.fMatched[i] = 0;
            }
         }
      }

   }

   public String toString() {
      StringBuffer str = new StringBuffer();
      String s = super.toString();
      int index2 = s.lastIndexOf(46);
      if (index2 != -1) {
         s = s.substring(index2 + 1);
      }

      str.append(s);

      for(int i = 0; i < this.fLocationPaths.length; ++i) {
         str.append('[');
         XPath.Step[] steps = this.fLocationPaths[i].steps;

         for(int j = 0; j < steps.length; ++j) {
            if (j == this.fCurrentStep[i]) {
               str.append('^');
            }

            str.append(steps[j].toString());
            if (j < steps.length - 1) {
               str.append('/');
            }
         }

         if (this.fCurrentStep[i] == steps.length) {
            str.append('^');
         }

         str.append(']');
         str.append(',');
      }

      return str.toString();
   }

   private String normalize(String s) {
      StringBuffer str = new StringBuffer();
      int length = s.length();

      for(int i = 0; i < length; ++i) {
         char c = s.charAt(i);
         switch(c) {
         case '\n':
            str.append("\\n");
            break;
         default:
            str.append(c);
         }
      }

      return str.toString();
   }
}
