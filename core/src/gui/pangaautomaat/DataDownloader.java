package gui.pangaautomaat;

import jdk.tools.jaotc.Main;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.*;
import java.io.*;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;

public class DataDownloader {
    public String dataPath;
    public String URL;
    public String absoluteDataPath;
    public String dataTime;
    public DataDownloader(){
        this.dataPath = "andmed/andmed.xml";
        this.URL = "https://www.eestipank.ee/valuutakursid/export/xml/latest";
        this.absoluteDataPath = new File(dataPath).getAbsolutePath();

    }
    public void download(){
        try {
            InputStream inputStream = new URL(this.URL).openStream();
            String path = absoluteDataPath;
            if (!Files.isDirectory(Paths.get(path).getParent())) {
                Files.createDirectories(Paths.get(path));
            }
            Files.copy(inputStream, Paths.get(path), StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public String[] getData(){
        if (!Files.exists(Paths.get(absoluteDataPath))){
            download();
        }
        try{
            DocumentBuilderFactory dBfactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = dBfactory.newDocumentBuilder();
            Document document = builder.parse(new File(dataPath));
            document.getDocumentElement().normalize();
            NodeList nList = document.getElementsByTagName("Cube");
            ArrayList<String> strings = new ArrayList<>();

            for (int i = 0; i < nList.getLength(); i++)
            {
                Node node = nList.item(i);
                if (node.getNodeType() == Node.ELEMENT_NODE)
                {
                    Element element = (Element) node;
                    if (element.hasAttribute("time")){
                        this.dataTime = element.getAttribute("time");
                    }
                    if (element.hasAttribute("currency")){
                        String valuutatähis = element.getAttribute("currency");
                        String s = valuutatähis+","+element.getAttribute("rate");
                        for (String valuuta:MainClass.valuutad) {
                            if (valuuta.equals(valuutatähis)){
                                strings.add(s);
                            }
                        }

                    }
                }
            }
            String[] toReturn = new String[strings.size()];
            for (int i = 0; i < strings.size(); i++) {
                toReturn[i] = strings.get(i);
            }
            return toReturn;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new String[]{"ERROR"};
    }
}
