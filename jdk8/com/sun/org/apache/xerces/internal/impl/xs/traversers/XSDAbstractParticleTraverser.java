package com.sun.org.apache.xerces.internal.impl.xs.traversers;

import com.sun.org.apache.xerces.internal.impl.xs.SchemaGrammar;
import com.sun.org.apache.xerces.internal.impl.xs.SchemaSymbols;
import com.sun.org.apache.xerces.internal.impl.xs.XSAnnotationImpl;
import com.sun.org.apache.xerces.internal.impl.xs.XSModelGroupImpl;
import com.sun.org.apache.xerces.internal.impl.xs.XSParticleDecl;
import com.sun.org.apache.xerces.internal.impl.xs.util.XInt;
import com.sun.org.apache.xerces.internal.impl.xs.util.XSObjectListImpl;
import com.sun.org.apache.xerces.internal.util.DOMUtil;
import com.sun.org.apache.xerces.internal.xs.XSObject;
import org.w3c.dom.Element;

abstract class XSDAbstractParticleTraverser extends XSDAbstractTraverser {
   XSDAbstractParticleTraverser.ParticleArray fPArray = new XSDAbstractParticleTraverser.ParticleArray();

   XSDAbstractParticleTraverser(XSDHandler handler, XSAttributeChecker gAttrCheck) {
      super(handler, gAttrCheck);
   }

   XSParticleDecl traverseAll(Element allDecl, XSDocumentInfo schemaDoc, SchemaGrammar grammar, int allContextFlags, XSObject parent) {
      Object[] attrValues = this.fAttrChecker.checkAttributes(allDecl, false, schemaDoc);
      Element child = DOMUtil.getFirstChildElement(allDecl);
      XSAnnotationImpl annotation = null;
      String childName;
      if (child != null && DOMUtil.getLocalName(child).equals(SchemaSymbols.ELT_ANNOTATION)) {
         annotation = this.traverseAnnotationDecl(child, attrValues, false, schemaDoc);
         child = DOMUtil.getNextSiblingElement(child);
      } else {
         childName = DOMUtil.getSyntheticAnnotation(allDecl);
         if (childName != null) {
            annotation = this.traverseSyntheticAnnotation(allDecl, childName, attrValues, false, schemaDoc);
         }
      }

      childName = null;
      this.fPArray.pushContext();

      XSParticleDecl particle;
      for(; child != null; child = DOMUtil.getNextSiblingElement(child)) {
         particle = null;
         childName = DOMUtil.getLocalName(child);
         if (childName.equals(SchemaSymbols.ELT_ELEMENT)) {
            particle = this.fSchemaHandler.fElementTraverser.traverseLocal(child, schemaDoc, grammar, 1, parent);
         } else {
            Object[] args = new Object[]{"all", "(annotation?, element*)", DOMUtil.getLocalName(child)};
            this.reportSchemaError("s4s-elt-must-match.1", args, child);
         }

         if (particle != null) {
            this.fPArray.addParticle(particle);
         }
      }

      particle = null;
      XInt minAtt = (XInt)attrValues[XSAttributeChecker.ATTIDX_MINOCCURS];
      XInt maxAtt = (XInt)attrValues[XSAttributeChecker.ATTIDX_MAXOCCURS];
      Long defaultVals = (Long)attrValues[XSAttributeChecker.ATTIDX_FROMDEFAULT];
      XSModelGroupImpl group = new XSModelGroupImpl();
      group.fCompositor = 103;
      group.fParticleCount = this.fPArray.getParticleCount();
      group.fParticles = this.fPArray.popContext();
      XSObjectListImpl annotations;
      if (annotation != null) {
         annotations = new XSObjectListImpl();
         ((XSObjectListImpl)annotations).addXSObject(annotation);
      } else {
         annotations = XSObjectListImpl.EMPTY_LIST;
      }

      group.fAnnotations = annotations;
      particle = new XSParticleDecl();
      particle.fType = 3;
      particle.fMinOccurs = minAtt.intValue();
      particle.fMaxOccurs = maxAtt.intValue();
      particle.fValue = group;
      particle.fAnnotations = annotations;
      particle = this.checkOccurrences(particle, SchemaSymbols.ELT_ALL, (Element)allDecl.getParentNode(), allContextFlags, defaultVals);
      this.fAttrChecker.returnAttrArray(attrValues, schemaDoc);
      return particle;
   }

   XSParticleDecl traverseSequence(Element seqDecl, XSDocumentInfo schemaDoc, SchemaGrammar grammar, int allContextFlags, XSObject parent) {
      return this.traverseSeqChoice(seqDecl, schemaDoc, grammar, allContextFlags, false, parent);
   }

   XSParticleDecl traverseChoice(Element choiceDecl, XSDocumentInfo schemaDoc, SchemaGrammar grammar, int allContextFlags, XSObject parent) {
      return this.traverseSeqChoice(choiceDecl, schemaDoc, grammar, allContextFlags, true, parent);
   }

   private XSParticleDecl traverseSeqChoice(Element decl, XSDocumentInfo schemaDoc, SchemaGrammar grammar, int allContextFlags, boolean choice, XSObject parent) {
      Object[] attrValues = this.fAttrChecker.checkAttributes(decl, false, schemaDoc);
      Element child = DOMUtil.getFirstChildElement(decl);
      XSAnnotationImpl annotation = null;
      String childName;
      if (child != null && DOMUtil.getLocalName(child).equals(SchemaSymbols.ELT_ANNOTATION)) {
         annotation = this.traverseAnnotationDecl(child, attrValues, false, schemaDoc);
         child = DOMUtil.getNextSiblingElement(child);
      } else {
         childName = DOMUtil.getSyntheticAnnotation(decl);
         if (childName != null) {
            annotation = this.traverseSyntheticAnnotation(decl, childName, attrValues, false, schemaDoc);
         }
      }

      childName = null;
      this.fPArray.pushContext();

      XSParticleDecl particle;
      for(; child != null; child = DOMUtil.getNextSiblingElement(child)) {
         particle = null;
         childName = DOMUtil.getLocalName(child);
         if (childName.equals(SchemaSymbols.ELT_ELEMENT)) {
            particle = this.fSchemaHandler.fElementTraverser.traverseLocal(child, schemaDoc, grammar, 0, parent);
         } else if (childName.equals(SchemaSymbols.ELT_GROUP)) {
            particle = this.fSchemaHandler.fGroupTraverser.traverseLocal(child, schemaDoc, grammar);
            if (this.hasAllContent(particle)) {
               particle = null;
               this.reportSchemaError("cos-all-limited.1.2", (Object[])null, child);
            }
         } else if (childName.equals(SchemaSymbols.ELT_CHOICE)) {
            particle = this.traverseChoice(child, schemaDoc, grammar, 0, parent);
         } else if (childName.equals(SchemaSymbols.ELT_SEQUENCE)) {
            particle = this.traverseSequence(child, schemaDoc, grammar, 0, parent);
         } else if (childName.equals(SchemaSymbols.ELT_ANY)) {
            particle = this.fSchemaHandler.fWildCardTraverser.traverseAny(child, schemaDoc, grammar);
         } else {
            Object[] args;
            if (choice) {
               args = new Object[]{"choice", "(annotation?, (element | group | choice | sequence | any)*)", DOMUtil.getLocalName(child)};
            } else {
               args = new Object[]{"sequence", "(annotation?, (element | group | choice | sequence | any)*)", DOMUtil.getLocalName(child)};
            }

            this.reportSchemaError("s4s-elt-must-match.1", args, child);
         }

         if (particle != null) {
            this.fPArray.addParticle(particle);
         }
      }

      particle = null;
      XInt minAtt = (XInt)attrValues[XSAttributeChecker.ATTIDX_MINOCCURS];
      XInt maxAtt = (XInt)attrValues[XSAttributeChecker.ATTIDX_MAXOCCURS];
      Long defaultVals = (Long)attrValues[XSAttributeChecker.ATTIDX_FROMDEFAULT];
      XSModelGroupImpl group = new XSModelGroupImpl();
      group.fCompositor = (short)(choice ? 101 : 102);
      group.fParticleCount = this.fPArray.getParticleCount();
      group.fParticles = this.fPArray.popContext();
      XSObjectListImpl annotations;
      if (annotation != null) {
         annotations = new XSObjectListImpl();
         ((XSObjectListImpl)annotations).addXSObject(annotation);
      } else {
         annotations = XSObjectListImpl.EMPTY_LIST;
      }

      group.fAnnotations = annotations;
      particle = new XSParticleDecl();
      particle.fType = 3;
      particle.fMinOccurs = minAtt.intValue();
      particle.fMaxOccurs = maxAtt.intValue();
      particle.fValue = group;
      particle.fAnnotations = annotations;
      particle = this.checkOccurrences(particle, choice ? SchemaSymbols.ELT_CHOICE : SchemaSymbols.ELT_SEQUENCE, (Element)decl.getParentNode(), allContextFlags, defaultVals);
      this.fAttrChecker.returnAttrArray(attrValues, schemaDoc);
      return particle;
   }

   protected boolean hasAllContent(XSParticleDecl particle) {
      if (particle != null && particle.fType == 3) {
         return ((XSModelGroupImpl)particle.fValue).fCompositor == 103;
      } else {
         return false;
      }
   }

   protected static class ParticleArray {
      XSParticleDecl[] fParticles = new XSParticleDecl[10];
      int[] fPos = new int[5];
      int fContextCount = 0;

      void pushContext() {
         ++this.fContextCount;
         if (this.fContextCount == this.fPos.length) {
            int newSize = this.fContextCount * 2;
            int[] newArray = new int[newSize];
            System.arraycopy(this.fPos, 0, newArray, 0, this.fContextCount);
            this.fPos = newArray;
         }

         this.fPos[this.fContextCount] = this.fPos[this.fContextCount - 1];
      }

      int getParticleCount() {
         return this.fPos[this.fContextCount] - this.fPos[this.fContextCount - 1];
      }

      void addParticle(XSParticleDecl particle) {
         if (this.fPos[this.fContextCount] == this.fParticles.length) {
            int newSize = this.fPos[this.fContextCount] * 2;
            XSParticleDecl[] newArray = new XSParticleDecl[newSize];
            System.arraycopy(this.fParticles, 0, newArray, 0, this.fPos[this.fContextCount]);
            this.fParticles = newArray;
         }

         this.fParticles[this.fPos[this.fContextCount]++] = particle;
      }

      XSParticleDecl[] popContext() {
         int count = this.fPos[this.fContextCount] - this.fPos[this.fContextCount - 1];
         XSParticleDecl[] array = null;
         if (count != 0) {
            array = new XSParticleDecl[count];
            System.arraycopy(this.fParticles, this.fPos[this.fContextCount - 1], array, 0, count);

            for(int i = this.fPos[this.fContextCount - 1]; i < this.fPos[this.fContextCount]; ++i) {
               this.fParticles[i] = null;
            }
         }

         --this.fContextCount;
         return array;
      }
   }
}
