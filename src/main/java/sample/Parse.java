package sample;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

/**
 * This class is dividing every document to terms by specific rules.
 */
public class Parse {

    List<String> stopWordsList;
    Map<String, Integer> termsAndFrequencyMap;

    Parse(String stopWordsPath)
    {
        stopWordsList = ExtractStopWords(stopWordsPath);
    }

    public Map<String,Integer> ParsingDocument(String docText){
        termsAndFrequencyMap = new HashMap<String, Integer>();
        List<String> termsList = BreakTextToTerms(docText);

        for(int i = 0; i < termsList.size(); i++)
        {
            String termKey = termsList.get(i);
            if(termsAndFrequencyMap.containsKey(termKey))
                termsAndFrequencyMap.put(termKey, termsAndFrequencyMap.get(termsList.get(i)) + 1);
            else
                termsAndFrequencyMap.put(termKey,1);
        }

        return termsAndFrequencyMap;
    }

     private List<String> BreakTextToTerms(String docText)
    {
        List<String> splited = new ArrayList(Arrays.asList(docText.split("\\s+")));
        List<String> splitedWithoutStopWords = new ArrayList();

        for (int i = 0; i < splited.size(); i++)
        {
            if(!IsStopWord(splited.get(i)))
            {
                splitedWithoutStopWords.add(splited.get(i));
            }
        }



        return splitedWithoutStopWords;
    }

    boolean IsStopWord(String word)
    {
        return stopWordsList.contains(word);
    }

    private List<String> ExtractStopWords(String stopWordsPath) {
        Scanner s = null;
        try {
            s = new Scanner(new File(stopWordsPath));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        ArrayList<String> stopWordsList = new ArrayList<String>();
        while (s.hasNext()){
            stopWordsList.add(s.next());
        }
        s.close();

        return stopWordsList;
    }

}
