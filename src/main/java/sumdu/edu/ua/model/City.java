package sumdu.edu.ua.model;

/**
 * Represents city connected with registered patients.
 */
public class City {

    private final int cityId;
    private final String cityName;

    /**
     * Creates city data object.
     *
     * @param cityId city identifier
     * @param cityName city name
     */
    public City(int cityId, String cityName) {
        this.cityId = cityId;
        this.cityName = cityName;
    }

    public int getCityId() {
        return cityId;
    }

    public String getCityName() {
        return cityName;
    }

    @Override
    public String toString() {
        return cityId + ". " + cityName;
    }
}
