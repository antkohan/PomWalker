package PomWalker;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import org.apache.maven.model.Dependency;


public class MainWalker {
	
	public static void main(String[] args) {
	
		//scanlog refers to the scanned list of repositories
		
		//Environment Variables
		final String scanlog = "index";
		final String repo = "/Users/antkohan/Desktop/testMaven";
		//final String repo = "/opt/maven/";
		//final String repo = "/opt/dmg/pom/";
		//final String repo = "C:\\\\Users\\Raula\\Documents\\maven";
		final String outF = "output.txt";
		
		ReadWriter errlog = new ReadWriter("log.txt");
		errlog.clearFile();

		MainWalker pomWalk = new MainWalker();
		ReadWriter rw = new ReadWriter(scanlog);
		ReadWriter outFile = new ReadWriter(outF);
		rw.clearFile();
		outFile.clearFile();
		pomWalk.scan(rw,scanlog,repo);
		pomWalk.extractPom(rw,outFile,"arti","arVer");
		
    }

	//this is to run the program
	private void scan(ReadWriter rw, String scanPath, String repPath) {

	    //STEP1 - extract all the POM files from the local repositories
	    System.out.println("\n***** SCANNING FOR POM FILES *****");
	    FileScan fw = new FileScan(scanPath);
	    fw.walk(repPath);
	    System.out.println("***** INDEXED "+rw.countLine()+" FILES ******\n");
       }
	
	private void extractPom(ReadWriter rw, ReadWriter outFile, String art, String artID) {
	System.out.println("***** EXTRACTING DATA FROM POMS *****");
        int totalPoms= rw.countLine();
	//STEP2 - read each content and get all the dependencies
        PomParser pp = new PomParser();
        for (int i = 1; i < totalPoms+1; i++) {
        	String data = pp.walkParse(rw.readAtLine(i));
		if(data != ""){
		    outFile.write(data);
		    System.out.println("");    
		}
		
	}
        
        System.out.println("***** WALKED POM FILES: "+totalPoms+" *****");
    	System.out.println("***** INDEPENDANT ARTIFACTS: "+pp.skip+" *****");
    	System.out.println("***** DEPENDANT ARTIFACTS: "+pp.anaPOM+" *****");
	
	}
}
