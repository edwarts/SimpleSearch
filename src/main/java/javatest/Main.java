package javatest;

import java.io.*;
import java.util.*;
import java.util.concurrent.*;

/**
 * Main class
 */
public class Main {

    public static void main(String[] args) {

        File indexableDirectory=null;
        try
        {
            indexableDirectory=parametersPreSetup(args);
        }
        catch (Exception es)
        {
            System.out.println(es.fillInStackTrace());
            System.exit(0);
        }


        Scanner keyboard = new Scanner(System.in);
        while (true) {
            System.out.println("search>");
            final String searchKeyWords = keyboard.nextLine();
            if (searchKeyWords.equals(":quit")) {
                System.exit(0);
            } else {
                //process the data
                File[] listOfFiles = indexableDirectory.listFiles();
                ArrayList<String> result = new ArrayList<String>();

                //Set the size of the threadPool as the size of core count
                int threadPoolSize = Runtime.getRuntime().availableProcessors();


                //Declare the cached thread pool
                ExecutorService executorService = Executors.newCachedThreadPool();
                List<Future<String>> resultList = new ArrayList<Future<String>>();
                for (int i = 0; i < listOfFiles.length; i++) {
                    //We need to return the fileName and Score, so we can only use callable interface
                    Future<String> future = executorService.submit(new TextProcessWorkerWithResult(i, listOfFiles[i].getAbsolutePath(), searchKeyWords));
                    //Store the result to the result List
                    resultList.add(future);
                }

                //Check whether all the task had been done, iterate the resultList
                //This is reduce phase, it will hold the current thread to wait the Future return result one by one
                for (Future<String> fs : resultList) {
                    try {
                        while (!fs.isDone()) ;// If the future does not return the result, it will wait here
                        result.add(fs.get());//Put result in the result array
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    } catch (ExecutionException e) {
                        e.printStackTrace();
                    } finally {
                        //Shutdown the service
                        executorService.shutdown();
                    }
                }
                sortResult(result);
                for (String oneResult : result) {
                    System.out.println(buildUpResult(oneResult));
                }
            }
        }


    }

    /**
     * This function used to convert the numerical number to be with % symbol
     *
     * @param mixResult This is result to be buildt up
     * @return String The return is in format of filename:range score
     */
    public static String buildUpResult(String mixResult) {
        return mixResult.split(":")[0] + ':' + String.valueOf(Double.valueOf(mixResult.split(":")[1]) * 100) + "%";
    }

    /**
     * This function used to sort the result
     *
     * @param result This result is a list of string to be sorted based on its rank score field
     */
    public static void sortResult(List<String> result) {
        Collections.sort(result, new Comparator<String>() {
            public int compare(String o1, String o2) {
                //spilt the string to compare
                Double score1 = Double.valueOf(o1.split(":")[1]);
                Double score2 = Double.valueOf(o2.split(":")[1]);
                return score2.compareTo(score1);
            }
        });
    }
    /**
     * This function used to validate the input parameters
     *
     * @param parameter Parameter from the consoleline
     */

    public static File parametersPreSetup(String[] parameter)
    {
        if (parameter.length == 0) {
            throw new IllegalArgumentException("No directory given to index.");
        }
        File indexableDirectory = new File(parameter[0]);
        if (indexableDirectory.exists() == false) {
            throw new IllegalArgumentException("Directory given does not exist");
        }
        return indexableDirectory;
    }

}
/**
 * Worker class to process the text in individual thread
 */
class TextProcessWorkerWithResult implements Callable<String> {
    private int id;
    private String fileToBeProcess;
    private String wordsToBeQuery;

    public TextProcessWorkerWithResult(int id, String fileName, String query) {
        this.id = id;
        this.fileToBeProcess = fileName;
        this.wordsToBeQuery = query;
    }

    /**
     * This function process the file to extract the words and check the search input
     */
    public String call() throws Exception {
        System.out.println("Procee " + Thread.currentThread().getName() + " is now working on file" + fileToBeProcess);
        File fileTobeRead=new File(fileToBeProcess);
        HashSet<String> contentResult = readFile(fileTobeRead);
        String[] queryArray = wordsToBeQuery.split(" ");
        int totalSize = queryArray.length;
        double counter = 0d;
        for (String oneQueryWord : queryArray) {
            System.out.println(oneQueryWord);
            if (contentResult.contains(oneQueryWord)) {
                counter += 1.0;
            }
        }
        //The logic for ranking is, how much the words to be search can be found in the file
        //The function is ranking(counterMatched,totalSizeOfQuery)=counterMatched/totalSizeOfQuery
        double rank = counter / totalSize;

        return fileTobeRead.getName() + ":" + String.valueOf(rank);
    }

    /**
     * This function process the file to extract the words and check the search input
     * @param file file to be read
     * @return HashSet<String> which contans all the word in the file
     */
    public HashSet<String> readFile(File file) throws IOException {
        //Read from file
        HashSet<String> fileContent = new HashSet<String>();
        FileInputStream is = new FileInputStream(file);
        //Read the byte stream from the reader
        InputStreamReader isr = new InputStreamReader(is);
        //Use the BufferedReader
        BufferedReader br = new BufferedReader(isr);
        String line = null;
        while ((line = br.readLine()) != null) {
        //replace the stop word
            String[] lineData = line.replace("\'", "").replace(",", "").replace("\"", "")
                    .replace(".", "").replace("...", "").split(" ");
            for (String oneWord : lineData) {
                if (fileContent.contains(oneWord) == false) {
                    fileContent.add(oneWord);
                }
            }
        }
        br.close();
        return fileContent;

    }
}
