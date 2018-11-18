package sample;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import java.nio.file.Files;
import java.util.Queue;

import static java.lang.System.out;

/**
 * The class is extracting a specific corpus.
 * The input is the corpus' path.
 */
public class ReadFile {

    //String mainFolderPath;
    int nextFolderToReadIndex;
    List<String> filesPaths;
    private List<String> listOfDocsNumbers;
    private List<String> listOfDocsCities;

        ReadFile(String mainFolderPath)
        {
           // this.mainFolderPath = mainFolderPath;
            nextFolderToReadIndex = 0;
            filesPaths = new ArrayList<String>();
            final File folder = new File(mainFolderPath);
            listOfDocsNumbers = new ArrayList<String>();
            listOfDocsCities = new ArrayList<String>();
            listFilesForFolder(folder);
        }

        List<String> ReadFolder(int numOfFoldersToReadFrom) throws IOException {
            List<String> listOfTexts = new ArrayList<String>();

            for(int i = nextFolderToReadIndex; i < nextFolderToReadIndex + numOfFoldersToReadFrom; i++)
            {
                if(i < filesPaths.size())
                     TransferFilePathToFileContent(filesPaths.get(i), listOfTexts, listOfDocsNumbers);
            }

            nextFolderToReadIndex = nextFolderToReadIndex + numOfFoldersToReadFrom;
            return listOfTexts;
        }

        private void TransferFilePathToFileContent(String filePath, List<String> listOfTexts,List<String>  listOfDocsNumbers) throws IOException {
            File f = new File(filePath);
            Document document = Jsoup.parse(new String(Files.readAllBytes(f.toPath())));
            Elements elements = document.getElementsByTag("DOC");
            for(Element element : elements) {
                listOfTexts.add(element.getElementsByTag("TEXT").text());
                listOfDocsNumbers.add(element.getElementsByTag("DOCNO").text());
                String city = element.getElementsByTag("F").toString();

                if(!city.equals("")) {
                    city = city.substring(city.indexOf("<f p=\"104\">", city.indexOf("</f>")));
                    if (city.length() > 15) {
                        city = city.substring(city.indexOf("\n "), city.indexOf(" \n"));
                        city = city.replaceAll("\n", "");
                        String cityline[] = city.split(" ");
                        city = cityline[2].toUpperCase();

                    }
                }

                listOfDocsCities.add(city);
            }



        }

        public List<String> getDocNumbersList()
        {
            return listOfDocsNumbers;
        }

        public List<String> getDocCitiesList()
        {
            return listOfDocsCities;
        }

        private void listFilesForFolder(File folder)
        {
                for (final File fileEntry : folder.listFiles()) {
                    if (fileEntry.isDirectory())
                        listFilesForFolder(fileEntry);
                    else
                        filesPaths.add(fileEntry.getPath());
                }
        }


}
