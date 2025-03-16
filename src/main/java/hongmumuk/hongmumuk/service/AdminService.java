package hongmumuk.hongmumuk.service;

import hongmumuk.hongmumuk.common.response.Apiresponse;
import hongmumuk.hongmumuk.common.response.status.ErrorStatus;
import hongmumuk.hongmumuk.common.response.status.SuccessStatus;
import hongmumuk.hongmumuk.dto.AdminDto;
import hongmumuk.hongmumuk.entity.Restaurant;
import hongmumuk.hongmumuk.repository.RestaurantRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class AdminService {

    private final RestaurantRepository restaurantRepository;

    public ResponseEntity<?> crudRestaurant(AdminDto.modifyRestaurantDto modifyRestaurantDto){

        Optional<Restaurant> restaurantOptional = restaurantRepository.findById(modifyRestaurantDto.getRid());
        if(restaurantOptional.isEmpty()){
            return ResponseEntity.ok(Apiresponse.isFailed(ErrorStatus.RESTAURANT_NOT_FOUND));
        }

        Restaurant restaurant = restaurantOptional.get();

        restaurant.setName(modifyRestaurantDto.getName());
        restaurant.setAddress(modifyRestaurantDto.getAddress());
        restaurant.setFront(modifyRestaurantDto.getFront());
        restaurant.setBack(modifyRestaurantDto.getBack());
        restaurant.setLongitude(modifyRestaurantDto.getLongitude());
        restaurant.setLatitude(modifyRestaurantDto.getLatitude());
        restaurant.setNaverLink(modifyRestaurantDto.getNaverLink());
        restaurant.setKakaoLink(modifyRestaurantDto.getKakaoLink());
        restaurantRepository.save(restaurant);

        return ResponseEntity.ok(Apiresponse.isSuccess(SuccessStatus.OK));
    }

    public ResponseEntity<?> deleteRestaurant(AdminDto.deleteRestaurantDto deleteRestaurantDto){

        Optional<Restaurant> restaurantOptional = restaurantRepository.findById(deleteRestaurantDto.getRid());
        if(restaurantOptional.isEmpty()){
            return ResponseEntity.ok(Apiresponse.isFailed(ErrorStatus.RESTAURANT_NOT_FOUND));
        }

        restaurantRepository.delete(restaurantOptional.get());

        return ResponseEntity.ok(Apiresponse.isSuccess(SuccessStatus.OK));
    }

}
