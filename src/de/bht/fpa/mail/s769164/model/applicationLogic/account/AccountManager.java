/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.bht.fpa.mail.s769164.model.applicationLogic.account;

import de.bht.fpa.mail.s769164.model.applicationData.Account;
import java.util.List;

/**
 *
 * @author Katrin Jeremies
 */
public class AccountManager implements AccountManagerIF{
    
    private AccountDAOIF dao;
    
    public AccountManager() {
        this.dao = new AccountDBDAO();
    }

    @Override
    public Account getAccount(String name) {
        List<Account> accList = dao.getAllAccounts();
        for(Account acc : accList){
            String accName = acc.getName();
            if(accName.equals(name)){
                return acc;
            }
        }
        return null;
    }

    @Override
    public List<Account> getAllAccounts() {
        List<Account> list = dao.getAllAccounts();
        return list;
    }

    @Override
    public boolean saveAccount(Account newAcc) {
        String newName = newAcc.getName();
        Account oldAcc = getAccount(newName);
        if(oldAcc != null){
            return false;
        }else{
            dao.saveAccount(newAcc);
            return true;
        }
    }

    @Override
    public boolean updateAccount(Account account) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
}
