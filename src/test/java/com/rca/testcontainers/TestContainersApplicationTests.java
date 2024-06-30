package com.rca.testcontainers;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.hasSize;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;

import java.util.List;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class TestContainersApplicationTests {
    @LocalServerPort
    private Integer port;

    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:latest");

    @BeforeAll
    static void beforeAll(){
        postgres.start();
    }

    @AfterAll
    static void afterAll(){
        postgres.stop();
    }

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry){
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }

    @Autowired
    BookService bookService;
    @Autowired
    BookRepository bookRepository;

    @BeforeEach
    void setup(){
        RestAssured.baseURI = "http://localhost" + port;
        bookRepository.deleteAll();
    }

    @Test
    void shouldGetAlBooks(){
        List<Book> books = List.of(
                new Book(null, "Java Microservices", 8900.0, "Yves"),
                new Book(null, "Java TestContainers", 9000.0, "Aristide")
        );
        bookRepository.saveAll(books);

        given()
                .contentType(ContentType.JSON)
                .when()
                .get("/api/books")
                .then()
                .statusCode(200)
                .body(".", hasSize(2));
    }

}

