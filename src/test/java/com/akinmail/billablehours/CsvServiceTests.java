package com.akinmail.billablehours;

import com.akinmail.billablehours.service.CsvService;
import dto.InvoiceDto;
import org.junit.Assert;
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
    public void shouldGenerateInvoice() throws FileNotFoundException {
        FileInputStream fileInputStream = new FileInputStream(getFileFromResources("testfile.csv"));
        List<InvoiceDto> invoiceDtoList = csvService.processInputFile(fileInputStream);
        assert invoiceDtoList.size()==3;
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