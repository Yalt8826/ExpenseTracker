package com.example;

import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

import javafx.animation.FadeTransition;
import javafx.animation.RotateTransition;
import javafx.animation.TranslateTransition;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.PieChart;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.Stage;
import javafx.util.Duration;



public class Controller {


    private Stage stage;
    private Scene scene;
    private Parent root;


    @FXML
    private AnchorPane rootNode;

    @FXML
    private Rectangle clip;

    @FXML
    private Text welcomeUserText;

    SessionFactory sessionFactory = new Configuration().configure().buildSessionFactory();



    //~~~~~~~~~~~~INITAILIZE~~~~~~~~~~~~~~~~~~


    
    @FXML
    public void initialize() {
        enterTranshistTF();
        enterInTranshistTF();
        enterSavTranshistTF();
    }

    int i = 0;



    //~~~~~~~~~~~~~~~~~THREADS~~~~~~~~~~~~~~~~~~~



    class setCurrUserThread extends Thread {
        public void run() {
            SessionManager.setCurrUser(user);
        }
    }


    class setExpTransactionListThread extends Thread {
        public void run() {
            SessionManager.setExpTransactionList();
        }
    }

    class setInTransactionListThread extends Thread {
        public void run() {
            SessionManager.setInTransactionList();
        }
    }

    class setTransactionListThread extends Thread {
        public void run() {
            SessionManager.setTransactionList();
        }
    }

    class setSavingsTransactionListThread extends Thread {
        public void run() {
            SessionManager.setSavingsTransactionList();
        }
    }

    class setTotalOverviewThreadThread extends Thread {
        public void run() {
            SessionManager.setTotalOverview();
        }
    }



    //TITLE BAR BUTTONS
    


    @FXML
    private void closeWindow(MouseEvent event) {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow(); 
        stage.close();
}

    @FXML
    private void minimizeWindow(MouseEvent event) {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow(); 
        stage.setIconified(true);
    } 

    @FXML
    private void maximizeWindow(MouseEvent event) {
    }



    //~~~~~~~~~~~~~~~~~~~~~~~~~LOGIN SCENE~~~~~~~~~~~~~~~~~~~~~~~~~~~~~



    @FXML
    private Text invalidLogin;

    @FXML
    private TextField lognamein;

    @FXML
    private TextField logpassin;

    @FXML
    void switchRegisterScene(MouseEvent event) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/com/example/RegisterScene.fxml")); 
            stage = (Stage)((Node) event.getSource()).getScene().getWindow(); 
            scene = new Scene(root); 
            scene.setFill(Color.TRANSPARENT);
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    UserObj user = new UserObj();

    
    @FXML
    void userLogin(MouseEvent event) throws SQLException, InterruptedException {

        UserDAO userDAO = new UserDAO(sessionFactory);
        user = userDAO.getUser(String.valueOf(lognamein.getText()));
        if(user==null)
        {
            System.out.println("INVALID USER");
            invalidLogin.setText("Check Name!!!");
            invalidLogin.setVisible(true);
        }
        else if(user.password.equals(String.valueOf(logpassin.getText())))
        {
            System.out.println("LOGIN SUCCESS");
            invalidLogin.setText("Logging in!!!");
            invalidLogin.setFill(Color.web("#ecedec"));
            invalidLogin.setVisible(true);

            SessionManager.setCurrUser(user);
            SessionManager.setExpTransactionList();
            SessionManager.setInTransactionList();
            SessionManager.setTransactionList();
            SessionManager.setSavingsTransactionList();
            SessionManager.setTotalOverview();
            LocalDate today = LocalDate.now();
            //if(today.getDayOfMonth()==today.lengthOfMonth())
                SessionManager.updateSavingsTransactionList();


            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/HomeScene.fxml"));
                Parent root = loader.load();
                Controller homeController = loader.getController();
                homeController.initializePieChart();
                homeController.initializehomeText();
                homeController.welcomeUserText.setText("Welcome, "+String.valueOf(lognamein.getText()));
                stage = (Stage)((Node) event.getSource()).getScene().getWindow(); 
                scene = new Scene(root); 
                scene.setFill(Color.TRANSPARENT);
                stage.setScene(scene);
                stage.centerOnScreen();
                stage.show();

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        else
        {
            System.out.println("INCORRECT PASSWORD");
            invalidLogin.setText("Check Password!!!");
            invalidLogin.setVisible(true);
        }
    }



    //~~~~~~~~~~~~~~~~~~REGISTER SCENE~~~~~~~~~~~~~~~~~~~



    @FXML
    private Text invalidReg;

    @FXML
    private TextField regnamein;

    @FXML
    private TextField regpassin;

    @FXML
    private TextField regphnoin;

    @FXML
    void regNewUser(MouseEvent event) throws SQLException {
        UserDAO userDAO = new UserDAO(sessionFactory);
        UserObj newUser =  userDAO.getUser(String.valueOf(regnamein.getText()));
        if(newUser==null)
        {
            newUser=new UserObj();
            newUser.setName(String.valueOf(regnamein.getText()));
            newUser.setPassword(String.valueOf(regpassin.getText()));
            newUser.setPhno(String.valueOf(regphnoin.getText()));
            userDAO.createUser(newUser);
            sessionFactory.close();
            invalidReg.setText("User registered");
            invalidReg.setVisible(true);
            System.out.println("USER REGISTERED IN DATABASE");
        }
        else
        {
            invalidReg.setText("User already registered");
            invalidReg.setVisible(true);
            System.out.println("USER ALREADY REGISTERED IN DATABASE");
        }
    }

    @FXML
    void switchLoginScene(MouseEvent event) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/com/example/LoginScene.fxml")); 
            stage = (Stage)((Node) event.getSource()).getScene().getWindow(); 
            scene = new Scene(root); 
            scene.setFill(Color.TRANSPARENT);
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }



    //~~~~~~~~~~~~HOME SCENE~~~~~~~~~~~~~~~~~



    //Burger Menu Toggle


    @FXML
    private VBox burgerMenu;
    
    @FXML
    private Rectangle line1;

    @FXML
    private Rectangle line2;

    @FXML
    private Rectangle line3;

    @FXML
    private AnchorPane SideBar;

    private boolean isMenuOpen = true;

    @FXML
    private Text homeExp;

    @FXML
    private Text homeIn;

    @FXML
    private Text homeSav;

    @FXML
    void toggleSidebar(MouseEvent event) {
        RotateTransition rotateL1 = new RotateTransition(Duration.seconds(0.3),line1);
        TranslateTransition translateL1 = new TranslateTransition(Duration.seconds(0.3),line1);
        RotateTransition rotateL3 = new RotateTransition(Duration.seconds(0.3),line3);
        TranslateTransition translateL3 = new TranslateTransition(Duration.seconds(0.3),line3);
        FadeTransition fadeL2 = new FadeTransition(Duration.seconds(0.1),line2);
        TranslateTransition translateSB = new TranslateTransition(Duration.seconds(0.3), SideBar);
        if(isMenuOpen)
        {
            //Line 1 Transitions Settings
            rotateL1.setByAngle(45);
            translateL1.setByY(line1.getHeight()*2);
            //Line 2 Transitions Settings
            rotateL3.setByAngle(-45);
            translateL3.setByY(-line3.getHeight()*2);
            //Line 2 Transitions Settings
            fadeL2.setFromValue(1);
            fadeL2.setToValue(0);
            //SideBar Transitions Settings
            translateSB.setByX(300);


            rotateL1.play();
            translateL1.play();
            fadeL2.play();
            rotateL3.play();
            translateL3.play();
            translateSB.play();


            isMenuOpen=false;
        }
        else
        {
            //Line 1 Transitions
            rotateL1.setByAngle(-45);
            translateL1.setByY(-line1.getHeight()*2);
            //Line 2 Transitions
            rotateL3.setByAngle(45);
            translateL3.setByY(line3.getHeight()*2);
            //Line 2 Transitions
            fadeL2.setFromValue(0);
            fadeL2.setToValue(1);
            //SideBar Transitions Settings
            translateSB.setByX(-300);


            rotateL1.play();
            translateL1.play();
            fadeL2.play();
            rotateL3.play();
            translateL3.play();
            translateSB.play();

            isMenuOpen = true;
        }
    }



    //Sidebar



    @FXML
    void switchColorBlack(MouseEvent event) {   
        Node sourceNode = (Node) event.getSource();
        if (sourceNode instanceof Text)
        {
            Text t = (Text) sourceNode;
            t.setFill(Color.web("#ecedec"));
        }
        else if(isMenuOpen)
        {
            line1.setFill(Color.web("#ECEDEC"));
            line2.setFill(Color.web("#ECEDEC"));
            line3.setFill(Color.web("#ECEDEC"));

        }
        else
        {
            line1.setFill(Color.web("#ecedec"));
            line2.setFill(Color.web("#ecedec"));
            line3.setFill(Color.web("#ecedec"));
        }
    }

    @FXML
    void switchColorRed(MouseEvent event) {
        Node sourceNode = (Node) event.getSource();
        if (sourceNode instanceof Text)
        {
            Text t = (Text) sourceNode;
            t.setFill(Color.web("#888888"));
        }
        else
        {
            line1.setFill(Color.web("#888888"));
            line2.setFill(Color.web("#888888"));
            line3.setFill(Color.web("#888888"));
        }
    }

    @FXML
    void switchTransactionScene(MouseEvent event)
    {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/ExpenseTransactionScene.fxml"));
            Parent root = loader.load();
            Controller homeController = loader.getController();
            homeController.initializePieChart();
            stage = (Stage)((Node) event.getSource()).getScene().getWindow(); 
            scene = new Scene(root); 
            scene.setFill(Color.TRANSPARENT);
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    void switchExpenseOverviewScene(MouseEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/ExpenseOverviewScene.fxml"));
            Parent root = loader.load();
            Controller homeController = loader.getController();
            homeController.initializeSavPieChart();
            stage = (Stage)((Node) event.getSource()).getScene().getWindow(); 
            scene = new Scene(root); 
            scene.setFill(Color.TRANSPARENT);
            stage.setScene(scene);
            stage.show();
            enterSavTranshistTF();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    void switchIncomeTransactionScene(MouseEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/IncomeTransactionScene.fxml"));
            Parent root = loader.load();
            Controller homeController = loader.getController();
            homeController.initializeincomePieChart();
            stage = (Stage)((Node) event.getSource()).getScene().getWindow(); 
            scene = new Scene(root); 
            scene.setFill(Color.TRANSPARENT);
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML 
    void switchHomeScene(MouseEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/HomeScene.fxml"));
                Parent root = loader.load();
                Controller homeController = loader.getController();
                homeController.initializePieChart();
                homeController.initializehomeText();
                homeController.welcomeUserText.setText("Welcome, "+SessionManager.getCurrUser().name);
                stage = (Stage)((Node) event.getSource()).getScene().getWindow(); 
                scene = new Scene(root); 
                scene.setFill(Color.TRANSPARENT);
                stage.setScene(scene);
                stage.centerOnScreen();
                stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    //HOME MONTHLY OVERVIEW TEXT

    void initializehomeText() {
        homeExp.setText("  Rs."+String.valueOf(SessionManager.totalExp));
        homeIn.setText("  Rs."+String.valueOf(SessionManager.totalIn));
        homeSav.setText("  Rs."+String.valueOf(SessionManager.totalIn-SessionManager.totalExp));
    }



    //~~~~~~~~~~~~~~~~~~~~EXPENSES OVERVIEW~~~~~~~~~~~~~~~~~~~~~~~~~~~~



    //Pie Chart


    @FXML
    private PieChart homePieChart;

    public void initializePieChart() {
        List<TransactionObj> transactions = SessionManager.getExpTransactionList();
        Map<String, Double> categoryTotals = new HashMap<>();

        // Accumulate amounts by category
        for (TransactionObj transaction : transactions) {
            String category = transaction.getCategory();
            double amount = transaction.getAmount();

            // Sum the amounts for each category {categoryTotals.put(category, categoryTotals.getOrDefault(category, 0.0) + amount);}
            categoryTotals.put(category, categoryTotals.getOrDefault(category, 0.0) + amount);
        }

        // Create the PieChart data
        ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList();
        for (Map.Entry<String, Double> entry : categoryTotals.entrySet()) {
            pieChartData.add(new PieChart.Data(entry.getKey(), entry.getValue()));
        }

        // Set the data to the PieChart
        homePieChart.setData(pieChartData);
        homePieChart.setLabelsVisible(true);
    }

    @FXML
    private Text textPC;

    @FXML
    public void switchHomePieChart(MouseEvent event) {
        if(i==0)
        {
        List<TransactionObj> transactions = SessionManager.getInTransactionList();
        Map<String, Double> categoryTotals = new HashMap<>();

        // Accumulate amounts by category
        for (TransactionObj transaction : transactions) {
            String category = transaction.getCategory();
            double amount = transaction.getAmount();

            // Sum the amounts for each category {categoryTotals.put(category, categoryTotals.getOrDefault(category, 0.0) + amount);}
            categoryTotals.put(category, categoryTotals.getOrDefault(category, 0.0) + amount);
            i=1;
        }

        // Create the PieChart data
        ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList();
        for (Map.Entry<String, Double> entry : categoryTotals.entrySet()) {
            pieChartData.add(new PieChart.Data(entry.getKey(), entry.getValue()));
        }

        // Set the data to the PieChart
        homePieChart.setData(pieChartData);
        homePieChart.setLabelsVisible(true);
        textPC.setText("Income Chart");
        }
        else 
        {
            initializePieChart();
            textPC.setText("Expense Chart");
            i=0;
        }
    }



    //~~~~~~~~~~~~~~~~~TRANSACTION SCENE~~~~~~~~~~~~~~~~~~~~~~



    //Insert New Transaction


    @FXML
    private TextField expAmtin;

    @FXML
    private TextField expCategoryin;

    @FXML
    void insertTransaction(MouseEvent event) throws InterruptedException {
        UserObj user = SessionManager.getCurrUser();
        TransactionDAO transactionDAO = new TransactionDAO(sessionFactory);
        TransactionObj newTransaction = new TransactionObj();
        newTransaction.setType(0);
        newTransaction.setCategory(String.valueOf(expCategoryin.getText()));
        newTransaction.setAmount(Integer.parseInt(expAmtin.getText()));
        newTransaction.setUid(user.id);
        newTransaction.setTransdate(new Date());
        transactionDAO.createTransaction(newTransaction);
        SessionManager.setExpTransactionList();
        SessionManager.setSavingsTransactionList();
        expCategoryin.setText("");
        expAmtin.setText("");
        initializePieChart();
        enterTranshistTF();
    }



    //Transaction History



    @FXML
    private TextFlow transhistTF;

    void enterTranshistTF() {
        if(transhistTF==null)
        {
            System.err.println("Expense Text Flow is null");
            return;
        }
        transhistTF.getChildren().clear();
        List<TransactionObj> transactions = SessionManager.getExpTransactionList();

    
        for (int i = transactions.size() - 1; i >= 0; i--) {
            TransactionObj transaction = transactions.get(i); 
    
            Text heading = new Text("Transaction ID: ");
            heading.setFont(Font.font("Arial", FontWeight.BOLD, 16));
            heading.setFill(Color.web("#ecedec"));
    
            Text content = new Text(String.valueOf(transaction.getId()) + "\n");
            content.setFont(Font.font("Arial", 12));
            content.setFill(Color.web("#ecedec"));
    
            Text dateHeading = new Text("Transaction Date: ");
            dateHeading.setFont(Font.font("Arial", FontWeight.BOLD, 16));
            dateHeading.setFill(Color.web("#ecedec"));
    
            Text dateContent = new Text(String.valueOf(transaction.getTransdate()) + "\n");
            dateContent.setFont(Font.font("Arial", 12));
            dateContent.setFill(Color.web("#ecedec"));
    
            Text categoryHeading = new Text("Category: ");
            categoryHeading.setFont(Font.font("Arial", FontWeight.BOLD, 14));
            categoryHeading.setFill(Color.web("#ecedec"));
    
            Text categoryContent = new Text(transaction.getCategory() + "\n");
            categoryContent.setFont(Font.font("Arial", 12));
            categoryContent.setFill(Color.web("#ecedec"));
    
            Text amountHeading = new Text("Amount: ");
            amountHeading.setFont(Font.font("Arial", FontWeight.BOLD, 14));
            amountHeading.setFill(Color.web("#ecedec"));
    
            Text amountContent = new Text(String.valueOf(transaction.getAmount()) + "\n");
            amountContent.setFont(Font.font("Arial", 12));
            amountContent.setFill(Color.web("#ecedec"));
    
            Line line = new Line(0, 0, 480, 0);
            line.setStrokeWidth(2);
            line.setStroke(Color.web("#ecedec"));
    
            Text ln = new Text("\n\n");
    
            transhistTF.getChildren().addAll(heading, content, dateHeading, dateContent, categoryHeading, categoryContent, amountHeading, amountContent, line, ln);
        }
    }

    @FXML
    private TextFlow incomeHistTextFlow;

    @FXML
    private TextField incomeAmt;

    @FXML
    private TextField incomeCategory;

    @FXML
    private PieChart incomePiechart;

    @FXML
    void addIncomeAmt(MouseEvent event) {
        UserObj user = SessionManager.getCurrUser();
        TransactionDAO transactionDAO = new TransactionDAO(sessionFactory);
        TransactionObj newTransaction = new TransactionObj();
        newTransaction.setType(1);
        newTransaction.setCategory(String.valueOf(incomeCategory.getText()));
        newTransaction.setAmount(Integer.parseInt(incomeAmt.getText()));
        newTransaction.setUid(user.id);
        newTransaction.setTransdate(new Date());
        transactionDAO.createTransaction(newTransaction);
        SessionManager.setInTransactionList();
        incomeCategory.setText("");
        incomeAmt.setText("");
        SessionManager.setSavingsTransactionList();
        SessionManager.setTotalOverview();
        enterInTranshistTF();
        initializeincomePieChart();
    }

    void enterInTranshistTF() {
        if(incomeHistTextFlow == null)
        {
            System.err.println("Savings Text Flow is null");
            return;
        }

        incomeHistTextFlow.getChildren().clear();
        List<TransactionObj> transactions = SessionManager.getInTransactionList();

    
        for (int i = transactions.size() - 1; i >= 0; i--) {
            TransactionObj transaction = transactions.get(i); 
    
            Text heading = new Text("Transaction ID: ");
            heading.setFont(Font.font("Arial", FontWeight.BOLD, 16));
            heading.setFill(Color.web("#ecedec"));
    
            Text content = new Text(String.valueOf(transaction.getId()) + "\n");
            content.setFont(Font.font("Arial", 12));
            content.setFill(Color.web("#ecedec"));
    
            Text dateHeading = new Text("Transaction Date: ");
            dateHeading.setFont(Font.font("Arial", FontWeight.BOLD, 16));
            dateHeading.setFill(Color.web("#ecedec"));
    
            Text dateContent = new Text(String.valueOf(transaction.getTransdate()) + "\n");
            dateContent.setFont(Font.font("Arial", 12));
            dateContent.setFill(Color.web("#ecedec"));
    
            Text categoryHeading = new Text("Category: ");
            categoryHeading.setFont(Font.font("Arial", FontWeight.BOLD, 14));
            categoryHeading.setFill(Color.web("#ecedec"));
    
            Text categoryContent = new Text(transaction.getCategory() + "\n");
            categoryContent.setFont(Font.font("Arial", 12));
            categoryContent.setFill(Color.web("#ecedec"));
    
            Text amountHeading = new Text("Amount: ");
            amountHeading.setFont(Font.font("Arial", FontWeight.BOLD, 14));
            amountHeading.setFill(Color.web("#ecedec"));
    
            Text amountContent = new Text(String.valueOf(transaction.getAmount()) + "\n");
            amountContent.setFont(Font.font("Arial", 12));
            amountContent.setFill(Color.web("#ecedec"));
    
            Line line = new Line(0, 0, 480, 0);
            line.setStrokeWidth(2);
            line.setStroke(Color.web("#ecedec"));
    
            Text ln = new Text("\n\n");
    
            incomeHistTextFlow.getChildren().addAll(heading, content, dateHeading, dateContent, categoryHeading, categoryContent, amountHeading, amountContent, line, ln);
        }
    }

    @FXML
    void initializeincomePieChart() {
        List<TransactionObj> transactions = SessionManager.getInTransactionList();
        Map<String, Double> categoryTotals = new HashMap<>();

        // Accumulate amounts by category
        for (TransactionObj transaction : transactions) {
            String category = transaction.getCategory();
            double amount = transaction.getAmount();

            // Sum the amounts for each category {categoryTotals.put(category, categoryTotals.getOrDefault(category, 0.0) + amount);}
            categoryTotals.put(category, categoryTotals.getOrDefault(category, 0.0) + amount);
        }

        // Create the PieChart data
        ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList();
        for (Map.Entry<String, Double> entry : categoryTotals.entrySet()) {
            pieChartData.add(new PieChart.Data(entry.getKey(), entry.getValue()));
        }

        // Set the data to the PieChart
        incomePiechart.setData(pieChartData);
        incomePiechart.setLabelsVisible(true);
    }   



    //~~~~~~~~~~~~~~EXPENSE OVERVIEW~~~~~~~~~~~~~~~~~~~~



    @FXML
    private TextFlow savingTF;

    @FXML
    void enterSavTranshistTF() {
        if(savingTF == null)
        {
            System.err.println("Income Text Flow is null");
            return;
        }

        savingTF.getChildren().clear();
        List<TransactionObj> transactions = SessionManager.getSavingsTransactionList();

    
        for (TransactionObj transaction : transactions)
        {
            Text heading = new Text("Transaction ID: ");
            heading.setFont(Font.font("Arial", FontWeight.BOLD, 16));
            heading.setFill(Color.web("#ecedec"));
    
            Text content = new Text(String.valueOf(transaction.getId()) + "\n");
            content.setFont(Font.font("Arial", 12));
            content.setFill(Color.web("#ecedec"));
    
            Text dateHeading = new Text("Transaction Date: ");
            dateHeading.setFont(Font.font("Arial", FontWeight.BOLD, 16));
            dateHeading.setFill(Color.web("#ecedec"));
    
            Text dateContent = new Text(String.valueOf(transaction.getTransdate()) + "\n");
            dateContent.setFont(Font.font("Arial", 12));
            dateContent.setFill(Color.web("#ecedec"));
    
            Text categoryHeading = new Text("Month: ");
            categoryHeading.setFill(Color.web("#ecedec"));
    
            Text categoryContent = new Text(transaction.getCategory() + "\n");
            categoryContent.setFont(Font.font("Arial", 12));
            categoryContent.setFill(Color.web("#ecedec"));
    
            Text amountHeading = new Text("Amount: ");
            amountHeading.setFont(Font.font("Arial", FontWeight.BOLD, 14));
            amountHeading.setFill(Color.web("#ecedec"));
    
            Text amountContent = new Text(String.valueOf(transaction.getAmount()) + "\n");
            amountContent.setFont(Font.font("Arial", 12));
            amountContent.setFill(Color.web("#ecedec"));
    
            Line line = new Line(0, 0, 480, 0);
            line.setStrokeWidth(2);
            line.setStroke(Color.web("#ecedec"));
    
            Text ln = new Text("\n\n");
    
            savingTF.getChildren().addAll(heading, content, dateHeading, dateContent, categoryHeading, categoryContent, amountHeading, amountContent, line, ln);
        }
    }

    @FXML
    private PieChart expOverviewPiechart;

    @FXML
    void initializeSavPieChart() {
        int[] total = SessionManager.getTotalOverview();
        Map<String, Double> categoryTotals = new HashMap<>();


        categoryTotals.put("Expense",(double) total[0]);
        categoryTotals.put("Savings",(double) (total[1]-total[0]));

        System.out.println("\n\n\n\nEXPENSE : "+total[0]+"\nSAVINGS : "+(total[1]-total[0])+"\n\n\n\n");

        ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList();
        for (Map.Entry<String, Double> entry : categoryTotals.entrySet()) {
            pieChartData.add(new PieChart.Data(entry.getKey(), entry.getValue()));
        }

        // Set the data to the PieChart
        expOverviewPiechart.setData(pieChartData);
        expOverviewPiechart.setLabelsVisible(true);
    }

}