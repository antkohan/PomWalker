package PomWalker;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import org.apache.maven.model.Dependency;


public class MainWalker {
	
    public static void main(String[] args) {
	
	final String index = "index";
	final String repo = "/Users/antkohan/Desktop/testMaven";
	final String outF = "output.txt";
		
	ReadWriter errlog = new ReadWriter("log.txt");
	errlog.clearFile();

	MainWalker pomWalk = new MainWalker();
	ReadWriter rw = new ReadWriter(index);
	ReadWriter outFile = new ReadWriter(outF);
	rw.clearFile();
	outFile.clearFile();
	pomWalk.scan(rw, index, repo);
	pomWalk.extractPom(rw, outFile, "arti", "arVer");
		
    }

    private void scan(ReadWriter rw, String index, String repPath) {

	System.out.println("\n***** SCANNING FOR POM FILES *****");
	FileScan fw = new FileScan(index);
	fw.walkPomsAndJars(repPath);
	System.out.println("***** INDEXED "+rw.countLine()+" FILES ******\n");
    }
	
    private void extractPom(ReadWriter rw, ReadWriter outFile, String art, String artID) {
	System.out.println("***** EXTRACTING DATA FROM POMS *****");
        int totalPoms= rw.countLine();

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
