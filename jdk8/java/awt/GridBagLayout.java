package java.awt;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Hashtable;

public class GridBagLayout implements LayoutManager2, Serializable {
   static final int EMPIRICMULTIPLIER = 2;
   protected static final int MAXGRIDSIZE = 512;
   protected static final int MINSIZE = 1;
   protected static final int PREFERREDSIZE = 2;
   protected Hashtable<Component, GridBagConstraints> comptable = new Hashtable();
   protected GridBagConstraints defaultConstraints = new GridBagConstraints();
   protected GridBagLayoutInfo layoutInfo;
   public int[] columnWidths;
   public int[] rowHeights;
   public double[] columnWeights;
   public double[] rowWeights;
   private Component componentAdjusting;
   transient boolean rightToLeft = false;
   static final long serialVersionUID = 8838754796412211005L;

   public void setConstraints(Component var1, GridBagConstraints var2) {
      this.comptable.put(var1, (GridBagConstraints)var2.clone());
   }

   public GridBagConstraints getConstraints(Component var1) {
      GridBagConstraints var2 = (GridBagConstraints)this.comptable.get(var1);
      if (var2 == null) {
         this.setConstraints(var1, this.defaultConstraints);
         var2 = (GridBagConstraints)this.comptable.get(var1);
      }

      return (GridBagConstraints)var2.clone();
   }

   protected GridBagConstraints lookupConstraints(Component var1) {
      GridBagConstraints var2 = (GridBagConstraints)this.comptable.get(var1);
      if (var2 == null) {
         this.setConstraints(var1, this.defaultConstraints);
         var2 = (GridBagConstraints)this.comptable.get(var1);
      }

      return var2;
   }

   private void removeConstraints(Component var1) {
      this.comptable.remove(var1);
   }

   public Point getLayoutOrigin() {
      Point var1 = new Point(0, 0);
      if (this.layoutInfo != null) {
         var1.x = this.layoutInfo.startx;
         var1.y = this.layoutInfo.starty;
      }

      return var1;
   }

   public int[][] getLayoutDimensions() {
      if (this.layoutInfo == null) {
         return new int[2][0];
      } else {
         int[][] var1 = new int[][]{new int[this.layoutInfo.width], new int[this.layoutInfo.height]};
         System.arraycopy(this.layoutInfo.minWidth, 0, var1[0], 0, this.layoutInfo.width);
         System.arraycopy(this.layoutInfo.minHeight, 0, var1[1], 0, this.layoutInfo.height);
         return var1;
      }
   }

   public double[][] getLayoutWeights() {
      if (this.layoutInfo == null) {
         return new double[2][0];
      } else {
         double[][] var1 = new double[][]{new double[this.layoutInfo.width], new double[this.layoutInfo.height]};
         System.arraycopy(this.layoutInfo.weightX, 0, var1[0], 0, this.layoutInfo.width);
         System.arraycopy(this.layoutInfo.weightY, 0, var1[1], 0, this.layoutInfo.height);
         return var1;
      }
   }

   public Point location(int var1, int var2) {
      Point var3 = new Point(0, 0);
      if (this.layoutInfo == null) {
         return var3;
      } else {
         int var5 = this.layoutInfo.startx;
         int var4;
         if (!this.rightToLeft) {
            for(var4 = 0; var4 < this.layoutInfo.width; ++var4) {
               var5 += this.layoutInfo.minWidth[var4];
               if (var5 > var1) {
                  break;
               }
            }
         } else {
            for(var4 = this.layoutInfo.width - 1; var4 >= 0 && var5 <= var1; --var4) {
               var5 += this.layoutInfo.minWidth[var4];
            }

            ++var4;
         }

         var3.x = var4;
         var5 = this.layoutInfo.starty;

         for(var4 = 0; var4 < this.layoutInfo.height; ++var4) {
            var5 += this.layoutInfo.minHeight[var4];
            if (var5 > var2) {
               break;
            }
         }

         var3.y = var4;
         return var3;
      }
   }

   public void addLayoutComponent(String var1, Component var2) {
   }

   public void addLayoutComponent(Component var1, Object var2) {
      if (var2 instanceof GridBagConstraints) {
         this.setConstraints(var1, (GridBagConstraints)var2);
      } else if (var2 != null) {
         throw new IllegalArgumentException("cannot add to layout: constraints must be a GridBagConstraint");
      }

   }

   public void removeLayoutComponent(Component var1) {
      this.removeConstraints(var1);
   }

   public Dimension preferredLayoutSize(Container var1) {
      GridBagLayoutInfo var2 = this.getLayoutInfo(var1, 2);
      return this.getMinSize(var1, var2);
   }

   public Dimension minimumLayoutSize(Container var1) {
      GridBagLayoutInfo var2 = this.getLayoutInfo(var1, 1);
      return this.getMinSize(var1, var2);
   }

   public Dimension maximumLayoutSize(Container var1) {
      return new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE);
   }

   public float getLayoutAlignmentX(Container var1) {
      return 0.5F;
   }

   public float getLayoutAlignmentY(Container var1) {
      return 0.5F;
   }

   public void invalidateLayout(Container var1) {
   }

   public void layoutContainer(Container var1) {
      this.arrangeGrid(var1);
   }

   public String toString() {
      return this.getClass().getName();
   }

   protected GridBagLayoutInfo getLayoutInfo(Container var1, int var2) {
      return this.GetLayoutInfo(var1, var2);
   }

   private long[] preInitMaximumArraySizes(Container var1) {
      Component[] var2 = var1.getComponents();
      int var9 = 0;
      int var10 = 0;
      long[] var11 = new long[2];

      for(int var12 = 0; var12 < var2.length; ++var12) {
         Component var3 = var2[var12];
         if (var3.isVisible()) {
            GridBagConstraints var4 = this.lookupConstraints(var3);
            int var5 = var4.gridx;
            int var6 = var4.gridy;
            int var7 = var4.gridwidth;
            int var8 = var4.gridheight;
            if (var5 < 0) {
               ++var10;
               var5 = var10;
            }

            if (var6 < 0) {
               ++var9;
               var6 = var9;
            }

            if (var7 <= 0) {
               var7 = 1;
            }

            if (var8 <= 0) {
               var8 = 1;
            }

            var9 = Math.max(var6 + var8, var9);
            var10 = Math.max(var5 + var7, var10);
         }
      }

      var11[0] = (long)var9;
      var11[1] = (long)var10;
      return var11;
   }

   protected GridBagLayoutInfo GetLayoutInfo(Container var1, int var2) {
      synchronized(var1.getTreeLock()) {
         Component[] var8 = var1.getComponents();
         boolean var20 = false;
         boolean var21 = false;
         boolean var22 = true;
         boolean var23 = true;
         boolean var30 = false;
         boolean var31 = false;
         int var10 = 0;
         int var9 = 0;
         int var25 = -1;
         int var24 = -1;
         long[] var33 = this.preInitMaximumArraySizes(var1);
         int var48 = 2L * var33[0] > 2147483647L ? Integer.MAX_VALUE : 2 * (int)var33[0];
         int var49 = 2L * var33[1] > 2147483647L ? Integer.MAX_VALUE : 2 * (int)var33[1];
         if (this.rowHeights != null) {
            var48 = Math.max(var48, this.rowHeights.length);
         }

         if (this.columnWidths != null) {
            var49 = Math.max(var49, this.columnWidths.length);
         }

         int[] var11 = new int[var48];
         int[] var12 = new int[var49];
         boolean var34 = false;

         Component var5;
         GridBagConstraints var6;
         int var13;
         int var14;
         int var16;
         int var17;
         int var44;
         int var45;
         int var46;
         int var47;
         for(var13 = 0; var13 < var8.length; ++var13) {
            var5 = var8[var13];
            if (var5.isVisible()) {
               var6 = this.lookupConstraints(var5);
               var44 = var6.gridx;
               var45 = var6.gridy;
               var46 = var6.gridwidth;
               if (var46 <= 0) {
                  var46 = 1;
               }

               var47 = var6.gridheight;
               if (var47 <= 0) {
                  var47 = 1;
               }

               if (var44 < 0 && var45 < 0) {
                  if (var24 >= 0) {
                     var45 = var24;
                  } else if (var25 >= 0) {
                     var44 = var25;
                  } else {
                     var45 = 0;
                  }
               }

               if (var44 < 0) {
                  var16 = 0;

                  for(var14 = var45; var14 < var45 + var47; ++var14) {
                     var16 = Math.max(var16, var11[var14]);
                  }

                  var44 = var16 - var44 - 1;
                  if (var44 < 0) {
                     var44 = 0;
                  }
               } else if (var45 < 0) {
                  var17 = 0;

                  for(var14 = var44; var14 < var44 + var46; ++var14) {
                     var17 = Math.max(var17, var12[var14]);
                  }

                  var45 = var17 - var45 - 1;
                  if (var45 < 0) {
                     var45 = 0;
                  }
               }

               var16 = var44 + var46;
               if (var9 < var16) {
                  var9 = var16;
               }

               var17 = var45 + var47;
               if (var10 < var17) {
                  var10 = var17;
               }

               for(var14 = var44; var14 < var44 + var46; ++var14) {
                  var12[var14] = var17;
               }

               for(var14 = var45; var14 < var45 + var47; ++var14) {
                  var11[var14] = var16;
               }

               Dimension var7;
               if (var2 == 2) {
                  var7 = var5.getPreferredSize();
               } else {
                  var7 = var5.getMinimumSize();
               }

               var6.minWidth = var7.width;
               var6.minHeight = var7.height;
               if (this.calculateBaseline(var5, var6, var7)) {
                  var34 = true;
               }

               if (var6.gridheight == 0 && var6.gridwidth == 0) {
                  var25 = -1;
                  var24 = -1;
               }

               if (var6.gridheight == 0 && var24 < 0) {
                  var25 = var44 + var46;
               } else if (var6.gridwidth == 0 && var25 < 0) {
                  var24 = var45 + var47;
               }
            }
         }

         if (this.columnWidths != null && var9 < this.columnWidths.length) {
            var9 = this.columnWidths.length;
         }

         if (this.rowHeights != null && var10 < this.rowHeights.length) {
            var10 = this.rowHeights.length;
         }

         GridBagLayoutInfo var4 = new GridBagLayoutInfo(var9, var10);
         var25 = -1;
         var24 = -1;
         Arrays.fill((int[])var11, (int)0);
         Arrays.fill((int[])var12, (int)0);
         int[] var35 = null;
         int[] var36 = null;
         short[] var37 = null;
         if (var34) {
            var4.maxAscent = var35 = new int[var10];
            var4.maxDescent = var36 = new int[var10];
            var4.baselineType = var37 = new short[var10];
            var4.hasBaseline = true;
         }

         int var18;
         for(var13 = 0; var13 < var8.length; ++var13) {
            var5 = var8[var13];
            if (var5.isVisible()) {
               var6 = this.lookupConstraints(var5);
               var44 = var6.gridx;
               var45 = var6.gridy;
               var46 = var6.gridwidth;
               var47 = var6.gridheight;
               if (var44 < 0 && var45 < 0) {
                  if (var24 >= 0) {
                     var45 = var24;
                  } else if (var25 >= 0) {
                     var44 = var25;
                  } else {
                     var45 = 0;
                  }
               }

               if (var44 < 0) {
                  if (var47 <= 0) {
                     var47 += var4.height - var45;
                     if (var47 < 1) {
                        var47 = 1;
                     }
                  }

                  var16 = 0;

                  for(var14 = var45; var14 < var45 + var47; ++var14) {
                     var16 = Math.max(var16, var11[var14]);
                  }

                  var44 = var16 - var44 - 1;
                  if (var44 < 0) {
                     var44 = 0;
                  }
               } else if (var45 < 0) {
                  if (var46 <= 0) {
                     var46 += var4.width - var44;
                     if (var46 < 1) {
                        var46 = 1;
                     }
                  }

                  var17 = 0;

                  for(var14 = var44; var14 < var44 + var46; ++var14) {
                     var17 = Math.max(var17, var12[var14]);
                  }

                  var45 = var17 - var45 - 1;
                  if (var45 < 0) {
                     var45 = 0;
                  }
               }

               if (var46 <= 0) {
                  var46 += var4.width - var44;
                  if (var46 < 1) {
                     var46 = 1;
                  }
               }

               if (var47 <= 0) {
                  var47 += var4.height - var45;
                  if (var47 < 1) {
                     var47 = 1;
                  }
               }

               var16 = var44 + var46;
               var17 = var45 + var47;

               for(var14 = var44; var14 < var44 + var46; ++var14) {
                  var12[var14] = var17;
               }

               for(var14 = var45; var14 < var45 + var47; ++var14) {
                  var11[var14] = var16;
               }

               if (var6.gridheight == 0 && var6.gridwidth == 0) {
                  var25 = -1;
                  var24 = -1;
               }

               if (var6.gridheight == 0 && var24 < 0) {
                  var25 = var44 + var46;
               } else if (var6.gridwidth == 0 && var25 < 0) {
                  var24 = var45 + var47;
               }

               var6.tempX = var44;
               var6.tempY = var45;
               var6.tempWidth = var46;
               var6.tempHeight = var47;
               int var32 = var6.anchor;
               if (var34) {
                  switch(var32) {
                  case 256:
                  case 512:
                  case 768:
                     if (var6.ascent >= 0) {
                        if (var47 == 1) {
                           var35[var45] = Math.max(var35[var45], var6.ascent);
                           var36[var45] = Math.max(var36[var45], var6.descent);
                        } else if (var6.baselineResizeBehavior == Component.BaselineResizeBehavior.CONSTANT_DESCENT) {
                           var36[var45 + var47 - 1] = Math.max(var36[var45 + var47 - 1], var6.descent);
                        } else {
                           var35[var45] = Math.max(var35[var45], var6.ascent);
                        }

                        if (var6.baselineResizeBehavior == Component.BaselineResizeBehavior.CONSTANT_DESCENT) {
                           var37[var45 + var47 - 1] = (short)(var37[var45 + var47 - 1] | 1 << var6.baselineResizeBehavior.ordinal());
                        } else {
                           var37[var45] = (short)(var37[var45] | 1 << var6.baselineResizeBehavior.ordinal());
                        }
                     }
                     break;
                  case 1024:
                  case 1280:
                  case 1536:
                     var18 = var6.minHeight + var6.insets.top + var6.ipady;
                     var35[var45] = Math.max(var35[var45], var18);
                     var36[var45] = Math.max(var36[var45], var6.insets.bottom);
                     break;
                  case 1792:
                  case 2048:
                  case 2304:
                     var18 = var6.minHeight + var6.insets.bottom + var6.ipady;
                     var36[var45] = Math.max(var36[var45], var18);
                     var35[var45] = Math.max(var35[var45], var6.insets.top);
                  }
               }
            }
         }

         var4.weightX = new double[var49];
         var4.weightY = new double[var48];
         var4.minWidth = new int[var49];
         var4.minHeight = new int[var48];
         if (this.columnWidths != null) {
            System.arraycopy(this.columnWidths, 0, var4.minWidth, 0, this.columnWidths.length);
         }

         if (this.rowHeights != null) {
            System.arraycopy(this.rowHeights, 0, var4.minHeight, 0, this.rowHeights.length);
         }

         if (this.columnWeights != null) {
            System.arraycopy(this.columnWeights, 0, var4.weightX, 0, Math.min(var4.weightX.length, this.columnWeights.length));
         }

         if (this.rowWeights != null) {
            System.arraycopy(this.rowWeights, 0, var4.weightY, 0, Math.min(var4.weightY.length, this.rowWeights.length));
         }

         int var19 = Integer.MAX_VALUE;

         for(var14 = 1; var14 != Integer.MAX_VALUE; var19 = Integer.MAX_VALUE) {
            for(var13 = 0; var13 < var8.length; ++var13) {
               var5 = var8[var13];
               if (var5.isVisible()) {
                  var6 = this.lookupConstraints(var5);
                  int var15;
                  double[] var10000;
                  double var26;
                  double var28;
                  double var38;
                  double var40;
                  int var50;
                  int[] var51;
                  if (var6.tempWidth == var14) {
                     var16 = var6.tempX + var6.tempWidth;
                     var26 = var6.weightx;

                     for(var15 = var6.tempX; var15 < var16; ++var15) {
                        var26 -= var4.weightX[var15];
                     }

                     if (var26 > 0.0D) {
                        var28 = 0.0D;

                        for(var15 = var6.tempX; var15 < var16; ++var15) {
                           var28 += var4.weightX[var15];
                        }

                        for(var15 = var6.tempX; var28 > 0.0D && var15 < var16; ++var15) {
                           var38 = var4.weightX[var15];
                           var40 = var38 * var26 / var28;
                           var10000 = var4.weightX;
                           var10000[var15] += var40;
                           var26 -= var40;
                           var28 -= var38;
                        }

                        var10000 = var4.weightX;
                        var10000[var16 - 1] += var26;
                     }

                     var18 = var6.minWidth + var6.ipadx + var6.insets.left + var6.insets.right;

                     for(var15 = var6.tempX; var15 < var16; ++var15) {
                        var18 -= var4.minWidth[var15];
                     }

                     if (var18 > 0) {
                        var28 = 0.0D;

                        for(var15 = var6.tempX; var15 < var16; ++var15) {
                           var28 += var4.weightX[var15];
                        }

                        for(var15 = var6.tempX; var28 > 0.0D && var15 < var16; ++var15) {
                           var38 = var4.weightX[var15];
                           var50 = (int)(var38 * (double)var18 / var28);
                           var51 = var4.minWidth;
                           var51[var15] += var50;
                           var18 -= var50;
                           var28 -= var38;
                        }

                        var51 = var4.minWidth;
                        var51[var16 - 1] += var18;
                     }
                  } else if (var6.tempWidth > var14 && var6.tempWidth < var19) {
                     var19 = var6.tempWidth;
                  }

                  if (var6.tempHeight != var14) {
                     if (var6.tempHeight > var14 && var6.tempHeight < var19) {
                        var19 = var6.tempHeight;
                     }
                  } else {
                     var17 = var6.tempY + var6.tempHeight;
                     var26 = var6.weighty;

                     for(var15 = var6.tempY; var15 < var17; ++var15) {
                        var26 -= var4.weightY[var15];
                     }

                     if (var26 > 0.0D) {
                        var28 = 0.0D;

                        for(var15 = var6.tempY; var15 < var17; ++var15) {
                           var28 += var4.weightY[var15];
                        }

                        for(var15 = var6.tempY; var28 > 0.0D && var15 < var17; ++var15) {
                           var38 = var4.weightY[var15];
                           var40 = var38 * var26 / var28;
                           var10000 = var4.weightY;
                           var10000[var15] += var40;
                           var26 -= var40;
                           var28 -= var38;
                        }

                        var10000 = var4.weightY;
                        var10000[var17 - 1] += var26;
                     }

                     var18 = -1;
                     if (var34) {
                        switch(var6.anchor) {
                        case 256:
                        case 512:
                        case 768:
                           if (var6.ascent >= 0) {
                              if (var6.tempHeight == 1) {
                                 var18 = var35[var6.tempY] + var36[var6.tempY];
                              } else if (var6.baselineResizeBehavior != Component.BaselineResizeBehavior.CONSTANT_DESCENT) {
                                 var18 = var35[var6.tempY] + var6.descent;
                              } else {
                                 var18 = var6.ascent + var36[var6.tempY + var6.tempHeight - 1];
                              }
                           }
                           break;
                        case 1024:
                        case 1280:
                        case 1536:
                           var18 = var6.insets.top + var6.minHeight + var6.ipady + var36[var6.tempY];
                           break;
                        case 1792:
                        case 2048:
                        case 2304:
                           var18 = var35[var6.tempY] + var6.minHeight + var6.insets.bottom + var6.ipady;
                        }
                     }

                     if (var18 == -1) {
                        var18 = var6.minHeight + var6.ipady + var6.insets.top + var6.insets.bottom;
                     }

                     for(var15 = var6.tempY; var15 < var17; ++var15) {
                        var18 -= var4.minHeight[var15];
                     }

                     if (var18 > 0) {
                        var28 = 0.0D;

                        for(var15 = var6.tempY; var15 < var17; ++var15) {
                           var28 += var4.weightY[var15];
                        }

                        for(var15 = var6.tempY; var28 > 0.0D && var15 < var17; ++var15) {
                           var38 = var4.weightY[var15];
                           var50 = (int)(var38 * (double)var18 / var28);
                           var51 = var4.minHeight;
                           var51[var15] += var50;
                           var18 -= var50;
                           var28 -= var38;
                        }

                        var51 = var4.minHeight;
                        var51[var17 - 1] += var18;
                     }
                  }
               }
            }

            var14 = var19;
         }

         return var4;
      }
   }

   private boolean calculateBaseline(Component var1, GridBagConstraints var2, Dimension var3) {
      int var4 = var2.anchor;
      if (var4 != 256 && var4 != 512 && var4 != 768) {
         var2.ascent = -1;
         return false;
      } else {
         int var5 = var3.width + var2.ipadx;
         int var6 = var3.height + var2.ipady;
         var2.ascent = var1.getBaseline(var5, var6);
         if (var2.ascent >= 0) {
            int var7 = var2.ascent;
            var2.descent = var6 - var2.ascent + var2.insets.bottom;
            var2.ascent += var2.insets.top;
            var2.baselineResizeBehavior = var1.getBaselineResizeBehavior();
            var2.centerPadding = 0;
            if (var2.baselineResizeBehavior == Component.BaselineResizeBehavior.CENTER_OFFSET) {
               int var8 = var1.getBaseline(var5, var6 + 1);
               var2.centerOffset = var7 - var6 / 2;
               if (var6 % 2 == 0) {
                  if (var7 != var8) {
                     var2.centerPadding = 1;
                  }
               } else if (var7 == var8) {
                  --var2.centerOffset;
                  var2.centerPadding = 1;
               }
            }
         }

         return true;
      }
   }

   protected void adjustForGravity(GridBagConstraints var1, Rectangle var2) {
      this.AdjustForGravity(var1, var2);
   }

   protected void AdjustForGravity(GridBagConstraints var1, Rectangle var2) {
      int var5 = var2.y;
      int var6 = var2.height;
      if (!this.rightToLeft) {
         var2.x += var1.insets.left;
      } else {
         var2.x -= var2.width - var1.insets.right;
      }

      var2.width -= var1.insets.left + var1.insets.right;
      var2.y += var1.insets.top;
      var2.height -= var1.insets.top + var1.insets.bottom;
      int var3 = 0;
      if (var1.fill != 2 && var1.fill != 1 && var2.width > var1.minWidth + var1.ipadx) {
         var3 = var2.width - (var1.minWidth + var1.ipadx);
         var2.width = var1.minWidth + var1.ipadx;
      }

      int var4 = 0;
      if (var1.fill != 3 && var1.fill != 1 && var2.height > var1.minHeight + var1.ipady) {
         var4 = var2.height - (var1.minHeight + var1.ipady);
         var2.height = var1.minHeight + var1.ipady;
      }

      switch(var1.anchor) {
      case 10:
         var2.x += var3 / 2;
         var2.y += var4 / 2;
         break;
      case 11:
      case 19:
         var2.x += var3 / 2;
         break;
      case 12:
         var2.x += var3;
         break;
      case 13:
         var2.x += var3;
         var2.y += var4 / 2;
         break;
      case 14:
         var2.x += var3;
         var2.y += var4;
         break;
      case 15:
      case 20:
         var2.x += var3 / 2;
         var2.y += var4;
         break;
      case 16:
         var2.y += var4;
         break;
      case 17:
         var2.y += var4 / 2;
      case 18:
         break;
      case 21:
         if (this.rightToLeft) {
            var2.x += var3;
         }

         var2.y += var4 / 2;
         break;
      case 22:
         if (!this.rightToLeft) {
            var2.x += var3;
         }

         var2.y += var4 / 2;
         break;
      case 23:
         if (this.rightToLeft) {
            var2.x += var3;
         }
         break;
      case 24:
         if (!this.rightToLeft) {
            var2.x += var3;
         }
         break;
      case 25:
         if (this.rightToLeft) {
            var2.x += var3;
         }

         var2.y += var4;
         break;
      case 26:
         if (!this.rightToLeft) {
            var2.x += var3;
         }

         var2.y += var4;
         break;
      case 256:
         var2.x += var3 / 2;
         this.alignOnBaseline(var1, var2, var5, var6);
         break;
      case 512:
         if (this.rightToLeft) {
            var2.x += var3;
         }

         this.alignOnBaseline(var1, var2, var5, var6);
         break;
      case 768:
         if (!this.rightToLeft) {
            var2.x += var3;
         }

         this.alignOnBaseline(var1, var2, var5, var6);
         break;
      case 1024:
         var2.x += var3 / 2;
         this.alignAboveBaseline(var1, var2, var5, var6);
         break;
      case 1280:
         if (this.rightToLeft) {
            var2.x += var3;
         }

         this.alignAboveBaseline(var1, var2, var5, var6);
         break;
      case 1536:
         if (!this.rightToLeft) {
            var2.x += var3;
         }

         this.alignAboveBaseline(var1, var2, var5, var6);
         break;
      case 1792:
         var2.x += var3 / 2;
         this.alignBelowBaseline(var1, var2, var5, var6);
         break;
      case 2048:
         if (this.rightToLeft) {
            var2.x += var3;
         }

         this.alignBelowBaseline(var1, var2, var5, var6);
         break;
      case 2304:
         if (!this.rightToLeft) {
            var2.x += var3;
         }

         this.alignBelowBaseline(var1, var2, var5, var6);
         break;
      default:
         throw new IllegalArgumentException("illegal anchor value");
      }

   }

   private void alignOnBaseline(GridBagConstraints var1, Rectangle var2, int var3, int var4) {
      if (var1.ascent >= 0) {
         int var5;
         if (var1.baselineResizeBehavior == Component.BaselineResizeBehavior.CONSTANT_DESCENT) {
            var5 = var3 + var4 - this.layoutInfo.maxDescent[var1.tempY + var1.tempHeight - 1] + var1.descent - var1.insets.bottom;
            if (!var1.isVerticallyResizable()) {
               var2.y = var5 - var1.minHeight;
               var2.height = var1.minHeight;
            } else {
               var2.height = var5 - var3 - var1.insets.top;
            }
         } else {
            int var6 = var1.ascent;
            if (this.layoutInfo.hasConstantDescent(var1.tempY)) {
               var5 = var4 - this.layoutInfo.maxDescent[var1.tempY];
            } else {
               var5 = this.layoutInfo.maxAscent[var1.tempY];
            }

            int var8;
            if (var1.baselineResizeBehavior == Component.BaselineResizeBehavior.OTHER) {
               boolean var7 = false;
               var6 = this.componentAdjusting.getBaseline(var2.width, var2.height);
               if (var6 >= 0) {
                  var6 += var1.insets.top;
               }

               if (var6 >= 0 && var6 <= var5) {
                  if (var5 + (var2.height - var6 - var1.insets.top) <= var4 - var1.insets.bottom) {
                     var7 = true;
                  } else if (var1.isVerticallyResizable()) {
                     var8 = this.componentAdjusting.getBaseline(var2.width, var4 - var1.insets.bottom - var5 + var6);
                     if (var8 >= 0) {
                        var8 += var1.insets.top;
                     }

                     if (var8 >= 0 && var8 <= var6) {
                        var2.height = var4 - var1.insets.bottom - var5 + var6;
                        var6 = var8;
                        var7 = true;
                     }
                  }
               }

               if (!var7) {
                  var6 = var1.ascent;
                  var2.width = var1.minWidth;
                  var2.height = var1.minHeight;
               }
            }

            var2.y = var3 + var5 - var6 + var1.insets.top;
            if (var1.isVerticallyResizable()) {
               switch(var1.baselineResizeBehavior) {
               case CONSTANT_ASCENT:
                  var2.height = Math.max(var1.minHeight, var3 + var4 - var2.y - var1.insets.bottom);
                  break;
               case CENTER_OFFSET:
                  int var10 = var2.y - var3 - var1.insets.top;
                  var8 = var3 + var4 - var2.y - var1.minHeight - var1.insets.bottom;
                  int var9 = Math.min(var10, var8);
                  var9 += var9;
                  if (var9 > 0 && (var1.minHeight + var1.centerPadding + var9) / 2 + var1.centerOffset != var5) {
                     --var9;
                  }

                  var2.height = var1.minHeight + var9;
                  var2.y = var3 + var5 - (var2.height + var1.centerPadding) / 2 - var1.centerOffset;
               case OTHER:
               }
            }
         }
      } else {
         this.centerVertically(var1, var2, var4);
      }

   }

   private void alignAboveBaseline(GridBagConstraints var1, Rectangle var2, int var3, int var4) {
      if (this.layoutInfo.hasBaseline(var1.tempY)) {
         int var5;
         if (this.layoutInfo.hasConstantDescent(var1.tempY)) {
            var5 = var3 + var4 - this.layoutInfo.maxDescent[var1.tempY];
         } else {
            var5 = var3 + this.layoutInfo.maxAscent[var1.tempY];
         }

         if (var1.isVerticallyResizable()) {
            var2.y = var3 + var1.insets.top;
            var2.height = var5 - var2.y;
         } else {
            var2.height = var1.minHeight + var1.ipady;
            var2.y = var5 - var2.height;
         }
      } else {
         this.centerVertically(var1, var2, var4);
      }

   }

   private void alignBelowBaseline(GridBagConstraints var1, Rectangle var2, int var3, int var4) {
      if (this.layoutInfo.hasBaseline(var1.tempY)) {
         if (this.layoutInfo.hasConstantDescent(var1.tempY)) {
            var2.y = var3 + var4 - this.layoutInfo.maxDescent[var1.tempY];
         } else {
            var2.y = var3 + this.layoutInfo.maxAscent[var1.tempY];
         }

         if (var1.isVerticallyResizable()) {
            var2.height = var3 + var4 - var2.y - var1.insets.bottom;
         }
      } else {
         this.centerVertically(var1, var2, var4);
      }

   }

   private void centerVertically(GridBagConstraints var1, Rectangle var2, int var3) {
      if (!var1.isVerticallyResizable()) {
         var2.y += Math.max(0, (var3 - var1.insets.top - var1.insets.bottom - var1.minHeight - var1.ipady) / 2);
      }

   }

   protected Dimension getMinSize(Container var1, GridBagLayoutInfo var2) {
      return this.GetMinSize(var1, var2);
   }

   protected Dimension GetMinSize(Container var1, GridBagLayoutInfo var2) {
      Dimension var3 = new Dimension();
      Insets var6 = var1.getInsets();
      int var5 = 0;

      int var4;
      for(var4 = 0; var4 < var2.width; ++var4) {
         var5 += var2.minWidth[var4];
      }

      var3.width = var5 + var6.left + var6.right;
      var5 = 0;

      for(var4 = 0; var4 < var2.height; ++var4) {
         var5 += var2.minHeight[var4];
      }

      var3.height = var5 + var6.top + var6.bottom;
      return var3;
   }

   protected void arrangeGrid(Container var1) {
      this.ArrangeGrid(var1);
   }

   protected void ArrangeGrid(Container var1) {
      Insets var5 = var1.getInsets();
      Component[] var6 = var1.getComponents();
      Rectangle var8 = new Rectangle();
      this.rightToLeft = !var1.getComponentOrientation().isLeftToRight();
      if (var6.length != 0 || this.columnWidths != null && this.columnWidths.length != 0 || this.rowHeights != null && this.rowHeights.length != 0) {
         GridBagLayoutInfo var14 = this.getLayoutInfo(var1, 2);
         Dimension var7 = this.getMinSize(var1, var14);
         if (var1.width < var7.width || var1.height < var7.height) {
            var14 = this.getLayoutInfo(var1, 1);
            var7 = this.getMinSize(var1, var14);
         }

         this.layoutInfo = var14;
         var8.width = var7.width;
         var8.height = var7.height;
         int var10 = var1.width - var8.width;
         int var9;
         double var12;
         int var15;
         int[] var10000;
         if (var10 != 0) {
            var12 = 0.0D;

            for(var9 = 0; var9 < var14.width; ++var9) {
               var12 += var14.weightX[var9];
            }

            if (var12 > 0.0D) {
               for(var9 = 0; var9 < var14.width; ++var9) {
                  var15 = (int)((double)var10 * var14.weightX[var9] / var12);
                  var10000 = var14.minWidth;
                  var10000[var9] += var15;
                  var8.width += var15;
                  if (var14.minWidth[var9] < 0) {
                     var8.width -= var14.minWidth[var9];
                     var14.minWidth[var9] = 0;
                  }
               }
            }

            var10 = var1.width - var8.width;
         } else {
            var10 = 0;
         }

         int var11 = var1.height - var8.height;
         if (var11 != 0) {
            var12 = 0.0D;

            for(var9 = 0; var9 < var14.height; ++var9) {
               var12 += var14.weightY[var9];
            }

            if (var12 > 0.0D) {
               for(var9 = 0; var9 < var14.height; ++var9) {
                  var15 = (int)((double)var11 * var14.weightY[var9] / var12);
                  var10000 = var14.minHeight;
                  var10000[var9] += var15;
                  var8.height += var15;
                  if (var14.minHeight[var9] < 0) {
                     var8.height -= var14.minHeight[var9];
                     var14.minHeight[var9] = 0;
                  }
               }
            }

            var11 = var1.height - var8.height;
         } else {
            var11 = 0;
         }

         var14.startx = var10 / 2 + var5.left;
         var14.starty = var11 / 2 + var5.top;

         for(int var3 = 0; var3 < var6.length; ++var3) {
            Component var2 = var6[var3];
            if (var2.isVisible()) {
               GridBagConstraints var4 = this.lookupConstraints(var2);
               if (!this.rightToLeft) {
                  var8.x = var14.startx;

                  for(var9 = 0; var9 < var4.tempX; ++var9) {
                     var8.x += var14.minWidth[var9];
                  }
               } else {
                  var8.x = var1.width - (var10 / 2 + var5.right);

                  for(var9 = 0; var9 < var4.tempX; ++var9) {
                     var8.x -= var14.minWidth[var9];
                  }
               }

               var8.y = var14.starty;

               for(var9 = 0; var9 < var4.tempY; ++var9) {
                  var8.y += var14.minHeight[var9];
               }

               var8.width = 0;

               for(var9 = var4.tempX; var9 < var4.tempX + var4.tempWidth; ++var9) {
                  var8.width += var14.minWidth[var9];
               }

               var8.height = 0;

               for(var9 = var4.tempY; var9 < var4.tempY + var4.tempHeight; ++var9) {
                  var8.height += var14.minHeight[var9];
               }

               this.componentAdjusting = var2;
               this.adjustForGravity(var4, var8);
               if (var8.x < 0) {
                  var8.width += var8.x;
                  var8.x = 0;
               }

               if (var8.y < 0) {
                  var8.height += var8.y;
                  var8.y = 0;
               }

               if (var8.width > 0 && var8.height > 0) {
                  if (var2.x != var8.x || var2.y != var8.y || var2.width != var8.width || var2.height != var8.height) {
                     var2.setBounds(var8.x, var8.y, var8.width, var8.height);
                  }
               } else {
                  var2.setBounds(0, 0, 0, 0);
               }
            }
         }

      }
   }
}
