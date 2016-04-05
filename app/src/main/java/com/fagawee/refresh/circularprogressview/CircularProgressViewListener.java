package com.fagawee.refresh.circularprogressview;


public interface CircularProgressViewListener {

    void onProgressUpdate(float currentProgress);

  
    void onProgressUpdateEnd(float currentProgress);

   
    void onAnimationReset();

    void onModeChanged(boolean isIndeterminate);
}