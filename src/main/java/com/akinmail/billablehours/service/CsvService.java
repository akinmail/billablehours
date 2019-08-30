package com.akinmail.billablehours.service;

import dto.EmployeeBillDto;
import dto.InvoiceDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.*;
import java.time.Duration;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.groupingBy;

@Service
public class CsvService {
    //The delimeter in a csv file is a comma
    private static final String COMMA = ",";
    //Create an instance of the Logger
    Logger logger = LoggerFactory.getLogger(CsvService.class);
    //The data includes a time field in this format
    DateTimeFormatter employeeTimeformatter = DateTimeFormatter.ofPattern("HH:mm:ss");

    /**
     * Accepts a FileInputStream object which represents an existing connection to an
     * actual file in the file system.
     * <p>
     * Using functional programming methods like map(), collect(), groupingBy(), it performs stream operations on the data
     * without having to load all the data in memory at the same time. This gives a significant memory usage improvement.
     *
     * <p>
     * If <code>inputFileStream</code> is null then a <code>NullPointerException</code>
     * is thrown.
     * <p>
     *
     *
     * @param      inputFileStream   the file to be opened for reading.
     * @throws     Exception      throws some of the Subclasses of Exception such as NumberFormatException and IoException.
     */
    public List<InvoiceDto> processInputFile(FileInputStream inputFileStream) throws Exception {
        //assert inputFileStream not null
        if (inputFileStream == null) {
            throw new NullPointerException("Input file stream cannot be null");
        }
        //The list to contain our final invoices per company
        List<InvoiceDto> invoiceDtoList = new ArrayList<>();
        //A map of groupings of the bills by company
        Map<String, List<EmployeeBillDto>> groupedBillsByCompany = null;
        try {
            //Buffer the inputstream for faster reading
            BufferedReader br = new BufferedReader(new InputStreamReader(inputFileStream));
            groupedBillsByCompany = br.lines()
                    // skip the header of the csv
                    .skip(1)
                    .filter(m->!m.isEmpty())
                    // convert to pojo
                    .map(mapToEmployeeDto)
                    // group rows by project name into a map using the project name as keys of the map
                    .collect(groupingBy(EmployeeBillDto::getProject));

            invoiceDtoList = groupedBillsByCompany.entrySet().stream()
                    //for each of the companies
                    .map(l -> {
                        //combine/reduce every pair that has the same id i.e its the same employee working on different days
                        l.getValue().stream().collect(Collectors.toMap(EmployeeBillDto::getId, Function.identity(), (EmployeeBillDto employeeBillDto1, EmployeeBillDto employeeBillDto2) -> {
                            employeeBillDto1.setNoOfHours(employeeBillDto1.getNoOfHours() + employeeBillDto2.getNoOfHours());
                            employeeBillDto1.setCost(employeeBillDto1.getNoOfHours() * employeeBillDto1.getUnitPrice());
                            //signal employee2 for deletion in next filter pass
                            employeeBillDto2.setId(Integer.MIN_VALUE);
                            return employeeBillDto1;
                        }));
                        return l;

                    })
                    .map(l -> {
                        //filter out those signalled for deletion i.e the duplicates
                        List<EmployeeBillDto> filteredList = l.getValue().stream()
                                .filter(employeeBillDto -> employeeBillDto.getId() != Integer.MIN_VALUE)
                                .collect(Collectors.toList());
                        l.setValue(filteredList);
                        return l;
                    })
                    //Map to the response data type
                    .map(l -> {
                        InvoiceDto invoiceDto = new InvoiceDto();
                        invoiceDto.setCompanyName(l.getKey());
                        invoiceDto.setEmployeeBills(l.getValue());
                        return invoiceDto;
                    }).collect(Collectors.toList()); //convert to a list


            br.close();
        }catch (NumberFormatException e){
            logger.error("The input file has mismatched data types, Please update it " + e);
            throw (e);
        }
         catch (Exception e) {
            logger.error("An error occurred while reading the file " + e);
             throw (e);
        }
        return invoiceDtoList ;
    }

    /**
     * Helper method to convert a line into the Java object representation
     * @param      line   Each non empty line of the input file.
     */
    private Function<String, EmployeeBillDto> mapToEmployeeDto = (line) -> {
        String[] p = line.split(COMMA);

        EmployeeBillDto employeeBillDto = new EmployeeBillDto();
        employeeBillDto.setId(Integer.parseInt(p[0])); //read id field
        LocalTime startTime = LocalTime.parse(regularizeTimeString(p[4]), employeeTimeformatter); //read start time field
        LocalTime endTime = LocalTime.parse(regularizeTimeString(p[5]), employeeTimeformatter); // read end time field
        //get difference in time
        long hoursDifference = Duration.between(startTime, endTime).toHours();
        employeeBillDto.setNoOfHours(hoursDifference);
        employeeBillDto.setUnitPrice(Long.parseLong(p[1])); //read unit price field
        employeeBillDto.setCost(Long.parseLong(p[1]) * hoursDifference); // read cost field
        employeeBillDto.setProject(p[2]); // read company name field

        return employeeBillDto;
    };

    /**
     * Custom date time regularizer based on the expected format
     * @param   dateString   The datestring to regularize.
     */
    private String regularizeTimeString(String dateString){
        StringBuilder stringBuilder = new StringBuilder();
        String[] sections = dateString.split(":");
        //pad left conditionally
        if(sections[0].length() == 1){
            stringBuilder.append("0");
            stringBuilder.append(sections[0]);
            stringBuilder.append(":");
        }else {
            stringBuilder.append(sections[0]);
            stringBuilder.append(":");
        }
        stringBuilder.append(sections[1]);
        stringBuilder.append(":");
        stringBuilder.append("00");
        return stringBuilder.toString();
    }
}
