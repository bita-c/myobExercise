package myob.payslip.impl;

import myob.payslip.Exception.PayslipProcessingException;
import myob.payslip.domain.Employee;
import myob.payslip.domain.Payslip;
import myob.payslip.utils.CsvMunger;
import myob.payslip.utils.EmployeeFactory;
import myob.payslip.utils.PayslipCalculator;
import org.apache.commons.csv.CSVRecord;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class PayslipProcessor {

    private static final Logger LOGGER = Logger.getLogger(PayslipProcessor.class);

    /**
     * Takes a path to a csv input file containing information to process and generate
     * payslip information for employees and writes a row per employee to the output csv file
     * to which the path has been passed
     *
     * @param inputCsvPath
     * @param outputCsvPath
     */
    public static void processPayslips(String inputCsvPath, String outputCsvPath) {

        List<Employee>  employees = new ArrayList<Employee>();
        List<CSVRecord> csvRecords;
        try {
            csvRecords = CsvMunger.readCsv(inputCsvPath);

        } catch(IOException e ) {
            LOGGER.error("Unable to read from input file: " + inputCsvPath);
            throw new PayslipProcessingException(e.getMessage());
        }

        for (CSVRecord record : csvRecords) {
            employees.add(EmployeeFactory.createEmployee(record));
        }

        // process each employee and calculate payslip values
        for (Employee employee : employees) {
            for (Payslip payslip : employee.getPayslips()) {
                payslip.setGrossIncome(PayslipCalculator.calculateGrossIncome(employee.getAnnualSalary()));
                payslip.setIncomeTax(PayslipCalculator.calculateIncomeTax(employee.getAnnualSalary()));
                payslip.setNetIncome(PayslipCalculator.calculateNetIncome(payslip.getGrossIncome(), payslip.getIncomeTax()));
                payslip.setIncomeSuper(PayslipCalculator.calculateSuper(employee.getSuperRate(), payslip.getGrossIncome()));
            }
        }

        try {
            CsvMunger.writeCsv(employees, outputCsvPath);

        } catch (IOException e) {
            LOGGER.error("Unable to write to output file: " + outputCsvPath);
            throw new PayslipProcessingException(e.getMessage());
        }
    }

    public static void main(String[] args){

        if (args.length != 1) {
            LOGGER.error("Need to provide input file path! ");
            System.exit(1);
        }

        String inputCsvPath = args[0];

        String outputCsvPath = "./output.csv";

        processPayslips(inputCsvPath, outputCsvPath);

        LOGGER.info("output is available under: " + outputCsvPath);
    }
}
