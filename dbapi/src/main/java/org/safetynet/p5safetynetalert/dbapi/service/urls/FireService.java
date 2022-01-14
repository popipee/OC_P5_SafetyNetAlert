package org.safetynet.p5safetynetalert.dbapi.service.urls;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.safetynet.p5safetynetalert.dbapi.model.entity.Address;
import org.safetynet.p5safetynetalert.dbapi.model.dto.FireDTO;
import org.safetynet.p5safetynetalert.dbapi.service.AddressService;
import org.safetynet.p5safetynetalert.dbapi.service.PersonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class FireService {

  private static final Logger LOGGER = LogManager.getLogger(FireService.class);
  @Autowired
  private PersonService personService;
  @Autowired
  private AddressService addressService;

  /**
   * This method returns the list of inhabitants living at the given address as well as the number of
   * its associated fire station. The list includes name, phone number, age and background
   * (medications, dosage and allergies) for each person.
   *
   * @param road is the road of the address you want to recover the people from.
   * @return an FireDTO object containing fire station and people for this address. (see description)
   */
  public FireDTO getFireDTOFromAddressInFire(String road) {
    LOGGER.debug("FireDTO creation...");
    Address address = addressService.getByRoad(road);
    FireDTO fireDTO = new FireDTO();
    fireDTO.setPersonsList(
      personService.getPersonsForFireDTOFromAddressInFire(
        personService.getPersonsFromAddress(address))
    );
    fireDTO.setFireStationNumber(address.getFireStation().getNumber());
    LOGGER.debug("FireDTO properly created");
    return fireDTO;
  }
}
