package ch.epfl.sweng.project.engine3d;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by Quentin on 19/11/2016.
 */

public class StringAdapter {

    private final String text;

    public StringAdapter(String text) {
        this.text = text;
    }

    public Bitmap textAsBitmap(int colorIndex) {
        Paint paint = new Paint();

        int textSize = 20;
        int width = 512;
        int height = 512;
        int epsilon = 30;

        paint.setTextSize(textSize);


        Bitmap image = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_4444);
        Canvas canvas = new Canvas(image);

        paint.setColor(colorIndex);
        canvas.drawRect(0, 0, width, height, paint);

        paint.setColor(Color.WHITE);
        canvas.drawRect(epsilon, epsilon, width - epsilon, height - epsilon, paint);
        paint.setColor(Color.BLACK);
        canvas.drawText(text, epsilon + 5, epsilon + 5 + textSize, paint);
        return image;
    }


    public Bitmap textToBitmap(int textSize, int widthBitmap, int colorIndex) {

        int epsilon = 30;

        Paint paint = new Paint();
        paint.setTextSize(textSize);
        Rect rect = new Rect();
        paint.getTextBounds(text, 0, text.length(), rect);
        final int heightTemp = rect.height();

        paint.getTextBounds("a a", 0, 3, rect);
        int temp = rect.width();
        paint.getTextBounds("a", 0, 1, rect);
        int spaceSize = temp - rect.width() + 1;

        String[] words = text.split(" ");

        //  int numberOfPixelPerLine = widthTemp/width;

        List<StringBuilder> builderArrayList = new ArrayList<>();
        builderArrayList.add(new StringBuilder());

        int numberOfPixels = 0;
        int indexOfLine = 0;
        for (int i = 0; i < words.length; i++) {
            paint.getTextBounds(words[i], 0, words[i].length(), rect);
            final int widthWord = rect.width();


            if (words[i].contains("\n")) {

                String[] spitedWord = words[i].split("\n");
                int widthWordSpited = 0;
                if (spitedWord.length != 0 && !spitedWord[0].isEmpty()) {
                    paint.getTextBounds(spitedWord[0], 0, spitedWord[0].length(), rect);
                    widthWordSpited = rect.width();
                }


                if (numberOfPixels + widthWordSpited + spaceSize >= widthBitmap - 2 * epsilon) {
                    indexOfLine++;
                    StringBuilder stringBuilder = new StringBuilder();
                    stringBuilder.append(words[i]);
                    builderArrayList.add(stringBuilder);
                    indexOfLine++;
                    builderArrayList.add(new StringBuilder());
                } else {
                    builderArrayList.get(indexOfLine).append(" " + words[i]);
                    indexOfLine++;
                    builderArrayList.add(new StringBuilder());
                }

                for (int j = 1; j < spitedWord.length; j++) {
                    StringBuilder stringBuilder = new StringBuilder();
                    stringBuilder.append(words[i]);
                    builderArrayList.add(stringBuilder);
                }

                numberOfPixels = 0;
            } else {
                if (numberOfPixels + widthWord + spaceSize >= widthBitmap - 2 * epsilon) {
                    numberOfPixels = 0;
                    indexOfLine++;
                    StringBuilder stringBuilder = new StringBuilder();
                    stringBuilder.append(words[i]);
                    builderArrayList.add(stringBuilder);
                } else {
                    numberOfPixels += (widthWord + spaceSize);
                    builderArrayList.get(indexOfLine).append(" " + words[i]);
                }
            }
        }
        List<String> list = new ArrayList<>();

        for (StringBuilder s : builderArrayList) {
            list.add(s.toString().replace('\n', ' '));
        }

        int heightBitmap = (heightTemp) * list.size() + 2 * epsilon;

        int powerOfHeight = (int) (Math.log(heightBitmap) / Math.log(2)) + 1;
        heightBitmap = (int) Math.pow(2, powerOfHeight);


        Bitmap image = Bitmap.createBitmap(widthBitmap, heightBitmap, Bitmap.Config.ARGB_4444);
        Canvas canvas = new Canvas(image);

        paint.setColor(colorIndex);
        canvas.drawRect(0, 0, widthBitmap, heightBitmap, paint);

        paint.setColor(Color.WHITE);
        canvas.drawRect(epsilon, epsilon, widthBitmap - epsilon, heightBitmap - epsilon, paint);


        paint.setColor(Color.BLACK);
        for (int i = 0; i < list.size(); i++) {
            canvas.drawText(list.get(i), epsilon, epsilon + (i + 1) * heightTemp, paint);
        }

        return image;
    }
}
