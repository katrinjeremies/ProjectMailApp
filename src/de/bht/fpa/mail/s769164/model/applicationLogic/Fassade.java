package de.bht.fpa.mail.s769164.model.applicationLogic;

import de.bht.fpa.mail.s769164.model.applicationData.Email;
import de.bht.fpa.mail.s769164.model.applicationData.Folder;
import java.io.File;
import java.util.List;

/**
 *
 * @author Katrin Jeremies
 */
public class Fassade implements ApplicationLogicIF{
    
    private FolderManagerIF fileManager;
    private final EmailManagerIF xmlEmailManager;
    
    public Fassade(File directory){
        fileManager = new FileManager(directory);
        xmlEmailManager = new XMLEmailManager();
    }

    @Override
    public Folder getTopFolder() {
        return fileManager.getTopFolder();
    }

    @Override
    public void loadContent(Folder folder) {
        fileManager.loadContent(folder);
    }

    @Override
    public List<Email> search(String pattern) {
        return xmlEmailManager.search(pattern);
    }

    @Override
    public void loadEmails(Folder folder) {
        xmlEmailManager.loadEmails(folder);
    }

    @Override
    public void changeDirectory(File file) {
        fileManager = new FileManager(file);
    }

    @Override
    public void saveEmails(File file) {
        xmlEmailManager.saveEmails(file);
    }
}
