package in.sam.main.service;

import in.sam.main.model.WeatherResponse;
import in.sam.main.model.ForecastResponse.Forecast;
import in.sam.main.model.ForecastResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class WeatherService {

    private final RestTemplate restTemplate;

    @Value("${api.key}")
    private String apiKey;

    public WeatherService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public WeatherResponse getWeatherData(String city) {
        String url = "https://api.openweathermap.org/data/2.5/weather?q=" + city + "&appId=" + apiKey + "&units=metric";
        return restTemplate.getForObject(url, WeatherResponse.class);
    }

    public ForecastResponse getForecastData(String city) {
        String url = "https://api.openweathermap.org/data/2.5/forecast?q=" + city + "&appId=" + apiKey + "&units=metric";
        return restTemplate.getForObject(url, ForecastResponse.class);
    }

    public ForecastResponse getFilteredForecastData(String city) {
        try {
            String url = "https://api.openweathermap.org/data/2.5/forecast?q=" + city + "&appId=" + apiKey + "&units=metric";
            ForecastResponse forecastResponse = restTemplate.getForObject(url, ForecastResponse.class);
            
            if (forecastResponse == null || forecastResponse.getList() == null) {
                System.out.println("No forecast data available.");
                return null;
            }

            List<Forecast> filteredList = forecastResponse.getList().stream()
                    .filter(forecast -> forecast.getDt_txt().contains("12:00:00"))
                    .limit(5)
                    .toList();

            forecastResponse.setList(filteredList);
            return forecastResponse;

        } catch (Exception e) {
            System.err.println("Failed to fetch forecast data: " + e.getMessage());
            return null;
        }
    }


}
