package in.sam.main.controller;


import in.sam.main.model.WeatherResponse;
import in.sam.main.service.WeatherService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;


@Controller
public class WeatherController {

    private final WeatherService weatherService;

    @Autowired
    public WeatherController(WeatherService weatherService) {
        this.weatherService = weatherService;
    }

    @GetMapping("/weather")
    public String getWeather(@RequestParam("city") String city, Model model) {
        try {
            WeatherResponse response = weatherService.getWeatherData(city);

            if (response == null) {
                model.addAttribute("error", "City not found.");
            } else {
                model.addAttribute("city", response.getName());
                model.addAttribute("country", response.getSys().getCountry());
                model.addAttribute("weatherDescription", response.getWeather().get(0).getDescription());
                model.addAttribute("temperature", response.getMain().getTemp());
                model.addAttribute("humidity", response.getMain().getHumidity());
                model.addAttribute("windspeed", response.getWind().getSpeed());
                model.addAttribute("weatherIcon", "wi wi-owm-" + response.getWeather().get(0).getId());
            }
        } catch (Exception e) {
            model.addAttribute("error", "Unable to fetch weather data. Please try again later.");
        }
        return "weather";
    }
}