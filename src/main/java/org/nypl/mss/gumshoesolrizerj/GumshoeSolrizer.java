package org.nypl.mss.gumshoesolrizerj;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

import java.io.*;
import java.util.Map;
import java.util.TreeMap;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrServer;
import org.apache.tika.exception.TikaException;

public class GumshoeSolrizer {
    private GumshoePackage pack;
    private Map<String, File> fileHash = new TreeMap();
    private Map<String, File> accessMap = new TreeMap();
    private Config conf = ConfigFactory.parseFile(new File("application.conf"));
    private final HttpSolrServer solr;

    GumshoeSolrizer(String packageLoc) throws IOException, TikaException, SolrServerException {
        System.out.println("GumshoeJr Solrizer v.0.0.1");
        solr = new HttpSolrServer(conf.getString("solr.test"));
        pingServer();
        System.out.println("You passed: " + packageLoc);
        pack = new GumshoePackage(packageLoc);

        if(!pack.checkValid()){
            System.err.println(packageLoc + " is not a valid package");
            System.exit(1);
        }

        mapFiles();
        if(pack.getConfigMap().get("access").equals("TRUE")) mapAccessFiles();

        System.out.println(pack);
        parseInventory();
        solr.commit();
        solr.optimize();
    }

    private void mapFiles() throws IOException {
        for(File file: pack.getParserFiles().listFiles()){
            if(file.isFile())
                fileHash.put(DigestUtils.md5Hex(new FileInputStream(file)), file);
        }
    }

    private void mapAccessFiles(){
        File accessDir = new File(pack.getParserFiles().getAbsolutePath() + File.separator + "access");
        for(File file: accessDir.listFiles()){
            accessMap.put(FilenameUtils.getBaseName(file.getName()), file);
        }
    }

    private void pingServer() throws IOException {
        try{
            solr.ping();
            System.out.println("connected to solr server");
        } catch (SolrServerException ex){
            System.err.println("Cannot connect to Solr Server, exiting");
            System.exit(1);
        }
    }

    private void parseInventory() throws IOException, TikaException, SolrServerException {
        BufferedReader br = new BufferedReader(new FileReader(pack.getIndexFile()));
        String line;
        while((line = br.readLine()) != null){
            String[] info = line.split("\t");
            if(info.length == 7){
                if(pack.getConfigMap().get("access").equals("TRUE")){
                } else {
                    solr.add(new FileProcessor(line, fileHash.get(line.split("\t")[6].toLowerCase()), pack.getConfigMap()).getSolrDoc());
                    solr.commit();
                }
            }
        }
    }

    public static void main(String[] args) throws IOException, TikaException, SolrServerException {
        new GumshoeSolrizer(args[0]);
    }
}
