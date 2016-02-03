import java.util.LinkedList;

/**
 * Created by inuya on 1/26/2016.
 */
public class MedicalRecord {
    double BMI;
    int    upperBloodPressure;
    int    lowerBloodPressure;
    double weight;
    double temperature;
    int    age;
    double heightInCentimeters;
    boolean isSmoker;
    String primaryInsurance; // To be moved into Billing
    String preferredPharmacy;
    LinkedList <String>reasonForVisit;
    LinkedList <String>allergies;
    LinkedList <String>diagnosis;

    String splitter = ":BREAK:";

    /**
     * Standard constructor. This constructor takes ALL of the possible entries for a Medical Record, whether they are
     * empty or not does not matter, just make sure that each entry is filled by something. If it is an empty number
     * replace with 0, if it is a String or LinkedList pass null for empty values.
     * @param BMI
     * Body Mass Index, a double that will represent this number calculated by the program
     * @param upperBloodPressure
     * The upper blood pressure read from the blood pressure reader (integer)
     * @param lowerBloodPressure
     * The lower blood pressure read from the blood pressure monitor (integer)
     * @param weight
     * The patient's weight in Pounds (double)
     * @param temperature
     * The patient's temperature in degrees F
     * @param age
     * The patient's age in years old
     * @param heightInCentimeters
     * The patient's Height in Centimeters
     * @param isSmoker
     * Boolean, is the patient a smoker? (True = yes or False = no)
     * @param primaryInsurance
     * String representing the patient's Insurance Company Name
     * @param preferredPharmacy
     * String representing the patient's Preferred Pharamacy Location
     * @param reasonForVisit
     * String Array (or linked list) that states all of the reasons that the patient has come in for
     * @param allergies
     * String Array (or linked list) that states all of the patient's known allergies
     * @param diagnosis
     * String Array (or linked list) that states all of the concluding diagnosis of the visit
     **/
    public MedicalRecord(double BMI, int upperBloodPressure, int lowerBloodPressure, double weight, double temperature, int age, double heightInCentimeters, boolean isSmoker, String primaryInsurance, String preferredPharmacy, LinkedList<String> reasonForVisit, LinkedList<String> allergies, LinkedList<String> diagnosis) {
        this.BMI                = BMI;
        this.upperBloodPressure = upperBloodPressure;
        this.lowerBloodPressure = lowerBloodPressure;
        this.weight             = weight;
        this.temperature        = temperature;
        this.age                = age;
        this.heightInCentimeters = heightInCentimeters;
        this.isSmoker           = isSmoker;
        this.primaryInsurance   = primaryInsurance;
        this.preferredPharmacy  = preferredPharmacy;
        this.reasonForVisit     = reasonForVisit;
        this.allergies          = allergies;
        this.diagnosis          = diagnosis;
    }

    public MedicalRecord(double BMI, int upperBloodPressure, int lowerBloodPressure, double weight, double temperature, int age, double heightInCentimeters, boolean isSmoker, String primaryInsurance, String preferredPharmacy, String[] reasonForVisit, String[] allergies, String[] diagnosis) {
        this.BMI                = BMI;
        this.upperBloodPressure = upperBloodPressure;
        this.lowerBloodPressure = lowerBloodPressure;
        this.weight             = weight;
        this.temperature        = temperature;
        this.age                = age;
        this.heightInCentimeters = heightInCentimeters;
        this.isSmoker           = isSmoker;
        this.primaryInsurance   = primaryInsurance;
        this.preferredPharmacy  = preferredPharmacy;

        setReasonForVisit(reasonForVisit);
        setAllergies(allergies);
        setDiagnosis(diagnosis);
    }

    public MedicalRecord() {

    }

    public double getBMI() {
        return BMI;
    }

    public void setBMI(double BMI) {
        this.BMI = BMI;
    }

    public int getUpperBloodPressure() {
        return upperBloodPressure;
    }

    public void setUpperBloodPressure(int upperBloodPressure) {
        this.upperBloodPressure = upperBloodPressure;
    }

    public int getLowerBloodPressure() {
        return lowerBloodPressure;
    }

    public void setLowerBloodPressure(int lowerBloodPressure) {
        this.lowerBloodPressure = lowerBloodPressure;
    }

    public double getWeight() {
        return weight;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }

    public double getTemperature() {
        return temperature;
    }

    public void setTemperature(double temperature) {
        this.temperature = temperature;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public double getHeightInCentimeters() {
        return heightInCentimeters;
    }

    public void setHeightInCentimeters(double heightInCentimeters) {
        this.heightInCentimeters = heightInCentimeters;
    }

    public boolean isSmoker() {
        return isSmoker;
    }

    public void setIsSmoker(boolean isSmoker) {
        this.isSmoker = isSmoker;
    }

    public String getPrimaryInsurance() {
        return primaryInsurance;
    }

    public void setPrimaryInsurance(String primaryInsurance) {
        this.primaryInsurance = primaryInsurance;
    }

    public String getPreferredPharmacy() {
        return preferredPharmacy;
    }

    public void setPreferredPharmacy(String preferredPharmacy) {
        this.preferredPharmacy = preferredPharmacy;
    }

    public LinkedList<String> getReasonForVisit() {
        return reasonForVisit;
    }

    public void setReasonForVisit(LinkedList<String> reasonForVisit) {
        this.reasonForVisit = reasonForVisit;
    }

    public void setReasonForVisit(String[] reasonForVisit){ for (String reason : reasonForVisit) this.reasonForVisit.add(reason); }

    public LinkedList<String> getAllergies() {
        return allergies;
    }

    public void setAllergies(LinkedList<String> allergies) { this.allergies = allergies; }

    public void setAllergies(String[] allergies){
        for (String allergy : allergies) this.allergies.add(allergy);
    }

    public LinkedList<String> getDiagnosis() { return diagnosis; }

    public void setDiagnosis(LinkedList<String> diagnosis) {
        this.diagnosis = diagnosis;
    }

    public void setDiagnosis(String[] diagnosis){
        for (String diag : diagnosis) this.diagnosis.add(diag);
    }

    @Override
    public String toString() {
        LinkedList<String> ret = new LinkedList<String>();
        ret.add(String.valueOf(this.BMI));
        ret.add(String.valueOf(this.upperBloodPressure));
        ret.add(String.valueOf(this.lowerBloodPressure));
        ret.add(String.valueOf(this.weight));
        ret.add(String.valueOf(this.temperature));
        ret.add(String.valueOf(this.age));
        ret.add(String.valueOf(this.heightInCentimeters));
        ret.add(String.valueOf(this.isSmoker));
        ret.add(this.primaryInsurance);
        ret.add(this.preferredPharmacy);
        ret.add(this.reasonForVisit.toString());
        ret.add(this.allergies.toString());
        ret.add(this.diagnosis.toString());
        return ret.toString();
    }

    public byte[] getReasonForVisitBytes() {
        String s = "";
        for(String T: reasonForVisit){
            s += s + T + splitter;
        }

        return s.getBytes();
    }

    public byte[] getDiagnosisBytes() {
        String s = "";
        for(String T: diagnosis){
            s+= s + T + splitter;
        }

        return s.getBytes();
    }

    public byte[] getAllergiesBytes() {
        String s = "";
        for(String T: allergies){
            s += s + T + splitter;
        }

        return s.getBytes();
    }
}
