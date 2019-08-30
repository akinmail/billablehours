package com.akinmail.billablehours;

import com.akinmail.billablehours.service.CsvService;
import dto.InvoiceDto;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.net.URL;
import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
public class CsvServiceTests {

    @InjectMocks
    CsvService csvService;

    @Test
    public void shouldGenerateInvoice() throws Exception {
        FileInputStream fileInputStream = new FileInputStream(getFileFromResources("TestFile.csv"));
        List<InvoiceDto> invoiceDtoList = csvService.processInputFile(fileInputStream);
        assert invoiceDtoList.size()==3;
    }

    @Test
    public void shouldSkipMissingRowsInCsvFile() throws Exception{
        FileInputStream fileInputStream = new FileInputStream(getFileFromResources("MissingRowTestFile.csv"));
        List<InvoiceDto> invoiceDtoList = csvService.processInputFile(fileInputStream);
        assert invoiceDtoList.size()==3;
    }

    @Test(expected = NumberFormatException.class)
    public void shouldThrowExceptionOnWrongDataTypeInFields() throws Exception{
        FileInputStream fileInputStream = new FileInputStream(getFileFromResources("WrongDataTypeTestFile.csv"));
        List<InvoiceDto> invoiceDtoList = csvService.processInputFile(fileInputStream);
    }

    @Test
    public void shouldRegularizeDateTime(){
        String date1 = csvService.regularizeTimeString("17:00");
        assert date1.equalsIgnoreCase("17:00:00");
        String date2 = csvService.regularizeTimeString("9:00");
        assert date2.equalsIgnoreCase("09:00:00");
        String date3 = csvService.regularizeTimeString("15:05");
        assert date3.equalsIgnoreCase("15:05:00");

    }

    // get file from classpath, resources folder
    private File getFileFromResources(String fileName) {

        ClassLoader classLoader = getClass().getClassLoader();

        URL resource = classLoader.getResource(fileName);
        if (resource == null) {
            throw new IllegalArgumentException("file is not found!");
        } else {
            return new File(resource.getFile());
        }

    }
}
