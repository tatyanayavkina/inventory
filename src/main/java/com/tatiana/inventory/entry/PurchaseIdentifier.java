package com.tatiana.inventory.entry;


public class PurchaseIdentifier {
    private Integer resourceId;
    private String clientEmail;

    public PurchaseIdentifier(Integer resourceId, String clientEmail) {
        this.resourceId = resourceId;
        this.clientEmail = clientEmail;
    }

    public Integer getResourceId() {
        return resourceId;
    }

    public void setResourceId(Integer resourceId) {
        this.resourceId = resourceId;
    }

    public String getClientEmail() {
        return clientEmail;
    }

    public void setClientEmail(String clientEmail) {
        this.clientEmail = clientEmail;
    }


}
