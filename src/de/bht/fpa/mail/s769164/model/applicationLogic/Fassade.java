package de.bht.fpa.mail.s769164.model.applicationLogic;

import de.bht.fpa.mail.s769164.model.applicationLogic.xml.FileManager;
import de.bht.fpa.mail.s769164.model.applicationLogic.xml.XMLEmailManager;
import de.bht.fpa.mail.s769164.model.applicationData.Account;
import de.bht.fpa.mail.s769164.model.applicationData.Email;
import de.bht.fpa.mail.s769164.model.applicationData.Folder;
import de.bht.fpa.mail.s769164.model.applicationLogic.account.AccountManager;
import de.bht.fpa.mail.s769164.model.applicationLogic.account.AccountManagerIF;
import de.bht.fpa.mail.s769164.model.applicationLogic.imap.IMapEmailManager;
import de.bht.fpa.mail.s769164.model.applicationLogic.imap.IMapFolderManager;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Katrin Jeremies
 */
public class Fassade implements ApplicationLogicIF{
    
    private FolderManagerIF fileManager;
    private EmailManagerIF xmlEmailManager;
    private AccountManagerIF accManager;
    private Folder markedFolder;
    
    public Fassade(File directory){
        fileManager = new FileManager(directory);
        xmlEmailManager = new XMLEmailManager();
        accManager = new AccountManager();
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
        return xmlEmailManager.search(pattern, markedFolder);
    }

    @Override
    public void loadEmails(Folder folder) {
        xmlEmailManager.loadEmails(folder);
        markedFolder = folder;
    }

    @Override
    public void changeDirectory(File file) {
        fileManager = new FileManager(file);
        xmlEmailManager = new XMLEmailManager();
    }

    @Override
    public void saveEmails(File file) {
        xmlEmailManager.saveEmails(file, markedFolder);
    }

    @Override
    public void openAccount(String name) {
        Account acc = accManager.getAccount(name);
        if(acc != null){
            fileManager = new IMapFolderManager(acc);
            xmlEmailManager = new IMapEmailManager(acc);
        }
    }

    @Override
    public List<String> getAllAccounts() {
        List<String> list = new ArrayList();
        List<Account> accounts = accManager.getAllAccounts();
        for(Account acc: accounts){
            String name = acc.getName();
            list.add(name);
        }
        return list;  
    }

    @Override
    public Account getAccount(String name) {
        return accManager.getAccount(name);
    }

    @Override
    public boolean saveAccount(Account account) {
        return accManager.saveAccount(account);
    }

    @Override
    public void updateAccount(Account account) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
