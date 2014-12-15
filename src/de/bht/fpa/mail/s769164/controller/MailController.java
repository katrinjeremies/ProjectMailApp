package de.bht.fpa.mail.s769164.controller;

import de.bht.fpa.mail.s769164.model.applicationData.Component;
import de.bht.fpa.mail.s769164.model.applicationData.Email;
import de.bht.fpa.mail.s769164.model.applicationData.Folder;
import de.bht.fpa.mail.s769164.model.applicationLogic.ApplicationLogicIF;
import de.bht.fpa.mail.s769164.model.applicationLogic.Fassade;
import java.io.File;
import java.net.URL;
import java.text.DateFormat;
import java.text.ParseException;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeItem.TreeModificationEvent;
import javafx.scene.control.TreeView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.DirectoryChooser;

/**
 * @author Katrin Jeremies
 */

public class MailController implements Initializable {
        
    @FXML
    private TreeView tree;
    
    @FXML
    private MenuBar menu;
    
    @FXML
    private TableView table;
    
    @FXML
    private Label senderID;
    
    @FXML
    private Label subjectID;
    
    @FXML
    private Label receivedID;
    
    @FXML
    private Label receiverID;
    
    @FXML
    private TableColumn receivedColumn;
    
    @FXML
    private TextArea textAreaID;
    
    @FXML 
    private Label counter;
    
    @FXML
    private TextField search;
    
    private ApplicationLogicIF appliLog;
    
    private boolean checkListener;
    
    private final ObservableList<Email> tableContent;
    
    
    public MailController(){
        String path = System.getProperty("user.home");     //set user-folder as default-tree
        File root = new File(path);
        appliLog = new Fassade(root);
        checkListener = true;    
        tableContent = FXCollections.observableArrayList();
    }
    
    
    /**
     * starts configureMenue and createTree
     * @param url
     * @param rb 
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        configureMenue();
        createTree();
        createTable();
        configureSearch();
    }    
    
    /**
     * eventhandling of menue
     */
    public void configureMenue(){    
        EventHandler handler = (EventHandler<ActionEvent>) (ActionEvent event) -> {
            MenuItem menuItem =(MenuItem) event.getSource();
            String context = menuItem.getText();
            DirectoryChooser chooser = new DirectoryChooser();
            File directory = chooser.showDialog(null);
            if(context.equals("Open")){
                if(directory != null){
                    appliLog.changeDirectory(directory);
                    createTree();
                }
            }else if(context.equals("Save")){
                appliLog.saveEmails(directory);
            }
        };
        for(Menu m:menu.getMenus()){
            for(MenuItem i:m.getItems()){
                i.setOnAction(handler);
            }
        }
    }
    
    /**
     * create tree
     */
    public void createTree(){
        Folder userFolder = appliLog.getTopFolder();
        appliLog.loadContent(userFolder);
        
        /*
        show selected start-file with children
        */
        TreeItem<Component> root = new TreeItem<>(userFolder);
        tree.setRoot(root); 
        root.setExpanded(true);
        String path = "/de/bht/fpa/mail/s769164/view/home.png";     //start-file-image
        Image image = new Image(getClass().getResourceAsStream(path));
        ImageView view = new ImageView(image);
        root.setGraphic(view);
        showComponents(root);
        
        /*
        eventhandling for open the directory
        */
        root.addEventHandler(TreeItem.<Component>branchExpandedEvent(), (TreeModificationEvent<Component> event) -> {
            TreeItem<Component> item = event.getTreeItem();
            item.getChildren().clear();     //clear Expandable-dummy of showComponents-methode
            Folder parent = (Folder)item.getValue();
            appliLog.loadContent(parent);
            showComponents(item);
        }); 
        
        /*
        create Changelistener for TreeView and load Emails to selected folder
        */
        if(checkListener){
            tree.getSelectionModel().selectedItemProperty().addListener(
            (ChangeListener<TreeItem>) (ObservableValue<? extends TreeItem> observable, TreeItem oldValue, TreeItem newValue) -> {
                /*
                "clean" all Labels and TextAreas and the TableView 
                */
                tableContent.clear();
                counter.setText("( )");
                senderID.setText("");
                subjectID.setText("");
                receivedID.setText("");
                receiverID.setText("");
                textAreaID.setText("");
                /*
                if there is a TreeItem selected ...
                */
                if(newValue != null){
                    /*
                    read out the mails of the current folder, ...
                    */
                    Folder folder = (Folder)newValue.getValue();
                    appliLog.loadEmails(folder);
                    printEmails (folder);
                    /*
                    check how much mails there are and ...
                    */
                    File file = new File(folder.getPath());
                    int size = folder.getEmails().size();
                    /*
                    marke-up the mail-number to the folder.
                    */
                    String newName = file.getName() + " (" + size + ")";
                    folder.setName(newName);
                    /*
                    The home-folder of the default-tree get a mail-number immediately.
                    */
                    tree.setShowRoot(false);
                    tree.setShowRoot(true);
                    
                    /*
                    table will sort by date descending
                    */
                    table.getSortOrder().add(receivedColumn);     //sort by date ...
                    receivedColumn.setSortType(TableColumn.SortType.DESCENDING);     //descending
                    /*
                    calling dateSorter to sort the date-numeral
                    */
                    Comparator comp = (Comparator<String>)(String a, String b) -> {
                            return dateSorter(a, b);
                    };
                    receivedColumn.setComparator(comp);
                }
            });
            checkListener = false;     //add ChangeListener only one time
        }
    }
    
    /**
     * Email-news on console
     * @param folder 
     */
    public void printEmails(Folder folder){
        System.out.println("Selected directory: " + folder.getPath());     //folder-path on console
        List<Email> list = folder.getEmails();
        System.out.println(list.size());     //number on console
        for(Email email: list){
            System.out.println(email);
            tableContent.add(email);
        }
        table.setItems(tableContent);
    }
    
    /**
     * create TreeItems for TreeView and assign to parent-TreeItem
     * @param parent 
     */
    public void showComponents(TreeItem<Component> parent){
        Component topFolder = (Folder) parent.getValue();
        if(parent.getChildren().isEmpty()){
            for(Component child: topFolder.getComponents()){
                TreeItem<Component> itemChild = new TreeItem<>(child);
                itemChild.setExpanded(false); 
                                
                /*
                files get icon
                */
                String folderPath = "/de/bht/fpa/mail/s769164/view/folder.png";
                Image image = new Image(getClass().getResourceAsStream(folderPath));
                ImageView view = new ImageView(image);
                itemChild.setGraphic(view);     
                
                /*
                Expandable-dummy with Content to unfold
                */
                parent.getChildren().add(itemChild);
                if(child.isExpandable()){
                    itemChild.getChildren().add(null);
                }
            }
        }
    }
    
    /**
     *create table to list mails and assign informations to TableColumn
     */
    public void createTable() {
        table.setPlaceholder(new Label(""));     //empty placeholder for preview
        for(TableColumn column: (ObservableList<TableColumn>) table.getColumns()){     //search in all columns ...
            String tableName = column.getText();     //and get the name of the table-column
            /*
            identify table-column
            assign contents of mail-attributes
            address to cell
            */
            if(tableName.equals("Importance")){
                PropertyValueFactory propertyFactory = new PropertyValueFactory("importance");
                column.setCellValueFactory(propertyFactory);  
            } else if(tableName.equals("Received")){
                PropertyValueFactory propertyFactory = new PropertyValueFactory("received");
                column.setCellValueFactory(propertyFactory);
            } else if(tableName.equals("Read")){
                PropertyValueFactory propertyFactory = new PropertyValueFactory("read");
                column.setCellValueFactory(propertyFactory);
            } else if(tableName.equals("Sender")){
                PropertyValueFactory propertyFactory = new PropertyValueFactory("sender");
                column.setCellValueFactory(propertyFactory);
            } else if(tableName.equals("Recipients")){
                PropertyValueFactory propertyFactory = new PropertyValueFactory("receiverTo");
                column.setCellValueFactory(propertyFactory);
            } else if(tableName.equals("Subject")){
                PropertyValueFactory propertyFactory = new PropertyValueFactory("subject");
                column.setCellValueFactory(propertyFactory);
            }
        }
        /*
        
        add Listener to the list to check if change/selection appears in the TableView and then call the writeEmailData-methode
        */
        ObservableList<Email> list = (ObservableList<Email>)table.getSelectionModel().getSelectedItems();
        list.addListener((ListChangeListener<Email>) fu -> writeEmailData());
    }
    
    /**
     * transfer contents of mail-attributes to the bootom right container
     */
    public void writeEmailData(){
        Email mail = (Email)table.getSelectionModel().getSelectedItem();
        if(mail != null){
            senderID.setText(mail.getSender());
            subjectID.setText(mail.getSubject());
            receivedID.setText(mail.getReceived());
            receiverID.setText(mail.getReceiver());
            textAreaID.setText(mail.getText());
        }
    }
    
    /**
     * sort date-column by date-numeral
     * @param a
     * @param b
     * @return 
     */
    public int dateSorter(String a, String b){
        try {
            DateFormat form = DateFormat.getDateTimeInstance(DateFormat.FULL, DateFormat.SHORT, Locale.GERMANY);
            Date date1 = form.parse(a);
            Date date2 = form.parse(b);
            Long time1 = date1.getTime();
            Long time2 = date2.getTime();
            return Long.compare(time1, time2);
        } catch (ParseException ex) {
            Logger.getLogger(MailController.class.getName()).log(Level.SEVERE, null, ex);
        }
        return -1; 
    }
    
    /**
     * configure search box and call handleKeyTypedEvent-methode if key was typed
     */
    public void configureSearch(){
        search.setOnKeyTyped((Event event) -> {
            handleKeyTypedEvent(event);
        });
    }
    
    /**
     * handle search query of the search box 
     * @param e 
     */
    public void handleKeyTypedEvent(Event e){
        String text = ((TextField)e.getSource()).getText();
        tableContent.clear();
        List<Email> list = appliLog.search(text);
        tableContent.addAll(list);     //set results to table
        counter.setText("(" + tableContent.size() + ")");     //set result-number next to search box
    }
}