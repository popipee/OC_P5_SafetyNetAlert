package org.safetynet.p5safetynetalert.dbapi.service;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.safetynet.p5safetynetalert.dbapi.model.dto.*;
import org.safetynet.p5safetynetalert.dbapi.model.entity.*;
import org.safetynet.p5safetynetalert.dbapi.model.initPersist.JsonMedicalRecord;
import org.safetynet.p5safetynetalert.dbapi.model.initPersist.JsonPerson;
import org.safetynet.p5safetynetalert.dbapi.repository.PersonRepository;
import org.safetynet.p5safetynetalert.dbapi.service.initPersist.IJsonDataInjectorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@SpringBootTest
public class PersonServiceTest {

  @Autowired
  private IPersonService iPersonService;
  @MockBean
  private IJsonDataInjectorService iJsonDataInjectorServiceMocked;
  @MockBean
  private PersonRepository personRepositoryMocked;
  @MockBean
  private AgeService ageServiceMocked;
  @MockBean
  private IPersonsMedicationService iPersonsMedicationServiceMocked;
  @MockBean
  private IPersonsAllergyService iPersonsAllergyServiceMocked;
  @MockBean
  private IMedicalRecordsService iMedicalRecordsServiceMocked;
  @MockBean
  private IAddressService iAddressServiceMocked;

  @Test
  public void getByFirstNameAndLastNameTest() {
    //given
    String firstName = "firstName";
    String lastName = "lastName";
    Person expectedPerson = new Person("firstName", "lastName", null, null, null, null);
    when(personRepositoryMocked.findByFirstNameAndLastName(firstName, lastName)).thenReturn(expectedPerson);
    //when
    Person result = iPersonService.getByFirstNameAndLastName(firstName, lastName);
    //then
    assertThat(result).isEqualTo(expectedPerson);
    verify(personRepositoryMocked, Mockito.times(1)).findByFirstNameAndLastName(firstName, lastName);
  }

  @Test
  public void getAllByNameTest() {
    //given
    String lastName = "lastName";
    Iterable<Person> expectedPersons = new ArrayList<>(
      List.of(
        new Person("joe", "lastname", null, null, null, null),
        new Person("june", "lastname", null, null, null, null)
      ));
    when(personRepositoryMocked.findAllByLastName(lastName)).thenReturn(expectedPersons);
    //when
    Iterable<Person> result = iPersonService.getAllByName(lastName);
    //then
    assertThat(result).isEqualTo(expectedPersons);
    verify(personRepositoryMocked, Mockito.times(1)).findAllByLastName(lastName);
  }

  @Test
  public void getEmailsTest() {
    //given
    Collection<Person> givenPersons = new ArrayList<>();
    givenPersons.add(new Person(null, null, null, null, "one mail", null));
    givenPersons.add(new Person(null, null, null, null, "another mail", null));
    givenPersons.add(new Person(null, null, null, null, "last mail", null));
    List<String> expected = new ArrayList<>(
      List.of(
        "one mail",
        "another mail",
        "last mail"
      )
    );
    //when
    Collection<String> results = iPersonService.getEmails(givenPersons);
    //then
    assertThat(results.containsAll(expected)).isTrue();
    assertThat(results.size()).isEqualTo(expected.size());
  }

  @Test
  public void getEmailsTestWithEmptyPersons() {
    //given
    Collection<Person> givenPersons = new ArrayList<>();
    //when
    Collection<String> results = iPersonService.getEmails(givenPersons);
    //then
    assertThat(results).isNull();
  }

  @Test
  public void getEmailsTestWithNullPersons() {
    //given
    Collection<Person> givenPersons = null;
    //when
    Collection<String> results = iPersonService.getEmails(givenPersons);
    //then
    assertThat(results).isNull();
  }

  @Test
  public void getPhonesTest() {
    //given
    Collection<Person> givenPersons = new ArrayList<>();
    givenPersons.add(new Person(null, null, null, "111", null, null));
    givenPersons.add(new Person(null, null, null, "222", null, null));
    givenPersons.add(new Person(null, null, null, "333", null, null));
    Collection<String> expected = new ArrayList<>(
      List.of(
        "111",
        "222",
        "333"
      )
    );
    //when
    Collection<String> results = iPersonService.getPhones(givenPersons);
    //then
    assertThat(results.containsAll(expected)).isTrue();
    assertThat(results.size()).isEqualTo(3);
  }

  @Test
  public void convertPersonsInPersonForFireDTOTest() {
    //given
    Collection<Person> givenPersons = new ArrayList<>();
    givenPersons.add(new Person("john", "doe", null, "111", null, null));
    givenPersons.add(new Person("june", "die", null, "222", null, null));
    givenPersons.add(new Person("ji", "lo", null, "333", null, null));

    List<MedicalRecordsDTO> medicalRecordsDTOList = new ArrayList<>();
    for (int i = 0; i < 3; i++) {
      medicalRecordsDTOList.add(new MedicalRecordsDTO());
    }

    int j = 0;
    for (Person person : givenPersons) {
      when(iMedicalRecordsServiceMocked.getMedicalRecords(person)).thenReturn(medicalRecordsDTOList.get(j));
      j++;
    }

    PersonForFireDTO expected1 = new PersonForFireDTO();
    expected1.setFirstName("john");
    expected1.setLastName("doe");
    expected1.setPhone("111");
    expected1.setMedicalRecords(medicalRecordsDTOList.get(0));
    PersonForFireDTO expected2 = new PersonForFireDTO();
    expected2.setFirstName("june");
    expected2.setLastName("die");
    expected2.setPhone("222");
    expected2.setMedicalRecords(medicalRecordsDTOList.get(1));
    PersonForFireDTO expected3 = new PersonForFireDTO();
    expected3.setFirstName("ji");
    expected3.setLastName("lo");
    expected3.setPhone("333");
    expected3.setMedicalRecords(medicalRecordsDTOList.get(2));

    //when
    Collection<PersonForFireDTO> results = iPersonService.convertPersonsInPersonForFireDTO(givenPersons);

    //then
    assertThat(results.size()).isEqualTo(3);
    assertThat(results.contains(expected1)).isTrue();
    assertThat(results.contains(expected2)).isTrue();
    assertThat(results.contains(expected3)).isTrue();
  }

  @Test
  public void getPersonDTOsFromAddressesTest() {
    //given
    Address givenAddress1 = new Address("rue rincais", null, null, null);
    Address givenAddress2 = new Address("rue racine", null, null, null);

    Person person1FromAddress1 = new Person("jules", null, null, null, null, givenAddress1);
    Person person2FromAddress1 = new Person("jack", null, null, null, null, givenAddress1);
    Person person1FromAddress2 = new Person("julie", null, null, null, null, givenAddress2);

    givenAddress1.setPersons(List.of(person1FromAddress1, person2FromAddress1));
    givenAddress2.setPersons(List.of(person1FromAddress2));

    Collection<Address> givenAddresses = new ArrayList<>(List.of(givenAddress1, givenAddress2));

    AddressDTO addressDTO1 = new AddressDTO("rue rincais", null, null);
    AddressDTO addressDTO2 = new AddressDTO("rue racine", null, null);
    Collection<PersonDTO> expectedPersonDTOs = new ArrayList<>(
      List.of(
        new PersonDTO("jules", null, null, null, addressDTO1),
        new PersonDTO("jack", null, null, null, addressDTO1),
        new PersonDTO("julie", null, null, null, addressDTO2)
      )
    );
    when(iAddressServiceMocked.convertAddressToAddressDTO(givenAddress1)).thenReturn(addressDTO1);
    when(iAddressServiceMocked.convertAddressToAddressDTO(givenAddress2)).thenReturn(addressDTO2);
    //when
    Collection<PersonDTO> result = iPersonService.getPersonDTOsFromAddresses(givenAddresses);
    //then
    assertThat(result).isEqualTo(expectedPersonDTOs);
  }

  @Test
  public void getPersonsForFloodTest() {
    //given
    //preparation
    Address givenAddress1 = new Address("rue rincais", null, null, null);
    Address givenAddress2 = new Address("rue racine", null, null, null);
    Person person1FromAddress1 = new Person("jules", null, null, null, null, givenAddress1);
    Person person2FromAddress1 = new Person("jack", null, null, null, null, givenAddress1);
    Person person1FromAddress2 = new Person("julie", null, null, null, null, givenAddress2);
    MedicalRecordsDTO medRecPerson1 = new MedicalRecordsDTO();
    MedicalRecordsDTO medRecPerson2 = new MedicalRecordsDTO();
    MedicalRecordsDTO medRecPerson3 = new MedicalRecordsDTO();
    givenAddress1.setPersons(List.of(person1FromAddress1, person2FromAddress1));
    givenAddress2.setPersons(List.of(person1FromAddress2));
    //given addresses
    Collection<Address> givenAddresses = new ArrayList<>(List.of(givenAddress1, givenAddress2));
    //expected result
    Collection<PersonForFloodDTO> expected = new ArrayList<>(
      List.of(
        new PersonForFloodDTO("jules", null, null, 0, medRecPerson1),
        new PersonForFloodDTO("jack", null, null, 0, medRecPerson2),
        new PersonForFloodDTO("julie", null, null, 0, medRecPerson3)
      )
    );
    //mocking
    when(iMedicalRecordsServiceMocked.getMedicalRecords(person1FromAddress1)).thenReturn(medRecPerson1);
    when(iMedicalRecordsServiceMocked.getMedicalRecords(person2FromAddress1)).thenReturn(medRecPerson2);
    when(iMedicalRecordsServiceMocked.getMedicalRecords(person1FromAddress2)).thenReturn(medRecPerson3);
    //when
    Collection<PersonForFloodDTO> result = iPersonService.getPersonsForFlood(givenAddresses);
    //then
    assertThat(result).isEqualTo(expected);
  }

  @Test
  public void getAdultsDTOTest() {
    //given
    Address givenAddress = new Address("rue jourdan", "nantes", "44000", null);
    Person person1 = new Person("jude", null, "adult", null, null, givenAddress);
    Person person2 = new Person("julia", null, "adult", null, null, givenAddress);
    Person person3 = new Person("maria", null, "child", null, null, givenAddress);
    givenAddress.setPersons(List.of(person1, person2, person3));

    AddressDTO expectedAddressDTO = new AddressDTO("rue jourdan", "nantes", "44000");
    Collection<PersonDTO> expected = new ArrayList<>(
      List.of(
        new PersonDTO("jude", null, null, "adult", expectedAddressDTO),
        new PersonDTO("julia", null, null, "adult", expectedAddressDTO)
      )
    );

    when(iAddressServiceMocked.convertAddressToAddressDTO(givenAddress)).thenReturn(expectedAddressDTO);
    when(ageServiceMocked.isStrictlyOverEighteen("adult")).thenReturn(true);
    when(ageServiceMocked.isStrictlyOverEighteen("child")).thenReturn(false);

    //when
    Collection<PersonDTO> result = iPersonService.getAdultsDTO(givenAddress);
    //then
    assertThat(result).isEqualTo(expected);
  }

  @Test
  public void getChildrenDTOTest() {
//given
    Address givenAddress = new Address("rue jourdan", "nantes", "44000", null);
    Person person1 = new Person("jude", null, "adult", null, null, givenAddress);
    Person person2 = new Person("julia", null, "adult", null, null, givenAddress);
    Person person3 = new Person("maria", null, "child", null, null, givenAddress);
    givenAddress.setPersons(List.of(person1, person2, person3));

    AddressDTO expectedAddressDTO = new AddressDTO("rue jourdan", "nantes", "44000");
    Collection<ChildDTO> expected = new ArrayList<>(
      List.of(
        new ChildDTO("maria", null, 3)
      )
    );

    when(iAddressServiceMocked.convertAddressToAddressDTO(givenAddress)).thenReturn(expectedAddressDTO);
    when(ageServiceMocked.isStrictlyOverEighteen("adult")).thenReturn(true);
    when(ageServiceMocked.isStrictlyOverEighteen("child")).thenReturn(false);
    when(ageServiceMocked.getAge("child")).thenReturn(3);

    //when
    Collection<ChildDTO> result = iPersonService.getChildrenDTO(givenAddress);
    //then
    assertThat(result).isEqualTo(expected);
  }

  @Test
  public void getPersonsFromAddressTest() {
    //given
    Address givenAddress = new Address("rue racine", "nantes", "44000", null);
    Person givenPerson1 = new Person("marion", null, null, null, null, givenAddress);
    Person givenPerson2 = new Person("julian", null, null, null, null, givenAddress);
    givenAddress.setPersons(List.of(givenPerson1, givenPerson2));
    Collection<Person> expected = new ArrayList<>(
      List.of(
        givenPerson1,
        givenPerson2
      )
    );
    //when
    Collection<Person> result = iPersonService.getPersonsFromAddress(givenAddress);
    //then
    assertThat(result).isEqualTo(expected);
  }

  @Test
  public void getPersonsFromAddressTestWithNullCollection() {
    //given
    Address givenAddress = new Address("rue racine", "nantes", "44000", null);
    givenAddress.setPersons(null);
    //when
    Collection<Person> result = iPersonService.getPersonsFromAddress(givenAddress);
    //then
    assertThat(result).isNull();
  }

  @Test
  public void getPersonsFromAddressesTest() {
    //given
    Address givenAddress1 = new Address("rue racine", "nantes", "44000", null);
    Address givenAddress2 = new Address("rue chave", "nantes", "44000", null);
    Person givenPerson1 = new Person("marion", null, null, null, null, givenAddress1);
    Person givenPerson2 = new Person("julian", null, null, null, null, givenAddress1);
    Person givenPerson3 = new Person("julian", null, null, null, null, givenAddress2);
    givenAddress1.setPersons(List.of(givenPerson1, givenPerson2));
    givenAddress2.setPersons(List.of(givenPerson3));

    Collection<Address> givenAddresses = new ArrayList<>();
    givenAddresses.add(givenAddress1);
    givenAddresses.add(givenAddress2);

    Collection<Person> expected = new ArrayList<>(
      List.of(
        givenPerson1,
        givenPerson2,
        givenPerson3
      )
    );
    //when
    Collection<Person> result = iPersonService.getPersonsFromAddresses(givenAddresses);
    //then
    assertThat(result).isEqualTo(expected);
  }

  @Test
  public void getPersonsFromAddressesTestNullAddressesCollection() {
    //given
    Collection<Address> givenAddresses = null;
    //when
    Collection<Person> result = iPersonService.getPersonsFromAddresses(givenAddresses);
    //then
    assertThat(result).isNull();
  }

  @Test
  public void getPersonsFromAddressesTestBlankAddressesCollection() {
    //given
    Collection<Address> givenAddresses = new ArrayList<>();
    //when
    Collection<Person> result = iPersonService.getPersonsFromAddresses(givenAddresses);
    //then
    assertThat(result).isNull();
  }

  @Test
  public void createPersonTestWithNotExistingAddress() {
    //given
    String givenFirstName = "jules";
    String givenLastName = "michou";
    JsonPerson givenJsonPerson = new JsonPerson(givenFirstName, givenLastName, "rue racine", "nantes", "44000", "123", "mail@mail.com");

    Address expectedAddress = new Address("rue racine", "nantes", "44000", null);

    when(personRepositoryMocked.existsByFirstNameAndLastName(givenFirstName, givenLastName)).thenReturn(false);
    when(iAddressServiceMocked.existsByRoadAndCityAndZipCode(expectedAddress)).thenReturn(false);

    //when
    JsonPerson result = iPersonService.createPerson(givenJsonPerson);
    //then
    verify(personRepositoryMocked, Mockito.times(1)).save(any(Person.class));
    verify(iAddressServiceMocked, Mockito.times(1)).save(any(Address.class));
    assertThat(result).isEqualTo(givenJsonPerson);
  }

  @Test
  public void createPersonTestWithExistingPerson() {
    //given
    String givenFirstName = "jules";
    String givenLastName = "michou";
    JsonPerson givenJsonPerson = new JsonPerson(givenFirstName, givenLastName, "rue racine", "nantes", "44000", "123", "mail@mail.com");

    when(personRepositoryMocked.existsByFirstNameAndLastName(givenFirstName, givenLastName)).thenReturn(true);
    //when
    JsonPerson result = iPersonService.createPerson(givenJsonPerson);
    //then
    verify(personRepositoryMocked, Mockito.times(0)).save(any(Person.class));
    assertThat(result).isNull();
  }

  @Test
  public void createPersonTestWithExistingAddress() {
    //given
    String givenFirstName = "jules";
    String givenLastName = "michou";
    JsonPerson givenJsonPerson = new JsonPerson(givenFirstName, givenLastName, "rue racine", "nantes", "44000", "123", "mail@mail.com");

    when(personRepositoryMocked.existsByFirstNameAndLastName(givenFirstName, givenLastName)).thenReturn(false);
    when(iAddressServiceMocked.existsByRoadAndCityAndZipCode(any(Address.class))).thenReturn(true);

    //when
    JsonPerson result = iPersonService.createPerson(givenJsonPerson);
    //then
    verify(personRepositoryMocked, Mockito.times(1)).save(any(Person.class));
    verify(iAddressServiceMocked, Mockito.times(0)).save(any(Address.class));
    verify(iAddressServiceMocked, Mockito.times(1)).getByRoadAndCityAndZipCode(any(Address.class));
    assertThat(result).isEqualTo(givenJsonPerson);
  }

  @Test
  public void createPersonTestWithBlankFirstName() {
    //given
    String givenFirstName = "";
    String givenLastName = "michou";
    JsonPerson givenJsonPerson = new JsonPerson(givenFirstName, givenLastName, "rue racine", "nantes", "44000", "123", "mail@mail.com");

    //when
    JsonPerson result = iPersonService.createPerson(givenJsonPerson);
    //then
    verify(personRepositoryMocked, Mockito.times(0)).save(any(Person.class));
    verify(iAddressServiceMocked, Mockito.times(0)).save(any(Address.class));
    verify(iAddressServiceMocked, Mockito.times(0)).getByRoadAndCityAndZipCode(any(Address.class));
    assertThat(result).isNull();
  }

  @Test
  public void createPersonTestWithBlankLastName() {
    //given
    String givenFirstName = "jules";
    String givenLastName = "";
    JsonPerson givenJsonPerson = new JsonPerson(givenFirstName, givenLastName, "rue racine", "nantes", "44000", "123", "mail@mail.com");

    //when
    JsonPerson result = iPersonService.createPerson(givenJsonPerson);
    //then
    verify(personRepositoryMocked, Mockito.times(0)).save(any(Person.class));
    verify(iAddressServiceMocked, Mockito.times(0)).save(any(Address.class));
    verify(iAddressServiceMocked, Mockito.times(0)).getByRoadAndCityAndZipCode(any(Address.class));
    assertThat(result).isNull();
  }

  @Test
  public void createPersonTestWithNullFirstName() {
    //given
    String givenFirstName = null;
    String givenLastName = "michou";
    JsonPerson givenJsonPerson = new JsonPerson(givenFirstName, givenLastName, "rue racine", "nantes", "44000", "123", "mail@mail.com");

    //when
    JsonPerson result = iPersonService.createPerson(givenJsonPerson);
    //then
    verify(personRepositoryMocked, Mockito.times(0)).save(any(Person.class));
    verify(iAddressServiceMocked, Mockito.times(0)).save(any(Address.class));
    verify(iAddressServiceMocked, Mockito.times(0)).getByRoadAndCityAndZipCode(any(Address.class));
    assertThat(result).isNull();
  }

  @Test
  public void createPersonTestWithNullLastName() {
    //given
    String givenFirstName = "jules";
    String givenLastName = null;
    JsonPerson givenJsonPerson = new JsonPerson(givenFirstName, givenLastName, "rue racine", "nantes", "44000", "123", "mail@mail.com");

    //when
    JsonPerson result = iPersonService.createPerson(givenJsonPerson);
    //then
    verify(personRepositoryMocked, Mockito.times(0)).save(any(Person.class));
    verify(iAddressServiceMocked, Mockito.times(0)).save(any(Address.class));
    verify(iAddressServiceMocked, Mockito.times(0)).getByRoadAndCityAndZipCode(any(Address.class));
    assertThat(result).isNull();
  }

  @Test
  public void updatePersonWithJsonPersonTestWithNotExistingPerson() {
    //given
    String givenFirstName = "emile";
    String givenLastName = "denis";
    JsonPerson givenJsonPerson = new JsonPerson(givenFirstName, givenLastName, null, null, null, null, null);
    when(personRepositoryMocked.existsByFirstNameAndLastName(givenFirstName, givenLastName)).thenReturn(false);
    //when
    JsonPerson result = iPersonService.updatePersonWithJsonPerson(givenJsonPerson);
    //then
    verify(personRepositoryMocked, Mockito.times(1)).existsByFirstNameAndLastName(givenFirstName, givenLastName);
    assertThat(result).isNull();
  }

  @Test
  public void updatePersonWithJsonPersonTest() {
    //given
    String givenFirstName = "emile";
    String givenLastName = "denis";
    JsonPerson givenJsonPerson = new JsonPerson(givenFirstName, givenLastName, "rue racine", "nantes", "44000", "123", "mail@mail.com");
    Address updatedAddress = new Address();
    Person foundPerson = new Person();
    when(personRepositoryMocked.existsByFirstNameAndLastName(givenFirstName, givenLastName)).thenReturn(true);
    when(personRepositoryMocked.findByFirstNameAndLastName(givenFirstName, givenLastName)).thenReturn(foundPerson);
    when(iAddressServiceMocked.existsByRoadAndCityAndZipCode(any(Address.class))).thenReturn(false);
    when(iAddressServiceMocked.save(any(Address.class))).thenReturn(updatedAddress);
    //when
    JsonPerson result = iPersonService.updatePersonWithJsonPerson(givenJsonPerson);
    //then
    verify(personRepositoryMocked, Mockito.times(1)).existsByFirstNameAndLastName(givenFirstName, givenLastName);
    verify(iAddressServiceMocked, Mockito.times(1)).existsByRoadAndCityAndZipCode(any(Address.class));
    verify(iAddressServiceMocked, Mockito.times(1)).save(any(Address.class));
    verify(personRepositoryMocked, Mockito.times(1)).save(foundPerson);
    assertThat(result).isEqualTo(givenJsonPerson);
  }

  @Test
  public void updatePersonWithJsonPersonTestWithExistingAddress() {
    //given
    String givenFirstName = "emile";
    String givenLastName = "denis";
    JsonPerson givenJsonPerson = new JsonPerson(givenFirstName, givenLastName, "rue racine", "nantes", "44000", "123", "mail@mail.com");
    Address updatedAddress = new Address(1, givenJsonPerson.getAddress(), givenJsonPerson.getCity(), givenJsonPerson.getZip(), null);
    Person foundPerson = new Person(givenFirstName, givenLastName, null, givenJsonPerson.getPhone(), givenJsonPerson.getEmail(), null);
    when(personRepositoryMocked.existsByFirstNameAndLastName(givenFirstName, givenLastName)).thenReturn(true);
    when(personRepositoryMocked.findByFirstNameAndLastName(givenFirstName, givenLastName)).thenReturn(foundPerson);
    when(iAddressServiceMocked.existsByRoadAndCityAndZipCode(any(Address.class))).thenReturn(true);
    when(iAddressServiceMocked.getByRoadAndCityAndZipCode(any(Address.class))).thenReturn(updatedAddress);
    //when
    JsonPerson result = iPersonService.updatePersonWithJsonPerson(givenJsonPerson);
    //then
    verify(personRepositoryMocked, Mockito.times(1)).existsByFirstNameAndLastName(givenFirstName, givenLastName);
    verify(iAddressServiceMocked, Mockito.times(1)).existsByRoadAndCityAndZipCode(any(Address.class));
    verify(iAddressServiceMocked, Mockito.times(0)).save(any(Address.class));
    verify(iAddressServiceMocked, Mockito.times(1)).getByRoadAndCityAndZipCode(any(Address.class));
    verify(personRepositoryMocked, Mockito.times(1)).save(foundPerson);
    assertThat(result).isEqualTo(givenJsonPerson);
  }

  @Test
  public void deleteTestWithNullFirstName() {
    //given
    String givenFirstName = null;
    String givenLastName = "denis";
    JsonPerson givenJsonPerson = new JsonPerson(givenFirstName, givenLastName, "rue racine", "nantes", "44000", "123", "mail@mail.com");
    //when
    JsonPerson result = iPersonService.delete(givenJsonPerson);
    //then
    verify(iPersonsAllergyServiceMocked, Mockito.times(0)).deleteAll(any(Iterable.class));
    verify(iPersonsMedicationServiceMocked, Mockito.times(0)).deleteAll(any(Iterable.class));
    verify(personRepositoryMocked, Mockito.times(0)).delete(any(Person.class));
    assertThat(result).isNull();
  }

  @Test
  public void deleteTestWithBlankFirstName() {
    //given
    String givenFirstName = "";
    String givenLastName = "denis";
    JsonPerson givenJsonPerson = new JsonPerson(givenFirstName, givenLastName, "rue racine", "nantes", "44000", "123", "mail@mail.com");
    //when
    JsonPerson result = iPersonService.delete(givenJsonPerson);
    //then
    verify(iPersonsAllergyServiceMocked, Mockito.times(0)).deleteAll(any(Iterable.class));
    verify(iPersonsMedicationServiceMocked, Mockito.times(0)).deleteAll(any(Iterable.class));
    verify(personRepositoryMocked, Mockito.times(0)).delete(any(Person.class));
    assertThat(result).isNull();
  }

  @Test
  public void deleteTestWithNullLastName() {
    //given
    String givenFirstName = "emile";
    String givenLastName = null;
    JsonPerson givenJsonPerson = new JsonPerson(givenFirstName, givenLastName, "rue racine", "nantes", "44000", "123", "mail@mail.com");
    //when
    JsonPerson result = iPersonService.delete(givenJsonPerson);
    //then
    verify(iPersonsAllergyServiceMocked, Mockito.times(0)).deleteAll(any(Iterable.class));
    verify(iPersonsMedicationServiceMocked, Mockito.times(0)).deleteAll(any(Iterable.class));
    verify(personRepositoryMocked, Mockito.times(0)).delete(any(Person.class));
    assertThat(result).isNull();
  }

  @Test
  public void deleteTestWithBlankLastName() {
    //given
    String givenFirstName = "emile";
    String givenLastName = "";
    JsonPerson givenJsonPerson = new JsonPerson(givenFirstName, givenLastName, "rue racine", "nantes", "44000", "123", "mail@mail.com");
    //when
    JsonPerson result = iPersonService.delete(givenJsonPerson);
    //then
    verify(iPersonsAllergyServiceMocked, Mockito.times(0)).deleteAll(any(Iterable.class));
    verify(iPersonsMedicationServiceMocked, Mockito.times(0)).deleteAll(any(Iterable.class));
    verify(personRepositoryMocked, Mockito.times(0)).delete(any(Person.class));
    assertThat(result).isNull();
  }

  @Test
  public void deleteTestWithNotExistingPerson() {
    //given
    String givenFirstName = "emile";
    String givenLastName = "denis";
    JsonPerson givenJsonPerson = new JsonPerson(givenFirstName, givenLastName, "rue racine", "nantes", "44000", "123", "mail@mail.com");
    when(personRepositoryMocked.existsByFirstNameAndLastName(givenFirstName, givenLastName)).thenReturn(false);
    //when
    JsonPerson result = iPersonService.delete(givenJsonPerson);
    //then
    verify(iPersonsAllergyServiceMocked, Mockito.times(0)).deleteAll(any(Iterable.class));
    verify(iPersonsMedicationServiceMocked, Mockito.times(0)).deleteAll(any(Iterable.class));
    verify(personRepositoryMocked, Mockito.times(0)).delete(any(Person.class));
    assertThat(result).isNull();
  }

  @Test
  public void deleteTest() {
    //given
    String givenFirstName = "emile";
    String givenLastName = "denis";
    JsonPerson givenJsonPerson = new JsonPerson(givenFirstName, givenLastName, "rue racine", "nantes", "44000", "123", "mail@mail.com");
    Collection<PersonsAllergy> givenPersonsAllergies = new ArrayList<>();
    Collection<PersonsMedication> givenPersonsMedication = new ArrayList<>();
    Person foundPersonToDelete = new Person(givenFirstName, givenLastName, null, givenJsonPerson.getPhone(), givenJsonPerson.getEmail(), null);
    foundPersonToDelete.setPersonsAllergies(givenPersonsAllergies);
    foundPersonToDelete.setPersonsMedications(givenPersonsMedication);
    when(personRepositoryMocked.existsByFirstNameAndLastName(givenFirstName, givenLastName)).thenReturn(true);
    when(personRepositoryMocked.findByFirstNameAndLastName(givenFirstName, givenLastName)).thenReturn(foundPersonToDelete);
    //when
    JsonPerson result = iPersonService.delete(givenJsonPerson);
    //then
    verify(iPersonsAllergyServiceMocked, Mockito.times(1)).deleteAll(givenPersonsAllergies);
    verify(iPersonsMedicationServiceMocked, Mockito.times(1)).deleteAll(givenPersonsMedication);
    verify(personRepositoryMocked, Mockito.times(1)).delete(foundPersonToDelete);
    assertThat(result).isEqualTo(givenJsonPerson);
  }

  @Test
  public void createMedicalRecordsTestWithNotExistingPerson() {
    //given
    String givenFirstName = "emile";
    String givenLastName = "denis";
    String givenBirthDate = "01/01/1965";
    List<String> givenMedications = new ArrayList<>();
    List<String> givenAllergies = new ArrayList<>();
    JsonMedicalRecord givenJsonMedicalRecord = new JsonMedicalRecord(givenFirstName, givenLastName, givenBirthDate, givenMedications,givenAllergies);
    when(personRepositoryMocked.existsByFirstNameAndLastName(givenFirstName, givenLastName)).thenReturn(false);
    //when
    JsonMedicalRecord result = iPersonService.createMedicalRecords(givenJsonMedicalRecord);
    //then
    verify(personRepositoryMocked,Mockito.times(1)).existsByFirstNameAndLastName(givenFirstName,givenLastName);
    verify(personRepositoryMocked,Mockito.times(0)).findByFirstNameAndLastName(givenFirstName,givenLastName);
    verify(iMedicalRecordsServiceMocked,Mockito.times(0)).getMedicalRecords(any(Person.class));
    verify(iMedicalRecordsServiceMocked,Mockito.times(0)).existsFromPerson(any(Person.class));
    verify(personRepositoryMocked,Mockito.times(0)).save(any(Person.class));
    verify(iMedicalRecordsServiceMocked,Mockito.times(0)).createMedicationsFromJsonPerson(any(JsonMedicalRecord.class),any(Person.class));
    verify(iMedicalRecordsServiceMocked,Mockito.times(0)).createAllergiesFromJsonPerson(any(JsonMedicalRecord.class),any(Person.class));
    assertThat(result).isNull();
  }

  @Test
  public void createMedicalRecordsTestWithAlreadyExistingMedicalRecord() {
    //given
    String givenFirstName = "emile";
    String givenLastName = "denis";
    String givenBirthDate = "01/01/1965";
    List<String> givenMedications = new ArrayList(List.of("someMed1","someMed2"));
    List<String> givenAllergies = new ArrayList(List.of("someAllergy1","someAllergy2"));
    MedicalRecordsDTO givenMedicalRecordDTO = new MedicalRecordsDTO();
    givenMedicalRecordDTO.setMedications(givenMedications);
    givenMedicalRecordDTO.setAllergies(givenAllergies);
    JsonMedicalRecord givenJsonMedicalRecord = new JsonMedicalRecord(givenFirstName, givenLastName, givenBirthDate, givenMedications,givenAllergies);
    Person foundPerson = new Person(givenFirstName, givenLastName, givenJsonMedicalRecord.getBirthdate(), "123", "mail@mail.com",null);

    when(personRepositoryMocked.existsByFirstNameAndLastName(givenFirstName, givenLastName)).thenReturn(true);
    when(personRepositoryMocked.findByFirstNameAndLastName(givenFirstName,givenLastName)).thenReturn(foundPerson);
    when(iMedicalRecordsServiceMocked.getMedicalRecords(foundPerson)).thenReturn(givenMedicalRecordDTO);
    when(iMedicalRecordsServiceMocked.existsFromPerson(foundPerson)).thenReturn(true);
    //when
    JsonMedicalRecord result = iPersonService.createMedicalRecords(givenJsonMedicalRecord);
    //then
    verify(personRepositoryMocked,Mockito.times(1)).existsByFirstNameAndLastName(givenFirstName,givenLastName);
    verify(personRepositoryMocked,Mockito.times(1)).findByFirstNameAndLastName(givenFirstName,givenLastName);
    verify(iMedicalRecordsServiceMocked,Mockito.times(2)).getMedicalRecords(foundPerson);
    verify(iMedicalRecordsServiceMocked,Mockito.times(1)).existsFromPerson(foundPerson);
    verify(personRepositoryMocked,Mockito.times(0)).save(any(Person.class));
    verify(iMedicalRecordsServiceMocked,Mockito.times(0)).createMedicationsFromJsonPerson(any(JsonMedicalRecord.class),any(Person.class));
    verify(iMedicalRecordsServiceMocked,Mockito.times(0)).createAllergiesFromJsonPerson(any(JsonMedicalRecord.class),any(Person.class));
    assertThat(result).isNull();
  }

  @Test
  public void createMedicalRecordsTestWithNonExistingMedicalRecord() {
    //given
    List<String> givenMedications = new ArrayList(List.of("someMed1","someMed2"));
    List<String> givenAllergies = new ArrayList(List.of("someAllergy1","someAllergy2"));

    List<String> existingMedications = new ArrayList<>();
    List<String> existingAllergies = new ArrayList<>();
    MedicalRecordsDTO existingMedicalRecordsDTO = new MedicalRecordsDTO();
    existingMedicalRecordsDTO.setMedications(existingMedications);
    existingMedicalRecordsDTO.setAllergies(existingAllergies);

    String givenFirstName = "emile";
    String givenLastName = "denis";
    String givenBirthDate = "01/01/1965";
    JsonMedicalRecord givenJsonMedicalRecord = new JsonMedicalRecord(givenFirstName, givenLastName, givenBirthDate, givenMedications,givenAllergies);
    Person foundPerson = new Person(givenFirstName, givenLastName, givenJsonMedicalRecord.getBirthdate(), "123", "mail@mail.com",null);

    when(personRepositoryMocked.existsByFirstNameAndLastName(givenFirstName, givenLastName)).thenReturn(true);
    when(personRepositoryMocked.findByFirstNameAndLastName(givenFirstName,givenLastName)).thenReturn(foundPerson);
    when(iMedicalRecordsServiceMocked.getMedicalRecords(foundPerson)).thenReturn(existingMedicalRecordsDTO);
    when(iMedicalRecordsServiceMocked.existsFromPerson(foundPerson)).thenReturn(false);
    //when
    JsonMedicalRecord result = iPersonService.createMedicalRecords(givenJsonMedicalRecord);
    //then
    verify(personRepositoryMocked,Mockito.times(1)).existsByFirstNameAndLastName(givenFirstName,givenLastName);
    verify(personRepositoryMocked,Mockito.times(2)).findByFirstNameAndLastName(givenFirstName,givenLastName);
    verify(iMedicalRecordsServiceMocked,Mockito.times(2)).getMedicalRecords(foundPerson);
    verify(iMedicalRecordsServiceMocked,Mockito.times(1)).existsFromPerson(foundPerson);
    verify(iMedicalRecordsServiceMocked,Mockito.times(1)).createMedicationsFromJsonPerson(givenJsonMedicalRecord,foundPerson);
    verify(iMedicalRecordsServiceMocked,Mockito.times(1)).createAllergiesFromJsonPerson(givenJsonMedicalRecord,foundPerson);
    assertThat(result).isEqualTo(givenJsonMedicalRecord);
  }

  @Test
  public void createMedicalRecordsTestWithExistingMedicalRecord() {
    //given

    List<String> existingMedications = new ArrayList<>(List.of("existingMed"));
    List<String> existingAllergies = new ArrayList<>(List.of("existingAllergy"));
    MedicalRecordsDTO existingMedicalRecordsDTO = new MedicalRecordsDTO();
    existingMedicalRecordsDTO.setMedications(existingMedications);
    existingMedicalRecordsDTO.setAllergies(existingAllergies);

    List<String> givenMedications = new ArrayList(List.of("someMed1","someMed2"));
    List<String> givenAllergies = new ArrayList(List.of("someAllergy1","someAllergy2"));

    String givenFirstName = "emile";
    String givenLastName = "denis";
    String givenBirthDate = "01/01/1965";
    JsonMedicalRecord givenJsonMedicalRecord = new JsonMedicalRecord(givenFirstName, givenLastName, givenBirthDate, givenMedications,givenAllergies);
    Person foundPerson = new Person(givenFirstName, givenLastName, givenJsonMedicalRecord.getBirthdate(), "123", "mail@mail.com",null);

    when(personRepositoryMocked.existsByFirstNameAndLastName(givenFirstName, givenLastName)).thenReturn(true);
    when(personRepositoryMocked.findByFirstNameAndLastName(givenFirstName,givenLastName)).thenReturn(foundPerson);
    when(iMedicalRecordsServiceMocked.getMedicalRecords(foundPerson)).thenReturn(existingMedicalRecordsDTO);
    when(iMedicalRecordsServiceMocked.existsFromPerson(foundPerson)).thenReturn(false);
    //when
    JsonMedicalRecord result = iPersonService.createMedicalRecords(givenJsonMedicalRecord);
    //then
    verify(personRepositoryMocked,Mockito.times(1)).existsByFirstNameAndLastName(givenFirstName,givenLastName);
    verify(personRepositoryMocked,Mockito.times(2)).findByFirstNameAndLastName(givenFirstName,givenLastName);
    verify(iMedicalRecordsServiceMocked,Mockito.times(2)).getMedicalRecords(foundPerson);
    verify(iMedicalRecordsServiceMocked,Mockito.times(1)).existsFromPerson(foundPerson);
    verify(iMedicalRecordsServiceMocked,Mockito.times(0)).createMedicationsFromJsonPerson(givenJsonMedicalRecord,foundPerson);
    verify(iMedicalRecordsServiceMocked,Mockito.times(0)).createAllergiesFromJsonPerson(givenJsonMedicalRecord,foundPerson);
    assertThat(result).isEqualTo(givenJsonMedicalRecord);
  }

  @Test
  public void updateMedicalRecordsTestWithNonExistingPerson() {
    //given
    List<String> givenMedications = new ArrayList(List.of("someMed1","someMed2"));
    List<String> givenAllergies = new ArrayList(List.of("someAllergy1","someAllergy2"));

    String givenFirstName = "emile";
    String givenLastName = "denis";
    String givenBirthDate = "01/01/1965";
    JsonMedicalRecord givenJsonMedicalRecord = new JsonMedicalRecord(givenFirstName, givenLastName, givenBirthDate, givenMedications,givenAllergies);
    Person foundPerson = new Person(givenFirstName, givenLastName, givenJsonMedicalRecord.getBirthdate(), "123", "mail@mail.com",null);

    when(personRepositoryMocked.existsByFirstNameAndLastName(givenFirstName, givenLastName)).thenReturn(false);
    //when
    JsonMedicalRecord result = iPersonService.updateMedicalRecords(givenJsonMedicalRecord);
    //then
    verify(personRepositoryMocked,Mockito.times(1)).existsByFirstNameAndLastName(givenFirstName, givenLastName);
    verify(iMedicalRecordsServiceMocked,Mockito.times(0)).deletePersonsMedicationsFromPerson(foundPerson);
    verify(iMedicalRecordsServiceMocked,Mockito.times(0)).createMedicationsFromJsonPerson(givenJsonMedicalRecord, foundPerson);
    verify(iMedicalRecordsServiceMocked,Mockito.times(0)).deletePersonsAllergiesFromPerson(foundPerson);
    verify(iMedicalRecordsServiceMocked,Mockito.times(0)).createAllergiesFromJsonPerson(givenJsonMedicalRecord, foundPerson);
    assertThat(result).isNull();
  }

  @Test
  public void updateMedicalRecordsTest() {
    //given
    List<String> givenMedications = new ArrayList(List.of("someMed1","someMed2"));
    List<String> givenAllergies = new ArrayList(List.of("someAllergy1","someAllergy2"));

    String givenFirstName = "emile";
    String givenLastName = "denis";
    String givenBirthDate = "01/01/1965";
    JsonMedicalRecord givenJsonMedicalRecord = new JsonMedicalRecord(givenFirstName, givenLastName, givenBirthDate, givenMedications,givenAllergies);
    Person foundPerson = new Person(givenFirstName, givenLastName, givenJsonMedicalRecord.getBirthdate(), "123", "mail@mail.com",null);

    when(personRepositoryMocked.existsByFirstNameAndLastName(givenFirstName, givenLastName)).thenReturn(true);
    when(personRepositoryMocked.findByFirstNameAndLastName(givenFirstName, givenLastName)).thenReturn(foundPerson);
    //when
    JsonMedicalRecord result = iPersonService.updateMedicalRecords(givenJsonMedicalRecord);
    //then
    verify(personRepositoryMocked,Mockito.times(1)).existsByFirstNameAndLastName(givenFirstName, givenLastName);
    verify(iMedicalRecordsServiceMocked,Mockito.times(1)).deletePersonsMedicationsFromPerson(foundPerson);
    verify(iMedicalRecordsServiceMocked,Mockito.times(1)).createMedicationsFromJsonPerson(givenJsonMedicalRecord, foundPerson);
    verify(iMedicalRecordsServiceMocked,Mockito.times(1)).deletePersonsAllergiesFromPerson(foundPerson);
    verify(iMedicalRecordsServiceMocked,Mockito.times(1)).createAllergiesFromJsonPerson(givenJsonMedicalRecord, foundPerson);
    assertThat(result).isEqualTo(givenJsonMedicalRecord);
  }

  @Test
  public void updateMedicalRecordsTestWithDifferenceInBirthDate() {
    //given
    List<String> givenMedications = new ArrayList(List.of("someMed1","someMed2"));
    List<String> givenAllergies = new ArrayList(List.of("someAllergy1","someAllergy2"));

    String givenFirstName = "emile";
    String givenLastName = "denis";
    String givenBirthDate = "01/01/1965";
    JsonMedicalRecord givenJsonMedicalRecord = new JsonMedicalRecord(givenFirstName, givenLastName, givenBirthDate, givenMedications,givenAllergies);
    Person foundPerson = new Person(givenFirstName, givenLastName, "someDiffBirthDate", "123", "mail@mail.com",null);
    Person savedPerson = new Person(givenFirstName, givenLastName, givenJsonMedicalRecord.getBirthdate(), "123", "mail@mail.com",null);

    when(personRepositoryMocked.existsByFirstNameAndLastName(givenFirstName, givenLastName)).thenReturn(true);
    when(personRepositoryMocked.findByFirstNameAndLastName(givenFirstName, givenLastName)).thenReturn(foundPerson);
    when(personRepositoryMocked.save(foundPerson)).thenReturn(savedPerson);
    //when
    JsonMedicalRecord result = iPersonService.updateMedicalRecords(givenJsonMedicalRecord);
    //then
    verify(personRepositoryMocked,Mockito.times(1)).existsByFirstNameAndLastName(givenFirstName, givenLastName);
    verify(personRepositoryMocked,Mockito.times(1)).save(foundPerson);
    verify(iMedicalRecordsServiceMocked,Mockito.times(1)).deletePersonsMedicationsFromPerson(savedPerson);
    verify(iMedicalRecordsServiceMocked,Mockito.times(1)).createMedicationsFromJsonPerson(givenJsonMedicalRecord, savedPerson);
    verify(iMedicalRecordsServiceMocked,Mockito.times(1)).deletePersonsAllergiesFromPerson(savedPerson);
    verify(iMedicalRecordsServiceMocked,Mockito.times(1)).createAllergiesFromJsonPerson(givenJsonMedicalRecord, savedPerson);
    assertThat(result).isEqualTo(givenJsonMedicalRecord);
  }

  @Test
  public void deleteMedicalRecordsTest() {
    //given
    List<String> givenMedications = new ArrayList(List.of("someMed1","someMed2"));
    List<String> givenAllergies = new ArrayList(List.of("someAllergy1","someAllergy2"));

    String givenFirstName = "emile";
    String givenLastName = "denis";
    String givenBirthDate = "01/01/1965";
    JsonMedicalRecord givenJsonMedicalRecord = new JsonMedicalRecord(givenFirstName, givenLastName, givenBirthDate, givenMedications,givenAllergies);
    Person foundPerson = new Person(givenFirstName, givenLastName, "01/01/1965", "123", "mail@mail.com",null);
    Person updatedPerson = new Person(givenFirstName, givenLastName, "", "123", "mail@mail.com",null);

    when(personRepositoryMocked.existsByFirstNameAndLastName(givenFirstName, givenLastName)).thenReturn(true);
    when(personRepositoryMocked.findByFirstNameAndLastName(givenFirstName, givenLastName)).thenReturn(foundPerson);
    when(personRepositoryMocked.save(foundPerson)).thenReturn(updatedPerson);
    //when
    JsonMedicalRecord result = iPersonService.deleteMedicalRecords(givenJsonMedicalRecord);
    //then
    verify(personRepositoryMocked,Mockito.times(1)).save(foundPerson);
    verify(iMedicalRecordsServiceMocked,Mockito.times(1)).deletePersonsMedicationsFromPerson(updatedPerson);
    verify(iMedicalRecordsServiceMocked,Mockito.times(1)).deletePersonsAllergiesFromPerson(updatedPerson);
    assertThat(result.getBirthdate()).isEqualTo("");
    assertThat(result.getMedications().size()).isEqualTo(0);
    assertThat(result.getAllergies().size()).isEqualTo(0);
  }

  @Test
  public void deleteMedicalRecordsTestWithNonExistingPerson() {
    //given
    List<String> givenMedications = new ArrayList(List.of("someMed1","someMed2"));
    List<String> givenAllergies = new ArrayList(List.of("someAllergy1","someAllergy2"));

    String givenFirstName = "emile";
    String givenLastName = "denis";
    String givenBirthDate = "01/01/1965";
    JsonMedicalRecord givenJsonMedicalRecord = new JsonMedicalRecord(givenFirstName, givenLastName, givenBirthDate, givenMedications,givenAllergies);
    Person foundPerson = new Person(givenFirstName, givenLastName, "01/01/1965", "123", "mail@mail.com",null);
    Person updatedPerson = new Person(givenFirstName, givenLastName, "", "123", "mail@mail.com",null);

    when(personRepositoryMocked.existsByFirstNameAndLastName(givenFirstName, givenLastName)).thenReturn(false);
    //when
    JsonMedicalRecord result = iPersonService.deleteMedicalRecords(givenJsonMedicalRecord);
    //then
    verify(personRepositoryMocked,Mockito.times(0)).save(foundPerson);
    verify(iMedicalRecordsServiceMocked,Mockito.times(0)).deletePersonsMedicationsFromPerson(updatedPerson);
    verify(iMedicalRecordsServiceMocked,Mockito.times(0)).deletePersonsAllergiesFromPerson(updatedPerson);
    assertThat(result).isNull();
  }
}
