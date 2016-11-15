***Simple Search***

This is quite simple version of Search Engine for searching files.

The best sultion is a spark job to do this but the question only required a simple one.

Future and ThreadPool had been used to process the task.

The data scruture in memery is HashSet to hold all of words extracted from text file.

In search of keywords, we assumed that only match the keywords without matching the sequences. The matching of sequence 

involved NLP technologies which I do not think is for a technical test.

**To run this**

Please run mvn package to build the jar

Please run java -jar target/simplesearch-1.0-SNAPSHOT-jar-with-dependencies.jar yourfilesPath to run

Please run mvn test to run unit test