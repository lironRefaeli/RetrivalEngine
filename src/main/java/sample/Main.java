package sample;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


    public class Main extends Application {


        @Override
        public void start(Stage primaryStage) throws Exception {

            FXMLLoader fxmlLoader = new FXMLLoader();
            Parent root = fxmlLoader.load(getClass().getResource("/mainWindow.fxml").openStream());
            primaryStage.setTitle("mainWindow");
            Scene scene = new Scene(root, 610, 320);
            scene.getStylesheets().add(getClass().getResource("/cssTemplate.css").toExternalForm());
            primaryStage.setScene(scene);
            primaryStage.show();


        }


        public static void main(final String[] args) throws IOException {

            launch(args);



            /*
            long startTime = System.nanoTime();
            int numOfLoops = 2; //number of loops in order to go through all the corpus' folders
            Map<String, TermDataInMap> termsCorpusMap = new HashMap<String, TermDataInMap>(); //a map that includes all the terms in the corpus
            Map<String, DocTermDataInMap> docsCorpusMap = new HashMap<String, DocTermDataInMap>();//a map that includes all the terms in the corpus

            //object of the class readFile - the input is the corpus location path
            //ReadFile readFile = new ReadFile("C:\\Users\\refaeli.liron\\Desktop\\corpus");
            ReadFile readFile = new ReadFile("C:\\Users\\david\\Desktop\\corpusTest");
            //object of the class readFile - the input is the stopWords documents location path
            //Parse parse = new Parse("C:\\Users\\refaeli.liron\\Desktop\\corpus\\StopWords.txt");
            Parse parse = new Parse("C:\\Users\\david\\Desktop\\corpus\\stop_words.txt");

            //the number of loop is determined by the numOfLoops parameter
            for (int i = 0; i < numOfLoops; i++) {
                System.out.println("start loop number: " + i + " time: " + (System.nanoTime() - startTime) / 1000000000.0);
                File file = new File("C:\\Users\\david\\Desktop\\RetrivalEngine\\src\\main\\java\\postingFiles\\posting" + i);
                //lists that will save documents content: texts, docsNumbers, cities.
                List<String> listOfTexts = readFile.ReadFolder(8);
                List<String> listOfDocsNumbers = readFile.getDocNumbersList();
                List<String> listOfDocsCities = readFile.getDocCitiesList();

                //loops over every text from one chunk
                for (int j = 0; j < listOfTexts.size(); j++) {

                    Map<String, String> postingMap = new HashMap<String, String>();// a map that includes posting data about the chunk of files
                    //for every text we will build temporaryMap in order to save all the terms and their frequency (tf) by Parse object
                    Map<String, Integer> temporaryMap = parse.ParsingDocument(listOfTexts.get(j));
                    //after parsing the text, we will creating new record in the docs Map
                    docsCorpusMap.put(listOfDocsNumbers.get(j), new DocTermDataInMap(returnMaxTf(temporaryMap), temporaryMap.size(), listOfDocsCities.get(j)));
                    //loops over one text's terms and merging temporaryMap to termsCorpusMap
                    for (String term : temporaryMap.keySet()) {
                        //NBA or GSW
                        if (Parse.IsUpperCase(term)) {
                            if (termsCorpusMap.containsKey(term)) {
                                //increasing frequency
                                termsCorpusMap.get(term).totalTf += temporaryMap.get(term);
                                termsCorpusMap.get(term).numOfDocuments++;
                            } else
                                //creating new record in termsCorpusMap
                                termsCorpusMap.put(term, new TermDataInMap(temporaryMap.get(term), 1));
                        }
                        //liron or first
                        else if (Parse.IsLowerCase(term)) {
                            //case term is "first" and we already have "FIRST" on the map
                            //save the frequency of "FIRST", remove it from map and add frequency + 1 to "first"
                            if (termsCorpusMap.containsKey(term.toUpperCase())) {
                                TermDataInMap upperCaseTerm = termsCorpusMap.get(term.toUpperCase());
                                termsCorpusMap.remove(term.toUpperCase());
                                termsCorpusMap.put(term, new TermDataInMap(upperCaseTerm.totalTf + temporaryMap.get(term), upperCaseTerm.numOfDocuments + 1));
                            }
                            //we do not have "FIRST" in our map
                            else {
                                //we have "first" in our map
                                if (termsCorpusMap.containsKey(term)) {
                                    termsCorpusMap.get(term).totalTf += temporaryMap.get(term);
                                    termsCorpusMap.get(term).numOfDocuments++;
                                }
                                //adds "first" to the map
                                else
                                    termsCorpusMap.put(term, new TermDataInMap(temporaryMap.get(term), 1));
                            }

                        }
                        //every other thing - like numbers or undefined characters
                        else {
                            if (termsCorpusMap.containsKey(term)) {
                                termsCorpusMap.get(term).totalTf += temporaryMap.get(term);
                                termsCorpusMap.get(term).numOfDocuments++;
                            } else
                                termsCorpusMap.put(term, new TermDataInMap(temporaryMap.get(term), 1));
                        }

                    }

                    //updating the posting map after finishing parsing a text
                    for (String term : temporaryMap.keySet()) {
                        //checking if posting map contains the term
                        if (!postingMap.containsKey(term)) {
                            //creating new record
                            postingMap.put(term, listOfDocsNumbers.get(j) + "~" + temporaryMap.get(term) + ",");
                        } else {
                            //deleting old record and creating new one
                            String postingOldData = postingMap.get(term);
                            postingMap.remove(term);
                            postingMap.put(term, postingOldData + listOfDocsNumbers.get(j) + "~" + temporaryMap.get(term) + ",");
                        }
                    }
                    //creating posting file and saving it in postingFilesFolder - his name is posting+the number of the loop
                    BufferedWriter writer = new BufferedWriter(new FileWriter(file));
                    //adding to the file all the term+posting data from posting map
                    for (String term : postingMap.keySet()) {
                        //the structure is - "term*docNum~tf,"
                        String data = term + "*" + postingMap.get(term);
                        writer.write(data);
                    }
                    //closing the file
                    writer.close();
                    //System.out.println("end creating posting " + (System.nanoTime()-startTime)/1000000000.0);
                    // System.out.println("end text number: " + j + " time: " + (System.nanoTime()-startTime)/1000000000.0);

                }
                System.out.println("end loop number: " + i + " time: " + (System.nanoTime() - startTime) / 1000000000.0);


            }

            System.out.println("I am done");
            */


        }

        static public synchronized int returnMaxTf(Map<String, Integer> temporaryMap) {
            int maxTf = -1;
            for (int value : temporaryMap.values()) {
                if (maxTf < value)
                    maxTf = value;
            }
            return maxTf;
        }
    }



