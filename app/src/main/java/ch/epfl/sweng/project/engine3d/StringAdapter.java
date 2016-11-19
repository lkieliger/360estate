package ch.epfl.sweng.project.engine3d;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

/**
 * Created by Quentin on 19/11/2016.
 */

public class StringAdapter {

    private final String text;

    public StringAdapter(String text) {
        this.text = text;
    }

    public Bitmap textAsBitmap(String text, int colorIndex) {
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
}
