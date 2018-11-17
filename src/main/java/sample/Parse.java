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

    public Map<String,Integer> ParsingDocument(String docText) {
        termsAndFrequencyMap = new HashMap<String, Integer>();
        BreakTextToTerms(docText);
        return termsAndFrequencyMap;
    }

     private void BreakTextToTerms(String docText)
    {
       // docText = docText.replaceAll(",'`\\\\/\\(\\)\\[]","");
        List<String> TermsOfDoc = new ArrayList(Arrays.asList(docText.split("\\s+|\\t|;|\\.|\\?|!|-|:|@|\\[|\\]|\\(|\\)|\\{|\\}|_|\\*|/")));
        for(int i = 0; i < TermsOfDoc.size(); i++)
        {
            String term = TermsOfDoc.get(i);
            String nextTerm = "";
            if(IsStopWord(term))
                continue;
            if(i != TermsOfDoc.size() -1)
                nextTerm = TermsOfDoc.get(i+1);

            if(term.contains(".")){
                String[] termSplitedByPoint = new String[2];
                for(int j = 0; j < term.length(); j++)
                {
                    if(term.charAt(j) == '.')
                    {
                        termSplitedByPoint[0] = term.substring(0,j);
                        termSplitedByPoint[1] = term.substring(j+1);
                    }
                }
                if(isNumeric(termSplitedByPoint[0]) && isNumeric(termSplitedByPoint[1])) {
                    System.out.println(termSplitedByPoint[0] + " " + termSplitedByPoint[1]);
                    term = HandleNumbersWithPoint(termSplitedByPoint[0], termSplitedByPoint[1]);
                }
            }

           if(isNumeric(term))
           {
               //only number
               term = HandleNumbersThatHadComma(term);

               //123 Thousand to 123K
               if(nextTerm.equals("Thousand"))
                   term = term + 'K';
               if(nextTerm.equals("Million"))
                   term = term + 'M';
               if(nextTerm.equals("Billion") || nextTerm.equals("Trillion"))
                   term = term + 'B';

           }
        }
    }

    private static String HandleNumbersWithPoint(String beforePoint, String afterPoint)
    {
        if(beforePoint.length() >= 4 && beforePoint.length() <= 6)
            return beforePoint.substring(0,beforePoint.length()-3) + '.'
                    + beforePoint.substring(beforePoint.length()-3) + afterPoint + 'K';
        if(beforePoint.length() >= 7 && beforePoint.length() <= 9)
            return beforePoint.substring(0,beforePoint.length()-6) + '.'
                    + beforePoint.substring(beforePoint.length()-6) + afterPoint + 'M';
        if(beforePoint.length() >= 10 && beforePoint.length() <= 12)
            return beforePoint.substring(0,beforePoint.length()-9) + '.'
                    + beforePoint.substring(beforePoint.length()-9) + afterPoint + 'B';
        return null;

    }

    private String HandleNumbersThatHadComma(String number)
    {
        if(number.length() == 4)
        {
           number = DeleteTheZeros(number);
           if(number.length() > 1)
             return number.charAt(0) + '.' + number.substring(1) + 'K';
           else
               return number.charAt(0) + "K";
        }
        if(number.length() == 5)
        {
            number = DeleteTheZeros(number);
            if(number.length() > 2)
                return number.substring(0,2) + '.' + number.substring(2) + 'K';
            else
            return number.substring(0,2) + 'K';
        }
        if(number.length() == 6)
        {
            number = DeleteTheZeros(number);
            if(number.length() > 3)
                return number.substring(0,3) + '.' + number.substring(3) + 'K';
            else
                return number.substring(0,3) + 'K';
        }
        if(number.length() == 7)
        {
            number = DeleteTheZeros(number);
            if(number.length() > 1)
                return number.charAt(0) + '.' + number.substring(1) + 'M';
            else
                return number.charAt(0) + "M";
        }
        if(number.length() == 8)
        {
            number = DeleteTheZeros(number);
            if(number.length() > 2)
                return number.substring(0,2) + '.' + number.substring(2) + 'M';
            else
                return number.substring(0,2) + 'M';
        }
        if(number.length() == 9)
        {
            number = DeleteTheZeros(number);
            if(number.length() > 3)
                return number.substring(0,3) + '.' + number.substring(3) + 'M';
            else
                return number.substring(0,3) + 'M';
        }
        if(number.length() == 10)
        {
            number = DeleteTheZeros(number);
            if(number.length() > 1)
                return number.charAt(0) + '.' + number.substring(1) + 'B';
            else
                return number.charAt(0) + "B";
        }
        if(number.length() == 11)
        {
            number = DeleteTheZeros(number);
            if(number.length() > 2)
                return number.substring(0,2) + '.' + number.substring(2) + 'B';
            else
                return number.substring(0,2) + 'B';
        }
        if(number.length() == 12)
        {
            number = DeleteTheZeros(number);
            if(number.length() > 3)
                return number.substring(0,3) + '.' + number.substring(3) + 'B';
            else
                return number.substring(0,3) + 'B';
        }

        return number;
    }

    private static String DeleteTheZeros(String number)
    {
        int length = number.length();
        if(length >= 4 && length <= 6)
        {
            for(int i = 0; i < 3; i++)
            {
                if(number.charAt(length-i-1) == '0')
                    number = number.substring(0,length-i-1);
                else
                    break;
            }
        }

        if(length >= 7 && length <= 9)
        {
            for(int i = 0; i < 6; i++)
            {
                if(number.charAt(length-i-1) == '0')
                    number = number.substring(0,length-i-1);
                else
                    break;
            }
        }

        if(length >= 10 && length <= 12)
        {
            for(int i = 0; i < 9; i++)
            {
                if(number.charAt(length-i-1) == '0')
                    number = number.substring(0,length-i-1);
                else
                    break;
            }
        }

        return number;
    }


    private static boolean isNumeric(String word)
    {
        try
        {
            Double.parseDouble(word);
        }
        catch(NumberFormatException nfe)
        {
            return false;
        }
        return true;
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
