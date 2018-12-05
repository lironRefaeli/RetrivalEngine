package sample;
import java.io.*;
import java.util.Scanner;


//This class goal is to take care of merging two temporary posting files
//Implements runnable in order to do so with threads for saving some runtime
public class MergeFiles implements Runnable {

    private static int counterNameOfFile = 1;
    private boolean text1Flag = true;
    private boolean text2Flag = true;
    private String curString1 = "";
    private String curString2 = "";
    private String[] splitedCurString1;
    private String[] splitedCurString2;
    private BufferedReader fileReader1;
    private BufferedReader fileReader2;
    private String pathToDisk;
    private BufferedWriter bw;
    private Indexer indexer;
    private int lineIndex;
    private static Object lock = new Object();

    //Class constructor
    public MergeFiles(String pathToDisk, Indexer indexer)
    {
        this.pathToDisk = pathToDisk;
        this.indexer = indexer;
        splitedCurString1 = new String[2];
        splitedCurString2 = new String[2];
        lineIndex = 1;
    }

    //One of the main function in this class
    //responsible for merging two temporary posting files and create one sorted lexicographic file
    //Mulitple threads are executing this function all combined
    public String margeTwoFiles(String firstFilePath, String secondFilePath) throws IOException
    {
        File file1 = new File(firstFilePath);
        FileReader firstReader = new FileReader(file1);
        fileReader1 = new BufferedReader(firstReader);
        File file2 = new File(secondFilePath);
        FileReader secondReader = new FileReader(file2);
        fileReader2 = new BufferedReader(secondReader);
        System.out.println(file1.getName() + "+" + file2.getName());
        File outFile;

        //only allow one thread to get access to creating new file and increasing the counterNameOfFile variable
        synchronized (lock)
        {
            outFile = new File(Indexer.postingFilesPath + "\\mergedFile" + counterNameOfFile+".txt");
            counterNameOfFile++;
        }

        FileWriter fw = new FileWriter(outFile);

        bw = new BufferedWriter(fw,262144);

        //both files still have new lines to read
        while (fileReader1.ready() && fileReader2.ready())
        {
            compareBetweenTwoTerms();
            bw.newLine();
        }

        //only file2 has more lines to read
        if ((!fileReader1.ready() && fileReader2.ready()))
        {
            //boolean text1Flag is here in case there is still a word in curString1
            while (!text1Flag && fileReader2.ready())
            {
                compareBetweenTwoTerms();
                bw.newLine();
            }
            //we have already read a word from file2, so we want to write it before we are overriding it
            if(!text2Flag)
            {
                text2Flag = true;
                bw.write(curString2);
                bw.newLine();
            }
            //in case we wrote curString1 and still have words in file2
            while (fileReader2.ready())
            {
                curString2 = fileReader2.readLine();
                bw.write(curString2);
                bw.newLine();
            }
            //curString1 was the biggest word lexicographic in both files
            if (!text1Flag)
                bw.write(curString1);
        }

        //only file1 has more lines to read
        else if (fileReader1.ready() && !fileReader2.ready())
        {
            //boolean text2Flag is here in case there is still a word in curString2
            while (!text2Flag && fileReader1.ready())
            {
                compareBetweenTwoTerms();
                bw.newLine();
            }
            //we have already read a word from file1, so we want to write it before we are overriding it
            if(!text1Flag)
            {
                text1Flag = true;
                bw.write(curString2);
                bw.newLine();
            }
            //in case we wrote curString1 and still have words in file2
            while (fileReader1.ready())
            {
                curString1 = fileReader1.readLine();
                bw.write(curString1);
                bw.newLine();
            }

            //curString1 was the biggest word lexicographic in both files
            if (!text2Flag)
                bw.write(curString2);
        }

        //delete the two posting files and close the readers and the writer
        bw.close();
        fw.close();
        fileReader1.close();
        fileReader2.close();
        file1.delete();
        file2.delete();
        System.out.println(outFile.getName() + " was complete");
        return outFile.getPath();
    }


    //This function write all the symbols from the last two posting files to the symbols file
    //At the end it calls to "WriteTheLettersPostingFiles" function that write all the other words
    //The symbols are coming first in every temp posting file, and that is why this function comes before the letters function
    public void margeTwoLastFilesAndCreatePermanentPostingFiles(String firstFilePath, String secondFilePath) throws IOException {
        //read the two last temp posting files
        File file1 = new File(firstFilePath);
        FileReader firstReader = new FileReader(file1);
        fileReader1 =new BufferedReader(firstReader);
        File file2= new File(secondFilePath);
        FileReader secondReader = new FileReader(file2);
        fileReader2 = new BufferedReader(secondReader);
        char firstCharOfLine1;
        char firstLineOfLine2;

        //create ths Symbols file
        File outFile = new File(Indexer.postingFilesPath + "\\Symbols.txt");
        FileWriter fw = new FileWriter(outFile);
        bw = new BufferedWriter(fw,262144);

        String termName;
        String termNameLower;
        int firstLetterOfBothTerms = 97;

        //both of the posting files still have new lines to read
        while (fileReader1.ready() && fileReader2.ready())
        {
            extractStrings();
            //nextLineInFile1 = fileReader1.nextLine();
            firstCharOfLine1 = curString1.toLowerCase().charAt(0);
            //nextLineInFile2 = fileReader2.nextLine();
            firstLineOfLine2 = curString2.toLowerCase().charAt(0);
            //both files have changed letters, so create new file with that new letter name
            if (firstCharOfLine1 == (char)firstLetterOfBothTerms && firstLineOfLine2 == (char)firstLetterOfBothTerms)
            {
                lineIndex = 1;
                bw.close();
                fw.close();
                outFile = new File(Indexer.postingFilesPath + "\\" + (char)firstLetterOfBothTerms +".txt");
                fw = new FileWriter(outFile);
                bw = new BufferedWriter(fw, 262144);
                firstLetterOfBothTerms++;
            }
            compareBetweenTwoTermsWithoutExtract();
            bw.newLine();
            if(text1Flag)
            {
                termName = splitedCurString1[0];
                termNameLower = termName.toLowerCase();
                try
                {
                    Indexer.termsCorpusMap.get(termName).pointerToPostingLine = lineIndex;
                    Indexer.termsCorpusMap.get(termName).idf = Math.log10(Indexer.NumberOfDocsInCorpus /
                            (Indexer.termsCorpusMap.get(termName).numOfDocuments));
                }
                catch (NullPointerException e)
                {
                    Indexer.termsCorpusMap.get(termNameLower).pointerToPostingLine = lineIndex;
                    Indexer.termsCorpusMap.get(termNameLower).idf = Math.log10(Indexer.NumberOfDocsInCorpus /
                            (Indexer.termsCorpusMap.get(termNameLower).numOfDocuments));
                }

                lineIndex++;
            }
            else
            {
                termName = splitedCurString2[0];
                termNameLower = termName.toLowerCase();
                try
                {
                    Indexer.termsCorpusMap.get(termName).pointerToPostingLine = lineIndex;
                    Indexer.termsCorpusMap.get(termName).idf = Math.log10(Indexer.NumberOfDocsInCorpus /
                            (Indexer.termsCorpusMap.get(termName).numOfDocuments));
                }
                catch (NullPointerException e)
                {
                    Indexer.termsCorpusMap.get(termNameLower).pointerToPostingLine = lineIndex;
                    Indexer.termsCorpusMap.get(termNameLower).idf = Math.log10(Indexer.NumberOfDocsInCorpus /
                            (Indexer.termsCorpusMap.get(termNameLower).numOfDocuments));
                }
                lineIndex++;
            }
        }

        //only file2 has more lines to read
        //it is not possible that file2 has words that start with a letter that file1 doesn't have
        //so no need of creating new file here
        while (!text1Flag && fileReader2.ready())
        {
            extractStrings();
            bw.write(curString2);
            bw.newLine();
            termName = splitedCurString2[0];
            termNameLower = termName.toLowerCase();
            try{
                Indexer.termsCorpusMap.get(termName).pointerToPostingLine = lineIndex;
                Indexer.termsCorpusMap.get(termName).idf = Math.log10(Indexer.NumberOfDocsInCorpus /
                        (Indexer.termsCorpusMap.get(termName).numOfDocuments));
            }
            catch(NullPointerException e)
            {
                Indexer.termsCorpusMap.get(termNameLower).pointerToPostingLine = lineIndex;
                Indexer.termsCorpusMap.get(termNameLower).idf = Math.log10(Indexer.NumberOfDocsInCorpus /
                        (Indexer.termsCorpusMap.get(termNameLower).numOfDocuments));
            }
            lineIndex++;

        }

        //only file1 has more lines to read
        //it is not possible that file1 has words that start with a letter that file2 doesn't have
        //so no need of creating new file here
        while (!text2Flag && fileReader1.ready())
        {
            extractStrings();
            bw.write(curString1);
            bw.newLine();
            termName = splitedCurString1[0];
            termNameLower = termName.toLowerCase();
            try{
                Indexer.termsCorpusMap.get(termName).pointerToPostingLine = lineIndex;
                Indexer.termsCorpusMap.get(termName).idf = Math.log10(Indexer.NumberOfDocsInCorpus /
                        (Indexer.termsCorpusMap.get(termName).numOfDocuments));
            }
            catch(NullPointerException e)
            {
                Indexer.termsCorpusMap.get(termNameLower).pointerToPostingLine = lineIndex;
                Indexer.termsCorpusMap.get(termNameLower).idf = Math.log10(Indexer.NumberOfDocsInCorpus /
                        (Indexer.termsCorpusMap.get(termNameLower).numOfDocuments));
            }
            lineIndex++;

        }
        //delete the two posting files and close the readers and the writer
        fileReader1.close();
        fileReader2.close();
        file1.delete();
        file2.delete();
        bw.close();
    }

    //curString is containing the term and all the doc's names that have this term
    //we want to spilt and take only the term's name for comparison
    private void extractStrings ()
    {
        if (text1Flag)
        {
            try {
                curString1 = fileReader1.readLine();
            } catch (IOException e) {
                e.printStackTrace();
            }
            splitedCurString1 = curString1.split("\\*");

        }
        if (text2Flag)
        {
            try {
                curString2 =fileReader2.readLine();
            } catch (IOException e) {
                e.printStackTrace();
            }
            splitedCurString2 = curString2.split("\\*");
        }
    }

    //lexicographic comparator between two terms, each from a different file
    private void compareBetweenTwoTerms () throws IOException {

        extractStrings();
        String term1 = splitedCurString1[0].toLowerCase();
        String term2 = splitedCurString2[0].toLowerCase();
        int compareToResualt = term1.compareTo(term2);

        if (compareToResualt < 0) {
            bw.write(curString1);
            text1Flag = true;
            text2Flag = false;
        } else if (compareToResualt > 0) {
            bw.write(curString2);
            text1Flag = false;
            text2Flag = true;
        }
        //curString1 == curString2
        else {
            String newString = "";
            if(IsLowerCase(splitedCurString1[0]) || IsLowerCase(splitedCurString2[0]))
                newString = term1 + "*" + splitedCurString1[1] + splitedCurString2[1];
            else
                newString = splitedCurString1[0] + "*" + splitedCurString1[1] + splitedCurString2[1];
            bw.write(newString);
            text1Flag = true;
            text2Flag = true;
        }
       /*

        else if(splitedCurString1.length > 1 && splitedCurString2.length > 1)
            newString = splitedCurString1[0] + "*" + splitedCurString1[1] + splitedCurString2[1];
       */
    }

    //lexicographic comparator between two terms, each from a different file
    private void compareBetweenTwoTermsWithoutExtract() throws IOException {

        String term1 = splitedCurString1[0].toLowerCase();
        String term2 = splitedCurString2[0].toLowerCase();
        int result = term1.compareTo(term2);

        if (result < 0) {
            bw.write(curString1);
            text1Flag = true;
            text2Flag = false;
        } else if (result > 0) {
            bw.write(curString2);
            text1Flag = false;
            text2Flag = true;
        }
        //curString1 == curString2
        else {
            String newString = "";
            if(IsLowerCase(splitedCurString1[0]) || IsLowerCase(splitedCurString2[0]))
                newString = term1 + "*" + splitedCurString1[1] + splitedCurString2[1];
            else
                newString = splitedCurString1[0] + "*" + splitedCurString1[1] + splitedCurString2[1];
            bw.write(newString);
            text1Flag = true;
            text2Flag = true;
        }
       /*

        else if(splitedCurString1.length > 1 && splitedCurString2.length > 1)
            newString = splitedCurString1[0] + "*" + splitedCurString1[1] + splitedCurString2[1];
       */
    }

    //Splitting the string it gets by the * char
    //implemented this function because we preferred doing it ourselves in order to save some runtime
    public String[] SplitString (String stringToSplit)
    {
        String[] splittedString = new String[2];
        for (int i = 0; i < stringToSplit.length(); i++) {
            if (stringToSplit.charAt(i) == '*')
                splittedString[0] = stringToSplit.substring(0, i);
            splittedString[1] = stringToSplit.substring(i + 1);
        }
        return splittedString;
    }

    //Override the run() method of Runnable class
    //extract two temp posting files from the concurrent queue and merge them together in "margeTwoFiles" function
    public void run ()
    {
        try
        {
            if (indexer.queueOfTempPostingFiles.size() >= 2)
            {
                String firstFilePath = indexer.queueOfTempPostingFiles.poll();
                String secondFilePath = indexer.queueOfTempPostingFiles.poll();
                indexer.queueOfTempPostingFiles.add(margeTwoFiles(firstFilePath, secondFilePath));

            }
        }
        catch (IOException e) { e.printStackTrace(); }

    }



    //checks if a word is all UpperCases using ASCII
    public boolean IsUpperCase(String term)
    {

        for(int i = 0; i < term.length(); i++)
        {
            char c = term.charAt(i);

            if(c == 45)
                continue;
            if(c == 36)
                continue;
            if(c < 65 || c > 90)
                return false;
        }
        return true;
    }


    //checks if a word is all LowerCases using ASCII
    public boolean IsLowerCase(String term)
    {
        for(int i = 0; i < term.length(); i++)
        {
            char c = term.charAt(i);

            if(c == 45)
                continue;
            if(c == 36)
                continue;

            if(c < 97 || c > 122)
                return false;
        }
        return true;
    }

}


