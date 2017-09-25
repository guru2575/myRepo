package hospital;

import javax.xml.bind.annotation.XmlRootElement;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;

@XmlRootElement(name = "doctor")
public class Doctor implements Comparable<Doctor> {
	private String doctorName;
	private List<Patient> patientList;   // person
	private int  id;

    public Doctor() { }
    
    @Override
    public String toString() {
	String s = doctorName +" -- ";
	for (Patient p : patientList) s += p.toString();
	return s;
    }

    //** properties
    public void setPatientName(List<Patient> patientList) {
	this.patientList = patientList;
    }
    
    @XmlElement
    public List<Patient> getPatientName() {
	return this.patientList;
    }
    
    @XmlElement
    public String getDoctorName() {
		return doctorName;
	}

	public void setDoctorName(String doctorName) {
		this.doctorName = doctorName;
	}
	
	 public void setId(int id) {
			this.id = id;
		    }
		    @XmlElement
		    public int getId() {
			return this.id;
		    }
		 // implementation of Comparable interface
		    public int compareTo(Doctor other) {
			return this.id - other.id;
		    }	
}