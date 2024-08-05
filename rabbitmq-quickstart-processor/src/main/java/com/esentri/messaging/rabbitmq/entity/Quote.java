package com.esentri.messaging.rabbitmq.entity;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class Quote {

  public String id;
  public int price;
}
