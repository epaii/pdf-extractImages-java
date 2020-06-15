
import org.apache.pdfbox.cos.COSName;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDResources;

import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;


import javax.imageio.ImageIO;
import java.io.File;

import java.util.ArrayList;

/**
 * Hello world!
 */
public class App {
    public static void main(String[] args) {
        File file = new File(args[0]);
        ArrayList<String> out = extractImages(file, args[1], args.length > 2 ? Integer.parseInt(args[2]) : -1, args.length > 3 ? Integer.parseInt(args[3]) : -1, args.length > 4 ? Integer.parseInt(args[4]) : -1, args.length > 5 ? Integer.parseInt(args[5]) : -1);


//        File file = new File("/Volumes/BOOTCAMP/php/phpworkspace/gaoxin-pdf-signs/pdfs/00000112901727.pdf");
//        ArrayList<String> out = extractImages(file, "/Volumes/BOOTCAMP/php/phpworkspace/gaoxin-pdf-signs/imgs/", -1, -1, -1, args.length > 5 ? Integer.parseInt(args[5]) : -1);

        if (out != null)
            for (int i = 0; i < out.size(); i++) {
                System.out.println(out.get(i));
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
                        //System.out.println(name);
                        //System.out.println(resources.getXObject(name).getCOSObject().getValues().size());
                        if(resources.getXObject(name).getCOSObject().getValues().size()==8){
                            pages_stack.add(new PDPage(resources.getXObject(name).getCOSObject()));
                        }else if(resources.isImageXObject(name)){
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

                        }
                    }
                  //  System.out.println("tmp_ocount "+tmp_count);

                }



            }
        } catch (Exception e) {
           // System.out.println(e.getMessage());
        }
        return out;


    }
}
