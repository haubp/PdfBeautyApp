package opswat.com.view.custom.SlideMenuLayout;

import android.support.annotation.ColorRes;
import android.support.annotation.FloatRange;
import android.support.annotation.IntDef;
import android.view.View;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public interface SlideMenuAction {

    int SLIDE_MODE_LEFT = 1001;
    int SLIDE_MODE_RIGHT = 1002;
    int SLIDE_MODE_LEFT_RIGHT = 1003;

    int SLIDE_MODE_NONE = 1004;

    /**
     * set slide mode.
     *
     * @param slideMode {@link #SLIDE_MODE_LEFT},{@link #SLIDE_MODE_LEFT_RIGHT},
     *                  {@link #SLIDE_MODE_RIGHT},{@link #SLIDE_MODE_NONE}
     */
    void setSlideMode(@SlideMode int slideMode);

    /**
     * @param slidePadding
     */
    void setSlidePadding(int slidePadding);

    /**
     * @param slideTime
     */
    void setSlideTime(int slideTime);

    /**
     * @param parallax，Default:true
     */
    void setParallaxSwitch(boolean parallax);

    /**
     * @param contentAlpha 0<contentAlpha<=1.0.
     *                     Default:0.5
     */
    void setContentAlpha(@FloatRange(from = 0f, to = 1.0f) float contentAlpha);

    /**
     * @param color ：#000000
     */
    void setContentShadowColor(@ColorRes int color);

    /**
     * @param contentToggle Default:false
     */
    void setContentToggle(boolean contentToggle);

    /**
     * @param allowTogging Default:true
     */
    void setAllowTogging(boolean allowTogging);

    /**
     * @return {@link View}
     */
    View getSlideLeftView();

    /**
     * @return {@link View}
     */
    View getSlideRightView();

    /**
     * @return {@link View}
     */
    View getSlideContentView();

    void toggleLeftSlide();

    void openLeftSlide();

    void closeLeftSlide();

    boolean isLeftSlideOpen();

    void toggleRightSlide();

    void openRightSlide();

    void closeRightSlide();

    boolean isRightSlideOpen();

    /**
     * @param listener {@link OnSlideChangedListener}
     */
    void addOnSlideChangedListener(OnSlideChangedListener listener);

    /**
     * Slide Mode.
     *
     * @hide
     */
    @IntDef({SLIDE_MODE_LEFT, SLIDE_MODE_RIGHT, SLIDE_MODE_LEFT_RIGHT, SLIDE_MODE_NONE})
    @Retention(RetentionPolicy.SOURCE)
    public @interface SlideMode {

    }
}
