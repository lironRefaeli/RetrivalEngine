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
/**
 * The class is extracting a specific corpus.
 * The input is the corpus' path.
 */
public class ReadFile {

    //String mainFolderPath;
    int nextFolderToReadIndex;
    List<String> filesPaths;

        ReadFile(String mainFolderPath)
        {
           // this.mainFolderPath = mainFolderPath;
            nextFolderToReadIndex = 0;
            filesPaths = new ArrayList<String>();
            final File folder = new File(mainFolderPath);
            listFilesForFolder(folder);
        }

        List<String> ReadFolder(int numOfFoldersToReadFrom) throws IOException {
            List<String> listOfTexts = new ArrayList<String>();
            for(int i = nextFolderToReadIndex; i < nextFolderToReadIndex + numOfFoldersToReadFrom; i++)
            {
                if(i < filesPaths.size())
                     TransferFilePathToFileContent(filesPaths.get(i), listOfTexts);
            }
         // System.out.println(listOfTexts.get(5429));
            nextFolderToReadIndex = nextFolderToReadIndex + numOfFoldersToReadFrom;
            return listOfTexts;
        }

        private void TransferFilePathToFileContent(String filePath, List<String> listOfTexts) throws IOException {
            File f = new File(filePath);
            Document document = Jsoup.parse(new String(Files.readAllBytes(f.toPath())));
            Elements elements = document.getElementsByTag("DOC");
            for(Element element : elements)
                listOfTexts.add(element.getElementsByTag("TEXT").text());

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
