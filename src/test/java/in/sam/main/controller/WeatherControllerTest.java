package in.sam.main.controller;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.ui.Model;
import java.util.Arrays;
import in.sam.main.service.WeatherService;
import in.sam.main.model.WeatherResponse;
import in.sam.main.model.ForecastResponse;

public class WeatherControllerTest {

    @Mock
    private WeatherService weatherService;

    @Mock
    private Model model;

    @InjectMocks
    private WeatherController weatherController;

    @BeforeEach
    public void setup() {
        // Initialize mocks and sample data before each test.
        weatherService = mock(WeatherService.class);
        model = mock(Model.class);
        weatherController = new WeatherController(weatherService);
    }

    @Test
    public void testGetWeatherSuccess() {
        // 1. Mock weather data
        WeatherResponse weatherResponse = new WeatherResponse();
        weatherResponse.setName("SampleCity");
        WeatherResponse.Sys sys = new WeatherResponse.Sys();
        sys.setCountry("SampleCountry");
        weatherResponse.setSys(sys);

        WeatherResponse.Weather weather = new WeatherResponse.Weather();
        weather.setId(500);  // for icon rendering
        weather.setDescription("light rain");
        weatherResponse.setWeather(Arrays.asList(weather));

        WeatherResponse.Main main = new WeatherResponse.Main();
        main.setTemp(20.5);
        main.setHumidity(65);
        weatherResponse.setMain(main);

        WeatherResponse.Wind wind = new WeatherResponse.Wind();
        wind.setSpeed(5.5);
        weatherResponse.setWind(wind);

        when(weatherService.getWeatherData("SampleCity")).thenReturn(weatherResponse);

        // 2. Mock forecast data
        ForecastResponse forecastResponse = new ForecastResponse();
        ForecastResponse.Forecast forecast = new ForecastResponse.Forecast();
        ForecastResponse.Main forecastMain = new ForecastResponse.Main();
        forecastMain.setTemp(22.0);

        ForecastResponse.Weather forecastWeather = new ForecastResponse.Weather();
        forecastWeather.setDescription("clear sky");
        forecast.setMain(forecastMain);
        forecast.setWeather(Arrays.asList(forecastWeather));
        forecast.setDt_txt("2024-11-10 12:00:00");

        forecastResponse.setList(Arrays.asList(forecast, forecast, forecast, forecast, forecast));

        when(weatherService.getFilteredForecastData("SampleCity")).thenReturn(forecastResponse);

        // 3. Call the controller method
        String viewName = weatherController.getWeather("SampleCity", model);

        // 4. Verify model attributes (simplified example)
        verify(model).addAttribute("city", "SampleCity");
        verify(model).addAttribute("country", "SampleCountry");
        verify(model).addAttribute("temperature", 20.5);
        verify(model).addAttribute("humidity", 65);
        verify(model).addAttribute("windspeed", 5.5);
        verify(model).addAttribute("weatherIcon", "wi wi-owm-500");
        verify(model).addAttribute("forecastList", forecastResponse.getList().subList(0, 5));

        // 5. Check view name
        assertEquals("weather", viewName);
    }
    
   
    @Test
    public void testGetWeather_CityNotFound() {
        when(weatherService.getWeatherData("NonExistentCity")).thenReturn(null);
        when(weatherService.getFilteredForecastData("NonExistentCity")).thenReturn(null);

        String viewName = weatherController.getWeather("NonExistentCity", model);

        verify(model).addAttribute("error", "City not found.");
        assertEquals("weather", viewName);
    }

    @Test
    public void testGetWeather_NullForecastResponse() {
        WeatherResponse weatherResponse = new WeatherResponse();
        weatherResponse.setName("SampleCity");

        when(weatherService.getWeatherData("SampleCity")).thenReturn(weatherResponse);
        when(weatherService.getFilteredForecastData("SampleCity")).thenReturn(null);

        String viewName = weatherController.getWeather("SampleCity", model);

        // Verify that only the error message is set in the model
        verify(model).addAttribute("error", "City not found.");
        assertEquals("weather", viewName);
    }


    @Test
    public void testGetWeather_ExceptionHandling() {
        // Configure the service to throw an exception when called
        when(weatherService.getWeatherData("SampleCity")).thenThrow(new RuntimeException("API error"));

        String viewName = weatherController.getWeather("SampleCity", model);

        // Verify that the error attribute is set
        verify(model).addAttribute("error", "Unable to fetch weather data. Please try again later.");
        assertEquals("weather", viewName);
    }

}
