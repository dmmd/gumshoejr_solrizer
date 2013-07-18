package org.nypl.mss.gumshoesolrizerj;

import java.io.File;
import org.apache.solr.common.SolrInputDocument;
import org.apache.tika.exception.TikaException;

import java.io.IOException;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Date;
import java.util.HashMap;

class FileProcessor{
    private SolrInputDocument solrDoc;
    FileProcessor(String fileInfo, File file, HashMap<String, String> config) throws IOException, TikaException {
        String[] info = fileInfo.split("\t");
        solrDoc  = new SolrInputDocument();
        TikaTool tikaTool = new TikaTool(file);
        try{
            System.out.println("Processing: " + file.getName());
            solrDoc.addField("id", config.get("colId") + "." + info[0]);
            solrDoc.addField("colId", config.get("colId"));
            solrDoc.addField("colName", config.get("colName")) ;
            solrDoc.addField("componentIdentifier", config.get("componentIdentifier")) ;
            solrDoc.addField("componentTitle", config.get("componentTitle")) ;
            solrDoc.addField("localIdentifier", config.get("localIdentifier"));
            solrDoc.addField("filename", info[1]);
            if(!file.getName().equals(info[1]));
                solrDoc.addField("accessFilename", file.getName());
            solrDoc.addField("filePath", info[2]);
            solrDoc.addField("fileType", info[3]);
            solrDoc.addField("fileSize", info[4]);
            solrDoc.addField("modDate",convertDateField(info[5]));
            solrDoc.addField("diskId", info[2].split("/")[0]);
            solrDoc.addField("md5", info[6]);
            solrDoc.addField("fileId", info[0]);

            //get basic tika info
            solrDoc.addField("tikaMime", tikaTool.getType());
            solrDoc.addField("language", tikaTool.getLang());

        //get entities
            for(String name: tikaTool.getNames()){solrDoc.addField("names", name);}
            for(String org: tikaTool.getOrgs()){solrDoc.addField("orgs", org);}
            for(String loc: tikaTool.getLocs()){solrDoc.addField("locs", loc);}

        //get full text
            solrDoc.addField("text", tikaTool.getFullText());
        } catch (Exception ex) {
            System.err.println(ex);
        }
    }

    private Date convertDateField(String in){
        Calendar cal = new GregorianCalendar();
        String d = in.split("\\(")[1];
        d = d.substring(0, d.length() -1);
        String[] date = d.split(" ")[0].split("-");
        String[] time = d.split(" ")[1].split(":");
        cal.set(Integer.parseInt(date[0]), Integer.parseInt(date[1]) - 1, Integer.parseInt(date[2]),
                Integer.parseInt(time[0]), Integer.parseInt(time[1]), Integer.parseInt(time[2])
        );
        return new Date(cal.getTimeInMillis());
    }

    public SolrInputDocument getSolrDoc(){
        return solrDoc;
    }
}