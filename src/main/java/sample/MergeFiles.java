package sample;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
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
    private Scanner fileReader1;
    private Scanner fileReader2;
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
        lineIndex = 0;
    }

    //One of the main function in this class
    //responsible for merging two temporary posting files and create one sorted lexicographic file
    //Mulitple threads are executing this function all combined
    public File margeTwoFiles(File file1, File file2) throws IOException
    {
        fileReader1 = new Scanner(file1);
        fileReader2 = new Scanner(file2);
        System.out.println(file1.getName() + "+" + file2.getName());
        File outFile;

        //only allow one thread to get access to creating new file and increasing the counterNameOfFile variable
        synchronized (lock)
        {
            outFile = new File(pathToDisk + "\\mergedFile" + counterNameOfFile);
            counterNameOfFile++;
        }

        FileWriter fw = new FileWriter(outFile);
        bw = new BufferedWriter(fw);

        //both files still have new lines to read
        while (fileReader1.hasNextLine() && fileReader2.hasNextLine())
        {
            compareBetweenTwoTerms();
            bw.newLine();
        }

        //only file2 has more lines to read
        if (!fileReader1.hasNextLine() && fileReader2.hasNextLine())
        {
            //boolean text1Flag is here in case there is still a word in curString1
            while (!text1Flag && fileReader2.hasNextLine())
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
            while (fileReader2.hasNextLine())
            {
                curString2 = fileReader2.nextLine();
                bw.write(curString2);
                bw.newLine();
            }
            //curString1 was the biggest word lexicographic in both files
            if (!text1Flag)
                bw.write(curString1);
        }

        //only file1 has more lines to read
        else if (fileReader1.hasNextLine() && !fileReader2.hasNextLine())
        {
            //boolean text2Flag is here in case there is still a word in curString2
            while (!text2Flag && fileReader1.hasNextLine())
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
            while (fileReader1.hasNextLine())
            {
                curString1 = fileReader1.nextLine();
                bw.write(curString1);
                bw.newLine();
            }

            //curString1 was the biggest word lexicographic in both files
            if (!text2Flag)
                bw.write(curString2);
        }

        //delete the two posting files and close the readers and the writer
        bw.close();
        fileReader1.close();
        fileReader2.close();
        file1.delete();
        file2.delete();
        System.out.println(outFile.getName() + " was complete");
        return outFile;
    }


    //This function write all the symbols from the last two posting files to the symbols file
    //At the end it calls to "WriteTheLettersPostingFiles" function that write all the other words
    //The symbols are coming first in every temp posting file, and that is why this function comes before the letters function
    public void margeTwoLastFilesAndCreatePermanentPostingFiles(File file1, File file2) throws IOException {
        //read the two last temp posting files
        fileReader1 = new Scanner(file1);
        fileReader2 = new Scanner(file2);
        String nextLineInFile1;
        char firstCharOfLine1;
        String nextLineInFile2;
        char firstLineOfLine2;

        //create ths Symbols file
        File outFile = new File(pathToDisk + "\\Symbols");
        FileWriter fw = new FileWriter(outFile);
        bw = new BufferedWriter(fw);

        String termName;
        int firstLetterOfBothTerms = 97;

        //both of the posting files still have new lines to read
        while (fileReader1.hasNextLine() && fileReader2.hasNextLine())
        {
            nextLineInFile1 = fileReader1.nextLine();
            firstCharOfLine1 = nextLineInFile1.charAt(0);
            nextLineInFile2 = fileReader2.nextLine();
            firstLineOfLine2 = nextLineInFile2.charAt(0);

            //both files have changed letters, so create new file with that new letter name
            if (firstCharOfLine1 == firstLetterOfBothTerms && firstLineOfLine2 == firstLetterOfBothTerms)
            {
                outFile = new File(pathToDisk + "\\" + (char)firstLetterOfBothTerms);
                fw = new FileWriter(outFile);
                bw = new BufferedWriter(fw);
                firstLetterOfBothTerms++;
            }
            compareBetweenTwoTerms();
            bw.newLine();
            if(text1Flag)
            {
                termName = SplitString(nextLineInFile1)[0];
                Indexer.termsCorpusMap.get(termName).pointerToPostingLine = lineIndex;
                Indexer.termsCorpusMap.get(termName).idf = Math.log10(Indexer.NumberOfDocsInCorpus/
                        (Indexer.termsCorpusMap.get(termName).numOfDocuments));
                lineIndex++;
            }
            else
            {
                termName = SplitString(nextLineInFile2)[0];
                Indexer.termsCorpusMap.get(termName).pointerToPostingLine = lineIndex;
                Indexer.termsCorpusMap.get(termName).idf = Math.log10(Indexer.NumberOfDocsInCorpus/
                        (Indexer.termsCorpusMap.get(termName).numOfDocuments));
                lineIndex++;
            }
        }

        //only file2 has more lines to read
        //it is not possible that file2 has words that start with a letter that file1 doesn't have
        //so no need of creating new file here
        while (!text1Flag && fileReader2.hasNextLine())
        {
            nextLineInFile2 = fileReader2.nextLine();
            bw.write(nextLineInFile2);
            bw.newLine();
            termName = SplitString(nextLineInFile2)[0];
            Indexer.termsCorpusMap.get(termName).pointerToPostingLine = lineIndex;
            Indexer.termsCorpusMap.get(termName).idf = Math.log10(Indexer.NumberOfDocsInCorpus/
                    (Indexer.termsCorpusMap.get(termName).numOfDocuments));
            lineIndex++;
        }

        //only file1 has more lines to read
        //it is not possible that file1 has words that start with a letter that file2 doesn't have
        //so no need of creating new file here
        while (!text2Flag && fileReader1.hasNextLine())
        {
            nextLineInFile1 = fileReader1.nextLine();
            bw.write(nextLineInFile1);
            bw.newLine();
            termName = SplitString(nextLineInFile1)[0];
            Indexer.termsCorpusMap.get(termName).pointerToPostingLine = lineIndex;
            Indexer.termsCorpusMap.get(termName).idf = Math.log10(Indexer.NumberOfDocsInCorpus/
                    (Indexer.termsCorpusMap.get(termName).numOfDocuments));
            lineIndex++;
        }
        //delete the two posting files and close the readers and the writer
        file1.delete();
        file2.delete();
        fileReader1.close();
        fileReader2.close();
        bw.close();
    }

    //curString is containing the term and all the doc's names that have this term
    //we want to spilt and take only the term's name for comparison
    private void extractStrings ()
    {
        if (text1Flag && fileReader1.hasNextLine())
        {
            curString1 = fileReader1.nextLine();
            splitedCurString1 = curString1.split("\\*");

        }
        if (text2Flag && fileReader2.hasNextLine())
        {
            curString2 = fileReader2.nextLine();
            splitedCurString2 = curString2.split("\\*");
        }
    }

    //lexicographic comparator between two terms, each from a different file
    private void compareBetweenTwoTerms () throws IOException {

        extractStrings();
        int compareToResualt = splitedCurString1[0].compareTo(splitedCurString2[0]);

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
            String newString = splitedCurString1[0] + "*" + splitedCurString1[1] + splitedCurString2[1];
            bw.write(newString);
            text1Flag = true;
            text2Flag = true;
        }
       /*
        //todo to think about it
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
        try {
            if (indexer.queueOfTempPostingFiles.size() >= 2) {
                final File firstFile = indexer.queueOfTempPostingFiles.poll();
                final File secondFile = indexer.queueOfTempPostingFiles.poll();
                indexer.queueOfTempPostingFiles.add(margeTwoFiles(firstFile, secondFile));

            }
        }
        catch (IOException e) { e.printStackTrace(); }

    }
    }


    /*
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

*/
