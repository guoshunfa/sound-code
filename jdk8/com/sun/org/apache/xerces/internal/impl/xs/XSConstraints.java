package com.sun.org.apache.xerces.internal.impl.xs;

import com.sun.org.apache.xerces.internal.impl.XMLErrorReporter;
import com.sun.org.apache.xerces.internal.impl.dv.InvalidDatatypeValueException;
import com.sun.org.apache.xerces.internal.impl.dv.ValidatedInfo;
import com.sun.org.apache.xerces.internal.impl.dv.ValidationContext;
import com.sun.org.apache.xerces.internal.impl.dv.XSSimpleType;
import com.sun.org.apache.xerces.internal.impl.xs.models.CMBuilder;
import com.sun.org.apache.xerces.internal.impl.xs.models.XSCMValidator;
import com.sun.org.apache.xerces.internal.impl.xs.util.SimpleLocator;
import com.sun.org.apache.xerces.internal.impl.xs.util.XSObjectListImpl;
import com.sun.org.apache.xerces.internal.util.SymbolHash;
import com.sun.org.apache.xerces.internal.xs.XSObjectList;
import com.sun.org.apache.xerces.internal.xs.XSTypeDefinition;
import java.util.Collections;
import java.util.Comparator;
import java.util.Vector;

public class XSConstraints {
   static final int OCCURRENCE_UNKNOWN = -2;
   static final XSSimpleType STRING_TYPE;
   private static XSParticleDecl fEmptyParticle;
   private static final Comparator ELEMENT_PARTICLE_COMPARATOR;

   public static XSParticleDecl getEmptySequence() {
      if (fEmptyParticle == null) {
         XSModelGroupImpl group = new XSModelGroupImpl();
         group.fCompositor = 102;
         group.fParticleCount = 0;
         group.fParticles = null;
         group.fAnnotations = XSObjectListImpl.EMPTY_LIST;
         XSParticleDecl particle = new XSParticleDecl();
         particle.fType = 3;
         particle.fValue = group;
         particle.fAnnotations = XSObjectListImpl.EMPTY_LIST;
         fEmptyParticle = particle;
      }

      return fEmptyParticle;
   }

   public static boolean checkTypeDerivationOk(XSTypeDefinition derived, XSTypeDefinition base, short block) {
      if (derived == SchemaGrammar.fAnyType) {
         return derived == base;
      } else if (derived != SchemaGrammar.fAnySimpleType) {
         if (derived.getTypeCategory() == 16) {
            if (((XSTypeDefinition)base).getTypeCategory() == 15) {
               if (base != SchemaGrammar.fAnyType) {
                  return false;
               }

               base = SchemaGrammar.fAnySimpleType;
            }

            return checkSimpleDerivation((XSSimpleType)derived, (XSSimpleType)base, block);
         } else {
            return checkComplexDerivation((XSComplexTypeDecl)derived, (XSTypeDefinition)base, block);
         }
      } else {
         return base == SchemaGrammar.fAnyType || base == SchemaGrammar.fAnySimpleType;
      }
   }

   public static boolean checkSimpleDerivationOk(XSSimpleType derived, XSTypeDefinition base, short block) {
      if (derived != SchemaGrammar.fAnySimpleType) {
         if (((XSTypeDefinition)base).getTypeCategory() == 15) {
            if (base != SchemaGrammar.fAnyType) {
               return false;
            }

            base = SchemaGrammar.fAnySimpleType;
         }

         return checkSimpleDerivation(derived, (XSSimpleType)base, block);
      } else {
         return base == SchemaGrammar.fAnyType || base == SchemaGrammar.fAnySimpleType;
      }
   }

   public static boolean checkComplexDerivationOk(XSComplexTypeDecl derived, XSTypeDefinition base, short block) {
      if (derived == SchemaGrammar.fAnyType) {
         return derived == base;
      } else {
         return checkComplexDerivation(derived, base, block);
      }
   }

   private static boolean checkSimpleDerivation(XSSimpleType derived, XSSimpleType base, short block) {
      if (derived == base) {
         return true;
      } else if ((block & 2) == 0 && (derived.getBaseType().getFinal() & 2) == 0) {
         XSSimpleType directBase = (XSSimpleType)derived.getBaseType();
         if (directBase == base) {
            return true;
         } else if (directBase != SchemaGrammar.fAnySimpleType && checkSimpleDerivation(directBase, base, block)) {
            return true;
         } else if ((derived.getVariety() == 2 || derived.getVariety() == 3) && base == SchemaGrammar.fAnySimpleType) {
            return true;
         } else {
            if (base.getVariety() == 3) {
               XSObjectList subUnionMemberDV = base.getMemberTypes();
               int subUnionSize = subUnionMemberDV.getLength();

               for(int i = 0; i < subUnionSize; ++i) {
                  base = (XSSimpleType)subUnionMemberDV.item(i);
                  if (checkSimpleDerivation(derived, base, block)) {
                     return true;
                  }
               }
            }

            return false;
         }
      } else {
         return false;
      }
   }

   private static boolean checkComplexDerivation(XSComplexTypeDecl derived, XSTypeDefinition base, short block) {
      if (derived == base) {
         return true;
      } else if ((derived.fDerivedBy & block) != 0) {
         return false;
      } else {
         XSTypeDefinition directBase = derived.fBaseType;
         if (directBase == base) {
            return true;
         } else if (directBase != SchemaGrammar.fAnyType && directBase != SchemaGrammar.fAnySimpleType) {
            if (directBase.getTypeCategory() == 15) {
               return checkComplexDerivation((XSComplexTypeDecl)directBase, (XSTypeDefinition)base, block);
            } else if (directBase.getTypeCategory() == 16) {
               if (((XSTypeDefinition)base).getTypeCategory() == 15) {
                  if (base != SchemaGrammar.fAnyType) {
                     return false;
                  }

                  base = SchemaGrammar.fAnySimpleType;
               }

               return checkSimpleDerivation((XSSimpleType)directBase, (XSSimpleType)base, block);
            } else {
               return false;
            }
         } else {
            return false;
         }
      }
   }

   public static Object ElementDefaultValidImmediate(XSTypeDefinition type, String value, ValidationContext context, ValidatedInfo vinfo) {
      XSSimpleType dv = null;
      XSComplexTypeDecl ctype;
      if (type.getTypeCategory() == 16) {
         dv = (XSSimpleType)type;
      } else {
         ctype = (XSComplexTypeDecl)type;
         if (ctype.fContentType == 1) {
            dv = ctype.fXSSimpleType;
         } else {
            if (ctype.fContentType != 3) {
               return null;
            }

            if (!((XSParticleDecl)ctype.getParticle()).emptiable()) {
               return null;
            }
         }
      }

      ctype = null;
      if (dv == null) {
         dv = STRING_TYPE;
      }

      try {
         Object actualValue = dv.validate(value, context, vinfo);
         if (vinfo != null) {
            actualValue = dv.validate(vinfo.stringValue(), context, vinfo);
         }

         return actualValue;
      } catch (InvalidDatatypeValueException var7) {
         return null;
      }
   }

   static void reportSchemaError(XMLErrorReporter errorReporter, SimpleLocator loc, String key, Object[] args) {
      if (loc != null) {
         errorReporter.reportError(loc, "http://www.w3.org/TR/xml-schema-1", key, args, (short)1);
      } else {
         errorReporter.reportError("http://www.w3.org/TR/xml-schema-1", key, args, (short)1);
      }

   }

   public static void fullSchemaChecking(XSGrammarBucket grammarBucket, SubstitutionGroupHandler SGHandler, CMBuilder cmBuilder, XMLErrorReporter errorReporter) {
      SchemaGrammar[] grammars = grammarBucket.getGrammars();

      for(int i = grammars.length - 1; i >= 0; --i) {
         SGHandler.addSubstitutionGroup(grammars[i].getSubstitutionGroups());
      }

      XSParticleDecl fakeDerived = new XSParticleDecl();
      XSParticleDecl fakeBase = new XSParticleDecl();
      fakeDerived.fType = 3;
      fakeBase.fType = 3;

      for(int g = grammars.length - 1; g >= 0; --g) {
         XSGroupDecl[] redefinedGroups = grammars[g].getRedefinedGroupDecls();
         SimpleLocator[] rgLocators = grammars[g].getRGLocators();
         int i = 0;

         while(i < redefinedGroups.length) {
            XSGroupDecl derivedGrp = redefinedGroups[i++];
            XSModelGroupImpl derivedMG = derivedGrp.fModelGroup;
            XSGroupDecl baseGrp = redefinedGroups[i++];
            XSModelGroupImpl baseMG = baseGrp.fModelGroup;
            fakeDerived.fValue = derivedMG;
            fakeBase.fValue = baseMG;
            if (baseMG == null) {
               if (derivedMG != null) {
                  reportSchemaError(errorReporter, rgLocators[i / 2 - 1], "src-redefine.6.2.2", new Object[]{derivedGrp.fName, "rcase-Recurse.2"});
               }
            } else if (derivedMG == null) {
               if (!fakeBase.emptiable()) {
                  reportSchemaError(errorReporter, rgLocators[i / 2 - 1], "src-redefine.6.2.2", new Object[]{derivedGrp.fName, "rcase-Recurse.2"});
               }
            } else {
               try {
                  particleValidRestriction(fakeDerived, SGHandler, fakeBase, SGHandler);
               } catch (XMLSchemaException var21) {
                  String key = var21.getKey();
                  reportSchemaError(errorReporter, rgLocators[i / 2 - 1], key, var21.getArgs());
                  reportSchemaError(errorReporter, rgLocators[i / 2 - 1], "src-redefine.6.2.2", new Object[]{derivedGrp.fName, key});
               }
            }
         }
      }

      SymbolHash elemTable = new SymbolHash();

      for(int i = grammars.length - 1; i >= 0; --i) {
         int keepType = 0;
         boolean fullChecked = grammars[i].fFullChecked;
         XSComplexTypeDecl[] types = grammars[i].getUncheckedComplexTypeDecls();
         SimpleLocator[] ctLocators = grammars[i].getUncheckedCTLocators();

         for(int j = 0; j < types.length; ++j) {
            if (!fullChecked && types[j].fParticle != null) {
               elemTable.clear();

               try {
                  checkElementDeclsConsistent(types[j], types[j].fParticle, elemTable, SGHandler);
               } catch (XMLSchemaException var18) {
                  reportSchemaError(errorReporter, ctLocators[j], var18.getKey(), var18.getArgs());
               }
            }

            if (types[j].fBaseType != null && types[j].fBaseType != SchemaGrammar.fAnyType && types[j].fDerivedBy == 2 && types[j].fBaseType instanceof XSComplexTypeDecl) {
               XSParticleDecl derivedParticle = types[j].fParticle;
               XSParticleDecl baseParticle = ((XSComplexTypeDecl)((XSComplexTypeDecl)types[j].fBaseType)).fParticle;
               if (derivedParticle == null) {
                  if (baseParticle != null && !baseParticle.emptiable()) {
                     reportSchemaError(errorReporter, ctLocators[j], "derivation-ok-restriction.5.3.2", new Object[]{types[j].fName, types[j].fBaseType.getName()});
                  }
               } else if (baseParticle != null) {
                  try {
                     particleValidRestriction(types[j].fParticle, SGHandler, ((XSComplexTypeDecl)((XSComplexTypeDecl)types[j].fBaseType)).fParticle, SGHandler);
                  } catch (XMLSchemaException var20) {
                     reportSchemaError(errorReporter, ctLocators[j], var20.getKey(), var20.getArgs());
                     reportSchemaError(errorReporter, ctLocators[j], "derivation-ok-restriction.5.4.2", new Object[]{types[j].fName});
                  }
               } else {
                  reportSchemaError(errorReporter, ctLocators[j], "derivation-ok-restriction.5.4.2", new Object[]{types[j].fName});
               }
            }

            XSCMValidator cm = types[j].getContentModel(cmBuilder);
            boolean further = false;
            if (cm != null) {
               try {
                  further = cm.checkUniqueParticleAttribution(SGHandler);
               } catch (XMLSchemaException var19) {
                  reportSchemaError(errorReporter, ctLocators[j], var19.getKey(), var19.getArgs());
               }
            }

            if (!fullChecked && further) {
               types[keepType++] = types[j];
            }
         }

         if (!fullChecked) {
            grammars[i].setUncheckedTypeNum(keepType);
            grammars[i].fFullChecked = true;
         }
      }

   }

   public static void checkElementDeclsConsistent(XSComplexTypeDecl type, XSParticleDecl particle, SymbolHash elemDeclHash, SubstitutionGroupHandler sgHandler) throws XMLSchemaException {
      int pType = particle.fType;
      if (pType != 2) {
         if (pType == 1) {
            XSElementDecl elem = (XSElementDecl)((XSElementDecl)particle.fValue);
            findElemInTable(type, elem, elemDeclHash);
            if (elem.fScope == 1) {
               XSElementDecl[] subGroup = sgHandler.getSubstitutionGroup(elem);

               for(int i = 0; i < subGroup.length; ++i) {
                  findElemInTable(type, subGroup[i], elemDeclHash);
               }
            }

         } else {
            XSModelGroupImpl group = (XSModelGroupImpl)particle.fValue;

            for(int i = 0; i < group.fParticleCount; ++i) {
               checkElementDeclsConsistent(type, group.fParticles[i], elemDeclHash, sgHandler);
            }

         }
      }
   }

   public static void findElemInTable(XSComplexTypeDecl type, XSElementDecl elem, SymbolHash elemDeclHash) throws XMLSchemaException {
      String name = elem.fName + "," + elem.fTargetNamespace;
      XSElementDecl existingElem = null;
      if ((existingElem = (XSElementDecl)((XSElementDecl)elemDeclHash.get(name))) == null) {
         elemDeclHash.put(name, elem);
      } else {
         if (elem == existingElem) {
            return;
         }

         if (elem.fType != existingElem.fType) {
            throw new XMLSchemaException("cos-element-consistent", new Object[]{type.fName, elem.fName});
         }
      }

   }

   private static boolean particleValidRestriction(XSParticleDecl dParticle, SubstitutionGroupHandler dSGHandler, XSParticleDecl bParticle, SubstitutionGroupHandler bSGHandler) throws XMLSchemaException {
      return particleValidRestriction(dParticle, dSGHandler, bParticle, bSGHandler, true);
   }

   private static boolean particleValidRestriction(XSParticleDecl dParticle, SubstitutionGroupHandler dSGHandler, XSParticleDecl bParticle, SubstitutionGroupHandler bSGHandler, boolean checkWCOccurrence) throws XMLSchemaException {
      Vector dChildren = null;
      Vector bChildren = null;
      int dMinEffectiveTotalRange = -2;
      int dMaxEffectiveTotalRange = -2;
      boolean bExpansionHappened = false;
      if (dParticle.isEmpty() && !bParticle.emptiable()) {
         throw new XMLSchemaException("cos-particle-restrict.a", (Object[])null);
      } else if (!dParticle.isEmpty() && bParticle.isEmpty()) {
         throw new XMLSchemaException("cos-particle-restrict.b", (Object[])null);
      } else {
         short dType = dParticle.fType;
         if (dType == 3) {
            dType = ((XSModelGroupImpl)dParticle.fValue).fCompositor;
            XSParticleDecl dtmp = getNonUnaryGroup(dParticle);
            if (dtmp != dParticle) {
               dParticle = dtmp;
               dType = dtmp.fType;
               if (dType == 3) {
                  dType = ((XSModelGroupImpl)dtmp.fValue).fCompositor;
               }
            }

            dChildren = removePointlessChildren(dParticle);
         }

         int dMinOccurs = dParticle.fMinOccurs;
         int dMaxOccurs = dParticle.fMaxOccurs;
         int bMaxOccurs;
         if (dSGHandler != null && dType == 1) {
            XSElementDecl dElement = (XSElementDecl)dParticle.fValue;
            if (dElement.fScope == 1) {
               XSElementDecl[] subGroup = dSGHandler.getSubstitutionGroup(dElement);
               if (subGroup.length > 0) {
                  dType = 101;
                  dMinEffectiveTotalRange = dMinOccurs;
                  dMaxEffectiveTotalRange = dMaxOccurs;
                  dChildren = new Vector(subGroup.length + 1);

                  for(bMaxOccurs = 0; bMaxOccurs < subGroup.length; ++bMaxOccurs) {
                     addElementToParticleVector(dChildren, subGroup[bMaxOccurs]);
                  }

                  addElementToParticleVector(dChildren, dElement);
                  Collections.sort(dChildren, ELEMENT_PARTICLE_COMPARATOR);
                  dSGHandler = null;
               }
            }
         }

         short bType = bParticle.fType;
         if (bType == 3) {
            bType = ((XSModelGroupImpl)bParticle.fValue).fCompositor;
            XSParticleDecl btmp = getNonUnaryGroup(bParticle);
            if (btmp != bParticle) {
               bParticle = btmp;
               bType = btmp.fType;
               if (bType == 3) {
                  bType = ((XSModelGroupImpl)btmp.fValue).fCompositor;
               }
            }

            bChildren = removePointlessChildren(bParticle);
         }

         int bMinOccurs = bParticle.fMinOccurs;
         bMaxOccurs = bParticle.fMaxOccurs;
         if (bSGHandler != null && bType == 1) {
            XSElementDecl bElement = (XSElementDecl)bParticle.fValue;
            if (bElement.fScope == 1) {
               XSElementDecl[] bsubGroup = bSGHandler.getSubstitutionGroup(bElement);
               if (bsubGroup.length > 0) {
                  bType = 101;
                  bChildren = new Vector(bsubGroup.length + 1);

                  for(int i = 0; i < bsubGroup.length; ++i) {
                     addElementToParticleVector(bChildren, bsubGroup[i]);
                  }

                  addElementToParticleVector(bChildren, bElement);
                  Collections.sort(bChildren, ELEMENT_PARTICLE_COMPARATOR);
                  bSGHandler = null;
                  bExpansionHappened = true;
               }
            }
         }

         switch(dType) {
         case 1:
            switch(bType) {
            case 1:
               checkNameAndTypeOK((XSElementDecl)dParticle.fValue, dMinOccurs, dMaxOccurs, (XSElementDecl)bParticle.fValue, bMinOccurs, bMaxOccurs);
               return bExpansionHappened;
            case 2:
               checkNSCompat((XSElementDecl)dParticle.fValue, dMinOccurs, dMaxOccurs, (XSWildcardDecl)bParticle.fValue, bMinOccurs, bMaxOccurs, checkWCOccurrence);
               return bExpansionHappened;
            case 101:
               dChildren = new Vector();
               dChildren.addElement(dParticle);
               checkRecurseLax(dChildren, 1, 1, dSGHandler, bChildren, bMinOccurs, bMaxOccurs, bSGHandler);
               return bExpansionHappened;
            case 102:
            case 103:
               dChildren = new Vector();
               dChildren.addElement(dParticle);
               checkRecurse(dChildren, 1, 1, dSGHandler, bChildren, bMinOccurs, bMaxOccurs, bSGHandler);
               return bExpansionHappened;
            default:
               throw new XMLSchemaException("Internal-Error", new Object[]{"in particleValidRestriction"});
            }
         case 2:
            switch(bType) {
            case 1:
            case 101:
            case 102:
            case 103:
               throw new XMLSchemaException("cos-particle-restrict.2", new Object[]{"any:choice,sequence,all,elt"});
            case 2:
               checkNSSubset((XSWildcardDecl)dParticle.fValue, dMinOccurs, dMaxOccurs, (XSWildcardDecl)bParticle.fValue, bMinOccurs, bMaxOccurs);
               return bExpansionHappened;
            default:
               throw new XMLSchemaException("Internal-Error", new Object[]{"in particleValidRestriction"});
            }
         case 101:
            switch(bType) {
            case 1:
            case 102:
            case 103:
               throw new XMLSchemaException("cos-particle-restrict.2", new Object[]{"choice:all,sequence,elt"});
            case 2:
               if (dMinEffectiveTotalRange == -2) {
                  dMinEffectiveTotalRange = dParticle.minEffectiveTotalRange();
               }

               if (dMaxEffectiveTotalRange == -2) {
                  dMaxEffectiveTotalRange = dParticle.maxEffectiveTotalRange();
               }

               checkNSRecurseCheckCardinality(dChildren, dMinEffectiveTotalRange, dMaxEffectiveTotalRange, dSGHandler, bParticle, bMinOccurs, bMaxOccurs, checkWCOccurrence);
               return bExpansionHappened;
            case 101:
               checkRecurseLax(dChildren, dMinOccurs, dMaxOccurs, dSGHandler, bChildren, bMinOccurs, bMaxOccurs, bSGHandler);
               return bExpansionHappened;
            default:
               throw new XMLSchemaException("Internal-Error", new Object[]{"in particleValidRestriction"});
            }
         case 102:
            switch(bType) {
            case 1:
               throw new XMLSchemaException("cos-particle-restrict.2", new Object[]{"seq:elt"});
            case 2:
               if (dMinEffectiveTotalRange == -2) {
                  dMinEffectiveTotalRange = dParticle.minEffectiveTotalRange();
               }

               if (dMaxEffectiveTotalRange == -2) {
                  dMaxEffectiveTotalRange = dParticle.maxEffectiveTotalRange();
               }

               checkNSRecurseCheckCardinality(dChildren, dMinEffectiveTotalRange, dMaxEffectiveTotalRange, dSGHandler, bParticle, bMinOccurs, bMaxOccurs, checkWCOccurrence);
               return bExpansionHappened;
            case 101:
               int min1 = dMinOccurs * dChildren.size();
               int max1 = dMaxOccurs == -1 ? dMaxOccurs : dMaxOccurs * dChildren.size();
               checkMapAndSum(dChildren, min1, max1, dSGHandler, bChildren, bMinOccurs, bMaxOccurs, bSGHandler);
               return bExpansionHappened;
            case 102:
               checkRecurse(dChildren, dMinOccurs, dMaxOccurs, dSGHandler, bChildren, bMinOccurs, bMaxOccurs, bSGHandler);
               return bExpansionHappened;
            case 103:
               checkRecurseUnordered(dChildren, dMinOccurs, dMaxOccurs, dSGHandler, bChildren, bMinOccurs, bMaxOccurs, bSGHandler);
               return bExpansionHappened;
            default:
               throw new XMLSchemaException("Internal-Error", new Object[]{"in particleValidRestriction"});
            }
         case 103:
            switch(bType) {
            case 1:
            case 101:
            case 102:
               throw new XMLSchemaException("cos-particle-restrict.2", new Object[]{"all:choice,sequence,elt"});
            case 2:
               if (dMinEffectiveTotalRange == -2) {
                  dMinEffectiveTotalRange = dParticle.minEffectiveTotalRange();
               }

               if (dMaxEffectiveTotalRange == -2) {
                  dMaxEffectiveTotalRange = dParticle.maxEffectiveTotalRange();
               }

               checkNSRecurseCheckCardinality(dChildren, dMinEffectiveTotalRange, dMaxEffectiveTotalRange, dSGHandler, bParticle, bMinOccurs, bMaxOccurs, checkWCOccurrence);
               return bExpansionHappened;
            case 103:
               checkRecurse(dChildren, dMinOccurs, dMaxOccurs, dSGHandler, bChildren, bMinOccurs, bMaxOccurs, bSGHandler);
               return bExpansionHappened;
            default:
               throw new XMLSchemaException("Internal-Error", new Object[]{"in particleValidRestriction"});
            }
         default:
            return bExpansionHappened;
         }
      }
   }

   private static void addElementToParticleVector(Vector v, XSElementDecl d) {
      XSParticleDecl p = new XSParticleDecl();
      p.fValue = d;
      p.fType = 1;
      v.addElement(p);
   }

   private static XSParticleDecl getNonUnaryGroup(XSParticleDecl p) {
      if (p.fType != 1 && p.fType != 2) {
         return p.fMinOccurs == 1 && p.fMaxOccurs == 1 && p.fValue != null && ((XSModelGroupImpl)p.fValue).fParticleCount == 1 ? getNonUnaryGroup(((XSModelGroupImpl)p.fValue).fParticles[0]) : p;
      } else {
         return p;
      }
   }

   private static Vector removePointlessChildren(XSParticleDecl p) {
      if (p.fType != 1 && p.fType != 2) {
         Vector children = new Vector();
         XSModelGroupImpl group = (XSModelGroupImpl)p.fValue;

         for(int i = 0; i < group.fParticleCount; ++i) {
            gatherChildren(group.fCompositor, group.fParticles[i], children);
         }

         return children;
      } else {
         return null;
      }
   }

   private static void gatherChildren(int parentType, XSParticleDecl p, Vector children) {
      int min = p.fMinOccurs;
      int max = p.fMaxOccurs;
      int type = p.fType;
      if (type == 3) {
         type = ((XSModelGroupImpl)p.fValue).fCompositor;
      }

      if (type != 1 && type != 2) {
         if (min == 1 && max == 1) {
            if (parentType == type) {
               XSModelGroupImpl group = (XSModelGroupImpl)p.fValue;

               for(int i = 0; i < group.fParticleCount; ++i) {
                  gatherChildren(type, group.fParticles[i], children);
               }
            } else if (!p.isEmpty()) {
               children.addElement(p);
            }
         } else {
            children.addElement(p);
         }

      } else {
         children.addElement(p);
      }
   }

   private static void checkNameAndTypeOK(XSElementDecl dElement, int dMin, int dMax, XSElementDecl bElement, int bMin, int bMax) throws XMLSchemaException {
      if (dElement.fName == bElement.fName && dElement.fTargetNamespace == bElement.fTargetNamespace) {
         if (!bElement.getNillable() && dElement.getNillable()) {
            throw new XMLSchemaException("rcase-NameAndTypeOK.2", new Object[]{dElement.fName});
         } else if (!checkOccurrenceRange(dMin, dMax, bMin, bMax)) {
            throw new XMLSchemaException("rcase-NameAndTypeOK.3", new Object[]{dElement.fName, Integer.toString(dMin), dMax == -1 ? "unbounded" : Integer.toString(dMax), Integer.toString(bMin), bMax == -1 ? "unbounded" : Integer.toString(bMax)});
         } else {
            if (bElement.getConstraintType() == 2) {
               if (dElement.getConstraintType() != 2) {
                  throw new XMLSchemaException("rcase-NameAndTypeOK.4.a", new Object[]{dElement.fName, bElement.fDefault.stringValue()});
               }

               boolean isSimple = false;
               if (dElement.fType.getTypeCategory() == 16 || ((XSComplexTypeDecl)dElement.fType).fContentType == 1) {
                  isSimple = true;
               }

               if (!isSimple && !bElement.fDefault.normalizedValue.equals(dElement.fDefault.normalizedValue) || isSimple && !bElement.fDefault.actualValue.equals(dElement.fDefault.actualValue)) {
                  throw new XMLSchemaException("rcase-NameAndTypeOK.4.b", new Object[]{dElement.fName, dElement.fDefault.stringValue(), bElement.fDefault.stringValue()});
               }
            }

            checkIDConstraintRestriction(dElement, bElement);
            int blockSet1 = dElement.fBlock;
            int blockSet2 = bElement.fBlock;
            if ((blockSet1 & blockSet2) != blockSet2 || blockSet1 == 0 && blockSet2 != 0) {
               throw new XMLSchemaException("rcase-NameAndTypeOK.6", new Object[]{dElement.fName});
            } else if (!checkTypeDerivationOk(dElement.fType, bElement.fType, (short)25)) {
               throw new XMLSchemaException("rcase-NameAndTypeOK.7", new Object[]{dElement.fName, dElement.fType.getName(), bElement.fType.getName()});
            }
         }
      } else {
         throw new XMLSchemaException("rcase-NameAndTypeOK.1", new Object[]{dElement.fName, dElement.fTargetNamespace, bElement.fName, bElement.fTargetNamespace});
      }
   }

   private static void checkIDConstraintRestriction(XSElementDecl derivedElemDecl, XSElementDecl baseElemDecl) throws XMLSchemaException {
   }

   private static boolean checkOccurrenceRange(int min1, int max1, int min2, int max2) {
      return min1 >= min2 && (max2 == -1 || max1 != -1 && max1 <= max2);
   }

   private static void checkNSCompat(XSElementDecl elem, int min1, int max1, XSWildcardDecl wildcard, int min2, int max2, boolean checkWCOccurrence) throws XMLSchemaException {
      if (checkWCOccurrence && !checkOccurrenceRange(min1, max1, min2, max2)) {
         throw new XMLSchemaException("rcase-NSCompat.2", new Object[]{elem.fName, Integer.toString(min1), max1 == -1 ? "unbounded" : Integer.toString(max1), Integer.toString(min2), max2 == -1 ? "unbounded" : Integer.toString(max2)});
      } else if (!wildcard.allowNamespace(elem.fTargetNamespace)) {
         throw new XMLSchemaException("rcase-NSCompat.1", new Object[]{elem.fName, elem.fTargetNamespace});
      }
   }

   private static void checkNSSubset(XSWildcardDecl dWildcard, int min1, int max1, XSWildcardDecl bWildcard, int min2, int max2) throws XMLSchemaException {
      if (!checkOccurrenceRange(min1, max1, min2, max2)) {
         throw new XMLSchemaException("rcase-NSSubset.2", new Object[]{Integer.toString(min1), max1 == -1 ? "unbounded" : Integer.toString(max1), Integer.toString(min2), max2 == -1 ? "unbounded" : Integer.toString(max2)});
      } else if (!dWildcard.isSubsetOf(bWildcard)) {
         throw new XMLSchemaException("rcase-NSSubset.1", (Object[])null);
      } else if (dWildcard.weakerProcessContents(bWildcard)) {
         throw new XMLSchemaException("rcase-NSSubset.3", new Object[]{dWildcard.getProcessContentsAsString(), bWildcard.getProcessContentsAsString()});
      }
   }

   private static void checkNSRecurseCheckCardinality(Vector children, int min1, int max1, SubstitutionGroupHandler dSGHandler, XSParticleDecl wildcard, int min2, int max2, boolean checkWCOccurrence) throws XMLSchemaException {
      if (checkWCOccurrence && !checkOccurrenceRange(min1, max1, min2, max2)) {
         throw new XMLSchemaException("rcase-NSRecurseCheckCardinality.2", new Object[]{Integer.toString(min1), max1 == -1 ? "unbounded" : Integer.toString(max1), Integer.toString(min2), max2 == -1 ? "unbounded" : Integer.toString(max2)});
      } else {
         int count = children.size();

         try {
            for(int i = 0; i < count; ++i) {
               XSParticleDecl particle1 = (XSParticleDecl)children.elementAt(i);
               particleValidRestriction(particle1, dSGHandler, wildcard, (SubstitutionGroupHandler)null, false);
            }

         } catch (XMLSchemaException var11) {
            throw new XMLSchemaException("rcase-NSRecurseCheckCardinality.1", (Object[])null);
         }
      }
   }

   private static void checkRecurse(Vector dChildren, int min1, int max1, SubstitutionGroupHandler dSGHandler, Vector bChildren, int min2, int max2, SubstitutionGroupHandler bSGHandler) throws XMLSchemaException {
      if (!checkOccurrenceRange(min1, max1, min2, max2)) {
         throw new XMLSchemaException("rcase-Recurse.1", new Object[]{Integer.toString(min1), max1 == -1 ? "unbounded" : Integer.toString(max1), Integer.toString(min2), max2 == -1 ? "unbounded" : Integer.toString(max2)});
      } else {
         int count1 = dChildren.size();
         int count2 = bChildren.size();
         int current = 0;

         int j;
         XSParticleDecl particle1;
         label58:
         for(j = 0; j < count1; ++j) {
            particle1 = (XSParticleDecl)dChildren.elementAt(j);
            int j = current;

            while(j < count2) {
               XSParticleDecl particle2 = (XSParticleDecl)bChildren.elementAt(j);
               ++current;

               try {
                  particleValidRestriction(particle1, dSGHandler, particle2, bSGHandler);
                  continue label58;
               } catch (XMLSchemaException var16) {
                  if (!particle2.emptiable()) {
                     throw new XMLSchemaException("rcase-Recurse.2", (Object[])null);
                  }

                  ++j;
               }
            }

            throw new XMLSchemaException("rcase-Recurse.2", (Object[])null);
         }

         for(j = current; j < count2; ++j) {
            particle1 = (XSParticleDecl)bChildren.elementAt(j);
            if (!particle1.emptiable()) {
               throw new XMLSchemaException("rcase-Recurse.2", (Object[])null);
            }
         }

      }
   }

   private static void checkRecurseUnordered(Vector dChildren, int min1, int max1, SubstitutionGroupHandler dSGHandler, Vector bChildren, int min2, int max2, SubstitutionGroupHandler bSGHandler) throws XMLSchemaException {
      if (!checkOccurrenceRange(min1, max1, min2, max2)) {
         throw new XMLSchemaException("rcase-RecurseUnordered.1", new Object[]{Integer.toString(min1), max1 == -1 ? "unbounded" : Integer.toString(max1), Integer.toString(min2), max2 == -1 ? "unbounded" : Integer.toString(max2)});
      } else {
         int count1 = dChildren.size();
         int count2 = bChildren.size();
         boolean[] foundIt = new boolean[count2];
         int j = 0;

         XSParticleDecl particle1;
         while(j < count1) {
            particle1 = (XSParticleDecl)dChildren.elementAt(j);
            int j = 0;

            while(true) {
               if (j < count2) {
                  XSParticleDecl particle2 = (XSParticleDecl)bChildren.elementAt(j);

                  try {
                     particleValidRestriction(particle1, dSGHandler, particle2, bSGHandler);
                     if (foundIt[j]) {
                        throw new XMLSchemaException("rcase-RecurseUnordered.2", (Object[])null);
                     }

                     foundIt[j] = true;
                  } catch (XMLSchemaException var16) {
                     ++j;
                     continue;
                  }

                  ++j;
                  break;
               }

               throw new XMLSchemaException("rcase-RecurseUnordered.2", (Object[])null);
            }
         }

         for(j = 0; j < count2; ++j) {
            particle1 = (XSParticleDecl)bChildren.elementAt(j);
            if (!foundIt[j] && !particle1.emptiable()) {
               throw new XMLSchemaException("rcase-RecurseUnordered.2", (Object[])null);
            }
         }

      }
   }

   private static void checkRecurseLax(Vector dChildren, int min1, int max1, SubstitutionGroupHandler dSGHandler, Vector bChildren, int min2, int max2, SubstitutionGroupHandler bSGHandler) throws XMLSchemaException {
      if (!checkOccurrenceRange(min1, max1, min2, max2)) {
         throw new XMLSchemaException("rcase-RecurseLax.1", new Object[]{Integer.toString(min1), max1 == -1 ? "unbounded" : Integer.toString(max1), Integer.toString(min2), max2 == -1 ? "unbounded" : Integer.toString(max2)});
      } else {
         int count1 = dChildren.size();
         int count2 = bChildren.size();
         int current = 0;

         label46:
         for(int i = 0; i < count1; ++i) {
            XSParticleDecl particle1 = (XSParticleDecl)dChildren.elementAt(i);
            int j = current;

            while(j < count2) {
               XSParticleDecl particle2 = (XSParticleDecl)bChildren.elementAt(j);
               ++current;

               try {
                  if (particleValidRestriction(particle1, dSGHandler, particle2, bSGHandler)) {
                     --current;
                  }
                  continue label46;
               } catch (XMLSchemaException var16) {
                  ++j;
               }
            }

            throw new XMLSchemaException("rcase-RecurseLax.2", (Object[])null);
         }

      }
   }

   private static void checkMapAndSum(Vector dChildren, int min1, int max1, SubstitutionGroupHandler dSGHandler, Vector bChildren, int min2, int max2, SubstitutionGroupHandler bSGHandler) throws XMLSchemaException {
      if (!checkOccurrenceRange(min1, max1, min2, max2)) {
         throw new XMLSchemaException("rcase-MapAndSum.2", new Object[]{Integer.toString(min1), max1 == -1 ? "unbounded" : Integer.toString(max1), Integer.toString(min2), max2 == -1 ? "unbounded" : Integer.toString(max2)});
      } else {
         int count1 = dChildren.size();
         int count2 = bChildren.size();

         label42:
         for(int i = 0; i < count1; ++i) {
            XSParticleDecl particle1 = (XSParticleDecl)dChildren.elementAt(i);
            int j = 0;

            while(j < count2) {
               XSParticleDecl particle2 = (XSParticleDecl)bChildren.elementAt(j);

               try {
                  particleValidRestriction(particle1, dSGHandler, particle2, bSGHandler);
                  continue label42;
               } catch (XMLSchemaException var15) {
                  ++j;
               }
            }

            throw new XMLSchemaException("rcase-MapAndSum.1", (Object[])null);
         }

      }
   }

   public static boolean overlapUPA(XSElementDecl element1, XSElementDecl element2, SubstitutionGroupHandler sgHandler) {
      if (element1.fName == element2.fName && element1.fTargetNamespace == element2.fTargetNamespace) {
         return true;
      } else {
         XSElementDecl[] subGroup = sgHandler.getSubstitutionGroup(element1);

         int i;
         for(i = subGroup.length - 1; i >= 0; --i) {
            if (subGroup[i].fName == element2.fName && subGroup[i].fTargetNamespace == element2.fTargetNamespace) {
               return true;
            }
         }

         subGroup = sgHandler.getSubstitutionGroup(element2);

         for(i = subGroup.length - 1; i >= 0; --i) {
            if (subGroup[i].fName == element1.fName && subGroup[i].fTargetNamespace == element1.fTargetNamespace) {
               return true;
            }
         }

         return false;
      }
   }

   public static boolean overlapUPA(XSElementDecl element, XSWildcardDecl wildcard, SubstitutionGroupHandler sgHandler) {
      if (wildcard.allowNamespace(element.fTargetNamespace)) {
         return true;
      } else {
         XSElementDecl[] subGroup = sgHandler.getSubstitutionGroup(element);

         for(int i = subGroup.length - 1; i >= 0; --i) {
            if (wildcard.allowNamespace(subGroup[i].fTargetNamespace)) {
               return true;
            }
         }

         return false;
      }
   }

   public static boolean overlapUPA(XSWildcardDecl wildcard1, XSWildcardDecl wildcard2) {
      XSWildcardDecl intersect = wildcard1.performIntersectionWith(wildcard2, wildcard1.fProcessContents);
      return intersect == null || intersect.fType != 3 || intersect.fNamespaceList.length != 0;
   }

   public static boolean overlapUPA(Object decl1, Object decl2, SubstitutionGroupHandler sgHandler) {
      if (decl1 instanceof XSElementDecl) {
         return decl2 instanceof XSElementDecl ? overlapUPA((XSElementDecl)decl1, (XSElementDecl)decl2, sgHandler) : overlapUPA((XSElementDecl)decl1, (XSWildcardDecl)decl2, sgHandler);
      } else {
         return decl2 instanceof XSElementDecl ? overlapUPA((XSElementDecl)decl2, (XSWildcardDecl)decl1, sgHandler) : overlapUPA((XSWildcardDecl)decl1, (XSWildcardDecl)decl2);
      }
   }

   static {
      STRING_TYPE = (XSSimpleType)SchemaGrammar.SG_SchemaNS.getGlobalTypeDecl("string");
      fEmptyParticle = null;
      ELEMENT_PARTICLE_COMPARATOR = new Comparator() {
         public int compare(Object o1, Object o2) {
            XSParticleDecl pDecl1 = (XSParticleDecl)o1;
            XSParticleDecl pDecl2 = (XSParticleDecl)o2;
            XSElementDecl decl1 = (XSElementDecl)pDecl1.fValue;
            XSElementDecl decl2 = (XSElementDecl)pDecl2.fValue;
            String namespace1 = decl1.getNamespace();
            String namespace2 = decl2.getNamespace();
            String name1 = decl1.getName();
            String name2 = decl2.getName();
            boolean sameNamespace = namespace1 == namespace2;
            int namespaceComparison = 0;
            if (!sameNamespace) {
               if (namespace1 != null) {
                  if (namespace2 != null) {
                     namespaceComparison = namespace1.compareTo(namespace2);
                  } else {
                     namespaceComparison = 1;
                  }
               } else {
                  namespaceComparison = -1;
               }
            }

            return namespaceComparison != 0 ? namespaceComparison : name1.compareTo(name2);
         }
      };
   }
}
