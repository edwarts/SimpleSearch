package javatest;

import junit.framework.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by boma on 15/11/2016.
 * Unit Test class
 */
public class SimpleSearchTest {
    TextProcessWorkerWithResult tpw;

    @Test
    public void readFileTest()
    {
        System.out.println("Test read file function");
        tpw=new TextProcessWorkerWithResult(1,"resources/data/readFileTestFile","");
        try {
            Assert.assertEquals(10,tpw.readFile(new File("src/test/resources/data/readFileTestFile")).size());
        } catch (IOException e) {
            e.printStackTrace();
        }


    }
    @Test
    /*
    *Test the threadCall function using given test file in resources
    * */
    public void threadCallTest()
    {
        tpw=new TextProcessWorkerWithResult(1,"resources/data/file1.txt","prison officers");
        try {
            Assert.assertEquals("file1.txt:1.0",tpw.call());
        } catch (Exception e) {
            e.printStackTrace();
        }
        tpw=new TextProcessWorkerWithResult(1,"resources/data/file1.txt","");
        try {
            Assert.assertEquals("file1.txt:0.0",tpw.call());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    /*Tes sorting algorithms
    * */
    @Test
    public void sortingTest()
    {
        ArrayList<String> resultToBeSort=new ArrayList<String>();
        resultToBeSort.add("file2.txt:0.0");
        resultToBeSort.add("file1.txt:1.0");
        Main.sortResult(resultToBeSort);
        Assert.assertEquals("file1.txt:1.0",resultToBeSort.get(0));
    }
    @Test
    public void parameterValidationTest()
    {
        String[] parameter={"resources/data/file1.txt"};
        Assert.assertTrue(Main.parametersPreSetup(parameter) instanceof File);
    }
    @Test
    public void resultBuiltTest()
    {
        Assert.assertEquals("file1.txt:100.0%",Main.buildUpResult("file1.txt:1.0"));
    }



}
