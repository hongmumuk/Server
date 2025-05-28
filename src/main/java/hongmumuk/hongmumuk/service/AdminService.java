package hongmumuk.hongmumuk.service;

import hongmumuk.hongmumuk.common.response.Apiresponse;
import hongmumuk.hongmumuk.common.response.status.ErrorStatus;
import hongmumuk.hongmumuk.common.response.status.SuccessStatus;
import hongmumuk.hongmumuk.dto.AdminDto;
import hongmumuk.hongmumuk.entity.Blog;
import hongmumuk.hongmumuk.entity.LikedRestaurant;
import hongmumuk.hongmumuk.entity.Restaurant;
import hongmumuk.hongmumuk.repository.BlogRepository;
import hongmumuk.hongmumuk.repository.LikedRestaurantRepository;
import hongmumuk.hongmumuk.repository.RestaurantRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class AdminService {

    private final RestaurantRepository restaurantRepository;
    private final LikedRestaurantRepository likedRestaurantRepository;
    private final BlogRepository blogRepository;
    private final S3Service s3Service;

    public ResponseEntity<?> addRestaurant(AdminDto.addNewOneDto addNewOneDto){

        Restaurant restaurant = Restaurant.toEntity(addNewOneDto);

        restaurantRepository.save(restaurant);

        return ResponseEntity.ok(Apiresponse.isSuccess(SuccessStatus.CREATED));
    }

    public ResponseEntity<?> changeResName(AdminDto.modifyResNameDto modifyResNameDto){
        Optional<Restaurant> restaurantOptional = restaurantRepository.findById(modifyResNameDto.getRid());
        if(restaurantOptional.isEmpty()){
            return ResponseEntity.ok(Apiresponse.isFailed(ErrorStatus.RESTAURANT_NOT_FOUND));
        }

        Restaurant restaurant = restaurantOptional.get();

        restaurant.setName(modifyResNameDto.getName());
        restaurantRepository.save(restaurant);

        return ResponseEntity.ok(Apiresponse.isSuccess(SuccessStatus.OK));
    }

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

        List<LikedRestaurant> likedRestaurantList = likedRestaurantRepository.findAllByRestaurant(restaurantOptional.get());

        likedRestaurantRepository.deleteAll(likedRestaurantList);

        List<Blog> blogs = blogRepository.findAllByRestaurant(restaurantOptional);

        blogRepository.deleteAll(blogs);

        restaurantRepository.delete(restaurantOptional.get());

        return ResponseEntity.ok(Apiresponse.isSuccess(SuccessStatus.OK));
    }

    public ResponseEntity<?> addThumbnail(Long rid, MultipartFile multipartFile){
        Optional<Restaurant> restaurantOptional = restaurantRepository.findById(rid);
        if(restaurantOptional.isEmpty()){
            return ResponseEntity.ok(Apiresponse.isFailed(ErrorStatus.RESTAURANT_NOT_FOUND));
        }

        Restaurant restaurant = restaurantOptional.get();

        if(restaurant.getFileName() != null){
            deleteThumbnail(restaurant.getId());
        }

        String uuidFileName = s3Service.getUuidFileName(multipartFile.getOriginalFilename());
        String fileUrl = s3Service.uploadFile(multipartFile);

        restaurant.setFileName(uuidFileName);
        restaurant.setImageUrl(fileUrl);

        return ResponseEntity.ok(Apiresponse.isSuccess(SuccessStatus.OK));
    }

    public ResponseEntity<?> deleteThumbnail(Long rid){
        Optional<Restaurant> restaurantOptional = restaurantRepository.findById(rid);
        if(restaurantOptional.isEmpty()){
            return ResponseEntity.ok(Apiresponse.isFailed(ErrorStatus.RESTAURANT_NOT_FOUND));
        }

        String result = s3Service.deleteFile("", restaurantOptional.get().getFileName());

        if(Objects.equals(result, "success")){
            return ResponseEntity.ok(Apiresponse.isSuccess(SuccessStatus.OK));
        }
        else{
            return ResponseEntity.ok(Apiresponse.isFailed(ErrorStatus.BAD_REQUEST));
        }
    }
}
