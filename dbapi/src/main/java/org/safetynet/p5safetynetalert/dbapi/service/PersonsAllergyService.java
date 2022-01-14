package org.safetynet.p5safetynetalert.dbapi.service;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.safetynet.p5safetynetalert.dbapi.model.entity.Person;
import org.safetynet.p5safetynetalert.dbapi.model.entity.PersonsAllergy;
import org.safetynet.p5safetynetalert.dbapi.repository.PersonsAllergyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class PersonsAllergyService {

  private static final Logger LOGGER = LogManager.getLogger(PersonsAllergyService.class);
  @Autowired
  private PersonsAllergyRepository personsAllergyRepository;

  /**
   * This method creates a list of allergy strings from a persons medications Iterable.
   *
   * @param personsAllergies is the person's allergies Iterable
   * @return see description.
   */
  public List<String> getAllergiesFromPersonsMedications(
      Iterable<PersonsAllergy> personsAllergies) {
    LOGGER.debug("Getting persons' allergies...");
    List<String> allergies = new ArrayList<>();
    for(PersonsAllergy personsAllergy : personsAllergies){
      allergies.add(personsAllergy.getAllergy().getName());
    }
    LOGGER.debug("Persons' allergies got.");
    return allergies;
  }

  public void delete(Iterable<PersonsAllergy> personsAllergies) {
    personsAllergyRepository.deleteAll(personsAllergies);
  }

  public PersonsAllergy save(PersonsAllergy personsAllergy) {
    return personsAllergyRepository.save(personsAllergy);
  }

  public Iterable<PersonsAllergy> getAllFromPerson(Person person) {
    return personsAllergyRepository.findAllByPerson(person);
  }

  public void deleteAllFromPerson(Person person) {
    Iterable<PersonsAllergy> personsAllergiesToDelete = getAllFromPerson(person);
    personsAllergyRepository.deleteAll(personsAllergiesToDelete);
  }
}
