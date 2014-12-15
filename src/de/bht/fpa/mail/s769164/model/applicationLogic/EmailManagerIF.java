package de.bht.fpa.mail.s769164.model.applicationLogic;

import de.bht.fpa.mail.s769164.model.applicationData.Email;
import de.bht.fpa.mail.s769164.model.applicationData.Folder;
import java.io.File;
import java.util.List;

/**
 * @author Katrin Jeremies
 */
public interface EmailManagerIF {
    
    void loadEmails(Folder f); 
    
    void saveEmails(File file);
    
    List<Email> search(String pattern);
    
}
