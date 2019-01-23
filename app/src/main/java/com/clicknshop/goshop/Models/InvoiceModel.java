package com.clicknshop.goshop.Models;

public class InvoiceModel {
    String invoiceId;
    OrderModel order;
    long time;

    public InvoiceModel(String invoiceId, OrderModel order, long time) {
        this.invoiceId = invoiceId;
        this.order = order;
        this.time = time;
    }

    public String getInvoiceId() {
        return invoiceId;
    }

    public void setInvoiceId(String invoiceId) {
        this.invoiceId = invoiceId;
    }

    public OrderModel getOrder() {
        return order;
    }

    public void setOrder(OrderModel order) {
        this.order = order;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public InvoiceModel() {

    }
}
