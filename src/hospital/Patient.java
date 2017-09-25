package hospital;

import javax.xml.bind.annotation.XmlRootElement;

import javax.xml.bind.annotation.XmlElement;

@XmlRootElement(name = "patient")
public class Patient implements Comparable<Patient> {
    private String patientName;   // person
    private String insNum;  // his/her prediction
    private int  id;    // identifier used as lookup-key

    public Patient() { }

    @Override
    public String toString() {
	return String.format("%2d: ", id) + patientName + " ==> " + insNum +"\n" ;
    }
    
    //** properties
    public void setPatientName(String patientName) {
	this.patientName = patientName;
    }
    @XmlElement
    public String getPatientName() {
	return this.patientName;
    }

    public void setInsNum(String insNum) {
	this.insNum = insNum;
    }
    @XmlElement
    public String getInsNum() {
	return this.insNum;
    }

    public void setId(int id) {
	this.id = id;
    }
    @XmlElement
    public int getId() {
	return this.id;
    }

    // implementation of Comparable interface
    public int compareTo(Patient other) {
	return this.id - other.id;
    }	
}