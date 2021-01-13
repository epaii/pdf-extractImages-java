package com.epaii.pdf.pngs;

import javax.imageio.ImageIO;
import java.awt.image.RenderedImage;
import java.io.File;
import java.io.IOException;
import java.util.concurrent.CountDownLatch;

public class IoTask implements Runnable{

    private final CountDownLatch count;
    private RenderedImage[] bims;
    private String[] tofiles;

    public IoTask(CountDownLatch count, RenderedImage[] bims, String[] tofiles){
        this.bims = bims;
        this.tofiles = tofiles;
        this.count= count;
    }
    @Override
    public void run() {
        try {
            for (int i =0;i<bims.length;i++)
            ImageIO.write(bims[i], "png", new File(tofiles[i]));
        } catch (IOException e) {
            e.printStackTrace();
        }
        this.count.countDown();
    }
}
