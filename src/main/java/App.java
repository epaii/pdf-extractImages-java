
import org.apache.pdfbox.cos.COSName;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageTree;
import org.apache.pdfbox.pdmodel.PDResources;

import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.apache.pdfbox.rendering.ImageType;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.apache.pdfbox.text.PDFTextStripper;


import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;

 
public class App {
    public static void main(String[] args) {
        File file;
        ArrayList<String> out = null;
        if(args.length>0){
            if(args[0].equals("text")) {
                file = new File(args[1]);
                extractText(file, args[2], args.length > 3 ? Integer.parseInt(args[3]) : 0, args.length > 4 ? Integer.parseInt(args[4]) : -1);
                return;
            }else if(args[0].equals("pages-images")){
                out = PDF2ImagPDFbox(args[1],args[2], args.length > 3 ? Float.parseFloat(args[3]) : 1000);
            }else{
                file = new File(args[0]);
                out = extractImages(file, args[1], args.length > 2 ? Integer.parseInt(args[2]) : -1, args.length > 3 ? Integer.parseInt(args[3]) : -1, args.length > 4 ? Integer.parseInt(args[4]) : -1, args.length > 5 ? Integer.parseInt(args[5]) : -1);

            }

        }else{
             // file = new File("/Volumes/BOOTCAMP/php/phpworkspace/gaoxin-pdf-signs/pdfs/00000112901727.pdf");
              file = new File("/Volumes/BOOTCAMP/java_project/pdf_2_imgaes/pdfs/88.pdf");
              out = extractImages(file, "/Volumes/BOOTCAMP/java_project/pdf_2_imgaes/pdfs/images/", -1, -1, -1, args.length > 5 ? Integer.parseInt(args[5]) : -1);

        }

        if (out != null)
            for (int i = 0; i < out.size(); i++) {
                System.out.println(out.get(i));
            }



    }


    public static ArrayList<String> PDF2ImagPDFbox(String filePath, String targetFolder,float d_width) {
        ArrayList<String> out = new ArrayList<String>();
        PDDocument pdf = null;
        try {
            pdf = PDDocument.load(new File(filePath));
            PDFRenderer pdfRenderer = new PDFRenderer(pdf);
            PDPageTree pageTree = pdf.getPages();
            int pageCounter = 0;
            int pagetotle = pageTree.getCount();
            for(PDPage page : pageTree){
                float width = page.getCropBox().getWidth();
                float scale =  (float) (d_width/width);
                BufferedImage bim = pdfRenderer.renderImage(pageCounter,scale, ImageType.RGB);
                String tofile =targetFolder + pageCounter + "_" + pagetotle + ".png";
                ImageIO.write(bim, "png", new File(tofile));
                pageCounter++;
                out.add(tofile);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        return out;
    }

    public static void extractText(File file,String textFile, int page_start, int page_end) {
        PDDocument doc = null;
        try {
            doc = PDDocument.load(file);
            int pagenumber = doc.getNumberOfPages();
            if(page_end==-1){
                page_end = pagenumber;
            }else{
                page_end = Math.min(page_end,pagenumber);
            }
            PDFTextStripper stripper = new PDFTextStripper();
            stripper.setSortByPosition(true);// 排序

//			stripper.setWordSeparator("");//pdfbox对中文默认是用空格分隔每一个字，通过这个语句消除空格
            stripper.setStartPage(page_start);// 设置转换的开始页
            stripper.setEndPage(page_end);// 设置转换的结束页

             String text =  stripper.getText(doc);
            OutputStreamWriter osw = new OutputStreamWriter(new FileOutputStream(textFile), "UTF-8");
           // osw.write(text.getBytes("UTF-8"));
            (new FileOutputStream(textFile)).write(text.getBytes("UTF-8"));
              //  System.out.println(text);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static ArrayList<String> extractImages(File file, String targetFolder, int start, int end, int page_start, int page_end) {

        ArrayList<String> out = new ArrayList<String>();

        try {
            PDDocument document = PDDocument.load(file);
            int count = -1;
            int page_count = -1;
            for (PDPage page : document.getPages()) {

                page_count++;
                if (page_start >= 0) {
                    if (page_count < page_start) {
                        continue;
                    }
                }
                if (page_end >= 0) {
                    if (page_count > page_end) {
                        break;
                    }
                }

                ArrayList<PDPage> pages_stack = new ArrayList<PDPage>();
                pages_stack.add(page);
                int this_count = -1;
                while (pages_stack.size()>0)
                {
                    PDPage _page = pages_stack.remove(0);

                    PDResources resources = _page.getResources();

                    Iterable<COSName> names = resources.getXObjectNames();
                  //  int tmp_count = 0;
                    for (COSName name : names) {
                      //  tmp_count++;
                      // System.out.println(name);
                      //  System.out.println(resources.getXObject(name).getCOSObject().getValues().size());
                          if(resources.isImageXObject(name)){
                            count++;
                            this_count++;
                            if (start >= 0) {
                                if (count < start) {
                                    continue;
                                }
                            }
                            if (end >= 0) {
                                if (count > end) {
                                    break;
                                }
                            }
                            PDImageXObject image = (PDImageXObject) resources.getXObject(name);
                            ImageIO.write(image.getImage(), "png", new File(targetFolder + page_count + "_" + this_count + ".png"));
                            out.add(targetFolder + page_count + "_" + this_count + ".png");

                        }else if(resources.getXObject(name).getCOSObject().getValues().size()==8){
                              pages_stack.add(new PDPage(resources.getXObject(name).getCOSObject()));


                        }
                    }
                  //  System.out.println("tmp_ocount "+tmp_count);

                }



            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return out;


    }
}
