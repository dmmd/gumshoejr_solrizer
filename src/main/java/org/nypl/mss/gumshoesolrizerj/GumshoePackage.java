package org.nypl.mss.gumshoesolrizerj;


import java.io.BufferedReader;
import java.io.FileReader;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.io.FilenameUtils;

public class GumshoePackage {
    private File files, indexFile, parserFiles;
    private Map<String, String> configMap = new HashMap();

    GumshoePackage(String packageLoc) throws IOException {
        files = new File(packageLoc);
        for(File file: files.listFiles()){
            if(FilenameUtils.getExtension(file.getName()).equals("tsv")){
                indexFile = file;
            } else if(FilenameUtils.getExtension(file.getName()).equals("txt")){
                BufferedReader br = new BufferedReader(new FileReader(file));
                String line;
                while((line = br.readLine()) !=null){
                    String[] kv = line.split(":\\s*");
                    configMap.put(kv[0].trim(), kv[1].trim());
                }
            } else if(file.getName().equals("files") && file.isDirectory()&& file.listFiles().length >= 1){
                parserFiles = file;
            }
        }
    }

    public boolean checkValid(){
        if(indexFile != null && parserFiles != null)
            return true;
        else return false;
    }

    @Override
    public String toString(){
        StringBuilder sb = new StringBuilder();

        sb.append(files.getName()
                + "\nfiles: " + parserFiles.getAbsolutePath()
                + "\nindex: " + indexFile.getAbsoluteFile()
                + "\nconfiguration:");

        for(Map.Entry e: configMap.entrySet()){
            sb.append("\n\t" + e.getKey().toString() +": " + e.getValue().toString());
        }
        return sb.toString();
    }

    public File getIndexFile(){
        return indexFile;
    }

    public File getParserFiles(){
        return parserFiles;
    }

    public HashMap<String, String> getConfigMap(){
        return (HashMap<String, String>) configMap;
    }
}
