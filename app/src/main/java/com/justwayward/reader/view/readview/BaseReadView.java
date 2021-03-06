package com.justwayward.reader.view.readview;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.view.View;
import android.widget.Scroller;

import com.justwayward.reader.bean.BookToc;
import com.justwayward.reader.manager.SettingManager;
import com.justwayward.reader.manager.ThemeManager;
import com.justwayward.reader.utils.LogUtils;
import com.justwayward.reader.utils.ScreenUtils;

import java.util.List;

/**
 * @author yuyh.
 * @date 2016/10/18.
 */
public abstract class BaseReadView extends View {

    protected int mScreenWidth;
    protected int mScreenHeight;

    protected Bitmap mCurPageBitmap, mNextPageBitmap;
    protected Canvas mCurrentPageCanvas, mNextPageCanvas;
    protected PageFactory pagefactory = null;

    protected OnReadStateChangeListener listener;
    protected String bookId;
    public boolean isPrepared = false;

    Scroller mScroller;

    public BaseReadView(Context context, String bookId, List<BookToc.mixToc.Chapters> chaptersList,
                        OnReadStateChangeListener listener) {
        super(context);
        this.listener = listener;
        this.bookId = bookId;

        mScreenWidth = ScreenUtils.getScreenWidth();
        mScreenHeight = ScreenUtils.getScreenHeight();

        mCurPageBitmap = Bitmap.createBitmap(mScreenWidth, mScreenHeight, Bitmap.Config.ARGB_8888);
        mNextPageBitmap = Bitmap.createBitmap(mScreenWidth, mScreenHeight, Bitmap.Config.ARGB_8888);
        mCurrentPageCanvas = new Canvas(mCurPageBitmap);
        mNextPageCanvas = new Canvas(mNextPageBitmap);

        mScroller = new Scroller(getContext());

        pagefactory = new PageFactory(getContext(), bookId, chaptersList);
        pagefactory.setOnReadStateChangeListener(listener);
    }

    public synchronized void init(int theme){
        if (!isPrepared) {
            try {
                pagefactory.setBgBitmap(ThemeManager.getThemeDrawable(theme));
                // 自动跳转到上次阅读位置
                int pos[] = SettingManager.getInstance().getReadProgress(bookId);
                int ret = pagefactory.openBook(pos[0], new int[]{pos[1], pos[2]});
                LogUtils.i("上次阅读位置：chapter=" + pos[0] + " startPos=" + pos[1] + " endPos=" + pos[2]);
                if (ret == 0) {
                    listener.onLoadChapterFailure(pos[0]);
                    return;
                }
                pagefactory.onDraw(mCurrentPageCanvas);
                postInvalidate();
            } catch (Exception e) {
            }
            isPrepared = true;
        }
    }

    public abstract void jumpToChapter(int chapter);

    public abstract void nextPage();

    public abstract void prePage();

    public abstract void setFontSize(final int fontSizePx);

    public abstract void setTextColor(int textColor, int titleColor);

    public abstract void setTheme(int theme);

    public abstract void setBattery(int battery);

    public abstract void setTime(String time);
}
