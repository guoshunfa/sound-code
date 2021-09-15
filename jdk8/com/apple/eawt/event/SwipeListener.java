package com.apple.eawt.event;

public interface SwipeListener extends GestureListener {
   void swipedUp(SwipeEvent var1);

   void swipedDown(SwipeEvent var1);

   void swipedLeft(SwipeEvent var1);

   void swipedRight(SwipeEvent var1);
}
