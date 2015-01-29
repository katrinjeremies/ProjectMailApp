/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.bht.fpa.mail.s769164.model.applicationLogic.imap;

import de.bht.fpa.mail.s769164.controller.MailController;
import de.bht.fpa.mail.s769164.model.applicationData.Account;
import de.bht.fpa.mail.s769164.model.applicationData.Email;
import de.bht.fpa.mail.s769164.model.applicationData.Folder;
import de.bht.fpa.mail.s769164.model.applicationLogic.EmailManagerIF;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Store;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.PropertyException;

/**
 *
 * @author Katrin Jeremies
 */
public class IMapEmailManager implements EmailManagerIF{
    
    private Account acc;
    
    public IMapEmailManager(Account acc){
        this.acc = acc;
    }

    @Override
    public void loadEmails(Folder f) {
        try{
            if(f.getEmails().isEmpty()){
                Store store = IMapConnectionHelper.connect(acc);
                if(store != null){
                    String path = f.getPath();
                    javax.mail.Folder imapFolder = store.getFolder(path);
                    if(imapFolder.exists()){
                        int imapType = imapFolder.getType();
                        if(imapType == 1 || imapType == 3){
                            imapFolder.open(javax.mail.Folder.READ_ONLY);
                            Message[] messages = imapFolder.getMessages();
                            for(Message message: messages){
                                Email mail = IMapEmailConverter.convertMessage(message);
                                f.addEmail(mail);
                            }
                            imapFolder.close(false);
                        }
                    }
                    store.close();
                }
            }
        }catch(MessagingException ex){
            Logger.getLogger(IMapEmailManager.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void saveEmails(File file, Folder markedFolder) {
        if(file != null){
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
                    String pathname = file.getPath() + "/mail" + (i+1) + ".xml";
                    m.marshal(mail, new File(pathname));
                }
            } catch (PropertyException ex) {
                Logger.getLogger(MailController.class.getName()).log(Level.SEVERE, null, ex);
            } catch (JAXBException ex) {
                Logger.getLogger(MailController.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

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
