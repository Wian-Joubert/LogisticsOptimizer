package Model;

public class Place {
    private final String street;
    private final String town;
    private final String city;
    private final String postcode;

    public Place (String street, String town, String city, String postcode){
        this.street = street;
        this.town = town;
        this.city = city;
        this.postcode = postcode;
    }

    public String getStreet(){
        return street;
    }
    public String getTown(){
        return town;
    }
    public String getCity(){
        return city;
    }
    public String getPostcode(){
        return postcode;
    }

    @Override
    public String toString() {
        return "Place{" +
                "street='" + street + '\'' +
                ", town='" + town + '\'' +
                ", city='" + city + '\'' +
                ", postcode='" + postcode + '\'' +
                '}';
    }
}
