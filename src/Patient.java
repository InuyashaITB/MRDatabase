import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.LinkedList;

/**
 * Created by inuya on 1/26/2016.
 */
public class Patient extends Person{
    /**
     * Internal use. mRecords are all of the patients medical records.
     * The medicalRecordNumber will be generated upon the creation of a new person.
     */
    private int    medicalRecordNumber;
    private LinkedList<MedicalRecord> mRecords;

    public Patient(String firstName, String lastName, String streetAddress, String cityAddress, int zipCode, int SSN, String phoneNumber) {
        this.setFirstName(firstName);
        this.setLastName(lastName);
        this.setStreetAddress(streetAddress);
        this.setCityAddress(cityAddress);
        this.setZipCode(zipCode);
        this.setSSN(SSN);
        this.setPhoneNumber(phoneNumber);
        this.medicalRecordNumber = pullSQLMedicalRecordNumber(this);
        mRecords = new LinkedList<>();

    }

    public Patient() { mRecords = new LinkedList<>(); }

    /**
     * @return
     * Generate a unique medical record number that will identify the person directly in the medical record system     *
     */
    private static int pullSQLMedicalRecordNumber(Patient p){
        //This needs to be filled in later
        try {
            Main SQLSettings = new Main();
            Connection conn = DriverManager.getConnection(SQLSettings.getMyUrl(), SQLSettings.getUser(), AESEncryption.decrypt(SQLSettings.getPass(), Patient.encryptionKey));

            String cipherSSNString = new String(p.getCipherSSN());

            String query = "SELECT idpatients FROM patients WHERE SSN = ?";
            PreparedStatement ps = conn.prepareStatement(query);
            ps.setString(1, cipherSSNString);

            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
            return 0;
        }catch(Exception E){
            E.printStackTrace();
            return 0;
        }
    }
    /**
     * @return
     * Returns a linked list of the person's Medical Records
     */
    public LinkedList<MedicalRecord> getmRecords() {
        return mRecords;
    }

    /**
     *
     * @param mRecords
     *  set the person's Medical Records with a Linked List, this function is only to be used if creating a new medical
     *  record
     */
    private void setmRecords(LinkedList<MedicalRecord> mRecords) {
        this.mRecords = mRecords;
    }

    /**
     * Adds a new Medical Record entry into the Patient's medical record file.
     * @param BMI
     * @param upperBloodPressure
     * @param lowerBloodPressure
     * @param weight
     * @param temperature
     * @param age
     * @param heightInCentimeters
     * @param isSmoker
     * @param primaryInsurance
     * @param preferredPharmacy
     * @param reasonForVisit
     * @param allergies
     * @param diagnosis
     */

    public void newMedicalRecord(double BMI, int upperBloodPressure, int lowerBloodPressure, double weight, double temperature, int age, double heightInCentimeters, boolean isSmoker, String primaryInsurance, String preferredPharmacy, String[] reasonForVisit, String[] allergies, String[] diagnosis){
        mRecords.add(new MedicalRecord(BMI, upperBloodPressure, lowerBloodPressure, weight, temperature, age, heightInCentimeters, isSmoker, primaryInsurance, preferredPharmacy, reasonForVisit, allergies, diagnosis));
    }

    public void newMedicalRecord(MedicalRecord record){
        mRecords.add(record);
    }

    /**
     * @return
     * returns the person's unique record number
     */
    public int getMedicalRecordNumber() {
        return medicalRecordNumber;
    }

    /**
     *
     * @param medicalRecordNumber
     * medicalRecordNumber is the unique record number for the person. This functions should only be used to fix
     * corrections
     */
    public void setMedicalRecordNumber(int medicalRecordNumber) {
        this.medicalRecordNumber = medicalRecordNumber;
    }

    public String mySQLInsert(MedicalRecord Record){
        String query = "";
        return query;
    }

    public String listFields() {
        Patient.class.getDeclaredFields()[0].getName();
        return "(idpatients, BMI, upperBloodPressure, lowerBloodPressure, weight, temperature, age, heightInCentimeters, isSmoker, primaryInsurance, preferredPharmacy, reasonForVisit, allergies, diagnosis)";
    }
}
