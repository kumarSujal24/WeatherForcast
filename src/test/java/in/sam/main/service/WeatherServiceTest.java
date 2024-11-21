package in.sam.main.service;

import in.sam.main.model.ForecastResponse;
import in.sam.main.model.ForecastResponse.Forecast;
import in.sam.main.model.ForecastResponse.Main;
import in.sam.main.model.ForecastResponse.Weather;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class WeatherServiceTest {

    @InjectMocks
    private WeatherService weatherService;

    @Mock
    private RestTemplate restTemplate;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testGetFilteredForecastData_LimitedResults() {
        ForecastResponse forecastResponse = new ForecastResponse();
        List<Forecast> forecastList = new ArrayList<>();

        for (int i = 0; i < 10; i++) {
            Forecast forecast = new Forecast();
            Main main = new Main();
            main.setTemp_min(10 + i);
            main.setTemp_max(20 + i);
            forecast.setMain(main);

            Weather weather = new Weather();
            weather.setDescription("Clear");
            forecast.setWeather(Collections.singletonList(weather));

            // Set different dates for grouping
            forecast.setDt_txt("2024-11-0" + (i % 5 + 1) + " 12:00:00");
            forecastList.add(forecast);
        }
        forecastResponse.setList(forecastList);

        when(restTemplate.getForObject(anyString(), eq(ForecastResponse.class))).thenReturn(forecastResponse);

        ForecastResponse filteredResponse = weatherService.getFilteredForecastData("SampleCity");
        assertNotNull(filteredResponse);
        assertNotNull(filteredResponse.getList()); // Ensure that the list is not null
        assertEquals(5, filteredResponse.getList().size()); // Check that 5 entries are returned

        for (Forecast forecast : filteredResponse.getList()) {
            assertTrue(forecast.getDt_txt().contains("12:00:00"));
        }
    }


    @Test
    public void testGetFilteredForecastData_NoForecastData() {
        ForecastResponse forecastResponse = new ForecastResponse();
        forecastResponse.setList(new ArrayList<>()); // Ensure the list is initialized

        when(restTemplate.getForObject(anyString(), eq(ForecastResponse.class))).thenReturn(forecastResponse);

        ForecastResponse filteredResponse = weatherService.getFilteredForecastData("SampleCity");
        assertNotNull(filteredResponse);
        assertNotNull(filteredResponse.getList()); // Ensure that the list is not null
        assertEquals(0, filteredResponse.getList().size());
    }

    @Test
    public void testGetFilteredForecastData_NoMatchingTimeEntries() {
        ForecastResponse forecastResponse = new ForecastResponse();
        List<Forecast> forecastList = new ArrayList<>();

        for (int i = 0; i < 5; i++) {
            Forecast forecast = new Forecast();
            forecast.setDt_txt("2024-11-08 09:00:00"); // Non-matching time entries
            forecastList.add(forecast);
        }
        forecastResponse.setList(forecastList);

        when(restTemplate.getForObject(anyString(), eq(ForecastResponse.class))).thenReturn(forecastResponse);

        ForecastResponse filteredResponse = weatherService.getFilteredForecastData("SampleCity");

        assertNotNull(filteredResponse); // Ensure that filteredResponse is not null
        assertNotNull(filteredResponse.getList()); // Ensure that the list is not null
        assertTrue(filteredResponse.getList().isEmpty()); // Ensure that the list is empty
    }

    @Test
    public void testGetFilteredForecastData_FewerThanFiveMatches() {
        ForecastResponse forecastResponse = new ForecastResponse();
        List<Forecast> forecastList = new ArrayList<>();

        for (int i = 0; i < 3; i++) {
            Forecast forecast = new Forecast();
            Main main = new Main();
            main.setTemp_min(10 + i);
            main.setTemp_max(20 + i);
            forecast.setMain(main);

            Weather weather = new Weather();
            weather.setDescription("Clear");
            forecast.setWeather(Collections.singletonList(weather));

            // Set different dates for grouping
            forecast.setDt_txt("2024-11-0" + (i + 1) + " 12:00:00");
            forecastList.add(forecast);
        }
        forecastResponse.setList(forecastList);

        when(restTemplate.getForObject(anyString(), eq(ForecastResponse.class))).thenReturn(forecastResponse);

        ForecastResponse filteredResponse = weatherService.getFilteredForecastData("SampleCity");
        assertNotNull(filteredResponse);
        assertNotNull(filteredResponse.getList()); // Ensure that the list is not null
        assertEquals(3, filteredResponse.getList().size()); // Check that 3 entries are returned

        for (Forecast forecast : filteredResponse.getList()) {
            assertTrue(forecast.getDt_txt().contains("12:00:00"));
        }
    }

    @Test
    public void testGetFilteredForecastData_MoreThanFiveMatches() {
        ForecastResponse forecastResponse = new ForecastResponse();
        List<Forecast> forecastList = new ArrayList<>();

        for (int i = 0; i < 10; i++) {
            Forecast forecast = new Forecast();
            Main main = new Main();
            main.setTemp_min(10 + i);
            main.setTemp_max(20 + i);
            forecast.setMain(main);

            Weather weather = new Weather();
            weather.setDescription("Clear");
            forecast.setWeather(Collections.singletonList(weather));

            // Set different dates for grouping
            forecast.setDt_txt("2024-11-0" + (i % 5 + 1) + " 12:00:00");
            forecastList.add(forecast);
        }
        forecastResponse.setList(forecastList);

        when(restTemplate.getForObject(anyString(), eq(ForecastResponse.class))).thenReturn(forecastResponse);

        ForecastResponse filteredResponse = weatherService.getFilteredForecastData("SampleCity");
        assertNotNull(filteredResponse);
        assertNotNull(filteredResponse.getList()); // Ensure that the list is not null
        assertEquals(5, filteredResponse.getList().size()); // Check that 5 entries are returned

        for (Forecast forecast : filteredResponse.getList()) {
            assertTrue(forecast.getDt_txt().contains("12:00:00"));
        }
    }

    @Test
    public void testGetFilteredForecastData_NullForecastResponse() {
        when(restTemplate.getForObject(anyString(), eq(ForecastResponse.class))).thenReturn(null);

        ForecastResponse filteredResponse = weatherService.getFilteredForecastData("SampleCity");
        assertNotNull(filteredResponse); // Ensure the method handles null response and returns a non-null object
        assertNotNull(filteredResponse.getList()); // Ensure that the list is not null
        assertTrue(filteredResponse.getList().isEmpty()); // Ensure the list is empty
    }
}
