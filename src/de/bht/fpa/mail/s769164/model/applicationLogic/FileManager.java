
/*
 * This class manages a hierarchy of folders and their content which is loaded 
 * from a given directory path.
 * 
 * @author Simone Strippgen
 */

package de.bht.fpa.mail.s769164.model.applicationLogic;

import de.bht.fpa.mail.s769164.model.applicationData.Folder;
import java.io.File;
import java.io.FileFilter;

public class FileManager implements FolderManagerIF{

    //top Folder of the managed hierarchy
    Folder topFolder;

    /**
     * Constructs a new FileManager object which manages a folder hierarchy, 
     * where file contains the path to the top directory. 
     * The contents of the  directory file are loaded into the top folder
     * @param file File which points to the top directory
     */
    public FileManager(File file) {
      topFolder = new Folder(file, true);
    }
    
    @Override
    public Folder getTopFolder() {
        return topFolder;
    }
    
    /**
     * Loads all relevant content in the directory path of a folder
     * object into the folder.
     * @param parent the folder into which the content of the corresponding 
     *          directory should be loaded
     */
    @Override
    public void loadContent(Folder parent) {
        if(parent.getComponents().isEmpty()){        
            File root = new File(parent.getPath());
            if (root.listFiles() != null){
                for(File child: root.listFiles()){
                    if(!child.isHidden() && child.isDirectory()){
                        FileFilter strainer = (File path) -> path.isDirectory();
                        
                        /**
                         *differentiation of Childfolder
                         */
                        if(child.listFiles(strainer) != null && child.listFiles(strainer).length == 0){
                            Folder folder = new Folder(child, false);
                            parent.addComponent(folder);
                        } else if(child.listFiles(strainer) != null) {
                            Folder folder = new Folder(child, true);
                            parent.addComponent(folder);
                        }
                    }
                }
            }
        }
    }
}
