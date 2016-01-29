import java.util.HashMap;
import java.util.LinkedList;
import java.util.Scanner;
import java.sql.*;

public class Main {

    private String myDriver = "com.mysql.jdbc.Driver";
    private String hostName = "localhost";
    private String databaseName = "cs380";
    private String myUrl = "jdbc:mysql://" + hostName + "/" + databaseName;
    private String user = "root";       // These will be modified later to allow for user input of the username and password
    private String pass = "12lights";   // of the MySQL Database System

    private Patient currentPatient; //Allow one patient to be active in memory at any given moment in time

    /**
     * 'results' is the LinkedList of integer values that represent the resulting codes of each function. This provides
     * better interaction with the user of the program such that they understand what an error means and point them
     * on where to look for help. These error codes are defined in the @function createErrorCode()
     */
    private LinkedList<Integer> results;

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
        Main m = new Main();

        m.mainMenu();
        m.resultsReadOut();
    }

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
            loadPatient(MRN);
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
    private void loadPatient(int MRN){
        try {
            Connection conn = DriverManager.getConnection(myUrl, user, pass);

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
        }
        catch (Exception E){
            E.printStackTrace();
            results.add(5001);
        }

    }

    private boolean displayTable() {
        try{
            Connection conn = DriverManager.getConnection(myUrl, user, pass);

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
        currentPatient.setZipCode(s.nextInt());

        System.out.print("SSN: ");
        currentPatient.setSSN(s.nextInt());

        System.out.print("Phone Number: ");
        currentPatient.setPhoneNumber(s.next().replaceAll("-",""));

        return addPatientToDB(currentPatient);
    }
    public boolean addPatientToDB(Patient p){
        if (p == null){
            System.out.println("Patient to be added cannot be null!");
            return false;
        }
        try {
            String cipherSSN = new String(p.getCipherSSN());
            Connection conn = DriverManager.getConnection(myUrl, user, pass);

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
            query = "INSERT INTO patients (firstName, lastName, streetAddress, cityAddress, zipCode, SSN, phoneNumber) "
                    +  "VALUES ('"
                    + p.getFirstName() + "', '"
                    + p.getLastName()  + "', '"
                    + p.getStreetAddress() + "', '"
                    + p.getCityAddress() + "', '"
                    + p.getZipCode() + "', '"
                    + cipherSSN + "', '"
                    + p.getPhoneNumber() +"');";
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

            Connection conn = DriverManager.getConnection(myUrl, user, pass);
            String query = "SELECT idpatients FROM patients WHERE SSN = ?";
            PreparedStatement ps = conn.prepareStatement(query);
            ps.setString(1, cipherSSNString);

            ResultSet rs = ps.executeQuery();
            if (rs.next()){
                int MRN = rs.getInt(1);
                System.out.println("MRN: " + MRN);
                loadPatient(MRN);
                return true;
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
            Connection conn = DriverManager.getConnection(myUrl, user, pass);
            String query = "SELECT idpatients FROM patients WHERE phoneNumber = ?";
            PreparedStatement ps = conn.prepareStatement(query);
            ps.setString(1, phoneNumber);

            ResultSet rs = ps.executeQuery();
            if (rs.next()){
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
        Scanner s = new Scanner(System.in);
        if (currentPatient.getMedicalRecordNumber() == 0) {
            System.out.println("Enter the patient Medical Record Number to be Deleted: ");
            int MRN = s.nextInt();
            try {
                Connection conn = DriverManager.getConnection(myUrl, user, pass);
                String query = "SELECT count(*) FROM patients WHERE idpatients = ?";
                PreparedStatement ps = conn.prepareStatement(query);
                ps.setInt(1, MRN);

                ResultSet rs = ps.executeQuery();
                if(rs.next()){
                    if(rs.getInt(1) > 0){
                        System.out.print("Are you sure? (Y|N) ");
                        if(s.next().equalsIgnoreCase("Y")){
                            query = "DELETE from patients WHERE idpatients = ?";
                            ps = conn.prepareStatement(query);
                            ps.setInt(1, MRN);

                            ps.executeUpdate();

                            conn.close();
                            return true;
                        }
                        else{
                            System.out.println("Cancelled deletion sequence");
                            results.add(3002);
                            conn.close();
                            return false;
                        }
                    }
                }

                conn.close();
            } catch (Exception E){
                E.printStackTrace();
                results.add(3001);
            }
        }

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

    public String getPass() {
        return pass;
    }

    public void setPass(String pass) {
        this.pass = pass;
    }
}
