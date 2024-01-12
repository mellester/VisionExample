package numberrecognition.com.databasemodule.dao;

import numberrecognition.com.databasemodule.object.CarNumber;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface CarNumberRepository extends CrudRepository<CarNumber, String>{
    Optional<CarNumber> findRecordByNumberIgnoreCase(String number);
    boolean existsByNumber(String number);
    List<Number> findByOrderByNumber();
    boolean existsBy();

}