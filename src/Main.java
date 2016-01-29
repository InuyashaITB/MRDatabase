import java.io.*;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Scanner;
import java.sql.*;

import static java.lang.String.valueOf;

public class Main {

    private String myDriver = "com.mysql.jdbc.Driver";

    /**
     * MySQL setting variables. These will be saved in the config.cfg file
     */
    private String hostName;
    private String databaseName;
    private String myUrl;

    /**
     * These will be asked upon entry to program every time.
     * Pass is encrypted.
     */
    private String user;
    private byte[] pass;

    private Patient currentPatient; //Allow one patient to be active in memory at any given moment in time

    /**
     * 'results' is the LinkedList of integer values that represent the resulting codes of each function. This provides
     * better interaction with the user of the program such that they understand what an error means and point them
     * on where to look for help. These error codes are defined in the @function createErrorCode()
     */
    private LinkedList<Integer> results;

    /**
     *
     * The errorCodes HashMap is my simple way of creating an error / result catching lookup table so that we can know
     * what and why something happens the way that it does.
     */
    private static HashMap<Integer, String> errorCodes = new HashMap<>();

    public Main() {
        try {
            currentPatient = new Patient();
            Class.forName(myDriver);
        }
        catch (Exception E){
            E.printStackTrace();
        }
        results = new LinkedList<>();
    }

    /**
     * the createErrorCodes() function fills our error code HashMap with the necessary data to perform lookups later on
     * for errors / results that are useful.
     */
    private static void createErrorCodes(){
        errorCodes.put(-1,      "No functions ran properly");
        errorCodes.put(0,       "User called for Exit");
        errorCodes.put(1,       "Patient Added");
        errorCodes.put(1000,    "Patient could not be Added");
        errorCodes.put(1001,    "Patient already exists within the Table");
        errorCodes.put(2,       "Patient Found");
        errorCodes.put(2000,    "Patient could not be Found");
        errorCodes.put(3,       "Patient Deleted");
        errorCodes.put(3000,    "Patient could not be Deleted");
        errorCodes.put(3001,    "Patient could not be Deleted due to database Error");
        errorCodes.put(3001,    "Patient deletion cancelled by User Request");
        errorCodes.put(4,       "Table Retrieved Successfully");
        errorCodes.put(4000,    "Table not displayed Correctly");
        errorCodes.put(5,       "Patient loaded into Memory");
        errorCodes.put(5000,    "Patient could not be loaded");
        errorCodes.put(5001,    "Patient loading issue from Database");
        errorCodes.put(10000,   "File Not Found Exception");
        errorCodes.put(10001,   "IOException while trying to create config.cfg");
        errorCodes.put(10002,   "config.cfg has been properly created");
        errorCodes.put(10003,   "Tables already exist within Database");
        errorCodes.put(10004,   "Error in createDatabase function");
        errorCodes.put(10005,   "Created Tables Correctly");
        errorCodes.put(10006,   "Database created");
        errorCodes.put(10007,   "Database already exists, continuing Table Creation");
        errorCodes.put(10008,   "Parse Exception in Date of Birth input, probably incorrect input format");
    }

    /**
     * The resultsReadOut() function is used to display the result codes that were accumulated up until this function
     * is called. It then clears out the results to prevent repetition.
     */
    protected void resultsReadOut(){
        if(!results.isEmpty()) {
            System.out.println("Results:");
            for (Integer code : results)
                System.out.println(" --" + errorCodes.get(code));
            System.out.println("");
            results.clear();
        }

    }

    /**
     *
     * @param args
     * The main function is the first function ran by the program. Mainly used to display the main menu and display
     * final result codes.
     */
    public static void main(String[] args){
        createErrorCodes();
        Scanner s = new Scanner(System.in);
        Main m = new Main();
        if(isFirstRun()){
            System.out.print("Enter the MySQL host name (i.e localhost): ");
            m.setHostName(s.nextLine());

            System.out.print("Enter the MySQL database name (i.e. Hospital Name): ");
            m.setDatabaseName(s.nextLine().replace(" ", ""));

            m.setMyUrl("jdbc:mysql://" + m.getHostName() + "/" + m.getDatabaseName());

            System.out.print("Enter the MySQL User Name: ");
            m.setUser(s.nextLine());

            System.out.print("Enter the MySQL Password: ");
            try{m.setPass(AESEncryption.encrypt(padString(s.nextLine()), m.currentPatient.getEncryptionKey()));}
            catch(Exception E){ m.results.add(9999); errorCodes.put(9999, stackTraceToString(E));}

            try {
                File f = new File("config.cfg");
                f.createNewFile();
                PrintStream p = new PrintStream(f);
                p.println("[MySQL Settings]");
                p.println("hostName: " + m.getHostName());
                p.println("databaseName: " + m.getDatabaseName());
                p.println("myUrl: " + m.getMyUrl());
                p.close();
                m.results.add(10002);

            } catch (FileNotFoundException E){
                E.printStackTrace();
                m.results.add(10000);
            } catch (IOException E){
                E.printStackTrace();
                m.results.add(10001);
            }

            m.resultsReadOut();
        }
        else{
            try {
                LinkedList<String> configuration = new LinkedList<>();
                Scanner fileR = new Scanner(new File("config.cfg"));
                while(fileR.hasNextLine()){
                    configuration.add(fileR.nextLine());
                }

                String hostName = configuration.get(1).split(" ")[1];
                String databaseName = configuration.get(2).split(" ")[1];
                String myUrl =configuration.get(3).split(" ")[1];
                m.setHostName(hostName);
                m.setDatabaseName(databaseName);
                m.setMyUrl(myUrl);

                System.out.print("Enter the MySQL User Name: ");
                m.setUser(s.nextLine());

                System.out.print("Enter the MySQL Password: ");
                try{m.setPass(AESEncryption.encrypt(padString(s.nextLine()), valueOf(m.currentPatient.getEncryptionKey())));}
                catch(Exception E){ m.results.add(9999); errorCodes.put(9999, stackTraceToString(E));}

                fileR.close();
            }
            catch (Exception E){
                errorCodes.put(9999, stackTraceToString(E));
                m.results.add(9999);
            }
        }
        m.createDatabaseTables();
        m.mainMenu();
        m.resultsReadOut();
    }

    /**
     * For Encryption Purposes
     * @param s
     * String that needs to be padded, if not done so already
     * @return
     * Returns a String that is a multiple of 16 bytes. Primarily used for Encryption purposes.
     */
    private static String padString(String s) {
        String ret;
        if((s.length() % 16) != 0){
            ret = s;
            while(ret.length() % 16 != 0){
                ret += "\0";
            }
            return ret;
        }
        else
            return s;
    }

    /**
     * Upon first run, we must make sure that the tables have been created within the database. If the tables (both)
     * already exist, it simply means that this is the first time that this particular program has been ran, and should
     * not override the current tables.
     * Otherwise, if they do not exist already, create new entries.
     */
    private void createDatabaseTables() {
        try {
            //For first connection, we must make sure that the database already exists
            Connection conn = DriverManager.getConnection(myUrl.replace(databaseName, ""), user, getDecryptedPass());
            String query;
            PreparedStatement preparedStmt;
            try {
                query = "CREATE DATABASE " + databaseName;
                preparedStmt = conn.prepareStatement(query);


            //If 1007 is the return code, then the DB already exists
                try {
                    if (preparedStmt.executeUpdate() == 1007) results.add(10007);
                    else results.add(10006);
                } catch (SQLException T){//Don't do anything with this exception
                     }
            } catch (Exception E){
                errorCodes.put(9999, stackTraceToString(E));
                results.add(9999);
            }
            conn.close();

            conn = DriverManager.getConnection(myUrl, user, getDecryptedPass());
            // create the java mysql update prepared statement
            query = "SELECT TABLE_NAME FROM information_schema.TABLES WHERE TABLE_SCHEMA = ?";
            preparedStmt = conn.prepareStatement(query);
            preparedStmt.setString(1, databaseName);

            ResultSet rs = preparedStmt.executeQuery();
            while(rs.next()) {
                String res = rs.getString("TABLE_NAME");
                try {
                    if (!(res.contains("patients"))) {
                        query = "CREATE TABLE patients (idpatients INT(11) NOT NULL AUTO_INCREMENT, firstName TINYTEXT, lastName TINYTEXT, streetAddress LONGTEXT, cityAddress TINYTEXT, zipCode INT(11), SSN LONGTEXT, phoneNumber TINYTEXT, dateOfBirth TINYTEXT, PRIMARY KEY (idpatients))";
                        preparedStmt = conn.prepareStatement(query);
                        preparedStmt.executeUpdate();
                        results.add(10003);
                    } else if (!(res.contains("records"))) {
                        query = "CREATE TABLE records (idpatients INT(11) NOT NULL, BMI DOUBLE, upperBloodPressure INT(5), lowerBloodPressure INT(5), weight DOUBLE, temperature DOUBLE, age INT(3), heightInCentimeters DOUBLE, isSmoker BOOLEAN, primaryInsurance LONGTEXT, preferredPharmacy LONGTEXT, reasonForVisit BLOB, allergies BLOB, diagnosis BLOB, PRIMARY KEY (idpatients))";
                        preparedStmt = conn.prepareStatement(query);
                        preparedStmt.executeUpdate();
                    }
                } catch (Exception E){}
            }
           //else {
           //    query = "CREATE TABLE patients (idpatients INT(11) NOT NULL AUTO_INCREMENT, firstName TINYTEXT, lastName TINYTEXT, streetAddress LONGTEXT, cityAddress TINYTEXT, zipCode INT(11), SSN LONGTEXT, phoneNumber TINYTEXT, dateOfBirth TINYTEXT, PRIMARY KEY (idpatients))";
           //    preparedStmt = conn.prepareStatement(query);
           //    preparedStmt.executeUpdate();


           //    query = "CREATE TABLE records (idpatients INT(11) NOT NULL, BMI DOUBLE, upperBloodPressure INT(5), lowerBloodPressure INT(5), weight DOUBLE, temperature DOUBLE, age INT(3), heightInCentimeters DOUBLE, isSmoker BOOLEAN, primaryInsurance LONGTEXT, preferredPharmacy LONGTEXT, reasonForVisit BLOB, allergies BLOB, diagnosis BLOB, PRIMARY KEY (idpatients))";
           //    preparedStmt = conn.prepareStatement(query);
           //    preparedStmt.executeUpdate();
           //}
            conn.close();
            results.add(10005);
        } catch (Exception E){
            errorCodes.put(9999, stackTraceToString(E));
            results.add(9999);
            results.add(10004);
        }

    }

    /**
     *
     * @return
     * First run returns a boolean value, if the file config.cfg already exists, then it is not the first run for this
     * program and shall be treated as such. Otherwise it is the first run, and we need to create the config.cfg file
     * with the proper configurations
     */
    private static boolean isFirstRun() { return (!(new File("config.cfg").exists()));}

    /**
     *
     * The mainMenu() function is what controls how the user interacts with the System. Currently used as a command line
     * interface, however this may change in the future to a GUI and be commented out based on GUI specifications.
     *
     */
    private void mainMenu(){
        do {
            resultsReadOut();
            System.out.println("Would you like to: \n 0.) Exit \n 1.) Add New Patient \n 2.) Search for Patient MRN \n 3.) Delete Patient \n 4.) Display Table \n 5.) Load Patient");
            Scanner s = new Scanner(System.in);
            if (s.hasNextInt()) {
                switch (s.nextInt()){
                    case 0: results.add(0);
                            return;
                    case 1: if(addNewPatientMenu())
                                results.add(1);
                            else
                                results.add(1000);
                            break;

                    case 2: if(searchPatientMenu())
                                results.add(2);
                            else
                                results.add(2000);
                            break;

                    case 3: if(deletePatientMenu())
                                results.add(3);
                            else
                                results.add(3000);
                            break;
                    case 4: if(displayTable())
                                results.add(4);
                            else
                                results.add(4000);
                            break;
                    case 5: if(selectPatientMenu())
                                results.add(5);
                            else
                                results.add(5000);
                            break;
                    case 6: try{System.out.println(AESEncryption.decrypt(pass, Patient.encryptionKey).replaceAll("\0", ""));}catch(Exception E){E.printStackTrace();}
                    default: break;
                }
            }
            else System.out.println("Please enter a valid number");
        }while(true);

    }

    /**
     *
     * @return
     * Returns a boolean value, indicating whether the patient was properly found and selected from the Database
     */
    private boolean selectPatientMenu() {
        System.out.println("Enter the patient's Medical Record Number");
        Scanner s = new Scanner(System.in);
        if (s.hasNextInt()){
            int MRN = s.nextInt();
            return loadPatient(MRN);
        }
        else System.out.println("Make sure that the Medical Record Number is correctly formatted");
        return false;
    }

    /**
     * The loadPatient function is to load the patient in question (identified by their MRN) into the currentPatient
     * variable.
     * @param MRN
     * MRN is the Medical Record Number of the patient being loaded into memory.
     */
    private boolean loadPatient(int MRN){
        try {
            Connection conn = DriverManager.getConnection(myUrl, user, getDecryptedPass());

            // create the java mysql update prepared statement
            String query = "SELECT * FROM patients WHERE idpatients = ?";
            PreparedStatement preparedStmt = conn.prepareStatement(query);
            preparedStmt.setInt(1, MRN);

            // execute the java prepared statement
            ResultSet rs = preparedStmt.executeQuery();
            if(rs.next()){
                currentPatient.setMedicalRecordNumber(rs.getInt("idpatients"));
                currentPatient.setFirstName(rs.getString("firstName"));
                currentPatient.setLastName(rs.getString("lastName"));
                currentPatient.setStreetAddress(rs.getString("streetAddress"));
                currentPatient.setCityAddress(rs.getString("cityAddress"));
                currentPatient.setZipCode(rs.getInt("zipCode"));
                currentPatient.setSSNCipher(rs.getString("SSN"));
                currentPatient.setPhoneNumber(rs.getString("phoneNumber"));
                System.out.println(currentPatient.getFirstName() + " " + currentPatient.getLastName() + " Loaded");
            }

            conn.close();
            return true;
        }
        catch (Exception E){
            errorCodes.put(9999, stackTraceToString(E));
            results.add(9999);
            results.add(5001);
            return false;
        }

    }

    private boolean displayTable() {
        try{
            Connection conn = DriverManager.getConnection(myUrl, user, getDecryptedPass());

            // create the java mysql update prepared statement
            String query = "SELECT * FROM patients";
            PreparedStatement preparedStmt = conn.prepareStatement(query);

            // execute the java prepared statement
            ResultSet rs = preparedStmt.executeQuery();
            ResultSetMetaData rsmd = rs.getMetaData();
            int columnsNumber = rsmd.getColumnCount();
            while(rs.next()){
                for(int i = 1; i <= columnsNumber; i++){
                    if(i>1) System.out.print(",  ");
                    String columnValue = rs.getString(i);
                    System.out.print(columnValue + " " + rsmd.getColumnName(i));
                }
                System.out.println("");
            }

            conn.close();
            return true;
        }
        catch (Exception E){
            E.printStackTrace();
            return false;
        }
    }

    private boolean addNewPatientMenu() {
        currentPatient = new Patient();

        Scanner s = new Scanner(System.in);
        System.out.print("First Name: ");
        currentPatient.setFirstName(s.nextLine());

        System.out.print("Last Name: ");
        currentPatient.setLastName(s.nextLine());

        do{
            System.out.print("Street Address: ");
            String street = s.nextLine();
            if(street.contains(",")){
                System.out.println("Please enter ONLY the Street address, NO City or Zip Code");
                continue;
            }
            currentPatient.setStreetAddress(street);
            break;
        }while(true);
        System.out.print("City: ");
        currentPatient.setCityAddress(s.nextLine());

        System.out.print("Zip Code: ");
        currentPatient.setZipCode(Integer.parseInt(s.nextLine()));

        System.out.print("SSN: ");
        while(!currentPatient.setSSN(s.nextLine())){System.out.print("SSN: ");}

        System.out.print("Phone Number: ");
        currentPatient.setPhoneNumber(s.nextLine().replaceAll("-",""));

        while(true) {
        System.out.print("Date of Birth MM/DD/YYYY: ");
        String input = s.nextLine();
        String pattern = "MM/dd/yyyy";
        SimpleDateFormat formatter = new SimpleDateFormat(pattern);
            try {
                java.util.Date dob = formatter.parse(input);
                currentPatient.setDateOfBirth(dob);
                break;
            } catch (Exception E) {
                E.printStackTrace();
                results.add(10008);
            }
        }

        return addPatientToDB(currentPatient);
    }
    public boolean addPatientToDB(Patient p){
        if (p == null){
            System.out.println("Patient to be added cannot be null!");
            return false;
        }
        try {
            String cipherSSN = new String(p.getCipherSSN());
            Connection conn = DriverManager.getConnection(myUrl, user, getDecryptedPass());

            // initial check to make sure that this is not a duplicate entry.
            String query = "SELECT count(*) FROM patients WHERE SSN = ?";
            PreparedStatement preparedStmt = conn.prepareStatement(query);
            preparedStmt.setString(1, cipherSSN);

            ResultSet rs = preparedStmt.executeQuery();
            if(rs.next()){
                int count = rs.getInt(1);
                if(count != 0) {
                    results.add(1001);
                    return false;
                }
            }

            // create the java mysql update prepared statement
            query = "INSERT INTO patients (firstName, lastName, streetAddress, cityAddress, zipCode, SSN, phoneNumber, dateOfBirth) "
                    +  "VALUES ('"
                    + p.getFirstName() + "', '"
                    + p.getLastName()  + "', '"
                    + p.getStreetAddress() + "', '"
                    + p.getCityAddress() + "', '"
                    + p.getZipCode() + "', '"
                    + cipherSSN + "', '"
                    + p.getPhoneNumber() + "', '"
                    + p.getDateOfBirthFormatted() + "');";
            preparedStmt = conn.prepareStatement(query);

            // execute the java prepared statement
            preparedStmt.executeUpdate();
            return true;
        }
        catch (Exception E){
            E.printStackTrace();
            return false;
        }
    }

    private boolean searchPatientMenu() {
        System.out.println("How would you like to search?\n"
                + " 1.) Phone Number\n"
                + " 2.) Social Security Number\n");
        Scanner s = new Scanner(System.in);
        while(true) {
            if (s.hasNextInt()) {
                switch (s.nextInt()) {
                    case 1:
                        System.out.print("Enter the Phone Number: ");
                        return searchPatientPhone(s.next());

                    case 2:
                        System.out.print("Enter the SSN: ");
                        return searchPatientSSN(s.nextInt());

                    default:
                        System.out.println("Enter a valid number");
                        break;
                }
            }
        }
    }

    private boolean searchPatientSSN(int SSN) {
        try {
            String SSNString = Integer.toString(SSN);
            if (SSNString.replaceAll("-", "").length() != 9) {
                System.out.println("SSN is not a valid one, make sure it is correct");
            }
            byte[] cipherSSN = AESEncryption.encrypt((SSNString + "\0\0\0\0\0\0\0"), Patient.encryptionKey);
            String cipherSSNString = new String(cipherSSN);

            Connection conn = DriverManager.getConnection(myUrl, user, getDecryptedPass());
            String query = "SELECT idpatients FROM patients WHERE SSN = ?";
            PreparedStatement ps = conn.prepareStatement(query);
            ps.setString(1, cipherSSNString);

            ResultSet rs = ps.executeQuery();
            if (rs.next()){
                int MRN = rs.getInt(1);
                System.out.println("MRN: " + MRN);
                return loadPatient(MRN);
            }

        }
        catch(Exception E){
            E.printStackTrace();
            return false;
        }
        return false;
    }

    private boolean searchPatientPhone(String phoneNumber){
        phoneNumber = phoneNumber.replaceAll("-","");
        try {
            Connection conn = DriverManager.getConnection(myUrl, user, getDecryptedPass());
            String query = "SELECT idpatients FROM patients WHERE phoneNumber = ?";
            PreparedStatement ps = conn.prepareStatement(query);
            ps.setString(1, phoneNumber);

            ResultSet rs = ps.executeQuery();
            while (rs.next()){
                System.out.println("MRN: " + rs.getInt(1));
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return false;
    }


    private boolean deletePatientMenu() {
        try {
            Scanner s = new Scanner(System.in);
            Connection conn = DriverManager.getConnection(myUrl, user, getDecryptedPass());
            String query;
            PreparedStatement ps;
            if (currentPatient.getMedicalRecordNumber() == 0) {
                System.out.println("Enter the patient Medical Record Number to be Deleted: ");
                int MRN = s.nextInt();
                try {
                    query = "SELECT count(*) FROM patients WHERE idpatients = ?";
                    ps = conn.prepareStatement(query);
                    ps.setInt(1, MRN);

                    ResultSet rs = ps.executeQuery();
                    if (rs.next()) {
                        if (rs.getInt(1) > 0) {
                            System.out.print("Are you sure? (Y|N) ");
                            if (s.next().equalsIgnoreCase("Y")) {
                                query = "DELETE FROM patients WHERE idpatients = ?";
                                ps = conn.prepareStatement(query);
                                ps.setInt(1, MRN);

                                ps.executeUpdate();

                                conn.close();
                                return true;
                            } else {
                                System.out.println("Cancelled deletion sequence");
                                results.add(3002);
                                conn.close();
                                return false;
                            }
                        }
                    }

                    conn.close();
                } catch (Exception E) {
                    E.printStackTrace();
                    results.add(3001);
                }
            } else {
                System.out.print("Are you sure? (Y|N) ");
                if (s.next().equalsIgnoreCase("Y")) {
                    query = "DELETE FROM patients WHERE idpatients = ?";
                    ps = conn.prepareStatement(query);
                    ps.setInt(1, currentPatient.getMedicalRecordNumber());

                    ps.executeUpdate();

                    conn.close();
                    return true;
                } else {
                    System.out.println("Cancelled deletion sequence");
                    results.add(3002);
                    conn.close();
                    return false;
                }
            }
        } catch (Exception E){results.add(1235); stackTraceToString(E);}
        return false;
    }

    public String getMyDriver() {
        return myDriver;
    }

    public void setMyDriver(String myDriver) {
        this.myDriver = myDriver;
    }

    public String getHostName() {
        return hostName;
    }

    public void setHostName(String hostName) {
        this.hostName = hostName;
    }

    public String getDatabaseName() {
        return databaseName;
    }

    public void setDatabaseName(String databaseName) {
        this.databaseName = databaseName;
    }

    public String getMyUrl() {
        return myUrl;
    }

    public void setMyUrl(String myUrl) {
        this.myUrl = myUrl;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    private String getDecryptedPass(){
        try{
        String ret = AESEncryption.decrypt(pass, currentPatient.getEncryptionKey()).replaceAll("\0","");
        return ret;
        }
        catch(Exception E){results.add(1234); errorCodes.put(1234, stackTraceToString(E)); return null;}
    }
    public byte[] getPass() {
        return pass;
    }

    public void setPass(byte[] pass) {
        this.pass = pass;
    }
    
    public static String stackTraceToString(Exception E){
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        E.printStackTrace(pw);
        return sw.toString();
    }
}
