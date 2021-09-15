package com.sun.org.apache.xerces.internal.impl.xs;

import com.sun.org.apache.xerces.internal.xni.QName;
import com.sun.org.apache.xerces.internal.xs.XSObjectList;
import com.sun.org.apache.xerces.internal.xs.XSSimpleTypeDefinition;
import com.sun.org.apache.xerces.internal.xs.XSTypeDefinition;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

public class SubstitutionGroupHandler {
   private static final XSElementDecl[] EMPTY_GROUP = new XSElementDecl[0];
   XSGrammarBucket fGrammarBucket;
   Map<XSElementDecl, Object> fSubGroupsB = new HashMap();
   private static final SubstitutionGroupHandler.OneSubGroup[] EMPTY_VECTOR = new SubstitutionGroupHandler.OneSubGroup[0];
   Map<XSElementDecl, XSElementDecl[]> fSubGroups = new HashMap();

   public SubstitutionGroupHandler(XSGrammarBucket grammarBucket) {
      this.fGrammarBucket = grammarBucket;
   }

   public XSElementDecl getMatchingElemDecl(QName element, XSElementDecl exemplar) {
      if (element.localpart == exemplar.fName && element.uri == exemplar.fTargetNamespace) {
         return exemplar;
      } else if (exemplar.fScope != 1) {
         return null;
      } else if ((exemplar.fBlock & 4) != 0) {
         return null;
      } else {
         SchemaGrammar sGrammar = this.fGrammarBucket.getGrammar(element.uri);
         if (sGrammar == null) {
            return null;
         } else {
            XSElementDecl eDecl = sGrammar.getGlobalElementDecl(element.localpart);
            if (eDecl == null) {
               return null;
            } else {
               return this.substitutionGroupOK(eDecl, exemplar, exemplar.fBlock) ? eDecl : null;
            }
         }
      }
   }

   protected boolean substitutionGroupOK(XSElementDecl element, XSElementDecl exemplar, short blockingConstraint) {
      if (element == exemplar) {
         return true;
      } else if ((blockingConstraint & 4) != 0) {
         return false;
      } else {
         XSElementDecl subGroup;
         for(subGroup = element.fSubGroup; subGroup != null && subGroup != exemplar; subGroup = subGroup.fSubGroup) {
         }

         return subGroup == null ? false : this.typeDerivationOK(element.fType, exemplar.fType, blockingConstraint);
      }
   }

   private boolean typeDerivationOK(XSTypeDefinition derived, XSTypeDefinition base, short blockingConstraint) {
      short devMethod = 0;
      short blockConstraint = blockingConstraint;
      Object type = derived;

      while(type != base && type != SchemaGrammar.fAnyType) {
         if (((XSTypeDefinition)type).getTypeCategory() == 15) {
            devMethod |= ((XSComplexTypeDecl)type).fDerivedBy;
         } else {
            devMethod = (short)(devMethod | 2);
         }

         type = ((XSTypeDefinition)type).getBaseType();
         if (type == null) {
            type = SchemaGrammar.fAnyType;
         }

         if (((XSTypeDefinition)type).getTypeCategory() == 15) {
            blockConstraint |= ((XSComplexTypeDecl)type).fBlock;
         }
      }

      if (type == base) {
         return (devMethod & blockConstraint) == 0;
      } else {
         if (base.getTypeCategory() == 16) {
            XSSimpleTypeDefinition st = (XSSimpleTypeDefinition)base;
            if (st.getVariety() == 3) {
               XSObjectList memberTypes = st.getMemberTypes();
               int length = memberTypes.getLength();

               for(int i = 0; i < length; ++i) {
                  if (this.typeDerivationOK(derived, (XSTypeDefinition)memberTypes.item(i), blockingConstraint)) {
                     return true;
                  }
               }
            }
         }

         return false;
      }
   }

   public boolean inSubstitutionGroup(XSElementDecl element, XSElementDecl exemplar) {
      return this.substitutionGroupOK(element, exemplar, exemplar.fBlock);
   }

   public void reset() {
      this.fSubGroupsB.clear();
      this.fSubGroups.clear();
   }

   public void addSubstitutionGroup(XSElementDecl[] elements) {
      for(int i = elements.length - 1; i >= 0; --i) {
         XSElementDecl element = elements[i];
         XSElementDecl subHead = element.fSubGroup;
         Vector subGroup = (Vector)this.fSubGroupsB.get(subHead);
         if (subGroup == null) {
            subGroup = new Vector();
            this.fSubGroupsB.put(subHead, subGroup);
         }

         subGroup.addElement(element);
      }

   }

   public XSElementDecl[] getSubstitutionGroup(XSElementDecl element) {
      XSElementDecl[] subGroup = (XSElementDecl[])this.fSubGroups.get(element);
      if (subGroup != null) {
         return subGroup;
      } else if ((element.fBlock & 4) != 0) {
         this.fSubGroups.put(element, EMPTY_GROUP);
         return EMPTY_GROUP;
      } else {
         SubstitutionGroupHandler.OneSubGroup[] groupB = this.getSubGroupB(element, new SubstitutionGroupHandler.OneSubGroup());
         int len = groupB.length;
         int rlen = 0;
         XSElementDecl[] ret = new XSElementDecl[len];

         for(int i = 0; i < len; ++i) {
            if ((element.fBlock & groupB[i].dMethod) == 0) {
               ret[rlen++] = groupB[i].sub;
            }
         }

         if (rlen < len) {
            XSElementDecl[] ret1 = new XSElementDecl[rlen];
            System.arraycopy(ret, 0, ret1, 0, rlen);
            ret = ret1;
         }

         this.fSubGroups.put(element, ret);
         return ret;
      }
   }

   private SubstitutionGroupHandler.OneSubGroup[] getSubGroupB(XSElementDecl element, SubstitutionGroupHandler.OneSubGroup methods) {
      Object subGroup = this.fSubGroupsB.get(element);
      if (subGroup == null) {
         this.fSubGroupsB.put(element, EMPTY_VECTOR);
         return EMPTY_VECTOR;
      } else if (subGroup instanceof SubstitutionGroupHandler.OneSubGroup[]) {
         return (SubstitutionGroupHandler.OneSubGroup[])((SubstitutionGroupHandler.OneSubGroup[])subGroup);
      } else {
         Vector group = (Vector)subGroup;
         Vector newGroup = new Vector();

         int j;
         for(int i = group.size() - 1; i >= 0; --i) {
            XSElementDecl sub = (XSElementDecl)group.elementAt(i);
            if (this.getDBMethods(sub.fType, element.fType, methods)) {
               short dMethod = methods.dMethod;
               short bMethod = methods.bMethod;
               newGroup.addElement(new SubstitutionGroupHandler.OneSubGroup(sub, methods.dMethod, methods.bMethod));
               SubstitutionGroupHandler.OneSubGroup[] group1 = this.getSubGroupB(sub, methods);

               for(j = group1.length - 1; j >= 0; --j) {
                  short dSubMethod = (short)(dMethod | group1[j].dMethod);
                  short bSubMethod = (short)(bMethod | group1[j].bMethod);
                  if ((dSubMethod & bSubMethod) == 0) {
                     newGroup.addElement(new SubstitutionGroupHandler.OneSubGroup(group1[j].sub, dSubMethod, bSubMethod));
                  }
               }
            }
         }

         SubstitutionGroupHandler.OneSubGroup[] ret = new SubstitutionGroupHandler.OneSubGroup[newGroup.size()];

         for(j = newGroup.size() - 1; j >= 0; --j) {
            ret[j] = (SubstitutionGroupHandler.OneSubGroup)newGroup.elementAt(j);
         }

         this.fSubGroupsB.put(element, ret);
         return ret;
      }
   }

   private boolean getDBMethods(XSTypeDefinition typed, XSTypeDefinition typeb, SubstitutionGroupHandler.OneSubGroup methods) {
      short dMethod = 0;
      short bMethod = 0;

      while(typed != typeb && typed != SchemaGrammar.fAnyType) {
         if (((XSTypeDefinition)typed).getTypeCategory() == 15) {
            dMethod |= ((XSComplexTypeDecl)typed).fDerivedBy;
         } else {
            dMethod = (short)(dMethod | 2);
         }

         typed = ((XSTypeDefinition)typed).getBaseType();
         if (typed == null) {
            typed = SchemaGrammar.fAnyType;
         }

         if (((XSTypeDefinition)typed).getTypeCategory() == 15) {
            bMethod |= ((XSComplexTypeDecl)typed).fBlock;
         }
      }

      if (typed == typeb && (dMethod & bMethod) == 0) {
         methods.dMethod = dMethod;
         methods.bMethod = bMethod;
         return true;
      } else {
         return false;
      }
   }

   private static final class OneSubGroup {
      XSElementDecl sub;
      short dMethod;
      short bMethod;

      OneSubGroup() {
      }

      OneSubGroup(XSElementDecl sub, short dMethod, short bMethod) {
         this.sub = sub;
         this.dMethod = dMethod;
         this.bMethod = bMethod;
      }
   }
}
