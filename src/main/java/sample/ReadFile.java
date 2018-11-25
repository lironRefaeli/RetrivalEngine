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
 * The class is extracting documents from a specific corpus.
 * The input is the corpus' path.
 */
public class ReadFile {


        int nextFolderToReadIndex; //pointer to the next folder to extract it's content
        private List<String> filesPaths; //list of all the file's paths in the given folder
        private List<String> listOfDocsNumbers; //list of all the documents' numbers in the files
        private List<String> listOfDocsCities; //list of all the documents' cities in the files
        private List<String> listOfTexts; //list of all the documents' texts in the files

        /**
         *
         * @param mainFolderPath
         */
        ReadFile(String mainFolderPath)
        {
            nextFolderToReadIndex = 0;
            filesPaths = new ArrayList<String>();
            //creating an object File from the input path, in order to read the content of the given folder
            final File folder = new File(mainFolderPath);
            //sending the File object to a function that will create a list of all the file's paths in the given folder
            listFilesForFolder(folder);
        }

        /**
         * the function is extracting all the file's paths in the given folder and
         * saving the paths in the filesPaths list.
         * @param folder
         */
        private void listFilesForFolder(File folder)
        {
            //checking all the files in the given folder
            for (final File fileEntry : folder.listFiles()) {
                //if there's a folder in the specific file
                if (fileEntry.isDirectory())
                    //we are recursively checking if there are files in it.
                    listFilesForFolder(fileEntry);
                else
                    //adding path of file to the list
                    filesPaths.add(fileEntry.getPath());
            }
        }

        /** this function we will play from the main.
         *
         * @param numOfFoldersToReadFrom //the size of chunk of folders. it's important to extract a chunk of folders to ensure better running time.
         * @return list of al the texts that were in the given folders.
         * @throws IOException
         */
        public  List<String> ReadFolder(int numOfFoldersToReadFrom) throws IOException {

            //for new chunk we are restart the lists
            listOfTexts = new ArrayList<String>();
            listOfDocsNumbers = new ArrayList<String>();
            listOfDocsCities = new ArrayList<String>();

            //reading the texts in a folder from the chunk folders.
            for(int i = nextFolderToReadIndex; i < nextFolderToReadIndex + numOfFoldersToReadFrom; i++)
            {
                if(i < filesPaths.size())
                     TransferFilePathToFileContent(filesPaths.get(i));
            }

            //we are working on separate chunk each time. when we're finishing with chunk, we continue to the next chunk by using nextFolderToReadIndex pointer.
            nextFolderToReadIndex = nextFolderToReadIndex + numOfFoldersToReadFrom;
            return listOfTexts;
        }

        /**
         * this function is getting path of file and extracting the content of the file to the appropriate lists.
         * @param filePath
         * @throws IOException
         */
        private void TransferFilePathToFileContent(String filePath) throws IOException {
                File f = new File(filePath);
                Document document = Jsoup.parse(new String(Files.readAllBytes(f.toPath())));
                //first, we will separate all the documents in a file.
                Elements elements = document.getElementsByTag("DOC");
                for(Element element : elements) {

                    //adding all the documents' texts in the file to the listOfTexts
                    listOfTexts.add(element.getElementsByTag("TEXT").text());

                    //adding all the docsNumbers in the file to the listOfDocsNumbers
                    listOfDocsNumbers.add(element.getElementsByTag("DOCNO").text());

                    //adding all the documents' cities in the file to the listOfCities
                    String city = element.getElementsByTag("F").toString();
                    // if there isn't an information about the city, we will add empty string to the list.
                    if(!city.equals("") && city.contains("<f p=\"104\">")) {
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


        /**
         *
          * @return listOfDocsNumbers
         */
        public List<String> getDocNumbersList()
        {
            return listOfDocsNumbers;
        }


        /**
         *
         * @return listOfDocsCities
         */
        public List<String> getDocCitiesList()
        {
            return listOfDocsCities;
        }




}
