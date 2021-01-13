package com.epaii.pdf.pngs;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.PDFRenderer;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.CountDownLatch;

public class Manager {
    public float d_width;
    public int pagetotle;
    public  String targetFolder;
    private String filePath;
    public boolean allOk = true;
    public CountDownLatch thread_count;
    public String[] pngs;
    public byte[] data;
    private PDDocument pdf;
    private PDFRenderer pdfRenderer;

    public Manager(String filePath, String targetFolder,float d_width)   {
        this.d_width = d_width;
        this.targetFolder = targetFolder;
        this.filePath =filePath;
    }
    public ArrayList<String> doTask(int thread_page_num) throws IOException, InterruptedException {
        File f = new File(filePath);
        int length = (int) f.length();
          data = new byte[length];
        new FileInputStream(f).read(data);
          pdf = PDDocument.load(data);
            pdfRenderer = new PDFRenderer(pdf);
        this.pagetotle =pdf.getNumberOfPages();
        pngs = new String[pagetotle];
        int thread_num = (int)Math.ceil((pagetotle*1.0)/(thread_page_num*1.0)) ;
          thread_count = new CountDownLatch(thread_num);
        for (int i=0;i<thread_num;i++)
        {
//            System.out.println(i*thread_page_num);
//            System.out.println(Math.min((i+1)*thread_page_num,pagetotle-1));
            new Thread(new Task(this,i*thread_page_num,Math.min((i+1)*thread_page_num,pagetotle))).start();
        }
        thread_count.await();
        if(allOk){

            return  new ArrayList(Arrays.asList(pngs));
        }else{
            return  new ArrayList<>();
        }

    }

    public byte[] getData() {
        return  data;
//        byte[] d = new byte[data.length];
//           System.arraycopy(data,0,d,0,data.length);
//           return d;
    }

    public PDDocument getPdf() {
        return  pdf;
    }

    public PDFRenderer getRenderer() {
        return pdfRenderer;
    }
}
