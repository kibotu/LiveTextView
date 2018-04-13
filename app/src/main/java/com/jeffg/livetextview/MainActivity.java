package com.jeffg.livetextview;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.jeffg.live_text_view.LiveTextView;

import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        LiveTextView textView = findViewById(R.id.liveTextView);
        textView.locale = Locale.GERMAN;

        textView.setText("Für Designer, Schriftsetzer, Layouter, Grafikenthusiasten und alle anderen.\n" +
                "\n" +
                "Ein Projekt startet und doch es gibt noch keinen Text, allerdings sollte das Layout schon bald präsentiert werden ... was tun?\n" +
                "\n" +
                "Damit das Projekt gleich starten kann benutze einfach etwas Lorem ipsum - Blind-, Füll-, Dummy-, Nachahmungs-, Platzhaltertext.\n" +
                "\n" +
                "Generiere einfach soviel Lorem Ipsum Text wie du brauchst, kopiere und füge ihn in dein Layout als vorübergehenden Platzhalter ein. Somit sieht das Projekt ein Stückchen vollständiger aus als zuvor. Viel Spaß dabei.");
        textView.start();
    }
}
