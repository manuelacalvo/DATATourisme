package bigdata;

import java.util.ArrayList;
import java.util.List;

public class POI {
    public String id;
    public String name;
    public List<String> type = new ArrayList<>();
    public String comment;
    public String lastUpdate;
    public String reduceMobilityAccess;
    public Address address;
    public String latitude;
    public String longitude;


    public POI(String id, String name, List<String> type, String comment, String lastUpdate, String reduceMobilityAccess, Address address, String latitude, String longitude) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.comment = comment;
        this.lastUpdate = lastUpdate;
        this.reduceMobilityAccess = reduceMobilityAccess;
        this.address = address;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    @Override
    public String toString() {
        return this.id + "," + this.name + "," + toStringArray(this.type) + "," + this.comment + "," + this.lastUpdate + "," + this.reduceMobilityAccess
                + "," + this.address.toString() + "," + this.latitude + "," + this.longitude + "\n";
    }

    public String toStringArray(List<String> list) {
        if (list != null && list.size() > 0) {
            final String[] str = {""};

            list.forEach(current -> str[0] += current + "|");
            return str[0];
        } else return null;
    }
}


