package com.example.comp440proj.report;


import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import java.math.BigDecimal;
import java.sql.Date;


@JsonPropertyOrder({"id","title","description","price","username","category","created_at"})
public interface CatItemView {
    String getCategory();
    Long getId();
    String getTitle();
    String getDescription();
    BigDecimal getPrice();
    String getUsername();
    Date getCreated_at();
}
