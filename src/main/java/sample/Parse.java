package sample;
import java.io.File;
import java.io.FileNotFoundException;
import java.time.Month;
import java.util.*;

/**
 * This class is dividing every document to terms by specific rules.
 */
public class Parse {

    List<String> stopWordsList;
    Map<String, Integer> termsAndFrequencyMap;

    Stemmer stemmer;
    static List<String> monthsNames ;
    private boolean useStemmer;

    Parse(String stopWordsPath)
    {
        stopWordsList = ReadStopWordToList(stopWordsPath);
        InitMonthsNames();
        stemmer = new Stemmer();
        useStemmer = true;
    }

    public Map<String,Integer> ParsingDocument(String docText) {
        termsAndFrequencyMap = new HashMap<String, Integer>();
        BreakTextToTerms(docText);
        return termsAndFrequencyMap;
    }

    private void BreakTextToTerms(String docText)
    {
        //cleaning the document before splitting (| is seperating between characters, and \\ is sometimes needed
        docText = docText.replaceAll(",|\\(|\\)|'|\"|`|\\{|}|\\[|]|\\\\|","");
        //splitting the document according to these delimiters - the second one is spaces
        //List<String> TermsOfDoc = new ArrayList(Arrays.asList(docText.split("\\n|\\s+|\\t|;|\\.|\\?|!|:|@|\\[|]|\\(|\\)|\\{|}|_|\\*|/")));
        List<String> TermsOfDoc = new ArrayList(Arrays.asList(docText.split("\\n|\\s+|\\t|;|\\?|!|:|@|\\[|]|\\(|\\)|\\{|}|_|\\*")));
        //todo if the term ocontains "."

        for(int i = 0; i < TermsOfDoc.size(); i++)
        {
            //handles with stop-words or empty strings and also next terms after current term
            String term = TermsOfDoc.get(i);
            if((IsStopWord(term.toLowerCase()) || term.equals("")) && !(term.equals("between") || term.equals("Between")))
                continue;
            String nextTerm = "";
            String secondNextTerm = "";
            String thirdNextTerm = "";

            boolean isNumericTerm = IsNumeric(term);
            boolean isNumericNextTerm = false;

            if(i+1 <= TermsOfDoc.size()-1 )
            {
                nextTerm = TermsOfDoc.get(i + 1);
                isNumericNextTerm = IsNumeric(nextTerm);
            }
            if(i+2 <= TermsOfDoc.size()-1 )
                secondNextTerm = TermsOfDoc.get(i+2);
            if(i+3 <= TermsOfDoc.size()-1 )
                thirdNextTerm = TermsOfDoc.get(i+3);


            //word-word, word-word-word, number-word, word-number, number-number
            //length bigger than 5 because we want al least 2 words with the "-"
            //we do not want "-" or "--" to be terms
            if(term.contains("-") && term.length() >= 5)
            {
                if(useStemmer)
                    term = CallStemmer(term);
                AddTermToMap(term);

                continue;
            }

            //Between 18 and 24
            if((term.equals("Between") || term.equals("between")) && isNumericNextTerm && secondNextTerm.equals("and") && IsNumeric(thirdNextTerm))
            {
                term = term + " " + nextTerm + " " + secondNextTerm + " " + thirdNextTerm;
                i = i +3;
                if(useStemmer)
                    term = CallStemmer(term);
                AddTermToMap(term);
                continue;
            }

            //First additional rule: 100 Kilometers or 100 kilometers
            if(isNumericTerm && (nextTerm.equals("Kilometers") || nextTerm.equals("kilometers") || nextTerm.equals("km")))
            {
                term = term + "km";
                i++;
                if(useStemmer)
                    term = CallStemmer(term);
                AddTermToMap(term);
                continue;
            }

            //Second additional rule
            //Dates: "14 May 1994"
            if(isNumericTerm && monthsNames.contains(nextTerm.toUpperCase()) && IsNumeric(secondNextTerm) && secondNextTerm.length() > 2)
            {
                if(term.length() == 1)
                    term = "0" + GetMonthNumber(nextTerm) + "-0" + term + "-" + secondNextTerm;
                else
                    term = "0" + GetMonthNumber(nextTerm) + "-" + term + "-" + secondNextTerm;
                i = i + 2;
                if(useStemmer)
                    term = CallStemmer(term);
                AddTermToMap(term);
                continue;
            }


            //Dates: "14 May/MAY/may"
            if(isNumericTerm && monthsNames.contains(nextTerm.toUpperCase()))
            {
                //the day number is 4
                if(term.length() == 1)
                    term = "0" + GetMonthNumber(nextTerm) + "-0" + term;
                else
                    term = "0" + GetMonthNumber(nextTerm) + "-" + term;
                i++;
                if(useStemmer)
                    term = CallStemmer(term);
                AddTermToMap(term);
                continue;
            }


            //for case June 4, JUNE 4
            if(monthsNames.contains(term.toUpperCase()) && isNumericNextTerm  && nextTerm.length()<=2)
            {
                if(nextTerm.length() == 1)
                    term = "0" + GetMonthNumber(term) + "-0" + nextTerm;
                else
                    term = "0" + GetMonthNumber(term) + "-" + nextTerm;
                i++;
                if(useStemmer)
                    term = CallStemmer(term);
                AddTermToMap(term);
                continue;
            }


            // for case May 1994, MAY 1994
            if(monthsNames.contains(term.toUpperCase()) && isNumericNextTerm)
            {
                term = nextTerm + "-0" + GetMonthNumber(term);
                i++;
                if(useStemmer)
                    term = CallStemmer(term);
                AddTermToMap(term);
                continue;
            }



            //case "$450" or "450,000,000" or "$100 million" or "$100 billion"
            if(term.charAt(0) == '$' && IsNumeric(term.substring(1)))
            {
                //handles with dollar numbers
                double value = Double.parseDouble(term.substring(1));
                //case "$450"
                if(value < 1000000 && !(nextTerm.equals("million") || nextTerm.equals("billion") || nextTerm.equals("trillion")))
                {
                    term = term.substring(1) + " Dollars";
                    if(useStemmer)
                        term = CallStemmer(term);
                    AddTermToMap(term);
                    continue;
                }
                else
                {
                    //case "$100 million"
                    if(nextTerm.equals("million"))
                    {
                        term = term.substring(1);
                        term = term + " M Dollars";
                        if(useStemmer)
                            term = CallStemmer(term);
                        AddTermToMap(term);
                        i++;
                        continue;
                    }
                    //"$100 billion"
                    if(nextTerm.equals("billion"))
                    {
                        term = term.substring(1);
                        term = term + "000 M Dollars";
                        if(useStemmer)
                            term = CallStemmer(term);
                        AddTermToMap(term);
                        i++;
                        continue;
                    }
                    if(nextTerm.equals("trillion"))
                    {
                        term = term.substring(1);
                        term = term + "000000 M Dollars";
                        if(useStemmer)
                            term = CallStemmer(term);
                        AddTermToMap(term);
                        i++;
                        continue;
                    }
                    //"450,000,000"
                    else
                    {
                        term = term.substring(1);
                        term = DeleteTheZeros(term);
                        term = term + " M Dollars";
                        if(useStemmer)
                            term = CallStemmer(term);
                        AddTermToMap(term);
                        continue;
                    }


                }
            }
            // case "1,000,000 Dollars"
            if(isNumericTerm && nextTerm.equals("Dollars"))
            {
                term = DeleteTheZeros(term);
                term = term + "M Dollars";
                i++;
                if(useStemmer)
                    term = CallStemmer(term);
                AddTermToMap(term);
                continue;
            }
            //case "20.6m Dollars"
            if(IsNumeric(term.substring(0,term.length()-1)) && term.charAt(term.length()-1) == 'm' && nextTerm.equals("Dollars"))
            {
                term = term.substring(0,term.length()-1) + " M Dollars";
                i++;
                if(useStemmer)
                    term = CallStemmer(term);
                AddTermToMap(term);
                continue;
            }

            //case "100bn Dollars"
            if(term.length()>=2 && IsNumeric(term.substring(0,term.length()-2)) && term.substring(term.length()-2,term.length()).equals("bn") && nextTerm.equals("Dollars"))
            {
                term = term.substring(0,term.length()-2) + "000 M Dollars";
                i++;
                if(useStemmer)
                    term = CallStemmer(term);
                AddTermToMap(term);
                continue;
            }
            //case "100 billion U.S. dollars"
            if(isNumericTerm && nextTerm.equals("billion") && secondNextTerm.equals("U.S.") && thirdNextTerm.equals("dollars"))
            {
                term = term + "000 M Dollars";
                i = i + 3;
                if(useStemmer)
                    term = CallStemmer(term);
                AddTermToMap(term);
                continue;
            }
            //case "100 million U.S. dollars"
            if(isNumericTerm && nextTerm.equals("million") && secondNextTerm.equals("U.S.") && thirdNextTerm.equals("dollars"))
            {
                term = term + " M Dollars";
                i = i + 3;
                if(useStemmer)
                    term = CallStemmer(term);
                AddTermToMap(term);
                continue;
            }
            //case "1 trillion U.S. dollars"
            if(isNumericTerm && nextTerm.equals("trillion") && secondNextTerm.equals("U.S.") && thirdNextTerm.equals("dollars"))
            {
                term = term + "000000 M Dollars";
                i = i + 3;
                if(useStemmer)
                    term = CallStemmer(term);
                AddTermToMap(term);
                continue;
            }


            //handles numbers that need to have "%" next to them - handles every case
            if(isNumericTerm && (nextTerm.equals("percent") || nextTerm.equals("percentage")))
            {
                term = term + "%";
                i++;
                if(useStemmer)
                    term = CallStemmer(term);
                AddTermToMap(term);
                continue;
            }


            //handles with numbers with point in them
            else if(term.contains("."))
            {
                String[] termSplittedByPoint = new String[2];
                for(int j = 0; j < term.length(); j++)
                {
                    if(term.charAt(j) == '.')
                    {
                        termSplittedByPoint[0] = term.substring(0,j);
                        termSplittedByPoint[1] = term.substring(j+1);
                    }
                }
                if(IsNumeric(termSplittedByPoint[0]) && termSplittedByPoint[0].length() >= 4 && IsNumeric(termSplittedByPoint[1]))
                {
                    term = HandleNumbersWithPoint(termSplittedByPoint[0], termSplittedByPoint[1]);
                    if(useStemmer)
                        term = CallStemmer(term);
                    AddTermToMap(term);
                    continue;
                }

            }

            //handling with regular numbers, with or without a word after
            else if(isNumericTerm)
            {
                //123 Thousand to 123K
                if(nextTerm.equals("Thousand"))
                {
                    term = term + 'K';
                    i++;
                    if(useStemmer)
                        term = CallStemmer(term);
                    AddTermToMap(term);
                    continue;
                }
                if(nextTerm.equals("Million"))
                {
                    term = term + 'M';
                    i++;
                    if(useStemmer)
                        term = CallStemmer(term);
                    AddTermToMap(term);
                    continue;
                }
                if(nextTerm.equals("Billion"))
                {
                    term = term + 'B';
                    i++;
                    if(useStemmer)
                        term = CallStemmer(term);
                    AddTermToMap(term);
                    continue;
                }
                if(nextTerm.equals("Trillion"))
                {
                    term = term + "00B";
                    i++;
                    if(useStemmer)
                        term = CallStemmer(term);
                    AddTermToMap(term);
                    continue;
                }
                //only a number
                else if(term.length() >= 4)
                {
                    term = HandleRegularNumbers(term);
                    if(useStemmer)
                        term = CallStemmer(term);
                    AddTermToMap(term);
                    continue;
                }


            }

            //case "35 3/4"
            if(isNumericTerm && nextTerm.contains("/")) {
                int indexOfSlash = nextTerm.indexOf("/");
                if (IsNumeric(nextTerm.substring(0, indexOfSlash)) && IsNumeric(nextTerm.substring(indexOfSlash + 1))) {

                    term = term + " " + nextTerm;
                    i++;
                    if(useStemmer)
                        term = CallStemmer(term);
                    AddTermToMap(term);
                    continue;
                }
            }
            if(useStemmer)
                term = CallStemmer(term);
            AddTermToMap(term);
        }
    }

    private void AddTermToMap(String term) {
        //NBA or GSW
        if(IsUpperCase(term))
        {
            if(termsAndFrequencyMap.containsKey(term))
                termsAndFrequencyMap.put(term, termsAndFrequencyMap.get(term) + 1);
            else
                termsAndFrequencyMap.put(term, 1);
        }
        //Liron or State
        else if(!IsUpperCase(term) && FirstIsUpperCase(term.charAt(0)))
        {
            //map contains liron or state
            if(termsAndFrequencyMap.containsKey(term.toLowerCase()))
            {
                termsAndFrequencyMap.put(term.toLowerCase(), termsAndFrequencyMap.get(term.toLowerCase()) + 1);
            }
            else
            {   //map contains LIRON or STATE
                if(termsAndFrequencyMap.containsKey(term.toUpperCase()))
                    termsAndFrequencyMap.put(term.toUpperCase(), termsAndFrequencyMap.get(term.toUpperCase()) + 1);
                    //add Liron as LIRON
                else
                    termsAndFrequencyMap.put(term.toUpperCase(), 1);
            }

        }
        //liron or first
        else if(IsLowerCase(term))
        {
            //case term is "first" and we already have "FIRST" on the map
            //save the frequency of "FIRST", remove it from map and add frequency + 1 to "first"
            if(termsAndFrequencyMap.containsKey(term.toUpperCase()))
            {
                int frequencyOfUpperCase = termsAndFrequencyMap.get(term.toUpperCase());
                termsAndFrequencyMap.remove(term.toUpperCase());
                termsAndFrequencyMap.put(term, frequencyOfUpperCase + 1);
            }
            //we do not have "FIRST" in our map
            else
            {
                //we have "first" in our map
                if(termsAndFrequencyMap.containsKey(term))
                    termsAndFrequencyMap.put(term, termsAndFrequencyMap.get(term) + 1);
                    //adds "first" to the map
                else
                    termsAndFrequencyMap.put(term, 1);
            }

        }

        else
        {
            if(termsAndFrequencyMap.containsKey(term))
                termsAndFrequencyMap.put(term, termsAndFrequencyMap.get(term) + 1);
            else
                termsAndFrequencyMap.put(term, 1);
        }
    }

    //Get "MAY" or "May" or "may" and return "5"
    private static int GetMonthNumber(String monthName) {
        monthName = monthName.toUpperCase();
        if(monthName.equals("JAN"))
            monthName = "JANUARY";
        else if(monthName.equals("FEB"))
            monthName = "FEBRUARY";
        else if(monthName.equals("MAR"))
            monthName = "MARCH";
        else if(monthName.equals("APR"))
            monthName = "APRIL";
        else if(monthName.equals("MAY"))
            monthName = "MAY";
        else if(monthName.equals("JUN"))
            monthName = "JUNE";
        else if(monthName.equals("JUL"))
            monthName = "JULY";
        else if(monthName.equals("AUG"))
            monthName = "AUGUST";
        else if(monthName.equals("SEP"))
            monthName = "SEPTEMBER";
        else if(monthName.equals("OCT"))
            monthName = "OCTOBER";
        else if(monthName.equals("NOV"))
            monthName = "NOVEMBER";
        else if(monthName.equals("DEC"))
            monthName = "DECEMBER";
        return Month.valueOf(monthName.toUpperCase()).getValue();
    }

    //checks if a word is all UpperCases using ASCII
    public static boolean IsUpperCase(String term)
    {
        for(int i = 0; i < term.length(); i++)
        {
            char c = term.charAt(i);
            if(c < 65 || c > 90)
                return false;
        }
        return true;
    }

    public static boolean FirstIsUpperCase(char firstCharOfTerm)
    {
        if(firstCharOfTerm < 65 || firstCharOfTerm > 90)
            return false;
        return true;
    }

    //checks if a word is all LowerCases using ASCII
    public static boolean IsLowerCase(String term)
    {
        for(int i = 0; i < term.length(); i++)
        {
            char c = term.charAt(i);
            if(c < 97 || c > 122)
                return false;
        }
        return true;
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

    private String HandleRegularNumbers(String number)
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

    //can read regular numbers and also numbers with dots.
    private static boolean IsNumeric(String word)
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

    private List<String> ReadStopWordToList(String stopWordsPath) {
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

    private void InitMonthsNames()
    {
        monthsNames = new ArrayList<String>();
        monthsNames.add("JANUARY");
        monthsNames.add("FEBRUARY");
        monthsNames.add("MARCH");
        monthsNames.add("APRIL");
        monthsNames.add("MAY");
        monthsNames.add("JUNE");
        monthsNames.add("JULY");
        monthsNames.add("AUGUST");
        monthsNames.add("SEPTEMBER");
        monthsNames.add("OCTOBER");
        monthsNames.add("NOVEMBER");
        monthsNames.add("DECEMBER");

        //Short writing
        monthsNames.add("JAN");
        monthsNames.add("FEB");
        monthsNames.add("MAR");
        monthsNames.add("APR");
        monthsNames.add("MAY");
        monthsNames.add("JUN");
        monthsNames.add("JUL");
        monthsNames.add("AUG");
        monthsNames.add("SEP");
        monthsNames.add("OCT");
        monthsNames.add("NOV");
        monthsNames.add("DEC");
    }

    private String CallStemmer(String term)
    {
        for(int i = 0; i < term.length(); i++)
        {
            stemmer.add(term.charAt(i));
        }
        stemmer.stem();
        return stemmer.toString();
    }


}