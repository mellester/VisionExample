package numberrecognition.com.databasemodule.api;

import numberrecognition.com.databasemodule.dao.CarNumberRepository;
import numberrecognition.com.databasemodule.object.CarNumber;
import numberrecognition.com.databasemodule.exceptions.NotFound;
import numberrecognition.com.databasemodule.exceptions.BadRequest;
import numberrecognition.com.databasemodule.object.IncomeNumber;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@Validated
@RequestMapping("/api/number")
public class NumberController {
    private final CarNumberRepository repository;
    public NumberController(CarNumberRepository repository) {
        this.repository = repository;
    }
    @PostMapping("/{number}")
    public NumberRecordResponse GetRecord(@PathVariable("number") String number) throws NotFound, BadRequest {
        if (!IncomeNumber.validNumber(number)) throw new BadRequest();
        CarNumber numberCar = repository.findRecordByNumberIgnoreCase(number).orElseThrow(NotFound::new);
        return new NumberRecordResponse(numberCar);
    }

    record NumberRecordResponse(String mark, String color, int owners) {
        NumberRecordResponse(CarNumber number) {
            this(number.getMark(),number.getColor(),number.getOwners());};

    }

}
