package myob.payslip.utils;

import myob.payslip.domain.Employee;
import myob.payslip.domain.Payslip;
import org.apache.commons.csv.CSVRecord;
import org.junit.Test;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class CsvMungerTest {

    // TODO: inject this via spring
    private String validFilePath = "/Users/bcrosby/workspace/myobExercise/src/test/resources/csv/valid-input.csv";
    private String invalidFilePath = "./valid-input.csv";

    @Test
    public void testSuccessCsvRead() throws IOException{
        int expectedRecordsCount  = 3;
        int expectedCSVRecordSize = 5;

        List<CSVRecord> csvRecords = CsvMunger.readCsv(validFilePath);

        // Ensure we have the correct number of rows
        assertEquals("Unexpected Number of records", expectedRecordsCount, csvRecords.size());

        for(CSVRecord csvRecord : csvRecords) {
            assertEquals("Unexpected CSVRecord size at record " + csvRecord.getRecordNumber(),
                expectedCSVRecordSize, csvRecord.size());
        }
    }

    @Test
    public void testFailureCsvRead_UnableToFindInputFile() {

        try {
            CsvMunger.readCsv(invalidFilePath);
            fail("Expected Exception not thrown");
        } catch (IOException e) {
            assertTrue("Expected FileNotFoundException not thrown", e instanceof FileNotFoundException);
        }
    }

    @Test
    public void testSuccessCsvWrite() throws IOException, ParseException {

        // TODO: put it under resources
        String outputFilePath = "./test-output.csv";

        String expectedHeader0 = "name";
        String expectedHeader1 = "pay period";
        String expectedHeader2 = "gross income";
        String expectedHeader3 = "income tax";
        String expectedHeader4 = "net income";
        String expectedHeader5 = "super";

        String expectedRow1Column0 = "firstNameA lastNameA";
        String expectedRow1Column1 = "01 January - 31 January";
        String expectedRow1Column2 = "40000";
        String expectedRow1Column3 = "100";
        String expectedRow1Column4 = "30000";
        String expectedRow1Column5 = "50";

        int expectedRecordsCount  = 2;
        int expectedCSVRecordSize = 6;

        CsvMunger.writeCsv(employeeListCreator(), outputFilePath);

        List<CSVRecord> csvRecords = CsvMunger.readCsv(outputFilePath);

        // Ensure we have the correct number of rows
        assertEquals("Unexpected Number of records", expectedRecordsCount, csvRecords.size());

        for(CSVRecord csvRecord : csvRecords) {
            assertEquals("Unexpected CSVRecord size at record " + csvRecord.getRecordNumber(),
                    expectedCSVRecordSize, csvRecord.size());
        }
        CSVRecord headers = csvRecords.get(0) ;
        assertEquals("Unexpected header at index 0", expectedHeader0, headers.get(0));
        assertEquals("Unexpected header at index 1", expectedHeader1, headers.get(1));
        assertEquals("Unexpected header at index 2", expectedHeader2, headers.get(2));
        assertEquals("Unexpected header at index 3", expectedHeader3, headers.get(3));
        assertEquals("Unexpected header at index 4", expectedHeader4, headers.get(4));
        assertEquals("Unexpected header at index 5", expectedHeader5, headers.get(5));

        CSVRecord row1 = csvRecords.get(1) ;
        assertEquals("Unexpected value at index 0 of row 1", expectedRow1Column0, row1.get(0));
        assertEquals("Unexpected value at index 1 of row 1", expectedRow1Column1, row1.get(1));
        assertEquals("Unexpected value at index 2 of row 1", expectedRow1Column2, row1.get(2));
        assertEquals("Unexpected value at index 3 of row 1", expectedRow1Column3, row1.get(3));
        assertEquals("Unexpected value at index 4 of row 1", expectedRow1Column4, row1.get(4));
        assertEquals("Unexpected value at index 5 of row 1", expectedRow1Column5, row1.get(5));
    }

    private List<Employee> employeeListCreator() throws ParseException {

        Employee employeeA = new Employee();

        List<Employee> employees = new ArrayList<Employee>();

        employeeA.setFirstName("firstNameA");
        employeeA.setLastName("lastNameA");
        employeeA.setSuperRate(new BigDecimal(0.9));
        employeeA.setAnnualSalary(new BigDecimal(50000));

        DateFormat formatter = new SimpleDateFormat("dd/MM/yy");
        Date startDate = formatter.parse("01/01/15");
        Date endDate = formatter.parse("31/01/15");

        Payslip payslipA1 = new Payslip();
        payslipA1.setStartDate(startDate);
        payslipA1.setEndDate(endDate);
        payslipA1.setGrossIncome(new BigDecimal(40000));
        payslipA1.setIncomeSuper(new BigDecimal(50));
        payslipA1.setNetIncome(new BigDecimal(30000));
        payslipA1.setIncomeTax(new BigDecimal(100));

        employeeA.getPayslips().add(payslipA1);
        employees.add(employeeA);

        return employees;
    }

}