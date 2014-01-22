package PomWalker;
import java.io.File;

//This class is used to extract the POM files and locations within
public class FileScan{
	
    private String indexPath;

    public FileScan(String index){
	indexPath = index;
    }
    
    public void walkPomsAndJars(String path) {
        ReadWriter wr = new ReadWriter(indexPath);
    
    	File root = new File(path);
        File[] list = root.listFiles();

        if (list == null) return;

	for ( File f : list ) {
            if ( f.isDirectory() ) {
                walkPomsAndJars( f.getAbsolutePath() );
            } else {
		StringBuilder paths = new StringBuilder();
		String lookFile = f.getAbsoluteFile().toString();
		String dirPath = lookFile.substring(0, lookFile.lastIndexOf(File.separator));

		if (getExtension(lookFile).equalsIgnoreCase("pom")){
		    File pomF = new File(lookFile);
		    File currDir = new File(dirPath);
		    File[] neighbors = currDir.listFiles();
		    paths.append(pomF.getAbsoluteFile());

		    for( File f2 : neighbors){
			String checkFile = f2.getAbsoluteFile().toString();
			if(getExtension(checkFile).equalsIgnoreCase("jar")){
			    paths.append(","+f2.getAbsolutePath());
			}
		    }
		    wr.write(paths.toString()+"\n");
		}
            }
        }
    }

    protected String getExtension(String name) {
        String[] str = name.split("\\.");
        if(str.length > 1) {
            return str[str.length - 1];
        }
        return ""; 
    }
}
