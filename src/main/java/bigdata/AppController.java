package bigdata;

import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class AppController {

    @RequestMapping(value = "/poi", method = RequestMethod.GET)
    @ResponseBody
    POI getPOI() {
        final POI poi = new POI();

        poi.id = "1";
        poi.name = "Test1";
        return poi;
    }

}
