package in.sam.main.controller;

import in.sam.main.model.WeatherResponse;
import in.sam.main.service.WeatherService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.ui.Model;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

public class WeatherControllerTest {

    @InjectMocks
    private WeatherController weatherController;

    @Mock
    private WeatherService weatherService; // Mock WeatherService instead of RestTemplate

    @Mock
    private Model model;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testGetWeather_Success() {
        // Arrange: Create a sample WeatherResponse object with dummy data
        WeatherResponse weatherResponse = new WeatherResponse();
        weatherResponse.setName("SampleCity");
        
        WeatherResponse.Sys sys = new WeatherResponse.Sys();
        sys.setCountry("SampleCountry");
        weatherResponse.setSys(sys);

        WeatherResponse.Weather weather = new WeatherResponse.Weather();
        weather.setId(800);
        weather.setDescription("Clear sky");
        weatherResponse.setWeather(Arrays.asList(weather));

        WeatherResponse.Main main = new WeatherResponse.Main();
        main.setTemp(25.0);
        main.setHumidity(50);
        weatherResponse.setMain(main);

        WeatherResponse.Wind wind = new WeatherResponse.Wind();
        wind.setSpeed(5.0);
        weatherResponse.setWind(wind);

        // Mock the WeatherService response to return the weatherResponse
        when(weatherService.getWeatherData("SampleCity")).thenReturn(weatherResponse);

        // Act: Call the getWeather method
        String viewName = weatherController.getWeather("SampleCity", model);

        // Assert: Verify that the model was populated correctly and view is "weather"
        verify(model).addAttribute("city", "SampleCity");
        verify(model).addAttribute("country", "SampleCountry");
        verify(model).addAttribute("weatherDescription", "Clear sky");
        verify(model).addAttribute("temperature", 25.0);
        verify(model).addAttribute("humidity", 50);
        verify(model).addAttribute("windspeed", 5.0);
        verify(model).addAttribute("weatherIcon", "wi wi-owm-800");

        assertEquals("weather", viewName);
    }

    @Test
    public void testGetWeather_CityNotFound() {
        // Arrange: Mock a null response to simulate city not found
        when(weatherService.getWeatherData("UnknownCity")).thenReturn(null);

        // Act: Call the getWeather method
        String viewName = weatherController.getWeather("UnknownCity", model);

        // Assert: Check that an error attribute is added and the view is "weather"
        verify(model).addAttribute("error", "City not found.");
        assertEquals("weather", viewName);
    }

    @Test
    public void testGetWeather_ApiFailure() {
        // Arrange: Throw an exception when the API call is made
        when(weatherService.getWeatherData("SampleCity")).thenThrow(new RuntimeException("API error"));

        // Act: Call the getWeather method
        String viewName = weatherController.getWeather("SampleCity", model);

        // Assert: Check that an error attribute is added and the view is "weather"
        verify(model).addAttribute("error", "Unable to fetch weather data. Please try again later.");
        assertEquals("weather", viewName);
    }
}
