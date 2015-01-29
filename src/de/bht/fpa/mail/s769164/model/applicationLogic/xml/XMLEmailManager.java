package de.bht.fpa.mail.s769164.model.applicationLogic.xml;

import de.bht.fpa.mail.s769164.controller.MailController;
import de.bht.fpa.mail.s769164.model.applicationData.Email;
import de.bht.fpa.mail.s769164.model.applicationData.Folder;
import de.bht.fpa.mail.s769164.model.applicationLogic.EmailManagerIF;
import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.bind.DataBindingException;
import javax.xml.bind.JAXB;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.PropertyException;

/**
 * @author Katrin Jeremies
 * load Emails to Folder
 */
public class XMLEmailManager implements EmailManagerIF {
        
    /**
     * load Emails to given folder
     * @param folder 
     */
    @Override
    public void loadEmails(Folder folder) {
        if(folder.getEmails().isEmpty()){
            File file = new File(folder.getPath());
            FileFilter strainer = (File pathname) -> {
                String path = pathname.getPath();
                return path.endsWith(".xml");
            };
            for(File child : file.listFiles(strainer)){
                try{
                    Email eMail = JAXB.unmarshal(child, Email.class);
                    folder.addEmail(eMail);
                }catch(DataBindingException e){ 
                    System.out.println("A non-email xml-file appeared!");
                }
            }
        }
    }
    
    /**
     * save Emails from markedFolder to the given parameter directory
     * @param directory 
     * @param markedFolder
     */
    @Override
    public void saveEmails(File directory, Folder markedFolder) {
        if(directory != null){
            try {
                /*
                transform email to xml-file
                */
                JAXBContext con = JAXBContext.newInstance(Email.class);
                Marshaller m = con.createMarshaller(); 
                m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
                List<Email> list = markedFolder.getEmails();
                for(int i = 0; i < list.size(); i++){
                    Email mail = list.get(i);
                    /*
                    set the path of the saved emails, and the counter i+1
                    to make sure that every mail gets a new name
                    */
                    String pathname = directory.getPath() + "/mail" + (i+1) + ".xml";
                    m.marshal(mail, new File(pathname));
                }
            } catch (PropertyException ex) {
                Logger.getLogger(MailController.class.getName()).log(Level.SEVERE, null, ex);
            } catch (JAXBException ex) {
                Logger.getLogger(MailController.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    
    /**
     * search the given query in the TextField in Email-objects
     * @param pattern
     * @param markedFolder
     * @return 
     */
    @Override
    public List<Email> search(String pattern, Folder markedFolder) {
        List<Email> emailList = new ArrayList();
        for(Email email: markedFolder.getEmails()){
            if(email.getSubject().contains(pattern)){
                emailList.add(email);
            }else if(email.getText().contains(pattern)){
                emailList.add(email);
            }else if(email.getReceived().contains(pattern)){
                emailList.add(email);
            }else if(email.getSent().contains(pattern)){
                emailList.add(email);
            }else if(email.getReceiver().contains(pattern)){
                emailList.add(email);
            }else if(email.getSender().contains(pattern)){
                emailList.add(email);
            }
        }
        return emailList;
    }
}
