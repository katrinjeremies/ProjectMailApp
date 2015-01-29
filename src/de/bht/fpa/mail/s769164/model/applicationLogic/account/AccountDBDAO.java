/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.bht.fpa.mail.s769164.model.applicationLogic.account;

import de.bht.fpa.mail.s769164.model.applicationData.Account;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import javax.persistence.Query;

/**
 *
 * @author Katrin Jeremies
 */
public class AccountDBDAO implements AccountDAOIF{
    
    EntityManagerFactory factory;
    
    public AccountDBDAO(){
        factory = Persistence.createEntityManagerFactory("fpa");
        TestDBDataProvider.createAccounts();
    }

    @Override
    public List<Account> getAllAccounts() {
        EntityManager manager = factory.createEntityManager();
        Query query = manager.createQuery("SELECT accounts FROM Account accounts ");
        List<Account> list = query.getResultList();
        manager.close();
        return list;
    }

    @Override
    public Account saveAccount(Account acc) {
        EntityManager manager = factory.createEntityManager();
        EntityTransaction trans = manager.getTransaction();
        trans.begin();
        manager.persist(acc);
        trans.commit();    
        manager.close();
        return acc;    }

    @Override
    public boolean updateAccount(Account acc) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
}
