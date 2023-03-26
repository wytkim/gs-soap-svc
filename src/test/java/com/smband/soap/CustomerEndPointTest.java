package com.smband.soap;

import static org.mockito.Mockito.when;
import static org.springframework.ws.test.server.RequestCreators.withPayload;
import static org.springframework.ws.test.server.ResponseMatchers.noFault;
import static org.springframework.ws.test.server.ResponseMatchers.payload;
import static org.springframework.ws.test.server.ResponseMatchers.validPayload;
import static org.springframework.ws.test.server.ResponseMatchers.xpath;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.webservices.server.WebServiceServerTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.core.io.ClassPathResource;
import org.springframework.ws.test.server.MockWebServiceClient;
import org.springframework.xml.transform.StringSource;

import com.smband.soap.model.Country;
import com.smband.soap.model.Currency;
import com.smband.soap.repo.CountryRepository;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@WebServiceServerTest
public class CustomerEndPointTest {
	private static final Map<String, String> NAMESPACE_MAPPING = createMapping();
	
	@Autowired
	private MockWebServiceClient client;
	@MockBean
	private CountryRepository countryRepository;
	
	@Test
	void givenXmlRequest_whenServiceInvoked_thenValidResponse() throws IOException {
		Country country = createCountry();
		when(countryRepository.findCountry("Spain")).thenReturn(country);
		log.info("데이터 확인!");
		StringSource request = new StringSource(
				"<gs:getCountryRequest xmlns:gs=\"http://www.smband.com/countries\">"
						+ "<gs:name>Spain</gs:name>"
			+ "</gs:getCountryRequest>"
		);
		
		StringSource expectedResponse = new StringSource(
				"<ns2:getCountryResponse xmlns:ns2=\"http://www.smband.com/countries\">"
						+ "<ns2:country>"
							+ "<ns2:name>Spain</ns2:name>"
							+ "<ns2:population>46704314</ns2:population>"
							+ "<ns2:capital>Madrid</ns2:capital>"
							+ "<ns2:currency>EUR</ns2:currency>"
						+ "</ns2:country>"
				+ "</ns2:getCountryResponse>"
				);
		
		client.sendRequest(withPayload(request))
			.andExpect(noFault())
			.andExpect(validPayload(new ClassPathResource("/countries.xsd")))
			.andExpect(payload(expectedResponse))
			.andExpect(xpath("/ns2:getCountryResponse/ns2:country/ns2:name", NAMESPACE_MAPPING).evaluatesTo("Spain"));
		log.info("test 완료.");
	}

	private Country createCountry() {
		Country spain = new Country();
		spain.setName("Spain");
		spain.setCapital("Madrid");
		spain.setCurrency(Currency.EUR);
		spain.setPopulation(46704314);
		
		return spain;
	}
	
	private static Map<String, String> createMapping() {
        Map<String, String> mapping = new HashMap<>();
        mapping.put("ns2", "http://www.smband.com/countries");
        return mapping;
    }
}
