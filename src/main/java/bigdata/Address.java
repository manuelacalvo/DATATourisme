package bigdata;

public class Address {
	public String locality;
    public String postalCode;
    public String street;

    @Override
    public String toString() {
        return this.locality + "," + this.postalCode + "," + this.street;
    }

    public Address(String locality, String postalCode, String street) {
    	this.locality = locality;
    	this.postalCode = postalCode;
    	this.street = street;
    }
}
