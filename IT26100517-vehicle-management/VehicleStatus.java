package com.vehiclerental.vehicle;


public enum VehicleStatus {

    AVAILABLE("Available"),
    RENTED("On rent"),
    MAINTENANCE("In maintenance");

    private final String label;

    VehicleStatus(String label) {       
        this.label = label;
    }

    public String getLabel() {
        return label;
    }

  
    public static VehicleStatus fromText(String text) {
        for (VehicleStatus s : values()) {
            if (s.name().equalsIgnoreCase(text)) {
                return s;
            }
        }
        throw new com.vehiclerental.common.ValidationException("Unknown vehicle status: " + text);
    }
}
