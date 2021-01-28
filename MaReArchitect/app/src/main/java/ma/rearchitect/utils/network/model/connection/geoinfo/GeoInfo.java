package opswat.com.network.model.connection.geoinfo;

import com.google.gson.annotations.SerializedName;

/**
 * Created by LenVo on 7/15/18.
 */

public class GeoInfo {
    @SerializedName("continent")
    private Continent continent;

    @SerializedName("country")
    private Country country;

    @SerializedName("location")
    private Location location;

    public Continent getContinent() {
        return continent;
    }

    public void setContinent(Continent continent) {
        this.continent = continent;
    }

    public Country getCountry() {
        return country;
    }

    public void setCountry(Country country) {
        this.country = country;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public String getFullLocation() {
        if (continent == null && country == null) {
            return null;
        }
        String fullLocation = (continent != null)? continent.getName() : "";
        if (country != null) {
            fullLocation += fullLocation.equals("")? country.getCode(): ", " + country.getCode();
        }
        return fullLocation;
    }
}
