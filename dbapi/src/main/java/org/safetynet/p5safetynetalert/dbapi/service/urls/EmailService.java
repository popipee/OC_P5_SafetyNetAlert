package org.safetynet.p5safetynetalert.dbapi.service.urls;

import org.safetynet.p5safetynetalert.dbapi.model.entity.Address;
import org.safetynet.p5safetynetalert.dbapi.model.dto.EmailListDTO;
import org.safetynet.p5safetynetalert.dbapi.service.AddressService;
import org.safetynet.p5safetynetalert.dbapi.service.PersonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collection;

@Service
public class EmailService {

  @Autowired
  AddressService addressService;
  @Autowired
  PersonService personService;

  /**
   * This method returns a list of each email of each inhabitant of a city encapsulated
   * in an EmailListDTO Object.
   *
   * @param city is a String containing the city where emails are extracted from.
   * @return An EmailListDTO object, see description
   */
  public EmailListDTO getAllEmailFromCityInhabitants(String city) {
    EmailListDTO emailListDTO = new EmailListDTO();
    Collection<Address> addresses = addressService.getAllByCity(city);
    if (addresses == null || addresses.size()==0) {
      return null;
    } else {
      emailListDTO.setEmailList(
        personService.getEmails(
          addressService.getPersons(
            addresses)));
      emailListDTO.setCityName(city);

      return emailListDTO;
    }
  }

}