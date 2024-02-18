package com.github.filonovsv.smartix_test_tusk;

import com.github.filonovsv.smartix_test_tusk.handlers.AbonentHandler;
import com.github.filonovsv.smartix_test_tusk.handlers.PaymentHandler;
import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import net.minidev.json.JSONArray;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class SmartixTestTuskApplicationTests {
    @Autowired
    TestRestTemplate restTemplate;

    @Test
    @DirtiesContext
    void shouldCreateAbonent() {
        AbonentHandler abonentHandler = new AbonentHandler("123", "123");
        ResponseEntity<Void> postResponse = restTemplate.postForEntity("/create", abonentHandler, Void.class);
        assertThat(postResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        ResponseEntity<Void> getResponse = restTemplate
                .withBasicAuth("123", "123")
                .getForEntity("/me", Void.class);
        assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    @DirtiesContext
    void shouldUpdateAbonent() {
        AbonentHandler abonentHandlerCreate = new AbonentHandler("123", "123");
        restTemplate.postForEntity("/create", abonentHandlerCreate, Void.class);
        AbonentHandler abonentHandlerUpdate = new AbonentHandler(null, null, "Sergey", null, null, null, null, null);
        ResponseEntity<Void> postResponse = restTemplate
                .withBasicAuth("123", "123")
                .postForEntity("/update", abonentHandlerUpdate, Void.class);
        assertThat(postResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        ResponseEntity<String> getResponse = restTemplate
                .withBasicAuth("123", "123")
                .getForEntity("/me", String.class);
        DocumentContext documentContext = JsonPath.parse(getResponse.getBody());
        String name = documentContext.read("$.name");
        assertThat(name).isNotNull();
        assertThat(name).isEqualTo("Sergey");
    }

    @Test
    @DirtiesContext
    void shouldGetBalance() {
        AbonentHandler abonentHandler = new AbonentHandler("123", "123");
        restTemplate.postForEntity("/create", abonentHandler, Void.class);
        ResponseEntity<String> getResponse = restTemplate
                .withBasicAuth("123", "123")
                .getForEntity("/balance", String.class);
        assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        DocumentContext documentContext = JsonPath.parse(getResponse.getBody());
        Integer rubles = documentContext.read("$.rubles");
        assertThat(rubles).isNotNull();
        assertThat(rubles).isEqualTo(1000);
        Integer copecks = documentContext.read("$.copecks");
        assertThat(copecks).isNotNull();
        assertThat(copecks).isEqualTo(0);
    }

    @Test
    @DirtiesContext
    void shouldPay() {
        AbonentHandler abonentHandlerPayer = new AbonentHandler("123", "123");
        restTemplate.postForEntity("/create", abonentHandlerPayer, Void.class);
        AbonentHandler abonentHandlerReceiver = new AbonentHandler("321", "321");
        restTemplate.postForEntity("/create", abonentHandlerReceiver, Void.class);
        PaymentHandler paymentHandler = new PaymentHandler("321", 10L, 10L);
        ResponseEntity<String> postResponse = restTemplate
                .withBasicAuth("123", "123")
                .postForEntity("/pay", paymentHandler, String.class);
        assertThat(postResponse.getStatusCode()).isEqualTo(HttpStatus.OK);

        ResponseEntity<String> getResponsePayer = restTemplate
                .withBasicAuth("123", "123")
                .getForEntity("/balance", String.class);
        assertThat(getResponsePayer.getStatusCode()).isEqualTo(HttpStatus.OK);
        DocumentContext documentContextPayer = JsonPath.parse(getResponsePayer.getBody());
        Integer rublesPayer = documentContextPayer.read("$.rubles");
        assertThat(rublesPayer).isNotNull();
        assertThat(rublesPayer).isEqualTo(989);
        Integer copecksPayer = documentContextPayer.read("$.copecks");
        assertThat(copecksPayer).isNotNull();
        assertThat(copecksPayer).isEqualTo(90);

        ResponseEntity<String> getResponseReceiver = restTemplate
                .withBasicAuth("321", "321")
                .getForEntity("/balance", String.class);
        assertThat(getResponseReceiver.getStatusCode()).isEqualTo(HttpStatus.OK);
        DocumentContext documentContextReceiver = JsonPath.parse(getResponseReceiver.getBody());
        Integer rublesReceiver = documentContextReceiver.read("$.rubles");
        assertThat(rublesReceiver).isNotNull();
        assertThat(rublesReceiver).isEqualTo(1010);
        Integer copecksReceiver = documentContextReceiver.read("$.copecks");
        assertThat(copecksReceiver).isNotNull();
        assertThat(copecksReceiver).isEqualTo(10);
    }

    @Test
    @DirtiesContext
    void shouldGetPaymentHistory() {
        AbonentHandler abonentHandlerPayer = new AbonentHandler("123", "123");
        restTemplate.postForEntity("/create", abonentHandlerPayer, Void.class);
        AbonentHandler abonentHandlerReceiver = new AbonentHandler("321", "321");
        restTemplate.postForEntity("/create", abonentHandlerReceiver, Void.class);
        PaymentHandler paymentHandler = new PaymentHandler("321", 10L, 10L);

        restTemplate
                .withBasicAuth("123", "123")
                .postForEntity("/pay", paymentHandler, String.class);
        restTemplate
                .withBasicAuth("123", "123")
                .postForEntity("/pay", paymentHandler, String.class);
        restTemplate
                .withBasicAuth("123", "123")
                .postForEntity("/pay", paymentHandler, String.class);

        ResponseEntity<String> response1 = restTemplate
                .withBasicAuth("123", "123")
                .getForEntity("/history?page=0&size=2", String.class);
        assertThat(response1.getStatusCode()).isEqualTo(HttpStatus.OK);

        DocumentContext documentContext1 = JsonPath.parse(response1.getBody());
        JSONArray page1 = documentContext1.read("$[*]");
        assertThat(page1.size()).isEqualTo(2);


        ResponseEntity<String> response2 = restTemplate
                .withBasicAuth("123", "123")
                .getForEntity("/history?page=1&size=2", String.class);
        assertThat(response2.getStatusCode()).isEqualTo(HttpStatus.OK);

        DocumentContext documentContext2 = JsonPath.parse(response2.getBody());
        JSONArray page2 = documentContext2.read("$[*]");
        assertThat(page2.size()).isEqualTo(1);
    }
}
