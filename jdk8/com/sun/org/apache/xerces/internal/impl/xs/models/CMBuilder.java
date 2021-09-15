package com.sun.org.apache.xerces.internal.impl.xs.models;

import com.sun.org.apache.xerces.internal.impl.dtd.models.CMNode;
import com.sun.org.apache.xerces.internal.impl.xs.XSComplexTypeDecl;
import com.sun.org.apache.xerces.internal.impl.xs.XSDeclarationPool;
import com.sun.org.apache.xerces.internal.impl.xs.XSElementDecl;
import com.sun.org.apache.xerces.internal.impl.xs.XSModelGroupImpl;
import com.sun.org.apache.xerces.internal.impl.xs.XSParticleDecl;

public class CMBuilder {
   private XSDeclarationPool fDeclPool = null;
   private static XSEmptyCM fEmptyCM = new XSEmptyCM();
   private int fLeafCount;
   private int fParticleCount;
   private CMNodeFactory fNodeFactory;

   public CMBuilder(CMNodeFactory nodeFactory) {
      this.fDeclPool = null;
      this.fNodeFactory = nodeFactory;
   }

   public void setDeclPool(XSDeclarationPool declPool) {
      this.fDeclPool = declPool;
   }

   public XSCMValidator getContentModel(XSComplexTypeDecl typeDecl) {
      short contentType = typeDecl.getContentType();
      if (contentType != 1 && contentType != 0) {
         XSParticleDecl particle = (XSParticleDecl)typeDecl.getParticle();
         if (particle == null) {
            return fEmptyCM;
         } else {
            XSCMValidator cmValidator = null;
            if (particle.fType == 3 && ((XSModelGroupImpl)particle.fValue).fCompositor == 103) {
               cmValidator = this.createAllCM(particle);
            } else {
               cmValidator = this.createDFACM(particle);
            }

            this.fNodeFactory.resetNodeCount();
            if (cmValidator == null) {
               cmValidator = fEmptyCM;
            }

            return (XSCMValidator)cmValidator;
         }
      } else {
         return null;
      }
   }

   XSCMValidator createAllCM(XSParticleDecl particle) {
      if (particle.fMaxOccurs == 0) {
         return null;
      } else {
         XSModelGroupImpl group = (XSModelGroupImpl)particle.fValue;
         XSAllCM allContent = new XSAllCM(particle.fMinOccurs == 0, group.fParticleCount);

         for(int i = 0; i < group.fParticleCount; ++i) {
            allContent.addElement((XSElementDecl)group.fParticles[i].fValue, group.fParticles[i].fMinOccurs == 0);
         }

         return allContent;
      }
   }

   XSCMValidator createDFACM(XSParticleDecl particle) {
      this.fLeafCount = 0;
      this.fParticleCount = 0;
      CMNode node = this.useRepeatingLeafNodes(particle) ? this.buildCompactSyntaxTree(particle) : this.buildSyntaxTree(particle, true);
      return node == null ? null : new XSDFACM(node, this.fLeafCount);
   }

   private CMNode buildSyntaxTree(XSParticleDecl particle, boolean optimize) {
      int maxOccurs = particle.fMaxOccurs;
      int minOccurs = particle.fMinOccurs;
      short type = particle.fType;
      CMNode nodeRet = null;
      if (type != 2 && type != 1) {
         if (type == 3) {
            XSModelGroupImpl group = (XSModelGroupImpl)particle.fValue;
            CMNode temp = null;
            boolean twoChildren = false;

            for(int i = 0; i < group.fParticleCount; ++i) {
               temp = this.buildSyntaxTree(group.fParticles[i], optimize && minOccurs == 1 && maxOccurs == 1 && (group.fCompositor == 102 || group.fParticleCount == 1));
               if (temp != null) {
                  if (nodeRet == null) {
                     nodeRet = temp;
                  } else {
                     nodeRet = this.fNodeFactory.getCMBinOpNode(group.fCompositor, nodeRet, temp);
                     twoChildren = true;
                  }
               }
            }

            if (nodeRet != null) {
               if (group.fCompositor == 101 && !twoChildren && group.fParticleCount > 1) {
                  nodeRet = this.fNodeFactory.getCMUniOpNode(5, nodeRet);
               }

               nodeRet = this.expandContentModel(nodeRet, minOccurs, maxOccurs, false);
            }
         }
      } else {
         nodeRet = this.fNodeFactory.getCMLeafNode(particle.fType, particle.fValue, this.fParticleCount++, this.fLeafCount++);
         nodeRet = this.expandContentModel(nodeRet, minOccurs, maxOccurs, optimize);
      }

      return nodeRet;
   }

   private CMNode expandContentModel(CMNode node, int minOccurs, int maxOccurs, boolean optimize) {
      CMNode nodeRet = null;
      if (minOccurs == 1 && maxOccurs == 1) {
         nodeRet = node;
      } else if (minOccurs == 0 && maxOccurs == 1) {
         nodeRet = this.fNodeFactory.getCMUniOpNode(5, node);
      } else if (minOccurs == 0 && maxOccurs == -1) {
         nodeRet = this.fNodeFactory.getCMUniOpNode(4, node);
      } else if (minOccurs == 1 && maxOccurs == -1) {
         nodeRet = this.fNodeFactory.getCMUniOpNode(6, node);
      } else if ((!optimize || node.type() != 1) && node.type() != 2) {
         if (maxOccurs == -1) {
            nodeRet = this.fNodeFactory.getCMUniOpNode(6, node);
            nodeRet = this.fNodeFactory.getCMBinOpNode(102, this.multiNodes(node, minOccurs - 1, true), nodeRet);
         } else {
            if (minOccurs > 0) {
               nodeRet = this.multiNodes(node, minOccurs, false);
            }

            if (maxOccurs > minOccurs) {
               node = this.fNodeFactory.getCMUniOpNode(5, node);
               if (nodeRet == null) {
                  nodeRet = this.multiNodes(node, maxOccurs - minOccurs, false);
               } else {
                  nodeRet = this.fNodeFactory.getCMBinOpNode(102, nodeRet, this.multiNodes(node, maxOccurs - minOccurs, true));
               }
            }
         }
      } else {
         nodeRet = this.fNodeFactory.getCMUniOpNode(minOccurs == 0 ? 4 : 6, node);
         nodeRet.setUserData(new int[]{minOccurs, maxOccurs});
      }

      return nodeRet;
   }

   private CMNode multiNodes(CMNode node, int num, boolean copyFirst) {
      if (num == 0) {
         return null;
      } else if (num == 1) {
         return copyFirst ? this.copyNode(node) : node;
      } else {
         int num1 = num / 2;
         return this.fNodeFactory.getCMBinOpNode(102, this.multiNodes(node, num1, copyFirst), this.multiNodes(node, num - num1, true));
      }
   }

   private CMNode copyNode(CMNode node) {
      int type = node.type();
      if (type != 101 && type != 102) {
         if (type != 4 && type != 6 && type != 5) {
            if (type == 1 || type == 2) {
               XSCMLeaf leaf = (XSCMLeaf)node;
               node = this.fNodeFactory.getCMLeafNode(leaf.type(), leaf.getLeaf(), leaf.getParticleId(), this.fLeafCount++);
            }
         } else {
            XSCMUniOp uni = (XSCMUniOp)node;
            node = this.fNodeFactory.getCMUniOpNode(type, this.copyNode(uni.getChild()));
         }
      } else {
         XSCMBinOp bin = (XSCMBinOp)node;
         node = this.fNodeFactory.getCMBinOpNode(type, this.copyNode(bin.getLeft()), this.copyNode(bin.getRight()));
      }

      return node;
   }

   private CMNode buildCompactSyntaxTree(XSParticleDecl particle) {
      int maxOccurs = particle.fMaxOccurs;
      int minOccurs = particle.fMinOccurs;
      short type = particle.fType;
      CMNode nodeRet = null;
      if (type != 2 && type != 1) {
         if (type == 3) {
            XSModelGroupImpl group = (XSModelGroupImpl)particle.fValue;
            if (group.fParticleCount == 1 && (minOccurs != 1 || maxOccurs != 1)) {
               return this.buildCompactSyntaxTree2(group.fParticles[0], minOccurs, maxOccurs);
            }

            CMNode temp = null;
            int count = 0;

            for(int i = 0; i < group.fParticleCount; ++i) {
               temp = this.buildCompactSyntaxTree(group.fParticles[i]);
               if (temp != null) {
                  ++count;
                  if (nodeRet == null) {
                     nodeRet = temp;
                  } else {
                     nodeRet = this.fNodeFactory.getCMBinOpNode(group.fCompositor, nodeRet, temp);
                  }
               }
            }

            if (nodeRet != null && group.fCompositor == 101 && count < group.fParticleCount) {
               nodeRet = this.fNodeFactory.getCMUniOpNode(5, nodeRet);
            }
         }

         return nodeRet;
      } else {
         return this.buildCompactSyntaxTree2(particle, minOccurs, maxOccurs);
      }
   }

   private CMNode buildCompactSyntaxTree2(XSParticleDecl particle, int minOccurs, int maxOccurs) {
      CMNode nodeRet = null;
      if (minOccurs == 1 && maxOccurs == 1) {
         nodeRet = this.fNodeFactory.getCMLeafNode(particle.fType, particle.fValue, this.fParticleCount++, this.fLeafCount++);
      } else if (minOccurs == 0 && maxOccurs == 1) {
         nodeRet = this.fNodeFactory.getCMLeafNode(particle.fType, particle.fValue, this.fParticleCount++, this.fLeafCount++);
         nodeRet = this.fNodeFactory.getCMUniOpNode(5, nodeRet);
      } else if (minOccurs == 0 && maxOccurs == -1) {
         nodeRet = this.fNodeFactory.getCMLeafNode(particle.fType, particle.fValue, this.fParticleCount++, this.fLeafCount++);
         nodeRet = this.fNodeFactory.getCMUniOpNode(4, nodeRet);
      } else if (minOccurs == 1 && maxOccurs == -1) {
         nodeRet = this.fNodeFactory.getCMLeafNode(particle.fType, particle.fValue, this.fParticleCount++, this.fLeafCount++);
         nodeRet = this.fNodeFactory.getCMUniOpNode(6, nodeRet);
      } else {
         nodeRet = this.fNodeFactory.getCMRepeatingLeafNode(particle.fType, particle.fValue, minOccurs, maxOccurs, this.fParticleCount++, this.fLeafCount++);
         if (minOccurs == 0) {
            nodeRet = this.fNodeFactory.getCMUniOpNode(4, nodeRet);
         } else {
            nodeRet = this.fNodeFactory.getCMUniOpNode(6, nodeRet);
         }
      }

      return nodeRet;
   }

   private boolean useRepeatingLeafNodes(XSParticleDecl particle) {
      int maxOccurs = particle.fMaxOccurs;
      int minOccurs = particle.fMinOccurs;
      short type = particle.fType;
      if (type == 3) {
         XSModelGroupImpl group = (XSModelGroupImpl)particle.fValue;
         if (minOccurs != 1 || maxOccurs != 1) {
            if (group.fParticleCount != 1) {
               return group.fParticleCount == 0;
            }

            XSParticleDecl particle2 = group.fParticles[0];
            short type2 = particle2.fType;
            return (type2 == 1 || type2 == 2) && particle2.fMinOccurs == 1 && particle2.fMaxOccurs == 1;
         }

         for(int i = 0; i < group.fParticleCount; ++i) {
            if (!this.useRepeatingLeafNodes(group.fParticles[i])) {
               return false;
            }
         }
      }

      return true;
   }
}
