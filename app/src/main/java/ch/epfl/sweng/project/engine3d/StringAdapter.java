package ch.epfl.sweng.project.engine3d;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;

import java.util.ArrayList;
import java.util.List;

public final class StringAdapter {

    private static final int contourColor = Color.rgb(70, 75, 75);
    private static final int backColor = Color.rgb(238, 238, 238);
    private static final int textColor = Color.rgb(23, 23, 23);
    private final String text;


    public StringAdapter(String text) {
        this.text = text;
    }

    private List<String> textToList(int textSize, int widthBitmap, int epsilon) {

        Paint paint = new Paint();
        paint.setTextSize(textSize);
        Rect rect = new Rect();
        String newLine = "\n";
        String[] words = text.split(" ");

        List<StringBuilder> stringArrayList = new ArrayList<>();
        stringArrayList.add(new StringBuilder());

        int indexOfLine = 0;
        for (String word : words) {
            paint.getTextBounds(stringArrayList.get(indexOfLine).toString() + " " + word, 0, word.length()
                    + stringArrayList.get(indexOfLine).length() + 1, rect);
            final int widthLine = rect.width();

            if (word.equals(newLine)) {
                indexOfLine++;
                stringArrayList.add(new StringBuilder());
            } else {
                if (widthLine >= widthBitmap - 2 * epsilon) {
                    indexOfLine++;
                    StringBuilder stringBuilder = new StringBuilder();
                    stringBuilder.append(word);
                    stringArrayList.add(stringBuilder);
                } else {
                    stringArrayList.get(indexOfLine).append(" ").append(word);
                }
            }
        }
        List<String> list = new ArrayList<>();

        for (StringBuilder s : stringArrayList) {
            list.add(s.toString().replace(newLine.charAt(0), ' '));
        }

        return list;
    }

    public Bitmap textToBitmap(int textSize, int widthBitmap, int contourSize, int marginSize, int heightLimit) {

        List<String> list = textToList(textSize, widthBitmap, contourSize + marginSize);

        Paint paint = new Paint();
        paint.setTextSize(textSize);

        Rect rect = new Rect();
        paint.getTextBounds(text, 0, text.length(), rect);
        int heightTemp = rect.height();

        int heightBitmap = (heightTemp) * list.size() + 2 * (contourSize + marginSize);

        int powerOfHeight = (int) (Math.log(heightBitmap) / Math.log(2)) + 1;
        heightBitmap = (int) Math.pow(2, powerOfHeight);

        if (heightBitmap > heightLimit) {
            heightBitmap = heightLimit;
        }

        Bitmap image = Bitmap.createBitmap(widthBitmap, heightBitmap, Bitmap.Config.ARGB_4444);
        Canvas canvas = new Canvas(image);

        paint.setColor(contourColor);
        canvas.drawRect(0, 0, widthBitmap, heightBitmap, paint);

        paint.setColor(backColor);
        canvas.drawRect(contourSize, contourSize, widthBitmap - contourSize, heightBitmap - contourSize, paint);

        paint.setColor(textColor);
        int i = 0;
        int currentHeight = contourSize + marginSize + 2 * heightTemp;
        while (i < list.size() && currentHeight < heightBitmap) {
            canvas.drawText(list.get(i), contourSize + marginSize, contourSize + marginSize + (i + 1) * heightTemp,
                    paint);
            currentHeight += heightTemp;
            i++;
        }
        return image;
    }
}
