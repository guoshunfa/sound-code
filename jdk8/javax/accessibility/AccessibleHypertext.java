package javax.accessibility;

public interface AccessibleHypertext extends AccessibleText {
   int getLinkCount();

   AccessibleHyperlink getLink(int var1);

   int getLinkIndex(int var1);
}
