package dto;

public class EmployeeBillDto {
    private int id;
    private long noOfHours;
    private long unitPrice;
    private long cost;
    private String project;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public long getNoOfHours() {
        return noOfHours;
    }

    public void setNoOfHours(long noOfHours) {
        this.noOfHours = noOfHours;
    }

    public long getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(long unitPrice) {
        this.unitPrice = unitPrice;
    }

    public long getCost() {
        return cost;
    }

    public void setCost(long cost) {
        this.cost = cost;
    }

    public String getProject() {
        return project;
    }

    public void setProject(String project) {
        this.project = project;
    }
}
