package net.java.naved.Controller;
import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import jakarta.servlet.http.HttpServletResponse;
import net.sf.jasperreports.engine.JREmptyDataSource;
import net.sf.jasperreports.engine.JRExporter;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.export.ooxml.JRXlsxExporter;
import net.sf.jasperreports.export.SimpleExporterInput;
import net.sf.jasperreports.export.SimpleOutputStreamExporterOutput;
@Controller
public class ReportController {

//    @Value("${jasper.report.template}")

    @GetMapping("/")
    public String showForm(Model model) {
        return "form"; // Return the name of your HTML form template
    }

    @PostMapping("/generate-report")
    public void generateReport(@RequestParam("jsonData") String jsonData, HttpServletResponse response) {
    	System.out.println("Received JSON data: " + jsonData);
    	
    	try {
    	JSONParser jsonparse=new JSONParser();
		JSONArray jsonArray=(JSONArray) jsonparse.parse(jsonData);
		List<JsonModel> list = new ArrayList<>();
		
		for (Object obj : jsonArray) {
	        JSONObject jsonObject = (JSONObject) obj;

	        String date = (String) jsonObject.get("Date");
	        String name = (String) jsonObject.get("StudentName");
	        String descc = (String) jsonObject.get("Description");
	        Long amt = (Long) jsonObject.get("Amount");
	        String category = (String) jsonObject.get("Category");
	        Long bal = (Long) jsonObject.get("Balance");

	        JsonModel student = new JsonModel(date, name, descc, amt, category, bal);
	        list.add(student);
	    }
		
		for (JsonModel student : list) {
            System.out.println("Date: " + student.getDate());
            System.out.println("Name: " + student.getName());
            System.out.println("Description: " + student.getDescc());
            System.out.println("Amount: " + student.getAmt());
            System.out.println("Category: " + student.getCategory());
            System.out.println("Balance: " + student.getBal());
            System.out.println();
        }
		
		String filePath="C:\\Users\\ssyed\\Documents\\workspace-spring-tool-suite-4-4.20.0.RELEASE\\WebApp\\src\\main\\resources\\exc.jrxml";
		
		JRBeanCollectionDataSource dataSource=new JRBeanCollectionDataSource(list);
		
		Map<String,Object> parameters=new HashMap<String,Object>();
		parameters.put("TableData",dataSource);
		
		JasperReport report=JasperCompileManager.compileReport(filePath);
		
		JasperPrint print=JasperFillManager.fillReport(report,parameters,new JREmptyDataSource());
		
		JasperExportManager.exportReportToPdfFile(print,"C:\\Users\\ssyed\\OneDrive\\Desktop\\SCOREME\\web.pdf");
		//PDF EXPORT
		response.setContentType("application/pdf");
        response.setHeader("Content-Disposition", "attachment; filename=web.pdf");

        // Write the PDF data to the response output stream
        JasperExportManager.exportReportToPdfStream(print, response.getOutputStream());

        // Flush and close the response output stream
        response.getOutputStream().flush();
        response.getOutputStream().close();
        
        
		JRXlsxExporter exporter=new JRXlsxExporter();
		exporter.setExporterInput(new SimpleExporterInput(print));
		exporter.setExporterOutput(new SimpleOutputStreamExporterOutput(new FileOutputStream(new File("C:\\Users\\ssyed\\OneDrive\\Desktop\\SCOREME\\Pro.xlsx"))));
		
		//exporter.exportReport();
		
		response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
	    response.setHeader("Content-Disposition", "attachment; filename=pro.xlsx");
	    exporter.exportReport();
	        // Create an exporter for Excel format
	   // JRExporter exporter = new JRXlsxExporter();

	        // Configure exporter (optional)
/*	    exporter.setParameter(JRXlsExporter.JASPER_PRINT, print);
	    exporter.setParameter(JRXlsExporter.OUTPUT_STREAM, response.getOutputStream());
	    exporter.setParameter(JRXlsExporter.IS_ONE_PAGE_PER_SHEET, Boolean.FALSE); // You can customize export options

	        // Export the report to Excel format
	    exporter.exportReport();

	        // Flush and close the response output stream
	    response.getOutputStream().flush();
	    response.getOutputStream().close();*/
		
    	}catch(Exception e) {
			System.out.println("Exception while creating report");
		}

    }
        
    }
