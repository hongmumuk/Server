package hongmumuk.hongmumuk.service;

import hongmumuk.hongmumuk.common.response.Apiresponse;
import hongmumuk.hongmumuk.common.response.status.SuccessStatus;
import hongmumuk.hongmumuk.dto.RandomDto;
import hongmumuk.hongmumuk.entity.Restaurant;
import hongmumuk.hongmumuk.repository.RestaurantRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RandomService {

    private final RestaurantRepository restaurantRepository;

    @Transactional
    public ResponseEntity<?> random() {
        List<Restaurant> restaurants = restaurantRepository.findAll();
        List<RandomDto> randomDto = restaurants.stream()
                .map(RandomDto::from)
                .collect(Collectors.toList());

        return ResponseEntity.ok(Apiresponse.isSuccess(SuccessStatus.OK, randomDto));
    }
}
