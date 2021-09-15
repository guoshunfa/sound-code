package com.sun.org.apache.xerces.internal.impl.xs.traversers;

import com.sun.org.apache.xerces.internal.impl.dv.InvalidDatatypeFacetException;
import com.sun.org.apache.xerces.internal.impl.dv.XSFacets;
import com.sun.org.apache.xerces.internal.impl.dv.XSSimpleType;
import com.sun.org.apache.xerces.internal.impl.dv.xs.XSSimpleTypeDecl;
import com.sun.org.apache.xerces.internal.impl.xs.SchemaGrammar;
import com.sun.org.apache.xerces.internal.impl.xs.SchemaSymbols;
import com.sun.org.apache.xerces.internal.impl.xs.XSAnnotationImpl;
import com.sun.org.apache.xerces.internal.impl.xs.XSAttributeGroupDecl;
import com.sun.org.apache.xerces.internal.impl.xs.XSAttributeUseImpl;
import com.sun.org.apache.xerces.internal.impl.xs.XSComplexTypeDecl;
import com.sun.org.apache.xerces.internal.impl.xs.XSConstraints;
import com.sun.org.apache.xerces.internal.impl.xs.XSModelGroupImpl;
import com.sun.org.apache.xerces.internal.impl.xs.XSParticleDecl;
import com.sun.org.apache.xerces.internal.impl.xs.XSWildcardDecl;
import com.sun.org.apache.xerces.internal.impl.xs.util.XInt;
import com.sun.org.apache.xerces.internal.impl.xs.util.XSObjectListImpl;
import com.sun.org.apache.xerces.internal.util.DOMUtil;
import com.sun.org.apache.xerces.internal.xni.QName;
import com.sun.org.apache.xerces.internal.xs.XSAttributeUse;
import com.sun.org.apache.xerces.internal.xs.XSObjectList;
import com.sun.org.apache.xerces.internal.xs.XSTypeDefinition;
import org.w3c.dom.Element;

class XSDComplexTypeTraverser extends XSDAbstractParticleTraverser {
   private static final int GLOBAL_NUM = 11;
   private static XSParticleDecl fErrorContent = null;
   private static XSWildcardDecl fErrorWildcard = null;
   private String fName = null;
   private String fTargetNamespace = null;
   private short fDerivedBy = 2;
   private short fFinal = 0;
   private short fBlock = 0;
   private short fContentType = 0;
   private XSTypeDefinition fBaseType = null;
   private XSAttributeGroupDecl fAttrGrp = null;
   private XSSimpleType fXSSimpleType = null;
   private XSParticleDecl fParticle = null;
   private boolean fIsAbstract = false;
   private XSComplexTypeDecl fComplexTypeDecl = null;
   private XSAnnotationImpl[] fAnnotations = null;
   private Object[] fGlobalStore = null;
   private int fGlobalStorePos = 0;
   private static final boolean DEBUG = false;

   private static XSParticleDecl getErrorContent() {
      if (fErrorContent == null) {
         XSParticleDecl particle = new XSParticleDecl();
         particle.fType = 2;
         particle.fValue = getErrorWildcard();
         particle.fMinOccurs = 0;
         particle.fMaxOccurs = -1;
         XSModelGroupImpl group = new XSModelGroupImpl();
         group.fCompositor = 102;
         group.fParticleCount = 1;
         group.fParticles = new XSParticleDecl[1];
         group.fParticles[0] = particle;
         XSParticleDecl errorContent = new XSParticleDecl();
         errorContent.fType = 3;
         errorContent.fValue = group;
         fErrorContent = errorContent;
      }

      return fErrorContent;
   }

   private static XSWildcardDecl getErrorWildcard() {
      if (fErrorWildcard == null) {
         XSWildcardDecl wildcard = new XSWildcardDecl();
         wildcard.fProcessContents = 2;
         fErrorWildcard = wildcard;
      }

      return fErrorWildcard;
   }

   XSDComplexTypeTraverser(XSDHandler handler, XSAttributeChecker gAttrCheck) {
      super(handler, gAttrCheck);
   }

   XSComplexTypeDecl traverseLocal(Element complexTypeNode, XSDocumentInfo schemaDoc, SchemaGrammar grammar) {
      Object[] attrValues = this.fAttrChecker.checkAttributes(complexTypeNode, false, schemaDoc);
      String complexTypeName = this.genAnonTypeName(complexTypeNode);
      this.contentBackup();
      XSComplexTypeDecl type = this.traverseComplexTypeDecl(complexTypeNode, complexTypeName, attrValues, schemaDoc, grammar);
      this.contentRestore();
      grammar.addComplexTypeDecl(type, this.fSchemaHandler.element2Locator(complexTypeNode));
      type.setIsAnonymous();
      this.fAttrChecker.returnAttrArray(attrValues, schemaDoc);
      return type;
   }

   XSComplexTypeDecl traverseGlobal(Element complexTypeNode, XSDocumentInfo schemaDoc, SchemaGrammar grammar) {
      Object[] attrValues = this.fAttrChecker.checkAttributes(complexTypeNode, true, schemaDoc);
      String complexTypeName = (String)attrValues[XSAttributeChecker.ATTIDX_NAME];
      this.contentBackup();
      XSComplexTypeDecl type = this.traverseComplexTypeDecl(complexTypeNode, complexTypeName, attrValues, schemaDoc, grammar);
      this.contentRestore();
      grammar.addComplexTypeDecl(type, this.fSchemaHandler.element2Locator(complexTypeNode));
      if (complexTypeName == null) {
         this.reportSchemaError("s4s-att-must-appear", new Object[]{SchemaSymbols.ELT_COMPLEXTYPE, SchemaSymbols.ATT_NAME}, complexTypeNode);
         type = null;
      } else {
         if (grammar.getGlobalTypeDecl(type.getName()) == null) {
            grammar.addGlobalComplexTypeDecl(type);
         }

         String loc = this.fSchemaHandler.schemaDocument2SystemId(schemaDoc);
         XSTypeDefinition type2 = grammar.getGlobalTypeDecl(type.getName(), loc);
         if (type2 == null) {
            grammar.addGlobalComplexTypeDecl(type, loc);
         }

         if (this.fSchemaHandler.fTolerateDuplicates) {
            if (type2 != null && type2 instanceof XSComplexTypeDecl) {
               type = (XSComplexTypeDecl)type2;
            }

            this.fSchemaHandler.addGlobalTypeDecl(type);
         }
      }

      this.fAttrChecker.returnAttrArray(attrValues, schemaDoc);
      return type;
   }

   private XSComplexTypeDecl traverseComplexTypeDecl(Element complexTypeDecl, String complexTypeName, Object[] attrValues, XSDocumentInfo schemaDoc, SchemaGrammar grammar) {
      this.fComplexTypeDecl = new XSComplexTypeDecl();
      this.fAttrGrp = new XSAttributeGroupDecl();
      Boolean abstractAtt = (Boolean)attrValues[XSAttributeChecker.ATTIDX_ABSTRACT];
      XInt blockAtt = (XInt)attrValues[XSAttributeChecker.ATTIDX_BLOCK];
      Boolean mixedAtt = (Boolean)attrValues[XSAttributeChecker.ATTIDX_MIXED];
      XInt finalAtt = (XInt)attrValues[XSAttributeChecker.ATTIDX_FINAL];
      this.fName = complexTypeName;
      this.fComplexTypeDecl.setName(this.fName);
      this.fTargetNamespace = schemaDoc.fTargetNamespace;
      this.fBlock = blockAtt == null ? schemaDoc.fBlockDefault : blockAtt.shortValue();
      this.fFinal = finalAtt == null ? schemaDoc.fFinalDefault : finalAtt.shortValue();
      this.fBlock = (short)(this.fBlock & 3);
      this.fFinal = (short)(this.fFinal & 3);
      this.fIsAbstract = abstractAtt != null && abstractAtt;
      this.fAnnotations = null;
      Element child = null;

      try {
         child = DOMUtil.getFirstChildElement(complexTypeDecl);
         String text;
         if (child != null) {
            if (DOMUtil.getLocalName(child).equals(SchemaSymbols.ELT_ANNOTATION)) {
               this.addAnnotation(this.traverseAnnotationDecl(child, attrValues, false, schemaDoc));
               child = DOMUtil.getNextSiblingElement(child);
            } else {
               text = DOMUtil.getSyntheticAnnotation(complexTypeDecl);
               if (text != null) {
                  this.addAnnotation(this.traverseSyntheticAnnotation(complexTypeDecl, text, attrValues, false, schemaDoc));
               }
            }

            if (child != null && DOMUtil.getLocalName(child).equals(SchemaSymbols.ELT_ANNOTATION)) {
               throw new XSDComplexTypeTraverser.ComplexTypeRecoverableError("s4s-elt-invalid-content.1", new Object[]{this.fName, SchemaSymbols.ELT_ANNOTATION}, child);
            }
         } else {
            text = DOMUtil.getSyntheticAnnotation(complexTypeDecl);
            if (text != null) {
               this.addAnnotation(this.traverseSyntheticAnnotation(complexTypeDecl, text, attrValues, false, schemaDoc));
            }
         }

         if (child == null) {
            this.fBaseType = SchemaGrammar.fAnyType;
            this.fDerivedBy = 2;
            this.processComplexContent(child, mixedAtt, false, schemaDoc, grammar);
         } else {
            String siblingName;
            Element elemTmp;
            if (DOMUtil.getLocalName(child).equals(SchemaSymbols.ELT_SIMPLECONTENT)) {
               this.traverseSimpleContent(child, schemaDoc, grammar);
               elemTmp = DOMUtil.getNextSiblingElement(child);
               if (elemTmp != null) {
                  siblingName = DOMUtil.getLocalName(elemTmp);
                  throw new XSDComplexTypeTraverser.ComplexTypeRecoverableError("s4s-elt-invalid-content.1", new Object[]{this.fName, siblingName}, elemTmp);
               }
            } else if (DOMUtil.getLocalName(child).equals(SchemaSymbols.ELT_COMPLEXCONTENT)) {
               this.traverseComplexContent(child, mixedAtt, schemaDoc, grammar);
               elemTmp = DOMUtil.getNextSiblingElement(child);
               if (elemTmp != null) {
                  siblingName = DOMUtil.getLocalName(elemTmp);
                  throw new XSDComplexTypeTraverser.ComplexTypeRecoverableError("s4s-elt-invalid-content.1", new Object[]{this.fName, siblingName}, elemTmp);
               }
            } else {
               this.fBaseType = SchemaGrammar.fAnyType;
               this.fDerivedBy = 2;
               this.processComplexContent(child, mixedAtt, false, schemaDoc, grammar);
            }
         }
      } catch (XSDComplexTypeTraverser.ComplexTypeRecoverableError var13) {
         this.handleComplexTypeError(var13.getMessage(), var13.errorSubstText, var13.errorElem);
      }

      this.fComplexTypeDecl.setValues(this.fName, this.fTargetNamespace, this.fBaseType, this.fDerivedBy, this.fFinal, this.fBlock, this.fContentType, this.fIsAbstract, this.fAttrGrp, this.fXSSimpleType, this.fParticle, new XSObjectListImpl(this.fAnnotations, this.fAnnotations == null ? 0 : this.fAnnotations.length));
      return this.fComplexTypeDecl;
   }

   private void traverseSimpleContent(Element simpleContentElement, XSDocumentInfo schemaDoc, SchemaGrammar grammar) throws XSDComplexTypeTraverser.ComplexTypeRecoverableError {
      Object[] simpleContentAttrValues = this.fAttrChecker.checkAttributes(simpleContentElement, false, schemaDoc);
      this.fContentType = 1;
      this.fParticle = null;
      Element simpleContent = DOMUtil.getFirstChildElement(simpleContentElement);
      String simpleContentName;
      if (simpleContent != null && DOMUtil.getLocalName(simpleContent).equals(SchemaSymbols.ELT_ANNOTATION)) {
         this.addAnnotation(this.traverseAnnotationDecl(simpleContent, simpleContentAttrValues, false, schemaDoc));
         simpleContent = DOMUtil.getNextSiblingElement(simpleContent);
      } else {
         simpleContentName = DOMUtil.getSyntheticAnnotation(simpleContentElement);
         if (simpleContentName != null) {
            this.addAnnotation(this.traverseSyntheticAnnotation(simpleContentElement, simpleContentName, simpleContentAttrValues, false, schemaDoc));
         }
      }

      if (simpleContent == null) {
         this.fAttrChecker.returnAttrArray(simpleContentAttrValues, schemaDoc);
         throw new XSDComplexTypeTraverser.ComplexTypeRecoverableError("s4s-elt-invalid-content.2", new Object[]{this.fName, SchemaSymbols.ELT_SIMPLECONTENT}, simpleContentElement);
      } else {
         simpleContentName = DOMUtil.getLocalName(simpleContent);
         if (simpleContentName.equals(SchemaSymbols.ELT_RESTRICTION)) {
            this.fDerivedBy = 2;
         } else {
            if (!simpleContentName.equals(SchemaSymbols.ELT_EXTENSION)) {
               this.fAttrChecker.returnAttrArray(simpleContentAttrValues, schemaDoc);
               throw new XSDComplexTypeTraverser.ComplexTypeRecoverableError("s4s-elt-invalid-content.1", new Object[]{this.fName, simpleContentName}, simpleContent);
            }

            this.fDerivedBy = 1;
         }

         Element elemTmp = DOMUtil.getNextSiblingElement(simpleContent);
         if (elemTmp != null) {
            this.fAttrChecker.returnAttrArray(simpleContentAttrValues, schemaDoc);
            String siblingName = DOMUtil.getLocalName(elemTmp);
            throw new XSDComplexTypeTraverser.ComplexTypeRecoverableError("s4s-elt-invalid-content.1", new Object[]{this.fName, siblingName}, elemTmp);
         } else {
            Object[] derivationTypeAttrValues = this.fAttrChecker.checkAttributes(simpleContent, false, schemaDoc);
            QName baseTypeName = (QName)derivationTypeAttrValues[XSAttributeChecker.ATTIDX_BASE];
            if (baseTypeName == null) {
               this.fAttrChecker.returnAttrArray(simpleContentAttrValues, schemaDoc);
               this.fAttrChecker.returnAttrArray(derivationTypeAttrValues, schemaDoc);
               throw new XSDComplexTypeTraverser.ComplexTypeRecoverableError("s4s-att-must-appear", new Object[]{simpleContentName, "base"}, simpleContent);
            } else {
               XSTypeDefinition type = (XSTypeDefinition)this.fSchemaHandler.getGlobalDecl(schemaDoc, 7, baseTypeName, simpleContent);
               if (type == null) {
                  this.fAttrChecker.returnAttrArray(simpleContentAttrValues, schemaDoc);
                  this.fAttrChecker.returnAttrArray(derivationTypeAttrValues, schemaDoc);
                  throw new XSDComplexTypeTraverser.ComplexTypeRecoverableError();
               } else {
                  this.fBaseType = type;
                  XSSimpleType baseValidator = null;
                  XSComplexTypeDecl baseComplexType = null;
                  int baseFinalSet = false;
                  short baseFinalSet;
                  if (type.getTypeCategory() == 15) {
                     baseComplexType = (XSComplexTypeDecl)type;
                     baseFinalSet = baseComplexType.getFinal();
                     if (baseComplexType.getContentType() == 1) {
                        baseValidator = (XSSimpleType)baseComplexType.getSimpleType();
                     } else if (this.fDerivedBy != 2 || baseComplexType.getContentType() != 3 || !((XSParticleDecl)baseComplexType.getParticle()).emptiable()) {
                        this.fAttrChecker.returnAttrArray(simpleContentAttrValues, schemaDoc);
                        this.fAttrChecker.returnAttrArray(derivationTypeAttrValues, schemaDoc);
                        throw new XSDComplexTypeTraverser.ComplexTypeRecoverableError("src-ct.2.1", new Object[]{this.fName, baseComplexType.getName()}, simpleContent);
                     }
                  } else {
                     baseValidator = (XSSimpleType)type;
                     if (this.fDerivedBy == 2) {
                        this.fAttrChecker.returnAttrArray(simpleContentAttrValues, schemaDoc);
                        this.fAttrChecker.returnAttrArray(derivationTypeAttrValues, schemaDoc);
                        throw new XSDComplexTypeTraverser.ComplexTypeRecoverableError("src-ct.2.1", new Object[]{this.fName, baseValidator.getName()}, simpleContent);
                     }

                     baseFinalSet = baseValidator.getFinal();
                  }

                  if ((baseFinalSet & this.fDerivedBy) != 0) {
                     this.fAttrChecker.returnAttrArray(simpleContentAttrValues, schemaDoc);
                     this.fAttrChecker.returnAttrArray(derivationTypeAttrValues, schemaDoc);
                     String errorKey = this.fDerivedBy == 1 ? "cos-ct-extends.1.1" : "derivation-ok-restriction.1";
                     throw new XSDComplexTypeTraverser.ComplexTypeRecoverableError(errorKey, new Object[]{this.fName, this.fBaseType.getName()}, simpleContent);
                  } else {
                     Element scElement = simpleContent;
                     simpleContent = DOMUtil.getFirstChildElement(simpleContent);
                     String text;
                     if (simpleContent != null) {
                        if (DOMUtil.getLocalName(simpleContent).equals(SchemaSymbols.ELT_ANNOTATION)) {
                           this.addAnnotation(this.traverseAnnotationDecl(simpleContent, derivationTypeAttrValues, false, schemaDoc));
                           simpleContent = DOMUtil.getNextSiblingElement(simpleContent);
                        } else {
                           text = DOMUtil.getSyntheticAnnotation(scElement);
                           if (text != null) {
                              this.addAnnotation(this.traverseSyntheticAnnotation(scElement, text, derivationTypeAttrValues, false, schemaDoc));
                           }
                        }

                        if (simpleContent != null && DOMUtil.getLocalName(simpleContent).equals(SchemaSymbols.ELT_ANNOTATION)) {
                           this.fAttrChecker.returnAttrArray(simpleContentAttrValues, schemaDoc);
                           this.fAttrChecker.returnAttrArray(derivationTypeAttrValues, schemaDoc);
                           throw new XSDComplexTypeTraverser.ComplexTypeRecoverableError("s4s-elt-invalid-content.1", new Object[]{this.fName, SchemaSymbols.ELT_ANNOTATION}, simpleContent);
                        }
                     } else {
                        text = DOMUtil.getSyntheticAnnotation(scElement);
                        if (text != null) {
                           this.addAnnotation(this.traverseSyntheticAnnotation(scElement, text, derivationTypeAttrValues, false, schemaDoc));
                        }
                     }

                     if (this.fDerivedBy == 2) {
                        if (simpleContent != null && DOMUtil.getLocalName(simpleContent).equals(SchemaSymbols.ELT_SIMPLETYPE)) {
                           XSSimpleType dv = this.fSchemaHandler.fSimpleTypeTraverser.traverseLocal(simpleContent, schemaDoc, grammar);
                           if (dv == null) {
                              this.fAttrChecker.returnAttrArray(simpleContentAttrValues, schemaDoc);
                              this.fAttrChecker.returnAttrArray(derivationTypeAttrValues, schemaDoc);
                              throw new XSDComplexTypeTraverser.ComplexTypeRecoverableError();
                           }

                           if (baseValidator != null && !XSConstraints.checkSimpleDerivationOk(dv, baseValidator, baseValidator.getFinal())) {
                              this.fAttrChecker.returnAttrArray(simpleContentAttrValues, schemaDoc);
                              this.fAttrChecker.returnAttrArray(derivationTypeAttrValues, schemaDoc);
                              throw new XSDComplexTypeTraverser.ComplexTypeRecoverableError("derivation-ok-restriction.5.2.2.1", new Object[]{this.fName, dv.getName(), baseValidator.getName()}, simpleContent);
                           }

                           baseValidator = dv;
                           simpleContent = DOMUtil.getNextSiblingElement(simpleContent);
                        }

                        if (baseValidator == null) {
                           this.fAttrChecker.returnAttrArray(simpleContentAttrValues, schemaDoc);
                           this.fAttrChecker.returnAttrArray(derivationTypeAttrValues, schemaDoc);
                           throw new XSDComplexTypeTraverser.ComplexTypeRecoverableError("src-ct.2.2", new Object[]{this.fName}, simpleContent);
                        }

                        Element attrNode = null;
                        XSFacets facetData = null;
                        short presentFacets = 0;
                        short fixedFacets = 0;
                        if (simpleContent != null) {
                           XSDAbstractTraverser.FacetInfo fi = this.traverseFacets(simpleContent, baseValidator, schemaDoc);
                           attrNode = fi.nodeAfterFacets;
                           facetData = fi.facetdata;
                           presentFacets = fi.fPresentFacets;
                           fixedFacets = fi.fFixedFacets;
                        }

                        String name = this.genAnonTypeName(simpleContentElement);
                        this.fXSSimpleType = this.fSchemaHandler.fDVFactory.createTypeRestriction(name, schemaDoc.fTargetNamespace, (short)0, baseValidator, (XSObjectList)null);

                        try {
                           this.fValidationState.setNamespaceSupport(schemaDoc.fNamespaceSupport);
                           this.fXSSimpleType.applyFacets(facetData, presentFacets, fixedFacets, this.fValidationState);
                        } catch (InvalidDatatypeFacetException var23) {
                           this.reportSchemaError(var23.getKey(), var23.getArgs(), simpleContent);
                           this.fXSSimpleType = this.fSchemaHandler.fDVFactory.createTypeRestriction(name, schemaDoc.fTargetNamespace, (short)0, baseValidator, (XSObjectList)null);
                        }

                        if (this.fXSSimpleType instanceof XSSimpleTypeDecl) {
                           ((XSSimpleTypeDecl)this.fXSSimpleType).setAnonymous(true);
                        }

                        if (attrNode != null) {
                           if (!this.isAttrOrAttrGroup(attrNode)) {
                              this.fAttrChecker.returnAttrArray(simpleContentAttrValues, schemaDoc);
                              this.fAttrChecker.returnAttrArray(derivationTypeAttrValues, schemaDoc);
                              throw new XSDComplexTypeTraverser.ComplexTypeRecoverableError("s4s-elt-invalid-content.1", new Object[]{this.fName, DOMUtil.getLocalName(attrNode)}, attrNode);
                           }

                           Element node = this.traverseAttrsAndAttrGrps(attrNode, this.fAttrGrp, schemaDoc, grammar, this.fComplexTypeDecl);
                           if (node != null) {
                              this.fAttrChecker.returnAttrArray(simpleContentAttrValues, schemaDoc);
                              this.fAttrChecker.returnAttrArray(derivationTypeAttrValues, schemaDoc);
                              throw new XSDComplexTypeTraverser.ComplexTypeRecoverableError("s4s-elt-invalid-content.1", new Object[]{this.fName, DOMUtil.getLocalName(node)}, node);
                           }
                        }

                        try {
                           this.mergeAttributes(baseComplexType.getAttrGrp(), this.fAttrGrp, this.fName, false, simpleContentElement);
                        } catch (XSDComplexTypeTraverser.ComplexTypeRecoverableError var22) {
                           this.fAttrChecker.returnAttrArray(simpleContentAttrValues, schemaDoc);
                           this.fAttrChecker.returnAttrArray(derivationTypeAttrValues, schemaDoc);
                           throw var22;
                        }

                        this.fAttrGrp.removeProhibitedAttrs();
                        Object[] errArgs = this.fAttrGrp.validRestrictionOf(this.fName, baseComplexType.getAttrGrp());
                        if (errArgs != null) {
                           this.fAttrChecker.returnAttrArray(simpleContentAttrValues, schemaDoc);
                           this.fAttrChecker.returnAttrArray(derivationTypeAttrValues, schemaDoc);
                           throw new XSDComplexTypeTraverser.ComplexTypeRecoverableError((String)errArgs[errArgs.length - 1], errArgs, attrNode);
                        }
                     } else {
                        this.fXSSimpleType = baseValidator;
                        if (simpleContent != null) {
                           if (!this.isAttrOrAttrGroup(simpleContent)) {
                              this.fAttrChecker.returnAttrArray(simpleContentAttrValues, schemaDoc);
                              this.fAttrChecker.returnAttrArray(derivationTypeAttrValues, schemaDoc);
                              throw new XSDComplexTypeTraverser.ComplexTypeRecoverableError("s4s-elt-invalid-content.1", new Object[]{this.fName, DOMUtil.getLocalName(simpleContent)}, simpleContent);
                           }

                           Element node = this.traverseAttrsAndAttrGrps(simpleContent, this.fAttrGrp, schemaDoc, grammar, this.fComplexTypeDecl);
                           if (node != null) {
                              this.fAttrChecker.returnAttrArray(simpleContentAttrValues, schemaDoc);
                              this.fAttrChecker.returnAttrArray(derivationTypeAttrValues, schemaDoc);
                              throw new XSDComplexTypeTraverser.ComplexTypeRecoverableError("s4s-elt-invalid-content.1", new Object[]{this.fName, DOMUtil.getLocalName(node)}, node);
                           }

                           this.fAttrGrp.removeProhibitedAttrs();
                        }

                        if (baseComplexType != null) {
                           try {
                              this.mergeAttributes(baseComplexType.getAttrGrp(), this.fAttrGrp, this.fName, true, simpleContentElement);
                           } catch (XSDComplexTypeTraverser.ComplexTypeRecoverableError var21) {
                              this.fAttrChecker.returnAttrArray(simpleContentAttrValues, schemaDoc);
                              this.fAttrChecker.returnAttrArray(derivationTypeAttrValues, schemaDoc);
                              throw var21;
                           }
                        }
                     }

                     this.fAttrChecker.returnAttrArray(simpleContentAttrValues, schemaDoc);
                     this.fAttrChecker.returnAttrArray(derivationTypeAttrValues, schemaDoc);
                  }
               }
            }
         }
      }
   }

   private void traverseComplexContent(Element complexContentElement, boolean mixedOnType, XSDocumentInfo schemaDoc, SchemaGrammar grammar) throws XSDComplexTypeTraverser.ComplexTypeRecoverableError {
      Object[] complexContentAttrValues = this.fAttrChecker.checkAttributes(complexContentElement, false, schemaDoc);
      boolean mixedContent = mixedOnType;
      Boolean mixedAtt = (Boolean)complexContentAttrValues[XSAttributeChecker.ATTIDX_MIXED];
      if (mixedAtt != null) {
         mixedContent = mixedAtt;
      }

      this.fXSSimpleType = null;
      Element complexContent = DOMUtil.getFirstChildElement(complexContentElement);
      String complexContentName;
      if (complexContent != null && DOMUtil.getLocalName(complexContent).equals(SchemaSymbols.ELT_ANNOTATION)) {
         this.addAnnotation(this.traverseAnnotationDecl(complexContent, complexContentAttrValues, false, schemaDoc));
         complexContent = DOMUtil.getNextSiblingElement(complexContent);
      } else {
         complexContentName = DOMUtil.getSyntheticAnnotation(complexContentElement);
         if (complexContentName != null) {
            this.addAnnotation(this.traverseSyntheticAnnotation(complexContentElement, complexContentName, complexContentAttrValues, false, schemaDoc));
         }
      }

      if (complexContent == null) {
         this.fAttrChecker.returnAttrArray(complexContentAttrValues, schemaDoc);
         throw new XSDComplexTypeTraverser.ComplexTypeRecoverableError("s4s-elt-invalid-content.2", new Object[]{this.fName, SchemaSymbols.ELT_COMPLEXCONTENT}, complexContentElement);
      } else {
         complexContentName = DOMUtil.getLocalName(complexContent);
         if (complexContentName.equals(SchemaSymbols.ELT_RESTRICTION)) {
            this.fDerivedBy = 2;
         } else {
            if (!complexContentName.equals(SchemaSymbols.ELT_EXTENSION)) {
               this.fAttrChecker.returnAttrArray(complexContentAttrValues, schemaDoc);
               throw new XSDComplexTypeTraverser.ComplexTypeRecoverableError("s4s-elt-invalid-content.1", new Object[]{this.fName, complexContentName}, complexContent);
            }

            this.fDerivedBy = 1;
         }

         Element elemTmp = DOMUtil.getNextSiblingElement(complexContent);
         if (elemTmp != null) {
            this.fAttrChecker.returnAttrArray(complexContentAttrValues, schemaDoc);
            String siblingName = DOMUtil.getLocalName(elemTmp);
            throw new XSDComplexTypeTraverser.ComplexTypeRecoverableError("s4s-elt-invalid-content.1", new Object[]{this.fName, siblingName}, elemTmp);
         } else {
            Object[] derivationTypeAttrValues = this.fAttrChecker.checkAttributes(complexContent, false, schemaDoc);
            QName baseTypeName = (QName)derivationTypeAttrValues[XSAttributeChecker.ATTIDX_BASE];
            if (baseTypeName == null) {
               this.fAttrChecker.returnAttrArray(complexContentAttrValues, schemaDoc);
               this.fAttrChecker.returnAttrArray(derivationTypeAttrValues, schemaDoc);
               throw new XSDComplexTypeTraverser.ComplexTypeRecoverableError("s4s-att-must-appear", new Object[]{complexContentName, "base"}, complexContent);
            } else {
               XSTypeDefinition type = (XSTypeDefinition)this.fSchemaHandler.getGlobalDecl(schemaDoc, 7, baseTypeName, complexContent);
               if (type == null) {
                  this.fAttrChecker.returnAttrArray(complexContentAttrValues, schemaDoc);
                  this.fAttrChecker.returnAttrArray(derivationTypeAttrValues, schemaDoc);
                  throw new XSDComplexTypeTraverser.ComplexTypeRecoverableError();
               } else if (!(type instanceof XSComplexTypeDecl)) {
                  this.fAttrChecker.returnAttrArray(complexContentAttrValues, schemaDoc);
                  this.fAttrChecker.returnAttrArray(derivationTypeAttrValues, schemaDoc);
                  throw new XSDComplexTypeTraverser.ComplexTypeRecoverableError("src-ct.1", new Object[]{this.fName, type.getName()}, complexContent);
               } else {
                  XSComplexTypeDecl baseType = (XSComplexTypeDecl)type;
                  this.fBaseType = baseType;
                  String text;
                  if ((baseType.getFinal() & this.fDerivedBy) != 0) {
                     this.fAttrChecker.returnAttrArray(complexContentAttrValues, schemaDoc);
                     this.fAttrChecker.returnAttrArray(derivationTypeAttrValues, schemaDoc);
                     text = this.fDerivedBy == 1 ? "cos-ct-extends.1.1" : "derivation-ok-restriction.1";
                     throw new XSDComplexTypeTraverser.ComplexTypeRecoverableError(text, new Object[]{this.fName, this.fBaseType.getName()}, complexContent);
                  } else {
                     complexContent = DOMUtil.getFirstChildElement(complexContent);
                     if (complexContent != null) {
                        if (DOMUtil.getLocalName(complexContent).equals(SchemaSymbols.ELT_ANNOTATION)) {
                           this.addAnnotation(this.traverseAnnotationDecl(complexContent, derivationTypeAttrValues, false, schemaDoc));
                           complexContent = DOMUtil.getNextSiblingElement(complexContent);
                        } else {
                           text = DOMUtil.getSyntheticAnnotation(complexContent);
                           if (text != null) {
                              this.addAnnotation(this.traverseSyntheticAnnotation(complexContent, text, derivationTypeAttrValues, false, schemaDoc));
                           }
                        }

                        if (complexContent != null && DOMUtil.getLocalName(complexContent).equals(SchemaSymbols.ELT_ANNOTATION)) {
                           this.fAttrChecker.returnAttrArray(complexContentAttrValues, schemaDoc);
                           this.fAttrChecker.returnAttrArray(derivationTypeAttrValues, schemaDoc);
                           throw new XSDComplexTypeTraverser.ComplexTypeRecoverableError("s4s-elt-invalid-content.1", new Object[]{this.fName, SchemaSymbols.ELT_ANNOTATION}, complexContent);
                        }
                     } else {
                        text = DOMUtil.getSyntheticAnnotation(complexContent);
                        if (text != null) {
                           this.addAnnotation(this.traverseSyntheticAnnotation(complexContent, text, derivationTypeAttrValues, false, schemaDoc));
                        }
                     }

                     try {
                        this.processComplexContent(complexContent, mixedContent, true, schemaDoc, grammar);
                     } catch (XSDComplexTypeTraverser.ComplexTypeRecoverableError var20) {
                        this.fAttrChecker.returnAttrArray(complexContentAttrValues, schemaDoc);
                        this.fAttrChecker.returnAttrArray(derivationTypeAttrValues, schemaDoc);
                        throw var20;
                     }

                     XSParticleDecl baseContent = (XSParticleDecl)baseType.getParticle();
                     if (this.fDerivedBy == 2) {
                        if (this.fContentType == 3 && baseType.getContentType() != 3) {
                           this.fAttrChecker.returnAttrArray(complexContentAttrValues, schemaDoc);
                           this.fAttrChecker.returnAttrArray(derivationTypeAttrValues, schemaDoc);
                           throw new XSDComplexTypeTraverser.ComplexTypeRecoverableError("derivation-ok-restriction.5.4.1.2", new Object[]{this.fName, baseType.getName()}, complexContent);
                        }

                        try {
                           this.mergeAttributes(baseType.getAttrGrp(), this.fAttrGrp, this.fName, false, complexContent);
                        } catch (XSDComplexTypeTraverser.ComplexTypeRecoverableError var19) {
                           this.fAttrChecker.returnAttrArray(complexContentAttrValues, schemaDoc);
                           this.fAttrChecker.returnAttrArray(derivationTypeAttrValues, schemaDoc);
                           throw var19;
                        }

                        this.fAttrGrp.removeProhibitedAttrs();
                        if (baseType != SchemaGrammar.fAnyType) {
                           Object[] errArgs = this.fAttrGrp.validRestrictionOf(this.fName, baseType.getAttrGrp());
                           if (errArgs != null) {
                              this.fAttrChecker.returnAttrArray(complexContentAttrValues, schemaDoc);
                              this.fAttrChecker.returnAttrArray(derivationTypeAttrValues, schemaDoc);
                              throw new XSDComplexTypeTraverser.ComplexTypeRecoverableError((String)errArgs[errArgs.length - 1], errArgs, complexContent);
                           }
                        }
                     } else {
                        if (this.fParticle == null) {
                           this.fContentType = baseType.getContentType();
                           this.fXSSimpleType = (XSSimpleType)baseType.getSimpleType();
                           this.fParticle = baseContent;
                        } else if (baseType.getContentType() != 0) {
                           if (this.fContentType == 2 && baseType.getContentType() != 2) {
                              this.fAttrChecker.returnAttrArray(complexContentAttrValues, schemaDoc);
                              this.fAttrChecker.returnAttrArray(derivationTypeAttrValues, schemaDoc);
                              throw new XSDComplexTypeTraverser.ComplexTypeRecoverableError("cos-ct-extends.1.4.3.2.2.1.a", new Object[]{this.fName}, complexContent);
                           }

                           if (this.fContentType == 3 && baseType.getContentType() != 3) {
                              this.fAttrChecker.returnAttrArray(complexContentAttrValues, schemaDoc);
                              this.fAttrChecker.returnAttrArray(derivationTypeAttrValues, schemaDoc);
                              throw new XSDComplexTypeTraverser.ComplexTypeRecoverableError("cos-ct-extends.1.4.3.2.2.1.b", new Object[]{this.fName}, complexContent);
                           }

                           if (this.fParticle.fType == 3 && ((XSModelGroupImpl)this.fParticle.fValue).fCompositor == 103 || ((XSParticleDecl)baseType.getParticle()).fType == 3 && ((XSModelGroupImpl)((XSParticleDecl)baseType.getParticle()).fValue).fCompositor == 103) {
                              this.fAttrChecker.returnAttrArray(complexContentAttrValues, schemaDoc);
                              this.fAttrChecker.returnAttrArray(derivationTypeAttrValues, schemaDoc);
                              throw new XSDComplexTypeTraverser.ComplexTypeRecoverableError("cos-all-limited.1.2", new Object[0], complexContent);
                           }

                           XSModelGroupImpl group = new XSModelGroupImpl();
                           group.fCompositor = 102;
                           group.fParticleCount = 2;
                           group.fParticles = new XSParticleDecl[2];
                           group.fParticles[0] = (XSParticleDecl)baseType.getParticle();
                           group.fParticles[1] = this.fParticle;
                           group.fAnnotations = XSObjectListImpl.EMPTY_LIST;
                           XSParticleDecl particle = new XSParticleDecl();
                           particle.fType = 3;
                           particle.fValue = group;
                           particle.fAnnotations = XSObjectListImpl.EMPTY_LIST;
                           this.fParticle = particle;
                        }

                        this.fAttrGrp.removeProhibitedAttrs();

                        try {
                           this.mergeAttributes(baseType.getAttrGrp(), this.fAttrGrp, this.fName, true, complexContent);
                        } catch (XSDComplexTypeTraverser.ComplexTypeRecoverableError var18) {
                           this.fAttrChecker.returnAttrArray(complexContentAttrValues, schemaDoc);
                           this.fAttrChecker.returnAttrArray(derivationTypeAttrValues, schemaDoc);
                           throw var18;
                        }
                     }

                     this.fAttrChecker.returnAttrArray(complexContentAttrValues, schemaDoc);
                     this.fAttrChecker.returnAttrArray(derivationTypeAttrValues, schemaDoc);
                  }
               }
            }
         }
      }
   }

   private void mergeAttributes(XSAttributeGroupDecl fromAttrGrp, XSAttributeGroupDecl toAttrGrp, String typeName, boolean extension, Element elem) throws XSDComplexTypeTraverser.ComplexTypeRecoverableError {
      XSObjectList attrUseS = fromAttrGrp.getAttributeUses();
      XSAttributeUseImpl oneAttrUse = null;
      int attrCount = attrUseS.getLength();

      for(int i = 0; i < attrCount; ++i) {
         oneAttrUse = (XSAttributeUseImpl)attrUseS.item(i);
         XSAttributeUse existingAttrUse = toAttrGrp.getAttributeUse(oneAttrUse.fAttrDecl.getNamespace(), oneAttrUse.fAttrDecl.getName());
         if (existingAttrUse == null) {
            String idName = toAttrGrp.addAttributeUse(oneAttrUse);
            if (idName != null) {
               throw new XSDComplexTypeTraverser.ComplexTypeRecoverableError("ct-props-correct.5", new Object[]{typeName, idName, oneAttrUse.fAttrDecl.getName()}, elem);
            }
         } else if (existingAttrUse != oneAttrUse && extension) {
            this.reportSchemaError("ct-props-correct.4", new Object[]{typeName, oneAttrUse.fAttrDecl.getName()}, elem);
            toAttrGrp.replaceAttributeUse(existingAttrUse, oneAttrUse);
         }
      }

      if (extension) {
         if (toAttrGrp.fAttributeWC == null) {
            toAttrGrp.fAttributeWC = fromAttrGrp.fAttributeWC;
         } else if (fromAttrGrp.fAttributeWC != null) {
            toAttrGrp.fAttributeWC = toAttrGrp.fAttributeWC.performUnionWith(fromAttrGrp.fAttributeWC, toAttrGrp.fAttributeWC.fProcessContents);
            if (toAttrGrp.fAttributeWC == null) {
               throw new XSDComplexTypeTraverser.ComplexTypeRecoverableError("src-ct.5", new Object[]{typeName}, elem);
            }
         }
      }

   }

   private void processComplexContent(Element complexContentChild, boolean isMixed, boolean isDerivation, XSDocumentInfo schemaDoc, SchemaGrammar grammar) throws XSDComplexTypeTraverser.ComplexTypeRecoverableError {
      Element attrNode = null;
      XSParticleDecl particle = null;
      boolean emptyParticle = false;
      if (complexContentChild != null) {
         String childName = DOMUtil.getLocalName(complexContentChild);
         if (childName.equals(SchemaSymbols.ELT_GROUP)) {
            particle = this.fSchemaHandler.fGroupTraverser.traverseLocal(complexContentChild, schemaDoc, grammar);
            attrNode = DOMUtil.getNextSiblingElement(complexContentChild);
         } else {
            XSModelGroupImpl group;
            if (childName.equals(SchemaSymbols.ELT_SEQUENCE)) {
               particle = this.traverseSequence(complexContentChild, schemaDoc, grammar, 0, this.fComplexTypeDecl);
               if (particle != null) {
                  group = (XSModelGroupImpl)particle.fValue;
                  if (group.fParticleCount == 0) {
                     emptyParticle = true;
                  }
               }

               attrNode = DOMUtil.getNextSiblingElement(complexContentChild);
            } else if (childName.equals(SchemaSymbols.ELT_CHOICE)) {
               particle = this.traverseChoice(complexContentChild, schemaDoc, grammar, 0, this.fComplexTypeDecl);
               if (particle != null && particle.fMinOccurs == 0) {
                  group = (XSModelGroupImpl)particle.fValue;
                  if (group.fParticleCount == 0) {
                     emptyParticle = true;
                  }
               }

               attrNode = DOMUtil.getNextSiblingElement(complexContentChild);
            } else if (childName.equals(SchemaSymbols.ELT_ALL)) {
               particle = this.traverseAll(complexContentChild, schemaDoc, grammar, 8, this.fComplexTypeDecl);
               if (particle != null) {
                  group = (XSModelGroupImpl)particle.fValue;
                  if (group.fParticleCount == 0) {
                     emptyParticle = true;
                  }
               }

               attrNode = DOMUtil.getNextSiblingElement(complexContentChild);
            } else {
               attrNode = complexContentChild;
            }
         }
      }

      Element node;
      if (emptyParticle) {
         node = DOMUtil.getFirstChildElement(complexContentChild);
         if (node != null && DOMUtil.getLocalName(node).equals(SchemaSymbols.ELT_ANNOTATION)) {
            node = DOMUtil.getNextSiblingElement(node);
         }

         if (node == null) {
            particle = null;
         }
      }

      if (particle == null && isMixed) {
         particle = XSConstraints.getEmptySequence();
      }

      this.fParticle = particle;
      if (this.fParticle == null) {
         this.fContentType = 0;
      } else if (isMixed) {
         this.fContentType = 3;
      } else {
         this.fContentType = 2;
      }

      if (attrNode != null) {
         if (!this.isAttrOrAttrGroup(attrNode)) {
            throw new XSDComplexTypeTraverser.ComplexTypeRecoverableError("s4s-elt-invalid-content.1", new Object[]{this.fName, DOMUtil.getLocalName(attrNode)}, attrNode);
         }

         node = this.traverseAttrsAndAttrGrps(attrNode, this.fAttrGrp, schemaDoc, grammar, this.fComplexTypeDecl);
         if (node != null) {
            throw new XSDComplexTypeTraverser.ComplexTypeRecoverableError("s4s-elt-invalid-content.1", new Object[]{this.fName, DOMUtil.getLocalName(node)}, node);
         }

         if (!isDerivation) {
            this.fAttrGrp.removeProhibitedAttrs();
         }
      }

   }

   private boolean isAttrOrAttrGroup(Element e) {
      String elementName = DOMUtil.getLocalName(e);
      return elementName.equals(SchemaSymbols.ELT_ATTRIBUTE) || elementName.equals(SchemaSymbols.ELT_ATTRIBUTEGROUP) || elementName.equals(SchemaSymbols.ELT_ANYATTRIBUTE);
   }

   private void traverseSimpleContentDecl(Element simpleContentDecl) {
   }

   private void traverseComplexContentDecl(Element complexContentDecl, boolean mixedOnComplexTypeDecl) {
   }

   private String genAnonTypeName(Element complexTypeDecl) {
      StringBuffer typeName = new StringBuffer("#AnonType_");

      for(Element node = DOMUtil.getParent(complexTypeDecl); node != null && node != DOMUtil.getRoot(DOMUtil.getDocument(node)); node = DOMUtil.getParent(node)) {
         typeName.append(node.getAttribute(SchemaSymbols.ATT_NAME));
      }

      return typeName.toString();
   }

   private void handleComplexTypeError(String messageId, Object[] args, Element e) {
      if (messageId != null) {
         this.reportSchemaError(messageId, args, e);
      }

      this.fBaseType = SchemaGrammar.fAnyType;
      this.fContentType = 3;
      this.fXSSimpleType = null;
      this.fParticle = getErrorContent();
      this.fAttrGrp.fAttributeWC = getErrorWildcard();
   }

   private void contentBackup() {
      if (this.fGlobalStore == null) {
         this.fGlobalStore = new Object[11];
         this.fGlobalStorePos = 0;
      }

      if (this.fGlobalStorePos == this.fGlobalStore.length) {
         Object[] newArray = new Object[this.fGlobalStorePos + 11];
         System.arraycopy(this.fGlobalStore, 0, newArray, 0, this.fGlobalStorePos);
         this.fGlobalStore = newArray;
      }

      this.fGlobalStore[this.fGlobalStorePos++] = this.fComplexTypeDecl;
      this.fGlobalStore[this.fGlobalStorePos++] = this.fIsAbstract ? Boolean.TRUE : Boolean.FALSE;
      this.fGlobalStore[this.fGlobalStorePos++] = this.fName;
      this.fGlobalStore[this.fGlobalStorePos++] = this.fTargetNamespace;
      this.fGlobalStore[this.fGlobalStorePos++] = new Integer((this.fDerivedBy << 16) + this.fFinal);
      this.fGlobalStore[this.fGlobalStorePos++] = new Integer((this.fBlock << 16) + this.fContentType);
      this.fGlobalStore[this.fGlobalStorePos++] = this.fBaseType;
      this.fGlobalStore[this.fGlobalStorePos++] = this.fAttrGrp;
      this.fGlobalStore[this.fGlobalStorePos++] = this.fParticle;
      this.fGlobalStore[this.fGlobalStorePos++] = this.fXSSimpleType;
      this.fGlobalStore[this.fGlobalStorePos++] = this.fAnnotations;
   }

   private void contentRestore() {
      this.fAnnotations = (XSAnnotationImpl[])((XSAnnotationImpl[])this.fGlobalStore[--this.fGlobalStorePos]);
      this.fXSSimpleType = (XSSimpleType)this.fGlobalStore[--this.fGlobalStorePos];
      this.fParticle = (XSParticleDecl)this.fGlobalStore[--this.fGlobalStorePos];
      this.fAttrGrp = (XSAttributeGroupDecl)this.fGlobalStore[--this.fGlobalStorePos];
      this.fBaseType = (XSTypeDefinition)this.fGlobalStore[--this.fGlobalStorePos];
      int i = (Integer)((Integer)this.fGlobalStore[--this.fGlobalStorePos]);
      this.fBlock = (short)(i >> 16);
      this.fContentType = (short)i;
      i = (Integer)((Integer)this.fGlobalStore[--this.fGlobalStorePos]);
      this.fDerivedBy = (short)(i >> 16);
      this.fFinal = (short)i;
      this.fTargetNamespace = (String)this.fGlobalStore[--this.fGlobalStorePos];
      this.fName = (String)this.fGlobalStore[--this.fGlobalStorePos];
      this.fIsAbstract = (Boolean)this.fGlobalStore[--this.fGlobalStorePos];
      this.fComplexTypeDecl = (XSComplexTypeDecl)this.fGlobalStore[--this.fGlobalStorePos];
   }

   private void addAnnotation(XSAnnotationImpl annotation) {
      if (annotation != null) {
         if (this.fAnnotations == null) {
            this.fAnnotations = new XSAnnotationImpl[1];
         } else {
            XSAnnotationImpl[] tempArray = new XSAnnotationImpl[this.fAnnotations.length + 1];
            System.arraycopy(this.fAnnotations, 0, tempArray, 0, this.fAnnotations.length);
            this.fAnnotations = tempArray;
         }

         this.fAnnotations[this.fAnnotations.length - 1] = annotation;
      }
   }

   private static final class ComplexTypeRecoverableError extends Exception {
      private static final long serialVersionUID = 6802729912091130335L;
      Object[] errorSubstText = null;
      Element errorElem = null;

      ComplexTypeRecoverableError() {
      }

      ComplexTypeRecoverableError(String msgKey, Object[] args, Element e) {
         super(msgKey);
         this.errorSubstText = args;
         this.errorElem = e;
      }
   }
}
