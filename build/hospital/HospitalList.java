package hospital;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicInteger;
import javax.xml.bind.annotation.XmlElement; 
import javax.xml.bind.annotation.XmlElementWrapper; 
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "hospitalList")
public class HospitalList {
    private List<Doctor> doctorList; 
    private AtomicInteger docId;
    private AtomicInteger patId;
    
    public HospitalList() { 
	doctorList = new CopyOnWriteArrayList<Doctor>(); 
	docId = new AtomicInteger();
	patId = new AtomicInteger();
    }

    @XmlElement 
    @XmlElementWrapper(name = "hospital") 
    public List<Doctor> getHospitalList() { 
	return this.doctorList;
    } 
    public void setHospitalList(List<Doctor> preds) { 
	this.doctorList = preds;
    }

    @Override
    public String toString() {
	String s = "";
	for (Doctor p : doctorList) s += p.toString();
	return s;
    }

    public Doctor find(int id) {
	Doctor pred = null;
	// Search the list -- for now, the list is short enough that
	// a linear search is ok but binary search would be better if the
	// list got to be an order-of-magnitude larger in size.
	for (Doctor p : doctorList) {
	    if (p.getId() == id) {
		pred = p;
		break;
	    }
	}	
	return pred;
    }
    
    public Doctor find(String docName) {
    	Doctor pred = null;
    	// Search the list -- for now, the list is short enough that
    	// a linear search is ok but binary search would be better if the
    	// list got to be an order-of-magnitude larger in size.
    	for (Doctor p : doctorList) {
    	    if (p.getDoctorName().equals(docName)) {
    		pred = p;
    		break;
    	    }
    	}	
    	return pred;
        }
    
    public int add(String doctorName, List<Patient> patientList) {
    List<Patient> patientListWithId = new ArrayList<>();
	int docId1 = docId.incrementAndGet();
	int patId1 = patId.incrementAndGet();
	Doctor d = new Doctor();
	d.setId(docId1);
	d.setDoctorName(doctorName);
	for(Patient patients : patientList){	
	Patient p = new Patient();
	p.setPatientName(patients.getPatientName());
	p.setInsNum(patients.getInsNum());
	p.setId(patId1);
	patId1 = patId.incrementAndGet();
	patientListWithId.add(p);
	}
	d.setPatientName(patientListWithId);
	doctorList.add(d);
	return docId1;
    }
}