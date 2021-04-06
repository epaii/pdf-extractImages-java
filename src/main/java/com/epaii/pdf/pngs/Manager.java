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
    private   int page_start;
    private   int page_end;
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

    public Manager(String filePath, String targetFolder,float d_width,int page_start, int page_end)   {
        this.d_width = d_width;
        this.targetFolder = targetFolder;
        this.filePath =filePath;
        this.page_start=page_start;
        this.page_end=page_end;
    }
    public ArrayList<String> doTask(int thread_page_num) throws IOException, InterruptedException {
        File f = new File(filePath);
        int length = (int) f.length();
          data = new byte[length];
        new FileInputStream(f).read(data);
          pdf = PDDocument.load(data);
            pdfRenderer = new PDFRenderer(pdf);
        this.pagetotle =pdf.getNumberOfPages();
        if(this.page_start<0){
            this.page_start = this.pagetotle+ this.page_start;
        }
        if(this.page_end<0){
            this.page_end = this.pagetotle+1+ this.page_end;
        }

        int outlength = this.page_end-this.page_start;


        pngs = new String[outlength];
        int thread_num = (int)Math.ceil((outlength*1.0)/(thread_page_num*1.0)) ;
          thread_count = new CountDownLatch(thread_num);
        for (int i=0;i<thread_num;i++)
        {
//            System.out.println(i*thread_page_num);
//            System.out.println(Math.min((i+1)*thread_page_num,pagetotle-1));
            new Thread(new Task(this,this.page_start+i*thread_page_num,Math.min((i+1)*thread_page_num,this.page_end))).start();
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
