package in.sam.main.service;

import in.sam.main.model.WeatherResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class WeatherService {
	
	private final RestTemplate restTemplate;

    @Value("${api.key}") // API key from application.properties or application.yml
    private String apiKey;

    public WeatherService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public WeatherResponse getWeatherData(String city) {
        String url = "https://api.openweathermap.org/data/2.5/weather?q=" + city + "&appId=" + apiKey + "&units=metric";
        
        // Call the API and return the result
        return restTemplate.getForObject(url, WeatherResponse.class);
    }

}
