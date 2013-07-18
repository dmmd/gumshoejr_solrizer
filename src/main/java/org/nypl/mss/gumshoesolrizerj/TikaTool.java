package org.nypl.mss.gumshoesolrizerj;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import opennlp.tools.namefind.NameFinderME;
import opennlp.tools.namefind.TokenNameFinderModel;
import opennlp.tools.tokenize.SimpleTokenizer;
import opennlp.tools.tokenize.Tokenizer;
import opennlp.tools.util.Span;
import org.apache.tika.Tika;
import org.apache.tika.exception.TikaException;
import org.apache.tika.language.ProfilingWriter;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Set;
import java.util.TreeSet;


class TikaTool {
    private final Tika tika;
    private final File file;
    private String type;
    private ProfilingWriter pw = new ProfilingWriter();
    private Set<String> nameEnts = new TreeSet<String>();
    private Set<String> orgEnts = new TreeSet<String>();
    private Set<String> locEnts = new TreeSet<String>();
    private final NameFinderME persNameFinder;
    private final NameFinderME orgNameFinder;
    private final NameFinderME locNameFinder;
    private final Config conf;

    public TikaTool(File file) throws IOException, TikaException{
        tika = new Tika();
        conf = ConfigFactory.parseFile(new File("application.conf"));
        this.file = file;
        persNameFinder = new NameFinderME(new TokenNameFinderModel(new FileInputStream(new File(conf.getString("nlp.models"),"en-ner-person.bin"))));
        orgNameFinder = new NameFinderME(new TokenNameFinderModel(new FileInputStream(new File(conf.getString("nlp.models"),"en-ner-organization.bin"))));
        locNameFinder = new NameFinderME(new TokenNameFinderModel(new FileInputStream(new File(conf.getString("nlp.models"),"en-ner-location.bin"))));
        parseFile();

    }

    private void parseFile() throws IOException, TikaException{
        try{
            type = tika.detect(file);
            Tokenizer tokenizer = SimpleTokenizer.INSTANCE;
            BufferedReader br = new BufferedReader(tika.parse(new FileInputStream(file)));
            String line;

            while((line = br.readLine()) != null){
                //add to language profile
                pw.append(line);
                //tokenize
                Span[] tokensSpans = tokenizer.tokenizePos(line);
                String[] tokens = Span.spansToStrings(tokensSpans, line);

                processEnts(line, tokensSpans, tokens, persNameFinder, nameEnts);
                processEnts(line, tokensSpans, tokens, orgNameFinder, orgEnts);
                processEnts(line, tokensSpans, tokens, locNameFinder, locEnts);
            }
        }catch(Exception e){
            System.err.println(e + " " + file.getName());
        }
    }

    public void processEnts(String line, Span[] tokensSpans, String[] tokens, NameFinderME finder, Set<String> ents){
        Span[] names = finder.find(tokens);
        for(int i = 0; i < names.length; i++){
            Span startSpan = tokensSpans[names[i].getStart()];
            int nameStart = startSpan.getStart();

            Span endSpan = tokensSpans[names[i].getEnd() - 1];
            int nameEnd = endSpan.getEnd();

            String name = line.substring(nameStart, nameEnd);
            if(!ents.contains(name)){
                ents.add(name);
            }
        }
    }

    public String getType(){
        return type;
    }

    public String getFullText() throws IOException, TikaException{
        return tika.parseToString(file);
    }

    public String getLang(){
        if(pw.getProfile().getCount() > 0)
            return pw.getLanguage().getLanguage();
        else
            return "unknown";
    }

    public Set<String> getNames(){
        return nameEnts;
    }

    public Set<String> getOrgs(){
        return orgEnts;
    }

    public Set<String> getLocs(){
        return locEnts;
    }
}