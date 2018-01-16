package org.sms.project.elasticsearch.option;

import org.elasticsearch.search.sort.SortOrder;

/**
 * class_descriptions:
 * @author Sunny
 * @since 1.8.0
 */
public class SortOption {

  private String field;
  private SortOrder order;

  public static final String JSON_FIELD_FIELD = "field";
  public static final String JSON_FIELD_ORDER = "order";

  public SortOption() {
  }

  public SortOption(SortOption other) {
    field = other.getField();
    order = other.getOrder();
  }

  public String getField() {
    return field;
  }

  public SortOption setField(String field) {
    this.field = field;
    return this;
  }

  public SortOrder getOrder() {
    return order;
  }

  public SortOption setOrder(SortOrder order) {
    this.order = order;
    return this;
  }
}