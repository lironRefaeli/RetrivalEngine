package sample;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

public class MergeFiles implements Runnable {

    static int counterNameOfFile = 1;
    boolean text1Flag = true;
    boolean text2Flag = true;
    String curString1 = "";
    String curString2 = "";
    String[] splitedCurString1 = new String[2];
    String[] splitedCurString2 = new String[2];
    Scanner fileReader1;
    Scanner fileReader2;
    String pathToDisk;
    BufferedWriter bw;
    Indexer indexer;
    static Object lock = new Object();

    public MergeFiles(String pathToDisk, Indexer indexer)
    {

        this.pathToDisk = pathToDisk;
        this.indexer = indexer;
    }

    public File margeTwoFiles(File file1, File file2) throws IOException {
        fileReader1 = new Scanner(file1);
        fileReader2 = new Scanner(file2);
        System.out.println(file1.getName()+"+"+file2.getName());

        File outFile;
        synchronized (lock) {
            outFile = new File(pathToDisk + "\\mergedFile" + counterNameOfFile);
            counterNameOfFile++;
        }


        FileWriter fw = new FileWriter(outFile);
        bw = new BufferedWriter(fw);

        while (fileReader1.hasNextLine() && fileReader2.hasNextLine()) {
            compareBetweenTwoTerms();
            bw.newLine();
        }

        //fileReader1 was finished
        if(!fileReader1.hasNextLine() && fileReader2.hasNextLine())
        {
            while(!text1Flag && fileReader2.hasNextLine()) {
                compareBetweenTwoTerms();
                bw.newLine();
            }

            while(fileReader2.hasNextLine() || !text2Flag)
            {
                text2Flag = true;
                if(fileReader2.hasNextLine())
                    curString2 = fileReader2.nextLine();
                bw.write(curString2);
                bw.newLine();
            }

            if(!text1Flag)
                bw.write(curString1);
        }
        //fileReader2 was finished
        else if(!fileReader2.hasNextLine() && fileReader1.hasNextLine())
        {
            while(!text2Flag && fileReader1.hasNextLine()) {
                compareBetweenTwoTerms();
                bw.newLine();
            }

            while (fileReader1.hasNextLine() || !text1Flag )
            {
                text1Flag = true;
                if(fileReader1.hasNextLine())
                    curString1 = fileReader1.nextLine();
                bw.write(curString1);
                bw.newLine();
            }

            if(!text2Flag)
                bw.write(curString2);
        }


        bw.close();
        fileReader1.close();
        fileReader2.close();
        file1.delete();
        file2.delete();
        System.out.println(outFile.getName()+" was complete");
        return outFile;
    }


    private void extractStrings()
    {
        if (text1Flag && fileReader1.hasNextLine()) {
            curString1 = fileReader1.nextLine();
            splitedCurString1 = curString1.split("\\*");
        }
        if (text2Flag && fileReader2.hasNextLine()) {
            curString2 = fileReader2.nextLine();
            splitedCurString2 = curString2.split("\\*");
        }
    }

    private void compareBetweenTwoTerms() throws IOException {

        extractStrings();
        if (splitedCurString1[0].toLowerCase().compareTo(splitedCurString2[0].toLowerCase()) < 0) {
            bw.write(curString1);
            text1Flag = true;
            text2Flag = false;

        } else if (splitedCurString1[0].toLowerCase().compareTo(splitedCurString2[0].toLowerCase()) > 0) {
            bw.write(curString2);
            text1Flag = false;
            text2Flag = true;
        }
        //curString1 == curString2
        else{
            String newString = "";
            if((IsUpperCase(splitedCurString1[0]) && IsLowerCase(splitedCurString2[0])) || (IsUpperCase(splitedCurString2[0]) && IsLowerCase(splitedCurString1[0])))
                newString = splitedCurString1[0].toLowerCase() + "*" + splitedCurString1[1] + splitedCurString2[1];
            //todo to think about it
            else if(splitedCurString1.length > 1 && splitedCurString2.length > 1)
                newString = splitedCurString1[0] + "*" + splitedCurString1[1] + splitedCurString2[1];

            bw.write(newString);
            text1Flag = true;
            text2Flag = true;


        }
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


    public void run() {

        try {
            if(indexer.queueOfTempPostingFiles.size() >= 2) {
                final File firstFile = indexer.queueOfTempPostingFiles.poll();
                final File secondFile = indexer.queueOfTempPostingFiles.poll();
                indexer.queueOfTempPostingFiles.add(margeTwoFiles(firstFile,secondFile));

            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}