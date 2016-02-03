import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.Period;
import java.time.Year;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by inuya on 1/26/2016.
 */
public class Person {
    /**
     * Regular inputs that every person will have to provide
     */
    protected String firstName;
    protected String lastName;
    protected String streetAddress;
    protected String cityAddress;
    protected int    zipCode;
    protected byte[] cipherSSN;
    protected String phoneNumber;
    protected Date   dateOfBirth;

    protected static final String encryptionKey = "fedcba9876543210";
    private String SSNCipher;


    /**
     * Standard constructor takes First Name, Last Name, Street Address, City, Zip Code, and Social Security Number as
     * parameters.
     * @param firstName
     * Patient's First Name
     * @param lastName
     * Patient's Last Name
     * @param streetAddress
     * Patient's Street Address Only
     * @param cityAddress
     * Patient's City Address Only
     * @param zipCode
     * Patient's Zip Code
     * @param SSN
     * Patient's Social Security Number
     * @param phoneNumber
     * Patient's Phone Number (no dashes)
     */
    public Person(String firstName, String lastName, String streetAddress, String cityAddress, int zipCode, int SSN, String phoneNumber, Date dateOfBirth) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.streetAddress = streetAddress;
        this.cityAddress = cityAddress;
        this.zipCode = zipCode;
        this.dateOfBirth = dateOfBirth;

        setSSN(SSN);

        this.phoneNumber = phoneNumber;
    }

    /**
     * Blank constructor for basic use of Person class
     */
    public Person(){}

    /**
     * @return
     * Returns the person's social security number. Protected because this should not be publicly visible. Can be used by
    functions that would be used as an extension of the Person object, otherwise it should not be publicly available.
     */
    protected int getSSN() {
        try {
            String result = AESEncryption.decrypt(cipherSSN, "");
            return Integer.parseInt(result);
        }
        catch (Exception E) {
            E.printStackTrace();
            return 0;
        }
    }

    /**
     * @return
     * Returns the person's Encrypted Social Security Number to be used with Encryption / Saving to blob file
     *
     */
    protected byte[] getCipherSSN(){
        return cipherSSN;
    }

    /**
     *
     * @param SSN
     * Sets the person's social security number, encrypted
     */
    protected boolean setSSN(int SSN) {
        try {
            String t = Integer.toString(SSN).replaceAll("-", "");
            if (t.length() != 9) {
                System.out.println("Make sure that SSN is correct");
                return false;
            }
            cipherSSN = AESEncryption.encrypt((t + "\0\0\0\0\0\0\0"), encryptionKey);
            return true;
        }
        catch(Exception E){
            E.printStackTrace();
            return false;
        }
    }

    /**
     *
     * @param SSN
     * Sets the person's social security number, encrypted
     */
    protected boolean setSSN(byte[] SSN) {
        try {
            String t = new String(SSN).replaceAll("-", "");

            cipherSSN = AESEncryption.encrypt((t + "\0\0\0\0\0\0\0"), encryptionKey);
            return true;
        }
        catch(Exception E){
            E.printStackTrace();
            return false;
        }
    }

    /**
     *
     * @param SSN
     * Sets the person's social security number, encrypted
     */
    protected boolean setSSN(String SSN) {
        if (SSN.replaceAll("-","").length() != 9){
            System.out.println("SSN is not of correct length");
            return false;
        }
        try {
            cipherSSN = AESEncryption.encrypt((SSN + "\0\0\0\0\0\0\0"), encryptionKey);
            return true;
        }
        catch(Exception E){
            E.printStackTrace();
            return false;
        }
    }

    /**
     *
     * @return
     * Returns the person's first name
     */
    public String getFirstName() {
        return firstName;
    }

    /**
     *
     * @return
     * Returns the person's last name
     */
    public String getLastName() {
        return lastName;
    }

    /**
     *
     * @return
     * Returns the person's street address
     */
    public String getStreetAddress() {
        return streetAddress;
    }

    /**
     *
     * @return
     * Returns the person's City address
     */
    public String getCityAddress() {
        return cityAddress;
    }

    /**
     *
     * @return
     *  Returns the person's Zip Code
     */
    public int getZipCode() {
        return zipCode;
    }


    /**
     *
     * @param firstName
     * Set the person's first name
     */
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    /**
     *
     * @param lastName
     * Set the person's last name
     */
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    /**
     *
     * @param streetAddress
     * set the person's Street Address
     */
    public void setStreetAddress(String streetAddress) {
        this.streetAddress = streetAddress;
    }

    /**
     *
     * @param cityAddress
     * set the person's City address
     */
    public void setCityAddress(String cityAddress) {
        this.cityAddress = cityAddress;
    }

    /**
     *
     * @param zipCode
     * set the person's zip code
     */
    public void setZipCode(int zipCode) {
        this.zipCode = zipCode;
    }

    public String getPhoneNumber() { return phoneNumber; }

    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber;  }

    public String getEncryptionKey() {
        return encryptionKey;
    }

    public void setSSNCipher(String SSNCipher) { this.SSNCipher = SSNCipher; }

    public int getAge(){
        LocalDate today = LocalDate.now();
        LocalDate birthday = LocalDate.of(this.dateOfBirth.getYear(), this.dateOfBirth.getMonth(), this.dateOfBirth.getDay());
        Period p = Period.between(birthday, today);

        return p.getYears();
    }

    public Date getDateOfBirth() {
        return dateOfBirth;
    }

    public String getDateOfBirthFormatted() {
        return (new SimpleDateFormat("MM/dd/yyy").format(dateOfBirth));
    }

    public void setDateOfBirth(Date dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }
}
