/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.bht.fpa.mail.s769164.model.applicationLogic.imap;

import de.bht.fpa.mail.s769164.model.applicationData.Account;
import de.bht.fpa.mail.s769164.model.applicationData.Component;
import de.bht.fpa.mail.s769164.model.applicationData.Folder;
import de.bht.fpa.mail.s769164.model.applicationLogic.FolderManagerIF;
import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.mail.MessagingException;
import javax.mail.Store;

/**
 *
 * @author Katrin Jeremies
 */
public class IMapFolderManager implements FolderManagerIF {
    
    private Account acc;
    private Folder top;
    
    public IMapFolderManager(Account acc){
        this.acc = acc;
        try{
            Store store = IMapConnectionHelper.connect(acc);
            String name = acc.getName();
            if(store == null){
                top = new Folder(new File(name), false);
            }else{
                javax.mail.Folder imapFolder = store.getDefaultFolder();
                top = new Folder(new File(name), true);
                top.setPath(imapFolder.getFullName());
                store.close();
            }
        }catch(MessagingException ex){
            Logger.getLogger(IMapFolderManager.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public Folder getTopFolder() {
        return top;
    }

    @Override
    public void loadContent(Folder f) {
        try{
            if(f.getComponents().isEmpty()){ 
                Store store = IMapConnectionHelper.connect(acc);
                if(store != null){
                    String path = f.getPath();
                    javax.mail.Folder imapFolder = store.getFolder(path);
                    if(imapFolder.exists()){
                        
                        for(javax.mail.Folder sub : imapFolder.list()){
                            String subName = sub.getFullName();
                            if(sub.list().length == 0){
                                Folder folder = new Folder(new File(subName), false);
                                folder.setPath(subName);
                                f.addComponent(folder);
                            } else {
                                Folder folder = new Folder(new File(subName), true);
                                folder.setPath(subName);
                                
                                if(sub.getName().equals("[Gmail]")){
                                    loadContent(folder);
                                    for(Component child : folder.getComponents()){
                                        f.addComponent(child);
                                    }
                                } else {
                                    f.addComponent(folder);
                                }
                            }
                        }
                    }
                    store.close();
                }
            }
        }catch(MessagingException ex){
             Logger.getLogger(IMapFolderManager.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
