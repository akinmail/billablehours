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
    private static final String COMMA = ",";
    Logger logger = LoggerFactory.getLogger(CsvService.class);
    DateTimeFormatter employeeTimeformatter = DateTimeFormatter.ofPattern("HH:mm:ss");

    public List<InvoiceDto> processInputFile(FileInputStream inputFileStream) {
        Map<String, List<EmployeeBillDto>> groupedBillsByCompany = null;
        List<InvoiceDto> invoiceDtoList = new ArrayList<>();
        try{
            BufferedReader br = new BufferedReader(new InputStreamReader(inputFileStream));
            groupedBillsByCompany = br.lines()
                    // skip the header of the csv
                    .skip(1)
                    // convert to pojo
                    .map(mapToEmployeeDto)
                    // group rows by project name into a map using the project name as keys of the map
                    .collect(groupingBy(EmployeeBillDto::getProject));

            AtomicInteger index = new AtomicInteger();

            invoiceDtoList = groupedBillsByCompany.entrySet().stream()
                    .map(l->{
                        l.getValue().stream().collect(Collectors.toMap(EmployeeBillDto::getId, Function.identity(), (EmployeeBillDto employeeBillDto1, EmployeeBillDto employeeBillDto2) -> {
                            employeeBillDto1.setNoOfHours(employeeBillDto1.getNoOfHours() + employeeBillDto2.getNoOfHours());
                            employeeBillDto1.setCost(employeeBillDto1.getNoOfHours() * employeeBillDto1.getUnitPrice());
                            //signal employee2 for deletion in next filter pass
                            //TODO filter out those signalled for deletion
                            employeeBillDto2.setId(Integer.MIN_VALUE);
                            return employeeBillDto1;
                        }));
                        return l;

                    })
                    .map(l->{
                        InvoiceDto invoiceDto = new InvoiceDto();
                        invoiceDto.setCompanyName(l.getKey());
                        invoiceDto.setEmployeeBills(l.getValue());
                        index.getAndIncrement();
                        return invoiceDto;
                    }).collect(Collectors.toList());


            br.close();
        } catch (Exception e) {
            logger.error("An error occurred while reading the file " + e);
        }
        return invoiceDtoList ;
    }

    private Function<String, EmployeeBillDto> mapToEmployeeDto = (line) -> {
        String[] p = line.split(COMMA);

        EmployeeBillDto employeeBillDto = new EmployeeBillDto();
        employeeBillDto.setId(Integer.parseInt(p[0]));
        LocalTime startTime = LocalTime.parse(regularizeTimeString(p[4]), employeeTimeformatter);
        LocalTime endTime = LocalTime.parse(regularizeTimeString(p[5]), employeeTimeformatter);
        //get difference in time
        long hoursDifference = Duration.between(startTime, endTime).toHours();
        employeeBillDto.setNoOfHours(hoursDifference);
        employeeBillDto.setUnitPrice(Long.parseLong(p[1]));
        employeeBillDto.setCost(Long.parseLong(p[1]) * hoursDifference);
        employeeBillDto.setProject(p[2]);

        return employeeBillDto;
    };

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
