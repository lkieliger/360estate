package ch.epfl.sweng.project.engine3d;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;

import java.util.ArrayList;
import java.util.List;

public class StringAdapter {

    private static final int EPSILON = 30;
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


    private List<String> textToList(int textSize, int widthBitmap) {

        Paint paint = new Paint();
        paint.setTextSize(textSize);
        Rect rect = new Rect();

        String[] words = text.split(" ");

        //  int numberOfPixelPerLine = widthTemp/width;

        List<StringBuilder> stringArrayList = new ArrayList<>();
        stringArrayList.add(new StringBuilder());

        int indexOfLine = 0;
        for (int i = 0; i < words.length; i++) {

            paint.getTextBounds(stringArrayList.get(indexOfLine).toString() + " " + words[i], 0, words[i].length()
                    + stringArrayList.get(indexOfLine).length() + 1, rect);
            final int widthLine = rect.width();

            if (words[i].equals("\n")) {
                indexOfLine++;
                stringArrayList.add(new StringBuilder());
            } else {
                if (widthLine >= widthBitmap - 2 * EPSILON) {
                    indexOfLine++;
                    StringBuilder stringBuilder = new StringBuilder();
                    stringBuilder.append(words[i]);
                    stringArrayList.add(stringBuilder);
                } else {
                    stringArrayList.get(indexOfLine).append(" " + words[i]);
                }
            }
        }
        List<String> list = new ArrayList<>();

        for (StringBuilder s : stringArrayList) {
            list.add(s.toString().replace('\n', ' '));
        }

        return list;
    }


    public Bitmap textToBitmap(int textSize, int widthBitmap, int colorIndex) {

        List<String> list = textToList(textSize, widthBitmap);

        Paint paint = new Paint();
        paint.setTextSize(textSize);

        Rect rect = new Rect();
        paint.getTextBounds(text, 0, text.length(), rect);
        int heightTemp = rect.height();

        int heightBitmap = (heightTemp) * list.size() + 2 * EPSILON;

        int powerOfHeight = (int) (Math.log(heightBitmap) / Math.log(2)) + 1;
        heightBitmap = (int) Math.pow(2, powerOfHeight);

        Bitmap image = Bitmap.createBitmap(widthBitmap, heightBitmap, Bitmap.Config.ARGB_4444);
        Canvas canvas = new Canvas(image);

        paint.setColor(colorIndex);
        canvas.drawRect(0, 0, widthBitmap, heightBitmap, paint);

        paint.setColor(Color.WHITE);
        canvas.drawRect(EPSILON, EPSILON, widthBitmap - EPSILON, heightBitmap - EPSILON, paint);

        paint.setColor(Color.BLACK);
        for (int i = 0; i < list.size(); i++) {
            canvas.drawText(list.get(i), EPSILON, EPSILON + (i + 1) * heightTemp, paint);
        }

        return image;
    }
}
