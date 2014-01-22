package PomWalker;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * 
 * @author Raula
 * This is to read and write files for scanning and other works
 */
public class ReadWriter {
	
    private String location;
	
    public ReadWriter(String target) {
	location = target;
    }
    
    public void clearFile(){
	try{
	    FileWriter f = new FileWriter(location, false);
	    f.close();
	} catch(IOException e){
	    e.printStackTrace();
	}
    }
    
    public int countLine(){
	int count = 0;
	try {
	    BufferedReader br = new BufferedReader(new FileReader(location));
	    String sCurrentLine;
	    while ((sCurrentLine = br.readLine()) != null) {
		count++;
	    }
	    br.close();
 	} catch (IOException e) {
	    e.printStackTrace();
	}
 	return count;
    }
	
    public String readAtLine(int line){
	String sb = "null";
	int countLine = 0;
	try {
	    BufferedReader br = new BufferedReader(new FileReader(location));
	    String sCurrentLine;
	    while ((sCurrentLine = br.readLine()) != null) {
		countLine++;
		if (countLine == line){
		    sb = sCurrentLine;
		    break;
		}
	    }
	    br.close();
 	} catch (IOException e) {
	    e.printStackTrace();
	}
	return sb;
    }

    public void write(String lineWrite){
	BufferedWriter writer = null;
        try {
            File outFile = new File(location);
	    System.out.print("Writing to: "+outFile.getCanonicalPath()+" Line: "+lineWrite);
            writer = new BufferedWriter(new FileWriter(outFile, true));
            writer.write(lineWrite);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                // Close the writer regardless of what happens...
                writer.close();
            } catch (Exception e) {
            }
        }
    }
	
    public void writeArti(String lineWrite, String artifact){
	BufferedWriter writer = null;
        try {
	    File outFile = new File(artifact+"-"+location);
            System.out.println("Writing to: "+outFile.getCanonicalPath()+" Line: "+lineWrite);

            writer = new BufferedWriter(new FileWriter(outFile, true));
            writer.write(lineWrite);
            writer.write("\n");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                // Close the writer regardless of what happens...
                writer.close();
            } catch (Exception e) {
            }
        }
    }

}
