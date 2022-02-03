package org.safetynet.p5safetynetalert.dbapi.model.dto;

import java.util.Collection;
import java.util.Objects;

import lombok.EqualsAndHashCode;
import lombok.Getter;

@Getter
public class PersonsFromFireStationDTO {
  private String title = "List of persons covered by a fire station.";
  private Integer adultCount;
  private Integer childrenCount;
  private Collection<PersonDTO> personsList;

  public void setAdultCount(Integer adultCount) {
    this.adultCount = adultCount;
  }

  public void setChildrenCount(Integer childrenCount) {
    this.childrenCount = childrenCount;
  }

  public void setPersonsList(Collection<PersonDTO> personsList) {
    this.personsList = personsList;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    PersonsFromFireStationDTO that = (PersonsFromFireStationDTO) o;
    return Objects.equals(title, that.title) && Objects.equals(adultCount, that.adultCount) && Objects.equals(childrenCount, that.childrenCount) && Objects.equals(personsList, that.personsList);
  }
}
