package com.jeffg.live_text_view;

import android.animation.Animator;
import android.app.Activity;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.Typeface;
import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;

import org.jsoup.Jsoup;

import java.util.ArrayList;
import java.util.Locale;
import java.util.Set;

import static android.os.Build.VERSION.SDK_INT;
import static android.os.Build.VERSION_CODES.LOLLIPOP;

public class LiveTextView extends RelativeLayout {
    public static final String TAG = "LiveTextView";

    private Context context;
    private AttributeSet attrs;
    private int styleAttr;
    private View view;

    TextView previous, current, next;
    LinearLayout parent;

    int textColor;
    int backgroundColor;
    int smallTextSize;
    int largeTextSize;
    boolean animate;
    boolean paused;
    boolean playing;
    LiveTextViewTTS liveTextViewTTS;
    OnLoadListener onLoadListener;
    OnFinishListener onFinishListener;

    ArrayList<String> text;
    TextToSpeech textToSpeech;
    int currentIndex;

    public interface OnFinishListener {
        void onFinish(LiveTextView liveTextView);
    }

    public interface OnLoadListener {
        void onLoad(LiveTextView liveTextView);
    }


    public LiveTextView(Context context) {
        super(context);
        this.context = context;
        initView();
    }

    public LiveTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        this.attrs = attrs;
        initView();
    }

    public LiveTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        this.attrs = attrs;
        this.styleAttr = defStyleAttr;
        initView();
    }

    private void initView() {
        this.view = this;
        inflate(context, R.layout.live_text_view_layout, this);

        TypedArray arr = context.obtainStyledAttributes(attrs, R.styleable.LiveTextView, styleAttr, 0);

        textColor = arr.getColor(R.styleable.LiveTextView_android_textColor, Color.BLACK);
        backgroundColor = arr.getColor(R.styleable.LiveTextView_android_background, Color.WHITE);
        smallTextSize = arr.getInteger(R.styleable.LiveTextView_smallTextSize, 12);
        largeTextSize = arr.getInteger(R.styleable.LiveTextView_largeTextSize, 14);
        animate = false;

        parent = (LinearLayout) findViewById(R.id.parent);
        next = (TextView) findViewById(R.id.nextTV);
        previous = (TextView) findViewById(R.id.previousTV);
        current = (TextView) findViewById(R.id.currentTV);


        currentIndex = 0;
        paused = false;
        playing = false;

        setTextColor(textColor);
        setBackgroundColor(backgroundColor);
        setSmallTextSize(smallTextSize);
        setLargeTextSize(largeTextSize);

        arr.recycle();
    }

    public void setTextColor(int color) {
        previous.setTextColor(color);
        current.setTextColor(color);
        next.setTextColor(color);
    }

    public void setBackgroundColor(int color) {
        parent.setBackgroundColor(color);
        previous.setBackgroundColor(color);
        current.setBackgroundColor(color);
        next.setBackgroundColor(color);
    }

    public void setTypeface(Typeface tf) {
        previous.setTypeface(tf);
        current.setTypeface(tf);
        next.setTypeface(tf);
    }

    public void setOnLoadListener(OnLoadListener listener) {
        onLoadListener = listener;
    }

    public void setOnFinishListener(OnFinishListener listener) {
        onFinishListener = listener;
    }

    public void animate(boolean bool) {
        animate = bool;
    }

    public void setLiveTextViewTTS(LiveTextViewTTS tts) {
        liveTextViewTTS = tts;
    }

    public void setSmallTextSize(int size) {
        previous.setTextSize(TypedValue.COMPLEX_UNIT_SP, size);
        next.setTextSize(TypedValue.COMPLEX_UNIT_SP, size);
    }

    public void setLargeTextSize(int size) {
        current.setTextSize(TypedValue.COMPLEX_UNIT_SP, size);
    }

    public void setText(String text1) {
        String[] splitText = text1.split("\\.");
        text = new ArrayList<>();
        for (String sentence : splitText) {
            text.add(Jsoup.parse(sentence).text());
        }
    }

    public void setSentences(ArrayList<String> sentences) {
        text = new ArrayList<>();
        for (String sentence : sentences) {
            text.add(Jsoup.parse(sentence).text());
        }
    }

    public void setSentences(String[] sentences) {
        text = new ArrayList<>();
        for (String sentence : sentences) {
            text.add(Jsoup.parse(sentence).text());
        }
    }

    private void setTextViews(Boolean isNext) {
        if (isNext == null) {
            Activity activity = (Activity) view.getContext();
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    try {
                        previous.setText(text.get(currentIndex - 1));
                    } catch (IndexOutOfBoundsException e) {
                        previous.setText("");
                    }
                    try {
                        current.setText(text.get(currentIndex));
                    } catch (IndexOutOfBoundsException e) {
                        current.setText("");
                    }
                    try {
                        next.setText(text.get(currentIndex + 1));
                    } catch (IndexOutOfBoundsException e) {
                        next.setText("");
                    }
                }
            });
        } else if (isNext) {
            Activity activity = (Activity) view.getContext();
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    YoYo.with(Techniques.FadeOutUp)
                            .duration(300)
                            .onEnd(new YoYo.AnimatorCallback() {
                                @Override
                                public void call(Animator animator) {
                                    try {
                                        previous.setText(text.get(currentIndex - 1));
                                        YoYo.with(Techniques.FadeInUp)
                                                .duration(300)
                                                .playOn((View) previous);
                                    } catch (IndexOutOfBoundsException e) {
                                        previous.setText("");
                                    }
                                }
                            })
                            .playOn((View) previous);


                    YoYo.with(Techniques.FadeOutUp)
                            .duration(300)
                            .onEnd(new YoYo.AnimatorCallback() {
                                @Override
                                public void call(Animator animator) {
                                    try {
                                        current.setText(text.get(currentIndex));
                                        YoYo.with(Techniques.FadeInUp)
                                                .duration(300)
                                                .playOn((View) current);
                                    } catch (IndexOutOfBoundsException e) {
                                        current.setText("");
                                    }
                                }
                            })
                            .playOn((View) current);
                    YoYo.with(Techniques.FadeOutUp)
                            .duration(300)
                            .onEnd(new YoYo.AnimatorCallback() {
                                @Override
                                public void call(Animator animator) {
                                    try {
                                        next.setText(text.get(currentIndex + 1));
                                        YoYo.with(Techniques.FadeInUp)
                                                .duration(300)
                                                .playOn((View) next);
                                    } catch (IndexOutOfBoundsException e) {
                                        next.setText("");
                                    }
                                }
                            })
                            .playOn((View) next);

                }
            });
        } else {
            Activity activity = (Activity) view.getContext();
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    YoYo.with(Techniques.FadeOutDown)
                            .duration(300)
                            .onEnd(new YoYo.AnimatorCallback() {
                                @Override
                                public void call(Animator animator) {
                                    try {
                                        previous.setText(text.get(currentIndex - 1));
                                        YoYo.with(Techniques.FadeInDown)
                                                .duration(300)
                                                .playOn((View) previous);
                                    } catch (IndexOutOfBoundsException e) {
                                        previous.setText("");
                                    }
                                }
                            })
                            .playOn((View) previous);


                    YoYo.with(Techniques.FadeOutDown)
                            .duration(300)
                            .onEnd(new YoYo.AnimatorCallback() {
                                @Override
                                public void call(Animator animator) {
                                    try {
                                        current.setText(text.get(currentIndex));
                                        YoYo.with(Techniques.FadeInDown)
                                                .duration(300)
                                                .playOn((View) current);
                                    } catch (IndexOutOfBoundsException e) {
                                        current.setText("");
                                    }
                                }
                            })
                            .playOn((View) current);
                    YoYo.with(Techniques.FadeOutDown)
                            .duration(300)
                            .onEnd(new YoYo.AnimatorCallback() {
                                @Override
                                public void call(Animator animator) {
                                    try {
                                        next.setText(text.get(currentIndex + 1));
                                        YoYo.with(Techniques.FadeInDown)
                                                .duration(300)
                                                .playOn((View) next);
                                    } catch (IndexOutOfBoundsException e) {
                                        next.setText("");
                                    }
                                }
                            })
                            .playOn((View) next);

                }
            });
        }
    }


    private void previous() {
        currentIndex = Math.max(0, currentIndex - 1);
        start();

        if (animate) {
            setTextViews(false);
        } else {
            setTextViews(null);
        }

    }

    public void pause() {
        if (textToSpeech != null) {
            paused = true;
            playing = false;
            textToSpeech.stop();
            textToSpeech.shutdown();

        }
    }

    public void stop() {
        if (textToSpeech != null) {
            textToSpeech.stop();
            textToSpeech.shutdown();
            playing = false;
        }
    }

    public void reset() {
        if (textToSpeech != null) {
            textToSpeech.stop();
            textToSpeech.shutdown();
            currentIndex = 0;
            playing = false;
        }
    }

    public void resume() {
        start();
    }

    public void fast_forwards() {
        pause();
        next();

    }

    public boolean isPlaying() {
        return playing;
    }

    public void fast_rewind() {
        pause();
        previous();
    }

    private void next() {

        currentIndex = Math.min(currentIndex + 1, text.size() - 1);

        if (currentIndex >= text.size()) {
            stop();
        } else {
            start();
            if (animate) {
                setTextViews(true);
            } else {
                setTextViews(null);
            }
        }
    }

    public void start() {

        textToSpeech = new TextToSpeech(context, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status == TextToSpeech.SUCCESS) {
                    if (onLoadListener != null) {
                        onLoadListener.onLoad(LiveTextView.this);
                    }
                    if (liveTextViewTTS != null) {
                        if (liveTextViewTTS.pitch != null) {
                            textToSpeech.setPitch(liveTextViewTTS.pitch);
                        }
                        if (liveTextViewTTS.language != null) {
                            textToSpeech.setLanguage(liveTextViewTTS.language);
                        }
                        if (liveTextViewTTS.audioAttributes != null) {
                            textToSpeech.setAudioAttributes(liveTextViewTTS.audioAttributes);
                        }
                        if (liveTextViewTTS.speechRate != null) {
                            textToSpeech.setSpeechRate(liveTextViewTTS.speechRate);
                        }
                        if (liveTextViewTTS.voice != null) {
                            textToSpeech.setVoice(liveTextViewTTS.voice);
                        }
                    }
                    textToSpeech.setOnUtteranceProgressListener(new UtteranceProgressListener() {
                        @Override
                        public void onStart(String s) {

                        }

                        @Override
                        public void onDone(String s) {
                            Log.i("UTTERANCE", s);
                            if (currentIndex == text.size() - 1) {
                                if (onFinishListener != null) {
                                    onFinishListener.onFinish(LiveTextView.this);
                                }
                            } else {
                                if (!paused) {
                                    next();
                                }
                            }

                        }

                        @Override
                        public void onError(String s) {

                        }
                    });
                    paused = false;
                    playing = true;

                    textToSpeech.setLanguage(locale);

                    Log.v(TAG, "Supported Languages: " + getSupportedLanguages(textToSpeech));

                    textToSpeech.speak(text.get(currentIndex), TextToSpeech.QUEUE_FLUSH, null, text.get(currentIndex));
                    setTextViews(null);
                } else {
                    throw new Error("An error occured initializing the Text-To-Speech Engine - Status: " + Integer.toString(status));
                }
            }


        });

    }

    public Locale locale = Locale.ENGLISH;


    public static ArrayList<Locale> getSupportedLanguages(TextToSpeech textToSpeech) {
        return SDK_INT >= LOLLIPOP
                ? initSupportedLanguagesLollipop(textToSpeech)
                : initSupportedLanguagesLegacy(textToSpeech);
    }

    private static ArrayList<Locale> initSupportedLanguagesLollipop(TextToSpeech textToSpeech) {
        ArrayList<Locale> languages = new ArrayList<>();
        Set<Locale> availableLocales = textToSpeech.getAvailableLanguages();
        for (Locale locale : availableLocales) {
            languages.add(locale);
        }
        return languages;
    }

    private static ArrayList<Locale> initSupportedLanguagesLegacy(TextToSpeech textToSpeech) {
        ArrayList<Locale> languages = new ArrayList<>();
        Locale[] allLocales = Locale.getAvailableLocales();
        for (Locale locale : allLocales) {
            try {
                int res = textToSpeech.isLanguageAvailable(locale);
                boolean hasVariant = (null != locale.getVariant() && locale.getVariant().length() > 0);
                boolean hasCountry = (null != locale.getCountry() && locale.getCountry().length() > 0);

                boolean isLocaleSupported =
                        false == hasVariant && false == hasCountry && res == TextToSpeech.LANG_AVAILABLE ||
                                false == hasVariant && true == hasCountry && res == TextToSpeech.LANG_COUNTRY_AVAILABLE ||
                                res == TextToSpeech.LANG_COUNTRY_VAR_AVAILABLE;

                Log.d(TAG, "TextToSpeech Engine isLanguageAvailable " + locale + " (supported=" + isLocaleSupported + ",res=" + res + ", country=" + locale.getCountry() + ", variant=" + locale.getVariant() + ")");

                if (true == isLocaleSupported) {
                    languages.add(locale);
                }
            } catch (Exception ex) {
                Log.e(TAG, "Error checking if language is available for TTS (locale=" + locale + "): " + ex.getClass().getSimpleName() + "-" + ex.getMessage());
            }
        }

        return languages;
    }
}
