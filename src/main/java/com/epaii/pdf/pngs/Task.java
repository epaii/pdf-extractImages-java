package com.epaii.pdf.pngs;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.rendering.ImageType;
import org.apache.pdfbox.rendering.PDFRenderer;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.concurrent.CountDownLatch;

public class Task implements Runnable {

    private int page_start = 0;
    private int page_end = 0;
    private Manager  manager;
    public Task(Manager manager, int page_start,int page_end){

        this.page_end = page_end;
        this.page_start = page_start;
        this.manager =manager;
    }
    @Override
    public void run() {
        try {
            PDDocument pdf = PDDocument.load(manager.getData());
            PDFRenderer pdfRenderer = new PDFRenderer(pdf);

            BufferedImage[] bims = new BufferedImage[page_end-page_start];
            String[] tofiles = new String[page_end-page_start];
            for(int i =page_start;i<page_end;i++){
                PDPage page = pdf.getPage(i);
                float width = page.getCropBox().getWidth();
                float scale =  (float) (manager.d_width/width);
                String tofile =manager.targetFolder + i + "_" + manager.pagetotle + ".png";
                synchronized (manager.pngs){
                    manager.pngs[i] = tofile;
                }
                BufferedImage bim = pdfRenderer.renderImage(i, scale, ImageType.RGB);
               // ImageIO.write(bim, "png", new File(tofile));
                bims[i-page_start] = bim;
                tofiles[i-page_start] =tofile;

            }

            pdf.close();

            int io_thred_num = (int) Math.ceil((page_end-page_start)/(100*1.0));
            CountDownLatch iocount =new CountDownLatch(io_thred_num);
            if(io_thred_num==1)
            {
                new IoTask(iocount, bims,tofiles).run();
            }else{

                for (int i=0;i<io_thred_num;i++)
                {
                    new Thread(new IoTask(iocount, bims,tofiles)).start();
                }
            }

            iocount.await();

        } catch (IOException | InterruptedException e) {
            synchronized (manager){
                manager.allOk = false;
            }
            System.out.println("ddddddd");
            e.printStackTrace();
        }
        manager.thread_count.countDown();

    }
}
