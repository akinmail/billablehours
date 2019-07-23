package dto;

import java.util.ArrayList;
import java.util.List;

public class InvoiceDto {
    private String companyName;
    List<EmployeeBillDto> employeeBills;

    public InvoiceDto() {
        this.employeeBills = new ArrayList<EmployeeBillDto>();
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public List<EmployeeBillDto> getEmployeeBills() {
        return employeeBills;
    }

    public void setEmployeeBills(List<EmployeeBillDto> employeeBills) {
        this.employeeBills = employeeBills;
    }
}
