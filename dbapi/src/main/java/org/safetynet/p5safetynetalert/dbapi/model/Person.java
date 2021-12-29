package org.safetynet.p5safetynetalert.dbapi.model;

import lombok.Data;
import lombok.extern.apachecommons.CommonsLog;

import javax.persistence.*;

@Entity
@Table(name = "persons")
public class Person {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;

  private String firstName;

  private String lastName;

  private String birthDate;

  private String phone;

  private String email;

  @ManyToOne(fetch = FetchType.LAZY)
  private Address address;

  public Person() {};

  public Person(String firstName, String lastName, String birthDate,
                String phone, String email, Address address) {
    this.firstName = firstName;
    this.lastName = lastName;
    this.birthDate = birthDate;
    this.phone = phone;
    this.email = email;
    this.address = address;
  }

  public void setId(Integer id) {
    this.id = id;
  }

  public void setFirstName(String firstName) {
    this.firstName = firstName;
  }

  public void setLastName(String lastName) {
    this.lastName = lastName;
  }

  public void setBirthDate(String birthDate) {
    this.birthDate = birthDate;
  }

  public void setPhone(String phone) {
    this.phone = phone;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public void setAddress(Address address) {
    this.address = address;
  }

  public Integer getId() {
    return id;
  }

  public String getFirstName() {
    return firstName;
  }

  public String getLastName() {
    return lastName;
  }

  public String getBirthDate() {
    return birthDate;
  }

  public String getPhone() {
    return phone;
  }

  public String getEmail() {
    return email;
  }

  public Address getAddress() {
    return address;
  }
}
