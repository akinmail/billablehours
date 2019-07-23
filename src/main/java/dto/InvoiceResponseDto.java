package dto;

import java.util.ArrayList;
import java.util.List;

public class InvoiceResponseDto {
    private List<InvoiceDto> companyInvoiceDtos;


    public InvoiceResponseDto() {
        this.companyInvoiceDtos = new ArrayList<>();
    }

    public List<InvoiceDto> getCompanyInvoiceDtos() {
        return companyInvoiceDtos;
    }

    public void setCompanyInvoiceDtos(List<InvoiceDto> companyInvoiceDtos) {
        this.companyInvoiceDtos = companyInvoiceDtos;
    }
}
