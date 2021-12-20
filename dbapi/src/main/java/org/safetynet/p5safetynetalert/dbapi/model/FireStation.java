package org.safetynet.p5safetynetalert.dbapi.model;

import lombok.Data;

import javax.persistence.*;

@Data
@Entity
@Table(name="fire_stations")
public class FireStation {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;

  private Integer number;

  public FireStation() {};

  public FireStation(Integer number) {
    this.number = number;
  }
}
