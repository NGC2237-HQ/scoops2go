# Software Testing & Quality Assurance CW2 - Scoops2Go

This repository contains the **Scoops2Go API** (Spring Boot) and my complete test suite for the CW2 assignment.

## My details

Name: **Fu Nanjun**
Student ID: **21906415**

## Prerequisites

- Java 21
- Maven (or use the included Maven wrapper `./mvnw`)
- Internet connection (for some API tests)

## How to Run the Tests

### Run All Unit/Integration Tests
1. Open the `api` folder in IntelliJ IDEA
2. Right-click on `src/test/java` → Run 'All Tests'
3. Or run via Maven: `./mvnw test`

### Run API Tests (Manual)
1. Start the Spring Boot application: run `Scoops2GoApiApplication.java`
2. Use Postman to test the endpoints

## Summary of work

### Test Case Design Specification
- **Location:** `Scoops2GoTestCases.xlsx`
- **Description:** 41 test cases covering 7 modules: Product Browsing (3), Treat Creation (7), Basket & Checkout (7), Order Tracking (3), Promotions (11), UI & Usability (5), API Testing (5). Each test case includes ID, description, pre-conditions, steps, test data, expected results, actual results, and status.

### Implemented Tests

#### Unit Tests
- **Location:** `src/test/java/Scoops2Go/scoops2goapi/`
- **Test Classes:**
  - `controller/OrderControllerTest.java` - Controller layer tests using Mockito
  - `infrastructure/PaymentGatewayTest.java` - StubPaymentGateway unit tests
  - `service/OrderServiceTest.java` - Service layer tests with @Nested, @ParameterizedTest, and Mockito

#### Integration Tests (Extra)
- **Location:** `src/test/java/Scoops2Go/scoops2goapi/integration/OrderServiceIntegrationTest.java`
- **Description:** 3 additional integration tests verifying database persistence, retrieval, and deletion. These tests are **not part of the original test plan** but were added to demonstrate integration testing techniques taught in Week 2.

#### API Tests (Manual)
- **Tool:** Postman
- **Tested Endpoints:** `GET /api/product`, `GET /api/product/{id}`, `POST /api/order`, `GET /api/order/{id}`, `PUT /api/order`, `DELETE /api/order/{id}`
- **Test Cases:** TC_API_001 to TC_API_005 in `Scoops2GoTestCases.xlsx`

### Current Test Status

- **Total Tests:** 41 (28 automated, 13 manual)
- **Passed:** 33 | **Failed:** 8
- The 8 failures are due to identified defects (see Defect Report for details).

### Defect Report
- **Location:** `Scoops2GoTestSummaryDefectReport.docx`
- **Summary:** 41 test cases executed (33 passed, 8 failed). 8 defects identified: 5 business logic defects, 3 UI/Usability defects. Additionally, 13 dependency vulnerabilities were discovered via SAST/SCA (see Security Testing Findings in the defect report).

### Continuous Integration
- **Location:** `.github/workflows/maven-test.yml`
- **Status:** Runs all tests (unit + integration) on push to `main` and pull requests (JDK 21, Maven). The pipeline currently fails due to identified defects (DEF-001 to DEF-008).

### Non-Functional Testing
- **Tool:** Lighthouse (via Edge DevTools)
- **Performance Score:** 35/100
- **Accessibility Score:** 80/100
- **Key Findings:** Slow load times (FCP: 11.8s, LCP: 22.0s); accessibility issues with ARIA, color contrast, and missing lang attribute. See defect report for details.