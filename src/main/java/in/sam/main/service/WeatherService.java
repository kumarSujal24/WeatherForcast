package in.sam.main.service;

import in.sam.main.model.WeatherResponse;
import in.sam.main.model.ForecastResponse;
import in.sam.main.model.ForecastResponse.Forecast;
import in.sam.main.model.ForecastResponse.Main;
import in.sam.main.model.ForecastResponse.Weather;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;
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
                ForecastResponse emptyResponse = new ForecastResponse();
                emptyResponse.setList(new ArrayList<>()); // Ensure the list is initialized
                return emptyResponse;
            }

            // Group by day and calculate min/max temperatures and overall description
            Map<String, List<Forecast>> groupedByDay = forecastResponse.getList().stream()
                    .collect(Collectors.groupingBy(f -> f.getDt_txt().substring(0, 10)));

            List<Forecast> aggregatedForecasts = new ArrayList<>();

            for (Map.Entry<String, List<Forecast>> entry : groupedByDay.entrySet()) {
                String date = entry.getKey();
                List<Forecast> dailyForecasts = entry.getValue();

                double minTemp = dailyForecasts.stream().mapToDouble(f -> f.getMain().getTemp_min()).min().orElse(Double.NaN);
                double maxTemp = dailyForecasts.stream().mapToDouble(f -> f.getMain().getTemp_max()).max().orElse(Double.NaN);
                String description = dailyForecasts.stream()
                        .collect(Collectors.groupingBy(f -> f.getWeather().get(0).getDescription(), Collectors.counting()))
                        .entrySet().stream()
                        .max(Map.Entry.comparingByValue())
                        .map(Map.Entry::getKey)
                        .orElse("No description available");

                Forecast aggregatedForecast = new Forecast();
                Main main = new Main();
                main.setTemp_min(minTemp);
                main.setTemp_max(maxTemp);
                aggregatedForecast.setMain(main);
                Weather weather = new Weather();
                weather.setDescription(description);
                aggregatedForecast.setWeather(Collections.singletonList(weather));
                aggregatedForecast.setDt_txt(date + " 12:00:00"); // Use a fixed time for aggregated data

                aggregatedForecasts.add(aggregatedForecast);
            }

            // Limit results to 5 entries
            List<Forecast> limitedResults = aggregatedForecasts.stream()
                    .sorted(Comparator.comparing(Forecast::getDt_txt))
                    .limit(5)
                    .collect(Collectors.toList());

            forecastResponse.setList(limitedResults);
            return forecastResponse;

        } catch (Exception e) {
            System.err.println("Failed to fetch forecast data: " + e.getMessage());
            ForecastResponse errorResponse = new ForecastResponse();
            errorResponse.setList(new ArrayList<>()); // Ensure the list is initialized
            return errorResponse;
        }
    }

}
