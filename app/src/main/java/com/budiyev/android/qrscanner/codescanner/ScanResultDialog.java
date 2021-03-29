/*
 * MIT License
 *
 * Copyright (c) 2018 Yuriy Budiyev [yuriy.budiyev@yandex.ru]
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package com.budiyev.android.qrscanner.codescanner;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.text.Html;
import android.text.method.ScrollingMovementMethod;
import android.util.TypedValue;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatDialog;
import com.budiyev.android.qrscanner.R;
import com.google.zxing.Result;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;

public class ScanResultDialog extends AppCompatDialog {
    private String language;
    private String drugTitle;
    private Result result;

    public ScanResultDialog(@NonNull Context context, @NonNull Result result) {
        super(context, resolveDialogTheme(context));
        this.result = result;
    }

    public void performAction() {
        String getURL = result.getText();
        List<String> dataList = getJSoupResponse(getURL, this.language);

        setTitle(R.string.scan_result);
        setContentView(R.layout.dialog_scan_result);
        //noinspection ConstantConditions
        StringBuilder data = new StringBuilder();
        if(dataList.size() > 0) {
            data.append("<h2>"+dataList.get(0)+"</h2>");
        }

        if(dataList.size() > 1) {
            data.append("<br><h3>INTRODUCTION</h3>");
            data.append(dataList.get(1));
        }
        if(dataList.size() > 2) {
            data.append("<br><h3>USES AND BENEFITS</h3>");
            data.append(dataList.get(2));
        }
        if(dataList.size() > 3) {
            data.append("<br><h3>SIDE EFFECTS</h3>");
            data.append(dataList.get(3));
        }
        if(dataList.size() > 4) {
            data.append("<br><h3>HOW TO USE</h3>");
            data.append(dataList.get(4));
        }
        if(dataList.size() > 5) {
            data.append("<br><h3>SAFETY ADVICE</h3>");
            data.append(dataList.get(5));
        }
        if(dataList.size() > 6) {
            data.append("<br><h3>MISSED DOSE</h3>");
            data.append(dataList.get(6));
        }

        TextView tv =  ((TextView) findViewById(R.id.result));
        tv.setMovementMethod(new ScrollingMovementMethod());
        tv.setText(Html.fromHtml(data.toString()));
        //noinspection ConstantConditions
        findViewById(R.id.buy).setOnClickListener(v -> {
            Intent browserIntent =
                    new Intent(Intent.ACTION_VIEW, Uri.parse(getURL));
            getContext().startActivity(browserIntent);
            dismiss();
        });

        findViewById(R.id.video).setOnClickListener(v -> {
            Intent videoIntent =
                    new Intent(getContext(), VideoActivity.class);

            videoIntent.putExtra("videoLang",this.language);
            videoIntent.putExtra("videoFilter",drugTitle);
            getContext().startActivity(videoIntent);
            dismiss();
        });

        findViewById(R.id.audio).setOnClickListener(v -> {
            Intent audioIntent =
                    new Intent(getContext(), AudioActivity.class);
            audioIntent.putExtra("audioLang",this.language);
            audioIntent.putExtra("audioFilter",drugTitle);
            getContext().startActivity(audioIntent);
            dismiss();
        });
        //noinspection ConstantConditions
        findViewById(R.id.close).setOnClickListener(v -> dismiss());
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    private static int resolveDialogTheme(@NonNull Context context) {
        TypedValue outValue = new TypedValue();
        context.getTheme().resolveAttribute(androidx.appcompat.R.attr.alertDialogTheme, outValue, true);
        return outValue.resourceId;
    }

    private List getJSoupResponse(String url, String language)  {
        Document document = null;
        List<String> dataList = new ArrayList<>();
        if(language.equals("Kannada")) {
            language = "kn";
        } else if(language.equals("Hindi")) {
            language = "hi";
        }
        try {
            document = Jsoup.connect(url).get();
            Element header = document.select("h1[class^=DrugHeader__title]").first();
            if(header != null) {
                drugTitle = header.text();
                if(!language.equals("English")) {
                    dataList.add(getTranslatedData(header.text(), language));
                } else {
                    dataList.add(header.text());
                }
            }
            
            Element overview = document.getElementById("overview");
            if(overview != null) {
                if(!language.equals("English")) {
                    dataList.add(getTranslatedData(overview.text(), language));
                } else {
                    dataList.add(overview.text());
                }
            }

            Element usesAndBenefits = document.getElementById("uses_and_benefits");
            if(usesAndBenefits != null) {
                if(!language.equals("English")) {
                    dataList.add(getTranslatedData(usesAndBenefits.text(), language));
                } else {
                    dataList.add(usesAndBenefits.text());
                }
            }

            Element sideEffects = document.getElementById("side_effects");
            if(sideEffects != null) {
                if(!language.equals("English")) {
                    dataList.add(getTranslatedData(sideEffects.text(), language));
                } else {
                    dataList.add(sideEffects.text());
                }
            }

            Element howToUse = document.getElementById("how_to_use");
            if(howToUse != null) {
                if(!language.equals("English")) {
                    dataList.add(getTranslatedData(howToUse.text(), language));
                } else {
                    dataList.add(howToUse.text());
                }
            }

            Element safetyAdvice = document.getElementById("safety_advice");
            if(safetyAdvice != null) {
                if(!language.equals("English")) {
                    dataList.add(getTranslatedData(safetyAdvice.text(), language));
                } else {
                    dataList.add(safetyAdvice.text());
                }
            }

            Element missedDose = document.getElementById("missed_dose");
            if(missedDose != null) {
                if(!language.equals("English")) {
                    dataList.add(getTranslatedData(missedDose.text(), language));
                } else {
                    dataList.add(missedDose.text());
                }
            }

        }catch(IOException ioe)  {
            ioe.printStackTrace();
        }
        return dataList;
    }

    private String getTranslatedData(String text, String language) {
        StringBuffer response = new StringBuffer();
        StringBuffer newResponse = new StringBuffer();
        try {
            String url = "https://translate.googleapis.com/translate_a/single?client=gtx&sl=en&tl="+language+"&dt=t&q=" + text;
            URL obj = new URL(url);
            HttpURLConnection con = (HttpURLConnection) obj.openConnection();
            con.setRequestMethod("GET");
            con.setRequestProperty("User-Agent","Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/89.0.4389.82 Safari/537.36");
            int responseCode = con.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) { // success
                BufferedReader in = new BufferedReader(new InputStreamReader(
                        con.getInputStream()));
                String inputLine;
                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                in.close();

            } else {
                System.out.println("GET request not worked");
            }
            JSONArray jsonArray = new JSONArray(response.toString());
            JSONArray parsedValues = (JSONArray) jsonArray.get(0);
            for(int i=0;i<parsedValues.length();i++) {
                JSONArray parsedValue = (JSONArray) parsedValues.get(i);
                String localeValue = (String) parsedValue.get(0);
                newResponse.append(localeValue);
            }

        } catch(Exception e) {
            e.printStackTrace();
        }

        return newResponse.toString();
    }
}
