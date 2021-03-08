package gui.pangaautomaat;

import java.io.BufferedInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;

public class DataDownloader {
    public DataDownloader(){

    }
    public void Download(){
        try (BufferedInputStream inputStream = new BufferedInputStream(new URL("https://www.eestipank.ee/valuutakursid/export/xml/latest").openStream());
             FileOutputStream fileOS = new FileOutputStream("/data/datas.xml")) {
            byte[] data = new byte[1024];
            int byteContent;
            while ((byteContent = inputStream.read(data, 0, 1024)) != -1) {
                fileOS.write(data, 0, byteContent);
            }
        } catch (IOException e) {
            // handles IO exceptions
        }
    }
}
