package com.vehiclerental.vehicle;

import java.time.Year;
import java.util.List;

import com.vehiclerental.common.FileHandler;
import com.vehiclerental.common.FileStorable;
import com.vehiclerental.common.RequestData;
import com.vehiclerental.common.ValidationException;


public abstract class Vehicle implements FileStorable {

    public static final String PLATE_PATTERN = "[A-Z]{2,3}-\\d{4}";

 
    public static final String IMAGE_PATTERN = "^(/images/[\\w/.-]+|https?://\\S+)\\.(jpg|jpeg|png|webp)$";

    private String id;
    private String brand;
    private String model;
    private int year;
    private String plateNumber;
    private double dailyRate;
    private int mileage;
    private VehicleStatus status;
    private String branchId;
    private String colour;
    private String fuelType;
    private String imageUrl;          

 
    protected Vehicle() {
        this.status = VehicleStatus.AVAILABLE;
        this.colour = "White";
        this.fuelType = "Petrol";
        this.imageUrl = "";
    }

    protected Vehicle(String id, String brand, String model, int year, String plateNumber,
                      double dailyRate, int mileage, String branchId, String colour, String fuelType) {
        this();
        this.id = id;
        setBrand(brand);
        setModel(model);
        setYear(year);
        setPlateNumber(plateNumber);
        setDailyRate(dailyRate);
        setMileage(mileage);
        setBranchId(branchId);
        setColour(colour);
        setFuelType(fuelType);
    }


    public abstract String getType();

    public abstract String getCategory();

    public abstract double calculateRentalCost(int days);


    public abstract List<String> getFeatures();

    protected abstract String[] extraFileFields();

    protected abstract void readExtraFields(String extra1, String extra2);

    public abstract void applyTypeDetails(RequestData data);

 
    public double calculateRentalCost(int days, double discountRate) {
        double cost = calculateRentalCost(days);    
        return Math.round(cost * (1 - discountRate) * 100.0) / 100.0;
    }

    public String getDisplayName() {
        return brand + " " + model + " (" + year + ")";
    }

    public boolean isAvailable() {
        return status == VehicleStatus.AVAILABLE;
    }

  
    protected void checkDays(int days) {
        if (days < 1) {
            throw new ValidationException("A rental must be at least 1 day");
        }
    }

    @Override
    public String toFileString() {
        String[] extra = extraFileFields();
        return FileHandler.join(getType(), id, brand, model, year, plateNumber, dailyRate, mileage,
                status.name(), branchId, colour, fuelType, extra[0], extra[1], imageUrl);
    }


    public static Vehicle fromFileString(String line) {
        String[] p = FileHandler.split(line);
        Vehicle v = createEmpty(p[0]);
        v.id = p[1];
        v.brand = p[2];
        v.model = p[3];
        v.year = Integer.parseInt(p[4]);
        v.plateNumber = p[5];
        v.dailyRate = Double.parseDouble(p[6]);
        v.mileage = Integer.parseInt(p[7]);
        v.status = VehicleStatus.valueOf(p[8]);
        v.branchId = p[9];
        v.colour = p[10];
        v.fuelType = p[11];
        v.readExtraFields(p[12], p[13]);
        v.imageUrl = p.length > 14 ? p[14] : "";     
        return v;
    }

    public static Vehicle createEmpty(String type) {
        switch (type == null ? "" : type.toUpperCase()) {
            case Car.TYPE:
                return new Car();
            case Van.TYPE:
                return new Van();
            case Motorcycle.TYPE:
                return new Motorcycle();
            default:
                throw new ValidationException("Vehicle type must be CAR, VAN or MOTORCYCLE");
        }
    }

  

    @Override
    public String getId() {
        return id;
    }

    void setId(String id) {
        this.id = id;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        if (brand == null || brand.trim().isEmpty()) {
            throw new ValidationException("Brand is required");
        }
        this.brand = brand.trim();
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        if (model == null || model.trim().isEmpty()) {
            throw new ValidationException("Model is required");
        }
        this.model = model.trim();
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        int now = Year.now().getValue();
        if (year < 1990 || year > now + 1) {
            throw new ValidationException("Year must be between 1990 and " + (now + 1));
        }
        this.year = year;
    }

    public String getPlateNumber() {
        return plateNumber;
    }

    public void setPlateNumber(String plateNumber) {
        String value = plateNumber == null ? "" : plateNumber.trim().toUpperCase().replace(' ', '-');
        if (!value.matches(PLATE_PATTERN)) {
            throw new ValidationException("Plate number must look like CAB-1234 (2-3 letters, dash, 4 digits)");
        }
        this.plateNumber = value;
    }

    public double getDailyRate() {
        return dailyRate;
    }

    public void setDailyRate(double dailyRate) {
        if (dailyRate <= 0) {
            throw new ValidationException("Daily rate must be more than 0");
        }
        this.dailyRate = dailyRate;
    }

    public int getMileage() {
        return mileage;
    }

    public void setMileage(int mileage) {
        if (mileage < 0) {
            throw new ValidationException("Mileage cannot be negative");
        }
        this.mileage = mileage;
    }

    public VehicleStatus getStatus() {
        return status;
    }

    public String getStatusLabel() {
        return status.getLabel();
    }

    public void setStatus(VehicleStatus status) {
        this.status = status;
    }

    public String getBranchId() {
        return branchId;
    }

    public void setBranchId(String branchId) {
        this.branchId = branchId == null ? "" : branchId.trim();
    }

    public String getColour() {
        return colour;
    }

    public void setColour(String colour) {
        this.colour = (colour == null || colour.trim().isEmpty()) ? "White" : colour.trim();
    }

    public String getFuelType() {
        return fuelType;
    }

    public void setFuelType(String fuelType) {
        this.fuelType = (fuelType == null || fuelType.trim().isEmpty()) ? "Petrol" : fuelType.trim();
    }

    public String getImageUrl() {
        return imageUrl;
    }


    public void setImageUrl(String imageUrl) {
        String value = imageUrl == null ? "" : imageUrl.trim();
        if (!value.isEmpty() && !value.toLowerCase().matches(IMAGE_PATTERN)) {
            throw new ValidationException("Photo must be an image path such as /images/vehicles/cars/toyota-prius.jpg");
        }
        this.imageUrl = value;
    }

    public boolean isHasPhoto() {
        return !imageUrl.isEmpty();
    }

    @Override
    public String toString() {
        return getType() + " " + getDisplayName() + " [" + plateNumber + "]";
    }
}
