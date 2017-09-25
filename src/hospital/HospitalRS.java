package hospital;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.DELETE;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.FormParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.servlet.ServletContext;
import com.fasterxml.jackson.databind.ObjectMapper;

@Path("/")
public class HospitalRS {
    @Context 
    private static ServletContext sctx;          // dependency injection
    public static void setSctx(ServletContext sctx) {
		HospitalRS.sctx = sctx;
	}

	private static HospitalList plist; // set in populate()

    public HospitalRS() { }

    @GET
    @Path("/xml")
    @Produces({MediaType.APPLICATION_XML}) 
    public Response getXml() {
	checkContext();
	return Response.ok(plist, "application/xml").build();
    }

    @GET
    @Path("/xml/{id: \\d+}")
    @Produces({MediaType.APPLICATION_XML}) // could use "application/xml" instead
    public Response getXml(@PathParam("id") int id) {
	checkContext();
	return toRequestedType(id, "application/xml");
    }

    @GET
    @Produces({MediaType.APPLICATION_JSON})
    @Path("/json")
    public Response getJson() {
	checkContext();
	return Response.ok(toJson(plist), "application/json").build();
    }

    @GET    
    @Produces({MediaType.APPLICATION_JSON})
    @Path("/json/{id: \\d+}")
    public Response getJson(@PathParam("id") int id) {
	checkContext();
	return toRequestedType(id, "application/json");
    }

    @GET
    @Path("/plain")
    @Produces({MediaType.TEXT_PLAIN}) 
    public  String getPlain() {
	checkContext();
	return plist.toString();
    }

    @POST
    @Produces({MediaType.TEXT_PLAIN})
    @Path("/create")
    public Response create(@FormParam("doctorName") String doctorName, 
			   @FormParam("patients") List<String> patientList) {
	checkContext();
	String msg = null;
	// Require both properties to create.
	if (doctorName == null || patientList == null) {
	    msg = "Property 'doctorName' or 'patientList' is missing.\n";
	    return Response.status(Response.Status.BAD_REQUEST).
		                                   entity(msg).
		                                   type(MediaType.TEXT_PLAIN).
		                                   build();
	}	    
	// Otherwise, create the Prediction and add it to the collection.
	ArrayList<Patient> patients = new ArrayList<Patient>();
	for(String patient : patientList){
	String[] interest = patient.split("!");
	Patient p = new Patient();
	p.setPatientName(interest[0]);
	p.setInsNum(interest[1]);
	patients.add(p);
	}
	int id = addDoctor(doctorName, patients);
	msg = "Doctor "+ doctorName + " with Patients: " + patientList ;
	return Response.ok(msg, "text/plain").build();
    }

    @PUT
    @Produces({MediaType.TEXT_PLAIN})
    @Path("/update")
    public Response update(@FormParam("id") int id,
			   @FormParam("doctorName") String docName, 
			   @FormParam("patients") List<String> patientList) {
	checkContext();

	// Check that sufficient data are present to do an edit.
	String msg = null;
	if (docName == null && patientList == null) 
	    msg = "Neither who nor what is given: nothing to edit.\n";

	Doctor p = plist.find(id);
	if (p == null)
	    msg = "There is no prediction with ID " + id + "\n";

	if (msg != null)
	    return Response.status(Response.Status.BAD_REQUEST).
		                                   entity(msg).
		                                   type(MediaType.TEXT_PLAIN).
		                                   build();
	// Update.
	if (docName != null){ 
	p.setDoctorName(docName);
	}
	
	msg = "DrPatientList has been updated.\n";
	return Response.ok(msg, "text/plain").build();
    }

    @DELETE
    @Produces({MediaType.TEXT_PLAIN})
    @Path("/delete/{id: \\d+}")
    public Response delete(@PathParam("id") int id) {
	checkContext();
	String msg = null;
	Doctor p = plist.find(id);
	if (p == null) {
	    msg = "There is no Doctor with ID " + id + ". Cannot delete.\n";
	    return Response.status(Response.Status.BAD_REQUEST).
		                                   entity(msg).
		                                   type(MediaType.TEXT_PLAIN).
		                                   build();
	}
	plist.getHospitalList().remove(p);
	msg = "Doctor " + id + " deleted.\n";

	return Response.ok(msg, "text/plain").build();
    }

    //** utilities
    private  void checkContext() {
	if (plist == null) populate();
    }

	private  void populate() {
	plist = new HospitalList();

	String filename = "/WEB-INF/data/drs.db";
	InputStream in = sctx.getResourceAsStream(filename);
	
	// Read the data into the array of Predictions. 
	if (in != null) {
	    try {
		BufferedReader reader = new BufferedReader(new InputStreamReader(in));
		String record = null;
		List<Patient> patientList = getPatientList();
		while ((record = reader.readLine()) != null) {
			List<Patient> specificDocPatientList = new ArrayList<>();
			String[] parts = record.split("!");
			for(int i=0; i< Integer.parseInt(parts[1]); i++){
			 specificDocPatientList.add(patientList.get(i));
			}
			patientList = patientList.subList(Integer.parseInt(parts[1]), patientList.size());
		    addDoctor(parts[0], specificDocPatientList);
		}
	    }
	    catch (Exception e) { 
		throw new RuntimeException("I/O failed!"); 
	    }
	}
	}
	
	private  ArrayList<Patient> getPatientList(){
		String filename = "/WEB-INF/data/patients.db";
		InputStream in = sctx.getResourceAsStream(filename);
		ArrayList<Patient> patientList = new ArrayList<>();
		if (in != null) {
		    try {
			BufferedReader reader = new BufferedReader(new InputStreamReader(in));
			String record = null;
			while ((record = reader.readLine()) != null) {
				String[] parts = record.split("!");
				Patient p = new Patient();
				p.setPatientName(parts[0]);
				p.setInsNum(parts[1]);
				patientList.add(p);
				}
		    }catch (Exception e) { 
				throw new RuntimeException("I/O failed!"); 
		    }
		}
		return patientList;
	}

    // Add a new prediction to the list.
    private  int addDoctor(String doctorName, List<Patient> patientList) {
	int id = plist.add(doctorName, patientList);
	return id;
    }

    // Prediction --> JSON document
    private String toJson(Doctor prediction) {
	String json = "If you see this, there's a problem.";
	try {
	    json = new ObjectMapper().writeValueAsString(prediction);
	}
	catch(Exception e) { }
	return json;
    }

    // PredictionsList --> JSON document
    private String toJson(HospitalList plist) {
	String json = "If you see this, there's a problem.";
	try {
	    json = new ObjectMapper().writeValueAsString(plist);
	}
	catch(Exception e) { }
	return json;
    }

    // Generate an HTTP error response or typed OK response.
    private Response toRequestedType(int id, String type) {
	Doctor pred = plist.find(id);
	if (pred == null) {
	    String msg = id + " is a bad ID.\n";
	    return Response.status(Response.Status.BAD_REQUEST).
		                                   entity(msg).
		                                   type(MediaType.TEXT_PLAIN).
		                                   build();
	}
	else if (type.contains("json"))
	    return Response.ok(toJson(pred), type).build();
	else
	    return Response.ok(pred, type).build(); // toXml is automatic
    }
    
   /* public static void main(String[] args) {
		update(2, "upData", null);
	}*/
    
}



