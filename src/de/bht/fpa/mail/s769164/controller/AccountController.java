/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.bht.fpa.mail.s769164.controller;

import de.bht.fpa.mail.s769164.model.applicationData.Account;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

/**
 *
 * @author Katrin Jeremies
 */

public class AccountController implements Initializable{
    
    @FXML
    private TextField name;
    
    @FXML
    private TextField host;
    
    @FXML
    private TextField username;
    
    @FXML
    private TextField password;
    
    @FXML
    private Label information;
    
    @FXML
    private Button cancel;
    
    @FXML
    private Button save;
    
    private final MailController mailControll;
    
    
    public AccountController(MailController mailControll){
        this.mailControll = mailControll;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        cancel.addEventHandler((ActionEvent.ACTION), e -> closeWindow());
        save.addEventHandler((ActionEvent.ACTION), e -> save());
    }
    
    public void closeWindow(){
        Stage stage = (Stage) information.getScene().getWindow();
        stage.close();
    }
    
    public void save(){
        String nameTxt = name.getText();
        String hostTxt = host.getText();
        String usernameTxt = username.getText();
        String passwordTxt = password.getText();
        
        boolean isEmpty = nameTxt.isEmpty() || hostTxt.isEmpty() || usernameTxt.isEmpty() || passwordTxt.isEmpty();
        if(isEmpty){
            information.setText("All textfields must contain data!");
        }else{
            Account account = new Account(nameTxt, hostTxt, usernameTxt, passwordTxt);
            System.out.println(nameTxt);
            if(mailControll.saveAccount(account)){
                closeWindow();
            }else{
                information.setText("Choose another name!");
            }
            
        }
    }
}   

