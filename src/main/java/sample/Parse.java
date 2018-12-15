package sample;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.*;
import java.net.URL;
import java.nio.charset.Charset;
import java.time.Month;
import java.util.*;

/**
 * This class is dividing every document to terms by specific rules.
 */
public class Parse {

    static HashSet<String> monthsNames;
    static HashSet<String> stopWordsList;
    private Stemmer stemmer;
    private boolean useStemmer;
    private List<String> TermsOfDoc;
    private String term = "";
    private String termToLowerCase = "";
    private String termToUpperCase = "";
    private String nextTerm = "";
    private String secondNextTerm = "";
    private String thirdNextTerm = "";
    private Map<String, Integer> termsAndFrequencyMap;

    Parse(String stopWordsPath, boolean stemmerSelection) throws IOException{

        InitMonthsNames();
        stopWordsList = ReadStopWordToList(stopWordsPath);

        //stemmer defenition
        this.useStemmer = stemmerSelection;
        if(useStemmer)
            stemmer = new Stemmer();

    }


    public boolean getStemmer()
    {
        return useStemmer;
    }


    public Map<String, Integer> ParsingDocument(String docText, String docNum) {

        termsAndFrequencyMap = new HashMap<>();
        BreakTextToTerms(docText, docNum);
        return termsAndFrequencyMap;
    }

    //delete the chars - and . from the beginning and from the end of every term
    public void cleaningTerm() {

        if(!term.equals("")) {
            char firstC = term.charAt(0);
            char lastC = term.charAt(term.length() - 1);

            if (firstC == '.' || firstC == '-')
                term = term.substring(1);
            if (!term.equals("") && (lastC == '.' || lastC == '-'))
                term = term.substring(0, term.length() - 1);
        }
    }


    private void BreakTextToTerms(String docText, String docNum) {

        //cleaning the document before splitting (| is separating between characters, and \\ is sometimes needed
        docText = docText.replaceAll(",|\\(|\\)|'|\"|`|\\{|}|\\[|]|\\\\|#|--|\\+|---|&|\\.\\.\\.|\\.\\.|\\||=|>|<|//|", "");

        //splitting the document according to these delimiters - the second one is spaces
        TermsOfDoc = new ArrayList(Arrays.asList(docText.split("\\n|\\s+|\\t|;|\\?|!|:|@|\\[|]|\\(|\\)|\\{|}|_|\\*")));

        //runs over one doc's length
        for (int i = 0; i < TermsOfDoc.size(); i++) {

            //extracting every term and saving it's lowerCase and UpperCase
            term = TermsOfDoc.get(i);
            cleaningTerm();
            termToLowerCase = term.toLowerCase();
            termToUpperCase = term.toUpperCase();

            //handles with stop-words or empty strings and also next terms after current term
            if ((IsStopWord(termToLowerCase) || term.equals("")))
                continue;

            //extracting nextTerm
            if (i + 1 <= TermsOfDoc.size() - 1)
                nextTerm = TermsOfDoc.get(i + 1);

            //checking if term is number or String
            if (IsNumeric(term))
                i = HandleWithNumbers(i);
            else
            {
                //if the term is a city - update in cityMap
                if(Indexer.citiesInAPI.containsKey(termToUpperCase)) {
                    updateCitiesInCorpus(docNum, i);

                }
                i = HandleWithStrings(i);
            }
        }
    }

    //Update the map that holds as keys the city name and as values an object og type of CityInMap
    public void updateCitiesInCorpus(String docNum, int positionInText) {
        String termInUpperCase = termToUpperCase;
        //if the city is in citiesInCorpus
        if(Indexer.citiesInCorpus.containsKey(termInUpperCase))
        {
            //not the first time of that city in that document
            if (Indexer.citiesInCorpus.get(termInUpperCase).placementsInDocs.containsKey(docNum))
                Indexer.citiesInCorpus.get(termInUpperCase).placementsInDocs.get(docNum).add(positionInText);
            else {
                List<Integer> positionsList = new ArrayList<>();
                positionsList.add(positionInText);
                Indexer.citiesInCorpus.get(termInUpperCase).placementsInDocs.put(docNum, positionsList);
            }
        }
        else //bring data from citiesInAPI
        {
            Indexer.citiesInCorpus.put(termInUpperCase, Indexer.citiesInAPI.get(termInUpperCase));
            List<Integer> positionsList = new ArrayList<>();
            positionsList.add(positionInText);
            Indexer.citiesInCorpus.get(termInUpperCase).placementsInDocs.put(docNum, positionsList);
        }
    }

    //This method gets terms which are numeric terms and checks if one of the defined rules on numeric terms
    //Is relevant for that term
    private int HandleWithNumbers(int index) {

        //nextTerm in lowerCase
        String nextTermToLowerCase = nextTerm.toLowerCase();

        //for numbers with point
        if(term.contains("."))
        {
            //split by "."
            String[] termSplittedByPoint = new String[2];
            for (int j = 0; j < term.length(); j++) {
                if (term.charAt(j) == '.') {
                    termSplittedByPoint[0] = term.substring(0, j);
                    termSplittedByPoint[1] = term.substring(j + 1);
                }
            }
            //thousand, million, billion and trillion
            if(termSplittedByPoint[0].length() >= 4 && termSplittedByPoint[0].length() <= 12)
                term = HandleNumbersWithPoint(termSplittedByPoint[0], termSplittedByPoint[1]);

        }
        //for numbers without a point
        else if(term.length() > 4)
            term = HandleRegularNumbers(term);

        CreateSecondAndThirdTerm(index);

        //for case "35 3/4"
        if (nextTerm.contains("/")) {
            int indexOfSlash = nextTerm.indexOf("/");
            if (IsNumeric(nextTerm.substring(0, indexOfSlash)) && IsNumeric(nextTerm.substring(indexOfSlash + 1))) {
                term = term + " " + nextTerm;
                index++;
            }

            //for case 22 3/4 Dollars
            if (secondNextTerm.equals("Dollars")) {
                index++;
                term = term + " Dollars";
                AddTermNumberToMap(term);
                return index;
            }
        }

        //handling with next term
        //for case next term is Dollars
        if (nextTerm.equals("Dollars")) {
            index++;
            term = term + " Dollars";
            AddTermNumberToMap(term);
            return index;
        }

        //handles numbers that need to have "%" next to them
        if ((nextTerm.equals("percent") || nextTerm.equals("percentage"))) {
            term = term + "%";
            index++;
            AddTermNumberToMap(term);
            return index;
        }

        if (nextTerm.equals("Thousand") ) {
            index++;
            if(term.length() < 4)
                term = term+"K";
            AddTermNumberToMap(term);
            return index;
        }

        if (nextTermToLowerCase.equals("million")) {
            //case "100 million U.S. dollars"
            if (secondNextTerm.equals("U.S.") && thirdNextTerm.equals("dollars")) {
                term = term + " M Dollars";
                index = index + 3;
                AddTermNumberToMap(term);
                return index;
            } else {
                term = term + 'M';
                index++;
                AddTermNumberToMap(term);
                return index;
            }
        }

        if (nextTermToLowerCase.equals("billion")) {
            //case "100 billion U.S. dollars"
            if (secondNextTerm.equals("U.S.") && thirdNextTerm.equals("dollars")) {
                term = term + "000 M Dollars";
                index = index + 3;
                AddTermNumberToMap(term);
                return index;
            } else {
                term = term + 'B';
                index++;
                AddTermNumberToMap(term);
                return index;
            }

        }

        if (nextTermToLowerCase.equals("trillion")) {
            //case "1 trillion U.S. dollars"
            if (secondNextTerm.equals("U.S.") && thirdNextTerm.equals("dollars")) {
                term = term + "000000 M Dollars";
                index = index + 3;
                AddTermNumberToMap(term);
                return index;
            } else {
                term = term + "00B";
                index++;
                AddTermNumberToMap(term);
                return index;
            }
        }

        //First additional rule: 100 Kilometers or 100 kilometers
        if ((nextTerm.equals("Kilometers") || nextTerm.equals("kilometers") || nextTerm.equals("km"))) {
            term = term + "km";
            index++;
            AddTermNumberToMap(term);
            return index;
        }

        //for the specific case May 15/MAY 15
        if(index != 0 && CheckPervTermEqualMay(index) )
        {
            int monthNum = GetMonthNumber(TermsOfDoc.get(index - 1));
            if(monthNum < 10) {
                if (term.length() == 1)
                    term = "0" + monthNum + "-0" + term;
                else
                    term = "0" + monthNum + "-" + term;
            }
            else
            {
                if (term.length() == 1)
                    term = monthNum + "-0" + term;
                else
                    term = monthNum + "-" + term;
            }

            AddTermToMap(term);
            return index;
        }

        //Second additional rule
        //Dates: "14 May 1994"
        if (monthsNames.contains(nextTerm.toUpperCase())) {
            int monthNum;
            if (IsNumeric(secondNextTerm) && secondNextTerm.length() > 2) {
                if (term.length() == 1) {
                    monthNum = GetMonthNumber(nextTerm);
                    if (monthNum < 10)
                        term = "0" + monthNum + "-0" + term + "-" + secondNextTerm;
                    else
                        term = monthNum + "-0" + term + "-" + secondNextTerm;
                } else {
                    monthNum = GetMonthNumber(nextTerm);
                    if (monthNum < 10)
                        term = "0" + monthNum + "-" + term + "-" + secondNextTerm;
                    else
                        term = monthNum + "-" + term + "-" + secondNextTerm;
                }
                index = index + 2;
                AddTermNumberToMap(term);
                return index;
            }
            //Dates: "14 May/MAY/may"
            else {
                //the day number is 4
                if (term.length() == 1) {
                    monthNum = GetMonthNumber(nextTerm);
                    if (monthNum < 10)
                        term = "0" + monthNum + "-0" + term;
                    else
                        term = monthNum + "-0" + term;
                } else {
                    monthNum = GetMonthNumber(nextTerm);
                    if (monthNum < 10)
                        term = "0" + monthNum + "-" + term;
                    else
                        term = monthNum + "-" + term;
                }


                index++;
                AddTermNumberToMap(term);
                return index;
            }
        }

        //Between 18 and 24
        if (nextTerm.equals("and") && IsNumeric(secondNextTerm) && index != 0 && CheckPervTermEqualBetween(index)) {
            CreateSecondAndThirdTerm(index);
            term = "between" + " " + term + " " + nextTerm + " " + secondNextTerm;
            index = index + 2;
            AddTermNumberToMap(term);
            return index;
        }

        AddTermNumberToMap(term);
        return index;

    }

    //This method handle with terms which are are not numeric
    private int HandleWithStrings(int index) {

        //case terms starts with $
        if (term.charAt(0) == '$' && IsNumeric(term.substring(1))) {
            //handles with dollar numbers
            double value = Double.parseDouble(term.substring(1));

            //case "$100 million"
            if (nextTerm.equals("million")) {
                term = term.substring(1);
                term = term + " M Dollars";
                AddTermToMap(term);
                index++;
                return index;
            }
            //"$100 billion"
            if (nextTerm.equals("billion")) {
                term = term.substring(1);
                term = term + "000 M Dollars";
                AddTermToMap(term);
                index++;
                return index;
            }
            if (nextTerm.equals("trillion")) {
                term = term.substring(1);
                term = term + "000000 M Dollars";
                AddTermToMap(term);
                index++;
                return index;
            }
            //"$450,000,000"
            else if(value > 1000000 ) {
                term = term.substring(1);
                term = DeleteTheZeros(term);
                term = term + " M Dollars";
                AddTermToMap(term);
                return index;
            }
            //$10
            else if(value < 1000000)
            {
                term = term.substring(1) + " Dollars";
                AddTermToMap(term);
                return index;
            }
        }

        if(nextTerm.equals("Dollars")) {
            //case "20.6m Dollars"
            if (IsNumeric(term.substring(0, term.length() - 1)) && term.charAt(term.length() - 1) == 'm') {
                term = term.substring(0, term.length() - 1) + " M Dollars";
                index++;
                AddTermToMap(term);
                return index;
            }

            //case "100bn Dollars"
            if (term.length() >= 2 && IsNumeric(term.substring(0, term.length() - 2)) && term.substring(term.length() - 2).equals("bn")) {
                term = term.substring(0, term.length() - 2) + "000 M Dollars";
                index++;
                AddTermToMap(term);
                return index;
            }

        }

        //for case June 4, JUNE 4
        if (monthsNames.contains(termToUpperCase) && IsNumeric(nextTerm)) {
            int monthNum = GetMonthNumber(term);
            if (nextTerm.length() <= 2) {
                if (nextTerm.length() == 1)
                {
                    if(monthNum < 10)
                        term = "0" + monthNum + "-0" + nextTerm;
                    else
                        term = monthNum + "-0" + nextTerm;
                } else {

                    if (monthNum < 10)
                        term = "0" + monthNum + "-" + nextTerm;
                    else
                        term = monthNum + "-" + nextTerm;
                }
                index++;
                AddTermToMap(term);
                return index;
            }
            // for case May 1994, MAY 1994
            else {

                if(monthNum < 10)
                    term = nextTerm + "-0" + monthNum;
                else
                    term = nextTerm + "-" + monthNum;
                index++;
                AddTermToMap(term);
                return index;
            }
        }

        //word-word, word-word-word, number-word, word-number, number-number
        //length bigger than 5 because we want al least 2 words with the "-"
        //we do not want "-" or "--" to be terms
        if (term.contains("-") && term.charAt(0) != '-') {
            AddTermToMap(term);
            return index;
        }

        AddTermToMap(term);
        return index;
    }

    //checking if the precious term is between
    private boolean CheckPervTermEqualBetween(int index) {
        return TermsOfDoc.get(index - 1).toLowerCase().equals("between");
    }

    //checking if the precious term is May
    private boolean CheckPervTermEqualMay(int index) {
        return (TermsOfDoc.get(index - 1).toLowerCase().equals("may")) ;
    }

    //creating secondNextTerm and thirdNextTerm
    private void CreateSecondAndThirdTerm(int index) {
        if (index + 2 <= TermsOfDoc.size() - 1)
            secondNextTerm = TermsOfDoc.get(index + 2);
        if (index + 3 <= TermsOfDoc.size() - 1)
            thirdNextTerm = TermsOfDoc.get(index + 3);

    }

    //inserts a numeric term to the temporary map with it's frequency
    //handles with terms of upper case letters. lower case letters and terms with first letter as capital letter
    private void AddTermNumberToMap(String term) {
      /*  if (term.charAt(term.length() - 1) == '.' || term.charAt(term.length() - 1) == '-')
            term = term.substring(0, term.length() - 1);

        if(term.equals(""))
            return;
       */
        if (useStemmer)
            term = CallStemmer(term);

        if (termsAndFrequencyMap.containsKey(term))
            termsAndFrequencyMap.put(term, termsAndFrequencyMap.get(term) + 1);
        else
            termsAndFrequencyMap.put(term, 1);

    }

    //inserts a non-numeric term to the temporary map with it's frequency
    //
    private void AddTermToMap(String term) {

      /*  if (term.charAt(term.length() - 1) == '.' || term.charAt(term.length() - 1) == '-')
            term = term.substring(0, term.length() - 1);

        if(term.equals(""))
            return;
       */
        termToLowerCase = term.toLowerCase();
        termToUpperCase = term.toUpperCase();

        if (useStemmer)
            term = CallStemmer(term);
        //NBA or GSW
        if (IsUpperCase(term)) {

            if (termsAndFrequencyMap.containsKey(termToLowerCase)) {
                termsAndFrequencyMap.put(termToLowerCase, termsAndFrequencyMap.get(termToLowerCase) + 1);
            } else if (termsAndFrequencyMap.containsKey(term))
                termsAndFrequencyMap.put(term, termsAndFrequencyMap.get(term) + 1);
            else
                termsAndFrequencyMap.put(term, 1);

        }
        //Liron or State
        else if (!IsUpperCase(term) && FirstIsUpperCase(term.charAt(0))) {
            //map contains liron or state
            if (termsAndFrequencyMap.containsKey(termToLowerCase)) {
                termsAndFrequencyMap.put(termToLowerCase, termsAndFrequencyMap.get(termToLowerCase) + 1);
            } else {   //map contains LIRON or STATE
                if (termsAndFrequencyMap.containsKey(termToUpperCase))
                    termsAndFrequencyMap.put(termToUpperCase, termsAndFrequencyMap.get(termToUpperCase) + 1);
                    //add Liron as LIRON
                else
                    termsAndFrequencyMap.put(termToUpperCase, 1);
            }
        }

        //liron or first
        else if (IsLowerCase(term)) {
            //case term is "first" and we already have "FIRST" on the map
            //save the frequency of "FIRST", remove it from map and add frequency + 1 to "first"
            if (termsAndFrequencyMap.containsKey(termToUpperCase)) {
                int frequencyOfUpperCase = termsAndFrequencyMap.get(termToUpperCase);
                termsAndFrequencyMap.remove(termToUpperCase);
                termsAndFrequencyMap.put(term, frequencyOfUpperCase + 1);
            }
            //we do not have "FIRST" in our map
            else {
                //we have "first" in our map
                if (termsAndFrequencyMap.containsKey(term))
                    termsAndFrequencyMap.put(term, termsAndFrequencyMap.get(term) + 1);
                    //adds "first" to the map
                else
                    termsAndFrequencyMap.put(term, 1);
            }

        } else {
            if (termsAndFrequencyMap.containsKey(term))
                termsAndFrequencyMap.put(term, termsAndFrequencyMap.get(term) + 1);
            else
                termsAndFrequencyMap.put(term, 1);
        }
    }

    //get the number that represents that month name
    //Get "MAY" or "May" or "may" and return "5"
    private static int GetMonthNumber(String monthName) {
        monthName = monthName.toUpperCase();
        if (monthName.equals("JAN"))
            monthName = "JANUARY";
        else if (monthName.equals("FEB"))
            monthName = "FEBRUARY";
        else if (monthName.equals("MAR"))
            monthName = "MARCH";
        else if (monthName.equals("APR"))
            monthName = "APRIL";
        else if (monthName.equals("MAY"))
            monthName = "MAY";
        else if (monthName.equals("JUN"))
            monthName = "JUNE";
        else if (monthName.equals("JUL"))
            monthName = "JULY";
        else if (monthName.equals("AUG"))
            monthName = "AUGUST";
        else if (monthName.equals("SEP"))
            monthName = "SEPTEMBER";
        else if (monthName.equals("OCT"))
            monthName = "OCTOBER";
        else if (monthName.equals("NOV"))
            monthName = "NOVEMBER";
        else if (monthName.equals("DEC"))
            monthName = "DECEMBER";
        return Month.valueOf(monthName.toUpperCase()).getValue();
    }

    //checks if a word is all UpperCases using ASCII
    public static boolean IsUpperCase(String term) {
        for (int i = 0; i < term.length(); i++) {
            char c = term.charAt(i);
            if (c == 45)
                continue;
            if (c == 36)
                continue;
            if (c < 65 || c > 90)
                return false;
        }
        return true;
    }

    //checks if the first char is uppercase
    public static boolean FirstIsUpperCase(char firstCharOfTerm) {
        if (firstCharOfTerm < 65 || firstCharOfTerm > 90)
            return false;
        return true;
    }

    //checks if a word is all LowerCases using ASCII
    public static boolean IsLowerCase(String term) {
        for (int i = 0; i < term.length(); i++) {
            char c = term.charAt(i);

            if (c == 45)
                continue;
            if (c == 36)
                continue;
            if (c < 97 || c > 122)
                return false;
        }
        return true;
    }

    //handle with doubles
    private static String HandleNumbersWithPoint(String beforePoint, String afterPoint) {
        if (beforePoint.length() >= 4 && beforePoint.length() <= 6)
            return beforePoint.substring(0, beforePoint.length() - 3) + '.'
                    + beforePoint.substring(beforePoint.length() - 3) + afterPoint + 'K';
        if (beforePoint.length() >= 7 && beforePoint.length() <= 9)
            return beforePoint.substring(0, beforePoint.length() - 6) + '.'
                    + beforePoint.substring(beforePoint.length() - 6) + afterPoint + 'M';
        if (beforePoint.length() >= 10 && beforePoint.length() <= 12)
            return beforePoint.substring(0, beforePoint.length() - 9) + '.'
                    + beforePoint.substring(beforePoint.length() - 9) + afterPoint + 'B';
        return null;

    }


    private String HandleRegularNumbers(String number) {
        if (number.length() == 4) {
            number = DeleteTheZeros(number);
            if (number.length() > 1)
                return number.charAt(0) + '.' + number.substring(1) + 'K';
            else
                return number.charAt(0) + "K";
        }
        if (number.length() == 5) {
            number = DeleteTheZeros(number);
            if (number.length() > 2)
                return number.substring(0, 2) + '.' + number.substring(2) + 'K';
            else
                return number.substring(0, 2) + 'K';
        }
        if (number.length() == 6) {
            number = DeleteTheZeros(number);
            if (number.length() > 3)
                return number.substring(0, 3) + '.' + number.substring(3) + 'K';
            else
                return number.substring(0, 3) + 'K';
        }
        if (number.length() == 7) {
            number = DeleteTheZeros(number);
            if (number.length() > 1)
                return number.charAt(0) + '.' + number.substring(1) + 'M';
            else
                return number.charAt(0) + "M";
        }
        if (number.length() == 8) {
            number = DeleteTheZeros(number);
            if (number.length() > 2)
                return number.substring(0, 2) + '.' + number.substring(2) + 'M';
            else
                return number.substring(0, 2) + 'M';
        }
        if (number.length() == 9) {
            number = DeleteTheZeros(number);
            if (number.length() > 3)
                return number.substring(0, 3) + '.' + number.substring(3) + 'M';
            else
                return number.substring(0, 3) + 'M';
        }
        if (number.length() == 10) {
            number = DeleteTheZeros(number);
            if (number.length() > 1)
                return number.charAt(0) + '.' + number.substring(1) + 'B';
            else
                return number.charAt(0) + "B";
        }
        if (number.length() == 11) {
            number = DeleteTheZeros(number);
            if (number.length() > 2)
                return number.substring(0, 2) + '.' + number.substring(2) + 'B';
            else
                return number.substring(0, 2) + 'B';
        }
        if (number.length() == 12) {
            number = DeleteTheZeros(number);
            if (number.length() > 3)
                return number.substring(0, 3) + '.' + number.substring(3) + 'B';
            else
                return number.substring(0, 3) + 'B';
        }

        return number;
    }


    //clean the zeros from numbers that their length is bigger than or equals to 4, and smaller than or equals 12
    private static String DeleteTheZeros(String number) {
        int length = number.length();
        if (length >= 4 && length <= 6) {
            for (int i = 0; i < 3; i++) {
                if (number.charAt(length - i - 1) == '0')
                    number = number.substring(0, length - i - 1);
                else
                    break;
            }
        }

        if (length >= 7 && length <= 9) {
            for (int i = 0; i < 6; i++) {
                if (number.charAt(length - i - 1) == '0')
                    number = number.substring(0, length - i - 1);
                else
                    break;
            }
        }

        if (length >= 10 && length <= 12) {
            for (int i = 0; i < 9; i++) {
                if (number.charAt(length - i - 1) == '0')
                    number = number.substring(0, length - i - 1);
                else
                    break;
            }
        }

        return number;
    }

    //can read regular numbers and also numbers with dots.
    private static boolean IsNumeric(String word) {
        try {
            Double.parseDouble(word);
        } catch (NumberFormatException nfe) {
            return false;
        }
        return true;
    }

    //checks if a term is in our stopwords list
    boolean IsStopWord(String word) {
        return stopWordsList.contains(word);
    }

    //read the stop words file into list named stopWordsList
    private HashSet<String> ReadStopWordToList(String stopWordsPath) throws IOException {
        Scanner s;
            s = new Scanner(new File(stopWordsPath));
        HashSet<String> stopWordsList = new HashSet<>();
        while (s.hasNext()) {
            stopWordsList.add(s.nextLine());
        }
        s.close();

        return stopWordsList;
    }

    private void InitMonthsNames() {
        monthsNames = new HashSet();
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

    private String CallStemmer(String term) {
        for (int i = 0; i < term.length(); i++) {
            stemmer.add(term.charAt(i));
        }
        stemmer.stem();
        return stemmer.toString();
    }

}//end of class