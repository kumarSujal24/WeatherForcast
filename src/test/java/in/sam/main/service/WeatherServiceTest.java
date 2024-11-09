package in.sam.main.service;

import in.sam.main.model.ForecastResponse;
import in.sam.main.model.ForecastResponse.Forecast;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
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
            forecast.setDt_txt("2024-11-08 12:00:00");
            forecastList.add(forecast);
        }
        forecastResponse.setList(forecastList);

        when(restTemplate.getForObject(anyString(), eq(ForecastResponse.class))).thenReturn(forecastResponse);

        ForecastResponse filteredResponse = weatherService.getFilteredForecastData("SampleCity");
        assertEquals(5, filteredResponse.getList().size());
    }

    @Test
    public void testGetFilteredForecastData_NoForecastData() {
        ForecastResponse forecastResponse = new ForecastResponse();
        forecastResponse.setList(new ArrayList<>());

        when(restTemplate.getForObject(anyString(), eq(ForecastResponse.class))).thenReturn(forecastResponse);

        ForecastResponse filteredResponse = weatherService.getFilteredForecastData("SampleCity");
        assertEquals(0, filteredResponse.getList().size());
    }

    @Test
    public void testGetFilteredForecastData_NoMatchingTimeEntries() {
        ForecastResponse forecastResponse = new ForecastResponse();
        List<Forecast> forecastList = new ArrayList<>();

        for (int i = 0; i < 5; i++) {
            Forecast forecast = new Forecast();
            forecast.setDt_txt("2024-11-08 09:00:00");
            forecastList.add(forecast);
        }
        forecastResponse.setList(forecastList);

        when(restTemplate.getForObject(anyString(), eq(ForecastResponse.class))).thenReturn(forecastResponse);

        ForecastResponse filteredResponse = weatherService.getFilteredForecastData("SampleCity");
        assertEquals(0, filteredResponse.getList().size());
    }

    @Test
    public void testGetFilteredForecastData_FewerThanFiveMatches() {
        ForecastResponse forecastResponse = new ForecastResponse();
        List<Forecast> forecastList = new ArrayList<>();

        for (int i = 0; i < 3; i++) {
            Forecast forecast = new Forecast();
            forecast.setDt_txt("2024-11-08 12:00:00");
            forecastList.add(forecast);
        }
        forecastResponse.setList(forecastList);

        when(restTemplate.getForObject(anyString(), eq(ForecastResponse.class))).thenReturn(forecastResponse);

        ForecastResponse filteredResponse = weatherService.getFilteredForecastData("SampleCity");
        assertEquals(3, filteredResponse.getList().size());
    }

    @Test
    public void testGetFilteredForecastData_MoreThanFiveMatches() {
        ForecastResponse forecastResponse = new ForecastResponse();
        List<Forecast> forecastList = new ArrayList<>();

        for (int i = 0; i < 10; i++) {
            Forecast forecast = new Forecast();
            forecast.setDt_txt("2024-11-08 12:00:00");
            forecastList.add(forecast);
        }
        forecastResponse.setList(forecastList);

        when(restTemplate.getForObject(anyString(), eq(ForecastResponse.class))).thenReturn(forecastResponse);

        ForecastResponse filteredResponse = weatherService.getFilteredForecastData("SampleCity");
        assertNotNull(filteredResponse);
        assertEquals(5, filteredResponse.getList().size());

        for (Forecast forecast : filteredResponse.getList()) {
            assertTrue(forecast.getDt_txt().contains("12:00:00"));
        }
    }

    @Test
    public void testGetFilteredForecastData_NullForecastResponse() {
        when(restTemplate.getForObject(anyString(), eq(ForecastResponse.class))).thenReturn(null);

        ForecastResponse filteredResponse = weatherService.getFilteredForecastData("SampleCity");
        assertNull(filteredResponse);
    }
}
