package org.safetynet.p5safetynetalert.dbapi.service;

import lombok.Data;
import org.safetynet.p5safetynetalert.dbapi.model.entity.PersonsMedication;
import org.safetynet.p5safetynetalert.dbapi.repository.PersonsMedicationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Data
@Service
public class PersonsMedicationService {

  @Autowired
  private PersonsMedicationRepository personsMedicationRepository;

  public List<String> getMedicationsFromPersonsMedications(
      Iterable<PersonsMedication> personsMedications) {

    List<String> medications = new ArrayList<>();

    for(PersonsMedication personsMedication : personsMedications){
      medications.add(personsMedication.getMedication().getName());
    }

    return medications;
  }
  
}
